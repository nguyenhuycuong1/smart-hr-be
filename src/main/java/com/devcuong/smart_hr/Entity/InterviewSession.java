package com.devcuong.smart_hr.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "interview_session")
public class InterviewSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String title;
    @Column(name = "job_post_code", nullable = false)
    @JsonProperty("job_post_code")
    String jobPostCode;
    String description;
    @Column(name = "start_time", nullable = false)
    @JsonProperty("start_time")
    OffsetDateTime startTime;
    @Column(name = "end_time", nullable = false)
    @JsonProperty("end_time")
    OffsetDateTime endTime;
    String location;
    @Column(name = "meeting_link")
    @JsonProperty("meeting_link")
    String meetingLink;
    String note;
}
