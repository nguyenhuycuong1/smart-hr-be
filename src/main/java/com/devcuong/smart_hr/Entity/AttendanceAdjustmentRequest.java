package com.devcuong.smart_hr.Entity;

import com.devcuong.smart_hr.enums.ApprovalStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "attendance_adjustment_request")
public class AttendanceAdjustmentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @JsonProperty("employee_code")
    @Column(nullable = false, name = "employee_code")
    String employeeCode;

    @JsonProperty("work_date")
    @Column(nullable = false, name = "work_date")
    LocalDate workDate;

    @JsonProperty("original_check_in")
    @Column(name = "original_check_in")
    LocalTime originalCheckIn;

    @JsonProperty("original_check_out")
    @Column(name = "original_check_out")
    LocalTime originalCheckOut;

    @JsonProperty("adjusted_check_in")
    @Column(name = "adjusted_check_in")
    LocalTime adjustedCheckIn;

    @JsonProperty("adjusted_check_out")
    @Column(name = "adjusted_check_out")
    LocalTime adjustedCheckOut;

    String reason;

    @Enumerated(EnumType.STRING)
    ApprovalStatus status;

    @JsonProperty("approved_by")
    @Column(name = "approved_by")
    String approvedBy;

    @JsonProperty("approved_at")
    @Column(name = "approved_at")
    LocalDateTime approvedAt;

    @JsonProperty("created_at")
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @Column(name = "updated_at")
    LocalDateTime updatedAt;
}
