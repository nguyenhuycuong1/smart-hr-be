package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.Entity.Contract;
import com.devcuong.smart_hr.Entity.Employee;
import com.devcuong.smart_hr.dto.ContractDTO;
import com.devcuong.smart_hr.dto.EmployeeDTO;
import com.devcuong.smart_hr.dto.EmployeeRecordDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.exception.AppException;
import com.devcuong.smart_hr.exception.ErrorCode;
import com.devcuong.smart_hr.repository.ContractRepository;
import com.devcuong.smart_hr.utils.CodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ContractService extends SearchService<Contract> {

    @Autowired
    ContractRepository repository;

    @Autowired
    EmployeeService employeeService;

    public ContractService(ContractRepository repository) {
        super(repository);
    }

    public Page<Contract> getAllContracts(PageFilterInput<Contract> input) {
        try {
            // Sử dụng SearchService để tìm kiếm và phân trang
            Page<Contract> contractPage = super.findAll(input);

            List<Contract> contracts = new ArrayList<>(contractPage.getContent());

            return new PageImpl<>(contracts, contractPage.getPageable(), contractPage.getTotalElements());
        } catch (Exception e) {
            log.error("Error retrieving contracts", e);
            // Xử lý ngoại lệ và ném ra một AppException
            throw new AppException(ErrorCode.UNCATEGORIZED, "Failed to retrieve contracts: " + e.getMessage());
        }
    }

    public Contract createContract(ContractDTO contractDto) {
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
        Contract contract = repository.findByContractCode(contractDto.getContractCode());
        if(contract == null) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "Contract not found");
        }
        contract.setStartDate(contractDto.getStartDate());
        contract.setEndDate(contractDto.getEndDate());
        contract.setContractType(contractDto.getContractType());
        contract.setBasicSalary(contractDto.getBasicSalary());
        contract.setJobPosition(contractDto.getJobPosition());
        contract.setPayFrequency(contractDto.getPayFrequency());
        contract.setShift(contractDto.getShift());
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
    private void updateExistingContractsStatus(String employeeCode, String contractCode, String status) {
        if ("Đang hoạt động".equals(status)) {
            List<Contract> activeContracts = repository.findListByEmployeeCodeAndStatus(employeeCode, "Đang hoạt động");

            for (Contract existingContract : activeContracts) {
                // Bỏ qua hợp đồng hiện tại nếu đang cập nhật (không phải tạo mới)
                if (contractCode != null && contractCode.equals(existingContract.getContractCode())) {
                    continue;
                }

                existingContract.setStatus("Đã hủy");
                repository.save(existingContract);
                log.info("Changed contract {} status from 'Đang hoạt động' to 'Đã hủy'",
                        existingContract.getContractCode());
            }
        }
    }

    private void updateWorkInfo(Contract contract, String employeeCode) {
        Employee employee = employeeService.getEmployeeByEmployeeCode(employeeCode);
        if("Đang hoạt động".equals(contract.getStatus())) {
            employee.setEmployeeType(contract.getContractType());
            employee.setJobCode(contract.getJobPosition());
            EmployeeDTO employeeDTO = EmployeeDTO.toDTO(employee);
            employeeService.updateEmployee(employeeDTO);
        }

    }

    public void updateExpiredContracts() {
        List<Contract> expiredContracts = repository.findExpiredContracts();
        for (Contract contract : expiredContracts) {
            contract.setStatus("Hết hạn");
            log.info("Changed contract {} status from 'Đang hoạt động' to 'Hết hạn'",
                    contract.getContractCode());
        }
        repository.saveAll(expiredContracts);
    }

    @Scheduled(cron = "0 0 0 * * ?") // Chạy hàng ngày vào 0 giờ sáng (00:00:00)
    public void autoUpdateExpiredContracts() {
        updateExpiredContracts();
    }


}
