package com.devcuong.smart_hr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class ContractDTO {
    @JsonProperty("contract_code")
    @Column(nullable = false, unique = true, name = "contract_code")
    String contractCode;

    @JsonProperty("contract_name")
    @Column(nullable = false, name = "contract_name")
    String contractName;

    @JsonProperty("contract_type")
    @Column(nullable = false, name = "contract_type")
    String contractType;

    @JsonProperty("employee_code")
    @Column(nullable = false, name = "employee_code")
    String employeeCode;

    @Column(nullable = false, name = "status")
    String status;

    @JsonProperty("start_date")
    @Column(nullable = false, name = "start_date")
    LocalDate startDate;

    @JsonProperty("end_date")
    @Column(nullable = false, name = "end_date")
    LocalDate endDate;

    @JsonProperty("basic_salary")
    @Column(nullable = false, name = "basic_salary")
    String basicSalary;

    @JsonProperty("job_position")
    @Column(nullable = false, name = "job_position")
    String jobPosition;

    @Column(name = "shift")
    String shift;

    @Column(nullable = false, name = "work_schedule_id")
    @JsonProperty("work_schedule_id")
    Integer workScheduleId;

    @JsonProperty("type_of_work")
    @Column(nullable = false, name = "type_of_work")
    String typeOfWork;

    @JsonProperty("pay_frequency")
    @Column(nullable = false, name = "pay_frequency")
    String payFrequency;

    @Column(name = "note")
    String note;
}
