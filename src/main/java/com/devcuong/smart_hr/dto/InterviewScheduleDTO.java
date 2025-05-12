package com.devcuong.smart_hr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InterviewScheduleDTO {
    Long id;
    @Column(name = "job_post_code", nullable = false)
    @JsonProperty("job_post_code")
    String jobPostCode;
    String title;
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
    @JsonProperty("candidate_codes")
    List<String> candidateCodes;
    @JsonProperty("recruiter_codes")
    List<String> recruiterCodes;
}
