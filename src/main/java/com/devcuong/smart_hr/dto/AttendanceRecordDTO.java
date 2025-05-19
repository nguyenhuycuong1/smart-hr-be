package com.devcuong.smart_hr.dto;

import com.devcuong.smart_hr.enums.AttendanceStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceRecordDTO {

    @JsonProperty("employee_code")
    String employeeCode;

    @JsonProperty("check_in_time")
    LocalTime checkInTime;

    @JsonProperty("check_out_time")
    LocalTime checkOutTime;

    @JsonProperty("work_date")
    LocalDate workDate;

    @Enumerated(EnumType.STRING)
    AttendanceStatus status;

    @JsonProperty("total_hours")
    Double totalHours;

    @JsonProperty("overtime_hours")
    Double overtimeHours;
}
