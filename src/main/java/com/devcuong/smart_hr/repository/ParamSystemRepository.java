package com.devcuong.smart_hr.repository;

import com.devcuong.smart_hr.Entity.ParamSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParamSystemRepository extends JpaRepository<ParamSystem, Integer> {
    List<ParamSystem> findByTableNameAndColumnName(String tableName, String columnName);
    ParamSystem findByTableNameAndColumnNameAndValue(String tableName, String columnName, String value);
}
