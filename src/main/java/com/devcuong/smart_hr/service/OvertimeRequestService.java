package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.Entity.Contract;
import com.devcuong.smart_hr.Entity.Employee;
import com.devcuong.smart_hr.Entity.OvertimeRequest;
import com.devcuong.smart_hr.dto.OvertimeRequestDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.enums.ApprovalStatus;
import com.devcuong.smart_hr.enums.ContractStatus;
import com.devcuong.smart_hr.exception.AppException;
import com.devcuong.smart_hr.exception.ErrorCode;
import com.devcuong.smart_hr.repository.ContractRepository;
import com.devcuong.smart_hr.repository.EmployeeRepository;
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

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    ContractRepository contractRepository;

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
        if (overtimeRequestDTO.getEmployeeCode() == null || overtimeRequestDTO.getEmployeeCode().isEmpty()) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Mã nhân viên không được để trống");
        }
        Employee employee = employeeRepository.findByEmployeeCode(overtimeRequestDTO.getEmployeeCode());
        if (employee == null) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Nhân viên không tồn tại");
        }
        Contract contract = contractRepository.findByEmployeeCodeAndStatusDangHoatDongOrSapHetHan(overtimeRequestDTO.getEmployeeCode()).getFirst();
        if (contract == null) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Nhân viên không có hợp đồng lao động đang hoạt động");
        }

        OvertimeRequest overtimeRequest = new OvertimeRequest();
        updateOvertimeRequestFromDTO(overtimeRequest, overtimeRequestDTO);
        overtimeRequest.setCreatedAt(LocalDateTime.now());
        overtimeRequest.setUpdatedAt(LocalDateTime.now());
        return repository.save(overtimeRequest);
    }

    public OvertimeRequest updateOvertimeRequest(Long id, OvertimeRequestDTO overtimeRequestDTO) {
        if (overtimeRequestDTO.getEmployeeCode() == null || overtimeRequestDTO.getEmployeeCode().isEmpty()) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Mã nhân viên không được để trống");
        }
        Employee employee = employeeRepository.findByEmployeeCode(overtimeRequestDTO.getEmployeeCode());
        if (employee == null) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Nhân viên không tồn tại");
        }
        Contract contract = contractRepository.findByEmployeeCodeAndStatusDangHoatDongOrSapHetHan(overtimeRequestDTO.getEmployeeCode()).getFirst();
        if (contract == null) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Nhân viên không có hợp đồng lao động đang hoạt động");
        }
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
        overtimeRequest.setApprovedAt(dto.getApprovedAt());
    }

    public void approveOvertimeRequest(Long id, String approvedBy) {
        OvertimeRequest overtimeRequest = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "Overtime request not found"));
        overtimeRequest.setStatus(ApprovalStatus.PHEDUYET);
        overtimeRequest.setApprovedBy(approvedBy);
        overtimeRequest.setApprovedAt(LocalDateTime.now());
        repository.save(overtimeRequest);
    }

    public void rejectOvertimeRequest(Long id, String rejectedBy) {
        OvertimeRequest overtimeRequest = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "Overtime request not found"));
        overtimeRequest.setStatus(ApprovalStatus.TUCHOI);
        overtimeRequest.setApprovedBy(rejectedBy);
        overtimeRequest.setApprovedAt(LocalDateTime.now());
        repository.save(overtimeRequest);
    }
}
