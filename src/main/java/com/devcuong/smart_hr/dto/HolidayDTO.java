package com.devcuong.smart_hr.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HolidayDTO {
    @JsonProperty("holiday_name")
    private String holidayName;
    
    @JsonProperty("holiday_date")
    private LocalDate holidayDate;
    
    @JsonProperty("is_annual")
    private Boolean isAnnual;
    
    @JsonProperty("is_paid")
    private Boolean isPaid;
}
