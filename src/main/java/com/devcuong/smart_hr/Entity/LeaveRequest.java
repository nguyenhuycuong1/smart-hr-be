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

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "leave_request")
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @JsonProperty("employee_code")
    @Column(nullable = false, name = "employee_code")
    String employeeCode;

    @JsonProperty("leave_type_id")
    @Column(nullable = false, name = "leave_type_id")
    Long leaveTypeId;

    @JsonProperty("start_date")
    @Column(name = "start_date")
    LocalDate startDate;

    @JsonProperty("end_date")
    @Column(name = "end_date")
    LocalDate endDate;

    String reason;

    @Enumerated(EnumType.STRING)
    ApprovalStatus status;

    @JsonProperty("approved_by")
    @Column(name = "approved_by")
    String approvedBy;

    @JsonProperty("approval_date")
    @Column(name = "approval_date")
    LocalDateTime approvalDate;

    @JsonProperty("created_at")
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @Column(name = "updated_at")
    LocalDateTime updatedAt;
}
