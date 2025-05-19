package com.devcuong.smart_hr.repository;

import com.devcuong.smart_hr.Entity.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long>, JpaSpecificationExecutor<AttendanceRecord> {
    AttendanceRecord findByEmployeeCodeAndWorkDate(String employeeCode, LocalDate workDate);
    List<AttendanceRecord> findAllByEmployeeCode(String employeeCode);
    List<AttendanceRecord> findAllByWorkDateBetween(LocalDate startDate, LocalDate endDate);
    List<AttendanceRecord> findAllByEmployeeCodeAndWorkDateBetween(String employeeCode, LocalDate startDate, LocalDate endDate);
}
