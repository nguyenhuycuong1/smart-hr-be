package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.Entity.*;
import com.devcuong.smart_hr.dto.RecruitmentRequestDTO;
import com.devcuong.smart_hr.dto.RecruitmentRequestRecordDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.enums.RecruitmentRequestStatus;
import com.devcuong.smart_hr.exception.AppException;
import com.devcuong.smart_hr.exception.ErrorCode;
import com.devcuong.smart_hr.repository.DepartmentRepository;
import com.devcuong.smart_hr.repository.JobPositionRepository;
import com.devcuong.smart_hr.repository.JobPostRepository;
import com.devcuong.smart_hr.repository.RecruitmentRequestRepository;
import com.devcuong.smart_hr.utils.CodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RecruitmentRequestService extends SearchService<RecruitmentRequest> {

    @Autowired
    private RecruitmentRequestRepository repository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private JobPositionRepository jobPositionRepository;

    @Autowired
    private JobPostRepository jobPostRepository;
    @Autowired
    private RecruitmentRequestRepository recruitmentRequestRepository;

    public RecruitmentRequestService(RecruitmentRequestRepository repository) {
        super(repository);
    }

    public Page<RecruitmentRequestRecordDTO> search(PageFilterInput<RecruitmentRequest> input) {
        try{
            Page<RecruitmentRequest> page =  super.findAll(input);
            List<RecruitmentRequestRecordDTO> recruitmentRequests = new ArrayList<>(page.getContent()).stream().map(this::convertToRecruitmentRequestRecordDTO).toList();

            return new PageImpl<>(recruitmentRequests, page.getPageable(), page.getTotalElements());
        }catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Search Failed");
        }
    }

    RecruitmentRequestRecordDTO convertToRecruitmentRequestRecordDTO(RecruitmentRequest recruitmentRequest) {
        RecruitmentRequestRecordDTO dto = getRecruitmentRequestRecordDTO(recruitmentRequest);
        // Lấy thông tin Department, Team, JobPosition nếu có
        if (recruitmentRequest.getDepartmentCode() != null) {
            Optional<Department> department = departmentRepository.findDepartmentByDepartmentCode(recruitmentRequest.getDepartmentCode());
            department.ifPresent(dto::setDepartment);
        }

        if (recruitmentRequest.getJobCode() != null) {
            Optional<JobPosition> jobPosition = jobPositionRepository.findJobPositionByJobCode(recruitmentRequest.getJobCode());
            jobPosition.ifPresent(dto::setJobPosition);
        }
        return dto;
    }

    private static RecruitmentRequestRecordDTO getRecruitmentRequestRecordDTO(RecruitmentRequest recruitmentRequest) {
        RecruitmentRequestRecordDTO dto = new RecruitmentRequestRecordDTO();
        dto.setId(recruitmentRequest.getId());
        dto.setQuantity(recruitmentRequest.getQuantity());
        dto.setStatus(recruitmentRequest.getStatus());
        dto.setCreatedAt(recruitmentRequest.getCreatedAt());
        dto.setCreatedBy(recruitmentRequest.getCreatedBy());
        dto.setUsernameCreated(recruitmentRequest.getUsernameCreated());
        dto.setDepartmentCode(recruitmentRequest.getDepartmentCode());
        dto.setJobCode(recruitmentRequest.getJobCode());
        dto.setRecruitmentRequestCode(recruitmentRequest.getRecruitmentRequestCode());
        dto.setUpdatedAt(recruitmentRequest.getUpdatedAt());
        dto.setUpdatedBy(recruitmentRequest.getUpdatedBy());
        dto.setUsernameUpdated(recruitmentRequest.getUsernameUpdated());
        return dto;
    }

    public RecruitmentRequest findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public RecruitmentRequest findByRecruitmentCode(String recruitmentCode) {
        return repository.findByRecruitmentRequestCode(recruitmentCode).orElse(null);
    }

    public RecruitmentRequest createRecruitmentRequest(RecruitmentRequestDTO recruitmentRequestDTO) {
        RecruitmentRequest recruitmentRequest = new RecruitmentRequest();
        recruitmentRequest.setRecruitmentRequestCode("TEMP");
        recruitmentRequest.setDepartmentCode(recruitmentRequestDTO.getDepartmentCode());
        if(recruitmentRequestDTO.getJobCode() == null || recruitmentRequestDTO.getJobCode().isEmpty()) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Vị trí công việc không được để trống");
        }
        recruitmentRequest.setJobCode(recruitmentRequestDTO.getJobCode());
        if(recruitmentRequestDTO.getQuantity() < 1 ) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Số lượng không hợp lệ");
        }
        recruitmentRequest.setQuantity(recruitmentRequestDTO.getQuantity());
        recruitmentRequest.setStatus(RecruitmentRequestStatus.KHOITAO);
        recruitmentRequest.setCreatedBy(recruitmentRequestDTO.getCreatedBy());
        recruitmentRequest.setCreatedAt(recruitmentRequestDTO.getCreatedAt());
        recruitmentRequest.setUsernameCreated(recruitmentRequestDTO.getUsernameCreated());
        recruitmentRequest = repository.save(recruitmentRequest);
        recruitmentRequest.setRecruitmentRequestCode(generateRecruitmentCode(recruitmentRequest.getId()));
        return repository.save(recruitmentRequest);
    }

    public String generateRecruitmentCode(Long id) {
            return CodeUtils.generateCode("RE", id);
    }

    public RecruitmentRequest updateRecruitmentRequest(RecruitmentRequestDTO recruitmentRequestDTO) {
        RecruitmentRequest recruitmentRequest = repository.findById(recruitmentRequestDTO.getId()).orElse(null);
        if (recruitmentRequest == null) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Recruitment Request not found");
        }
        recruitmentRequest.setDepartmentCode(recruitmentRequestDTO.getDepartmentCode());
        if(recruitmentRequestDTO.getJobCode() == null || recruitmentRequestDTO.getJobCode().isEmpty()) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Vị trí công việc không được để trống");
        }
        recruitmentRequest.setJobCode(recruitmentRequestDTO.getJobCode());
        if(recruitmentRequestDTO.getQuantity() < 1 ) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Số lượng không hợp lệ");
        }
        recruitmentRequest.setQuantity(recruitmentRequestDTO.getQuantity());
        return repository.save(recruitmentRequest);
    }

    public void deleteRecruitmentRequest(Long id) {
        RecruitmentRequest recruitmentRequest = repository.findById(id).orElse(null);
        if (recruitmentRequest == null) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Recruitment Request not found");
        }
        repository.delete(recruitmentRequest);
    }

    public void updateStatus(List<RecruitmentRequestDTO> list, RecruitmentRequestStatus status, String username) {
        if (list == null || list.isEmpty()) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Danh sách không được để trống");
        }

        for (RecruitmentRequestDTO item : list) {
            RecruitmentRequest recruitmentRequest = repository.findById(item.getId()).orElse(null);
            if (recruitmentRequest == null) {
                throw new AppException(ErrorCode.UNCATEGORIZED, "Recruitment Request not found with ID: " + item.getId());
            }
            if(status == RecruitmentRequestStatus.DANGDUYET
                    && !username.equals(recruitmentRequest.getUsernameCreated())) {
                throw new AppException(ErrorCode.UNCATEGORIZED, "Bạn không có quyền gửi duyệt bản ghi này!");
            }
            if(recruitmentRequest.getStatus() != RecruitmentRequestStatus.KHOITAO && status == RecruitmentRequestStatus.DANGDUYET) {
                throw new AppException(ErrorCode.UNCATEGORIZED, "Trạng thái của bản ghi không phải là khởi tạo!");
            }
            if(
                recruitmentRequest.getStatus() != RecruitmentRequestStatus.DANGDUYET
                && (status == RecruitmentRequestStatus.PHEDUYET || status == RecruitmentRequestStatus.TUCHOI)
            ) {
                throw new AppException(ErrorCode.UNCATEGORIZED, "Trạng thái của bản khi không phải là đang duyệt!");
            }

            if(status == RecruitmentRequestStatus.PHEDUYET || status == RecruitmentRequestStatus.TUCHOI){
                recruitmentRequest.setUpdatedBy(item.getUpdatedBy());
                recruitmentRequest.setUpdatedAt(item.getUpdatedAt());
                recruitmentRequest.setUsernameUpdated(item.getUsernameUpdated());
            }

            recruitmentRequest.setStatus(status);
            repository.save(recruitmentRequest);
        }
    }

    public void updateStatusAndCreateJobPost(List<RecruitmentRequestDTO> list) {
        if (list == null || list.isEmpty()) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Danh sách không được để trống");
        }

        for (RecruitmentRequestDTO item : list) {
            RecruitmentRequest recruitmentRequest = repository.findById(item.getId()).orElse(null);
            if (recruitmentRequest == null) {
                throw new AppException(ErrorCode.UNCATEGORIZED, "Recruitment Request not found with ID: " + item.getId());
            }
            if(item.getStatus() != RecruitmentRequestStatus.PHEDUYET) {
                throw new AppException(ErrorCode.INPUT_INVALID, "Vui lòng chọn yêu cầu có trạng thái Phê duyệt để đăng bài!");
            }
            recruitmentRequest.setUpdatedBy(item.getUpdatedBy());
            recruitmentRequest.setUpdatedAt(item.getUpdatedAt());
            recruitmentRequest.setUsernameUpdated(item.getUsernameUpdated());
            recruitmentRequest.setStatus(RecruitmentRequestStatus.DANGBAI);
            recruitmentRequestRepository.save(recruitmentRequest);
        }
    }

    public void updateStatusByRequestCode(String requestCode, RecruitmentRequestStatus status) {
        RecruitmentRequest recruitmentRequest = repository.findByRecruitmentRequestCode(requestCode).orElse(null);
        if (recruitmentRequest == null) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Recruitment Request not found with ID: " + requestCode);
        }
        recruitmentRequest.setStatus(status);
        recruitmentRequestRepository.save(recruitmentRequest);
    }


}
