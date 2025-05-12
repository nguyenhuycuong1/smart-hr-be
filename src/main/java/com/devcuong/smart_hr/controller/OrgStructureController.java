package com.devcuong.smart_hr.controller;

import com.devcuong.smart_hr.Entity.Department;
import com.devcuong.smart_hr.Entity.JobPosition;
import com.devcuong.smart_hr.Entity.Team;
import com.devcuong.smart_hr.dto.DepartmentDTO;
import com.devcuong.smart_hr.dto.JobPositionDTO;
import com.devcuong.smart_hr.dto.TeamDTO;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.dto.response.ApiResponse;
import com.devcuong.smart_hr.dto.response.PageResponse;
import com.devcuong.smart_hr.service.DepartmentService;
import com.devcuong.smart_hr.service.JobPositionService;
import com.devcuong.smart_hr.service.OrgStructureService;
import com.devcuong.smart_hr.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class OrgStructureController {
    @Autowired
    private OrgStructureService orgStructureService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private JobPositionService jobPositionService;

    @Autowired
    private TeamService teamService;

    //Department
    @GetMapping("/departments")
    public ApiResponse getAllDepartments() {
        return ApiResponse.<List<Department>>builder().data(orgStructureService.getDepartments()).build().success();
    }

    @PostMapping("/departments/search")
    public PageResponse searchDepartments(@RequestBody PageFilterInput<Department> input) {
        Page<Department> page = departmentService.searchDepartment(input);
        return PageResponse.builder().data(page.getContent()).dataCount(page.getTotalElements()).build().success();
    }

    @PostMapping("/departments/create")
    public ApiResponse addDepartment(@RequestBody DepartmentDTO department) {
        return ApiResponse.<Department>builder().data(orgStructureService.createDepartment(department)).build().success();
    }

    @PutMapping("/departments/{departmentCode}")
    public ApiResponse updateDepartment(@PathVariable String departmentCode, @RequestBody DepartmentDTO department) {
        return ApiResponse.builder().data(orgStructureService.updateDepartment(departmentCode, department)).build().success();
    }

    @DeleteMapping("/departments/{departmentCode}")
    public ApiResponse deleteDepartment(@PathVariable String departmentCode) {
        orgStructureService.deleteDepartment(departmentCode);
        return ApiResponse.builder().build().success();
    }

    // Team

    @GetMapping("/teams")
    public ApiResponse getAllTeams() {
        return ApiResponse.<List<Team>>builder().data(orgStructureService.getTeams()).build().success();
    }

    @PostMapping("/teams/search")
    public PageResponse searchTeams(@RequestBody PageFilterInput<Team> input) {
        Page<Team> page = teamService.searchTeam(input);
        return PageResponse.builder().data(page.getContent()).dataCount(page.getTotalElements()).build().success();
    }

    @GetMapping("/teams/with-department")
    public ApiResponse getTeamWithDept() {
        return ApiResponse.builder().data(orgStructureService.getTeamWithDepartment()).build().success();
    }

    @PostMapping("/teams/create")
    public ApiResponse addTeam(@RequestBody TeamDTO team) {
        return ApiResponse.<Team>builder().data(orgStructureService.createTeam(team)).build().success();
    }

    @PutMapping("/teams/{teamCode}")
    public ApiResponse updateTeam(@PathVariable String teamCode, @RequestBody TeamDTO team) {
        return ApiResponse.builder().data(orgStructureService.updateTeam(teamCode, team)).build().success();
    }

    @DeleteMapping("/teams/{teamCode}")
    public ApiResponse deleteTeam(@PathVariable String teamCode) {
        orgStructureService.deleteTeam(teamCode);
        return ApiResponse.builder().build().success();
    }

    // Job Position
    @GetMapping("/job-positions")
    public ApiResponse getAllJobPositions() {
        return ApiResponse.<List<JobPosition>>builder()
                .data(orgStructureService.getJobPositions())
                .build().success();
    }

    @PostMapping("/job-positions/search")
    public PageResponse searchJobPositions(@RequestBody PageFilterInput<JobPosition> input) {
        Page<JobPosition> page = jobPositionService.searchJobPosition(input);
        return PageResponse.builder().data(page.getContent()).dataCount(page.getTotalElements()).build().success();
    }

    @PostMapping("/job-positions/create")
    public ApiResponse addJobPosition(@RequestBody JobPositionDTO jobPosition) {
        log.info(jobPosition.toString());
        return ApiResponse.builder()
                .data(orgStructureService.createJobPosition(jobPosition))
                .build().success();
    }

    @PutMapping("/job-positions/{jobCode}")
    public ApiResponse updateJobPosition(@PathVariable String jobCode, @RequestBody JobPositionDTO jobPosition) {
        return ApiResponse.builder().data(orgStructureService.updateJobPosition(jobCode, jobPosition)).build().success();
    }

    @DeleteMapping("/job-positions/{jobCode}")
    public ApiResponse deleteJobPosition(@PathVariable String jobCode) {
        orgStructureService.deleteJobPosition(jobCode);
        return ApiResponse.builder().build().success();
    }

    @GetMapping("/job-positions/with-department")
    public ApiResponse getAllJobPositionsWithDepartment() {
        return ApiResponse.builder().data(orgStructureService.getJobPositionWithDepartment()).build().success();
    }

}
