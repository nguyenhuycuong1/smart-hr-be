package com.devcuong.smart_hr.repository;

import com.devcuong.smart_hr.Entity.LeaveRequest;
import com.devcuong.smart_hr.enums.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long>, JpaSpecificationExecutor<LeaveRequest> {
    List<LeaveRequest> findByEmployeeCode(String employeeCode);
    List<LeaveRequest> findByStatus(ApprovalStatus status);
    List<LeaveRequest> findByEmployeeCodeAndStatus(String employeeCode, ApprovalStatus status);
    List<LeaveRequest> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
    List<LeaveRequest> findByEmployeeCodeAndStartDateBetween(String employeeCode, LocalDate startDate, LocalDate endDate);
    List<LeaveRequest> findByApprovedBy(String approvedBy);
}
