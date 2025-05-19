package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.Entity.LeaveRequest;
import com.devcuong.smart_hr.dto.LeaveRequestDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.exception.AppException;
import com.devcuong.smart_hr.exception.ErrorCode;
import com.devcuong.smart_hr.repository.LeaveRequestRepository;
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
public class LeaveRequestService extends SearchService<LeaveRequest> {

    @Autowired
    LeaveRequestRepository repository;

    public LeaveRequestService(LeaveRequestRepository repository) {
        super(repository);
    }

    public Page<LeaveRequest> getAllLeaveRequests(PageFilterInput<LeaveRequest> input) {
        try {
            Page<LeaveRequest> leavePage = super.findAll(input);
            List<LeaveRequest> leaves = new ArrayList<>(leavePage.getContent());
            return new PageImpl<>(leaves, leavePage.getPageable(), leavePage.getTotalElements());
        } catch (Exception e) {
            log.error("Error retrieving leave requests", e);
            throw new AppException(ErrorCode.UNCATEGORIZED, "Failed to retrieve leave requests: " + e.getMessage());
        }
    }

    public List<LeaveRequest> getListLeaveRequests() {
        return repository.findAll();
    }

    public LeaveRequest createLeaveRequest(LeaveRequestDTO leaveRequestDTO) {
        LeaveRequest leaveRequest = new LeaveRequest();
        updateLeaveRequestFromDTO(leaveRequest, leaveRequestDTO);
        leaveRequest.setCreatedAt(LocalDateTime.now());
        leaveRequest.setUpdatedAt(LocalDateTime.now());
        return repository.save(leaveRequest);
    }

    public LeaveRequest updateLeaveRequest(Long id, LeaveRequestDTO leaveRequestDTO) {
        LeaveRequest leaveRequest = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "Leave request not found"));
        updateLeaveRequestFromDTO(leaveRequest, leaveRequestDTO);
        leaveRequest.setUpdatedAt(LocalDateTime.now());
        return repository.save(leaveRequest);
    }

    public void deleteLeaveRequest(Long id) {
        LeaveRequest leaveRequest = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "Leave request not found"));
        repository.delete(leaveRequest);
    }

    private void updateLeaveRequestFromDTO(LeaveRequest leaveRequest, LeaveRequestDTO dto) {
        leaveRequest.setEmployeeCode(dto.getEmployeeCode());
        leaveRequest.setLeaveTypeId(dto.getLeaveTypeId());
        leaveRequest.setStartDate(dto.getStartDate());
        leaveRequest.setEndDate(dto.getEndDate());
        leaveRequest.setReason(dto.getReason());
        leaveRequest.setStatus(dto.getStatus());
        leaveRequest.setApprovedBy(dto.getApprovedBy());
        leaveRequest.setApprovalDate(dto.getApprovalDate());
    }
}
