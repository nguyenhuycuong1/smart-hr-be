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
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "contract")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    Integer id;

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

    @Column(nullable = false, name = "shift")
    String shift;

    @JsonProperty("type_of_work")
    @Column(nullable = false, name = "type_of_work")
    String typeOfWork;

    @JsonProperty("pay_frequency")
    @Column(nullable = false, name = "pay_frequency")
    String payFrequency;

    @Column(name = "note")
    String note;
}
