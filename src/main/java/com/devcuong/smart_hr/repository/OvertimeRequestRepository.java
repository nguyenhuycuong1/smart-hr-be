package com.devcuong.smart_hr.repository;

import com.devcuong.smart_hr.Entity.OvertimeRequest;
import com.devcuong.smart_hr.enums.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OvertimeRequestRepository extends JpaRepository<OvertimeRequest, Long>, JpaSpecificationExecutor<OvertimeRequest> {
    List<OvertimeRequest> findByEmployeeCode(String employeeCode);
    List<OvertimeRequest> findByStatus(ApprovalStatus status);
    List<OvertimeRequest> findByEmployeeCodeAndStatus(String employeeCode, ApprovalStatus status);
    List<OvertimeRequest> findByWorkDate(LocalDate workDate);
    List<OvertimeRequest> findByWorkDateBetween(LocalDate startDate, LocalDate endDate);
    List<OvertimeRequest> findByEmployeeCodeAndWorkDateBetween(String employeeCode, LocalDate startDate, LocalDate endDate);
    List<OvertimeRequest> findByApprovedBy(String approvedBy);
}
