package com.devcuong.smart_hr.Entity;

import com.devcuong.smart_hr.enums.CandidateStatus;
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
@Table(name = "candidate")
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @JsonProperty("candidate_code")
    @Column(name = "candidate_code", nullable = false)
    String candidateCode;

    @JsonProperty("first_name")
    @Column(name = "first_name", nullable = false)
    String firstName;

    @JsonProperty("last_name")
    @Column(name = "last_name")
    String lastName;

    String email;

    @JsonProperty("phone_number")
    @Column(name = "phone_number")
    String phoneNumber;

    @JsonProperty("job_post_code")
    @Column(name = "job_post_code")
    String jobPostCode;

    @JsonProperty("resume_url")
    @Column(name = "resume_url")
    String resumeUrl;

    @Enumerated(EnumType.STRING)
    CandidateStatus status;

    @JsonProperty("applied_at")
    @Column(name = "applied_at")
    LocalDate appliedAt;

    @JsonProperty("dob")
    @Column(nullable = false, name = "dob")
    LocalDate dob;

    @Column(name = "address")
    String address;

    @JsonProperty("current_address")
    @Column(name = "current_address")
    String currentAddress;

    @JsonProperty("gender")
    @Column(name = "gender")
    String gender;
}
