package com.devcuong.smart_hr.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "candidate_stage")
public class CandidateStage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @JsonProperty("candidate_code")
    @Column(name = "candidate_code", nullable = false)
    String candidateCode;

    @JsonProperty("stage_id")
    @Column(name = "stage_id", nullable = false)
    Integer stageId;

    @JsonProperty("job_post_code")
    @Column(name = "job_post_code", nullable = false)
    String jobPostCode;

    @JsonProperty("status")
    @Column(name = "status")
    String status;

    @JsonProperty("updated_at")
    @Column(name = "updated_at")
    String updatedAt;

    @JsonProperty("note")
    @Column(name = "note")
    String note;
}
