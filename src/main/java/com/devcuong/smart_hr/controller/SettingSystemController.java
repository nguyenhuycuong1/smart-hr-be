package com.devcuong.smart_hr.controller;

import com.devcuong.smart_hr.dto.SettingSystemDTO;
import com.devcuong.smart_hr.dto.response.ApiResponse;
import com.devcuong.smart_hr.service.SettingSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/setting-system")
public class SettingSystemController {
    @Autowired
    private SettingSystemService settingSystemService;

    @GetMapping
    public ApiResponse getSettingSystem() {
        return ApiResponse.builder().data(settingSystemService.getSettingSystem()).build().success();
    }

    @PutMapping
    public ApiResponse updateSettingSystem(@RequestBody SettingSystemDTO settingSystemDTO) {
        return ApiResponse.builder().data(settingSystemService.saveSettingSystem(settingSystemDTO)).build().success();
    }

    @PutMapping("/update-weekday")
    public ApiResponse updateWeekDay(@RequestBody SettingSystemDTO settingSystemDTO) {
        return ApiResponse.builder().data(settingSystemService.saveWeekday(settingSystemDTO)).build().success();
    }

    @PutMapping("/update-threshold")
    public ApiResponse updateThreshold(@RequestBody SettingSystemDTO settingSystemDTO) {
        return ApiResponse.builder().data(settingSystemService.saveThreshold(settingSystemDTO)).build().success();
    }
}
