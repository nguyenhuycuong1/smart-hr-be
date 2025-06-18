package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.Entity.*;
import com.devcuong.smart_hr.enums.ApprovalStatus;
import com.devcuong.smart_hr.enums.AttendanceStatus;
import com.devcuong.smart_hr.enums.ContractStatus;
import com.devcuong.smart_hr.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.time.Period;

@Service
public class DashboardService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private JobPositionRepository jobPositionRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private AttendanceRecordRepository attendanceRecordRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    public Map<String, Object> getDashboardPersonnelScreen(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> dashboardData = new HashMap<>();

        Map<String, Object> employeeStats = getEmployeeStatistics(startDate, endDate);
        dashboardData.put("overview_card", employeeStats);

        List<Map<String, Object>> employeeCountByDepartment = getEmployeeCountByDepartment();
        dashboardData.put("employee_count_by_department", employeeCountByDepartment);

        List<Map<String, Object>> employeeCountByJobPosition = getEmployeeCountByJobPosition();
        dashboardData.put("employee_count_by_job_position", employeeCountByJobPosition);

        List<Map<String, Object>> employeeMovementByMonth = getEmployeeMovementByMonth(endDate);
        dashboardData.put("personnel_trend", employeeMovementByMonth);

        List<Map<String, Object>> employeeMovementByDepartment = getEmployeeMovementByDepartment(startDate, endDate);
        dashboardData.put("department_change", employeeMovementByDepartment);

        // Add age and gender distribution data to dashboard
        Map<String, Object> ageGenderDistribution = getEmployeeAgeGenderDistribution();
        dashboardData.put("age_gender_distribution", ageGenderDistribution);

        List<Map<String, Object>> personnelCostsByDepartment = getPersonnelCostsByDepartment();
        dashboardData.put("personnel_costs_by_department", personnelCostsByDepartment);

        // Add personnel cost trends for the 5 most recent quarters
        List<Map<String, Object>> personnelCostTrends = getPersonnelCostTrends();
        dashboardData.put("personnel_cost_trends", personnelCostTrends);

        return dashboardData;
    }

    /**
     * Returns employee statistics based on the provided time frame
     *
     * @param startDate The start date for calculating new employees, resigned employees, and retention rate
     * @param endDate The end date for calculating new employees, resigned employees, and retention rate
     * @return A map containing employee statistics:
     *         - totalActiveEmployees: total number of employees without a resignation date
     *         - newEmployees: number of employees hired in the given period
     *         - resignedEmployees: number of employees who resigned in the given period
     *         - retentionRate: employee retention rate as a percentage for the given period
     */
    public Map<String, Object> getEmployeeStatistics(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> statistics = new HashMap<>();

        // Get all employees
        List<Employee> allEmployees = employeeRepository.findAll();

        // Calculate total active employees (employees without resignDate)
        long totalActiveEmployees = allEmployees.stream()
                .filter(employee -> employee.getResignDate() == null)
                .count();

        // Calculate new employees in the given period
        long newEmployees = allEmployees.stream()
                .filter(employee -> employee.getHireDate() != null
                        && !employee.getHireDate().isBefore(startDate)
                        && !employee.getHireDate().isAfter(endDate))
                .count();

        // Calculate resigned employees in the given period
        long resignedEmployees = allEmployees.stream()
                .filter(employee -> employee.getResignDate() != null
                        && !employee.getResignDate().isBefore(startDate)
                        && !employee.getResignDate().isAfter(endDate))
                .count();

        // Calculate retention rate
        // Formula: (1 - (resigned employees / (total employees at start + new employees))) * 100
        long employeesAtStartPeriod = allEmployees.stream()
                .filter(employee -> employee.getHireDate() != null
                        && employee.getHireDate().isBefore(startDate)
                        && (employee.getResignDate() == null
                            || employee.getResignDate().isAfter(startDate)))
                .count();

        double retentionRate = 0.0;
        if (employeesAtStartPeriod + newEmployees > 0) {
            retentionRate = (1.0 - (double) resignedEmployees / (employeesAtStartPeriod + newEmployees)) * 100;
        }

        // Populate the statistics map
        statistics.put("total_active_employees", totalActiveEmployees);
        statistics.put("new_employees", newEmployees);
        statistics.put("resigned_employees", resignedEmployees);
        statistics.put("retention_rate", retentionRate);

        return statistics;
    }

    /**
     * Returns the count of employees in each department
     *
     * @return A list of maps, each containing:
     *         - "name": the department name
     *         - "value": the number of employees in that department
     */
    public List<Map<String, Object>> getEmployeeCountByDepartment() {
        List<Map<String, Object>> result = new ArrayList<>();

        // Get all departments
        List<Department> departments = departmentRepository.findAll();

        // Get all employees
        List<Employee> allEmployees = employeeRepository.findAll();

        // For each department, count active employees
        for (Department department : departments) {
            String departmentCode = department.getDepartmentCode();

            // Count employees in this department (only active employees without resignDate)
            long employeeCount = allEmployees.stream()
                    .filter(employee -> departmentCode.equals(employee.getDepartmentCode())
                            && employee.getResignDate() == null)
                    .count();

            // Create a map with department name and employee count
            Map<String, Object> departmentData = new HashMap<>();
            departmentData.put("name", department.getDepartmentName());
            departmentData.put("value", employeeCount);

            result.add(departmentData);
        }

        return result;
    }

    /**
     * Returns the count of employees in each job position
     *
     * @return A list of maps, each containing:
     *         - "name": the job position name
     *         - "value": the number of employees in that job position
     */
    public List<Map<String, Object>> getEmployeeCountByJobPosition() {
        List<Map<String, Object>> result = new ArrayList<>();

        // Get all job positions
        List<JobPosition> jobPositions = jobPositionRepository.findAll();

        // Get all employees
        List<Employee> allEmployees = employeeRepository.findAll();

        // For each job position, count active employees
        for (JobPosition jobPosition : jobPositions) {
            String jobCode = jobPosition.getJobCode();

            // Count employees in this job position (only active employees without resignDate)
            long employeeCount = allEmployees.stream()
                    .filter(employee -> jobCode.equals(employee.getJobCode())
                            && employee.getResignDate() == null)
                    .count();

            // Create a map with job position name and employee count
            Map<String, Object> jobPositionData = new HashMap<>();
            jobPositionData.put("name", jobPosition.getJobName());
            jobPositionData.put("value", employeeCount);

            result.add(jobPositionData);
        }

        return result;
    }

    /**
     * Returns employee movement data for the 12 months of the current year
     *
     * @return A list of maps, each containing:
     *         - "month": the month name (January, February, etc.)
     *         - "totalEmployees": total number of employees at the end of that month
     *         - "newEmployees": number of new employees hired in that month
     *         - "resignedEmployees": number of employees who resigned in that month
     */
    public List<Map<String, Object>> getEmployeeMovementByMonth(LocalDate date) {
        List<Map<String, Object>> result = new ArrayList<>();

        // Get current year
        int currentYear = date.getYear();

        // Get all employees
        List<Employee> allEmployees = employeeRepository.findAll();

        // For each month of the current year
        for (int month = 1; month <= 12; month++) {
            // Calculate start and end date of the month
            LocalDate startOfMonth = LocalDate.of(currentYear, month, 1);
            LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);

            // Count new employees hired in this month
            long newEmployeesCount = allEmployees.stream()
                    .filter(employee -> employee.getHireDate() != null
                            && !employee.getHireDate().isBefore(startOfMonth)
                            && !employee.getHireDate().isAfter(endOfMonth))
                    .count();

            // Count employees who resigned in this month
            long resignedEmployeesCount = allEmployees.stream()
                    .filter(employee -> employee.getResignDate() != null
                            && !employee.getResignDate().isBefore(startOfMonth)
                            && !employee.getResignDate().isAfter(endOfMonth))
                    .count();

            // Count total employees at the end of the month
            // (hired on or before this month's end date AND either still employed or resigned after this month)
            long totalEmployeesCount = allEmployees.stream()
                    .filter(employee -> employee.getHireDate() != null
                            && !employee.getHireDate().isAfter(endOfMonth)
                            && (employee.getResignDate() == null || employee.getResignDate().isAfter(endOfMonth)))
                    .count();

            // Create month data map
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", startOfMonth.getMonth().toString());
            monthData.put("total_employees", totalEmployeesCount);
            monthData.put("new_employees", newEmployeesCount);
            monthData.put("resigned_employees", resignedEmployeesCount);

            result.add(monthData);
        }

        return result;
    }

    /**
     * Returns employee turnover data by department
     *
     * @param startDate The start date for calculating new employees and resigned employees
     * @param endDate The end date for calculating new employees and resigned employees
     * @return A list of maps, each containing:
     *         - "department_name": the name of the department
     *         - "current_employees": total number of active employees in the department
     *         - "new_employees": number of new employees hired in the department during the period
     *         - "resigned_employees": number of employees who resigned from the department during the period
     */
    public List<Map<String, Object>> getEmployeeMovementByDepartment(LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> result = new ArrayList<>();

        // Get all departments
        List<Department> departments = departmentRepository.findAll();

        // Get all employees
        List<Employee> allEmployees = employeeRepository.findAll();

        // For each department, calculate employee statistics
        for (Department department : departments) {
            String departmentCode = department.getDepartmentCode();
            Map<String, Object> departmentData = new HashMap<>();

            // Count current active employees in this department
            long currentEmployeesCount = allEmployees.stream()
                    .filter(employee -> departmentCode.equals(employee.getDepartmentCode())
                            && employee.getResignDate() == null)
                    .count();

            // Count new employees hired in this department during the period
            long newEmployeesCount = allEmployees.stream()
                    .filter(employee -> departmentCode.equals(employee.getDepartmentCode())
                            && employee.getHireDate() != null
                            && !employee.getHireDate().isBefore(startDate)
                            && !employee.getHireDate().isAfter(endDate))
                    .count();

            // Count employees who resigned from this department during the period
            long resignedEmployeesCount = allEmployees.stream()
                    .filter(employee -> departmentCode.equals(employee.getDepartmentCode())
                            && employee.getResignDate() != null
                            && !employee.getResignDate().isBefore(startDate)
                            && !employee.getResignDate().isAfter(endDate))
                    .count();

            // Populate the department data
            departmentData.put("department_name", department.getDepartmentName());
            departmentData.put("current_employees", currentEmployeesCount);
            departmentData.put("new_employees", newEmployeesCount);
            departmentData.put("resigned_employees", resignedEmployeesCount);

            result.add(departmentData);
        }

        return result;
    }

    /**
     * Returns employee distribution data by age ranges and gender
     *
     * @return A map containing:
     *         - "by_age": a list of maps with age range and count
     *         - "by_gender": a map with gender counts
     *         - "by_age_and_gender": a list of maps with age range, gender, and count
     */
    public Map<String, Object> getEmployeeAgeGenderDistribution() {
        Map<String, Object> result = new HashMap<>();

        // Define age ranges
        List<String> ageRanges = Arrays.asList("18-25", "26-30", "31-35", "36-40", "41-45", "46-50", "51+");

        // Get all active employees
        List<Employee> activeEmployees = employeeRepository.findByIsActiveTrue();

        // Calculate age distribution
        Map<String, Long> ageDistribution = calculateAgeDistribution(activeEmployees, ageRanges);

        // Format age distribution for response
        List<Map<String, Object>> ageDistributionResult = ageRanges.stream()
                .map(range -> {
                    Map<String, Object> rangeData = new HashMap<>();
                    rangeData.put("range", range);
                    rangeData.put("count", ageDistribution.getOrDefault(range, 0L));
                    return rangeData;
                })
                .collect(Collectors.toList());

        // Calculate gender distribution
        Map<String, Long> genderDistribution = activeEmployees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getGender,
                        Collectors.counting()
                ));

        // Calculate distribution by both age and gender
        List<Map<String, Object>> ageGenderDistribution = calculateAgeGenderDistribution(activeEmployees, ageRanges);

        // Add all data to result
        result.put("by_age", ageDistributionResult);
        result.put("by_gender", genderDistribution);
        result.put("by_age_and_gender", ageGenderDistribution);

        return result;
    }

    /**
     * Helper method to calculate age distribution based on defined age ranges
     */
    private Map<String, Long> calculateAgeDistribution(List<Employee> employees, List<String> ageRanges) {
        LocalDate today = LocalDate.now();

        return employees.stream()
                .filter(employee -> employee.getDob() != null)
                .collect(Collectors.groupingBy(
                        employee -> {
                            int age = Period.between(employee.getDob(), today).getYears();
                            return getAgeRange(age, ageRanges);
                        },
                        Collectors.counting()
                ));
    }

    /**
     * Helper method to calculate age and gender distribution
     */
    private List<Map<String, Object>> calculateAgeGenderDistribution(List<Employee> employees, List<String> ageRanges) {
        LocalDate today = LocalDate.now();
        List<Map<String, Object>> result = new ArrayList<>();

        // Group employees by age range and gender
        Map<String, Map<String, Long>> distribution = employees.stream()
                .filter(employee -> employee.getDob() != null)
                .collect(Collectors.groupingBy(
                        employee -> {
                            int age = Period.between(employee.getDob(), today).getYears();
                            return getAgeRange(age, ageRanges);
                        },
                        Collectors.groupingBy(
                                Employee::getGender,
                                Collectors.counting()
                        )
                ));

        // Format the result
        for (String ageRange : ageRanges) {
            Map<String, Long> genderCounts = distribution.getOrDefault(ageRange, Collections.emptyMap());

            for (Map.Entry<String, Long> entry : genderCounts.entrySet()) {
                Map<String, Object> item = new HashMap<>();
                item.put("age_range", ageRange);
                item.put("gender", entry.getKey());
                item.put("count", entry.getValue());
                result.add(item);
            }
        }

        return result;
    }

    /**
     * Helper method to determine the age range for a given age
     */
    private String getAgeRange(int age, List<String> ageRanges) {
        if (age < 18) return "Under 18";
        if (age <= 25) return ageRanges.get(0); // 18-25
        if (age <= 30) return ageRanges.get(1); // 26-30
        if (age <= 35) return ageRanges.get(2); // 31-35
        if (age <= 40) return ageRanges.get(3); // 36-40
        if (age <= 45) return ageRanges.get(4); // 41-45
        if (age <= 50) return ageRanges.get(5); // 46-50
        return ageRanges.get(6); // 51+
    }

    /**
     * Returns data describing personnel costs by department
     *
     * @return A list of maps, each containing:
     *         - "department_name": the name of the department
     *         - "department_code": the code of the department
     *         - "employee_count": number of active employees in the department
     *         - "total_salary": sum of basic salaries of all active employees in the department
     *         - "average_salary": average basic salary per employee in the department
     */
    public List<Map<String, Object>> getPersonnelCostsByDepartment() {
        List<Map<String, Object>> result = new ArrayList<>();

        // Get all departments
        List<Department> departments = departmentRepository.findAll();

        // Get all active employees
        List<Employee> activeEmployees = employeeRepository.findAll().stream()
                .filter(employee -> employee.getResignDate() == null)
                .toList();

        // Get all active contracts (where status is ACTIVE)
        List<Contract> activeContracts = contractRepository.findAllContractIsActive();

        // For each department, calculate salary statistics
        for (Department department : departments) {
            String departmentCode = department.getDepartmentCode();
            Map<String, Object> departmentData = new HashMap<>();

            // Get employees in this department
            List<Employee> departmentEmployees = activeEmployees.stream()
                    .filter(employee -> departmentCode.equals(employee.getDepartmentCode()))
                    .toList();

            // Calculate total salary by summing basic salaries from contracts
            double totalSalary = 0.0;
            int employeeCount = departmentEmployees.size();

            for (Employee employee : departmentEmployees) {
                String employeeCode = employee.getEmployeeCode();

                // Find active contract for this employee
                Optional<Contract> employeeContract = activeContracts.stream()
                        .filter(contract -> employeeCode.equals(contract.getEmployeeCode()))
                        .findFirst();

                // Add salary to total if contract exists
                if (employeeContract.isPresent()) {
                    String basicSalaryStr = employeeContract.get().getBasicSalary();
                    try {
                        double salary = Double.parseDouble(basicSalaryStr);
                        totalSalary += salary;
                    } catch (NumberFormatException e) {
                        // Handle case where salary is not a valid number
                        // Log error or skip this employee
                    }
                }
            }

            // Calculate average salary
            double averageSalary = employeeCount > 0 ? totalSalary / employeeCount : 0.0;

            // Populate the department data
            departmentData.put("department_name", department.getDepartmentName());
            departmentData.put("department_code", departmentCode);
            departmentData.put("employee_count", employeeCount);
            departmentData.put("total_salary", totalSalary);
            departmentData.put("average_salary", averageSalary);

            result.add(departmentData);
        }

        return result;
    }

    /**
     * Returns data describing personnel cost trends for the 5 most recent quarters
     *
     * @return A list of maps, each containing:
     *         - "quarter": the quarter label (e.g., "Q1 2025")
     *         - "total_cost": total personnel cost for the quarter
     *         - "average_cost_per_employee": average cost per employee for the quarter
     *         - "employee_count": number of employees during that quarter
     *         - "department_costs": list of department costs for the quarter
     */
    public List<Map<String, Object>> getPersonnelCostTrends() {
        List<Map<String, Object>> result = new ArrayList<>();

        // Get current date to determine the most recent quarter
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        int currentQuarter = (currentDate.getMonthValue() - 1) / 3 + 1;

        // Generate the 5 most recent quarters (including current quarter)
        List<Map<String, Object>> quarters = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Map<String, Object> quarter = new HashMap<>();
            int quarterNumber = currentQuarter - i;
            int year = currentYear;

            // Adjust year if we go back to previous year's quarters
            while (quarterNumber <= 0) {
                quarterNumber += 4;
                year -= 1;
            }

            // Calculate start and end dates for the quarter
            int startMonth = (quarterNumber - 1) * 3 + 1;
            LocalDate startDate = LocalDate.of(year, startMonth, 1);
            LocalDate endDate = startDate.plusMonths(3).minusDays(1);

            quarter.put("quarter_number", quarterNumber);
            quarter.put("year", year);
            quarter.put("label", "Q" + quarterNumber + " " + year);
            quarter.put("start_date", startDate);
            quarter.put("end_date", endDate);

            quarters.add(quarter);
        }

        // Sort quarters chronologically (oldest first)
        Collections.reverse(quarters);

        // Get all employees and contracts
        List<Employee> allEmployees = employeeRepository.findAll();
        List<Contract> allContracts = contractRepository.findAllContractIsActive();

        // For each quarter, calculate personnel costs
        for (Map<String, Object> quarter : quarters) {
            LocalDate startDate = (LocalDate) quarter.get("start_date");
            LocalDate endDate = (LocalDate) quarter.get("end_date");
            String quarterLabel = (String) quarter.get("label");

            Map<String, Object> quarterData = new HashMap<>();

            // Find employees active during this quarter
            List<Employee> activeEmployees = allEmployees.stream()
                    .filter(employee -> employee.getHireDate() != null
                            && !employee.getHireDate().isAfter(endDate)
                            && (employee.getResignDate() == null || !employee.getResignDate().isBefore(startDate)))
                    .toList();

            // Find contracts active during this quarter
            List<Contract> activeContracts = allContracts.stream()
                    .filter(contract -> !contract.getStartDate().isAfter(endDate)
                            && !contract.getEndDate().isBefore(startDate))
                    .toList();

            // Calculate total personnel cost for the quarter
            double totalQuarterCost = 0.0;
            int totalEmployeeCount = activeEmployees.size();

            for (Contract contract : activeContracts) {
                totalQuarterCost += Double.parseDouble(contract.getBasicSalary());
            }

            // Calculate average cost per employee
            double averageCostPerEmployee = totalEmployeeCount > 0 ?
                    totalQuarterCost / totalEmployeeCount : 0.0;

            // Create quarter data
            quarterData.put("quarter", quarterLabel);
            quarterData.put("total_cost", totalQuarterCost);
            quarterData.put("average_cost_per_employee", averageCostPerEmployee);
            quarterData.put("employee_count", totalEmployeeCount);

            result.add(quarterData);
        }

        return result;
    }

    public Map<String, Object> getDashboardAttendanceAndLeaveScreen(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> dashboardData = new HashMap<>();

        // Get attendance and leave statistics
        Map<String, Object> attendanceLeaveStats = getAttendanceLeaveStatistics(startDate, endDate);
        dashboardData.put("overview_card", attendanceLeaveStats);

        // Add other attendance-related data if needed
        List<Map<String, Object>> weeklyAttendanceTrend = getWeeklyAttendanceTrend();
        dashboardData.put("weekly_attendance_trend", weeklyAttendanceTrend);

        List<int[]> checkInDistribution = getWeeklyCheckInTimeDistribution();
        dashboardData.put("check_in_distribution", checkInDistribution);

        List<int[]> checkOutDistribution = getWeeklyCheckOutTimeDistribution();
        dashboardData.put("check_out_distribution", checkOutDistribution);

        List<Map<String, Object>> leaveRequestsByType = getLeaveTypeDistribution(endDate);
        dashboardData.put("leave_requests_by_type", leaveRequestsByType);

        List<Map<String, Object>> leaveRequestsByStatus = getLeaveRequestsByDepartmentAndStatus(endDate);
        dashboardData.put("leave_requests_by_department", leaveRequestsByStatus);

        List<Map<String, Object>> leaveRequestTrendByMonth = getLeaveRequestTrendsByMonth(endDate);
        dashboardData.put("leave_request_trend_by_month", leaveRequestTrendByMonth);

        List<Map<String, Object>> top5MaxEmployeesByTotalHours = getTopMaxTotalHoursInMonth();
        dashboardData.put("top_highest_by_total_hours", top5MaxEmployeesByTotalHours);

        List<Map<String, Object>> top5MinEmployeesByTotalHours = getTopMinTotalHoursInMonth();
        dashboardData.put("top_lowest_by_total_hours", top5MinEmployeesByTotalHours);

        return dashboardData;
    }

    /**
     * Returns attendance and leave statistics for dashboard overview
     *
     * @param endDate The reference date for calculating statistics
     * @return A map containing:
     *         - "total_working_hours": Total working hours for the month of endDate
     *         - "on_time_percentage": Percentage of on-time attendance for the month of endDate
     *         - "total_leave_days_used": Total leave days used in the year of endDate
     *         - "employees_currently_on_leave": Number of employees currently on leave
     */
    public Map<String, Object> getAttendanceLeaveStatistics(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new HashMap<>();

        // Get all employees
        List<Employee> allEmployees = employeeRepository.findAll().stream()
                .filter(employee -> employee.getResignDate() == null)
                .toList();

        // Calculate first day of the month and last day of the month for endDate
        LocalDate startOfMonth = endDate.withDayOfMonth(1);

        // Calculate first day of the year and last day of the year for endDate
        LocalDate startOfYear = LocalDate.of(endDate.getYear(), 1, 1);
        LocalDate endOfYear = LocalDate.of(endDate.getYear(), 12, 31);

        // 1. Total working hours for the month
        double totalWorkingHours = 0.0;

        for (Employee employee : allEmployees) {
            List<AttendanceRecord> monthlyAttendance = attendanceRecordRepository
                    .findByEmployeeCodeAndWorkDateBetween(employee.getEmployeeCode(), startOfMonth, endDate);

            for (AttendanceRecord record : monthlyAttendance) {
                if (record.getTotalHours() != null) {
                    totalWorkingHours += record.getTotalHours();
                }
            }
        }

        // 2. On-time attendance percentage for the month
        int totalAttendanceRecords = 0;
        int onTimeRecords = 0;

        for (Employee employee : allEmployees) {
            List<AttendanceRecord> monthlyAttendance = attendanceRecordRepository
                    .findByEmployeeCodeAndWorkDateBetween(employee.getEmployeeCode(), startOfMonth, endDate);

            for (AttendanceRecord record : monthlyAttendance) {
                if (record.getStatus() != null) {
                    totalAttendanceRecords++;
                    if (record.getStatus() == AttendanceStatus.BINHTHUONG) {
                        onTimeRecords++;
                    }
                }
            }
        }

        double onTimePercentage = totalAttendanceRecords > 0 ?
                (double) onTimeRecords / totalAttendanceRecords * 100 : 0.0;

        // 3. Total leave days used in the year
        int totalLeaveDaysUsed = 0;

        for (Employee employee : allEmployees) {
            // Get approved leave requests for the year
            List<LeaveRequest> yearlyLeaveRequests = leaveRequestRepository
                    .findApprovedLeaveRequestsByEmployeeCodeAndYear(employee.getEmployeeCode(), endDate.getYear());

            for (LeaveRequest leaveRequest : yearlyLeaveRequests) {
                if (leaveRequest.getStartDate() != null && leaveRequest.getEndDate() != null) {
                    // Calculate days between start and end dates (inclusive)
                    int leaveDays = Period.between(leaveRequest.getStartDate(), leaveRequest.getEndDate()).getDays() + 1;
                    totalLeaveDaysUsed += leaveDays;
                }
            }
        }

        // 4. Number of employees currently on leave
        LocalDate today = LocalDate.now();
        int employeesCurrentlyOnLeave = 0;

        for (Employee employee : allEmployees) {
            List<LeaveRequest> approvedLeaves = leaveRequestRepository
                    .findByEmployeeCodeAndStatus(employee.getEmployeeCode(), ApprovalStatus.PHEDUYET);

            boolean isOnLeaveToday = approvedLeaves.stream()
                    .anyMatch(leave ->
                        leave.getStartDate() != null &&
                        leave.getEndDate() != null &&
                        !today.isBefore(leave.getStartDate()) &&
                        !today.isAfter(leave.getEndDate())
                    );

            if (isOnLeaveToday) {
                employeesCurrentlyOnLeave++;
            }
        }

        // Populate the result map
        result.put("total_working_hours", totalWorkingHours);
        result.put("on_time_percentage", onTimePercentage);
        result.put("total_leave_days_used", totalLeaveDaysUsed);
        result.put("employees_currently_on_leave", employeesCurrentlyOnLeave);

        return result;
    }

    /**
     * Returns attendance trend data for the 7 days of the current week
     *
     * @return A list of maps, each containing:
     *         - "date": the date in format "yyyy-MM-dd"
     *         - "day_of_week": the name of the day (Monday, Tuesday, etc.)
     *         - "on_time_count": number of employees who arrived on time
     *         - "late_count": number of employees who arrived late
     *         - "early_departure_count": number of employees who left early
     *         - "absence_count": number of employees who were absent
     *         - "overtime_count": number of employees who worked overtime
     */
    public List<Map<String, Object>> getWeeklyAttendanceTrend() {
        List<Map<String, Object>> result = new ArrayList<>();

        // Get current date
        LocalDate currentDate = LocalDate.now();

        // Calculate the first day of the current week (Monday)
        LocalDate startOfWeek = currentDate.minusDays(currentDate.getDayOfWeek().getValue() - 1);

        // Get all employees
        List<Employee> activeEmployees = employeeRepository.findAll().stream()
                .filter(employee -> employee.getResignDate() == null)
                .toList();

        // For each day of the week (Monday to Sunday)
        for (int dayOffset = 0; dayOffset < 7; dayOffset++) {
            LocalDate date = startOfWeek.plusDays(dayOffset);
            Map<String, Object> dayData = new HashMap<>();

            // Add date information
            dayData.put("date", date.toString());
            dayData.put("day_of_week", date.getDayOfWeek().toString());

            // Initialize counters for each attendance status
            int onTimeCount = 0;
            int lateCount = 0;
            int earlyDepartureCount = 0;
            int absenceCount = 0;
            int overtimeCount = 0;
            List<AttendanceRecord> records = attendanceRecordRepository
                    .findByWorkDate(date);

            for (AttendanceRecord record: records) {
                if (record != null) {
                    // Count based on attendance status
                    if (record.getStatus() == AttendanceStatus.BINHTHUONG) {
                        onTimeCount++;
                    } else if (record.getStatus() == AttendanceStatus.MUON) {
                        lateCount++;
                    } else if (record.getStatus() == AttendanceStatus.VESOM) {
                        earlyDepartureCount++;
                    } else if (record.getStatus() == AttendanceStatus.VANG) {
                        absenceCount++;
                    } else if (record.getStatus() == AttendanceStatus.THEMGIO) {
                        overtimeCount++;
                    }
                }
            }


            // Add counts to day data
            dayData.put("on_time_count", onTimeCount);
            dayData.put("late_count", lateCount);
            dayData.put("early_departure_count", earlyDepartureCount);
            dayData.put("absence_count", absenceCount);
            dayData.put("overtime_count", overtimeCount);

            // Calculate total expected employees (excluding weekends)
            int totalExpectedEmployees = date.getDayOfWeek().getValue() < 6 ? activeEmployees.size() : 0;
            dayData.put("total_expected_employees", totalExpectedEmployees);

            result.add(dayData);
        }

        return result;
    }

    /**
     * Returns the distribution of check-in times for the current week
     * grouped by hour slots from 7:00 to 18:00
     *
     * @return A list of arrays, each containing:
     *         - hourIndex: index of time slot (0 for 7:00, 1 for 8:00, etc.)
     *         - dayIndex: index of day in week (0 for Monday, 1 for Tuesday, etc.)
     *         - count: number of employees who checked in on that day during that time slot
     */
    public List<int[]> getWeeklyCheckInTimeDistribution() {
        // Get current date
        LocalDate currentDate = LocalDate.now();

        // Calculate the first day of the current week (Monday)
        LocalDate startOfWeek = currentDate.minusDays(currentDate.getDayOfWeek().getValue() - 1);

        // Calculate the last day of the current week (Sunday)
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        // Get all attendance records for the week
        List<AttendanceRecord> weeklyRecords = attendanceRecordRepository.findByWorkDateBetween(startOfWeek, endOfWeek);

        // Filter records with valid check-in times
        List<AttendanceRecord> recordsWithCheckIn = weeklyRecords.stream()
                .filter(record -> record.getCheckInTime() != null)
                .toList();

        // Initialize a 2D array to store counts for each day and time slot
        // 11 time slots (7:00 to 19:00) x 7 days (Monday to Sunday)
        int[][] countMatrix = new int[13][7];

        // Count check-ins for each day and time slot
        for (AttendanceRecord record : recordsWithCheckIn) {
            // Calculate day index (0 for Monday, 1 for Tuesday, etc.)
            int dayIndex = record.getWorkDate().getDayOfWeek().getValue() - 1;

            // Calculate time slot index (0 for 7:00, 1 for 8:00, etc.)
            int hour = record.getCheckInTime().getHour();

            // Only count hours between 7:00 and 17:59
            if (hour >= 7 && hour < 19) {
                int hourIndex = hour - 7;
                countMatrix[hourIndex][dayIndex]++;
            }
        }

        // Convert the 2D array to the required output format
        List<int[]> result = new ArrayList<>();
        for (int hourIndex = 0; hourIndex < 13; hourIndex++) {
            for (int dayIndex = 0; dayIndex < 7; dayIndex++) {
                // Include all entries (both zero and non-zero counts) for complete data representation
                result.add(new int[]{hourIndex, dayIndex, countMatrix[hourIndex][dayIndex]});
            }
        }

        return result;
    }

    /**
     * Returns the distribution of check-out times for the current week
     * grouped by hour slots from 7:00 to 18:00
     *
     * @return A list of arrays, each containing:
     *         - hourIndex: index of time slot (0 for 7:00, 1 for 8:00, etc.)
     *         - dayIndex: index of day in week (0 for Monday, 1 for Tuesday, etc.)
     *         - count: number of employees who checked out on that day during that time slot
     */
    public List<int[]> getWeeklyCheckOutTimeDistribution() {
        // Get current date
        LocalDate currentDate = LocalDate.now();

        // Calculate the first day of the current week (Monday)
        LocalDate startOfWeek = currentDate.minusDays(currentDate.getDayOfWeek().getValue() - 1);

        // Calculate the last day of the current week (Sunday)
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        // Get all attendance records for the week
        List<AttendanceRecord> weeklyRecords = attendanceRecordRepository.findByWorkDateBetween(startOfWeek, endOfWeek);

        // Filter records with valid check-out times
        List<AttendanceRecord> recordsWithCheckOut = weeklyRecords.stream()
                .filter(record -> record.getCheckOutTime() != null)
                .toList();

        // Initialize a 2D array to store counts for each day and time slot
        // 11 time slots (7:00 to 19:00) x 7 days (Monday to Sunday)
        int[][] countMatrix = new int[13][7];

        // Count check-outs for each day and time slot
        for (AttendanceRecord record : recordsWithCheckOut) {
            // Calculate day index (0 for Monday, 1 for Tuesday, etc.)
            int dayIndex = record.getWorkDate().getDayOfWeek().getValue() - 1;

            // Calculate time slot index (0 for 7:00, 1 for 8:00, etc.)
            int hour = record.getCheckOutTime().getHour();

            // Only count hours between 7:00 and 17:59
            if (hour >= 7 && hour < 19) {
                int hourIndex = hour - 7;
                countMatrix[hourIndex][dayIndex]++;
            }
        }

        // Convert the 2D array to the required output format
        List<int[]> result = new ArrayList<>();
        for (int hourIndex = 0; hourIndex < 13; hourIndex++) {
            for (int dayIndex = 0; dayIndex < 7; dayIndex++) {
                // Include all entries (both zero and non-zero counts) for complete data representation
                result.add(new int[]{hourIndex, dayIndex, countMatrix[hourIndex][dayIndex]});
            }
        }

        return result;
    }

    /**
     * Returns statistics on leave types distribution for a pie chart
     * Groups approved leave requests by leave type for the year of the provided end date
     *
     * @param endDate The reference date to determine the year for statistics
     * @return A list of maps, each containing:
     *         - "name": the leave type name
     *         - "value": the count of approved leave requests of that type
     */
    public List<Map<String, Object>> getLeaveTypeDistribution(LocalDate endDate) {
        List<Map<String, Object>> result = new ArrayList<>();

        // Get the year from endDate
        int year = endDate.getYear();

        // Get all leave types
        List<LeaveType> allLeaveTypes = leaveTypeRepository.findAll();

        // Create a map to store counts by leave type ID
        Map<Long, Integer> leaveTypeCounts = new HashMap<>();

        // Initialize counts for all leave types to zero
        for (LeaveType leaveType : allLeaveTypes) {
            leaveTypeCounts.put(leaveType.getId(), 0);
        }

            List<LeaveRequest> approvedLeaveRequests = leaveRequestRepository
                    .findApprovedLeaveRequestsByYear(year);

            for (LeaveRequest leaveRequest : approvedLeaveRequests) {
                Long leaveTypeId = leaveRequest.getLeaveTypeId();
                leaveTypeCounts.put(leaveTypeId, leaveTypeCounts.getOrDefault(leaveTypeId, 0) + 1);
            }

        // Create result list with leave type names and counts
        for (LeaveType leaveType : allLeaveTypes) {
            Long leaveTypeId = leaveType.getId();
            int count = leaveTypeCounts.getOrDefault(leaveTypeId, 0);

            // Only include leave types that have been used
            if (count > 0) {
                Map<String, Object> leaveTypeData = new HashMap<>();
                leaveTypeData.put("name", leaveType.getLeaveTypeName());
                leaveTypeData.put("value", count);
                result.add(leaveTypeData);
            }
        }

        return result;
    }

    /**
     * Returns leave request distribution by department and status
     * Groups leave requests by department and approval status for visualization
     *
     * @param endDate The reference date to determine the year for statistics
     * @return A list of maps, each containing:
     *         - "department": the department name
     *         - "approved": count of approved leave requests
     *         - "pending": count of pending leave requests
     *         - "rejected": count of rejected leave requests
     */
    public List<Map<String, Object>> getLeaveRequestsByDepartmentAndStatus(LocalDate endDate) {
        List<Map<String, Object>> result = new ArrayList<>();

        // Get the year from endDate
        int year = endDate.getYear();

        // Get all departments
        List<Department> departments = departmentRepository.findAll();

        // Get all employees with their department codes
        Map<String, String> employeeDepartments = employeeRepository.findAll().stream()
                .filter(employee -> employee.getDepartmentCode() != null)  // Filter out employees with null department codes
                .collect(Collectors.toMap(
                        Employee::getEmployeeCode,
                        Employee::getDepartmentCode,
                        (existing, replacement) -> existing // In case of duplicate keys, keep the first one
                ));

        // Get all leave requests for the year
        List<LeaveRequest> allLeaveRequests = leaveRequestRepository.findAllByYear(year);

        // Group by department and count by status
        for (Department department : departments) {
            String departmentCode = department.getDepartmentCode();
            String departmentName = department.getDepartmentName();

            // Count leave requests by status for this department
            long approvedCount = allLeaveRequests.stream()
                    .filter(request -> departmentCode.equals(employeeDepartments.get(request.getEmployeeCode())) &&
                                      ApprovalStatus.PHEDUYET.equals(request.getStatus()))
                    .count();

            long pendingCount = allLeaveRequests.stream()
                    .filter(request -> departmentCode.equals(employeeDepartments.get(request.getEmployeeCode())) &&
                                      ApprovalStatus.DANGCHO.equals(request.getStatus()))
                    .count();

            long rejectedCount = allLeaveRequests.stream()
                    .filter(request -> departmentCode.equals(employeeDepartments.get(request.getEmployeeCode())) &&
                                      ApprovalStatus.TUCHOI.equals(request.getStatus()))
                    .count();

            // Only include departments with at least one leave request
            if (approvedCount > 0 || pendingCount > 0 || rejectedCount > 0) {
                Map<String, Object> departmentData = new HashMap<>();
                departmentData.put("department", departmentName);
                departmentData.put("approved", approvedCount);
                departmentData.put("pending", pendingCount);
                departmentData.put("rejected", rejectedCount);

                result.add(departmentData);
            }
        }

        return result;
    }

    /**
     * Returns leave request trends across the 12 months of the current year
     *
     * @param endDate The reference date to determine the year for statistics
     * @return A list of maps, each containing:
     *         - "month": the month name (January, February, etc.)
     *         - "approved": count of approved leave requests for that month
     *         - "pending": count of pending leave requests for that month
     *         - "rejected": count of rejected leave requests for that month
     *         - "total": total leave requests for that month
     */
    public List<Map<String, Object>> getLeaveRequestTrendsByMonth(LocalDate endDate) {
        List<Map<String, Object>> result = new ArrayList<>();

        // Get the year from endDate
        int year = endDate.getYear();

        // Get all leave requests for the year
        List<LeaveRequest> allLeaveRequests = leaveRequestRepository.findAllByYear(year);

        // For each month of the year
        for (int month = 1; month <= 12; month++) {
            // Calculate start and end date of the month
            LocalDate startOfMonth = LocalDate.of(year, month, 1);
            LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);

            // Count leave requests by status for this month
            int totalCount = 0;

            for (LeaveRequest request : allLeaveRequests) {
                // Check if leave request falls in this month (either start or end date is in the month)
                if (request.getStartDate() != null && request.getEndDate() != null) {
                    boolean startsInMonth = !request.getStartDate().isBefore(startOfMonth) && !request.getStartDate().isAfter(endOfMonth);
                    boolean endsInMonth = !request.getEndDate().isBefore(startOfMonth) && !request.getEndDate().isAfter(endOfMonth);
                    boolean spansMonth = request.getStartDate().isBefore(startOfMonth) && request.getEndDate().isAfter(endOfMonth);

                    if (startsInMonth || endsInMonth || spansMonth) {
                        if (request.getStatus() == ApprovalStatus.PHEDUYET) {
                            totalCount++;
                        }
                    }
                }
            }

            // Create month data
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", "T" + month);
            monthData.put("total", totalCount);

            result.add(monthData);
        }

        return result;
    }


    public List<Map<String, Object>> getTopMaxTotalHoursInMonth() {
        LocalDate startDate = LocalDate.now().withDayOfMonth(1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        List<Map<String, Object>> result = new ArrayList<>();

        // Get all employees
        List<Employee> employees = employeeRepository.findAll().stream()
                .filter(employee -> employee.getResignDate() == null) // Only consider active employees
                .toList();

        Pageable pageable = PageRequest.of(0, 5); // Get top 5 records

        List<Object[]> attendanceRecords = attendanceRecordRepository.findTop5MaxEmployeesByTotalHours(startDate, endDate, pageable);
        for (Employee employee : employees) {
            for (Object[] record : attendanceRecords) {
                Map<String, Object> item = new HashMap<>();
                if( employee.getEmployeeCode().equals(record[0])) {
                    item.put("employee_code", employee.getEmployeeCode());
                    item.put("name", employee.getLastName() + " " + employee.getFirstName());
                    item.put("value", record[1]);
                    result.add(item);
                }
            }

        }
        return result;

    }

    public List<Map<String, Object>> getTopMinTotalHoursInMonth() {
        LocalDate startDate = LocalDate.now().withDayOfMonth(1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        List<Map<String, Object>> result = new ArrayList<>();

        // Get all employees
        List<Employee> employees = employeeRepository.findAll().stream()
                .filter(employee -> employee.getResignDate() == null) // Only consider active employees
                .toList();

        Pageable pageable = PageRequest.of(0, 5); // Get top 5 records

        List<Object[]> attendanceRecords = attendanceRecordRepository.findTop5MinEmployeesByTotalHours(startDate, endDate, pageable);
        for (Employee employee : employees) {
            for (Object[] record : attendanceRecords) {
                Map<String, Object> item = new HashMap<>();
                if( employee.getEmployeeCode().equals(record[0])) {
                    item.put("employee_code", employee.getEmployeeCode());
                    item.put("name", employee.getLastName() + " " + employee.getFirstName());
                    item.put("value", record[1]);
                    result.add(item);
                }
            }

        }
        return result;

    }
}
