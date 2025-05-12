package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.Entity.Department;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.exception.AppException;
import com.devcuong.smart_hr.exception.ErrorCode;
import com.devcuong.smart_hr.repository.DepartmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService extends SearchService<Department> {
    public DepartmentService(DepartmentRepository repository) {
        super(repository);
    }

    public Page<Department> searchDepartment(PageFilterInput<Department> input) {
        try {
            Page<Department> page = super.findAll(input);
            List<Department> departments = page.getContent();
            return new PageImpl<>(departments, page.getPageable(), page.getTotalElements());
        } catch (RuntimeException e) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Search failed");
        }
    }
}
