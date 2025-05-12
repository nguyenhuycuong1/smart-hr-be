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
public class JobPositionDTO {
    @JsonProperty("job_name")
    String jobName;
    @JsonProperty("job_code")
    String jobCode;
    String description;
    @JsonProperty("department_code")
    String departmentCode;
}
