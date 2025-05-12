package com.devcuong.smart_hr.repository;

import com.devcuong.smart_hr.Entity.InterviewRecruiter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewRecruiterRepository extends JpaRepository<InterviewRecruiter, Long> {
    InterviewRecruiter findByRecruiterCodeAndInterviewSessionId(String recruiterCode, Long id);

    List<InterviewRecruiter> findByInterviewSessionId(Long interviewSessionId);

    void deleteAllByInterviewSessionId(Long interviewSessionId);
}
