package com.devcuong.smart_hr.service;


import com.devcuong.smart_hr.Entity.InterviewCandidate;
import com.devcuong.smart_hr.Entity.InterviewRecruiter;
import com.devcuong.smart_hr.Entity.InterviewSession;
import com.devcuong.smart_hr.dto.InterviewScheduleDTO;
import com.devcuong.smart_hr.exception.AppException;
import com.devcuong.smart_hr.exception.ErrorCode;
import com.devcuong.smart_hr.repository.InterviewCandidateRepository;
import com.devcuong.smart_hr.repository.InterviewRecruiterRepository;
import com.devcuong.smart_hr.repository.InterviewSessionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.WeekFields;
import java.util.*;

@Service
public class InterviewSessionService {
    @Autowired
    private InterviewSessionRepository interviewSessionRepository;
    @Autowired
    private InterviewCandidateRepository interviewCandidateRepository;
    @Autowired
    private InterviewRecruiterRepository interviewRecruiterRepository;

    @Transactional
    public void createInterviewSession(InterviewScheduleDTO interviewScheduleDTO) {
        if(interviewScheduleDTO.getJobPostCode() == null || interviewScheduleDTO.getJobPostCode().isEmpty()) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Mã bài đăng tuyển dụng không được bỏ trống");
        }
        if(interviewScheduleDTO.getStartTime() == null || interviewScheduleDTO.getEndTime() == null) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Thời gian bắt đầu và kết thúc không được bỏ trống");
        }
        if (interviewScheduleDTO.getCandidateCodes() == null || interviewScheduleDTO.getCandidateCodes().isEmpty()) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Mã ứng viên tham gia phỏng vấn không được bỏ trống");
        }
        if (interviewScheduleDTO.getRecruiterCodes() == null || interviewScheduleDTO.getRecruiterCodes().isEmpty()) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Mã nhân viên phụ trách phỏng vấn không được bỏ trống");
        }
        checkValidStartTimeAndEndTime(interviewScheduleDTO.getStartTime(), interviewScheduleDTO.getEndTime());
        InterviewSession interviewSession = new InterviewSession();
        interviewSession.setTitle(interviewScheduleDTO.getTitle());
        interviewSession.setJobPostCode(interviewScheduleDTO.getJobPostCode());
        interviewSession.setDescription(interviewScheduleDTO.getDescription());
        interviewSession.setStartTime(interviewScheduleDTO.getStartTime());
        interviewSession.setEndTime(interviewScheduleDTO.getEndTime());
        interviewSession.setLocation(interviewScheduleDTO.getLocation());
        interviewSession.setMeetingLink(interviewScheduleDTO.getMeetingLink());
        interviewSession.setNote(interviewScheduleDTO.getNote());
        interviewSessionRepository.save(interviewSession);
        List<String> candidateCodes = interviewScheduleDTO.getCandidateCodes();
        candidateCodes.forEach(candidateCode -> {
                this.checkValidTimeOfCandidate(candidateCode,interviewSession.getId(), interviewScheduleDTO.getStartTime(), interviewScheduleDTO.getEndTime());
                InterviewCandidate interviewCandidate = new InterviewCandidate();
                interviewCandidate.setCandidateCode(candidateCode);
                interviewCandidate.setInterviewSessionId(interviewSession.getId());
                interviewCandidateRepository.save(interviewCandidate);
        });
        List<String> recruiterCodes = interviewScheduleDTO.getRecruiterCodes();
        recruiterCodes.forEach(recruiterCode -> {
                this.checkValidTimeOfRecruiter(recruiterCode,interviewSession.getId(), interviewScheduleDTO.getStartTime(), interviewScheduleDTO.getEndTime());
                InterviewRecruiter interviewRecruiter = new InterviewRecruiter();
                interviewRecruiter.setRecruiterCode(recruiterCode);
                interviewRecruiter.setInterviewSessionId(interviewSession.getId());
                interviewRecruiterRepository.save(interviewRecruiter);
        });
    }

    public List<InterviewSession> getInterviewSessions() {
        return interviewSessionRepository.findAll();
    }

    public InterviewSession getInterviewSession(Long id) {
        return interviewSessionRepository.findById(id).orElse(null);
    }

    public List<Map<String, Object>> getInterviewSessionsByWeek(Long timestamp, int weekOffset) {
        OffsetDateTime dateTime = OffsetDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp),
                ZoneOffset.UTC
        );

        if (weekOffset != 0) {
            dateTime = dateTime.plusWeeks(weekOffset);
        }

        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        OffsetDateTime startOfWeek = dateTime
                .with(weekFields.dayOfWeek(), 1)  // Monday
                .withHour(0)
                .withMinute(0)
                .withSecond(0);

        OffsetDateTime endOfWeek = dateTime
                .with(weekFields.dayOfWeek(), 7)  // Sunday
                .withHour(23)
                .withMinute(59)
                .withSecond(59);

        List<InterviewSession> interviewSessions = interviewSessionRepository.findSessionsByWeekRange(startOfWeek, endOfWeek);
        List<Map<String, Object>> result = new ArrayList<>();
        result = interviewSessions.stream().map(interviewSession -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", interviewSession.getId());
            map.put("title", interviewSession.getTitle());
            map.put("description", interviewSession.getDescription());
            map.put("job_post_code", interviewSession.getJobPostCode());
            map.put("start_time", interviewSession.getStartTime());
            map.put("end_time", interviewSession.getEndTime());
            map.put("location", interviewSession.getLocation());
            map.put("meeting_link", interviewSession.getMeetingLink());
            map.put("note", interviewSession.getNote());
            map.put("candidate_codes", interviewCandidateRepository.findByInterviewSessionId(interviewSession.getId()).stream().map(InterviewCandidate::getCandidateCode));
            map.put("recruiter_codes", interviewRecruiterRepository.findByInterviewSessionId(interviewSession.getId()).stream().map(InterviewRecruiter::getRecruiterCode));
            return map;
        }).toList();
        return result;
    }

    public void checkValidTimeOfRecruiter(String recruiterCode, Long interviewSessionId, OffsetDateTime startTime, OffsetDateTime endTime) {
        List<InterviewSession> interviewSessions = interviewSessionRepository.findSessionsByTimeRange(startTime, endTime);
        if(interviewSessions.isEmpty()) {
            return;
        }
        interviewSessions.forEach(interviewSession -> {
            if(!interviewSession.getId().equals(interviewSessionId)) {
                InterviewRecruiter interviewRecruiter = interviewRecruiterRepository.findByRecruiterCodeAndInterviewSessionId(recruiterCode, interviewSession.getId());
                if (interviewRecruiter != null) {
                    throw new AppException(ErrorCode.INPUT_INVALID, "Nhân viên phụ trách phỏng vấn đã có lịch khác trong khung giờ này!");
                }
            }
        });
    }

    public void checkValidTimeOfCandidate(String candidateCode, Long interviewSessionId, OffsetDateTime startTime, OffsetDateTime endTime) {
        List<InterviewSession> interviewSessions = interviewSessionRepository.findSessionsByTimeRange(startTime, endTime);
        if(interviewSessions.isEmpty()) {
            return;
        }
        interviewSessions.forEach(interviewSession -> {
            if(!interviewSession.getId().equals(interviewSessionId)) {
                InterviewCandidate interviewCandidate = interviewCandidateRepository.findByCandidateCodeAndInterviewSessionId(candidateCode, interviewSession.getId());
                if (interviewCandidate != null) {
                    throw new AppException(ErrorCode.INPUT_INVALID, "Ứng viên đã có lịch khác trong khung giờ này!");
                }
            }
        });
    }

    @Transactional
    public void updateInterviewSession(InterviewScheduleDTO interviewScheduleDTO) {
        if(interviewScheduleDTO.getJobPostCode() == null || interviewScheduleDTO.getJobPostCode().isEmpty()) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Mã bài đăng tuyển dụng không được bỏ trống");
        }
        if(interviewScheduleDTO.getStartTime() == null || interviewScheduleDTO.getEndTime() == null) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Thời gian bắt đầu và kết thúc không được bỏ trống");
        }
        if (interviewScheduleDTO.getCandidateCodes() == null || interviewScheduleDTO.getCandidateCodes().isEmpty()) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Mã ứng viên tham gia phỏng vấn không được bỏ trống");
        }
        if (interviewScheduleDTO.getRecruiterCodes() == null || interviewScheduleDTO.getRecruiterCodes().isEmpty()) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Mã nhân viên phụ trách phỏng vấn không được bỏ trống");
        }
        InterviewSession interviewSession = getInterviewSession(interviewScheduleDTO.getId());
        if (interviewSession == null) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Không tìm thấy lịch phỏng vấn!");
        }
        checkValidStartTimeAndEndTime(interviewScheduleDTO.getStartTime(), interviewScheduleDTO.getEndTime());
        interviewSession.setTitle(interviewScheduleDTO.getTitle());
        interviewSession.setJobPostCode(interviewScheduleDTO.getJobPostCode());
        interviewSession.setDescription(interviewScheduleDTO.getDescription());
        interviewSession.setStartTime(interviewScheduleDTO.getStartTime());
        interviewSession.setEndTime(interviewScheduleDTO.getEndTime());
        interviewSession.setLocation(interviewScheduleDTO.getLocation());
        interviewSession.setMeetingLink(interviewScheduleDTO.getMeetingLink());
        interviewSession.setNote(interviewScheduleDTO.getNote());
        interviewSessionRepository.save(interviewSession);
        List<String> candidateCodes = interviewScheduleDTO.getCandidateCodes();
        interviewCandidateRepository.deleteAllByInterviewSessionId(interviewSession.getId());
        candidateCodes.forEach(candidateCode -> {
                this.checkValidTimeOfCandidate(candidateCode,interviewSession.getId(), interviewScheduleDTO.getStartTime(), interviewScheduleDTO.getEndTime());
                InterviewCandidate interviewCandidate = new InterviewCandidate();
                interviewCandidate.setCandidateCode(candidateCode);
                interviewCandidate.setInterviewSessionId(interviewSession.getId());
                interviewCandidateRepository.save(interviewCandidate);
        });
        interviewRecruiterRepository.deleteAllByInterviewSessionId(interviewSession.getId());
        List<String> recruiterCodes = interviewScheduleDTO.getRecruiterCodes();
        recruiterCodes.forEach(recruiterCode -> {
                this.checkValidTimeOfRecruiter(recruiterCode,interviewSession.getId(), interviewScheduleDTO.getStartTime(), interviewScheduleDTO.getEndTime());
                InterviewRecruiter interviewRecruiter = new InterviewRecruiter();
                interviewRecruiter.setRecruiterCode(recruiterCode);
                interviewRecruiter.setInterviewSessionId(interviewSession.getId());
                interviewRecruiterRepository.save(interviewRecruiter);
        });
    }

    @Transactional
    public void deleteInterviewSession(Long id) {
        InterviewSession interviewSession = getInterviewSession(id);
        if (interviewSession == null) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Không tìm thấy lịch phỏng vấn!");
        }
        interviewSessionRepository.delete(interviewSession);
        interviewCandidateRepository.deleteAllByInterviewSessionId(id);
        interviewRecruiterRepository.deleteAllByInterviewSessionId(id);
    }

    private void checkValidStartTimeAndEndTime(OffsetDateTime startTime, OffsetDateTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Thời gian bắt đầu phải trước thời gian kết thúc");
        }
    }

}
