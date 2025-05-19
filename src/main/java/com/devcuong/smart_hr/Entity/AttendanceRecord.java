package com.devcuong.smart_hr.Entity;

import com.devcuong.smart_hr.enums.AttendanceStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "attendance_record")
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @JsonProperty("employee_code")
    @Column(nullable = false, name = "employee_code")
    String employeeCode;

    @JsonProperty("check_in_time")
    @Column(name = "check_in_time")
    LocalTime checkInTime;

    @JsonProperty("check_out_time")
    @Column(name = "check_out_time")
    LocalTime checkOutTime;

    @JsonProperty("work_date")
    @Column(nullable = false, name = "work_date")
    LocalDate workDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    AttendanceStatus status;

    @JsonProperty("total_hours")
    @Column(name = "total_hours")
    Double totalHours;

    @JsonProperty("overtime_hours")
    @Column(name = "overtime_hours")
    Double overtimeHours;
}
