package com.devcuong.smart_hr.dto;

import com.devcuong.smart_hr.Entity.ParamSystem;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParamSystemDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    Integer id;
    @JsonProperty("table_name")
    @Column(nullable = false, name = "table_name")
    String tableName;
    @JsonProperty("column_name")
    @Column(nullable = false, name = "column_name")
    String columnName;
    @Column(nullable = false)
    String value;
    String description;

    public ParamSystemDTO(Integer id, String tableName, String columnName, String value, String description) {
        this.id = id;
        this.tableName = tableName;
        this.columnName = columnName;
        this.value = value;
        this.description = description;
    }

    public ParamSystemDTO() {}

    public ParamSystem toEntity() {
        ParamSystem entity = new ParamSystem();
        entity.setId(id);
        entity.setTableName(tableName);
        entity.setColumnName(columnName);
        entity.setValue(value);
        entity.setDescription(description);
        return entity;
    }
}
