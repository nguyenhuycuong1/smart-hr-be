package com.devcuong.smart_hr.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KeycloakUserDTO {

    @NotNull
    String username;
    String password;
    String firstName;
    String lastName;
    List<String> listGroupRoles;

    String employeeCode;
    String email;
}
