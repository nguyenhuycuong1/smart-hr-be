package com.devcuong.smart_hr.controller;

import com.devcuong.smart_hr.Entity.LeaveType;
import com.devcuong.smart_hr.dto.LeaveTypeDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.dto.response.ApiResponse;
import com.devcuong.smart_hr.dto.response.PageResponse;
import com.devcuong.smart_hr.service.LeaveTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave-types")
public class LeaveTypeController {

    @Autowired
    private LeaveTypeService leaveTypeService;

    @GetMapping
    public ApiResponse getListLeaveTypes() {
        return ApiResponse.builder()
                .data(leaveTypeService.getListLeaveTypes())
                .build()
                .success();
    }

    @PostMapping("/search")
    public PageResponse getAllLeaveTypes(@RequestBody PageFilterInput<LeaveType> input) {
        Page<LeaveType> listLeaveTypes = leaveTypeService.getAllLeaveTypes(input);
        return PageResponse.builder()
                .data(listLeaveTypes.getContent())
                .dataCount(listLeaveTypes.getTotalElements())
                .build()
                .success();
    }

    @PostMapping("/create")
    public ApiResponse createLeaveType(@RequestBody LeaveTypeDTO leaveType) {
        return ApiResponse.builder()
                .data(leaveTypeService.createLeaveType(leaveType))
                .build()
                .success();
    }

    @PutMapping("/update/{id}")
    public ApiResponse updateLeaveType(@PathVariable Long id, @RequestBody LeaveTypeDTO leaveType) {
        return ApiResponse.builder()
                .data(leaveTypeService.updateLeaveType(id, leaveType))
                .build()
                .success();
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse deleteLeaveType(@PathVariable Long id) {
        leaveTypeService.deleteLeaveType(id);
        return ApiResponse.builder().build().success();
    }
}
