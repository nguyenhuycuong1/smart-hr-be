package com.devcuong.smart_hr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyAttendanceSummaryDTO {
    @JsonProperty("employee_code")
    private String employeeCode;
    private int year;
    private int month;
    @JsonProperty("total_work_days")
    private int totalWorkDays;
    @JsonProperty("total_work_hours")
    private double totalWorkHours;
    @JsonProperty("total_late_days")
    private int lateDaysCount;
    @JsonProperty("total_early_leave_days")
    private int earlyLeaveDaysCount;
    @JsonProperty("total_absent_days")
    private int absentDaysCount;
    @JsonProperty("total_overtime_hours")
    private double totalOvertimeHours;
}
