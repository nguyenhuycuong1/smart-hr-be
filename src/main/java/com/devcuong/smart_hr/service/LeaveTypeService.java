package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.Entity.LeaveType;
import com.devcuong.smart_hr.dto.LeaveTypeDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.exception.AppException;
import com.devcuong.smart_hr.exception.ErrorCode;
import com.devcuong.smart_hr.repository.LeaveTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class LeaveTypeService extends SearchService<LeaveType> {

    @Autowired
    LeaveTypeRepository repository;

    public LeaveTypeService(LeaveTypeRepository repository) {
        super(repository);
    }

    public Page<LeaveType> getAllLeaveTypes(PageFilterInput<LeaveType> input) {
        try {
            Page<LeaveType> leaveTypePage = super.findAll(input);
            List<LeaveType> leaveTypes = new ArrayList<>(leaveTypePage.getContent());
            return new PageImpl<>(leaveTypes, leaveTypePage.getPageable(), leaveTypePage.getTotalElements());
        } catch (Exception e) {
            log.error("Error retrieving leave types", e);
            throw new AppException(ErrorCode.UNCATEGORIZED, "Failed to retrieve leave types: " + e.getMessage());
        }
    }

    public List<LeaveType> getListLeaveTypes() {
        return repository.findAll();
    }

    public LeaveType createLeaveType(LeaveTypeDTO leaveTypeDTO) {
        LeaveType leaveType = new LeaveType();
        updateLeaveTypeFromDTO(leaveType, leaveTypeDTO);
        return repository.save(leaveType);
    }

    public LeaveType updateLeaveType(Long id, LeaveTypeDTO leaveTypeDTO) {
        LeaveType leaveType = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "Leave type not found"));
        updateLeaveTypeFromDTO(leaveType, leaveTypeDTO);
        return repository.save(leaveType);
    }

    public void deleteLeaveType(Long id) {
        LeaveType leaveType = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "Leave type not found"));
        repository.delete(leaveType);
    }

    private void updateLeaveTypeFromDTO(LeaveType leaveType, LeaveTypeDTO dto) {
        leaveType.setLeaveTypeName(dto.getLeaveTypeName());
        leaveType.setDescription(dto.getDescription());
        leaveType.setIsPaid(dto.getIsPaid());
        leaveType.setMaxDaysPerYear(dto.getMaxDaysPerYear());
    }
}
