package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.Entity.Candidate;
import com.devcuong.smart_hr.Entity.JobPost;
import com.devcuong.smart_hr.Entity.PipelineStage;
import com.devcuong.smart_hr.Entity.RecruitmentRequest;
import com.devcuong.smart_hr.dto.JobPostDTO;
import com.devcuong.smart_hr.dto.JobPostRecordDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.enums.RecruitmentRequestStatus;
import com.devcuong.smart_hr.exception.AppException;
import com.devcuong.smart_hr.exception.ErrorCode;
import com.devcuong.smart_hr.repository.*;
import com.devcuong.smart_hr.utils.CodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JobPostService extends SearchService<JobPost> {

    @Autowired
    private JobPostRepository jobPostRepository;

    @Autowired
    private RecruitmentRequestRepository recruitmentRequestRepository;

    @Autowired
    private RecruitmentRequestService recruitmentRequestService;

    @Autowired
    private PipelineStageRepository pipelineStageRepository;

    @Autowired
    private CandidateStageRepository candidateStageRepository;

    @Autowired
    private CandidateRepository candidateRepository;


    public JobPostService(JobPostRepository repository) {
        super(repository);
    }

    public Page<JobPostRecordDTO> searchJobPost(PageFilterInput<JobPost> input) {
        try {
            Page<JobPost> page = super.findAll(input);
            List<JobPostRecordDTO> jobPosts = page.getContent().stream().map(this::convertToJobPostRecordDTO).collect(Collectors.toList());
            return new PageImpl<>(jobPosts, page.getPageable(), page.getTotalElements());
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Search job post failed");
        }
    }

    public JobPostRecordDTO convertToJobPostRecordDTO(JobPost jobPost) {
        JobPostRecordDTO jobPostRecordDTO = new JobPostRecordDTO();
        jobPostRecordDTO.setId(jobPost.getId());
        jobPostRecordDTO.setRequestCode(jobPost.getRequestCode());
        jobPostRecordDTO.setTitle(jobPost.getTitle());
        jobPostRecordDTO.setCreatedAt(jobPost.getCreatedAt());
        jobPostRecordDTO.setDescription(jobPost.getDescription());
        jobPostRecordDTO.setJobPostCode(jobPost.getJobPostCode());
        jobPostRecordDTO.setIsOpen(jobPost.getIsOpen());
        RecruitmentRequest recruitmentRequest = recruitmentRequestRepository.findByRecruitmentRequestCode(jobPostRecordDTO.getRequestCode()).orElseThrow(null);
        if (recruitmentRequest != null) {
            jobPostRecordDTO.setRecruitmentRequestRecord(recruitmentRequestService.convertToRecruitmentRequestRecordDTO(recruitmentRequest));
        }else {
            jobPostRecordDTO.setRecruitmentRequestRecord(null);
        }
        return jobPostRecordDTO;
    }

    public JobPost createJobPost(JobPostDTO post) {
        if (post.getRequestCode() == null || post.getRequestCode().isEmpty()) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Mã yêu cầu tuyển dụng không được để trống.");
        }
        if (post.getTitle() == null || post.getTitle().isEmpty()) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Tiêu đề bài đăng tuyển dụng không được để trống.");
        }
        JobPost existingJobPost = jobPostRepository.findByRequestCode(post.getRequestCode()).orElse(null);
        if (existingJobPost != null) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Yêu cầu tuyển dụng này đã được đăng bài! Vui lòng chọn yêu cầu tuyển dụng khác.");
        }
        RecruitmentRequest recruitmentRequest = recruitmentRequestRepository.findByRecruitmentRequestCode(post.getRequestCode()).orElse(null);
        JobPost newJobPost = new JobPost();
        newJobPost.setRequestCode(post.getRequestCode());
        newJobPost.setTitle(post.getTitle());
        newJobPost.setCreatedAt(post.getCreatedAt());
        newJobPost.setDescription(post.getDescription());
        newJobPost.setJobPostCode("TEMP");
        newJobPost.setIsOpen(true);
        jobPostRepository.save(newJobPost);
        newJobPost.setJobPostCode(generateJobPostCode(newJobPost.getId()));
        assert recruitmentRequest != null;

        // Cập nhật trạng thái yêu cầu tuyển dụng
        recruitmentRequest.setStatus(RecruitmentRequestStatus.DANGBAI);
        recruitmentRequestRepository.save(recruitmentRequest);

        // Tạo quy trình tuyển dụng mặc định
        createPipelineStage(newJobPost);
        return newJobPost;
    }

    public void createPipelineStage(JobPost jobPost) {
        PipelineStage pipelineStage = new PipelineStage();
        pipelineStage.setJobPostCode(jobPost.getJobPostCode());
        pipelineStage.setStageName("Ứng tuyển");
        Integer maxOrder = pipelineStageRepository.findMaxOrder();
        pipelineStage.setStageOrder((maxOrder==null ? 1 : maxOrder) + 1);
        pipelineStage.setIsOpen(true);
        pipelineStage.setCreatedAt(jobPost.getCreatedAt());
        pipelineStageRepository.save(pipelineStage);
    }

    public String generateJobPostCode(Long id) {
        return CodeUtils.generateCode("JP", id);
    }

    public Map<String, Object> getJobPostByRequestCode(String requestCode) {
        RecruitmentRequest recruitmentRequest = recruitmentRequestRepository.findByRecruitmentRequestCode(requestCode).orElse(null);
        if (recruitmentRequest == null) {
            throw new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy yêu cầu tuyển dụng!");
        }
        JobPost jobPost = jobPostRepository.findByRequestCode(requestCode).orElse(null);
        if (jobPost == null) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Không tìm thấy bài đăng tuyển dụng");
        }
        return Map.of("jobPost", jobPost, "recruitmentRequest", recruitmentRequest);
    }

    public Map<String, Object> getJobPostByJobPostCode(String jobPostCode) {
        JobPost jobPost = jobPostRepository.findByJobPostCode(jobPostCode).orElse(null);
        if (jobPost == null) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Không tìm thấy bài đăng tuyển dụng");
        }
        RecruitmentRequest recruitmentRequest = recruitmentRequestRepository.findByRecruitmentRequestCode(jobPost.getRequestCode()).orElse(null);
        if (recruitmentRequest == null) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Không tìm thấy yêu cầu tuyển dụng");
        }
        return Map.of("job_post", jobPost, "recruitment_request", recruitmentRequest);
    }

    public JobPost findJobPostByJobPostCode(String jobPostCode) {
        return jobPostRepository.findByJobPostCode(jobPostCode).orElse(null);
    }

    public JobPost updateJobPost(JobPostDTO jobPostDTO) {
        if (jobPostDTO.getRequestCode() == null || jobPostDTO.getRequestCode().isEmpty()) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Mã yêu cầu tuyển dụng không được để trống.");
        }
        if (jobPostDTO.getTitle() == null || jobPostDTO.getTitle().isEmpty()) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Tiêu đề bài đăng tuyển dụng không được để trống.");
        }
        JobPost jobPost = jobPostRepository.findByRequestCode(jobPostDTO.getRequestCode()).orElse(null);
        if (jobPost == null) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Không tìm thấy bài đăng tuyển dụng");
        }
        jobPost.setTitle(jobPostDTO.getTitle());
        jobPost.setDescription(jobPostDTO.getDescription());
        return jobPostRepository.save(jobPost);
    }

    @Transactional
    public void deleteJobPost(Long id) {
        JobPost jobPostEntity = jobPostRepository.findById(id).orElse(null);
        if (jobPostEntity == null) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Không tìm thấy bài đăng tuyển dụng");
        }

        Candidate candidateExisting = candidateRepository.findByJobPostCode(jobPostEntity.getJobPostCode()).stream().findFirst().orElse(null);
        log.info("Candidate existing: {}", candidateExisting);
        if (candidateExisting != null) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Không thể xóa bài đăng tuyển dụng khi có ứng viên đã nộp đơn.");
        }
        else {
            // Xóa tất cả các giai đoạn ứng viên liên quan đến bài đăng tuyển dụng này
            List<PipelineStage> pipelineStages = pipelineStageRepository.findByJobPostCode(jobPostEntity.getJobPostCode());
            if (!pipelineStages.isEmpty()) {
                pipelineStages.forEach(pipelineStage -> {
                    candidateStageRepository.deleteByStageId(pipelineStage.getId());
                    pipelineStageRepository.delete(pipelineStage);
                });
            }
    //        candidateRepository.deleteByJobPostCode(jobPostEntity.getJobPostCode());
            jobPostRepository.delete(jobPostEntity);
        }
    }

    public void updateIsOpenJobPost(String jobPostCode ,Boolean isOpen) {
        JobPost jobPostEntity = jobPostRepository.findByJobPostCode(jobPostCode).orElse(null);
        if (jobPostEntity == null) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Không tìm thấy bài đăng tuyển dụng");
            }
        jobPostEntity.setIsOpen(isOpen);
        jobPostRepository.save(jobPostEntity);

    }
}
