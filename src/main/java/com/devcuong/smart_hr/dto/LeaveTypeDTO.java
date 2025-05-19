package com.devcuong.smart_hr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveTypeDTO {
    @JsonProperty("leave_type_name")
    private String leaveTypeName;
    
    private String description;
    
    @JsonProperty("is_paid")
    private Boolean isPaid;
    
    @JsonProperty("max_days_per_year")
    private Integer maxDaysPerYear;
}
