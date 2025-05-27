package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.Entity.AttendanceRecord;
import com.devcuong.smart_hr.Entity.Contract;
import com.devcuong.smart_hr.Entity.SettingSystem;
import com.devcuong.smart_hr.Entity.WorkSchedule;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

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

    public AttendanceRecordService(AttendanceRecordRepository repository) {
        super(repository);
    }

    public Page<AttendanceRecord> getAllAttendanceRecords(PageFilterInput<AttendanceRecord> input) {
        try {
            Page<AttendanceRecord> recordPage = super.findAll(input);
            List<AttendanceRecord> records = new ArrayList<>(recordPage.getContent());
            return new PageImpl<>(records, recordPage.getPageable(), recordPage.getTotalElements());
        } catch (Exception e) {
            log.error("Error retrieving attendance records", e);
            throw new AppException(ErrorCode.UNCATEGORIZED, "Failed to retrieve attendance records: " + e.getMessage());
        }
    }

    public List<AttendanceRecord> getListAttendanceRecord() {
        return repository.findAll();
    }

    public AttendanceRecord getAttendanceRecordById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "Attendance record not found"));
    }
    

    public AttendanceRecord createAttendanceRecord(AttendanceRecordDTO recordDTO) {
        AttendanceRecord record = new AttendanceRecord();
        // Check if employee already checked
        AttendanceRecord existingRecord = repository.findByEmployeeCodeAndWorkDate(recordDTO.getEmployeeCode(), recordDTO.getWorkDate());
        if (existingRecord != null) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Không thể tạo mới chấm công, nhân viên đã chấm công ngày này!");
        }
        updateRecordFromDTO(record, recordDTO);
        return repository.save(record);
    }

    public AttendanceRecord updateAttendanceRecord(Long id, AttendanceRecordDTO recordDTO) {
        AttendanceRecord record = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "Attendance record not found"));

        // Update record from DTO
        updateRecordFromDTO(record, recordDTO);

        // Get active contract for employee
        Contract activeContract = contractService.findActiveContractByEmployeeCode(record.getEmployeeCode());

        // Get work schedule from contract
        WorkSchedule workSchedule = workScheduleService.getWorkScheduleById(activeContract.getWorkScheduleId());

        SettingSystemDTO settingSystem = settingSystemService.getSettingSystem();

        if(record.getCheckInTime() != null && record.getCheckOutTime() != null) {
            Double totalHours =
                    calculateTotalWorkHours(record.getCheckInTime(), record.getCheckOutTime(),
                            workSchedule.getBreakStart(), workSchedule.getBreakEnd());
            updateTotalWorkHoursAndOverTimeHours(record, totalHours, workSchedule.getTotalWorkHours());
        }else {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Không thể cập nhật thời gian chấm công khi chưa có giờ vào hoặc giờ ra!");
        }

        record.setStatus(updateStatus(record, workSchedule, settingSystem));
        return repository.save(record);
    }

    public AttendanceRecord updateAttendanceRecord(Long id, AttendanceRecord entity) {
        AttendanceRecord record = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "Attendance record not found"));
        // Update record from entity
        updateRecordFromEntity(record, entity);

        // Get active contract for employee
        Contract activeContract = contractService.findActiveContractByEmployeeCode(record.getEmployeeCode());

        // Get work schedule from contract
        WorkSchedule workSchedule = workScheduleService.getWorkScheduleById(activeContract.getWorkScheduleId());

        SettingSystemDTO settingSystem = settingSystemService.getSettingSystem();

        if(record.getCheckInTime() != null && record.getCheckOutTime() != null) {
            Double totalHours =
                calculateTotalWorkHours(record.getCheckInTime(), record.getCheckOutTime(),
                        workSchedule.getBreakStart(), workSchedule.getBreakEnd());
            updateTotalWorkHoursAndOverTimeHours(record, totalHours, workSchedule.getTotalWorkHours());
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
        int lateThreshold = settingSystem.getLateThresholdMinutes() != null ? settingSystem.getLateThresholdMinutes() : 0;
        int absentThreshold = settingSystem.getAbsentThresholdMinutes() != null ? settingSystem.getAbsentThresholdMinutes() : 0;
        int leaveEarlyThreshold = settingSystem.getEarlyLeaveThresholdMinutes() != null ? settingSystem.getEarlyLeaveThresholdMinutes() : 0;
        int overtimeThreshold = settingSystem.getOvertimeThresholdMinutes() != null ? settingSystem.getOvertimeThresholdMinutes() : 0;

        // Create attendance record
        AttendanceRecord record = new AttendanceRecord();
        record.setEmployeeCode(employeeCode);
        record.setCheckInTime(LocalTime.from(LocalDateTime.now()));
        record.setWorkDate(LocalDate.now());

        // Calculate late threshold time (start time + allowed late minutes)
        LocalTime lateThresholdTime = workSchedule.getStartTime()
            .plusMinutes(lateThreshold);

        LocalTime absentThresholdTime = workSchedule.getStartTime()
                .plusMinutes(absentThreshold);

        // Determine status based on check-in time
        LocalTime checkInTime = record.getCheckInTime();
        if (checkInTime.isAfter(absentThresholdTime)) {
            record.setStatus(AttendanceStatus.VANG);
        }
        else if (checkInTime.isAfter(lateThresholdTime)) {
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
        int lateThreshold = settingSystem.getLateThresholdMinutes() != null ? settingSystem.getLateThresholdMinutes() : 0;
        int absentThreshold = settingSystem.getAbsentThresholdMinutes() != null ? settingSystem.getAbsentThresholdMinutes() : 0;
        int leaveEarlyThreshold = settingSystem.getEarlyLeaveThresholdMinutes() != null ? settingSystem.getEarlyLeaveThresholdMinutes() : 0;
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
            updateTotalWorkHoursAndOverTimeHours(record, totalHours, workSchedule.getTotalWorkHours());
        }

        LocalTime overtimeThresholdTime = workSchedule.getEndTime()
                .plusMinutes(overtimeThreshold);


        // Calculate early leave threshold time (end time + allowed early leave minutes)
        LocalTime earlyLeaveThresholdTime = workSchedule.getEndTime()
                .minusMinutes(leaveEarlyThreshold);

        // Determine status based on check-out time
        LocalTime checkOutTime = record.getCheckOutTime();
        if(record.getStatus() == AttendanceStatus.BINHTHUONG) {
            if (checkOutTime.isBefore(earlyLeaveThresholdTime)) {
                record.setStatus(AttendanceStatus.VESOM);
            } else if (checkOutTime.isAfter(overtimeThresholdTime)){
                record.setStatus(AttendanceStatus.THEMGIO);
            }
        }


        // Save and return record
        return repository.save(record);
    }

    private AttendanceStatus updateStatus(AttendanceRecord record, WorkSchedule workSchedule, SettingSystemDTO settingSystem) {
        int lateThreshold = settingSystem.getLateThresholdMinutes() != null ? settingSystem.getLateThresholdMinutes() : 0;
        int absentThreshold = settingSystem.getAbsentThresholdMinutes() != null ? settingSystem.getAbsentThresholdMinutes() : 0;
        int leaveEarlyThreshold = settingSystem.getEarlyLeaveThresholdMinutes() != null ? settingSystem.getEarlyLeaveThresholdMinutes() : 0;

        LocalTime lateThresholdTime = workSchedule.getStartTime().plusMinutes(lateThreshold);
        LocalTime absentThresholdTime = workSchedule.getStartTime().plusMinutes(absentThreshold);
        LocalTime earlyLeaveThresholdTime = workSchedule.getEndTime().minusMinutes(leaveEarlyThreshold);

        if (record.getCheckInTime() == null) {
            return AttendanceStatus.VANG; // Absent
        } else if (record.getCheckInTime().isAfter(absentThresholdTime)) {
            return AttendanceStatus.VANG; // Absent
        } else if (record.getCheckInTime().isAfter(lateThresholdTime)) {
            return AttendanceStatus.MUON; // Late
        } else if (record.getCheckOutTime() != null && record.getCheckOutTime().isBefore(earlyLeaveThresholdTime)) {
            return AttendanceStatus.VESOM; // Early leave
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
                log.info("Processing daily absences for tenant: {}", tenant);
                // Set tenant context
                TenantContext.setCurrentTenant(tenant);
                
                LocalDate today = LocalDate.now();
                
                // Skip weekends or holidays if needed
                if (isWeekendOrHoliday(today)) {
                    log.info("Today is weekend or holiday for tenant {}, skipping absence check", tenant);
                    continue;
                }
                
                // Get all active employees
                List<String> activeEmployeeCodes = contractService.getAllActiveEmployeeCodes();
                log.info("Found {} active employees for tenant {}", activeEmployeeCodes.size(), tenant);
                
                for (String employeeCode : activeEmployeeCodes) {
                    // Check if employee has an attendance record for today
                    AttendanceRecord record = repository
                            .findByEmployeeCodeAndWorkDate(employeeCode, today);
                            
                    // If no record exists, create an absence record
                    if (record == null) {
                        createAbsenceRecord(employeeCode, today);
                        log.info("Created absence record for employee: {} in tenant {}", employeeCode, tenant);
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
        return !workDays.contains(day);
        // Additional holiday check logic can be added here
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

        Double workTime = checkOutTime.getHour() - checkInTime.getHour() + (checkOutTime.getMinute() - checkInTime.getMinute()) / 60.0;
        Double breakTime = breakTimeEnd.getHour() - breakTimeStart.getHour() + (breakTimeEnd.getMinute() - breakTimeStart.getMinute()) / 60.0;
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
}

