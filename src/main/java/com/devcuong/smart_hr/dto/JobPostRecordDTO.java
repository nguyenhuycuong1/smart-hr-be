package com.devcuong.smart_hr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class JobPostRecordDTO extends JobPostDTO {

    @JsonProperty("recruitment_request")
    RecruitmentRequestRecordDTO recruitmentRequestRecord;



}
