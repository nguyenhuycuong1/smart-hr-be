package com.devcuong.smart_hr.repository;

import com.devcuong.smart_hr.Entity.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobPostRepository extends JpaRepository<JobPost, Long>, JpaSpecificationExecutor<JobPost> {
    Optional<JobPost> findByRequestCode(String requestCode);

    Optional<JobPost> findByJobPostCode(String jobPostCode);
}
