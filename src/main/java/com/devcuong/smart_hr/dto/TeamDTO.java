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
public class TeamDTO {

    @JsonProperty("team_code")
    String teamCode;

    @JsonProperty("team_name")
    String teamName;

    String description;

    @JsonProperty("department_code")
    String departmentCode;

}
