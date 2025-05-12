package com.devcuong.smart_hr.repository;

import com.devcuong.smart_hr.Entity.InterviewSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface InterviewSessionRepository extends JpaRepository<InterviewSession, Long> {
    @Query("SELECT s FROM InterviewSession s " +
            "WHERE s.startTime BETWEEN :startOfWeek AND :endOfWeek " +
            "ORDER BY s.startTime ASC")
    List<InterviewSession> findSessionsByWeekRange(
            @Param("startOfWeek") OffsetDateTime startOfWeek,
            @Param("endOfWeek") OffsetDateTime endOfWeek
    );

    @Query("SELECT s FROM InterviewSession s " +
            "WHERE s.startTime >= :startTime AND s.startTime <= :endTime " +
            "ORDER BY s.startTime ASC")
    List<InterviewSession> findSessionsByTimeRange(
            @Param("startTime") OffsetDateTime startTime,
            @Param("endTime") OffsetDateTime endTime
    );



}
