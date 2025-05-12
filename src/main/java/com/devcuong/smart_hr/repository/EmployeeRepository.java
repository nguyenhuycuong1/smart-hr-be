package com.devcuong.smart_hr.repository;

import com.devcuong.smart_hr.Entity.Employee;
import com.devcuong.smart_hr.dto.EmployeeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
    Employee findByEmployeeCode(String employeeCode);

    // Tìm kiếm theo common search term
    @Query("SELECT e FROM Employee e WHERE " +
            "LOWER(e.employeeCode) LIKE :searchTerm OR " +
            "LOWER(e.firstName) LIKE :searchTerm OR " +
            "LOWER(e.lastName) LIKE :searchTerm OR " +
            "LOWER(e.email) LIKE :searchTerm OR " +
            "LOWER(e.phoneNumber) LIKE :searchTerm OR " +
            "LOWER(e.departmentCode) LIKE :searchTerm OR " +
            "LOWER(e.jobCode) LIKE :searchTerm OR " +
            "LOWER(e.teamCode) LIKE :searchTerm OR " +
            "LOWER(e.taxNumber) LIKE :searchTerm OR " +
            "LOWER(e.healthInsuranceNumber) LIKE :searchTerm OR " +
            "LOWER(e.socialInsuranceCode) LIKE :searchTerm OR " +
            "LOWER(e.identificationNumber) LIKE :searchTerm OR " +
            "LOWER(CAST(e.employeeType AS string)) LIKE :searchTerm OR " +
            "LOWER(CAST(e.gender AS string)) LIKE :searchTerm OR " +
            "LOWER(CAST(e.maritalStatus AS string)) LIKE :searchTerm OR " +
            "LOWER(e.address) LIKE :searchTerm OR " +
            "LOWER(e.currentAddress) LIKE :searchTerm OR " +
            "LOWER(e.note) LIKE :searchTerm")
    Page<Employee> findByCommonSearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Tìm kiếm theo filter sử dụng Specification
    default Page<Employee> findWithFilter(EmployeeDTO filter, Pageable pageable) {
        return findAll((Specification<Employee>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Thêm các điều kiện tìm kiếm dựa trên các trường của filter
            if (filter != null) {
                if (StringUtils.hasText(filter.getEmployeeCode())) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("employeeCode")),
                            "%" + filter.getEmployeeCode().toLowerCase() + "%"
                    ));
                }

                if (StringUtils.hasText(filter.getFirstName())) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("firstName")),
                            "%" + filter.getFirstName().toLowerCase() + "%"
                    ));
                }

                if (StringUtils.hasText(filter.getLastName())) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("lastName")),
                            "%" + filter.getLastName().toLowerCase() + "%"
                    ));
                }

                if (StringUtils.hasText(filter.getEmail())) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("email")),
                            "%" + filter.getEmail().toLowerCase() + "%"
                    ));
                }

                if (StringUtils.hasText(filter.getPhoneNumber())) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("phoneNumber")),
                            "%" + filter.getPhoneNumber().toLowerCase() + "%"
                    ));
                }

                if (filter.getDob() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("dob"), filter.getDob()));
                }

                if (StringUtils.hasText(filter.getGender())) {
                    predicates.add(criteriaBuilder.equal(root.get("gender"), filter.getGender()));
                }

                if (StringUtils.hasText(filter.getDepartmentCode())) {
                    predicates.add(criteriaBuilder.equal(root.get("departmentCode"), filter.getDepartmentCode()));
                }

                if (StringUtils.hasText(filter.getJobCode())) {
                    predicates.add(criteriaBuilder.equal(root.get("jobCode"), filter.getJobCode()));
                }

                if (StringUtils.hasText(filter.getTeamCode())) {
                    predicates.add(criteriaBuilder.equal(root.get("teamCode"), filter.getTeamCode()));
                }

                if (StringUtils.hasText(filter.getEmployeeType())) {
                    predicates.add(criteriaBuilder.equal(root.get("employeeType"), filter.getEmployeeType()));
                }

                if (filter.getHireDate() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("hireDate"), filter.getHireDate()));
                }

                if (filter.getResignDate() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("resignDate"), filter.getResignDate()));
                }

                if (StringUtils.hasText(filter.getAddress())) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("address")),
                            "%" + filter.getAddress().toLowerCase() + "%"
                    ));
                }

                if (StringUtils.hasText(filter.getCurrentAddress())) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("currentAddress")),
                            "%" + filter.getCurrentAddress().toLowerCase() + "%"
                    ));
                }

                if (StringUtils.hasText(filter.getTaxNumber())) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("taxNumber")),
                            "%" + filter.getTaxNumber().toLowerCase() + "%"
                    ));
                }

                if (StringUtils.hasText(filter.getHealthInsuranceNumber())) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("healthInsuranceNumber")),
                            "%" + filter.getHealthInsuranceNumber().toLowerCase() + "%"
                    ));
                }

                if (StringUtils.hasText(filter.getSocialInsuranceCode())) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("socialInsuranceCode")),
                            "%" + filter.getSocialInsuranceCode().toLowerCase() + "%"
                    ));
                }

                if (StringUtils.hasText(filter.getIdentificationNumber())) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("identificationNumber")),
                            "%" + filter.getIdentificationNumber().toLowerCase() + "%"
                    ));
                }

                if (StringUtils.hasText(filter.getMaritalStatus())) {
                    predicates.add(criteriaBuilder.equal(root.get("maritalStatus"), filter.getMaritalStatus()));
                }

                if (StringUtils.hasText(filter.getNote())) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("note")),
                            "%" + filter.getNote().toLowerCase() + "%"
                    ));
                }
            }

            return predicates.isEmpty() ? null : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }
}