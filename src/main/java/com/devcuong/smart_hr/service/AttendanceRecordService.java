package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.Entity.*;
import com.devcuong.smart_hr.config.MultitenancyProperties;
import com.devcuong.smart_hr.config.TenantContext;
import com.devcuong.smart_hr.dto.AttendanceRecordDTO;
import com.devcuong.smart_hr.dto.MonthlyAttendanceSummaryDTO;
import com.devcuong.smart_hr.dto.SettingSystemDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.enums.AttendanceStatus;
import com.devcuong.smart_hr.exception.AppException;
import com.devcuong.smart_hr.exception.ErrorCode;
import com.devcuong.smart_hr.repository.AttendanceRecordRepository;
import com.devcuong.smart_hr.repository.EmployeeRepository;
import com.devcuong.smart_hr.repository.HolidayRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AttendanceRecordService extends SearchService<AttendanceRecord> {

    @Autowired
    AttendanceRecordRepository repository;

    @Autowired
    SettingSystemService settingSystemService;

    @Autowired
    WorkScheduleService workScheduleService;

    @Autowired
    ContractService contractService;
    
    @Autowired
    MultitenancyProperties multitenancyProperties;
    
    @Autowired
    HolidayRepository holidayRepository;
    
    @Autowired
    LeaveRequestService leaveRequestService;

    @Autowired
    EmployeeRepository employeeRepository;

    public AttendanceRecordService(AttendanceRecordRepository repository) {
        super(repository);
    }

    public Page<Map<String, Object>> getAllAttendanceRecords(PageFilterInput<AttendanceRecord> input) {
        try {
            Page<AttendanceRecord> recordPage = super.findAll(input);
            List<Map<String, Object>> records = new ArrayList<>(recordPage.getContent()).stream().map(this::toMap).toList();
            return new PageImpl<>(records, recordPage.getPageable(), recordPage.getTotalElements());
        } catch (Exception e) {
            log.error("Error retrieving attendance records", e);
            throw new AppException(ErrorCode.UNCATEGORIZED, "Failed to retrieve attendance records: " + e.getMessage());
        }
    }

    private Map<String, Object> toMap(AttendanceRecord attendanceRecord) {
        Employee employee = employeeRepository.findByEmployeeCode(attendanceRecord.getEmployeeCode());
        String employeeName = employee != null ? employee.getLastName() + " " + employee.getFirstName() : "Unknown Employee";

        // Use HashMap instead of Map.of() to handle null values
        Map<String, Object> result = new HashMap<>();
        result.put("id", attendanceRecord.getId());
        result.put("employee_code", attendanceRecord.getEmployeeCode());
        result.put("check_in_time", attendanceRecord.getCheckInTime());
        result.put("check_out_time", attendanceRecord.getCheckOutTime());
        result.put("work_date", attendanceRecord.getWorkDate());
        result.put("status", attendanceRecord.getStatus());
        result.put("total_hours", attendanceRecord.getTotalHours());
        result.put("overtime_hours", attendanceRecord.getOvertimeHours());
        result.put("employee_name", employeeName);

        return result;
    }

    public List<AttendanceRecord> getListAttendanceRecord() {
        return repository.findAll();
    }

    public AttendanceRecord getAttendanceRecordById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "Attendance record not found"));
    }
    

    public AttendanceRecord createAttendanceRecord(AttendanceRecordDTO recordDTO) {
        if(recordDTO.getEmployeeCode() == null || recordDTO.getEmployeeCode().isEmpty()){
            throw new AppException(ErrorCode.INPUT_INVALID, "Mã nhân viên không được để trống!");
        }
        if(recordDTO.getWorkDate() == null){
            throw new AppException(ErrorCode.INPUT_INVALID, "Ngày làm việc không được để trống!");
        }
        if(recordDTO.getCheckInTime() == null){
            throw new AppException(ErrorCode.INPUT_INVALID, "Thời gian vào không được để trống!");
        }
        if(recordDTO.getCheckOutTime() == null){
            throw new AppException(ErrorCode.INPUT_INVALID, "Thời gian ra không được để trống!");
        }
        AttendanceRecord record = new AttendanceRecord();
        // Check if employee already checked
        AttendanceRecord existingRecord = repository.findByEmployeeCodeAndWorkDate(recordDTO.getEmployeeCode(), recordDTO.getWorkDate());
        if (existingRecord != null) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Không thể tạo mới chấm công, nhân viên đã chấm công ngày này!");
        }
