package com.devcuong.smart_hr.repository;

import com.devcuong.smart_hr.Entity.Contract;
import com.devcuong.smart_hr.dto.ContractDTO;
import com.devcuong.smart_hr.enums.ContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Integer>, JpaSpecificationExecutor<Contract> {
    Contract findByEmployeeCode(String employeeCode);
    List<Contract> findAllByEmployeeCode(String employeeCode);

    Contract findByEmployeeCodeAndStatus(String employeeCode, ContractStatus status);



    Contract findByContractCode(String contractCode);

    @Query("SELECT c FROM Contract c WHERE c.employeeCode = :employeeCode AND c.status = :status")
    List<Contract> findListByEmployeeCodeAndStatus(String employeeCode, ContractStatus status);

    @Query("SELECT c FROM Contract c WHERE c.status = 'DANGHOATDONG' AND c.endDate < CURRENT_DATE")
    List<Contract> findExpiredContracts();
    
    @Query("SELECT c FROM Contract c WHERE (c.status = 'DANGHOATDONG' OR c.status = 'SAPHETHAN') AND c.endDate BETWEEN CURRENT_DATE AND :expiryDate")
    List<Contract> findContractsExpiringWithinDays(@Param("expiryDate") LocalDate expiryDate);

    @Query("SELECT c FROM Contract c WHERE c.employeeCode = :employeeCode AND (c.status = 'DANGHOATDONG' OR c.status = 'SAPHETHAN') ORDER BY c.startDate DESC LIMIT 1")
    Optional<Contract> findFirstByEmployeeCodeAndIsActiveOrderByStartDateDesc(@Param("employeeCode") String employeeCode);

    @Query("SELECT c FROM Contract c WHERE (c.status = 'DANGHOATDONG' OR c.status = 'SAPHETHAN') ORDER BY c.startDate DESC")
    List<Contract> findAllContractIsActive();

    @Query("SELECT c FROM Contract c WHERE c.employeeCode = :employeeCode AND (c.status = 'DANGHOATDONG' OR c.status = 'SAPHETHAN')")
    List<Contract> findByEmployeeCodeAndStatusDangHoatDongOrSapHetHan(@Param("employeeCode") String employeeCode);
}
