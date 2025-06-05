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

import java.util.Map;

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
        Page<Map<String, Object>> listLeaveRequests = leaveRequestService.getAllLeaveRequests(input);
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

    @PostMapping("/approve/{id}/{approvedBy}")
    public ApiResponse approveLeaveRequest(@PathVariable Long id, @PathVariable String approvedBy) {
        return ApiResponse.builder()
                .data(leaveRequestService.approvalLeaveRequest(id, approvedBy))
                .build()
                .success();
    }

    @PostMapping("/reject/{id}/{approvedBy}")
    public ApiResponse rejectLeaveRequest(@PathVariable Long id, @PathVariable String approvedBy) {
        return ApiResponse.builder()
                .data(leaveRequestService.rejectLeaveRequest(id, approvedBy))
                .build()
                .success();
    }

    @GetMapping("/{employeeCode}/leave-balance")
    public ApiResponse getLeaveBalance(@PathVariable String employeeCode) {
        return ApiResponse.builder()
                .data(leaveRequestService.calculateLeaveBalance(employeeCode))
                .build()
                .success();
    }
}
