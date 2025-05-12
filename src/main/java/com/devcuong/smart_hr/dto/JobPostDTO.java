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
import org.mapstruct.Builder;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobPostDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "job_post_code", nullable = false)
    @JsonProperty("job_post_code")
    String jobPostCode;

    @Column(name = "request_code", nullable = false)
    @JsonProperty("request_code")
    String requestCode;

    @Column(name = "title", nullable = false)
    @JsonProperty("title")
    String title;

    @Column(name = "description")
    @JsonProperty("description")
    String description;

    @JsonProperty("is_open")
    @Column(name = "is_open")
    Boolean isOpen;

    @Column(name = "created_at")
    @JsonProperty("created_at")
    LocalDate createdAt;
}
