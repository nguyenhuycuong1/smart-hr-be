package com.devcuong.smart_hr.controller;

import com.devcuong.smart_hr.dto.BankInfoDTO;
import com.devcuong.smart_hr.dto.response.ApiResponse;
import com.devcuong.smart_hr.service.BankInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bank-info")
public class BankInfoController {

    @Autowired
    BankInfoService bankInfoService;

    @PutMapping("{id}")
    ApiResponse updateBankInfo(@PathVariable Integer id, @RequestBody BankInfoDTO bankInfoDTO) {
        bankInfoService.updateBankInfo(id, bankInfoDTO);
        return ApiResponse.builder().build().success();
    }

    @PostMapping("/create-or-update")
    ApiResponse createOrUpdate(@RequestBody BankInfoDTO bankInfoDTO) {
        bankInfoService.createOrUpdateBankInfo(bankInfoDTO);
        return ApiResponse.builder().build().success();
    }


}
