package com.devcuong.smart_hr.repository;

import com.devcuong.smart_hr.Entity.SettingSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping
public interface SettingSystemRepository extends JpaRepository<SettingSystem, Integer> {

}
