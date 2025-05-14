package com.devcuong.smart_hr.controller;

import com.devcuong.smart_hr.Entity.WorkSchedule;
import com.devcuong.smart_hr.dto.WorkScheduleDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.dto.response.ApiResponse;
import com.devcuong.smart_hr.dto.response.PageResponse;
import com.devcuong.smart_hr.service.WorkScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/work-schedules")
public class WorkScheduleController {

    @Autowired
    private WorkScheduleService workScheduleService;

    @GetMapping
    public ApiResponse getListWorkSchedule() {
        return ApiResponse.builder()
                .data(workScheduleService.getListWorkSchedule())
                .build()
                .success();
    }

    @PostMapping("/search")
    public PageResponse getAllWorkSchedules(@RequestBody PageFilterInput<WorkSchedule> input) {
        Page<WorkSchedule> listWorkSchedules = workScheduleService.getAllWorkSchedules(input);
        return PageResponse.builder()
                .data(listWorkSchedules.getContent())
                .dataCount(listWorkSchedules.getTotalElements())
                .build()
                .success();
    }

    @PostMapping("/create")
    public ApiResponse createWorkSchedule(@RequestBody WorkScheduleDTO workSchedule) {
        return ApiResponse.builder()
                .data(workScheduleService.createWorkSchedule(workSchedule))
                .build()
                .success();
    }

    @PutMapping("/update/{id}")
    public ApiResponse updateWorkSchedule(@PathVariable Integer id, @RequestBody WorkScheduleDTO workSchedule) {
        return ApiResponse.builder()
                .data(workScheduleService.updateWorkSchedule(id, workSchedule))
                .build()
                .success();
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse deleteWorkSchedule(@PathVariable Integer id) {
        workScheduleService.deleteWorkSchedule(id);
        return ApiResponse.builder().build().success();
    }
}
