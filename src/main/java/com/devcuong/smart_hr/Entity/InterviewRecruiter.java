package com.devcuong.smart_hr.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "interview_recruiter")
public class InterviewRecruiter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @JsonProperty("recruiter_code")
    @Column(nullable = false, name = "recruiter_code")
    String recruiterCode;
    @Column(nullable = false, name = "interview_session_id")
    @JsonProperty("interview_session_id")
    Long interviewSessionId;

}
