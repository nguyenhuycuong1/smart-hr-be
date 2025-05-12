package com.devcuong.smart_hr.controller;

import com.devcuong.smart_hr.Entity.Candidate;
import com.devcuong.smart_hr.dto.CandidateDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.dto.response.ApiResponse;
import com.devcuong.smart_hr.dto.response.PageResponse;
import com.devcuong.smart_hr.service.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {
    @Autowired
    private CandidateService candidateService;

    @PostMapping("/search")
    public PageResponse findAllCandidates(@RequestBody PageFilterInput<Candidate> input) {
        Page<Map<String, Object>> page = candidateService.search(input);
        return PageResponse.builder().data(page.getContent()).dataCount(page.getTotalElements()).build().success();
    }

    @GetMapping("/{candidateCode}")
    public ApiResponse findCandidateByCandidateCode(@PathVariable("candidateCode") String candidateCode) {
        return ApiResponse.builder().data(candidateService.findByCandidateCode(candidateCode)).build().success();
    }

    @PostMapping("/create")
    public ApiResponse createCandidate(@RequestBody CandidateDTO candidate) {
        return ApiResponse.builder().data(candidateService.create(candidate)).build().success();
    }

    @PutMapping("/update")
    public ApiResponse updateCandidate(@RequestBody CandidateDTO candidate) {
        return ApiResponse.builder().data(candidateService.update(candidate)).build().success();
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse deleteCandidate(@PathVariable Long id) {
        candidateService.deleteById(id);
        return ApiResponse.builder().build().success();
    }
}
