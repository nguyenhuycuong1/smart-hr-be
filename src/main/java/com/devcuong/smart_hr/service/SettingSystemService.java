package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.Entity.SettingSystem;
import com.devcuong.smart_hr.dto.SettingSystemDTO;
import com.devcuong.smart_hr.exception.AppException;
import com.devcuong.smart_hr.exception.ErrorCode;
import com.devcuong.smart_hr.repository.SettingSystemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingSystemService {

    @Autowired
    private SettingSystemRepository settingSystemRepository;

    public SettingSystemDTO getSettingSystem() {
        SettingSystem settingSystem = settingSystemRepository.findById(1).orElse(null);
        if (settingSystem == null) {
            throw new AppException(ErrorCode.NOT_EXISTS, "Setting system does not exist");
        }
        SettingSystemDTO settingSystemDTO = new SettingSystemDTO();
        settingSystemDTO.setPrefixEmpCode(settingSystem.getPrefixEmpCode());
        settingSystemDTO.setWeekStartDate(settingSystem.getWeekStartDate());
        settingSystemDTO.setWeekEndDate(settingSystem.getWeekEndDate());
        settingSystemDTO.setLateThresholdMinutes(settingSystem.getLateThresholdMinutes());
        settingSystemDTO.setAbsentThresholdMinutes(settingSystem.getAbsentThresholdMinutes());
        return settingSystemDTO;
    }

    public SettingSystem saveSettingSystem(SettingSystemDTO settingSystemDTO) {
        SettingSystem settingSystem = settingSystemRepository.findById(1).orElse(null);
        if (settingSystem == null) {
            throw new AppException(ErrorCode.NOT_EXISTS, "Setting system does not exist");
        }
        settingSystem.setPrefixEmpCode(settingSystemDTO.getPrefixEmpCode());
        settingSystemRepository.save(settingSystem);
        return settingSystem;
    }

    public SettingSystem saveWeekday(SettingSystemDTO settingSystemDTO) {
        SettingSystem settingSystem = settingSystemRepository.findById(1).orElse(null);
        if (settingSystem == null) {
            throw new AppException(ErrorCode.NOT_EXISTS, "Setting system does not exist");
        }
        settingSystem.setWeekStartDate(settingSystemDTO.getWeekStartDate());
        settingSystem.setWeekEndDate(settingSystemDTO.getWeekEndDate());
        settingSystemRepository.save(settingSystem);
        return settingSystem;
    }

    public SettingSystem saveThreshold(SettingSystemDTO settingSystemDTO) {
        SettingSystem settingSystem = settingSystemRepository.findById(1).orElse(null);
        if (settingSystem == null) {
            throw new AppException(ErrorCode.NOT_EXISTS, "Setting system does not exist");
        }
        settingSystem.setLateThresholdMinutes(settingSystemDTO.getLateThresholdMinutes());
        settingSystem.setAbsentThresholdMinutes(settingSystemDTO.getAbsentThresholdMinutes());
        settingSystem.setEarlyLeaveThresholdMinutes(settingSystemDTO.getEarlyLeaveThresholdMinutes());
        settingSystem.setOvertimeThresholdMinutes(settingSystemDTO.getOvertimeThresholdMinutes());
        settingSystemRepository.save(settingSystem);
        return settingSystem;
    }
}
