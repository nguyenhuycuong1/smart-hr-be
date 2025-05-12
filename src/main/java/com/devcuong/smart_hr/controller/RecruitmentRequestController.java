package com.devcuong.smart_hr.controller;

import com.devcuong.smart_hr.Entity.RecruitmentRequest;
import com.devcuong.smart_hr.dto.RecruitmentRequestDTO;
import com.devcuong.smart_hr.dto.RecruitmentRequestRecordDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.dto.response.ApiResponse;
import com.devcuong.smart_hr.dto.response.PageResponse;
import com.devcuong.smart_hr.enums.RecruitmentRequestStatus;
import com.devcuong.smart_hr.service.RecruitmentRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recruitment-requests")
public class RecruitmentRequestController {

    @Autowired
    private RecruitmentRequestService recruitmentRequestService;

    @PostMapping("/search")
    public PageResponse search(@RequestBody PageFilterInput<RecruitmentRequest> input) {
        Page<RecruitmentRequestRecordDTO> page = recruitmentRequestService.search(input);
        return PageResponse.builder().data(page.getContent()).dataCount(page.getTotalElements()).build().success();
    }

    @PostMapping("/create")
    public ApiResponse create(@RequestBody RecruitmentRequestDTO recruitmentRequestDTO) {
        return ApiResponse.builder().data(recruitmentRequestService.createRecruitmentRequest(recruitmentRequestDTO)).build().success();
    }

    @PutMapping("/update")
    public ApiResponse update(@RequestBody RecruitmentRequestDTO recruitmentRequestDTO) {
        return ApiResponse.builder().data(recruitmentRequestService.updateRecruitmentRequest(recruitmentRequestDTO)).build();
    }

    @DeleteMapping("/delete/{recruitmentRequestId}")
    public ApiResponse delete(@PathVariable Long recruitmentRequestId) {
        recruitmentRequestService.deleteRecruitmentRequest(recruitmentRequestId);
        return ApiResponse.builder().build().success();
    }

    @PutMapping("/update-status/{username}/{status}")
    public ApiResponse updateStatus(@RequestBody List<RecruitmentRequestDTO> list, @PathVariable RecruitmentRequestStatus status,@PathVariable String username) {
        recruitmentRequestService.updateStatus(list,status,username);
        return ApiResponse.builder().build().success();
    }
}
