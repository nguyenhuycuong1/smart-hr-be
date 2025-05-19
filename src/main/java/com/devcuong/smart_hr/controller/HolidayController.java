package com.devcuong.smart_hr.controller;

import com.devcuong.smart_hr.Entity.Holiday;
import com.devcuong.smart_hr.dto.HolidayDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.dto.response.ApiResponse;
import com.devcuong.smart_hr.dto.response.PageResponse;
import com.devcuong.smart_hr.service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/holidays")
public class HolidayController {

    @Autowired
    private HolidayService holidayService;

    @GetMapping
    public ApiResponse getListHolidays() {
        return ApiResponse.builder()
                .data(holidayService.getListHolidays())
                .build()
                .success();
    }

    @PostMapping("/search")
    public PageResponse getAllHolidays(@RequestBody PageFilterInput<Holiday> input) {
        Page<Holiday> listHolidays = holidayService.getAllHolidays(input);
        return PageResponse.builder()
                .data(listHolidays.getContent())
                .dataCount(listHolidays.getTotalElements())
                .build()
                .success();
    }

    @PostMapping("/create")
    public ApiResponse createHoliday(@RequestBody HolidayDTO holiday) {
        return ApiResponse.builder()
                .data(holidayService.createHoliday(holiday))
                .build()
                .success();
    }

    @PutMapping("/update/{id}")
    public ApiResponse updateHoliday(@PathVariable Long id, @RequestBody HolidayDTO holiday) {
        return ApiResponse.builder()
                .data(holidayService.updateHoliday(id, holiday))
                .build()
                .success();
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse deleteHoliday(@PathVariable Long id) {
        holidayService.deleteHoliday(id);
        return ApiResponse.builder().build().success();
    }
}
