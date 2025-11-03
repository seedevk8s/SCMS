package com.university.scms.domain.career.repository;

import com.university.scms.domain.career.entity.CareerPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * CareerPlan 리포지토리
 * 진로 계획 정보에 대한 데이터 액세스를 담당합니다.
 * 
 * MSA 전환 대비:
 * - Career Domain의 핵심 리포지토리
 * - userId로 사용자 참조 (Auth Domain과 분리)
 */
@Repository
public interface CareerPlanRepository extends JpaRepository<CareerPlan, Long> {

    // ========== 기본 조회 메서드 ==========
    
    /**
     * 사용자 ID로 진로 계획 목록 조회
     */
    List<CareerPlan> findByUserId(Long userId);
    
    /**
     * 사용자 ID와 상태로 진로 계획 목록 조회
     */
    List<CareerPlan> findByUserIdAndStatus(Long userId, String status);
    
    /**
     * 사용자의 최신 진로 계획 조회
     */
    @Query("SELECT cp FROM CareerPlan cp WHERE cp.userId = :userId ORDER BY cp.createdAt DESC LIMIT 1")
    Optional<CareerPlan> findLatestByUserId(@Param("userId") Long userId);

    // ========== 상태별 조회 ==========
    
    /**
     * 상태별 진로 계획 목록 조회
     */
    List<CareerPlan> findByStatus(String status);
    
    /**
     * 진행 중인 진로 계획 목록 조회
     */
    @Query("SELECT cp FROM CareerPlan cp WHERE cp.status = 'IN_PROGRESS' ORDER BY cp.createdAt DESC")
    List<CareerPlan> findAllInProgress();
    
    /**
     * 완료된 진로 계획 목록 조회
     */
    @Query("SELECT cp FROM CareerPlan cp WHERE cp.status = 'COMPLETED' ORDER BY cp.updatedAt DESC")
    List<CareerPlan> findAllCompleted();

    // ========== 목표 분야별 조회 ==========
    
    /**
     * 목표 분야로 진로 계획 검색
     */
    List<CareerPlan> findByTargetFieldContaining(String targetField);
    
    /**
     * 사용자 ID와 목표 분야로 진로 계획 조회
     */
    List<CareerPlan> findByUserIdAndTargetFieldContaining(Long userId, String targetField);

    // ========== 날짜 관련 조회 ==========
    
    /**
     * 목표 날짜 이전의 진로 계획 조회
     */
    @Query("SELECT cp FROM CareerPlan cp WHERE cp.targetDate <= :targetDate AND cp.status != 'COMPLETED'")
    List<CareerPlan> findUncompletedBeforeDate(@Param("targetDate") LocalDate targetDate);
    
    /**
     * 특정 기간 내 목표 날짜를 가진 진로 계획 조회
     */
    @Query("SELECT cp FROM CareerPlan cp WHERE cp.targetDate BETWEEN :startDate AND :endDate")
    List<CareerPlan> findByTargetDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * 사용자의 특정 기간 내 진로 계획 조회
     */
    @Query("SELECT cp FROM CareerPlan cp WHERE cp.userId = :userId AND cp.targetDate BETWEEN :startDate AND :endDate")
    List<CareerPlan> findByUserIdAndTargetDateBetween(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    // ========== 검색 메서드 ==========
    
    /**
     * 제목으로 진로 계획 검색 (부분 일치)
     */
    List<CareerPlan> findByTitleContaining(String title);
    
    /**
     * 사용자 ID와 제목으로 진로 계획 검색
     */
    List<CareerPlan> findByUserIdAndTitleContaining(Long userId, String title);

    // ========== 통계 메서드 ==========
    
    /**
     * 사용자의 진로 계획 개수 조회
     */
    long countByUserId(Long userId);
    
    /**
     * 사용자의 상태별 진로 계획 개수 조회
     */
    long countByUserIdAndStatus(Long userId, String status);
    
    /**
     * 상태별 전체 진로 계획 개수 조회
     */
    long countByStatus(String status);
    
    /**
     * 목표 분야별 진로 계획 개수 조회
     */
    @Query("SELECT COUNT(cp) FROM CareerPlan cp WHERE cp.targetField = :targetField")
    long countByTargetField(@Param("targetField") String targetField);

    // ========== 존재 여부 확인 ==========
    
    /**
     * 사용자의 특정 제목 진로 계획 존재 여부 확인
     */
    boolean existsByUserIdAndTitle(Long userId, String title);
}
