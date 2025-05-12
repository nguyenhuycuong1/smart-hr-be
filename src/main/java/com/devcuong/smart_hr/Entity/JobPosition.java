package com.devcuong.smart_hr.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "job_position")
public class JobPosition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @JsonProperty("job_code")
    @Column(nullable = false, name = "job_code")
    String jobCode;
    @JsonProperty("job_name")
    @Column(nullable = false, name = "job_name")
    String jobName;
    String description;
    @JsonProperty("department_code")
    @Column(name = "department_code")
    String departmentCode;
}
