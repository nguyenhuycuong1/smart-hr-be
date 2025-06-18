package com.devcuong.smart_hr.controller;

import com.devcuong.smart_hr.Entity.Employee;
import com.devcuong.smart_hr.dto.BankInfoDTO;
import com.devcuong.smart_hr.dto.EmployeeDTO;
import com.devcuong.smart_hr.dto.EmployeeRecordDTO;
import com.devcuong.smart_hr.dto.EmployeeWithBankInfoDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.dto.response.ApiResponse;
import com.devcuong.smart_hr.dto.response.PageResponse;
import com.devcuong.smart_hr.exception.AppException;
import com.devcuong.smart_hr.exception.ErrorCode;
import com.devcuong.smart_hr.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/create")
    public ApiResponse createNewEmployee(@RequestBody EmployeeDTO employeeDTO) {
        return ApiResponse.builder().data(employeeService.createEmployee(employeeDTO)).build().success();
    }

    @PostMapping("/search")
    public PageResponse getEmployees(@RequestBody PageFilterInput<Employee> input) {
        Page<EmployeeRecordDTO> listEmployees = employeeService.getAllEmployees(input);
        return PageResponse.<List<EmployeeRecordDTO>>builder().data(listEmployees.getContent()).dataCount(listEmployees.getTotalElements()).build().success();
    }

    @GetMapping("/{employeeCode}")
    public ApiResponse getEmployeeProfile(@PathVariable String employeeCode) {
        return ApiResponse.builder().data(employeeService.getEmployee(employeeCode)).build().success();
    }

    @PostMapping("/create-or-update")
    public ApiResponse createOrUpdateEmp(@RequestBody EmployeeDTO employeeInfo) {
        Employee employee = employeeService.createOrUpdateEmployeeWithEmpInfo(employeeInfo);
        return ApiResponse.builder().data(employee).build().success();
    }

    @DeleteMapping("/{employeeCode}")
    public ApiResponse deleteEmployee(@PathVariable String employeeCode) {
        employeeService.deleteEmployee(employeeCode);
        return ApiResponse.builder().build().success();
    }

    @PostMapping("/create-with-bank-info")
    public ApiResponse createWithBankInfo(@RequestBody EmployeeWithBankInfoDTO employeeWithBankInfoDTO) {
        return ApiResponse.builder().data(employeeService.createEmployeeAndBankInfo(employeeWithBankInfoDTO.getEmployee(), employeeWithBankInfoDTO.getBankInfo())).build().success();

    }

    @PostMapping("/export-excel")
    public ResponseEntity<ByteArrayResource> exportExcel(@RequestBody PageFilterInput<Employee> input) {
        try {
            ByteArrayInputStream excelData = employeeService.exportEmployeeData(input);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=employees.xls");
            headers.add("Content-Type", "application/vnd.ms-excel");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(new ByteArrayResource(excelData.readAllBytes()));
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Failed to export employee data: " + e.getMessage());
        }
    }
}
