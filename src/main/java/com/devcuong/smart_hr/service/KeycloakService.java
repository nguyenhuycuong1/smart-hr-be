package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.Entity.Employee;
import com.devcuong.smart_hr.config.TenantIdentifierResolver;
import com.devcuong.smart_hr.dto.KeycloakGroupRoleDTO;
import com.devcuong.smart_hr.dto.KeycloakUserDTO;
import com.devcuong.smart_hr.dto.RoleDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.dto.response.PageResponse;
import com.devcuong.smart_hr.exception.AppException;
import com.devcuong.smart_hr.exception.ErrorCode;
import com.devcuong.smart_hr.repository.EmployeeRepository;
import com.devcuong.smart_hr.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class KeycloakService {

    @Value("${keycloak.auth-server-url}")
    private String serverUrl;
    @Value("${keycloak.app.realm}")
    private String realm;
    @Value("${spring.security.oauth2.client.registration.oidc.client-id}")
    private String clientId;




    private final Keycloak keycloak;
    private final TenantIdentifierResolver tenantIdentifierResolver;
    private final EmployeeRepository employeeRepository;

    public KeycloakService(Keycloak keycloak, TenantIdentifierResolver tenantIdentifierResolver, EmployeeRepository employeeRepository) {
        this.keycloak = keycloak;
        this.tenantIdentifierResolver = tenantIdentifierResolver;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public UserRepresentation registerUser(KeycloakUserDTO userDTO) {
        RealmResource realmResource = keycloak.realm(realm);

        // Kiểm tra xem phần trước dấu @ trong username không được để trống
        if (userDTO.getUsername() != null && userDTO.getUsername().contains("@")) {
            String localPart = userDTO.getUsername().substring(0, userDTO.getUsername().indexOf("@"));
            if (localPart.trim().isEmpty()) {
                throw new AppException(ErrorCode.INPUT_INVALID, "Tên tài khoản không được để trống!");
            }
        }

        if (userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Mật khẩu không được để trống!");
        }

        // Kiểm tra xem người dùng đã tồn tại hay chưa
        List<UserRepresentation> existingUsers = realmResource.users().search(userDTO.getUsername());
        if (!existingUsers.isEmpty()) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Đã tồn tại tài khoản: " + userDTO.getUsername());
        }

        UserRepresentation user = new UserRepresentation();
        user.setUsername(userDTO.getUsername());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getUsername());
        user.setEnabled(true);
        user.setGroups(List.of(Objects.requireNonNull(tenantIdentifierResolver.resolveCurrentTenantIdentifier())));

        Map<String, List<String>> attributes = new HashMap<>();
        // Thêm email vào attributes
        if(userDTO.getEmail() != null) {
            attributes.put("workEmail", Collections.singletonList(userDTO.getEmail()));
        }
        // Thêm employeeCode vào attributes
        if (userDTO.getEmployeeCode() != null) {
            // Kiểm tra xem employeeCode có tồn tại trong hệ thống không
            Employee employee = employeeRepository.findByEmployeeCode(userDTO.getEmployeeCode());
            if (employee == null) {
                throw new AppException(ErrorCode.INPUT_INVALID, "Mã nhân viên không tồn tại trong hệ thống: " + userDTO.getEmployeeCode());
            }
            attributes.put("employeeCode", Collections.singletonList(userDTO.getEmployeeCode()));
            employee.setHasAccount(true);
            employeeRepository.save(employee);
        }
        user.setAttributes(attributes);

        Response response = realmResource.users().create(user);

        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        UserRepresentation createdBy = realmResource.users().search(currentUserLogin.get()).get(0);
        UserResource createdByResource = realmResource.users().get(createdBy.getId());
        // Retrieve the user's group information
        GroupRepresentation currentGroup = createdByResource.groups().stream().findFirst().orElse(null);


        String userId = CreatedResponseUtil.getCreatedId(response);
        log.info("Created user {} with id {}", user.getUsername(), userId);

        // Set password
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setValue(userDTO.getPassword());

        UserResource userResource = realmResource.users().get(userId);
        log.info("Creating password {}", userDTO.getUsername());
        userResource.resetPassword(credentialRepresentation);
        assert currentGroup != null;
        userResource.joinGroup(currentGroup.getId());

        //add role for user by group role
        if (!CollectionUtils.isEmpty(userDTO.getListGroupRoles())) {
            List<RoleRepresentation> groupRoleReps = new ArrayList<>();
            for (String group : userDTO.getListGroupRoles()) {
                groupRoleReps.add(realmResource.roles().get(group).toRepresentation());

                // Create database relation
//                if (group.contains("__group")) {
//                    UserRoleEntity userRole = new UserRoleEntity();
//                    userRole.setUserId(user.getUsername());
//                    userRole.setRoleId(group);
//                    userRole.setIsActive(true);
//                    userRoleRepository.save(userRole);
//                }
            }
            userResource.roles().realmLevel().add(groupRoleReps);
        }

        return this.getUser(userId);

    }

    @Transactional
    public UserRepresentation updateUser(String userId, KeycloakUserDTO userDTO) {
        RealmResource resource = keycloak.realm(realm);
        UserResource userResource = resource.users().get(userId);
        UserRepresentation user = userResource.toRepresentation();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getUsername());

        Map<String, List<String>> attributes = new HashMap<>();
        // Thêm email vào attributes
        if(userDTO.getEmail() != null) {
            attributes.put("workEmail", Collections.singletonList(userDTO.getEmail()));
        }
        // Thêm employeeCode vào attributes
        if (userDTO.getEmployeeCode() != null) {
            // Kiểm tra xem employeeCode có tồn tại trong hệ thống không
            Employee employee = employeeRepository.findByEmployeeCode(userDTO.getEmployeeCode());
            if (employee == null) {
                throw new AppException(ErrorCode.INPUT_INVALID, "Mã nhân viên không tồn tại trong hệ thống: " + userDTO.getEmployeeCode());
            }
            attributes.put("employeeCode", Collections.singletonList(userDTO.getEmployeeCode()));
            employee.setHasAccount(true);
            employeeRepository.save(employee);
        }
        user.setAttributes(attributes);

        userResource.update(user);

        List<RoleRepresentation> assignedRoles = userResource
                .roles().realmLevel().listAll();

        List<RoleRepresentation> rolesToRemove = assignedRoles.stream()
                .filter(role -> !role.getName().equals("default-roles-smart_hr"))
                .collect(Collectors.toList());
        
        if (!CollectionUtils.isEmpty(rolesToRemove)) {
            userResource.roles().realmLevel().remove(rolesToRemove);
        }

        //add role for user by group role
        if (!CollectionUtils.isEmpty(userDTO.getListGroupRoles())) {
            List<RoleRepresentation> groupRoleReps = new ArrayList<>();
            for (String group : userDTO.getListGroupRoles()) {
                groupRoleReps.add(resource.roles().get(group).toRepresentation());

                // Create database relation
//                if (group.contains("__group")) {
//                    UserRoleEntity userRole = new UserRoleEntity();
//                    userRole.setUserId(user.getUsername());
//                    userRole.setRoleId(group);
//                    userRole.setIsActive(true);
//                    userRoleRepository.save(userRole);
//                }
            }
            userResource.roles().realmLevel().add(groupRoleReps);
        }

        return this.getUser(userId);
    }

    public List<UserRepresentation> getUsers() {
            RealmResource realmResource = keycloak.realm(realm);
            Optional<String> optional = SecurityUtils.getCurrentUserLogin();
            String currentOrganization = "";
            currentOrganization = optional.map(s -> s.substring(s.lastIndexOf("@") + 1)).orElse("");
        return realmResource.users().search(currentOrganization);
    }

    public UserRepresentation getUser(String id) {
        RealmResource realmResource = keycloak.realm(realm);
        return realmResource.users().get(id).toRepresentation();
    }

    public UserRepresentation getUserByEmployeeCode(String employeeCode) {
        RealmResource realmResource = keycloak.realm(realm);
        List<UserRepresentation> users = realmResource.users().list();
        return users.stream()
                .filter(user -> hasEmployeeCode(user, employeeCode))
                .findFirst()
                .orElse(null);
    }

    private boolean hasEmployeeCode(UserRepresentation user, String employeeCode) {
        if (user.getAttributes() == null || employeeCode == null) {
            return false;
        }

        List<String> employeeCodes = user.getAttributes().get("employeeCode");
        return employeeCodes != null && employeeCodes.contains(employeeCode);
    }

    public void deleteUserByEmployeeCode(String employeeCode) {
        Employee employee = employeeRepository.findByEmployeeCode(employeeCode);
        employee.setHasAccount(false);
        employeeRepository.save(employee);
        UserRepresentation user = this.getUserByEmployeeCode(employeeCode);
        if (user == null) {
            throw new AppException(ErrorCode.NOT_EXISTS, "User not exists with employee code: " + employeeCode);
        }
        this.deleteUser(user.getId());
    }

    public Page<UserRepresentation> getUsers(PageFilterInput<UserRepresentation> input) {
        Pageable pageable = input.getPageSize() == 0 ? Pageable.unpaged() : PageRequest.of(input.getPageNumber(), input.getPageSize());

        RealmResource realmResource = keycloak.realm(realm);
        Optional<String> optional = SecurityUtils.getCurrentUserLogin();
        String currentOrganization = "";
        currentOrganization = optional.map(s -> s.substring(s.lastIndexOf("@") + 1)).orElse("");

        List<UserRepresentation> filterUsers = realmResource.users().search(currentOrganization);
        List<UserRepresentation> pagedUsers = pageable.isPaged() ? filterUsers.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .toList() : filterUsers;
        long totalElements = filterUsers.size();
        return new PageImpl<>(pagedUsers, pageable, totalElements);
    }

    public void deleteUser(String id) {
        RealmResource realmResource = keycloak.realm(realm);
        UserRepresentation user = this.getUser(id);
        if (user == null) {
            throw new AppException(ErrorCode.NOT_EXISTS, "User not exists");
        }

        Response response = realmResource.users().delete(id);
        if(response.getStatus() == HttpStatus.NOT_FOUND.value()) {
            throw new AppException(ErrorCode.NOT_EXISTS, "User not exists");
        }
        response.close();

    }

    public List<RoleRepresentation> getRoles() {
        String tenantId = tenantIdentifierResolver.resolveCurrentTenantIdentifier();
        RealmResource realmResource = keycloak.realm(realm);
        List<RoleRepresentation> roles = realmResource.roles().list();
        roles.removeIf(role -> {
            if (role.getName().contains("__group")) {
                return !role.getName().startsWith(tenantId + "_");
            }
            return false;
        });
        roles.sort((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
        return roles;
    }



    public RoleDTO getRoles(String userId) {
        RealmResource realmResource = keycloak.realm(realm);
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setAssignedRoles(
                realmResource
                        .users()
                        .get(userId)
                        .roles()
                        .realmLevel()
                        .listAll()
                        .stream()
                        .map(RoleRepresentation::getName)
                        .collect(Collectors.toList())
        );
        roleDTO.setEffectiveRoles(
                realmResource
                        .users()
                        .get(userId)
                        .roles()
                        .realmLevel()
                        .listEffective()
                        .stream()
                        .map(RoleRepresentation::getName)
                        .collect(Collectors.toList())
        );
        roleDTO.setAvailableRoles(
                realmResource
                        .users()
                        .get(userId)
                        .roles()
                        .realmLevel()
                        .listAvailable()
                        .stream()
                        .map(RoleRepresentation::getName)
                        .collect(Collectors.toList())
        );
        return roleDTO;
    }

    public Page<RoleRepresentation> getGroupRoles(PageFilterInput<RoleRepresentation> input) {
        Pageable pageable = input.getPageSize() == 0 ? Pageable.unpaged() : PageRequest.of(input.getPageNumber(), input.getPageSize());

        // Lấy danh sách từ Keycloak
        RealmResource realmResource = keycloak.realm(realm);
        List<RoleRepresentation> roleRepresentations = realmResource.roles().list();

        String tenantId = tenantIdentifierResolver.resolveCurrentTenantIdentifier();

        // Lọc danh sách
        List<RoleRepresentation> filteredRoles = roleRepresentations.stream()
                .filter(role -> role.getName().equals("admin_business") || role.getName().startsWith(tenantId) && role.getName().endsWith("__group") &&
                        role.getDescription().contains(input.getFilter().getDescription() != null ? input.getFilter().getDescription() : ""))
                .sorted(Comparator.comparing(RoleRepresentation::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();

        // Áp dụng phân trang
        List<RoleRepresentation> pagedRoles = pageable.isPaged() ? filteredRoles.stream()
                .skip(pageable.getOffset())  // Bỏ qua các phần tử trước đó
                .limit(pageable.getPageSize())
                .toList() : filteredRoles;


        // Tổng số phần tử sau khi lọc (không bị ảnh hưởng bởi skip/limit)
        long totalElements = filteredRoles.size();

        return new PageImpl<RoleRepresentation>(pagedRoles, pageable, totalElements);
    }

    public List<RoleRepresentation> getRolesByGroupCode(String groupCode) {
        RealmResource realmResource = keycloak.realm(realm);
        RoleResource roleResource = realmResource.roles().get(groupCode);
        return roleResource.getRoleComposites().stream().toList();
    }


    public void createGroupRole(KeycloakGroupRoleDTO groupRoleDTO) {
        if (groupRoleDTO.getDescription() == null || groupRoleDTO.getDescription().isEmpty()) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Tên nhóm quyền không được để trống!");
        }
        if (groupRoleDTO.getName() == null || groupRoleDTO.getName().trim().isEmpty()) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Mã nhóm quyền không được để trống!");
        }

        String groupRoleName = tenantIdentifierResolver.resolveCurrentTenantIdentifier() + "_" + groupRoleDTO.getName().trim() + "__group";
        RealmResource realmResource = keycloak.realm(realm);

        try {
            RoleRepresentation groupRoleRepresentation = new RoleRepresentation();
            groupRoleRepresentation.setName(groupRoleName);
            groupRoleRepresentation.setDescription(groupRoleDTO.getDescription());
            groupRoleRepresentation.setComposite(true);



            realmResource.roles().create(groupRoleRepresentation);
            groupRoleRepresentation = realmResource.roles().get(groupRoleName).toRepresentation();
            List<RoleRepresentation> childrenRoles = new ArrayList<>();
            groupRoleDTO.getRoles().forEach(role -> {childrenRoles.add(realmResource.roles().get(role).toRepresentation());});

            realmResource.rolesById().addComposites(groupRoleRepresentation.getId(), childrenRoles);

        } catch (Exception e) {
            throw new AppException(ErrorCode.INPUT_INVALID);
        }
    }

    public void updateGroupRole(String groupCode, KeycloakGroupRoleDTO groupRoleDTO) {
        if (groupRoleDTO.getDescription() == null || groupRoleDTO.getDescription().isEmpty()) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Tên nhóm quyền không được để trống!");
        }
        if (groupRoleDTO.getName() == null || groupRoleDTO.getName().trim().isEmpty()) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Mã nhóm quyền không được để trống!");
        }
        RealmResource realmResource = keycloak.realm(realm);

        try {
            RoleRepresentation roleRepresentation = realmResource.roles().get(groupCode).toRepresentation();
            roleRepresentation.setDescription(groupRoleDTO.getDescription());

            List<RoleRepresentation> childrenRoles = new ArrayList<>();
            groupRoleDTO.getRoles().forEach(role -> {childrenRoles.add(realmResource.roles().get(role).toRepresentation());});
            Set<RoleRepresentation> toDelete = realmResource.rolesById().getRoleComposites(roleRepresentation.getId());
            realmResource.rolesById().deleteComposites(roleRepresentation.getId(), new ArrayList<>(toDelete));
            realmResource.rolesById().addComposites(roleRepresentation.getId(), childrenRoles);
        } catch (Exception e) {
            throw new AppException(ErrorCode.INPUT_INVALID);
        }


    }

    public void deleteGroupRole(String groupCode) {
        RealmResource realmResource = keycloak.realm(realm);
        RoleResource roleResource = realmResource.roles().get(groupCode);
        try {
            realmResource.rolesById().deleteRole(roleResource.toRepresentation().getId());
        } catch (Exception e) {
            throw new AppException(ErrorCode.INPUT_INVALID);
        }
    }

}
