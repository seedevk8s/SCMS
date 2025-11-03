package com.university.scms.domain.counseling.repository;

import com.university.scms.domain.entity.CounselingReservation;
import com.university.scms.domain.entity.CounselingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * CounselingReservation 리포지토리
 * 상담 예약 정보에 대한 데이터 액세스를 담당합니다.
 * 
 * MSA 전환 대비:
 * - Counseling Domain의 핵심 리포지토리
 * - studentId, counselorId는 User Domain의 ID만 저장
 */
@Repository
public interface CounselingReservationRepository extends JpaRepository<CounselingReservation, Long> {

    // ========== 기본 조회 메서드 ==========
    
    /**
     * 학생 ID로 예약 목록 조회
     */
    List<CounselingReservation> findByStudentId(Long studentId);
    
    /**
     * 상담사 ID로 예약 목록 조회
     */
    List<CounselingReservation> findByCounselorId(Long counselorId);
    
    /**
     * 상태별 예약 목록 조회
     */
    List<CounselingReservation> findByStatus(CounselingStatus status);

    // ========== 학생 관련 조회 ==========
    
    /**
     * 학생의 상태별 예약 목록 조회
     */
    List<CounselingReservation> findByStudentIdAndStatus(Long studentId, CounselingStatus status);
    
    /**
     * 학생의 특정 기간 예약 목록 조회
     */
    @Query("SELECT cr FROM CounselingReservation cr " +
           "WHERE cr.studentId = :studentId " +
           "AND cr.reservationDate BETWEEN :startDate AND :endDate " +
           "ORDER BY cr.reservationDate DESC")
    List<CounselingReservation> findByStudentIdAndDateRange(
            @Param("studentId") Long studentId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * 학생의 예약 건수 조회
     */
    long countByStudentId(Long studentId);
    
    /**
     * 학생의 상태별 예약 건수 조회
     */
    long countByStudentIdAndStatus(Long studentId, CounselingStatus status);

    // ========== 상담사 관련 조회 ==========
    
    /**
     * 상담사의 상태별 예약 목록 조회
     */
    List<CounselingReservation> findByCounselorIdAndStatus(Long counselorId, CounselingStatus status);
    
    /**
     * 상담사의 특정 기간 예약 목록 조회
     */
    @Query("SELECT cr FROM CounselingReservation cr " +
           "WHERE cr.counselorId = :counselorId " +
           "AND cr.reservationDate BETWEEN :startDate AND :endDate " +
           "ORDER BY cr.reservationDate ASC")
    List<CounselingReservation> findByCounselorIdAndDateRange(
            @Param("counselorId") Long counselorId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * 상담사의 예약 건수 조회
     */
    long countByCounselorId(Long counselorId);
    
    /**
     * 상담사의 상태별 예약 건수 조회
     */
    long countByCounselorIdAndStatus(Long counselorId, CounselingStatus status);

    // ========== 날짜 기반 조회 ==========
    
    /**
     * 특정 날짜 이후의 예약 목록 조회
     */
    List<CounselingReservation> findByReservationDateAfter(LocalDateTime dateTime);
    
    /**
     * 특정 날짜 이전의 예약 목록 조회
     */
    List<CounselingReservation> findByReservationDateBefore(LocalDateTime dateTime);
    
    /**
     * 특정 기간의 예약 목록 조회
     */
    @Query("SELECT cr FROM CounselingReservation cr " +
           "WHERE cr.reservationDate BETWEEN :startDate AND :endDate " +
           "ORDER BY cr.reservationDate ASC")
    List<CounselingReservation> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // ========== 상담 유형별 조회 ==========
    
    /**
     * 상담 유형별 예약 목록 조회
     */
    List<CounselingReservation> findByCounselingType(String counselingType);
    
    /**
     * 상담 유형별 예약 건수 조회
     */
    long countByCounselingType(String counselingType);
    
    /**
     * 학생의 상담 유형별 예약 목록 조회
     */
    List<CounselingReservation> findByStudentIdAndCounselingType(Long studentId, String counselingType);

    // ========== 복합 조회 ==========
    
    /**
     * 특정 날짜에 특정 상담사의 예약 목록 조회
     */
    @Query("SELECT cr FROM CounselingReservation cr " +
           "WHERE cr.counselorId = :counselorId " +
           "AND DATE(cr.reservationDate) = DATE(:date) " +
           "ORDER BY cr.reservationDate ASC")
    List<CounselingReservation> findByCounselorIdAndDate(
            @Param("counselorId") Long counselorId,
            @Param("date") LocalDateTime date);
    
    /**
     * 임박한 예약 목록 조회 (24시간 이내, 확정된 예약만)
     */
    @Query("SELECT cr FROM CounselingReservation cr " +
           "WHERE cr.status = 'CONFIRMED' " +
           "AND cr.reservationDate > :now " +
           "AND cr.reservationDate <= :imminentThreshold " +
           "ORDER BY cr.reservationDate ASC")
    List<CounselingReservation> findImminentReservations(
            @Param("now") LocalDateTime now,
            @Param("imminentThreshold") LocalDateTime imminentThreshold);
    
    /**
     * 과거 완료되지 않은 예약 목록 조회
     */
    @Query("SELECT cr FROM CounselingReservation cr " +
           "WHERE cr.reservationDate < :now " +
           "AND cr.status NOT IN ('COMPLETED', 'CANCELLED') " +
           "ORDER BY cr.reservationDate ASC")
    List<CounselingReservation> findPastIncompleteReservations(@Param("now") LocalDateTime now);

    // ========== 통계 조회 ==========
    
    /**
     * 전체 예약 건수 조회
     */
    @Query("SELECT COUNT(cr) FROM CounselingReservation cr")
    long countAllReservations();
    
    /**
     * 상태별 예약 건수 조회
     */
    long countByStatus(CounselingStatus status);
    
    /**
     * 특정 기간의 예약 건수 조회
     */
    @Query("SELECT COUNT(cr) FROM CounselingReservation cr " +
           "WHERE cr.reservationDate BETWEEN :startDate AND :endDate")
    long countByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * 특정 기간의 상태별 예약 건수 조회
     */
    @Query("SELECT COUNT(cr) FROM CounselingReservation cr " +
           "WHERE cr.reservationDate BETWEEN :startDate AND :endDate " +
           "AND cr.status = :status")
    long countByDateRangeAndStatus(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") CounselingStatus status);

    // ========== 존재 여부 확인 ==========
    
    /**
     * 학생의 특정 시간 예약 존재 여부 확인
     */
    @Query("SELECT CASE WHEN COUNT(cr) > 0 THEN true ELSE false END " +
           "FROM CounselingReservation cr " +
           "WHERE cr.studentId = :studentId " +
           "AND cr.reservationDate = :reservationDate " +
           "AND cr.status NOT IN ('CANCELLED')")
    boolean existsByStudentIdAndReservationDate(
            @Param("studentId") Long studentId,
            @Param("reservationDate") LocalDateTime reservationDate);
    
    /**
     * 상담사의 특정 시간 예약 존재 여부 확인
     */
    @Query("SELECT CASE WHEN COUNT(cr) > 0 THEN true ELSE false END " +
           "FROM CounselingReservation cr " +
           "WHERE cr.counselorId = :counselorId " +
           "AND cr.reservationDate = :reservationDate " +
           "AND cr.status NOT IN ('CANCELLED')")
    boolean existsByCounselorIdAndReservationDate(
            @Param("counselorId") Long counselorId,
            @Param("reservationDate") LocalDateTime reservationDate);
}
