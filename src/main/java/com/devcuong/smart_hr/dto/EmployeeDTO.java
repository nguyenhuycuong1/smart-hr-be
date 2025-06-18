package com.devcuong.smart_hr.dto;

import com.devcuong.smart_hr.Entity.Employee;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeDTO {
    @Column(nullable = false, unique = true)
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
    @Column(name = "is_active")
    @JsonProperty("is_active")
    Boolean isActive = true;
    @JsonProperty("has_account")
    @Column(name = "has_account")
    Boolean hasAccount = false;


    public static EmployeeDTO toDTO(Employee employee) {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.id = employee.getId();
        employeeDTO.employeeCode = employee.getEmployeeCode();
        employeeDTO.firstName = employee.getFirstName();
        employeeDTO.lastName = employee.getLastName();
        employeeDTO.dob = employee.getDob();
        employeeDTO.hireDate = employee.getHireDate();
        employeeDTO.resignDate = employee.getResignDate();
        employeeDTO.gender = employee.getGender();
        employeeDTO.phoneNumber = employee.getPhoneNumber();
        employeeDTO.email = employee.getEmail();
        employeeDTO.address = employee.getAddress();
        employeeDTO.currentAddress = employee.getCurrentAddress();
        employeeDTO.employeeType = employee.getEmployeeType();
        employeeDTO.departmentCode = employee.getDepartmentCode();
        employeeDTO.teamCode = employee.getTeamCode();
        employeeDTO.jobCode = employee.getJobCode();
        employeeDTO.taxNumber = employee.getTaxNumber();
        employeeDTO.socialInsuranceCode = employee.getSocialInsuranceCode();
        employeeDTO.healthInsuranceNumber = employee.getHealthInsuranceNumber();
        employeeDTO.identificationNumber = employee.getIdentificationNumber();
        employeeDTO.maritalStatus = employee.getMaritalStatus();
        employeeDTO.note = employee.getNote();
        employeeDTO.isActive = employee.getIsActive();
        employeeDTO.hasAccount = employee.getHasAccount();
        return employeeDTO;
    }
}