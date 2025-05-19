package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.Entity.AttendanceRecord;
import com.devcuong.smart_hr.dto.AttendanceRecordDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.exception.AppException;
import com.devcuong.smart_hr.exception.ErrorCode;
import com.devcuong.smart_hr.repository.AttendanceRecordRepository;
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
public class AttendanceRecordService extends SearchService<AttendanceRecord> {

    @Autowired
    AttendanceRecordRepository repository;

    public AttendanceRecordService(AttendanceRecordRepository repository) {
        super(repository);
    }

    public Page<AttendanceRecord> getAllAttendanceRecords(PageFilterInput<AttendanceRecord> input) {
        try {
            Page<AttendanceRecord> recordPage = super.findAll(input);
            List<AttendanceRecord> records = new ArrayList<>(recordPage.getContent());
            return new PageImpl<>(records, recordPage.getPageable(), recordPage.getTotalElements());
        } catch (Exception e) {
            log.error("Error retrieving attendance records", e);
            throw new AppException(ErrorCode.UNCATEGORIZED, "Failed to retrieve attendance records: " + e.getMessage());
        }
    }

    public List<AttendanceRecord> getListAttendanceRecord() {
        return repository.findAll();
    }

    public AttendanceRecord createAttendanceRecord(AttendanceRecordDTO recordDTO) {
        AttendanceRecord record = new AttendanceRecord();
        updateRecordFromDTO(record, recordDTO);
        return repository.save(record);
    }

    public AttendanceRecord updateAttendanceRecord(Long id, AttendanceRecordDTO recordDTO) {
        AttendanceRecord record = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "Attendance record not found"));
        updateRecordFromDTO(record, recordDTO);
        return repository.save(record);
    }

    public void deleteAttendanceRecord(Long id) {
        AttendanceRecord record = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "Attendance record not found"));
        repository.delete(record);
    }

    private void updateRecordFromDTO(AttendanceRecord record, AttendanceRecordDTO dto) {
        record.setEmployeeCode(dto.getEmployeeCode());
        record.setCheckInTime(dto.getCheckInTime());
        record.setCheckOutTime(dto.getCheckOutTime());
        record.setWorkDate(dto.getWorkDate());
        record.setStatus(dto.getStatus());
        record.setTotalHours(dto.getTotalHours());
        record.setOvertimeHours(dto.getOvertimeHours());
    }
}
