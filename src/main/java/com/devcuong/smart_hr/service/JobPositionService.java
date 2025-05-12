package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.Entity.JobPosition;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.exception.AppException;
import com.devcuong.smart_hr.exception.ErrorCode;
import com.devcuong.smart_hr.repository.JobPositionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobPositionService extends SearchService<JobPosition> {
    public JobPositionService(JobPositionRepository repository) {
        super(repository);
    }

    public Page<JobPosition> searchJobPosition(PageFilterInput<JobPosition> input) {
        try {
            Page<JobPosition> page = super.findAll(input);
            List<JobPosition> jobPositions = page.getContent();
            return new PageImpl<>(jobPositions, page.getPageable(), page.getTotalElements());
        } catch (RuntimeException e) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Search failed");
        }
    }
}
