package com.devcuong.smart_hr.controller;

import com.devcuong.smart_hr.Entity.InterviewSession;
import com.devcuong.smart_hr.dto.InterviewScheduleDTO;
import com.devcuong.smart_hr.dto.response.ApiResponse;
import com.devcuong.smart_hr.repository.InterviewSessionRepository;
import com.devcuong.smart_hr.service.InterviewSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/interview-sessions")
public class InterviewSessionController {
    @Autowired
    private InterviewSessionService interviewSessionService;

    @PostMapping("create")
    public ApiResponse createInterviewSession(@RequestBody InterviewScheduleDTO interviewScheduleDTO) {
        interviewSessionService.createInterviewSession(interviewScheduleDTO);
        return ApiResponse.builder().build().success();
    }

    @GetMapping()
    public ApiResponse getAllInterviewSessions() {
        return ApiResponse.builder().data(interviewSessionService.getInterviewSessions()).build().success();
    }

    @GetMapping("/by-week")
    public ApiResponse getInterviewSessionsByWeek(
            @RequestParam("timestamp") Long timestamp,
            @RequestParam(value = "weekOffset", defaultValue = "0") int weekOffset
    ) {
        List<Map<String, Object>> sessions = interviewSessionService
                .getInterviewSessionsByWeek(timestamp, weekOffset);
        return ApiResponse.builder()
                .data(sessions)
                .build()
                .success();
    }

    @PutMapping("/update")
    public ApiResponse updateInterviewSession(@RequestBody InterviewScheduleDTO interviewScheduleDTO) {
        interviewSessionService.updateInterviewSession(interviewScheduleDTO);
        return ApiResponse.builder().build().success();
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse deleteInterviewSession(@PathVariable Long id) {
        interviewSessionService.deleteInterviewSession(id);
        return ApiResponse.builder().build().success();
    }
}
