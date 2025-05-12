package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.Entity.JobPost;
import com.devcuong.smart_hr.Entity.PipelineStage;
import com.devcuong.smart_hr.dto.JobPostRecordDTO;
import com.devcuong.smart_hr.dto.PipelineStageDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.exception.AppException;
import com.devcuong.smart_hr.exception.ErrorCode;
import com.devcuong.smart_hr.repository.PipelineStageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PipelineStageService extends SearchService<PipelineStage> {
    @Autowired
    private PipelineStageRepository pipelineStageRepository;

    @Autowired
    private JobPostService jobPostService;

    @Autowired
    private CandidateStageService candidateStageService;

    PipelineStageService(PipelineStageRepository pipelineStageRepository) {
        super(pipelineStageRepository);
    }

    public Page<Map<String, Object>> searchPipeline(PageFilterInput<PipelineStage> input) {
        Page<PipelineStage> page = super.findAll(input);
        List<Map<String, Object>> pipelineStages = page.getContent().stream().map(this::convertToPipelineRecord).collect(Collectors.toList());
        return new PageImpl<Map<String, Object>>(pipelineStages, page.getPageable(), page.getTotalElements());
    }

    public Map<String, Object> convertToPipelineRecord(PipelineStage pipelineStage) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("pipe_line", pipelineStage);
        JobPost jobPost = jobPostService.findJobPostByJobPostCode(pipelineStage.getJobPostCode());
            JobPostRecordDTO jobPostRecordDTO = jobPostService.convertToJobPostRecordDTO(jobPost);
            result.put("job_post", jobPostRecordDTO);
        return result;
    }

    public PipelineStage createPipeline(PipelineStageDTO pipelineStageDTO) {
        PipelineStage pipelineStage = new PipelineStage();
        pipelineStage.setJobPostCode(pipelineStageDTO.getJobPostCode());
        pipelineStage.setStageName(pipelineStageDTO.getStageName());
        Integer maxOrder = pipelineStageRepository.findMaxOrder();
        pipelineStage.setStageOrder((maxOrder==null ? 1 : maxOrder) + 1);
        pipelineStage.setIsOpen(true);
        pipelineStage.setCreatedAt(pipelineStageDTO.getCreatedAt());
        return pipelineStageRepository.save(pipelineStage);
    }

    public void deletePipeline(Integer id) {
        pipelineStageRepository.findById(id).ifPresent(pipelineStage -> {
            candidateStageService.deleteByStageId(pipelineStage.getId());
            pipelineStageRepository.delete(pipelineStage);
        });
    }

    public void updateBatchStageOrder(List<PipelineStageDTO> pipelineStageDTOs) {
        pipelineStageDTOs.forEach(pipelineStageDTO -> {
            PipelineStage pipelineStage = pipelineStageRepository.findById(pipelineStageDTO.getId()).orElse(null);
            if (pipelineStage != null) {
                pipelineStage.setStageOrder(pipelineStageDTO.getStageOrder());
                pipelineStageRepository.save(pipelineStage);
            }
        });
    }

    public void updateStageName(PipelineStageDTO pipelineStageDTO) {
        PipelineStage pipelineStage = pipelineStageRepository.findById(pipelineStageDTO.getId()).orElse(null);
        if (pipelineStage != null) {
            pipelineStage.setStageName(pipelineStageDTO.getStageName());
        }else {
            throw new AppException(ErrorCode.NOT_FOUND, "Pipeline not found");
        }
    }

    public void updateStageStatusWithJobPostCode(String jopPostCode, boolean status) {
        List<PipelineStage> pipelineStages = pipelineStageRepository.findByJobPostCode(jopPostCode);
        if(pipelineStages.isEmpty()) {
           throw new AppException(ErrorCode.NOT_FOUND, "Pipeline not found");
        }
        pipelineStages.forEach(pipelineStage -> {
            pipelineStage.setIsOpen(status);
            pipelineStageRepository.save(pipelineStage);
        });
    }


}
