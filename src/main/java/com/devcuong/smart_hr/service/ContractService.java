package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.Entity.*;
import com.devcuong.smart_hr.config.MultitenancyProperties;
import com.devcuong.smart_hr.config.TenantContext;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ContractService extends SearchService<Contract> {

    @Autowired
    ContractRepository repository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    WorkScheduleRepository workScheduleRepository;

    @Autowired
    MultitenancyProperties multitenancyProperties;

    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private JobPositionRepository jobPositionRepository;

    public ContractService(ContractRepository repository) {
        super(repository);
    }

    public Page<Map<String, Object>> getAllContracts(PageFilterInput<Contract> input) {
        try {
            // Sử dụng SearchService để tìm kiếm và phân trang
            Page<Contract> contractPage = super.findAll(input);

            List<Map<String, Object>> contracts = new ArrayList<>(contractPage.getContent()).stream().map(this::convertToMap).collect(Collectors.toList());

            return new PageImpl<>(contracts, contractPage.getPageable(), contractPage.getTotalElements());
        } catch (Exception e) {
            log.error("Error retrieving contracts", e);
            // Xử lý ngoại lệ và ném ra một AppException
            throw new AppException(ErrorCode.UNCATEGORIZED, "Failed to retrieve contracts: " + e.getMessage());
        }
    }

    public Map<String, Object> convertToMap(Contract contract) {
        Map<String, Object> contractMap = new HashMap<>();
        contractMap.put("contract_code", contract.getContractCode());
        contractMap.put("contract_name", contract.getContractName());
        contractMap.put("employee_code", contract.getEmployeeCode());
        contractMap.put("start_date", contract.getStartDate());
        contractMap.put("end_date", contract.getEndDate());
        contractMap.put("contract_type", contract.getContractType());
        contractMap.put("basic_salary", contract.getBasicSalary());
        contractMap.put("job_position", contract.getJobPosition());
        contractMap.put("pay_frequency", contract.getPayFrequency());
        contractMap.put("shift", contract.getShift());
        contractMap.put("work_schedule_id", contract.getWorkScheduleId());
        contractMap.put("type_of_work", contract.getTypeOfWork());
        contractMap.put("status", contract.getStatus());
        contractMap.put("note", contract.getNote());

        // Lấy thông tin ca làm việc từ work schedule id
        if (contract.getWorkScheduleId() != null) {
            WorkSchedule workSchedule = workScheduleRepository.findById(contract.getWorkScheduleId())
                    .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "Work schedule not found"));
            contractMap.put("work_schedule", workSchedule);
        }

        return contractMap;
    }

    public Contract createContract(ContractDTO contractDto) {
        // Kiểm tra các trường bắt buộc
        if (contractDto.getEmployeeCode() == null || contractDto.getEmployeeCode().trim().isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Mã nhân viên không được bỏ trống!");
        }
        if (contractDto.getContractType() == null || contractDto.getContractType().trim().isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Loại hợp đồng không được bỏ trống!");
        }
        if (contractDto.getStartDate() == null) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Ngày bắt đầu không được bỏ trống!");
        }
        if (contractDto.getEndDate() == null) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Ngày kết thúc không được bỏ trống!");
        }
        if (contractDto.getBasicSalary() == null || contractDto.getBasicSalary().trim().isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Lương cơ bản không được bỏ trống!");
        }
        if (contractDto.getJobPosition() == null || contractDto.getJobPosition().trim().isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Vị trí công việc không được bỏ trống!");
        }
        if (contractDto.getWorkScheduleId() == null) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Lịch làm việc không được bỏ trống!");
        }
        if (contractDto.getTypeOfWork() == null || contractDto.getTypeOfWork().trim().isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Loại công việc không được bỏ trống!");
        }
        if (contractDto.getPayFrequency() == null || contractDto.getPayFrequency().trim().isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Tần suất thanh toán không được bỏ trống!");
        }
        if (contractDto.getStatus() == null) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Trạng thái hợp đồng không được bỏ trống!");
        }
        // Kiểm tra mã nhân viên có tồn tại hay không
        Employee employee = employeeRepository.findByEmployeeCode(contractDto.getEmployeeCode());
        if (employee == null) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Mã nhân viên không tồn tại!");
        }

        checkValidStartAndEndDate(contractDto.getStartDate(), contractDto.getEndDate());

        Contract contract = new Contract();
        contract.setContractCode("TEMP");
        contract.setContractName("TEMP");
        contract.setEmployeeCode(contractDto.getEmployeeCode());
        contract.setStartDate(contractDto.getStartDate());
        contract.setEndDate(contractDto.getEndDate());
        contract.setContractType(contractDto.getContractType());
        contract.setBasicSalary(contractDto.getBasicSalary());
        contract.setJobPosition(contractDto.getJobPosition());
        contract.setPayFrequency(contractDto.getPayFrequency());
        contract.setShift(contractDto.getShift());
        contract.setWorkScheduleId(contractDto.getWorkScheduleId());
        contract.setTypeOfWork(contractDto.getTypeOfWork());
        contract.setStatus(contractDto.getStatus());
        contract.setNote(contractDto.getNote());
        contract = repository.save(contract);
        contract.setContractCode(generateContractCode(contract.getId()));
        contract.setContractName(generateContractName(contract.getEmployeeCode(), contract.getContractType()));
        // Cập nhật trạng thái cho các hợp đồng đang hoạt động trước đó
        updateExistingContractsStatus(contract.getEmployeeCode(),contract.getContractCode(), contract.getStatus());
        updateWorkInfo(contract, contract.getEmployeeCode());
        return repository.save(contract);
    }

    public String generateContractName(String employeeCode, String contractType) {
        return "Hợp đồng " + contractType + " " + employeeCode;
    }

    public String generateContractCode(Integer contractId) {
        return CodeUtils.generateCode("K", contractId);
    }

    public Contract updateContract(ContractDTO contractDto) {
        // Kiểm tra các trường bắt buộc
        if (contractDto.getContractCode() == null || contractDto.getContractCode().trim().isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Mã hợp đồng không được bỏ trống!");
        }
        if (contractDto.getEmployeeCode() == null || contractDto.getEmployeeCode().trim().isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Mã nhân viên không được bỏ trống!");
        }
        if (contractDto.getContractType() == null || contractDto.getContractType().trim().isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Loại hợp đồng không được bỏ trống!");
        }
        if (contractDto.getStartDate() == null) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Ngày bắt đầu không được bỏ trống!");
        }
        if (contractDto.getEndDate() == null) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Ngày kết thúc không được bỏ trống!");
        }
        if (contractDto.getBasicSalary() == null || contractDto.getBasicSalary().trim().isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Lương cơ bản không được bỏ trống!");
        }
        if (contractDto.getJobPosition() == null || contractDto.getJobPosition().trim().isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Vị trí công việc không được bỏ trống!");
        }
        if (contractDto.getWorkScheduleId() == null) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Lịch làm việc không được bỏ trống!");
        }
        if (contractDto.getTypeOfWork() == null || contractDto.getTypeOfWork().trim().isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Loại công việc không được bỏ trống!");
        }
        if (contractDto.getPayFrequency() == null || contractDto.getPayFrequency().trim().isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Tần suất thanh toán không được bỏ trống!");
        }
        if (contractDto.getStatus() == null) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Trạng thái hợp đồng không được bỏ trống!");
        }

        Contract contract = repository.findByContractCode(contractDto.getContractCode());
        if(contract == null) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Contract not found");
        }

        checkValidStartAndEndDate(contractDto.getStartDate(), contractDto.getEndDate());

        contract.setStartDate(contractDto.getStartDate());
        contract.setEndDate(contractDto.getEndDate());
        contract.setContractType(contractDto.getContractType());
        contract.setBasicSalary(contractDto.getBasicSalary());
        contract.setJobPosition(contractDto.getJobPosition());
        contract.setPayFrequency(contractDto.getPayFrequency());
        contract.setShift(contractDto.getShift());
        contract.setWorkScheduleId(contractDto.getWorkScheduleId());
        contract.setTypeOfWork(contractDto.getTypeOfWork());
        contract.setStatus(contractDto.getStatus());
        contract.setNote(contractDto.getNote());
        // Cập nhật trạng thái cho các hợp đồng đang hoạt động trước đó
        updateExistingContractsStatus(contract.getEmployeeCode(),contract.getContractCode(), contract.getStatus());
        updateWorkInfo(contract, contract.getEmployeeCode());
        return repository.save(contract);
    }

    public void deleteContract(String contractCode) {
        Contract contract = repository.findByContractCode(contractCode);
        if(contract == null) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Contract not found");
        }
        repository.delete(contract);
    }

    /**
     * Kiểm tra và cập nhật trạng thái của các hợp đồng hiện tại
     * Nếu hợp đồng mới có trạng thái "Đang hoạt động", các hợp đồng đang hoạt động khác của nhân viên sẽ bị hủy
     *
     * @param employeeCode Mã nhân viên
     * @param contractCode Mã hợp đồng hiện tại (để loại trừ khi cập nhật)
     * @param status Trạng thái của hợp đồng mới
     */
    private void updateExistingContractsStatus(String employeeCode, String contractCode, ContractStatus status) {
        if (status == ContractStatus.DANGHOATDONG || status == ContractStatus.SAPHETHAN) {
            List<Contract> activeContract = repository.findByEmployeeCodeAndStatusDangHoatDongOrSapHetHan(employeeCode);
            for (Contract contract : activeContract) {
                if (contract.getContractCode().equals(contractCode)) {
                    // Nếu hợp đồng hiện tại là hợp đồng đang hoạt động, không cần cập nhật
                    continue;
                }
                // Cập nhật trạng thái của các hợp đồng đang hoạt động khác thành "Đã hủy"
                contract.setStatus(ContractStatus.DAHUY);
                repository.save(contract);
                log.info("Changed contract {} status from 'Đang hoạt động' to 'Đã hủy'",
                        contract.getContractCode());
            }
        }
    }

    private void updateWorkInfo(Contract contract, String employeeCode) {
        Employee employee = employeeRepository.findByEmployeeCode(employeeCode);
        JobPosition jobPosition = jobPositionRepository.findJobPositionByJobCode(contract.getJobPosition()).orElse(null);
        if (jobPosition == null) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Không tìm thấy vị trí công việc");
        }
        if(contract.getStatus() == ContractStatus.DANGHOATDONG || contract.getStatus() == ContractStatus.SAPHETHAN) {
            employee.setEmployeeType(contract.getContractType());
            employee.setJobCode(contract.getJobPosition());
            employee.setDepartmentCode(jobPosition.getDepartmentCode());
            employeeRepository.save(employee);
        }

    }

    public void updateExpiredContracts() {
        List<Contract> expiredContracts = repository.findExpiredContracts();
        for (Contract contract : expiredContracts) {
            contract.setStatus(ContractStatus.HETHAN);
            log.info("Changed contract {} status from 'Đang hoạt động' to 'Hết hạn'",
                    contract.getContractCode());
        }
        repository.saveAll(expiredContracts);
    }

    /**
     * Cập nhật trạng thái các hợp đồng sắp hết hạn trong 7 ngày tới
     */
    public void updateSoonToExpireContracts() {
        LocalDate sevenDaysLater = LocalDate.now().plusDays(7);
        List<Contract> soonToExpireContracts = repository.findContractsExpiringWithinDays(sevenDaysLater);

        for (Contract contract : soonToExpireContracts) {
            // Chỉ cập nhật nếu trạng thái hiện tại là DANGHOATDONG
            if (contract.getStatus() == ContractStatus.DANGHOATDONG) {
                contract.setStatus(ContractStatus.SAPHETHAN);
                log.info("Changed contract {} status from 'Đang hoạt động' to 'Sắp hết hạn' (expires on {})",
                        contract.getContractCode(), contract.getEndDate());
            }
        }

        if (!soonToExpireContracts.isEmpty()) {
            repository.saveAll(soonToExpireContracts);
        }
    }

    @Scheduled(cron = "0 0 0 * * ?") // Chạy hàng ngày vào 0 giờ sáng (00:00:00)
    public void autoUpdateExpiredContracts() {
        for (String tenant : multitenancyProperties.getTenants()) {
            try {
                TenantContext.setCurrentTenant(tenant);
                updateExpiredContracts();
                updateSoonToExpireContracts(); // Thêm cập nhật hợp đồng sắp hết hạn
            }catch (Exception e) {
                log.error("Error updating contracts for tenant {}: {}", tenant, e.getMessage());
            } finally {
                TenantContext.clear();
            }
        }

    }

    public Contract findActiveContractByEmployeeCode(String employeeCode) {
        return repository.findFirstByEmployeeCodeAndIsActiveOrderByStartDateDesc(employeeCode).orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED, "No active contract found for employee"));

    }

    public List<String> getAllActiveEmployeeCodes() {
        return repository.findAllContractIsActive().stream()
                .map(Contract::getEmployeeCode)
                .collect(Collectors.toList());
    }

    private void checkValidStartAndEndDate(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Ngày bắt đầu không được sau ngày kết thúc!");
        }
    }

    public ByteArrayInputStream exportContractData(PageFilterInput<Contract> input) {
        String[] headers = {
            "Mã hợp đồng", "Tên hợp đồng", "Mã nhân viên", "Họ và tên" , "Ngày bắt đầu", "Ngày kết thúc",
            "Loại hợp đồng", "Lương cơ bản", "Vị trí công việc", "Tần suất thanh toán",
            "Ca làm việc", "Lịch làm việc", "Hình thức", "Trạng thái", "Ghi chú"
        };
        try {
            List<Contract> contracts = super.findAll(input).getContent();

            if (contracts == null || contracts.isEmpty()) {
                throw new AppException(ErrorCode.NOT_FOUND, "No contract data found for export");
            }

            // Convert contracts to data array
            Object[][] data = new Object[contracts.size()][headers.length];

            for (int i = 0; i < contracts.size(); i++) {
                Contract contract = contracts.get(i);

                // Get work schedule name
                String workScheduleName = null;
                if (contract.getWorkScheduleId() != null) {
                    WorkSchedule workSchedule = workScheduleRepository.findById(contract.getWorkScheduleId()).orElse(null);
                    if (workSchedule != null) {
                        workScheduleName = workSchedule.getScheduleName();
                    }
                }

                String employeeName = null;
                if (contract.getEmployeeCode() != null) {
                    Employee employee = employeeRepository.findByEmployeeCode(contract.getEmployeeCode());
                    if (employee != null) {
                        employeeName = employee.getLastName() + " " + employee.getFirstName();
                    }
                }

                // Fill data row
                data[i][0] = contract.getContractCode();
                data[i][1] = contract.getContractName();
                data[i][2] = contract.getEmployeeCode();
                data[i][3] = employeeName != null ? employeeName : "N/A";
                data[i][4] = contract.getStartDate();
                data[i][5] = contract.getEndDate();
                data[i][6] = contract.getContractType();
                data[i][7] = contract.getBasicSalary();
                data[i][8] = contract.getJobPosition();
                data[i][9] = contract.getPayFrequency();
                data[i][10] = contract.getShift();
                data[i][11] = workScheduleName;
                data[i][12] = contract.getTypeOfWork();
                data[i][13] = contract.getStatus().toString();
                data[i][14] = contract.getNote();
            }

            // Create Excel file with data
            return POIUtils.createSimpleExcel(headers, data);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Error exporting contract data: " + e.getMessage());
        }
    }
}
