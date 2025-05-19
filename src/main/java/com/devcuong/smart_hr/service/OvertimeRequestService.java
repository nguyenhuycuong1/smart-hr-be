package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.Entity.OvertimeRequest;
import com.devcuong.smart_hr.dto.OvertimeRequestDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.exception.AppException;
import com.devcuong.smart_hr.exception.ErrorCode;
import com.devcuong.smart_hr.repository.OvertimeRequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OvertimeRequestService extends SearchService<OvertimeRequest> {

    @Autowired
    OvertimeRequestRepository repository;

    public OvertimeRequestService(OvertimeRequestRepository repository) {
        super(repository);
    }

    public Page<OvertimeRequest> getAllOvertimeRequests(PageFilterInput<OvertimeRequest> input) {
        try {
            Page<OvertimeRequest> overtimePage = super.findAll(input);
            List<OvertimeRequest> overtimes = new ArrayList<>(overtimePage.getContent());
            return new PageImpl<>(overtimes, overtimePage.getPageable(), overtimePage.getTotalElements());
        } catch (Exception e) {
            log.error("Error retrieving overtime requests", e);
            throw new AppException(ErrorCode.UNCATEGORIZED, "Failed to retrieve overtime requests: " + e.getMessage());
        }
    }

    public List<OvertimeRequest> getListOvertimeRequests() {
        return repository.findAll();
    }

    public OvertimeRequest createOvertimeRequest(OvertimeRequestDTO overtimeRequestDTO) {
        OvertimeRequest overtimeRequest = new OvertimeRequest();
        updateOvertimeRequestFromDTO(overtimeRequest, overtimeRequestDTO);
        overtimeRequest.setCreatedAt(LocalDateTime.now());
        overtimeRequest.setUpdatedAt(LocalDateTime.now());
        return repository.save(overtimeRequest);
    }

    public OvertimeRequest updateOvertimeRequest(Long id, OvertimeRequestDTO overtimeRequestDTO) {
        OvertimeRequest overtimeRequest = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "Overtime request not found"));
        updateOvertimeRequestFromDTO(overtimeRequest, overtimeRequestDTO);
        overtimeRequest.setUpdatedAt(LocalDateTime.now());
        return repository.save(overtimeRequest);
    }

    public void deleteOvertimeRequest(Long id) {
        OvertimeRequest overtimeRequest = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "Overtime request not found"));
        repository.delete(overtimeRequest);
    }

    private void updateOvertimeRequestFromDTO(OvertimeRequest overtimeRequest, OvertimeRequestDTO dto) {
        overtimeRequest.setEmployeeCode(dto.getEmployeeCode());
        overtimeRequest.setWorkDate(dto.getWorkDate());
        overtimeRequest.setStartTime(dto.getStartTime());
        overtimeRequest.setEndTime(dto.getEndTime());
        overtimeRequest.setReason(dto.getReason());
        overtimeRequest.setStatus(dto.getStatus());
        overtimeRequest.setApprovedBy(dto.getApprovedBy());
        overtimeRequest.setApprovalDate(dto.getApprovalDate());
    }
}
