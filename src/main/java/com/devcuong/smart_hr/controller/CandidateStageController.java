package com.devcuong.smart_hr.controller;

import com.devcuong.smart_hr.Entity.CandidateStage;
import com.devcuong.smart_hr.dto.CandidateStageDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.dto.response.ApiResponse;
import com.devcuong.smart_hr.dto.response.PageResponse;
import com.devcuong.smart_hr.enums.CandidateStatus;
import com.devcuong.smart_hr.service.CandidateStageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Path;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/candidate-stages")
public class CandidateStageController {
    @Autowired
    private CandidateStageService candidateStageService;

    @PostMapping("/search")
    public PageResponse search(@RequestBody PageFilterInput<CandidateStage> input) {
        Page<Map<String, Object>> page = candidateStageService.search(input);
        return PageResponse.builder().data(page.getContent()).dataCount(page.getTotalElements()).build().success();
    }

    @PostMapping("/create")
    public ApiResponse create(@RequestBody CandidateStageDTO candidateStage) {
        return ApiResponse.builder().data(candidateStageService.createCandidateStage(candidateStage)).build().success();
    }

    @PutMapping("/update")
    public ApiResponse update(@RequestBody CandidateStageDTO candidateStage) {
        return ApiResponse.builder().data(candidateStageService.updateCandidateStage(candidateStage)).build().success();
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse delete(@PathVariable Long id) {
        candidateStageService.deleteCandidateStage(id);
        return ApiResponse.builder().build().success();
    }

    @DeleteMapping("/delete-and-update/{id}/{status}")
    public ApiResponse deleteAndUpdate(@PathVariable Long id, @PathVariable CandidateStatus status) {
        candidateStageService.deleteAndUpdateStatusCandidate(id, status);
        return ApiResponse.builder().build().success();
    }
}
