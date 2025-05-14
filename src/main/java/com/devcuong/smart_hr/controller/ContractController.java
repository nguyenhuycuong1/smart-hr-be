package com.devcuong.smart_hr.controller;

import com.devcuong.smart_hr.Entity.Contract;
import com.devcuong.smart_hr.dto.ContractDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.dto.response.ApiResponse;
import com.devcuong.smart_hr.dto.response.PageResponse;
import com.devcuong.smart_hr.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {

    @Autowired
    private ContractService contractService;

    @PostMapping("/search")
    public PageResponse getAllContracts(@RequestBody PageFilterInput<Contract> input) {
        Page<Map<String, Object>> listContracts = contractService.getAllContracts(input);
        return PageResponse.builder().data(listContracts.getContent()).dataCount(listContracts.getTotalElements()).build().success();
    }

    @PostMapping("/create")
    public ApiResponse createContract(@RequestBody ContractDTO contract) {
        return ApiResponse.builder().data(contractService.createContract(contract)).build().success();
    }

    @PutMapping("/update")
    public ApiResponse updateContract(@RequestBody ContractDTO contract) {
        return ApiResponse.builder().data(contractService.updateContract(contract)).build().success();
    }

    @DeleteMapping("/{contractCode}")
    public ApiResponse deleteContract(@PathVariable String contractCode) {
        contractService.deleteContract(contractCode);
        return ApiResponse.builder().build().success();
    }
}
