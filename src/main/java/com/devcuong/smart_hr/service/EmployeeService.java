package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.Entity.*;
import com.devcuong.smart_hr.dto.BankInfoDTO;
import com.devcuong.smart_hr.dto.ContractDTO;
import com.devcuong.smart_hr.dto.EmployeeDTO;
import com.devcuong.smart_hr.dto.EmployeeRecordDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.exception.AppException;
import com.devcuong.smart_hr.exception.ErrorCode;
import com.devcuong.smart_hr.repository.*;
import com.devcuong.smart_hr.utils.CodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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


    public EmployeeService(EmployeeRepository employeeRepository) {
        super(employeeRepository);
    }

    public Employee createEmployee(EmployeeDTO employeeDTO) {
        SettingSystem settingSystem = settingSystemRepository.findById(1).orElse(null);
        if(settingSystem == null) {
            throw new AppException(ErrorCode.NOT_EXISTS, "Setting system does not exist");
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
        Contract contractActive = contractRepository.findByEmployeeCodeAndStatus(employee.getEmployeeCode(), "Đang hoạt động");
        if(contractActive != null) {
            dto.setContractActive(contractActive);
        }

        return dto;
    }

    public Map<String, Object> getEmployee(String employeeCode) {
        Employee employee = employeeRepository.findByEmployeeCode(employeeCode);
        if(employee == null) {
            throw new AppException(ErrorCode.NOT_EXISTS, "Employee does not exist");
        }
        List<Contract> contracts = contractRepository.findAllByEmployeeCode(employeeCode);
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

        return employeeRepository.save(employee);
    }

    public void deleteEmployee(String employeeCode) {
        Employee employee = employeeRepository.findByEmployeeCode(employeeCode);
        if(employee == null) {
            throw new AppException(ErrorCode.NOT_FOUND, "employee not found");
        }
        BankInfo bankInfo = bankInfoRepository.findByEmployeeCode(employeeCode);
        if(bankInfo != null) {
            bankInfoRepository.delete(bankInfo);
        }
        employeeRepository.delete(employee);
    }

    public Employee createEmployeeAndBankInfo(EmployeeDTO employeeDTO, BankInfoDTO bankInfoDTO) {
        Employee employee = this.createEmployee(employeeDTO);
        BankInfo bankInfo = new BankInfo();
        bankInfo.setBankCode(bankInfoDTO.getBankCode());
        bankInfo.setBankName(bankInfoDTO.getBankName());
        bankInfo.setEmployeeCode(employee.getEmployeeCode());
        bankInfo.setBankNumber(bankInfoDTO.getBankNumber());
        bankInfoRepository.save(bankInfo);
        return employee;
    }
}