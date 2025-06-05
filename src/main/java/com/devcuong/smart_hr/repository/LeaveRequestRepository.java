package com.devcuong.smart_hr.repository;

import com.devcuong.smart_hr.Entity.LeaveRequest;
import com.devcuong.smart_hr.enums.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long>, JpaSpecificationExecutor<LeaveRequest> {
    
    List<LeaveRequest> findByEmployeeCode(String employeeCode);
    
    List<LeaveRequest> findByEmployeeCodeAndStatus(String employeeCode, ApprovalStatus status);
    
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.employeeCode = :employeeCode AND lr.status = 'PHEDUYET' AND YEAR(lr.startDate) = :year")
    List<LeaveRequest> findApprovedLeaveRequestsByEmployeeCodeAndYear(@Param("employeeCode") String employeeCode, @Param("year") int year);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.status = 'PHEDUYET' AND YEAR(lr.startDate) = :year")
    List<LeaveRequest> findApprovedLeaveRequestsByYear(int year);

    @Query("SELECT lr FROM LeaveRequest lr WHERE YEAR(lr.startDate) = :year")
    List<LeaveRequest> findAllByYear(int year);
}
