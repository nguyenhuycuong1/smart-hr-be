package com.devcuong.smart_hr.dto;

import com.devcuong.smart_hr.enums.ApprovalStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestDTO {
    @JsonProperty("employee_code")
    private String employeeCode;
    
    @JsonProperty("leave_type_id")
    private Long leaveTypeId;
    
    @JsonProperty("start_date")
    private LocalDate startDate;
    
    @JsonProperty("end_date")
    private LocalDate endDate;
    
    private String reason;
    
    private ApprovalStatus status;
    
    @JsonProperty("approved_by")
    private String approvedBy;
    
    @JsonProperty("approved_at")
    private LocalDateTime approvedAt;
}
