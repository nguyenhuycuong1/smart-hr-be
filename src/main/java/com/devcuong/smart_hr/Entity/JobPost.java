package com.devcuong.smart_hr.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "job_post")
public class JobPost {
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
