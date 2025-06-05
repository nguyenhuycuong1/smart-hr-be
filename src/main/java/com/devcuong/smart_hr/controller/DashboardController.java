package com.devcuong.smart_hr.controller;

import com.devcuong.smart_hr.dto.response.ApiResponse;
import com.devcuong.smart_hr.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/personnel/{startDate}/{endDate}")
    public ApiResponse getPersonnelDashboard(@PathVariable LocalDate startDate, @PathVariable LocalDate endDate) {
        return ApiResponse.builder()
                .data(dashboardService.getDashboardPersonnelScreen(startDate,endDate))
                .build()
                .success();
    }

    @GetMapping("/attendance-and-leave/{startDate}/{endDate}")
    public ApiResponse getAttendanceAndLeavesDashboard(@PathVariable LocalDate startDate, @PathVariable LocalDate endDate) {
        return ApiResponse.builder()
                .data(dashboardService.getDashboardAttendanceAndLeaveScreen(startDate, endDate))
                .build()
                .success();
    }


}
