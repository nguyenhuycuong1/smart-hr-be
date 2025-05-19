package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.Entity.Holiday;
import com.devcuong.smart_hr.dto.HolidayDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.exception.AppException;
import com.devcuong.smart_hr.exception.ErrorCode;
import com.devcuong.smart_hr.repository.HolidayRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class HolidayService extends SearchService<Holiday> {

    @Autowired
    HolidayRepository repository;

    public HolidayService(HolidayRepository repository) {
        super(repository);
    }

    public Page<Holiday> getAllHolidays(PageFilterInput<Holiday> input) {
        try {
            Page<Holiday> holidayPage = super.findAll(input);
            List<Holiday> holidays = new ArrayList<>(holidayPage.getContent());
            return new PageImpl<>(holidays, holidayPage.getPageable(), holidayPage.getTotalElements());
        } catch (Exception e) {
            log.error("Error retrieving holidays", e);
            throw new AppException(ErrorCode.UNCATEGORIZED, "Failed to retrieve holidays: " + e.getMessage());
        }
    }

    public List<Holiday> getListHolidays() {
        return repository.findAll();
    }

    public Holiday createHoliday(HolidayDTO holidayDTO) {
        Holiday holiday = new Holiday();
        updateHolidayFromDTO(holiday, holidayDTO);
        return repository.save(holiday);
    }

    public Holiday updateHoliday(Long id, HolidayDTO holidayDTO) {
        Holiday holiday = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "Holiday not found"));
        updateHolidayFromDTO(holiday, holidayDTO);
        return repository.save(holiday);
    }

    public void deleteHoliday(Long id) {
        Holiday holiday = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "Holiday not found"));
        repository.delete(holiday);
    }

    private void updateHolidayFromDTO(Holiday holiday, HolidayDTO dto) {
        holiday.setHolidayName(dto.getHolidayName());
        holiday.setHolidayDate(dto.getHolidayDate());
        holiday.setIsAnnual(dto.getIsAnnual());
        holiday.setIsPaid(dto.getIsPaid());
    }
}
