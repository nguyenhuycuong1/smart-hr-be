package com.devcuong.smart_hr.repository;

import com.devcuong.smart_hr.Entity.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord,Long>, JpaSpecificationExecutor<AttendanceRecord> {

    AttendanceRecord findByEmployeeCodeAndWorkDate(String employeeCode, LocalDate today);
    
    List<AttendanceRecord> findByEmployeeCodeAndWorkDateBetween(
            String employeeCode, LocalDate startDate, LocalDate endDate);
}
