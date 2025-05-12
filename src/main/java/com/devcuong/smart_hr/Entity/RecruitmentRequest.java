package com.devcuong.smart_hr.Entity;

import com.devcuong.smart_hr.enums.RecruitmentRequestStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor()
@AllArgsConstructor()
@Table(name = "recruitment_request")
public class RecruitmentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("recruitment_request_code")
    @Column(name = "recruitment_request_code", nullable = false)
    private String recruitmentRequestCode;

    @JsonProperty("department_code")
    @Column(name = "department_code")
    private String departmentCode;

    @JsonProperty("job_code")
    @Column(name = "job_code", nullable = false)
    private String jobCode;

    @JsonProperty("quantity")
    @Column(name = "quantity")
    private Integer quantity;

    @JsonProperty("status")
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private RecruitmentRequestStatus status;

    @JsonProperty("created_at")
    @Column(name = "created_at")
    private LocalDate createdAt;

    @JsonProperty("created_by")
    @Column(name = "created_by")
    private String createdBy;

    @JsonProperty("username_created")
    @Column(name = "username_created")
    private String usernameCreated;

    @JsonProperty("updated_at")
    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @JsonProperty("updated_by")
    @Column(name = "updated_by")
    private String updatedBy;

    @JsonProperty("username_updated")
    @Column(name = "username_updated")
    private String usernameUpdated;;

}