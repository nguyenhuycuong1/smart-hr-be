package com.devcuong.smart_hr.repository;

import com.devcuong.smart_hr.Entity.Contract;
import com.devcuong.smart_hr.dto.ContractDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Integer>, JpaSpecificationExecutor<Contract> {
    Contract findByEmployeeCode(String employeeCode);
    List<Contract> findAllByEmployeeCode(String employeeCode);

    Contract findByEmployeeCodeAndStatus(String employeeCode, String status);

    Contract findByContractCode(String contractCode);

    @Query("SELECT c FROM Contract c WHERE c.employeeCode = :employeeCode AND c.status = :status")
    List<Contract> findListByEmployeeCodeAndStatus(String employeeCode, String status);

    @Query("SELECT c FROM Contract c WHERE c.status = 'Đang hoạt động' AND c.endDate < CURRENT_DATE")
    List<Contract> findExpiredContracts();

    @Query("SELECT c FROM Contract c WHERE c.employeeCode = :employeeCode AND c.status = 'Đang hoạt động' ORDER BY c.startDate DESC LIMIT 1")
    Optional<Contract> findFirstByEmployeeCodeAndIsActiveOrderByStartDateDesc(@Param("employeeCode") String employeeCode);

    @Query("SELECT c FROM Contract c WHERE c.status = 'Đang hoạt động' ORDER BY c.startDate DESC")
    List<Contract> findAllContractIsActive();
}
