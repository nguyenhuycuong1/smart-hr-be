package com.devcuong.smart_hr.repository;

import com.devcuong.smart_hr.Entity.PipelineStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PipelineStageRepository extends JpaRepository<PipelineStage, Integer>, JpaSpecificationExecutor<PipelineStage> {

    @Query("SELECT MAX(p.stageOrder) FROM PipelineStage p")
    Integer findMaxOrder();

    List<PipelineStage> findByJobPostCode(String jobPostCode);
}
