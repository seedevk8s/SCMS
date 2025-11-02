package com.university.scms.domain.competency.repository;

import com.university.scms.domain.competency.entity.CompetencySurvey;
import com.university.scms.domain.program.entity.TargetRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * CompetencySurvey 리포지토리
 * 역량 설문 정보에 대한 데이터 액세스를 담당합니다.
 * 
 * MSA 전환 대비:
 * - Competency Domain의 핵심 리포지토리
 * - createdBy는 userId로만 참조 (Auth Domain)
 */
@Repository
public interface CompetencySurveyRepository extends JpaRepository<CompetencySurvey, Long> {

    // ========== 기본 조회 메서드 ==========
    
    /**
     * 제목으로 설문 조회
     */
    Optional<CompetencySurvey> findByTitle(String title);
    
    /**
     * 활성화 상태로 설문 목록 조회
     */
    List<CompetencySurvey> findByIsActive(Boolean isActive);
    
    /**
     * 설문 유형별 목록 조회
     */
    List<CompetencySurvey> findBySurveyType(String surveyType);

    // ========== 대상 역할별 조회 ==========
    
    /**
     * 대상 역할별 설문 목록 조회
     */
    List<CompetencySurvey> findByTargetRole(TargetRole targetRole);
    
    /**
     * 활성화된 특정 역할 대상 설문 목록 조회
     */
    List<CompetencySurvey> findByTargetRoleAndIsActive(TargetRole targetRole, Boolean isActive);
    
    /**
     * 특정 역할이 응답 가능한 활성 설문 목록 조회 (ALL 포함)
     */
    @Query("SELECT s FROM CompetencySurvey s WHERE s.isActive = true " +
           "AND (s.targetRole = :targetRole OR s.targetRole = 'ALL') " +
           "ORDER BY s.createdAt DESC")
    List<CompetencySurvey> findActiveSurveysForRole(@Param("targetRole") TargetRole targetRole);

    // ========== 기간별 조회 ==========
    
    /**
     * 현재 진행 중인 설문 목록 조회
     */
    @Query("SELECT s FROM CompetencySurvey s WHERE s.isActive = true " +
           "AND s.startDate <= :now AND s.endDate >= :now " +
           "ORDER BY s.endDate ASC")
    List<CompetencySurvey> findOngoingSurveys(@Param("now") LocalDateTime now);
    
    /**
     * 특정 기간에 시작하는 설문 목록 조회
     */
    @Query("SELECT s FROM CompetencySurvey s WHERE s.startDate BETWEEN :startDate AND :endDate " +
           "ORDER BY s.startDate ASC")
    List<CompetencySurvey> findSurveysByStartDateBetween(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);
    
    /**
     * 특정 기간에 종료하는 설문 목록 조회
     */
    @Query("SELECT s FROM CompetencySurvey s WHERE s.endDate BETWEEN :startDate AND :endDate " +
           "ORDER BY s.endDate ASC")
    List<CompetencySurvey> findSurveysByEndDateBetween(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);
    
    /**
     * 종료 예정 설문 목록 조회 (n일 이내)
     */
    @Query("SELECT s FROM CompetencySurvey s WHERE s.isActive = true " +
           "AND s.endDate BETWEEN :now AND :deadline " +
           "ORDER BY s.endDate ASC")
    List<CompetencySurvey> findSurveysEndingSoon(
        @Param("now") LocalDateTime now,
        @Param("deadline") LocalDateTime deadline);

    // ========== 생성자별 조회 ==========
    
    /**
     * 생성자별 설문 목록 조회
     */
    List<CompetencySurvey> findByCreatedBy(Long createdBy);
    
    /**
     * 생성자별 활성 설문 목록 조회
     */
    List<CompetencySurvey> findByCreatedByAndIsActive(Long createdBy, Boolean isActive);

    // ========== 복합 조회 메서드 ==========
    
    /**
     * 설문 유형 및 활성화 상태로 조회
     */
    List<CompetencySurvey> findBySurveyTypeAndIsActive(String surveyType, Boolean isActive);
    
    /**
     * 응답 가능한 설문 목록 조회 (활성화 + 기간 내)
     */
    @Query("SELECT s FROM CompetencySurvey s WHERE s.isActive = true " +
           "AND (s.targetRole = :targetRole OR s.targetRole = 'ALL') " +
           "AND s.startDate <= :now AND s.endDate >= :now " +
           "ORDER BY s.endDate ASC")
    List<CompetencySurvey> findAvailableSurveysForRole(
        @Param("targetRole") TargetRole targetRole,
        @Param("now") LocalDateTime now);

    // ========== 통계 메서드 ==========
    
    /**
     * 활성 설문 수 조회
     */
    long countByIsActive(Boolean isActive);
    
    /**
     * 특정 역할 대상 설문 수 조회
     */
    long countByTargetRole(TargetRole targetRole);
    
    /**
     * 진행 중인 설문 수 조회
     */
    @Query("SELECT COUNT(s) FROM CompetencySurvey s WHERE s.isActive = true " +
           "AND s.startDate <= :now AND s.endDate >= :now")
    long countOngoingSurveys(@Param("now") LocalDateTime now);

    // ========== 검색 메서드 ==========
    
    /**
     * 제목으로 설문 검색 (부분 일치)
     */
    List<CompetencySurvey> findByTitleContaining(String keyword);
    
    /**
     * 제목 또는 설명으로 설문 검색
     */
    @Query("SELECT s FROM CompetencySurvey s WHERE s.title LIKE %:keyword% " +
           "OR s.description LIKE %:keyword% ORDER BY s.createdAt DESC")
    List<CompetencySurvey> searchByKeyword(@Param("keyword") String keyword);
    
    /**
     * 활성 설문에서 키워드 검색
     */
    @Query("SELECT s FROM CompetencySurvey s WHERE s.isActive = true " +
           "AND (s.title LIKE %:keyword% OR s.description LIKE %:keyword%) " +
           "ORDER BY s.createdAt DESC")
    List<CompetencySurvey> searchActiveByKeyword(@Param("keyword") String keyword);
}
