package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.Entity.ParamSystem;
import com.devcuong.smart_hr.dto.ParamSystemDTO;
import com.devcuong.smart_hr.exception.AppException;
import com.devcuong.smart_hr.exception.ErrorCode;
import com.devcuong.smart_hr.repository.ParamSystemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ParamSystemService {
    @Autowired
    private ParamSystemRepository repo;

    public List<ParamSystem> getParams(String tableName, String columnName) {
        return repo.findByTableNameAndColumnName(tableName, columnName);
    }

    public List<ParamSystem> addParams(ParamSystemDTO params) {
        ParamSystem existParam =
                repo.findByTableNameAndColumnNameAndValue(
                        params.getTableName(),
                        params.getColumnName(),
                        params.getValue()
                );
        if (existParam != null) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Giá trị đã tồn tại");
        }
        ParamSystem newParam = new ParamSystem();
        newParam.setTableName(params.getTableName());
        newParam.setColumnName(params.getColumnName());
        newParam.setValue(params.getValue());
        newParam.setDescription(params.getDescription());
        repo.save(newParam);
        return this.getParams(params.getTableName(), params.getColumnName());
    }

    public void deleteParams(Integer id) {
        repo.deleteById(id);
    }

    public List<ParamSystem> createBatchParam(List<ParamSystemDTO> params) {
        List<ParamSystem> processedParams = new ArrayList<>();

        for (ParamSystemDTO param : params) {
            if (param.getId() != null) {
                // Nếu có ID → Cập nhật bản ghi hiện có
                Optional<ParamSystem> existingParamOpt = repo.findById(param.getId());

                if (existingParamOpt.isPresent()) {
                    ParamSystem existingParam = existingParamOpt.get();
                    existingParam.setTableName(param.getTableName());
                    existingParam.setColumnName(param.getColumnName());
                    existingParam.setValue(param.getValue());
                    existingParam.setDescription(param.getDescription());
                    repo.save(existingParam);
                    processedParams.add(existingParam);
                } else {
                    throw new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy bản ghi có ID: " + param.getId());
                }
            } else {
                // Nếu không có ID → Thêm mới
                ParamSystem newParam = new ParamSystem();
                newParam.setTableName(param.getTableName());
                newParam.setColumnName(param.getColumnName());
                newParam.setValue(param.getValue());
                newParam.setDescription(param.getDescription());
                repo.save(newParam);
                processedParams.add(newParam);
            }
        }

        return processedParams;
    }

}
