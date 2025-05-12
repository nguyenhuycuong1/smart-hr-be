package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.Entity.BankInfo;
import com.devcuong.smart_hr.Entity.Employee;
import com.devcuong.smart_hr.dto.BankInfoDTO;
import com.devcuong.smart_hr.exception.AppException;
import com.devcuong.smart_hr.exception.ErrorCode;
import com.devcuong.smart_hr.repository.BankInfoRepository;
import com.devcuong.smart_hr.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BankInfoService {
    @Autowired
    BankInfoRepository bankInfoRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    public void updateBankInfo(Integer id, BankInfoDTO bankInfoDTO) {
        BankInfo bankInfo = bankInfoRepository.findById(id).orElse(null);
        if(bankInfo == null) {
            throw new AppException(ErrorCode.NOT_FOUND, "Bank info not found!");
        }
        bankInfo.setBankCode(bankInfoDTO.getBankCode());
        bankInfo.setBankNumber(bankInfoDTO.getBankNumber());
        bankInfo.setBankName(bankInfoDTO.getBankName());
        bankInfo.setEmployeeCode(bankInfoDTO.getEmployeeCode());
        bankInfoRepository.save(bankInfo);
    }

    public void createBankInfo(BankInfoDTO bankInfoDTO) {
        if(bankInfoDTO.getEmployeeCode() != null && !bankInfoDTO.getEmployeeCode().isEmpty()) {
            Employee employee = employeeRepository.findByEmployeeCode(bankInfoDTO.getEmployeeCode());
            if(employee == null) {
                throw new AppException(ErrorCode.NOT_FOUND, "not found");
            }
            BankInfo bankInfo = new BankInfo();
            bankInfo.setBankCode(bankInfoDTO.getBankCode());
            bankInfo.setBankNumber(bankInfoDTO.getBankNumber());
            bankInfo.setBankName(bankInfoDTO.getBankName());
            bankInfo.setEmployeeCode(bankInfoDTO.getEmployeeCode());
            bankInfoRepository.save(bankInfo);
        }else {
            throw new AppException(ErrorCode.INPUT_INVALID, "input invalid");
        }
    }

    public void createOrUpdateBankInfo(BankInfoDTO bankInfoDTO) {
            if(bankInfoDTO.getId() != null) {
                log.info("update");
                updateBankInfo(bankInfoDTO.getId(), bankInfoDTO);
            } else {
                log.info("create");
                createBankInfo(bankInfoDTO);
            }
    }
}
