package com.devcuong.smart_hr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class PipelineStageDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @JsonProperty("job_post_code")
    @Column(name = "job_post_code")
    String jobPostCode;

    @JsonProperty("stage_name")
    @Column(name = "stage_name")
    String stageName;

    @JsonProperty("stage_order")
    @Column(name = "stage_order")
    Integer stageOrder;

    @JsonProperty("is_open")
    @Column(name = "is_open")
    Boolean isOpen;

    @JsonProperty("created_at")
    @Column(name = "created_at")
    LocalDate createdAt;
}
