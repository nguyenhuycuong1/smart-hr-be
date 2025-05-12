package com.devcuong.smart_hr.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoleDTO {
    List<String> effectiveRoles;
    List<String> assignedRoles;
    List<String> availableRoles;
}
