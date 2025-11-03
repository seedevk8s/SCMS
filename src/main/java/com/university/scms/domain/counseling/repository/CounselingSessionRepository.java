package com.university.scms.domain.counseling.repository;

import com.university.scms.domain.counseling.entity.CounselingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * CounselingSession 리포지토리
 * 상담 세션 기록에 대한 데이터 액세스를 담당합니다.
 * 
 * MSA 전환 대비:
 * - Counseling Domain의 리포지토리
 * - reservation과 JPA 관계 유지 (같은 도메인)
 */
@Repository
public interface CounselingSessionRepository extends JpaRepository<CounselingSession, Long> {

    // ========== 기본 조회 메서드 ==========
    
    /**
     * 예약 ID로 세션 목록 조회
     */
    List<CounselingSession> findByReservationId(Long reservationId);
    
    /**
     * 예약 ID로 세션 존재 여부 확인
     */
    boolean existsByReservationId(Long reservationId);
    
    /**
     * 예약 ID로 세션 개수 조회
     */
    long countByReservationId(Long reservationId);

    // ========== 상태별 조회 ==========
    
    /**
     * 진행 중인 세션 목록 조회
     */
    @Query("SELECT cs FROM CounselingSession cs " +
           "WHERE cs.startTime IS NOT NULL " +
           "AND cs.endTime IS NULL " +
           "ORDER BY cs.startTime DESC")
    List<CounselingSession> findInProgressSessions();
    
    /**
     * 완료된 세션 목록 조회
     */
    @Query("SELECT cs FROM CounselingSession cs " +
           "WHERE cs.startTime IS NOT NULL " +
           "AND cs.endTime IS NOT NULL " +
           "ORDER BY cs.endTime DESC")
    List<CounselingSession> findCompletedSessions();
    
    /**
     * 시작되지 않은 세션 목록 조회
     */
    @Query("SELECT cs FROM CounselingSession cs " +
           "WHERE cs.startTime IS NULL " +
           "ORDER BY cs.createdAt DESC")
    List<CounselingSession> findNotStartedSessions();

    // ========== 날짜 기반 조회 ==========
    
    /**
     * 특정 날짜에 시작된 세션 목록 조회
     */
    @Query("SELECT cs FROM CounselingSession cs " +
           "WHERE DATE(cs.startTime) = DATE(:date) " +
           "ORDER BY cs.startTime ASC")
    List<CounselingSession> findByStartDate(@Param("date") LocalDateTime date);
    
    /**
     * 특정 기간에 시작된 세션 목록 조회
     */
    @Query("SELECT cs FROM CounselingSession cs " +
           "WHERE cs.startTime BETWEEN :startDate AND :endDate " +
           "ORDER BY cs.startTime ASC")
    List<CounselingSession> findByStartTimeBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * 특정 기간에 종료된 세션 목록 조회
     */
    @Query("SELECT cs FROM CounselingSession cs " +
           "WHERE cs.endTime BETWEEN :startDate AND :endDate " +
           "ORDER BY cs.endTime DESC")
    List<CounselingSession> findByEndTimeBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // ========== 후속 상담 관련 조회 ==========
    
    /**
     * 후속 상담이 필요한 세션 목록 조회
     */
    List<CounselingSession> findByFollowUpRequiredTrue();
    
    /**
     * 후속 상담이 예정된 세션 목록 조회
     */
    @Query("SELECT cs FROM CounselingSession cs " +
           "WHERE cs.followUpRequired = true " +
           "AND cs.nextSessionDate IS NOT NULL " +
           "ORDER BY cs.nextSessionDate ASC")
    List<CounselingSession> findSessionsWithScheduledFollowUp();
    
    /**
     * 특정 날짜에 후속 상담이 예정된 세션 목록 조회
     */
    @Query("SELECT cs FROM CounselingSession cs " +
           "WHERE cs.followUpRequired = true " +
           "AND DATE(cs.nextSessionDate) = DATE(:date) " +
           "ORDER BY cs.nextSessionDate ASC")
    List<CounselingSession> findByNextSessionDate(@Param("date") LocalDateTime date);
    
    /**
     * 임박한 후속 상담 세션 목록 조회 (7일 이내)
     */
    @Query("SELECT cs FROM CounselingSession cs " +
           "WHERE cs.followUpRequired = true " +
           "AND cs.nextSessionDate BETWEEN :now AND :weekLater " +
           "ORDER BY cs.nextSessionDate ASC")
    List<CounselingSession> findImminentFollowUpSessions(
            @Param("now") LocalDateTime now,
            @Param("weekLater") LocalDateTime weekLater);

    // ========== 예약과 결합 조회 ==========
    
