package com.devcuong.smart_hr.service;

import com.devcuong.smart_hr.Entity.Team;
import com.devcuong.smart_hr.dto.request.PageFilterInput;
import com.devcuong.smart_hr.exception.AppException;
import com.devcuong.smart_hr.exception.ErrorCode;
import com.devcuong.smart_hr.repository.TeamRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeamService extends SearchService<Team> {

    public TeamService(TeamRepository repository) {
        super(repository);
    }

    public Page<Team> searchTeam(PageFilterInput<Team> input) {
        try {
            Page<Team> page = super.findAll(input);
            List<Team> teams = new ArrayList<>(page.getContent());

            return new PageImpl<>(teams, page.getPageable(), page.getTotalElements());

        } catch (RuntimeException e) {
            throw new AppException(ErrorCode.UNCATEGORIZED, "search fail");
        }
    }

}