//        checkValidTimeCheckInAndCheckOut(recordDTO.getCheckInTime(), recordDTO.getCheckOutTime());
        updateRecordFromDTO(record, recordDTO);
        // Get active contract for employee
        Contract activeContract = contractService.findActiveContractByEmployeeCode(recordDTO.getEmployeeCode());
        if(activeContract == null) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Không tìm thấy hợp đồng làm việc cho nhân viên này!");
        }
        if(activeContract.getStartDate().isAfter(recordDTO.getWorkDate()) ||
           (activeContract.getEndDate() != null && activeContract.getEndDate().isBefore(recordDTO.getWorkDate()))) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Ngày làm việc không nằm trong khoảng hợp đồng làm việc của nhân viên này!");
        }
        // Get work schedule from contract
        WorkSchedule workSchedule = workScheduleService.getWorkScheduleById(activeContract.getWorkScheduleId());
        if(workSchedule == null) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Không tìm thấy lịch làm việc cho hợp đồng này!");
        }
        SettingSystemDTO settingSystemDTO = settingSystemService.getSettingSystem();
        Double totals = calculateTotalWorkHours(recordDTO.getCheckInTime(), recordDTO.getCheckOutTime(),
                workSchedule.getBreakStart(), workSchedule.getBreakEnd());
        updateTotalWorkHoursAndOverTimeHours(record, totals, workSchedule.getTotalWorkHours());
        record.setStatus(updateStatus(record, workSchedule,settingSystemDTO));
        return repository.save(record);
    }

    public AttendanceRecord updateAttendanceRecord(Long id, AttendanceRecordDTO recordDTO) {
        AttendanceRecord record = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "Attendance record not found"));
//        checkValidTimeCheckInAndCheckOut(recordDTO.getCheckInTime(), recordDTO.getCheckOutTime());
        // Update record from DTO
        updateRecordFromDTO(record, recordDTO);

        // Get active contract for employee
        Contract activeContract = contractService.findActiveContractByEmployeeCode(record.getEmployeeCode());
        if(activeContract == null) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Không tìm thấy hợp đồng làm việc cho nhân viên này!");
        }
        if(activeContract.getStartDate().isAfter(recordDTO.getWorkDate()) ||
                (activeContract.getEndDate() != null && activeContract.getEndDate().isBefore(recordDTO.getWorkDate()))) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Ngày làm việc không nằm trong khoảng hợp đồng làm việc của nhân viên này!");
        }

        // Get work schedule from contract
        WorkSchedule workSchedule = workScheduleService.getWorkScheduleById(activeContract.getWorkScheduleId());

        SettingSystemDTO settingSystem = settingSystemService.getSettingSystem();

        if(record.getCheckInTime() != null && record.getCheckOutTime() != null) {
            Double totalHours =
                    calculateTotalWorkHours(record.getCheckInTime(), record.getCheckOutTime(),
                            workSchedule.getBreakStart(), workSchedule.getBreakEnd());
            updateTotalWorkHoursAndOverTimeHours(record, totalHours, workSchedule.getTotalWorkHours());
        } else {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Không thể cập nhật thời gian chấm công khi chưa có giờ vào hoặc giờ ra!");
        }

        record.setStatus(updateStatus(record, workSchedule, settingSystem));
        return repository.save(record);
    }

    public AttendanceRecord updateAttendanceRecord(Long id, AttendanceRecord entity) {
        AttendanceRecord record = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "Attendance record not found"));
