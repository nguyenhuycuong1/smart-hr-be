package com.devcuong.smart_hr.Entity;

import com.devcuong.smart_hr.dto.EmployeeDTO;
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
@Table(name = "employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    Integer id;

    @JsonProperty("employee_code")
    @Column(nullable = false, name = "employee_code", unique = true)
    String employeeCode;

    @JsonProperty("first_name")
    @Column(nullable = false, name = "first_name")
    String firstName;
    @JsonProperty("last_name")
    @Column(name = "last_name")
    String lastName;
    @JsonProperty("dob")
    @Column(nullable = false, name = "dob")
    LocalDate dob;
    @JsonProperty("hire_date")
    @Column(nullable = false, name = "hire_date")
    LocalDate hireDate;
    @JsonProperty("resign_date")
    @Column(name = "resign_date")
    LocalDate resignDate;
    @Column(nullable = false)
    String gender;
    @JsonProperty("phone_number")
    @Column(name = "phone_number")
    String phoneNumber;
    @Column(name = "email")
    String email;
    @Column(name = "address")
    String address;
    @JsonProperty("current_address")
    @Column(name = "current_address")
    String currentAddress;
    @JsonProperty("employee_type")
    @Column(name = "employee_type")
    String employeeType;
    @JsonProperty("department_code")
    @Column(name = "department_code")
    String departmentCode;
    @JsonProperty("team_code")
    @Column(name = "team_code")
    String teamCode;
    @JsonProperty("job_code")
    @Column(name = "job_code")
    String jobCode;
    @JsonProperty("tax_number")
    @Column(name = "tax_number")
    String taxNumber;
    @JsonProperty("social_insurance_code")
    @Column(name = "social_insurance_code")
    String socialInsuranceCode;
    @JsonProperty("health_insurance_code")
    @Column(name = "health_insurance_code")
    String healthInsuranceNumber;
    @JsonProperty("identification_number")
    @Column(name = "identification_number")
    String identificationNumber;
    @JsonProperty("marital_status")
    @Column(name = "marital_status")
    String maritalStatus;
    @Column(name = "note")
    String note;


}
