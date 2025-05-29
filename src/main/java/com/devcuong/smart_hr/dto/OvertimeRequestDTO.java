package com.devcuong.smart_hr.dto;

import com.devcuong.smart_hr.enums.ApprovalStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OvertimeRequestDTO {
    @JsonProperty("employee_code")
    private String employeeCode;
    
    @JsonProperty("work_date")
    private LocalDate workDate;
    
    @JsonProperty("start_time")
    private LocalTime startTime;
    
    @JsonProperty("end_time")
    private LocalTime endTime;
    
    private String reason;
    
    private ApprovalStatus status;
    
    @JsonProperty("approved_by")
    private String approvedBy;
    
    @JsonProperty("approved_at")
    private LocalDateTime approvedAt;
}
