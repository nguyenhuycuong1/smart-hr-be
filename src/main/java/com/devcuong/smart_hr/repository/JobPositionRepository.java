package com.devcuong.smart_hr.repository;

import com.devcuong.smart_hr.Entity.JobPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobPositionRepository extends JpaRepository<JobPosition, Integer>, JpaSpecificationExecutor<JobPosition> {
    Optional<JobPosition> findJobPositionByJobCode(String jobCode);
}
