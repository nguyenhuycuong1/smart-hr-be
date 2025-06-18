package com.devcuong.smart_hr.controller;

import com.devcuong.smart_hr.dto.KeycloakGroupRoleDTO;
import com.devcuong.smart_hr.dto.KeycloakUserDTO;
import com.devcuong.smart_hr.dto.RoleDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.dto.response.ApiResponse;
import com.devcuong.smart_hr.dto.response.PageResponse;
import com.devcuong.smart_hr.dto.response.Result;
import com.devcuong.smart_hr.service.KeycloakService;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class KeycloakController {

    private final KeycloakService keycloakService;

    public KeycloakController(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    @PostMapping("/users/register")
    public ApiResponse registerUser(@RequestBody KeycloakUserDTO user) {
        return ApiResponse.<UserRepresentation>builder()
                .data(keycloakService.registerUser(user))
                .build().success("Đăng ký tài khoản thành công!");
    }

    @PutMapping("/users/update/{userId}")
    public ApiResponse updateUser(@PathVariable String userId, @RequestBody KeycloakUserDTO user) {
        return ApiResponse.<UserRepresentation>builder()
                .data(keycloakService.updateUser(userId, user))
                .build().success();
    }

    @GetMapping("/users/{userId}")
    public ApiResponse getUser(@PathVariable String userId) {
        return ApiResponse.builder().data(keycloakService.getUser(userId)).build().success();
    }

    @GetMapping("/users/by-employee-code/{employeeCode}")
    public ApiResponse getUserByEmployeeCode(@PathVariable String employeeCode) {
        return ApiResponse.builder().data(keycloakService.getUserByEmployeeCode(employeeCode)).build().success();
    }

    @PostMapping("/users")
    public PageResponse getAllUsers(@RequestBody PageFilterInput<UserRepresentation> input) {
        Page<UserRepresentation> page = keycloakService.getUsers(input);
        return PageResponse.<List<UserRepresentation>>builder()
                .data(page.getContent())
                .dataCount(page.getTotalElements())
                .build().success();
    }

    @DeleteMapping("/users/delete/{userId}")
    public ApiResponse deleteUser(@PathVariable String userId) {
        keycloakService.deleteUser(userId);
        return ApiResponse.builder().build().success();
    }

    @GetMapping("/roles")
    public ApiResponse<List<RoleRepresentation>> getAllRoles() {
        return ApiResponse.<List<RoleRepresentation>>builder()
                .result(new Result("Thành công", 200, true))
                .data(keycloakService.getRoles())
                .build();
    }

    @GetMapping("/roles/{userId}")
    public ApiResponse getRolesByUserId(@PathVariable String userId) {
        return ApiResponse.<RoleDTO>builder()
                .data(keycloakService.getRoles(userId))
                .build().success();
    }

    @PostMapping("/groups")
    public PageResponse getAllGroupRoles(@RequestBody PageFilterInput<RoleRepresentation> input) {
        Page<RoleRepresentation> roleRepresentations = keycloakService.getGroupRoles(input);
        return PageResponse.<List<RoleRepresentation>>builder()
                .data(roleRepresentations.getContent())
                .dataCount(roleRepresentations.getTotalElements())
                .build().success();
    }

    @GetMapping("/groups/roles/{groupCode}")
    public ApiResponse getAllRolesByGroupCode(@PathVariable String groupCode) {
        return ApiResponse.<List<RoleRepresentation>>builder()
                .data(keycloakService.getRolesByGroupCode(groupCode))
                .build().success();
    }

    @PostMapping("/groups/create")
    public ApiResponse createGroup(@RequestBody KeycloakGroupRoleDTO group) {
        keycloakService.createGroupRole(group);
        return ApiResponse.builder().build().success();
    }

    @PutMapping("/groups/roles/{groupCode}")
    public ApiResponse updateGroupRole(@PathVariable String groupCode, @RequestBody KeycloakGroupRoleDTO role) {
        keycloakService.updateGroupRole(groupCode,role);
        return ApiResponse.builder().build().success();
    }

    @DeleteMapping("/groups/roles/{groupCode}")
    public ApiResponse deleteGroupRole(@PathVariable String groupCode) {
        keycloakService.deleteGroupRole(groupCode);
        return ApiResponse.builder().build().success();
    }

}
