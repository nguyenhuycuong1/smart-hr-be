package com.devcuong.smart_hr.Entity;

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
@Table(name = "work_schedule")
public class WorkSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    Integer id;

    @JsonProperty("schedule_name")
    @Column(nullable = false, name = "schedule_name")
    String scheduleName;

    @JsonProperty("start_time")
    @Column(nullable = false, name = "start_time")
    LocalTime startTime;

    @JsonProperty("end_time")
    @Column(nullable = false, name = "end_time")
    LocalTime endTime;

    @Column(name = "description")
    String description;

    @JsonProperty("break_start")
    @Column(name = "break_start")
    LocalTime breakStart;

    @JsonProperty("break_end")
    @Column(name = "break_end")
    LocalTime breakEnd;

    @JsonProperty("total_work_hours")
    @Column(name = "total_work_hours")
    Double totalWorkHours;

    @JsonProperty("created_at")
    @Column(name = "created_at")
    LocalDate createdAt;

    @JsonProperty("updated_at")
    @Column(name = "updated_at")
    LocalDate updatedAt;
}
