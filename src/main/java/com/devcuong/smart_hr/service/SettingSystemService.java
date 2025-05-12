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
}
