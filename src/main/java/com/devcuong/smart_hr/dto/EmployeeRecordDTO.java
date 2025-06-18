package com.devcuong.smart_hr.dto;

import com.devcuong.smart_hr.Entity.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class EmployeeRecordDTO extends EmployeeDTO{

    Department department;
    Team team;
    JobPosition jobPosition;
    Map<String, Object> contractActive;

    public EmployeeRecordDTO() {
        super();
    }

    public EmployeeRecordDTO(Integer id, String employeeCode, String firstName, String lastName, LocalDate dob, LocalDate hireDate, LocalDate resignDate, String gender, String phoneNumber, String email, String address, String currentAddress, String employeeType, String departmentCode, String teamCode, String jobCode, String taxNumber, String socialInsuranceCode, String healthInsuranceNumber, String identificationNumber, String maritalStatus, String note, Boolean isActive, Boolean hasAccount, Department department, Team team, JobPosition jobPosition, Map<String, Object> contractActive) {
        super(id, employeeCode, firstName, lastName, dob, hireDate, resignDate, gender, phoneNumber, email, address, currentAddress, employeeType, departmentCode, teamCode, jobCode, taxNumber, socialInsuranceCode, healthInsuranceNumber, identificationNumber, maritalStatus, note, isActive, hasAccount);
        this.department = department;
        this.team = team;
        this.jobPosition = jobPosition;
        this.contractActive = contractActive;
    }




}
