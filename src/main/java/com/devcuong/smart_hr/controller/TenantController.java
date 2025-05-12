package com.devcuong.smart_hr.controller;

import com.devcuong.smart_hr.Entity.Tenant;
import com.devcuong.smart_hr.dto.response.ApiResponse;
import com.devcuong.smart_hr.dto.response.Result;
import com.devcuong.smart_hr.service.TenantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/tenant")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @GetMapping
    public ApiResponse<Tenant> getInfoBusiness() {
        return ApiResponse.<Tenant>builder()
                .data(tenantService.getTenant())
                .result(Result.builder()
                        .message("Thành công")
                        .responseCode(200)
                        .success(true)
                        .build())
                .build();
    }
}
