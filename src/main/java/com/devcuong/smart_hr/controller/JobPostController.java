package com.devcuong.smart_hr.controller;

import com.devcuong.smart_hr.Entity.JobPost;
import com.devcuong.smart_hr.dto.JobPostDTO;
import com.devcuong.smart_hr.dto.JobPostRecordDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.dto.response.ApiResponse;
import com.devcuong.smart_hr.dto.response.PageResponse;
import com.devcuong.smart_hr.service.JobPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/job-posts")
public class JobPostController {

    @Autowired
    private JobPostService jobPostService;

    @PostMapping("/create")
    public ApiResponse createJobPost(@RequestBody JobPostDTO jobPost) {
        return ApiResponse.builder().data(jobPostService.createJobPost(jobPost)).build().success();
    }

    @PostMapping("/search")
    public PageResponse searchJobPost(@RequestBody PageFilterInput<JobPost> input) {
        Page<JobPostRecordDTO> jobPosts = jobPostService.searchJobPost(input);
        return PageResponse.builder().data(jobPosts.getContent()).dataCount(jobPosts.getTotalElements()).build().success();
    }

    @GetMapping("/get-by-request-code/{requestCode}")
    public ApiResponse getJobPostByRequestCode(@PathVariable String requestCode) {
        return ApiResponse.builder().data(jobPostService.getJobPostByRequestCode(requestCode)).build();
    }

    @GetMapping("/get-by-job-post-code/{jobPostCode}")
    public ApiResponse getJobPostByJobPostCode(@PathVariable String jobPostCode) {
        return ApiResponse.builder().data(jobPostService.getJobPostByJobPostCode(jobPostCode)).build();
    }

    @PutMapping("/update")
    public ApiResponse updateJobPost(@RequestBody JobPostDTO jobPost) {
        return ApiResponse.builder().data(jobPostService.updateJobPost(jobPost)).build().success();
    }

    @DeleteMapping("{id}")
    public ApiResponse deleteJobPost(@PathVariable Long id) {
        jobPostService.deleteJobPost(id);
        return ApiResponse.builder().build().success();
    }

}
