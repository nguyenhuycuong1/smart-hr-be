package com.devcuong.smart_hr.dto;

import lombok.Data;

@Data
public class EmployeeWithBankInfoDTO {
    private EmployeeDTO employee;
    private BankInfoDTO bankInfo;
}
