package com.devcuong.smart_hr.controller;

import com.devcuong.smart_hr.Entity.AttendanceRecord;
import com.devcuong.smart_hr.dto.AttendanceRecordDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.dto.response.ApiResponse;
import com.devcuong.smart_hr.dto.response.PageResponse;
import com.devcuong.smart_hr.service.AttendanceRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance-records")
public class AttendanceRecordController {

    @Autowired
    private AttendanceRecordService attendanceRecordService;

    @GetMapping
    public ApiResponse getListAttendanceRecord() {
        return ApiResponse.builder()
                .data(attendanceRecordService.getListAttendanceRecord())
                .build()
                .success();
    }

    @PostMapping("/search")
    public PageResponse getAllAttendanceRecords(@RequestBody PageFilterInput<AttendanceRecord> input) {
        Page<AttendanceRecord> listAttendanceRecords = attendanceRecordService.getAllAttendanceRecords(input);
        return PageResponse.builder()
                .data(listAttendanceRecords.getContent())
                .dataCount(listAttendanceRecords.getTotalElements())
                .build()
                .success();
    }

    @PostMapping("/create")
    public ApiResponse createAttendanceRecord(@RequestBody AttendanceRecordDTO attendanceRecord) {
        return ApiResponse.builder()
                .data(attendanceRecordService.createAttendanceRecord(attendanceRecord))
                .build()
                .success();
    }

    @PutMapping("/update/{id}")
    public ApiResponse updateAttendanceRecord(@PathVariable Long id, @RequestBody AttendanceRecordDTO attendanceRecord) {
        return ApiResponse.builder()
                .data(attendanceRecordService.updateAttendanceRecord(id, attendanceRecord))
                .build()
                .success();
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse deleteAttendanceRecord(@PathVariable Long id) {
        attendanceRecordService.deleteAttendanceRecord(id);
        return ApiResponse.builder().build().success();
    }

    @PostMapping("/check-in/{employeeCode}")
    public ApiResponse checkIn(@PathVariable String employeeCode) {
        return ApiResponse.builder()
                .data(attendanceRecordService.checkIn(employeeCode))
                .build()
                .success();
    }

    @PostMapping("/check-out/{employeeCode}/{attendanceRecordId}")
    public ApiResponse checkOut(@PathVariable String employeeCode, @PathVariable Long attendanceRecordId) {
        return ApiResponse.builder()
                .data(attendanceRecordService.checkOut(attendanceRecordId,employeeCode))
                .build()
                .success();
    }

    @GetMapping("/monthly-summary/{employeeCode}")
    public ApiResponse getMonthlySummary(@PathVariable String employeeCode) {
        return ApiResponse.builder()
                .data(attendanceRecordService.getCurrentMonthAttendanceSummary(employeeCode))
                .build()
                .success();
    }
}
