package com.devcuong.smart_hr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalanceDTO {
    @JsonProperty("leave_type_id")
    private Long leaveTypeId;
    @JsonProperty("leave_type_name")
    private String leaveTypeName;
    @JsonProperty("max_days_per_year")
    private Integer maxDaysPerYear;
    @JsonProperty("used_days")
    private Integer usedDays;
    @JsonProperty("remaining_days")
    private Integer remainingDays;
    @JsonProperty("is_paid")
    private Boolean isPaid;
}
