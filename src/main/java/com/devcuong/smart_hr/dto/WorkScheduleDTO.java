package com.devcuong.smart_hr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class WorkScheduleDTO {
    Integer id;

    @JsonProperty("schedule_name")
    String scheduleName;

    @JsonProperty("start_time")
    LocalTime startTime;

    @JsonProperty("end_time")
    LocalTime endTime;

    String description;

    @JsonProperty("break_start")
    LocalTime breakStart;

    @JsonProperty("break_end")
    LocalTime breakEnd;

    @JsonProperty("created_at")
    LocalDate createdAt;

    @JsonProperty("updated_at")
    LocalDate updatedAt;
}
