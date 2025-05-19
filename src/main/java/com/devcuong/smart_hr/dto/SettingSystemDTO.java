package com.devcuong.smart_hr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class SettingSystemDTO {

    @JsonProperty("prefix_emp_code")
    String prefixEmpCode;

    @JsonProperty("week_start_date")
    @Column(name = "week_start_date")
    Integer weekStartDate;

    @JsonProperty("week_end_date")
    @Column(name = "week_end_date")
    Integer weekEndDate;

    @JsonProperty("late_threshold_minutes")
    @Column(name = "late_threshold_minutes")
    Integer lateThresholdMinutes;

    @JsonProperty("absent_threshold_minutes")
    @Column(name = "absent_threshold_minutes")
    Integer absentThresholdMinutes;

    @JsonProperty("early_leave_threshold_minutes")
    @Column(name = "early_leave_threshold_minutes")
    Integer earlyLeaveThresholdMinutes;

    @JsonProperty("overtime_threshold_minutes")
    @Column(name = "overtime_threshold_minutes")
    Integer overtimeThresholdMinutes;
}
