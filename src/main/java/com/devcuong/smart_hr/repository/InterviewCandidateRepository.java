package com.devcuong.smart_hr.repository;

import com.devcuong.smart_hr.Entity.InterviewCandidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewCandidateRepository extends JpaRepository<InterviewCandidate, Long> {
    InterviewCandidate findByCandidateCodeAndInterviewSessionId(String candidateCode, Long interviewSessionId);

    List<InterviewCandidate> findByInterviewSessionId(Long id);

    void deleteAllByInterviewSessionId(Long id);
}
