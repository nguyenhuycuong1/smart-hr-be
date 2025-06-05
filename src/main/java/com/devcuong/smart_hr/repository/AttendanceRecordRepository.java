package com.devcuong.smart_hr.repository;

import com.devcuong.smart_hr.Entity.AttendanceRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord,Long>, JpaSpecificationExecutor<AttendanceRecord> {

    AttendanceRecord findByEmployeeCodeAndWorkDate(String employeeCode, LocalDate today);

    List<AttendanceRecord> findByWorkDate(LocalDate workDate);
    
    List<AttendanceRecord> findByEmployeeCodeAndWorkDateBetween(
            String employeeCode, LocalDate startDate, LocalDate endDate);

    List<AttendanceRecord> findByWorkDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT a.employeeCode, SUM(a.totalHours) as totalHours FROM AttendanceRecord a " +
           "WHERE a.workDate BETWEEN :startDate AND :endDate AND a.totalHours IS NOT NULL " +
           "GROUP BY a.employeeCode ORDER BY totalHours DESC")
    List<Object[]> findTop5MaxEmployeesByTotalHours(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate, Pageable pageable);

    @Query("SELECT a.employeeCode, SUM(a.totalHours) as totalHours FROM AttendanceRecord a " +
            "WHERE a.workDate BETWEEN :startDate AND :endDate AND a.totalHours IS NOT NULL " +
            "GROUP BY a.employeeCode ORDER BY totalHours ASC")
    List<Object[]> findTop5MinEmployeesByTotalHours(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate, Pageable pageable);
}