//        checkValidTimeCheckInAndCheckOut(entity.getCheckInTime(), entity.getCheckOutTime());
        // Update record from entity
        updateRecordFromEntity(record, entity);

        // Get active contract for employee
        Contract activeContract = contractService.findActiveContractByEmployeeCode(record.getEmployeeCode());
        if(activeContract == null) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Không tìm thấy hợp đồng làm việc cho nhân viên này!");
        }
        if(activeContract.getStartDate().isAfter(entity.getWorkDate()) ||
                (activeContract.getEndDate() != null && activeContract.getEndDate().isBefore(entity.getWorkDate()))) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Ngày làm việc không nằm trong khoảng hợp đồng làm việc của nhân viên này!");
        }

        // Get work schedule from contract
        WorkSchedule workSchedule = workScheduleService.getWorkScheduleById(activeContract.getWorkScheduleId());

        SettingSystemDTO settingSystem = settingSystemService.getSettingSystem();

        if(record.getCheckInTime() != null && record.getCheckOutTime() != null) {
            Double totalHours =
                calculateTotalWorkHours(record.getCheckInTime(), record.getCheckOutTime(),
                        workSchedule.getBreakStart(), workSchedule.getBreakEnd());
            updateTotalWorkHoursAndOverTimeHours(record, totalHours, workSchedule.getTotalWorkHours());
        } else {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Không thể cập nhật thời gian chấm công khi chưa có giờ vào hoặc giờ ra!");
        }

        record.setStatus(updateStatus(record, workSchedule, settingSystem));

        return repository.save(record);
    }

    public void deleteAttendanceRecord(Long id) {
        AttendanceRecord record = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "Attendance record not found"));
        repository.delete(record);
    }

    public AttendanceRecord checkIn(String employeeCode) {
        // Check if employee already checked in today
        LocalDate today = LocalDate.now();
        AttendanceRecord existingRecord = repository.findByEmployeeCodeAndWorkDate(employeeCode, today);
        if (existingRecord != null) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Bạn đã chấm công ngày hôm nay!");
        }

        // Get active contract for employee
        Contract activeContract = contractService.findActiveContractByEmployeeCode(employeeCode);
               
        // Get work schedule from contract
        WorkSchedule workSchedule = workScheduleService.getWorkScheduleById(activeContract.getWorkScheduleId());
        
        // Get setting system for late threshold
        SettingSystemDTO settingSystem = settingSystemService.getSettingSystem();
        LocalTime lateThresholdTime = null;
        if(settingSystem.getLateThresholdMinutes() != null) {
            // Calculate late threshold time (start time + allowed late minutes)
            lateThresholdTime = workSchedule.getStartTime()
                    .plusMinutes(settingSystem.getLateThresholdMinutes());
        }

        LocalTime absentThresholdTime = null;
        if(settingSystem.getAbsentThresholdMinutes() != null) {
            absentThresholdTime = workSchedule.getStartTime().plusMinutes(settingSystem.getAbsentThresholdMinutes());
        }else {
            absentThresholdTime = workSchedule.getEndTime();
        }

        // Create attendance record
        AttendanceRecord record = new AttendanceRecord();
        record.setEmployeeCode(employeeCode);
        record.setCheckInTime(LocalTime.from(LocalDateTime.now()));
        record.setWorkDate(LocalDate.now());

        // Determine status based on check-in time
        LocalTime checkInTime = record.getCheckInTime();
        if (absentThresholdTime != null && checkInTime.isAfter(absentThresholdTime)) {
            record.setStatus(AttendanceStatus.VANG);
        }
        else if (lateThresholdTime != null && checkInTime.isAfter(lateThresholdTime)) {
            record.setStatus(AttendanceStatus.MUON);
        }
        else {
            record.setStatus(AttendanceStatus.BINHTHUONG);
        }

        // Save and return record
        return repository.save(record);
    }
    
    public AttendanceRecord checkOut(Long id, String employeeCode) {
        // Get active contract for employee
        Contract activeContract = contractService.findActiveContractByEmployeeCode(employeeCode);
               
        // Get work schedule from contract
        WorkSchedule workSchedule = workScheduleService.getWorkScheduleById(activeContract.getWorkScheduleId());

        // Get setting system business hours
        SettingSystemDTO settingSystem = settingSystemService.getSettingSystem();
        LocalTime earlyLeaveThresholdTime = null;
        if(settingSystem.getEarlyLeaveThresholdMinutes() != null) {
            // Calculate early leave threshold time (end time + allowed early leave minutes)
             earlyLeaveThresholdTime = workSchedule.getEndTime()
                    .minusMinutes(settingSystem.getEarlyLeaveThresholdMinutes());
        }
        int overtimeThreshold = settingSystem.getOvertimeThresholdMinutes() != null ? settingSystem.getOvertimeThresholdMinutes() : 0;

        // Update attendance record
        AttendanceRecord record = repository.findById(id).orElseThrow(null);
        if(record == null) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Attendance record not found");
        }
        if(record.getCheckInTime() == null) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Bạn chưa chấm công!");
        }
        record.setEmployeeCode(employeeCode);
        record.setCheckOutTime(LocalTime.from(LocalDateTime.now()));
        record.setWorkDate(LocalDate.now());

        // Calculate total hours worked
        if (record.getCheckInTime() != null && record.getCheckOutTime() != null) {
            Double totalHours =
                calculateTotalWorkHours(record.getCheckInTime(), record.getCheckOutTime(),
                        workSchedule.getBreakStart(), workSchedule.getBreakEnd());
            if(isWeekendOrHoliday(record.getWorkDate())) {
                record.setTotalHours(0.0);
                record.setOvertimeHours(totalHours);
            }else{
                updateTotalWorkHoursAndOverTimeHours(record, totalHours, workSchedule.getTotalWorkHours());
            }
        }

        LocalTime overtimeThresholdTime = workSchedule.getEndTime()
                .plusMinutes(overtimeThreshold);




        // Determine status based on check-out time
        LocalTime checkOutTime = record.getCheckOutTime();
        if(record.getStatus() == AttendanceStatus.BINHTHUONG) {
            if (earlyLeaveThresholdTime != null && checkOutTime.isBefore(earlyLeaveThresholdTime)) {
                record.setStatus(AttendanceStatus.VESOM);
            } else if (checkOutTime.isAfter(overtimeThresholdTime)){
                record.setStatus(AttendanceStatus.THEMGIO);
            }
        }


        // Save and return record
        return repository.save(record);
    }

    private AttendanceStatus updateStatus(AttendanceRecord record, WorkSchedule workSchedule, SettingSystemDTO settingSystem) {
        LocalTime lateThresholdTime = null;
        LocalTime absentThresholdTime = null;
        LocalTime earlyLeaveThresholdTime = null;
        log.info("earlyLeaveThresholdMinutes: {}, lateThresholdMinutes: {}, absentThresholdMinutes: {}",
                settingSystem.getEarlyLeaveThresholdMinutes(), settingSystem.getLateThresholdMinutes(), settingSystem.getAbsentThresholdMinutes());
        if(settingSystem.getLateThresholdMinutes() != null) {
            lateThresholdTime = workSchedule.getStartTime().plusMinutes(settingSystem.getLateThresholdMinutes());
        }
        if(settingSystem.getAbsentThresholdMinutes() != null) {
            absentThresholdTime = workSchedule.getStartTime().plusMinutes(settingSystem.getAbsentThresholdMinutes());
        }else {
            absentThresholdTime = workSchedule.getEndTime();
        }
        if(settingSystem.getEarlyLeaveThresholdMinutes() != null) {
            earlyLeaveThresholdTime = workSchedule.getStartTime().plusMinutes(settingSystem.getEarlyLeaveThresholdMinutes());
        }


        int overtimeMinute = settingSystem.getOvertimeThresholdMinutes() != null ? settingSystem.getOvertimeThresholdMinutes() : 0;
        LocalTime overtimeThresholdTime = workSchedule.getEndTime().plusMinutes(overtimeMinute);

        log.info("lateThresholdTime: {}, absentThresholdTime: {}, earlyLeaveThresholdTime: {}, overtimeThresholdTime: {}",
                lateThresholdTime, absentThresholdTime, earlyLeaveThresholdTime, overtimeThresholdTime);


        if (record.getCheckInTime() == null) {
            return AttendanceStatus.VANG; // Absent
        } else if (absentThresholdTime != null && record.getCheckInTime().isAfter(absentThresholdTime)) {
            return AttendanceStatus.VANG; // Absent
        } else if (lateThresholdTime != null && record.getCheckInTime().isAfter(lateThresholdTime)) {
            return AttendanceStatus.MUON; // Late
        } else if (earlyLeaveThresholdTime != null && record.getCheckOutTime() != null && record.getCheckOutTime().isBefore(earlyLeaveThresholdTime)) {
            return AttendanceStatus.VESOM; // Early leave
        } else if ( record.getCheckOutTime() != null && record.getCheckOutTime().isAfter(overtimeThresholdTime)) {
            return AttendanceStatus.THEMGIO;
        } else {
            return AttendanceStatus.BINHTHUONG; // Normal
        }
    }

    private void updateRecordFromDTO(AttendanceRecord record, AttendanceRecordDTO dto) {
        record.setEmployeeCode(dto.getEmployeeCode());
        record.setCheckInTime(dto.getCheckInTime());
        record.setCheckOutTime(dto.getCheckOutTime());
        record.setWorkDate(dto.getWorkDate());
        record.setStatus(dto.getStatus());
        record.setTotalHours(dto.getTotalHours());
        record.setOvertimeHours(dto.getOvertimeHours());
    }

    private void updateRecordFromEntity(AttendanceRecord record, AttendanceRecord dto) {
        record.setEmployeeCode(dto.getEmployeeCode());
        record.setCheckInTime(dto.getCheckInTime());
        record.setCheckOutTime(dto.getCheckOutTime());
        record.setWorkDate(dto.getWorkDate());
        record.setStatus(dto.getStatus());
        record.setTotalHours(dto.getTotalHours());
        record.setOvertimeHours(dto.getOvertimeHours());
    }
    
    /**
     * Generate monthly attendance statistics for the current month
     * @param employeeCode The employee code to get statistics for
     * @return MonthlyAttendanceSummaryDTO containing the attendance statistics
     */
    public MonthlyAttendanceSummaryDTO getCurrentMonthAttendanceSummary(String employeeCode) {
        // Get current year and month
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();
        
        // Calculate first and last day of current month
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();
        
        // Get all attendance records for the employee in the current month
        List<AttendanceRecord> monthlyRecords = repository.findByEmployeeCodeAndWorkDateBetween(
                employeeCode, firstDayOfMonth, lastDayOfMonth);
        
        // Initialize counters
        int totalWorkDays = 0;
        double totalWorkHours = 0;
        int lateDaysCount = 0;
        int earlyLeaveDaysCount = 0;
        int absentDaysCount = 0;
        double totalOvertimeHours = 0;
        
        // Calculate statistics
        for (AttendanceRecord record : monthlyRecords) {
            // Only count records with check-in and check-out times as work days
            if (record.getCheckInTime() != null && record.getCheckOutTime() != null) {
                totalWorkDays++;
                
                // Add total hours if available
                if (record.getTotalHours() != null) {
                    totalWorkHours += record.getTotalHours();
                }
                
                // Add overtime hours if available
                if (record.getOvertimeHours() != null) {
                    totalOvertimeHours += record.getOvertimeHours();
                }
            }
            
            // Count by status
            if (record.getStatus() != null) {
                switch (record.getStatus()) {
                    case MUON:
                        lateDaysCount++;
                        break;
                    case VESOM:
                        earlyLeaveDaysCount++;
                        break;
                    case VANG:
                        absentDaysCount++;
                        break;
                    default:
                        break;
                }
            }
        }
        
        // Create and return summary DTO
        MonthlyAttendanceSummaryDTO summary = new MonthlyAttendanceSummaryDTO();
        summary.setEmployeeCode(employeeCode);
        summary.setYear(year);
        summary.setMonth(month);
        summary.setTotalWorkDays(totalWorkDays);
        summary.setTotalWorkHours(totalWorkHours);
        summary.setLateDaysCount(lateDaysCount);
        summary.setEarlyLeaveDaysCount(earlyLeaveDaysCount);
        summary.setAbsentDaysCount(absentDaysCount);
        summary.setTotalOvertimeHours(totalOvertimeHours);
        
        return summary;
    }


    @Scheduled(cron = "0 0 23 * * ?")  // Run at 11:00 PM every day
    public void checkDailyAbsences() {
        log.info("Running daily absence check");
        // Process for each tenant
        for (String tenant : multitenancyProperties.getTenants()) {
            try {
                // Set tenant context
                TenantContext.setCurrentTenant(tenant);
                
                LocalDate today = LocalDate.now();
                
                // Skip weekends or holidays if needed
                if (isWeekendOrHoliday(today)) {
                    continue;
                }
                
                // Get all active employees
                List<String> activeEmployeeCodes = employeeRepository.findByIsActiveTrue().stream().map(Employee::getEmployeeCode).toList();
                
                for (String employeeCode : activeEmployeeCodes) {
                    // Check if employee has an attendance record for today
                    AttendanceRecord record = repository
                            .findByEmployeeCodeAndWorkDate(employeeCode, today);
                            
                    // If no record exists, check if employee has an approved leave request for today
                    if (record == null) {
                        // Check if employee has an approved leave request for today
                        boolean hasApprovedLeave = hasApprovedLeaveRequest(employeeCode, today);
                        
                        // Only create absence record if there's no approved leave request
                        if (!hasApprovedLeave) {
                            createAbsenceRecord(employeeCode, today);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error processing daily absences for tenant {}: {}", tenant, e.getMessage(), e);
            } finally {
                // Clear tenant context
                TenantContext.clear();
            }
        }
    }

    /**
     * Check if the employee has an approved leave request for the specified date
     * 
     * @param employeeCode The employee code
     * @param date The date to check
     * @return true if the employee has an approved leave request for the date
     */
    private boolean hasApprovedLeaveRequest(String employeeCode, LocalDate date) {
        try {
            // Get all approved leave requests that cover the specified date
            return leaveRequestService.hasApprovedLeaveRequestForDate(employeeCode, date);
        } catch (Exception e) {
            log.error("Error checking approved leave requests for employee {}: {}", 
                    employeeCode, e.getMessage(), e);
            return false;
        }
    }

    private boolean isWeekendOrHoliday(LocalDate date) {
        // Implement weekend/holiday check logic based on your business rules
        DayOfWeek day = date.getDayOfWeek();
        SettingSystemDTO settingSystem = settingSystemService.getSettingSystem();
        List<DayOfWeek> workDays = new ArrayList<>();
        for (int i = settingSystem.getWeekStartDate(); i <= settingSystem.getWeekEndDate(); i++) {
            if(i==2){
                workDays.add(DayOfWeek.MONDAY);
            } else if(i==3){
                workDays.add(DayOfWeek.TUESDAY);
            } else if(i==4){
                workDays.add(DayOfWeek.WEDNESDAY);
            } else if(i==5){
                workDays.add(DayOfWeek.THURSDAY);
            } else if(i==6){
                workDays.add(DayOfWeek.FRIDAY);
            } else if(i==7){
                workDays.add(DayOfWeek.SATURDAY);
            } else if(i==8){
                workDays.add(DayOfWeek.SUNDAY);
            }
        }
        
        // Check if the day is a holiday
        List<LocalDate> holidays = holidayRepository.findAllHolidaysForYear(date.getYear());
        
        return !workDays.contains(day) || holidays.contains(date);
    }

    private void createAbsenceRecord(String employeeCode, LocalDate date) {
        try {
            // Get work schedule for employee
            Contract activeContract = contractService.findActiveContractByEmployeeCode(employeeCode);
            WorkSchedule workSchedule = workScheduleService.getWorkScheduleById(activeContract.getWorkScheduleId());

            // Create absence record
            AttendanceRecord record = new AttendanceRecord();
            record.setEmployeeCode(employeeCode);
            record.setWorkDate(date);
            record.setStatus(AttendanceStatus.VANG);

            // Set check times to null or to schedule times if required by your system
            record.setCheckInTime(null);
            record.setCheckOutTime(null);
            record.setTotalHours(0.0);
            record.setOvertimeHours(0.0);

            // Save the record
            repository.save(record);
        } catch (Exception e) {
            log.error("Error creating absence record for employee {}: {}",
                    employeeCode, e.getMessage(), e);
        }
    }

    private Double calculateTotalWorkHours(LocalTime checkInTime, LocalTime checkOutTime, LocalTime breakTimeStart, LocalTime breakTimeEnd) {
        if (checkInTime == null || checkOutTime == null) {
            return 0.0;
        }

        if(checkInTime.isAfter(breakTimeStart) && checkOutTime.isBefore(breakTimeEnd)) {
            checkInTime = breakTimeEnd;
        }

//        Double workTime = (checkOutTime.getHour() - checkInTime.getHour()) + (checkOutTime.getMinute() - checkInTime.getMinute())  / 60.0;
//        Double breakTime = (breakTimeEnd.getHour() - breakTimeStart.getHour()) + (breakTimeEnd.getMinute() - breakTimeStart.getMinute()) / 60.0;
//        log.info("Work time: {}, Break time: {}", workTime, breakTime);
            int workHours = checkOutTime.getHour() - checkInTime.getHour();
            int workMinutes = checkOutTime.getMinute() - checkInTime.getMinute();
            int breakHours = breakTimeEnd.getHour() - breakTimeStart.getHour();
            int breakMinutes = breakTimeEnd.getMinute() - breakTimeStart.getMinute();
            if(workHours < 0 || (workHours == 0 && workMinutes < 0)) {
                workHours += 24;
            }
            double workTime = workHours + workMinutes / 60.0;
            if(breakHours < 0 || (breakHours == 0 && breakMinutes < 0)) {
                breakHours += 24;
            }
            double breakTime = breakHours + breakMinutes / 60.0;

        if(workTime - breakTime <= 0) {
            return workTime;
        }
        return workTime - breakTime;
    }

    private void updateTotalWorkHoursAndOverTimeHours(AttendanceRecord record, Double totalHours, Double workHoursInShift) {
        if(totalHours > workHoursInShift) {
            record.setOvertimeHours(totalHours - workHoursInShift);
            record.setTotalHours(workHoursInShift);
        } else {
            record.setOvertimeHours(0.0);
            record.setTotalHours(totalHours);
        }
    }

    private void checkValidTimeCheckInAndCheckOut(LocalTime checkInTime, LocalTime checkOutTime) {
        if (checkInTime.isAfter(checkOutTime)) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Thời gian vào không thể sau thời gian ra!");
        }
    }
}

