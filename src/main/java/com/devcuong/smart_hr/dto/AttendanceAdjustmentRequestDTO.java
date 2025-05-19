package com.devcuong.smart_hr.dto;

import com.devcuong.smart_hr.enums.ApprovalStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceAdjustmentRequestDTO {

    @JsonProperty("employee_code")
    String employeeCode;

    @JsonProperty("work_date")
    LocalDate workDate;

    @JsonProperty("original_check_in")
    LocalTime originalCheckIn;

    @JsonProperty("original_check_out")
    LocalTime originalCheckOut;

    @JsonProperty("adjusted_check_in")
    LocalTime adjustedCheckIn;

    @JsonProperty("adjusted_check_out")
    LocalTime adjustedCheckOut;

    String reason;

    @Enumerated(EnumType.STRING)
    ApprovalStatus status;

    @JsonProperty("approved_by")
    String approvedBy;

    @JsonProperty("approved_at")
    LocalDateTime approvedAt;
}
