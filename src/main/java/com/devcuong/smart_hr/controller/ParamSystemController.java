package com.devcuong.smart_hr.controller;

import com.devcuong.smart_hr.Entity.ParamSystem;
import com.devcuong.smart_hr.dto.ParamSystemDTO;
import com.devcuong.smart_hr.dto.response.ApiResponse;
import com.devcuong.smart_hr.service.ParamSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/params")
public class ParamSystemController {

    @Autowired
    private ParamSystemService paramSystemService;

    @GetMapping("/{tableName}/{columnName}")
    public ApiResponse getParam(@PathVariable String tableName, @PathVariable String columnName) {
        return ApiResponse.builder().data(paramSystemService.getParams(tableName, columnName)).build().success();
    }

    @PostMapping
    public ApiResponse addParam(@RequestBody ParamSystemDTO paramSystem) {
        return ApiResponse.builder().data(paramSystemService.addParams(paramSystem)).build().success();
    }

    @DeleteMapping("/{id}")
    public void deleteParam(@PathVariable Integer id) {
        paramSystemService.deleteParams(id);
    }

    @PostMapping("/create/batch")
    public ApiResponse createBatch(@RequestBody List<ParamSystemDTO> params) {
        return ApiResponse.builder().data(paramSystemService.createBatchParam(params)).build().success();
    }
}
