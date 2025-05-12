package com.devcuong.smart_hr.repository;

import com.devcuong.smart_hr.Entity.Candidate;
import com.devcuong.smart_hr.enums.CandidateStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long>, JpaSpecificationExecutor<Candidate> {
    Candidate findByCandidateCode(String candidateCode);

    Long countByJobPostCodeAndStatus(String jobPostCode, CandidateStatus status);

    List<Candidate> findByJobPostCodeAndCandidateCodeNot(String jobPostCode, String candidateCode);
}
