package com.devcuong.smart_hr.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "setting_system")
public class SettingSystem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @JsonProperty("prefix_emp_code")
    @Column(name = "prefix_emp_code", nullable = false)
    String prefixEmpCode;

    public SettingSystem() {

    }

    public SettingSystem(String prefixEmpCode) {
        this.prefixEmpCode = prefixEmpCode;
    }
}
