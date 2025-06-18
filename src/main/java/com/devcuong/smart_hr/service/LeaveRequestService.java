package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.Entity.Employee;
import com.devcuong.smart_hr.Entity.LeaveRequest;
import com.devcuong.smart_hr.Entity.LeaveType;
import com.devcuong.smart_hr.dto.LeaveBalanceDTO;
import com.devcuong.smart_hr.dto.LeaveRequestDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.enums.ApprovalStatus;
import com.devcuong.smart_hr.exception.AppException;
import com.devcuong.smart_hr.exception.ErrorCode;
import com.devcuong.smart_hr.repository.EmployeeRepository;
import com.devcuong.smart_hr.repository.LeaveRequestRepository;
import com.devcuong.smart_hr.repository.LeaveTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class LeaveRequestService extends SearchService<LeaveRequest> {

    @Autowired
    LeaveRequestRepository repository;
    
    @Autowired
    LeaveTypeRepository leaveTypeRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    public LeaveRequestService(LeaveRequestRepository repository) {
        super(repository);
    }

    public Page<Map<String, Object>> getAllLeaveRequests(PageFilterInput<LeaveRequest> input) {
        try {
            Page<LeaveRequest> leavePage = super.findAll(input);
            List<Map<String, Object>> leaves = new ArrayList<>(leavePage.getContent()).stream().map(this::toMap).toList();
            return new PageImpl<>(leaves, leavePage.getPageable(), leavePage.getTotalElements());
        } catch (Exception e) {
            log.error("Error retrieving leave requests", e);
            throw new AppException(ErrorCode.UNCATEGORIZED, "Failed to retrieve leave requests: " + e.getMessage());
        }
    }

    private Map<String, Object> toMap(LeaveRequest leaveRequest) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", leaveRequest.getId());
        map.put("employee_code", leaveRequest.getEmployeeCode());
        map.put("leave_type_id", leaveRequest.getLeaveTypeId());
        map.put("start_date", leaveRequest.getStartDate());
        map.put("end_date", leaveRequest.getEndDate());
        map.put("reason", leaveRequest.getReason());
        map.put("status", leaveRequest.getStatus());
        map.put("approved_by", leaveRequest.getApprovedBy());
        map.put("approved_at", leaveRequest.getApprovedAt());
        map.put("created_at", leaveRequest.getCreatedAt());
        map.put("updated_at", leaveRequest.getUpdatedAt());

        if(leaveRequest.getEmployeeCode() != null) {
            Employee employee = employeeRepository.findByEmployeeCode(leaveRequest.getEmployeeCode());
            if(employee != null) {
                map.put("employee_name", employee.getLastName() + " " + employee.getFirstName());
            } else {
                map.put("employee_name", "Unknown Employee");
            }
        }
        if(leaveRequest.getLeaveTypeId() != null) {
            LeaveType leaveType = leaveTypeRepository.findById(leaveRequest.getLeaveTypeId())
                    .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "Leave type not found"));
            map.put("leave_type_name", leaveType.getLeaveTypeName());
            map.put("is_paid", leaveType.getIsPaid());
        } else {
            map.put("leave_type_name", "Unknown Leave Type");
            map.put("is_paid", false);
        }
        return map;
    }

    public List<LeaveRequest> getListLeaveRequests() {
        return repository.findAll();
    }

    public LeaveRequest createLeaveRequest(LeaveRequestDTO leaveRequestDTO) {
        if (leaveRequestDTO.getEmployeeCode() == null ) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Mã nhân viên không được để trống");
        }
        if (leaveRequestDTO.getLeaveTypeId() == null || leaveRequestDTO.getLeaveTypeId() <= 0) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Loại phép không được để trống");
        }
        if(leaveRequestDTO.getReason() == null || leaveRequestDTO.getReason().isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Lý do xin phép không được để trống");
        }

        LeaveRequest leaveRequest = new LeaveRequest();
        checkValidStartDateAndEndDate(leaveRequestDTO.getStartDate(), leaveRequestDTO.getEndDate());
        updateLeaveRequestFromDTO(leaveRequest, leaveRequestDTO);
        leaveRequest.setCreatedAt(LocalDateTime.now());
        leaveRequest.setUpdatedAt(LocalDateTime.now());
        return repository.save(leaveRequest);
    }

    public LeaveRequest updateLeaveRequest(Long id, LeaveRequestDTO leaveRequestDTO) {
        if (leaveRequestDTO.getEmployeeCode() == null ) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Mã nhân viên không được để trống");
        }
        if (leaveRequestDTO.getLeaveTypeId() == null) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Loại phép không được để trống");
        }
        if(leaveRequestDTO.getReason() == null || leaveRequestDTO.getReason().isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Lý do xin phép không được để trống");
        }
        LeaveRequest leaveRequest = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "Không tìm thấy yêu cầu xin phép với ID: " + id));
        checkValidStartDateAndEndDate(leaveRequestDTO.getStartDate(), leaveRequestDTO.getEndDate());
        updateLeaveRequestFromDTO(leaveRequest, leaveRequestDTO);
        leaveRequest.setUpdatedAt(LocalDateTime.now());
        return repository.save(leaveRequest);
    }

    public void deleteLeaveRequest(Long id) {
        LeaveRequest leaveRequest = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "Leave request not found"));
        repository.delete(leaveRequest);
    }

    private void updateLeaveRequestFromDTO(LeaveRequest leaveRequest, LeaveRequestDTO dto) {
        leaveRequest.setEmployeeCode(dto.getEmployeeCode());
        leaveRequest.setLeaveTypeId(dto.getLeaveTypeId());
        leaveRequest.setStartDate(dto.getStartDate());
        leaveRequest.setEndDate(dto.getEndDate());
        leaveRequest.setReason(dto.getReason());
        leaveRequest.setStatus(dto.getStatus());
        leaveRequest.setApprovedBy(dto.getApprovedBy());
        leaveRequest.setApprovedAt(dto.getApprovedAt());
    }

    public LeaveRequest approvalLeaveRequest(Long id, String approvedBy) {
        LeaveRequest leaveRequest = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "Leave request not found"));

        leaveRequest.setStatus(ApprovalStatus.PHEDUYET);
        leaveRequest.setApprovedBy(approvedBy);
        leaveRequest.setApprovedAt(LocalDateTime.now());

        return repository.save(leaveRequest);
    }

    public LeaveRequest rejectLeaveRequest(Long id, String approvedBy) {
        LeaveRequest leaveRequest = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "Leave request not found"));

        leaveRequest.setStatus(ApprovalStatus.TUCHOI);
        leaveRequest.setApprovedBy(approvedBy);
        leaveRequest.setApprovedAt(LocalDateTime.now());

        return repository.save(leaveRequest);
    }

    /**
     * Check if an employee has an approved leave request for a specific date
     * 
     * @param employeeCode The employee code
     * @param date The date to check
     * @return true if the employee has an approved leave request for the date
     */
    public boolean hasApprovedLeaveRequestForDate(String employeeCode, LocalDate date) {
        List<LeaveRequest> leaveRequests = repository.findByEmployeeCodeAndStatus(employeeCode, ApprovalStatus.PHEDUYET);
        
        for (LeaveRequest request : leaveRequests) {
            // Check if the date falls within the leave request period
            if ((request.getStartDate().isBefore(date) || request.getStartDate().isEqual(date)) && 
                (request.getEndDate().isAfter(date) || request.getEndDate().isEqual(date))) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Calculate the used and remaining leave days for each leave type for a given employee
     * 
     * @param employeeCode The employee code
     * @return List of LeaveBalanceDTO containing leave type information and balance
     */
    public List<LeaveBalanceDTO> calculateLeaveBalance(String employeeCode) {
        try {
            // Get the current year
            int currentYear = Year.now().getValue();
            
            // Get all leave types
            List<LeaveType> leaveTypes = leaveTypeRepository.findAll();
            
            // Get all approved leave requests for the employee in the current year
            List<LeaveRequest> leaveRequests = repository.findPendingAndApprovedLeaveRequestsByEmployeeCodeAndYear(employeeCode, currentYear);
            
            // Calculate used days for each leave type
            Map<Long, Integer> usedDaysMap = new HashMap<>();
            
            for (LeaveRequest request : leaveRequests) {
                Long leaveTypeId = request.getLeaveTypeId();
                
                // Calculate days between start and end date (inclusive)
                long daysBetween = Duration.between(
                        request.getStartDate().atStartOfDay(),
                        request.getEndDate().atStartOfDay()
                ).toDays() + 1; // Add 1 to include the end date
                
                // Add to used days for this leave type
                usedDaysMap.put(leaveTypeId, usedDaysMap.getOrDefault(leaveTypeId, 0) + (int) daysBetween);
            }
            
            // Create result list
            List<LeaveBalanceDTO> result = new ArrayList<>();
            
            for (LeaveType leaveType : leaveTypes) {
                int maxDays = leaveType.getMaxDaysPerYear() != null ? leaveType.getMaxDaysPerYear() : 0;
                int usedDays = usedDaysMap.getOrDefault(leaveType.getId(), 0);
                int remainingDays = maxDays - usedDays;
                
                // Ensure remaining days is not negative
                remainingDays = Math.max(0, remainingDays);
                
                LeaveBalanceDTO balanceDTO = new LeaveBalanceDTO(
                        leaveType.getId(),
                        leaveType.getLeaveTypeName(),
                        maxDays,
                        usedDays,
                        remainingDays,
                        leaveType.getIsPaid()
                );
                
                result.add(balanceDTO);
            }
            
            return result;
        } catch (Exception e) {
            log.error("Error calculating leave balance for employee {}", employeeCode, e);
            throw new AppException(ErrorCode.UNCATEGORIZED, "Failed to calculate leave balance: " + e.getMessage());
        }
    }

    /**
     * Scheduled task to reject pending leave requests whose endDate is before or equal to today
     */
    @Scheduled(cron = "0 0 1 * * *") // Runs every day at 1:00 AM
    public void scheduledUpdateStatusExpiredPendingRequests() {
        LocalDate today = LocalDate.now();
        List<LeaveRequest> expiredPendingRequests = repository.findByStatusAndEndDateBeforeOrEqual(ApprovalStatus.DANGCHO, today);
        for (LeaveRequest request : expiredPendingRequests) {
            request.setStatus(ApprovalStatus.TUCHOI);
            request.setApprovedBy("Hệ thống");
            request.setApprovedAt(LocalDateTime.now());
            repository.save(request);
        }
    }

    private void checkValidStartDateAndEndDate(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Ngày bắt đầu và ngày kết thúc không được để trống");
        }
        if (startDate.isAfter(endDate)) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Ngày bắt đầu không được sau ngày kết thúc");
        }
    }


}
