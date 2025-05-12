package com.devcuong.smart_hr.dto;

import com.devcuong.smart_hr.Entity.Department;
import com.devcuong.smart_hr.Entity.JobPosition;
import com.devcuong.smart_hr.Entity.RecruitmentRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class RecruitmentRequestRecordDTO extends RecruitmentRequestDTO {
    Department department;
    @JsonProperty("job_position")
    JobPosition jobPosition;

    public RecruitmentRequestRecordDTO(RecruitmentRequestDTO recruitmentRequestDTO, Department department, JobPosition jobPosition) {
        super(recruitmentRequestDTO);
        this.department = department;
        this.jobPosition = jobPosition;
    }

}
