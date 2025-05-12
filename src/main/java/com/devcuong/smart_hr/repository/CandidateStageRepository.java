package com.devcuong.smart_hr.repository;

import com.devcuong.smart_hr.Entity.CandidateStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CandidateStageRepository extends JpaRepository<CandidateStage, Long>, JpaSpecificationExecutor<CandidateStage> {
    void deleteByStageId(Integer stageId);

    List<CandidateStage> findAllByStageId(Integer stageId);
}
