package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.Entity.Candidate;
import com.devcuong.smart_hr.Entity.JobPost;
import com.devcuong.smart_hr.Entity.PipelineStage;
import com.devcuong.smart_hr.Entity.RecruitmentRequest;
import com.devcuong.smart_hr.dto.CandidateDTO;
import com.devcuong.smart_hr.dto.CandidateStageDTO;
import com.devcuong.smart_hr.dto.JobPostRecordDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.enums.CandidateStatus;
import com.devcuong.smart_hr.enums.RecruitmentRequestStatus;
import com.devcuong.smart_hr.exception.AppException;
import com.devcuong.smart_hr.exception.ErrorCode;
import com.devcuong.smart_hr.repository.CandidateRepository;
import com.devcuong.smart_hr.repository.PipelineStageRepository;
import com.devcuong.smart_hr.utils.CodeUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CandidateService extends SearchService<Candidate>{

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private JobPostService jobPostService;

    @Autowired
    private PipelineStageRepository stageRepository;

    @Autowired
    private RecruitmentRequestService recruitmentRequestService;

    public CandidateService(JpaSpecificationExecutor<Candidate> repository) {
        super(repository);
    }

    public Page<Map<String, Object>> search(PageFilterInput<Candidate> input) {
        try {
            Page<Candidate> page = super.findAll(input);
            List<Map<String, Object>> candidates = page.getContent().stream().map(this::convertToMap).collect(Collectors.toList());
            return new PageImpl<>(candidates, page.getPageable(), page.getTotalElements());
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Search failed");
        }
    }

    public Map<String, Object> convertToMap(Candidate candidate) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", candidate.getId());
        map.put("candidate_code", candidate.getCandidateCode());
        map.put("email", candidate.getEmail());
        map.put("first_name", candidate.getFirstName());
        map.put("last_name", candidate.getLastName());
        map.put("phone_number", candidate.getPhoneNumber());
        map.put("job_post_code", candidate.getJobPostCode());
        map.put("resume_url", candidate.getResumeUrl());
        map.put("status", candidate.getStatus());
        map.put("applied_at", candidate.getAppliedAt());
        map.put("gender", candidate.getGender());
        map.put("address", candidate.getAddress());
        map.put("dob", candidate.getDob());
        map.put("current_address", candidate.getCurrentAddress());
        JobPost jobPost = jobPostService.findJobPostByJobPostCode(candidate.getJobPostCode());
        JobPostRecordDTO jobPostRecordDTO = jobPostService.convertToJobPostRecordDTO(jobPost);
        map.put("job_position", jobPostRecordDTO);
        return map;
    }

    public Candidate findById(Long id) {
        return candidateRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Candidate not found"));
    }

    public Candidate findByCandidateCode(String candidateCode) {
        return candidateRepository.findByCandidateCode(candidateCode);
    }

    public Candidate create(CandidateDTO candidate) {
        Candidate newCandidate = new Candidate();
        newCandidate.setCandidateCode("TEMP");
        newCandidate.setEmail(candidate.getEmail());
        newCandidate.setFirstName(candidate.getFirstName());
        newCandidate.setLastName(candidate.getLastName());
        newCandidate.setPhoneNumber(candidate.getPhoneNumber());
        newCandidate.setJobPostCode(candidate.getJobPostCode());
        newCandidate.setResumeUrl(candidate.getResumeUrl());
        newCandidate.setStatus(candidate.getStatus());
        newCandidate.setAppliedAt(candidate.getAppliedAt());
        newCandidate.setGender(candidate.getGender());
        newCandidate.setAddress(candidate.getAddress());
        newCandidate.setDob(candidate.getDob());
        newCandidate.setCurrentAddress(candidate.getCurrentAddress());
        candidateRepository.save(newCandidate);
        newCandidate.setCandidateCode(generateCode(newCandidate.getId()));
        return candidateRepository.save(newCandidate);
    }

    public String generateCode(Long id) {
        return CodeUtils.generateCode("CA", id);
    }

    @Transactional
    public Candidate update(CandidateDTO candidateDTO) {
        Candidate candidate = findById(candidateDTO.getId());
        candidate.setFirstName(candidateDTO.getFirstName());
        candidate.setLastName(candidateDTO.getLastName());
        candidate.setPhoneNumber(candidateDTO.getPhoneNumber());
        candidate.setResumeUrl(candidateDTO.getResumeUrl());
        candidate.setStatus(candidateDTO.getStatus());
        this.updateStatus(candidate.getCandidateCode(), candidateDTO.getStatus());
        candidate.setAppliedAt(candidateDTO.getAppliedAt());
        candidate.setGender(candidateDTO.getGender());
        candidate.setAddress(candidateDTO.getAddress());
        candidate.setDob(candidateDTO.getDob());
        candidate.setEmail(candidateDTO.getEmail());
        candidate.setCurrentAddress(candidateDTO.getCurrentAddress());
        return candidateRepository.save(candidate);
    }

    @Transactional
    public void updateStatus(String candidateCode, CandidateStatus status) {
        Candidate candidate = candidateRepository.findByCandidateCode(candidateCode);
        if (candidate != null) {
            candidate.setStatus(status);
            candidateRepository.save(candidate);
            if(status == CandidateStatus.TRUNGTUYEN) {
                handleSuccessfulCandidate(candidate);
            }
        } else {
                throw new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy ứng viên!");
            }
    }

    @Transactional
    public void handleSuccessfulCandidate(Candidate candidate) {
        Long count = candidateRepository.countByJobPostCodeAndStatus(candidate.getJobPostCode(), CandidateStatus.TRUNGTUYEN);
        Map<String, Object> jobPost = jobPostService.getJobPostByJobPostCode(candidate.getJobPostCode());
        RecruitmentRequest recruitmentRequest = (RecruitmentRequest) jobPost.get("recruitment_request");
        Integer quantity = recruitmentRequest.getQuantity();
        if(count >= quantity) {
            jobPostService.updateIsOpenJobPost(candidate.getJobPostCode(), false);
            List<PipelineStage> pipelineStages = stageRepository.findByJobPostCode(candidate.getJobPostCode());
            pipelineStages.forEach(pipelineStage -> {
                pipelineStage.setIsOpen(false);
                stageRepository.save(pipelineStage);
            });
            recruitmentRequestService.updateStatusByRequestCode(recruitmentRequest.getRecruitmentRequestCode(), RecruitmentRequestStatus.HOANTHANH);
            updateStatusOtherCandidates(candidate.getCandidateCode(), candidate.getJobPostCode());


        }
    }

    public void deleteById(Long id) {
        Candidate candidate = findById(id);
        candidateRepository.delete(candidate);
    }

    @Transactional
    public void updateStatusOtherCandidates(String candidateCode, String jobPostCode) {
        List<Candidate> candidates = candidateRepository.findByJobPostCodeAndCandidateCodeNot(jobPostCode, candidateCode);
        candidates.forEach(candidate -> {
            if (candidate.getStatus() != CandidateStatus.TRUNGTUYEN) {
                candidate.setStatus(CandidateStatus.KHONGDAT);
                candidateRepository.save(candidate);
            }
        });
    }



}
