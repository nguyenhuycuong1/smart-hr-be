package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.Entity.WorkSchedule;
import com.devcuong.smart_hr.dto.WorkScheduleDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.exception.AppException;
import com.devcuong.smart_hr.exception.ErrorCode;
import com.devcuong.smart_hr.repository.WorkScheduleRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class WorkScheduleService extends SearchService<WorkSchedule> {

    @Autowired
    WorkScheduleRepository repository;

    public WorkScheduleService(WorkScheduleRepository repository) {
        super(repository);
    }

    public Page<WorkSchedule> getAllWorkSchedules(PageFilterInput<WorkSchedule> input) {
        try {
            Page<WorkSchedule> schedulePage = super.findAll(input);
            List<WorkSchedule> schedules = new ArrayList<>(schedulePage.getContent());
            return new PageImpl<>(schedules, schedulePage.getPageable(), schedulePage.getTotalElements());
        } catch (Exception e) {
            log.error("Error retrieving work schedules", e);
            throw new AppException(ErrorCode.UNCATEGORIZED, "Failed to retrieve work schedules: " + e.getMessage());
        }
    }

    public List<WorkSchedule> getListWorkSchedule() {
        return repository.findAll();
    }

    public WorkSchedule getWorkScheduleById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "Work schedule not found"));
    }

    public WorkSchedule createWorkSchedule(WorkScheduleDTO scheduleDTO) {
        if(scheduleDTO.getScheduleName() == null || scheduleDTO.getScheduleName().isEmpty()) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Tên ca làm việc không được để trống");
        }
        if(scheduleDTO.getStartTime() == null || scheduleDTO.getEndTime() == null) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Thời gian bắt đầu và kết thúc không được để trống");
        }
        if(scheduleDTO.getTotalWorkHours()== null || scheduleDTO.getTotalWorkHours() <= 0) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Tổng số giờ làm việc phải lớn hơn 0");
        }
        WorkSchedule schedule = new WorkSchedule();
        updateScheduleFromDTO(schedule, scheduleDTO);
        schedule.setCreatedAt(LocalDate.now());
        schedule.setUpdatedAt(LocalDate.now());
        return repository.save(schedule);
    }

    public WorkSchedule updateWorkSchedule(Integer id, WorkScheduleDTO scheduleDTO) {
        if(scheduleDTO.getScheduleName() == null || scheduleDTO.getScheduleName().isEmpty()) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Tên ca làm việc không được để trống");
        }
        if(scheduleDTO.getStartTime() == null || scheduleDTO.getEndTime() == null) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Thời gian bắt đầu và kết thúc không được để trống");
        }
        if(scheduleDTO.getTotalWorkHours()== null || scheduleDTO.getTotalWorkHours() <= 0) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Tổng số giờ làm việc phải lớn hơn 0");
        }
        WorkSchedule schedule = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "Work schedule not found"));
        updateScheduleFromDTO(schedule, scheduleDTO);
        schedule.setUpdatedAt(LocalDate.now());
        return repository.save(schedule);
    }

    public void deleteWorkSchedule(Integer id) {
        WorkSchedule schedule = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "Work schedule not found"));
        repository.delete(schedule);
    }

    private void updateScheduleFromDTO(WorkSchedule schedule, WorkScheduleDTO dto) {
        schedule.setScheduleName(dto.getScheduleName());
        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());
        schedule.setDescription(dto.getDescription());
        schedule.setBreakStart(dto.getBreakStart());
        schedule.setBreakEnd(dto.getBreakEnd());
        schedule.setTotalWorkHours(dto.getTotalWorkHours());
    }

}
