package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.Entity.*;
import com.devcuong.smart_hr.dto.BankInfoDTO;
import com.devcuong.smart_hr.dto.ContractDTO;
import com.devcuong.smart_hr.dto.EmployeeDTO;
import com.devcuong.smart_hr.dto.EmployeeRecordDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.enums.ContractStatus;
import com.devcuong.smart_hr.exception.AppException;
import com.devcuong.smart_hr.exception.ErrorCode;
import com.devcuong.smart_hr.repository.*;
import com.devcuong.smart_hr.utils.CodeUtils;
import com.devcuong.smart_hr.utils.POIUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmployeeService extends SearchService<Employee>{
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private BankInfoRepository bankInfoRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private JobPositionRepository jobPositionRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private SettingSystemRepository settingSystemRepository;

    @Autowired
    private ContractService contractService;

    @Autowired
    private KeycloakService keycloakService;


    public EmployeeService(EmployeeRepository employeeRepository) {
        super(employeeRepository);
    }

    public Employee createEmployee(EmployeeDTO employeeDTO) {
        SettingSystem settingSystem = settingSystemRepository.findById(1).orElse(null);
        if(settingSystem == null) {
            throw new AppException(ErrorCode.NOT_EXISTS, "Setting system does not exist");
        }

        // Kiểm tra các trường bắt buộc
        if (employeeDTO.getFirstName() == null || employeeDTO.getFirstName().trim().isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Tên không được bỏ trống!");
        }
        if (employeeDTO.getDob() == null) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Ngày sinh không được bỏ trống!");
        }
        if (employeeDTO.getHireDate() == null) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Ngày vào làm không được bỏ trống!");
        }
        if (employeeDTO.getGender() == null || employeeDTO.getGender().trim().isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Giới tính không được bỏ trống!");
        }

        Employee employee = new Employee();

        employee.setEmployeeCode("TEMP");
        employee.setEmployeeType(employeeDTO.getEmployeeType());
        employee.setFirstName(employeeDTO.getFirstName());
        employee.setLastName(employeeDTO.getLastName());
        employee.setEmail(employeeDTO.getEmail());
        employee.setPhoneNumber(employeeDTO.getPhoneNumber());
        employee.setDob(employeeDTO.getDob());
        employee.setGender(employeeDTO.getGender());
        employee.setDepartmentCode(employeeDTO.getDepartmentCode());
        employee.setAddress(employeeDTO.getAddress());
        employee.setCurrentAddress(employeeDTO.getCurrentAddress());
        employee.setHireDate(employeeDTO.getHireDate());
        employee.setJobCode(employeeDTO.getJobCode());
        employee.setTeamCode(employeeDTO.getTeamCode());
        employee.setTaxNumber(employeeDTO.getTaxNumber());
        employee.setHealthInsuranceNumber(employeeDTO.getHealthInsuranceNumber());
        employee.setSocialInsuranceCode(employeeDTO.getSocialInsuranceCode());
        employee.setMaritalStatus(employeeDTO.getMaritalStatus());
        employee.setResignDate(employeeDTO.getResignDate());
        employee.setIdentificationNumber(employeeDTO.getIdentificationNumber());
        employee.setNote(employeeDTO.getNote());
        employee.setIsActive(employeeDTO.getResignDate() == null);
        employee.setHasAccount(employeeDTO.getHasAccount());
        employee = employeeRepository.save(employee);

        // Tạo mã Nhân viên
        String prefixCode = settingSystem.getPrefixEmpCode();
        String employeeCode = CodeUtils.generateCode(prefixCode, employee.getId());

        employee.setEmployeeCode(employeeCode);

        return employeeRepository.save(employee);
    }

    public Employee getEmployeeByEmployeeCode(String employeeCode) {
        Employee employee = employeeRepository.findByEmployeeCode(employeeCode);
        if(employee == null) {
            throw new AppException(ErrorCode.NOT_EXISTS, "Employee does not exist");
        }
        return employee;
    }

    public Page<EmployeeRecordDTO> getAllEmployees(PageFilterInput<Employee> input) {
        try {
            // Sử dụng SearchService để tìm kiếm và phân trang
            Page<Employee> employeePage = super.findAll(input);

            // Chuyển đổi từ Employee sang EmployeeRecordDTO
            List<EmployeeRecordDTO> employeesDTO = employeePage.getContent()
                    .stream()
                    .map(this::convertToEmployeeRecordDTO)
                    .collect(Collectors.toList());

            return new PageImpl<>(employeesDTO, employeePage.getPageable(), employeePage.getTotalElements());
        } catch (Exception e) {
            log.error("Error retrieving employees", e);
            // Xử lý ngoại lệ và ném ra một AppException
            throw new AppException(ErrorCode.UNCATEGORIZED, "Failed to retrieve employees: " + e.getMessage());
        }
    }

    // Phương thức chuyển đổi từ Employee sang EmployeeRecordDTO
    private EmployeeRecordDTO convertToEmployeeRecordDTO(Employee employee) {
        EmployeeRecordDTO dto = new EmployeeRecordDTO();
        dto.setId(employee.getId());
        dto.setEmployeeCode(employee.getEmployeeCode());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setDob(employee.getDob());
        dto.setHireDate(employee.getHireDate());
        dto.setResignDate(employee.getResignDate());
        dto.setGender(employee.getGender());
        dto.setPhoneNumber(employee.getPhoneNumber());
        dto.setEmail(employee.getEmail());
        dto.setAddress(employee.getAddress());
        dto.setCurrentAddress(employee.getCurrentAddress());
        dto.setEmployeeType(employee.getEmployeeType());
        dto.setDepartmentCode(employee.getDepartmentCode());
        dto.setTeamCode(employee.getTeamCode());
        dto.setJobCode(employee.getJobCode());
        dto.setTaxNumber(employee.getTaxNumber());
        dto.setSocialInsuranceCode(employee.getSocialInsuranceCode());
        dto.setHealthInsuranceNumber(employee.getHealthInsuranceNumber());
        dto.setIdentificationNumber(employee.getIdentificationNumber());
        dto.setMaritalStatus(employee.getMaritalStatus());
        dto.setNote(employee.getNote());
        dto.setIsActive(employee.getResignDate() == null);

        // Lấy thông tin Department, Team, JobPosition nếu có
        if (employee.getDepartmentCode() != null) {
            Optional<Department> department = departmentRepository.findDepartmentByDepartmentCode(employee.getDepartmentCode());
            department.ifPresent(dto::setDepartment);
        }
        if (employee.getTeamCode() != null) {
            Optional<Team> team = teamRepository.findTeamByTeamCode(employee.getTeamCode());
            team.ifPresent(dto::setTeam);
        }
        if (employee.getJobCode() != null) {
            Optional<JobPosition> jobPosition = jobPositionRepository.findJobPositionByJobCode(employee.getJobCode());
            jobPosition.ifPresent(dto::setJobPosition);
        }
        List<Contract> activeContracts = contractRepository.findByEmployeeCodeAndStatusDangHoatDongOrSapHetHan(employee.getEmployeeCode());
        if (!activeContracts.isEmpty()) {
            Contract contractActive = activeContracts.get(0);
            dto.setContractActive(contractService.convertToMap(contractActive));
        }

        return dto;
    }

    public Map<String, Object> getEmployee(String employeeCode) {
        Employee employee = employeeRepository.findByEmployeeCode(employeeCode);
        if(employee == null) {
            throw new AppException(ErrorCode.NOT_EXISTS, "Employee does not exist");
        }
        List<Map<String, Object>> contracts = contractRepository.findAllByEmployeeCode(employeeCode).stream().map(contract -> {
            return contractService.convertToMap(contract);
        }).collect(Collectors.toList());
        BankInfo bankInfo = bankInfoRepository.findByEmployeeCode(employeeCode);
        Map<String, Object> result = new HashMap<>();
        result.put("employee", employee);
        result.put("contracts", contracts);
        result.put("bank_info", bankInfo);
        if(employee.getDepartmentCode() != null) {
            Optional<Department> department = departmentRepository.findDepartmentByDepartmentCode(employee.getDepartmentCode());
            result.put("department", department);
        }else{
            result.put("department", null);
        }
        if(employee.getJobCode() != null) {
            Optional<JobPosition> jobPosition = jobPositionRepository.findJobPositionByJobCode(employee.getJobCode());
            result.put("job_position", jobPosition);
        }else {
            result.put("job_position", null);
        }
        if(employee.getTeamCode() != null) {
            Optional<Team> team = teamRepository.findTeamByTeamCode(employee.getTeamCode());
            result.put("team", team);
        }else {
            result.put("team", null);
        }
        return result;
    }

    public Employee createOrUpdateEmployeeWithEmpInfo(EmployeeDTO employeeInfo) {
        if (employeeInfo.getEmployeeCode() != null && !employeeInfo.getEmployeeCode().isEmpty()) {
            return updateEmployee(employeeInfo);
        } else {
            // Create new employee
            return createEmployee(employeeInfo);
        }
    }

    public Employee updateEmployee(EmployeeDTO employeeDTO) {
        Employee employee = employeeRepository.findByEmployeeCode(employeeDTO.getEmployeeCode());
        if (employee == null) {
            throw new AppException(ErrorCode.NOT_EXISTS, "Employee does not exist");
        }

        // Kiểm tra các trường bắt buộc
        if (employeeDTO.getFirstName() == null || employeeDTO.getFirstName().trim().isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Tên không được bỏ trống!");
        }
        if (employeeDTO.getDob() == null) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Ngày sinh không được bỏ trống!");
        }
        if (employeeDTO.getHireDate() == null) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Ngày vào làm không được bỏ trống!");
        }
        if (employeeDTO.getGender() == null || employeeDTO.getGender().trim().isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Giới tính không được bỏ trống!");
        }

        employee.setEmployeeType(employeeDTO.getEmployeeType());
        employee.setFirstName(employeeDTO.getFirstName());
        employee.setLastName(employeeDTO.getLastName());
        employee.setEmail(employeeDTO.getEmail());
        employee.setPhoneNumber(employeeDTO.getPhoneNumber());
        employee.setDob(employeeDTO.getDob());
        employee.setGender(employeeDTO.getGender());
        employee.setDepartmentCode(employeeDTO.getDepartmentCode());
        employee.setAddress(employeeDTO.getAddress());
        employee.setCurrentAddress(employeeDTO.getCurrentAddress());
        employee.setHireDate(employeeDTO.getHireDate());
        employee.setJobCode(employeeDTO.getJobCode());
        employee.setTeamCode(employeeDTO.getTeamCode());
        employee.setTaxNumber(employeeDTO.getTaxNumber());
        employee.setHealthInsuranceNumber(employeeDTO.getHealthInsuranceNumber());
        employee.setSocialInsuranceCode(employeeDTO.getSocialInsuranceCode());
        employee.setMaritalStatus(employeeDTO.getMaritalStatus());
        employee.setResignDate(employeeDTO.getResignDate());
        employee.setIdentificationNumber(employeeDTO.getIdentificationNumber());
        employee.setNote(employeeDTO.getNote());
        employee.setHasAccount(employeeDTO.getHasAccount());
        if (employeeDTO.getResignDate() != null) {
            // Nếu ngày nghỉ việc được cập nhật, cần cập nhật trạng thái hợp đồng
            Contract contract = contractRepository.findByEmployeeCodeAndStatusDangHoatDongOrSapHetHan(employee.getEmployeeCode()).getFirst();
            if (contract != null) {
                contract.setStatus(ContractStatus.HETHAN);
                contract.setEndDate(employeeDTO.getResignDate());
                contractRepository.save(contract);
            }
        }
        employee.setIsActive(employeeDTO.getResignDate() == null);

        return employeeRepository.save(employee);
    }

    @Transactional
    public void deleteEmployee(String employeeCode) {
        Employee employee = employeeRepository.findByEmployeeCode(employeeCode);
        if(employee == null) {
            throw new AppException(ErrorCode.NOT_FOUND, "employee not found");
        }
        List<Contract> contracts = contractRepository.findAllByEmployeeCode(employeeCode);
        if (contracts.isEmpty()) {
            contractRepository.deleteAll(contracts);
        }
        BankInfo bankInfo = bankInfoRepository.findByEmployeeCode(employeeCode);
        if(bankInfo != null) {
            bankInfoRepository.delete(bankInfo);
        }
        // Xóa người dùng trong Keycloak
        if(employee.getHasAccount()) {
            keycloakService.deleteUserByEmployeeCode(employeeCode);
        }
        // Xóa nhân viên
        employeeRepository.delete(employee);
    }

    public Employee createEmployeeAndBankInfo(EmployeeDTO employeeDTO, BankInfoDTO bankInfoDTO) {
        Employee employee = this.createEmployee(employeeDTO);
        if(bankInfoDTO.getBankCode() != null && !bankInfoDTO.getBankCode().isEmpty()) {
            BankInfo bankInfo = new BankInfo();
            bankInfo.setBankCode(bankInfoDTO.getBankCode());
            bankInfo.setBankName(bankInfoDTO.getBankName());
            bankInfo.setEmployeeCode(employee.getEmployeeCode());
            bankInfo.setBankNumber(bankInfoDTO.getBankNumber());
            bankInfoRepository.save(bankInfo);
        }
        return employee;
    }

    public ByteArrayInputStream exportEmployeeData(PageFilterInput<Employee> input) {

        List<EmployeeRecordDTO> employees = getAllEmployees(input).getContent();

        if (employees == null || employees.isEmpty()) {
            throw new AppException(ErrorCode.NOT_FOUND, "No employee data found for export");
        }

        String[] headers = {"Mã nhân viên", "Họ", "Tên", "Ngày sinh", "Ngày vào làm",
                "Ngày nghỉ việc", "Giới tính", "SĐT", "Email", "ĐC",
                "ĐCTT", "Loại nhân viên", "Phòng ban", "Đội nhóm",
                "Chức vụ", "Mã số thuế", "BHXH", "BHYT",
                "CMND/CCCD", "Tình trạng hôn nhân", "Ghi chú", "Trạng thái" };

        try {
            // Convert employees to data array
            Object[][] data = new Object[employees.size()][headers.length];

            for (int i = 0; i < employees.size(); i++) {
                EmployeeRecordDTO employee = employees.get(i);

                // Optional lookups for related entities
                String departmentName = null;
                if (employee.getDepartmentCode() != null) {
                    Optional<Department> department = departmentRepository.findDepartmentByDepartmentCode(employee.getDepartmentCode());
                    departmentName = department.map(Department::getDepartmentName).orElse(null);
                }

                String teamName = null;
                if (employee.getTeamCode() != null) {
                    Optional<Team> team = teamRepository.findTeamByTeamCode(employee.getTeamCode());
                    teamName = team.map(Team::getTeamName).orElse(null);
                }

                String jobName = null;
                if (employee.getJobCode() != null) {
                    Optional<JobPosition> job = jobPositionRepository.findJobPositionByJobCode(employee.getJobCode());
                    jobName = job.map(JobPosition::getJobName).orElse(null);
                }

                // Fill data row
                data[i][0] = employee.getEmployeeCode();
                data[i][1] = employee.getLastName();
                data[i][2] = employee.getFirstName();
                data[i][3] = employee.getDob();
                data[i][4] = employee.getHireDate();
                data[i][5] = employee.getResignDate();
                data[i][6] = employee.getGender();
                data[i][7] = employee.getPhoneNumber();
                data[i][8] = employee.getEmail();
                data[i][9] = employee.getAddress();
                data[i][10] = employee.getCurrentAddress();
                data[i][11] = employee.getEmployeeType();
                data[i][12] = departmentName;
                data[i][13] = teamName;
                data[i][14] = jobName;
                data[i][15] = employee.getTaxNumber();
                data[i][16] = employee.getSocialInsuranceCode();
                data[i][17] = employee.getHealthInsuranceNumber();
                data[i][18] = employee.getIdentificationNumber();
                data[i][19] = employee.getMaritalStatus();
                data[i][20] = employee.getNote();
                data[i][21] = employee.getIsActive() ? "Đang làm việc" : "Đã nghỉ việc";
            }

            // Create Excel file with data
            return POIUtils.createSimpleExcel(headers, data);
        } catch (Exception e) {
            log.error("Error exporting employee data: ", e);
            throw new AppException(ErrorCode.BAD_REQUEST, "Error exporting employee data: " + e.getMessage());
        }
    }

    private Object[][] convertMapListToArray(List<Map<String, Object>> data, String[] headers) {
        Object[][] result = new Object[data.size()][headers.length];

        for (int i = 0; i < data.size(); i++) {
            Map<String, Object> row = data.get(i);
            for (int j = 0; j < headers.length; j++) {
                String key = headers[j].toLowerCase().replace(" ", "_");
                result[i][j] = row.get(key);
            }
        }

        return result;
    }


}
