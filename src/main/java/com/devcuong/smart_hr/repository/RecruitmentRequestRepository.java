package com.devcuong.smart_hr.repository;

import com.devcuong.smart_hr.Entity.RecruitmentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecruitmentRequestRepository extends JpaRepository<RecruitmentRequest, Long>, JpaSpecificationExecutor<RecruitmentRequest> {

    Optional<RecruitmentRequest> findByRecruitmentRequestCode(String recruitmentRequestCode);

}
