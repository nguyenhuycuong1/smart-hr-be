package com.devcuong.smart_hr.repository;

import com.devcuong.smart_hr.Entity.WorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Integer>, JpaSpecificationExecutor<WorkSchedule> {
    WorkSchedule findByScheduleName(String scheduleName);
}
