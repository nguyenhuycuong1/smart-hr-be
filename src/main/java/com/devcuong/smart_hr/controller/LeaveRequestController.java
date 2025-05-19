package com.devcuong.smart_hr.controller;

import com.devcuong.smart_hr.Entity.LeaveRequest;
import com.devcuong.smart_hr.dto.LeaveRequestDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.dto.response.ApiResponse;
import com.devcuong.smart_hr.dto.response.PageResponse;
import com.devcuong.smart_hr.service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave-requests")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @GetMapping
    public ApiResponse getListLeaveRequests() {
        return ApiResponse.builder()
                .data(leaveRequestService.getListLeaveRequests())
                .build()
                .success();
    }

    @PostMapping("/search")
    public PageResponse getAllLeaveRequests(@RequestBody PageFilterInput<LeaveRequest> input) {
        Page<LeaveRequest> listLeaveRequests = leaveRequestService.getAllLeaveRequests(input);
        return PageResponse.builder()
                .data(listLeaveRequests.getContent())
                .dataCount(listLeaveRequests.getTotalElements())
                .build()
                .success();
    }

    @PostMapping("/create")
    public ApiResponse createLeaveRequest(@RequestBody LeaveRequestDTO leaveRequest) {
        return ApiResponse.builder()
                .data(leaveRequestService.createLeaveRequest(leaveRequest))
                .build()
                .success();
    }

    @PutMapping("/update/{id}")
    public ApiResponse updateLeaveRequest(@PathVariable Long id, @RequestBody LeaveRequestDTO leaveRequest) {
        return ApiResponse.builder()
                .data(leaveRequestService.updateLeaveRequest(id, leaveRequest))
                .build()
                .success();
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse deleteLeaveRequest(@PathVariable Long id) {
        leaveRequestService.deleteLeaveRequest(id);
        return ApiResponse.builder().build().success();
    }
}
