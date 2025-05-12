package com.devcuong.smart_hr.repository;

import com.devcuong.smart_hr.Entity.BankInfo;
import com.devcuong.smart_hr.dto.BankInfoDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankInfoRepository extends JpaRepository<BankInfo, Integer> {
    BankInfo findByEmployeeCode(String employeeCode);
}
