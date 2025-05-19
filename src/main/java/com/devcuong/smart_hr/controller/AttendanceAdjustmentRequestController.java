package com.devcuong.smart_hr.controller;

import com.devcuong.smart_hr.Entity.AttendanceAdjustmentRequest;
import com.devcuong.smart_hr.dto.AttendanceAdjustmentRequestDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.dto.response.ApiResponse;
import com.devcuong.smart_hr.dto.response.PageResponse;
import com.devcuong.smart_hr.service.AttendanceAdjustmentRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance-adjustments")
public class AttendanceAdjustmentRequestController {

    @Autowired
    private AttendanceAdjustmentRequestService adjustmentService;

    @GetMapping
    public ApiResponse getListAdjustments() {
        return ApiResponse.builder()
                .data(adjustmentService.getListAdjustments())
                .build()
                .success();
    }

    @PostMapping("/search")
    public PageResponse getAllAdjustments(@RequestBody PageFilterInput<AttendanceAdjustmentRequest> input) {
        Page<AttendanceAdjustmentRequest> listAdjustments = adjustmentService.getAllAdjustments(input);
        return PageResponse.builder()
                .data(listAdjustments.getContent())
                .dataCount(listAdjustments.getTotalElements())
                .build()
                .success();
    }

    @PostMapping("/create")
    public ApiResponse createAdjustment(@RequestBody AttendanceAdjustmentRequestDTO adjustment) {
        return ApiResponse.builder()
                .data(adjustmentService.createAdjustment(adjustment))
                .build()
                .success();
    }

    @PutMapping("/update/{id}")
    public ApiResponse updateAdjustment(@PathVariable Long id, @RequestBody AttendanceAdjustmentRequestDTO adjustment) {
        return ApiResponse.builder()
                .data(adjustmentService.updateAdjustment(id, adjustment))
                .build()
                .success();
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse deleteAdjustment(@PathVariable Long id) {
        adjustmentService.deleteAdjustment(id);
        return ApiResponse.builder().build().success();
    }
}
