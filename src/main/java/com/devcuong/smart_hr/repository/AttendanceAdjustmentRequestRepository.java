package com.devcuong.smart_hr.repository;

import com.devcuong.smart_hr.Entity.AttendanceAdjustmentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceAdjustmentRequestRepository extends JpaRepository<AttendanceAdjustmentRequest, Long>, JpaSpecificationExecutor<AttendanceAdjustmentRequest> {
    List<AttendanceAdjustmentRequest> findByEmployeeCode(String employeeCode);
    List<AttendanceAdjustmentRequest> findByStatus(String status);
    List<AttendanceAdjustmentRequest> findByEmployeeCodeAndStatus(String employeeCode, String status);
    AttendanceAdjustmentRequest findByEmployeeCodeAndWorkDate(String employeeCode, LocalDate workDate);
    List<AttendanceAdjustmentRequest> findByApprovedBy(String approvedBy);
}
