package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.Entity.Department;
import com.devcuong.smart_hr.Entity.JobPosition;
import com.devcuong.smart_hr.Entity.Team;
import com.devcuong.smart_hr.dto.DepartmentDTO;
import com.devcuong.smart_hr.dto.JobPositionDTO;
import com.devcuong.smart_hr.dto.TeamDTO;
import com.devcuong.smart_hr.exception.AppException;
import com.devcuong.smart_hr.exception.ErrorCode;
import com.devcuong.smart_hr.repository.DepartmentRepository;
import com.devcuong.smart_hr.repository.JobPositionRepository;
import com.devcuong.smart_hr.repository.TeamRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OrgStructureService {
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private JobPositionRepository jobPositionRepository;

    // Department service



    public List<Department> getDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartment(String departmentCode) {
        return departmentRepository.findDepartmentByDepartmentCode(departmentCode).orElse(null);
    }

    public Department createDepartment(DepartmentDTO departmentDTO) {
        Department existingDepartment = departmentRepository.findDepartmentByDepartmentCode(departmentDTO.getDepartmentCode()).orElse(null);
        if (existingDepartment == null) {
            Department newDepartment = new Department();
            if (departmentDTO.getDepartmentCode() == null || departmentDTO.getDepartmentCode().trim().isEmpty()) {
                throw new AppException(ErrorCode.INPUT_INVALID, "Mã phòng ban không được để trống!");
            }
            newDepartment.setDepartmentCode(departmentDTO.getDepartmentCode());
            if (departmentDTO.getDepartmentName() == null || departmentDTO.getDepartmentName().trim().isEmpty()) {
                throw new AppException(ErrorCode.INPUT_INVALID, "Tên phòng ban không được để trống!");
            }
            newDepartment.setDepartmentName(departmentDTO.getDepartmentName());
            newDepartment.setDescription(departmentDTO.getDescription());
            departmentRepository.save(newDepartment);
            return newDepartment;
        } else {
            throw new AppException(ErrorCode.INPUT_INVALID, "Phòng ban đã tồn tại với mã: " + departmentDTO.getDepartmentCode());
        }
    }

    public Department updateDepartment(String departmentCode, DepartmentDTO departmentDTO) {
        Department oldDepartment = getDepartment(departmentCode);
        if (oldDepartment == null) {
            throw new AppException(ErrorCode.NOT_EXISTS, "Phòng ban không tồn tại");
        }
        if (departmentDTO.getDepartmentCode() == null || departmentDTO.getDepartmentCode().trim().isEmpty()) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Mã phòng ban không được để trống!");
        }
        oldDepartment.setDepartmentName(departmentDTO.getDepartmentName());
        oldDepartment.setDescription(departmentDTO.getDescription());

        departmentRepository.save(oldDepartment);
        return oldDepartment;
    }

    public void deleteDepartment(String departmentCode) {
        Department department = getDepartment(departmentCode);
        if (department == null) {
            throw new AppException(ErrorCode.NOT_EXISTS, "Department not found");
        }
        departmentRepository.delete(department);
    }



    // Team service

    public List<Team> getTeams() {
        return teamRepository.findAll();
    }

    public Team getTeam(String teamCode) {
        return teamRepository.findTeamByTeamCode(teamCode).orElse(null);
    }

    public Team createTeam(TeamDTO teamDTO) {
        Team ExistingTeam = teamRepository.findTeamByTeamCode(teamDTO.getTeamCode()).orElse(null);
        if (ExistingTeam == null) {

        Team newTeam = new Team();
        if (teamDTO.getTeamCode() == null || teamDTO.getTeamCode().trim().isEmpty()) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Mã đội nhóm không được để trống!");
        }
        newTeam.setTeamCode(teamDTO.getTeamCode());
        if (teamDTO.getTeamName() == null || teamDTO.getTeamName().trim().isEmpty()) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Tên đội nhóm không được để trống!");
        }
        newTeam.setTeamName(teamDTO.getTeamName());
        newTeam.setDescription(teamDTO.getDescription());
        newTeam.setDepartmentCode(teamDTO.getDepartmentCode());
        teamRepository.save(newTeam);
        return newTeam;
        }
        else {
            throw new AppException(ErrorCode.NOT_EXISTS, "Team already exists");
        }
    }

    public Team updateTeam(String teamCode, TeamDTO teamDTO) {
        Team oldTeam = getTeam(teamCode);
        if (oldTeam == null) {
            throw new AppException(ErrorCode.NOT_EXISTS, "Team not found");
        }
        oldTeam.setDepartmentCode(teamDTO.getDepartmentCode());
        oldTeam.setTeamName(teamDTO.getTeamName());
        oldTeam.setDescription(teamDTO.getDescription());

        teamRepository.save(oldTeam);
        return oldTeam;
    }

    public void deleteTeam(String teamCode) {
        Team team = getTeam(teamCode);
        if (team == null) {
            throw new AppException(ErrorCode.NOT_EXISTS, "Team not found");
        }
        teamRepository.delete(team);
    }

    public Map<String, List<Team>> getTeamWithDepartment() {
        Map<String, List<Team>> teamWithDepartment = new HashMap<>();
        List<Team> teams = getTeams();
        for (Team team : teams) {
            String departmentCode = team.getDepartmentCode() == null || team.getDepartmentCode().trim().isEmpty()
                    ? "no_dept" : team.getDepartmentCode();
            teamWithDepartment.computeIfAbsent(departmentCode, k -> new ArrayList<>()).add(team);
        }
        return teamWithDepartment;
    }

    // Job position service

    public List<JobPosition> getJobPositions() {
        return jobPositionRepository.findAll();
    }

    public JobPosition getJobPosition(String jobPositionCode) {
        return jobPositionRepository.findJobPositionByJobCode(jobPositionCode).orElse(null);
    }

    public JobPosition createJobPosition(JobPositionDTO jobPositionDTO) {
        JobPosition existingJobPosition = jobPositionRepository.findJobPositionByJobCode(jobPositionDTO.getJobCode()).orElse(null);
        if (existingJobPosition == null) {
            JobPosition newJobPosition = new JobPosition();
            if (jobPositionDTO.getJobCode() == null || jobPositionDTO.getJobCode().trim().isEmpty()) {
                throw new AppException(ErrorCode.INPUT_INVALID, "Mã vị trí công việc không được để trống!");
            }
            newJobPosition.setJobCode(jobPositionDTO.getJobCode());
            if (jobPositionDTO.getJobName() == null || jobPositionDTO.getJobName().trim().isEmpty()) {
                throw new AppException(ErrorCode.INPUT_INVALID, "Tên vị trí công việc không được để trống!");
            }
            newJobPosition.setJobName(jobPositionDTO.getJobName());
            newJobPosition.setDepartmentCode(jobPositionDTO.getDepartmentCode());
            newJobPosition.setDescription(jobPositionDTO.getDescription());
            jobPositionRepository.save(newJobPosition);
            return newJobPosition;
        }else {
            throw new AppException(ErrorCode.NOT_EXISTS, "Job Position already exists");
        }
    }

    public JobPosition updateJobPosition(String jobCode, JobPositionDTO jobPositionDTO) {
        JobPosition oldJobPosition = getJobPosition(jobCode);
        if (oldJobPosition == null) {
            throw new AppException(ErrorCode.NOT_EXISTS, "Vị trí công việc không tồn tại");
        }
        if (jobPositionDTO.getJobCode() == null || jobPositionDTO.getJobCode().trim().isEmpty()) {
            throw new AppException(ErrorCode.INPUT_INVALID, "Mã vị trí công việc không được để trống!");
        }
        oldJobPosition.setJobName(jobPositionDTO.getJobName());
        oldJobPosition.setDepartmentCode(jobPositionDTO.getDepartmentCode());
        oldJobPosition.setDescription(jobPositionDTO.getDescription());

        jobPositionRepository.save(oldJobPosition);
        return oldJobPosition;
    }

    public void deleteJobPosition(String jobCode) {
        JobPosition jobPosition = getJobPosition(jobCode);
        if (jobPosition == null) {
            throw new AppException(ErrorCode.NOT_EXISTS, "Vị trí công việc không tồn tại");
        }
        jobPositionRepository.delete(jobPosition);
    }

    public Map<String, List<JobPosition>> getJobPositionWithDepartment() {
        List<JobPosition> jobPositions = getJobPositions();
        Map<String, List<JobPosition>> jobPositionMap = new HashMap<>();
        for (JobPosition job : jobPositions) {
            String departmentCode = (job.getDepartmentCode() == null || job.getDepartmentCode().trim().isEmpty())
                    ? "no_dept"
                    : job.getDepartmentCode();

            jobPositionMap
                    .computeIfAbsent(departmentCode, k -> new ArrayList<>())
                    .add(job);
        }
        return jobPositionMap;
    }
}
