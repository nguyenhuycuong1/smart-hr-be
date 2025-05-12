package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.dto.EmployeeDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.service.specification.SearchSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public abstract class SearchService<T> {
    private final JpaSpecificationExecutor<T> repository;
    public SearchService(JpaSpecificationExecutor<T> repository) {
        this.repository = repository;
    }

    public Page<T> findAll(PageFilterInput<T> input) {
        Sort.Direction direction = Sort.Direction.ASC;
        if("desc".equalsIgnoreCase(input.getSortOrder())) {
            direction = Sort.Direction.DESC;
        }
        String sortProperty = Optional.ofNullable(input.getSortProperty()).orElse("id");
        Pageable pageable;
        if(input.getPageSize() == 0) {
            pageable = Pageable.unpaged(Sort.by(direction, sortProperty));
        }else {
            pageable = PageRequest.of(input.getPageNumber(), input.getPageSize(), Sort.by(direction, sortProperty));
        }
        Specification<T> spec = new SearchSpecification<>(input.getFilter(), input.getCommon());
        return repository.findAll(spec, pageable);
    }
}
