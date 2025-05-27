package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.Entity.AttendanceAdjustmentRequest;
import com.devcuong.smart_hr.Entity.AttendanceRecord;
import com.devcuong.smart_hr.dto.AttendanceAdjustmentRequestDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.enums.ApprovalStatus;
import com.devcuong.smart_hr.exception.AppException;
import com.devcuong.smart_hr.exception.ErrorCode;
import com.devcuong.smart_hr.repository.AttendanceAdjustmentRequestRepository;
import com.devcuong.smart_hr.repository.AttendanceRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AttendanceAdjustmentRequestService extends SearchService<AttendanceAdjustmentRequest> {

    @Autowired
    AttendanceAdjustmentRequestRepository repository;

    @Autowired
    AttendanceRecordRepository attendanceRecordRepository;

    @Autowired
    AttendanceRecordService attendanceRecordService;

    public AttendanceAdjustmentRequestService(AttendanceAdjustmentRequestRepository repository) {
        super(repository);
    }

    public Page<AttendanceAdjustmentRequest> getAllAdjustments(PageFilterInput<AttendanceAdjustmentRequest> input) {
        try {
            Page<AttendanceAdjustmentRequest> adjustmentPage = super.findAll(input);
            List<AttendanceAdjustmentRequest> adjustments = new ArrayList<>(adjustmentPage.getContent());
            return new PageImpl<>(adjustments, adjustmentPage.getPageable(), adjustmentPage.getTotalElements());
        } catch (Exception e) {
            log.error("Error retrieving adjustment requests", e);
            throw new AppException(ErrorCode.UNCATEGORIZED, "Failed to retrieve adjustment requests: " + e.getMessage());
        }
    }

    public List<AttendanceAdjustmentRequest> getListAdjustments() {
        return repository.findAll();
    }

    public AttendanceAdjustmentRequest createAdjustment(AttendanceAdjustmentRequestDTO adjustmentDTO) {
        AttendanceAdjustmentRequest adjustment = new AttendanceAdjustmentRequest();
        updateAdjustmentFromDTO(adjustment, adjustmentDTO);
        adjustment.setCreatedAt(LocalDateTime.now());
        adjustment.setUpdatedAt(LocalDateTime.now());
        return repository.save(adjustment);
    }

    public AttendanceAdjustmentRequest updateAdjustment(Long id, AttendanceAdjustmentRequestDTO adjustmentDTO) {
        AttendanceAdjustmentRequest adjustment = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "Adjustment request not found"));
        updateAdjustmentFromDTO(adjustment, adjustmentDTO);
        adjustment.setUpdatedAt(LocalDateTime.now());
        return repository.save(adjustment);
    }

    public void deleteAdjustment(Long id) {
        AttendanceAdjustmentRequest adjustment = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "Adjustment request not found"));
        repository.delete(adjustment);
    }

    private void updateAdjustmentFromDTO(AttendanceAdjustmentRequest adjustment, AttendanceAdjustmentRequestDTO dto) {
        adjustment.setEmployeeCode(dto.getEmployeeCode());
        adjustment.setWorkDate(dto.getWorkDate());
        adjustment.setOriginalCheckIn(dto.getOriginalCheckIn());
        adjustment.setOriginalCheckOut(dto.getOriginalCheckOut());
        adjustment.setAdjustedCheckIn(dto.getAdjustedCheckIn());
        adjustment.setAdjustedCheckOut(dto.getAdjustedCheckOut());
        adjustment.setReason(dto.getReason());
        adjustment.setStatus(dto.getStatus());
        adjustment.setApprovedBy(dto.getApprovedBy());
        adjustment.setApprovedAt(dto.getApprovedAt());
    }

    public void approvedAdjustment(Long id, String approvedBy) {
        AttendanceAdjustmentRequest adjustment = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "Adjustment request not found"));
        adjustment.setStatus(ApprovalStatus.PHEDUYET);
        adjustment.setApprovedBy(approvedBy);
        adjustment.setApprovedAt(LocalDateTime.now());
        AttendanceRecord attendanceRecord = attendanceRecordRepository.findByEmployeeCodeAndWorkDate(adjustment.getEmployeeCode(), adjustment.getWorkDate());
        if (attendanceRecord != null) {
            attendanceRecord.setCheckInTime(adjustment.getAdjustedCheckIn());
            attendanceRecord.setCheckOutTime(adjustment.getAdjustedCheckOut());
            AttendanceRecord updatedAttendanceRecord = attendanceRecordService.updateAttendanceRecord(attendanceRecord.getId(), attendanceRecord);
        } else {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Attendance record not found");
        }
        repository.save(adjustment);
    }

    public void rejectedAdjustment(Long id, String approvedBy) {
        AttendanceAdjustmentRequest adjustment = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "Adjustment request not found"));
        adjustment.setStatus(ApprovalStatus.TUCHOI);
        adjustment.setApprovedBy(approvedBy);
        adjustment.setApprovedAt(LocalDateTime.now());
        repository.save(adjustment);
    }

//    private Boolean checkApprovalTime(LocalDateTime workDate) {
//        LocalDate now = LocalDate.now();
//        return workDate.isAfter(now);
//    }
}
