package com.devcuong.smart_hr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class BankInfoDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    Integer id;
    @JsonProperty("bank_code")
    @Column(nullable = false, unique = true, name = "bank_code")
    String bankCode;
    @JsonProperty("bank_name")
    @Column(nullable = false, name = "bank_name")
    String bankName;
    @JsonProperty("bank_number")
    @Column(nullable = false, name = "bank_number")
    String bankNumber;
    @JsonProperty("employee_code")
    @Column(nullable = false, name = "employee_code")
    String employeeCode;
}