    /**
     * 학생의 모든 세션 조회 (예약을 통해)
     */
    @Query("SELECT cs FROM CounselingSession cs " +
           "JOIN cs.reservation cr " +
           "WHERE cr.studentId = :studentId " +
           "ORDER BY cs.startTime DESC")
    List<CounselingSession> findByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 상담사의 모든 세션 조회 (예약을 통해)
     */
    @Query("SELECT cs FROM CounselingSession cs " +
           "JOIN cs.reservation cr " +
           "WHERE cr.counselorId = :counselorId " +
           "ORDER BY cs.startTime DESC")
    List<CounselingSession> findByCounselorId(@Param("counselorId") Long counselorId);
    
    /**
     * 학생의 완료된 세션 조회
     */
    @Query("SELECT cs FROM CounselingSession cs " +
           "JOIN cs.reservation cr " +
           "WHERE cr.studentId = :studentId " +
           "AND cs.startTime IS NOT NULL " +
           "AND cs.endTime IS NOT NULL " +
           "ORDER BY cs.endTime DESC")
    List<CounselingSession> findCompletedSessionsByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 상담사의 완료된 세션 조회
     */
    @Query("SELECT cs FROM CounselingSession cs " +
           "JOIN cs.reservation cr " +
           "WHERE cr.counselorId = :counselorId " +
           "AND cs.startTime IS NOT NULL " +
           "AND cs.endTime IS NOT NULL " +
           "ORDER BY cs.endTime DESC")
    List<CounselingSession> findCompletedSessionsByCounselorId(@Param("counselorId") Long counselorId);

    // ========== 통계 조회 ==========
    
    /**
     * 전체 세션 수 조회
     */
    @Query("SELECT COUNT(cs) FROM CounselingSession cs")
    long countAllSessions();
    
    /**
     * 완료된 세션 수 조회
     */
    @Query("SELECT COUNT(cs) FROM CounselingSession cs " +
           "WHERE cs.startTime IS NOT NULL " +
           "AND cs.endTime IS NOT NULL")
    long countCompletedSessions();
    
    /**
     * 진행 중인 세션 수 조회
     */
    @Query("SELECT COUNT(cs) FROM CounselingSession cs " +
           "WHERE cs.startTime IS NOT NULL " +
           "AND cs.endTime IS NULL")
    long countInProgressSessions();
    
    /**
     * 특정 기간의 완료된 세션 수 조회
     */
    @Query("SELECT COUNT(cs) FROM CounselingSession cs " +
           "WHERE cs.endTime BETWEEN :startDate AND :endDate")
    long countCompletedSessionsByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * 학생의 총 세션 수 조회
     */
    @Query("SELECT COUNT(cs) FROM CounselingSession cs " +
           "JOIN cs.reservation cr " +
           "WHERE cr.studentId = :studentId")
    long countByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 상담사의 총 세션 수 조회
     */
    @Query("SELECT COUNT(cs) FROM CounselingSession cs " +
           "JOIN cs.reservation cr " +
           "WHERE cr.counselorId = :counselorId")
    long countByCounselorId(@Param("counselorId") Long counselorId);

    // ========== 평균 통계 ==========
    
    /**
     * 전체 평균 상담 시간 조회 (분 단위)
     */
    @Query("SELECT AVG(cs.actualDuration) FROM CounselingSession cs " +
           "WHERE cs.actualDuration IS NOT NULL")
    Double findAverageSessionDuration();
    
    /**
     * 상담사별 평균 상담 시간 조회 (분 단위)
     */
    @Query("SELECT AVG(cs.actualDuration) FROM CounselingSession cs " +
           "JOIN cs.reservation cr " +
           "WHERE cr.counselorId = :counselorId " +
           "AND cs.actualDuration IS NOT NULL")
    Double findAverageSessionDurationByCounselorId(@Param("counselorId") Long counselorId);

    // ========== 최근 조회 ==========
    
    /**
     * 최근 완료된 세션 조회 (limit 개수만큼)
     */
    @Query("SELECT cs FROM CounselingSession cs " +
           "WHERE cs.startTime IS NOT NULL " +
           "AND cs.endTime IS NOT NULL " +
           "ORDER BY cs.endTime DESC " +
           "LIMIT :limit")
    List<CounselingSession> findRecentCompletedSessions(@Param("limit") int limit);
    
    /**
     * 학생의 최근 완료된 세션 조회
     */
    @Query("SELECT cs FROM CounselingSession cs " +
           "JOIN cs.reservation cr " +
           "WHERE cr.studentId = :studentId " +
           "AND cs.startTime IS NOT NULL " +
           "AND cs.endTime IS NOT NULL " +
           "ORDER BY cs.endTime DESC " +
           "LIMIT :limit")
    List<CounselingSession> findRecentCompletedSessionsByStudentId(
            @Param("studentId") Long studentId,
            @Param("limit") int limit);
}
