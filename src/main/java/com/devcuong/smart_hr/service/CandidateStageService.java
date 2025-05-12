package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.Entity.CandidateStage;
import com.devcuong.smart_hr.dto.CandidateStageDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.enums.CandidateStatus;
import com.devcuong.smart_hr.exception.AppException;
import com.devcuong.smart_hr.exception.ErrorCode;
import com.devcuong.smart_hr.repository.CandidateRepository;
import com.devcuong.smart_hr.repository.CandidateStageRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CandidateStageService extends SearchService<CandidateStage> {

    @Autowired
    private CandidateStageRepository candidateStageRepository;

    @Autowired
    private CandidateService candidateService;

    CandidateStageService(CandidateStageRepository candidateStageRepository) {
        super(candidateStageRepository);
    }

    public Page<Map<String, Object>> search(PageFilterInput<CandidateStage> input) {
        try{
            Page<CandidateStage> page = super.findAll(input);
            List<Map<String, Object>> candidateStages = page.getContent().stream().map(this::convertToMap).collect(Collectors.toList());
            return new PageImpl<>(candidateStages, page.getPageable(), page.getTotalElements());

        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Search Failed!");
        }

    }

    public Map<String, Object> convertToMap(CandidateStage candidateStage) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", candidateStage.getId());
        map.put("candidate_code", candidateStage.getCandidateCode());
        map.put("stage_id", candidateStage.getStageId());
        map.put("job_post_code", candidateStage.getJobPostCode());
        map.put("updated_at", candidateStage.getUpdatedAt());
        map.put("status", candidateStage.getStatus());
        map.put("note", candidateStage.getNote());
        map.put("candidate", candidateService.findByCandidateCode(candidateStage.getCandidateCode()));
        return map;
    }

    public CandidateStage createCandidateStage(CandidateStageDTO candidateStageDTO) {
        CandidateStage candidateStage = new CandidateStage();
        candidateStage.setCandidateCode(candidateStageDTO.getCandidateCode());
        candidateStage.setStageId(candidateStageDTO.getStageId());
        candidateStage.setJobPostCode(candidateStageDTO.getJobPostCode());
        candidateStage.setUpdatedAt(candidateStageDTO.getUpdatedAt());
        candidateStage.setStatus(candidateStageDTO.getStatus());
        candidateStage.setNote(candidateStageDTO.getNote());
        try {
            candidateStageRepository.save(candidateStage);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Không tồn ứng viên!");
        }
        candidateService.updateStatus(candidateStage.getCandidateCode(), CandidateStatus.DANGUNGTUYEN);
        return candidateStage;
    }

    public CandidateStage updateCandidateStage(CandidateStageDTO candidateStageDTO) {
        CandidateStage candidateStage = candidateStageRepository.findById(candidateStageDTO.getId()).orElseThrow(null);

        if (Objects.isNull(candidateStage)) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Không tồn ứng viên!");
        }
        candidateStage.setStageId(candidateStageDTO.getStageId());
        candidateStage.setJobPostCode(candidateStageDTO.getJobPostCode());
        candidateStage.setUpdatedAt(candidateStageDTO.getUpdatedAt());
        candidateStage.setNote(candidateStageDTO.getNote());
        try {
            candidateStageRepository.save(candidateStage);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Không tìm thấy ứng viên!");
        }
        return candidateStage;
    }

    public void deleteCandidateStage(Long id) {
        CandidateStage candidateStage = candidateStageRepository.findById(id).orElseThrow(null);
        if (Objects.isNull(candidateStage)) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Không tìm thấy ứng viên!");
        }
        candidateStageRepository.delete(candidateStage);
    }

    @Transactional
    public void deleteByStageId(Integer stageId) {
        candidateStageRepository.findAllByStageId(stageId).forEach(stage -> {
            candidateService.updateStatus(stage.getCandidateCode(), CandidateStatus.KHOITAO);
        });
        candidateStageRepository.deleteByStageId(stageId);
    }

    public void deleteAndUpdateStatusCandidate(Long id, CandidateStatus status) {
        CandidateStage candidateStage = candidateStageRepository.findById(id).orElseThrow(null);
        if(candidateStage == null) {
            throw new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy ứng viên!");
        }
        candidateService.updateStatus(candidateStage.getCandidateCode(), status);
        candidateStageRepository.delete(candidateStage);
    }

}
