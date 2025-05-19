package com.devcuong.smart_hr.controller;

import com.devcuong.smart_hr.Entity.OvertimeRequest;
import com.devcuong.smart_hr.dto.OvertimeRequestDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.dto.response.ApiResponse;
import com.devcuong.smart_hr.dto.response.PageResponse;
import com.devcuong.smart_hr.service.OvertimeRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/overtime-requests")
public class OvertimeRequestController {

    @Autowired
    private OvertimeRequestService overtimeRequestService;

    @GetMapping
    public ApiResponse getListOvertimeRequests() {
        return ApiResponse.builder()
                .data(overtimeRequestService.getListOvertimeRequests())
                .build()
                .success();
    }

    @PostMapping("/search")
    public PageResponse getAllOvertimeRequests(@RequestBody PageFilterInput<OvertimeRequest> input) {
        Page<OvertimeRequest> listOvertimeRequests = overtimeRequestService.getAllOvertimeRequests(input);
        return PageResponse.builder()
                .data(listOvertimeRequests.getContent())
                .dataCount(listOvertimeRequests.getTotalElements())
                .build()
                .success();
    }

    @PostMapping("/create")
    public ApiResponse createOvertimeRequest(@RequestBody OvertimeRequestDTO overtimeRequest) {
        return ApiResponse.builder()
                .data(overtimeRequestService.createOvertimeRequest(overtimeRequest))
                .build()
                .success();
    }

    @PutMapping("/update/{id}")
    public ApiResponse updateOvertimeRequest(@PathVariable Long id, @RequestBody OvertimeRequestDTO overtimeRequest) {
        return ApiResponse.builder()
                .data(overtimeRequestService.updateOvertimeRequest(id, overtimeRequest))
                .build()
                .success();
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse deleteOvertimeRequest(@PathVariable Long id) {
        overtimeRequestService.deleteOvertimeRequest(id);
        return ApiResponse.builder().build().success();
    }
}
