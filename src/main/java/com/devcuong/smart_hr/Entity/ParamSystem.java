package com.devcuong.smart_hr.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "param_system")
public class ParamSystem {

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

}
