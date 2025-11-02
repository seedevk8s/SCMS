package com.university.scms.domain.competency.repository;

import com.university.scms.domain.competency.entity.CompetencyResult;
import com.university.scms.domain.competency.entity.CompetencySurvey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * CompetencyResult 리포지토리
 * 역량 평가 결과에 대한 데이터 액세스를 담당합니다.
 * 
 * MSA 전환 대비:
 * - Competency Domain의 핵심 리포지토리
 * - userId는 ID로만 참조 (Auth Domain)
 * - CompetencySurvey와 JPA 관계 유지 (같은 도메인)
 */
@Repository
public interface CompetencyResultRepository extends JpaRepository<CompetencyResult, Long> {

    // ========== 기본 조회 메서드 ==========
    
    /**
     * 설문과 사용자로 결과 조회 (Unique)
     */
    Optional<CompetencyResult> findBySurveyAndUserId(CompetencySurvey survey, Long userId);
    
    /**
     * 설문 ID와 사용자 ID로 결과 조회
     */
    @Query("SELECT r FROM CompetencyResult r WHERE r.survey.id = :surveyId AND r.userId = :userId")
    Optional<CompetencyResult> findBySurveyIdAndUserId(
        @Param("surveyId") Long surveyId,
        @Param("userId") Long userId);
    
    /**
     * 설문과 사용자로 결과 존재 여부 확인
     */
    boolean existsBySurveyAndUserId(CompetencySurvey survey, Long userId);

    // ========== 사용자별 조회 ==========
    
    /**
     * 특정 사용자의 모든 결과 조회 (최신순)
     */
    List<CompetencyResult> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * 특정 사용자의 최신 n개 결과 조회
     */
    @Query("SELECT r FROM CompetencyResult r WHERE r.userId = :userId " +
           "ORDER BY r.createdAt DESC LIMIT :limit")
    List<CompetencyResult> findTopNByUserId(@Param("userId") Long userId, @Param("limit") int limit);
    
    /**
     * 특정 사용자의 특정 역량 수준 결과 조회
     */
    List<CompetencyResult> findByUserIdAndCompetencyLevel(Long userId, String competencyLevel);

    // ========== 설문별 조회 ==========
    
    /**
     * 특정 설문의 모든 결과 조회
     */
    List<CompetencyResult> findBySurvey(CompetencySurvey survey);
    
    /**
     * 특정 설문 ID의 모든 결과 조회
     */
    @Query("SELECT r FROM CompetencyResult r WHERE r.survey.id = :surveyId " +
           "ORDER BY r.createdAt DESC")
    List<CompetencyResult> findBySurveyIdOrderByCreatedAtDesc(@Param("surveyId") Long surveyId);
    
    /**
     * 특정 설문의 특정 역량 수준 결과 조회
     */
    @Query("SELECT r FROM CompetencyResult r WHERE r.survey.id = :surveyId " +
           "AND r.competencyLevel = :level ORDER BY r.totalScore DESC")
    List<CompetencyResult> findBySurveyIdAndLevel(
        @Param("surveyId") Long surveyId,
        @Param("level") String level);

    // ========== 역량 수준별 조회 ==========
    
    /**
     * 역량 수준별 결과 조회
     */
    List<CompetencyResult> findByCompetencyLevel(String competencyLevel);
    
    /**
     * 특정 설문의 역량 수준별 개수
     */
    @Query("SELECT r.competencyLevel, COUNT(r) FROM CompetencyResult r " +
           "WHERE r.survey.id = :surveyId GROUP BY r.competencyLevel")
    List<Object[]> countByLevelForSurvey(@Param("surveyId") Long surveyId);

    // ========== 점수별 조회 ==========
    
    /**
     * 특정 점수 이상인 결과 조회
     */
    @Query("SELECT r FROM CompetencyResult r WHERE r.totalScore >= :minScore " +
           "ORDER BY r.totalScore DESC")
    List<CompetencyResult> findByTotalScoreGreaterThanEqual(@Param("minScore") Integer minScore);
    
    /**
     * 특정 설문의 점수 범위 결과 조회
     */
    @Query("SELECT r FROM CompetencyResult r WHERE r.survey.id = :surveyId " +
           "AND r.totalScore BETWEEN :minScore AND :maxScore " +
           "ORDER BY r.totalScore DESC")
    List<CompetencyResult> findBySurveyIdAndScoreRange(
        @Param("surveyId") Long surveyId,
        @Param("minScore") Integer minScore,
        @Param("maxScore") Integer maxScore);
    
    /**
     * 특정 설문의 상위 n개 결과 조회
     */
    @Query("SELECT r FROM CompetencyResult r WHERE r.survey.id = :surveyId " +
           "ORDER BY r.totalScore DESC LIMIT :limit")
    List<CompetencyResult> findTopScoresBySurveyId(
        @Param("surveyId") Long surveyId,
        @Param("limit") int limit);

    // ========== 통계 메서드 ==========
    
    /**
     * 특정 설문의 결과 개수
     */
    long countBySurvey(CompetencySurvey survey);
    
    /**
     * 특정 사용자의 결과 개수
     */
    long countByUserId(Long userId);
    
    /**
     * 특정 역량 수준의 결과 개수
     */
    long countByCompetencyLevel(String competencyLevel);
    
    /**
     * 특정 설문의 평균 점수
     */
    @Query("SELECT AVG(r.totalScore) FROM CompetencyResult r WHERE r.survey.id = :surveyId")
    Optional<Double> findAverageScoreBySurveyId(@Param("surveyId") Long surveyId);
    
    /**
     * 특정 설문의 최고 점수
     */
    @Query("SELECT MAX(r.totalScore) FROM CompetencyResult r WHERE r.survey.id = :surveyId")
    Optional<Integer> findMaxScoreBySurveyId(@Param("surveyId") Long surveyId);
    
    /**
     * 특정 설문의 최저 점수
     */
    @Query("SELECT MIN(r.totalScore) FROM CompetencyResult r WHERE r.survey.id = :surveyId")
    Optional<Integer> findMinScoreBySurveyId(@Param("surveyId") Long surveyId);
    
    /**
     * 특정 사용자의 평균 점수
     */
    @Query("SELECT AVG(r.totalScore) FROM CompetencyResult r WHERE r.userId = :userId")
    Optional<Double> findAverageScoreByUserId(@Param("userId") Long userId);

    // ========== 사용자 역량 추이 조회 ==========
    
    /**
     * 특정 사용자의 역량 변화 추이 조회 (시간순)
     */
    @Query("SELECT r FROM CompetencyResult r WHERE r.userId = :userId " +
           "ORDER BY r.createdAt ASC")
    List<CompetencyResult> findUserCompetencyTrend(@Param("userId") Long userId);
    
    /**
     * 특정 사용자의 최근 결과와 이전 결과 비교
     */
    @Query("SELECT r FROM CompetencyResult r WHERE r.userId = :userId " +
           "ORDER BY r.createdAt DESC LIMIT 2")
    List<CompetencyResult> findRecentTwoResultsByUserId(@Param("userId") Long userId);

    // ========== 복합 조회 메서드 ==========
    
    /**
     * 특정 설문의 우수 결과 조회 (고급 수준)
     */
    @Query("SELECT r FROM CompetencyResult r WHERE r.survey.id = :surveyId " +
           "AND r.competencyLevel = '고급' ORDER BY r.totalScore DESC")
    List<CompetencyResult> findExcellentResultsBySurveyId(@Param("surveyId") Long surveyId);
    
    /**
     * 특정 설문의 개선 필요 결과 조회 (초급 수준)
     */
    @Query("SELECT r FROM CompetencyResult r WHERE r.survey.id = :surveyId " +
           "AND r.competencyLevel = '초급' ORDER BY r.totalScore ASC")
    List<CompetencyResult> findImprovementNeededResultsBySurveyId(@Param("surveyId") Long surveyId);
    
    /**
     * 여러 사용자의 결과 일괄 조회
     */
    @Query("SELECT r FROM CompetencyResult r WHERE r.survey.id = :surveyId " +
           "AND r.userId IN :userIds ORDER BY r.totalScore DESC")
    List<CompetencyResult> findBySurveyIdAndUserIdIn(
        @Param("surveyId") Long surveyId,
        @Param("userIds") List<Long> userIds);

    // ========== 분석용 메서드 ==========
    
    /**
     * 특정 설문의 점수 분포 조회 (10점 단위)
     */
    @Query("SELECT FLOOR(r.totalScore / 10) * 10, COUNT(r) " +
           "FROM CompetencyResult r WHERE r.survey.id = :surveyId " +
           "GROUP BY FLOOR(r.totalScore / 10) * 10 ORDER BY FLOOR(r.totalScore / 10) * 10")
    List<Object[]> findScoreDistributionBySurveyId(@Param("surveyId") Long surveyId);
    
    /**
     * 전체 사용자의 최신 역량 수준 분포
     */
    @Query("SELECT r.competencyLevel, COUNT(DISTINCT r.userId) FROM CompetencyResult r " +
           "WHERE r.id IN (SELECT MAX(r2.id) FROM CompetencyResult r2 GROUP BY r2.userId) " +
           "GROUP BY r.competencyLevel")
    List<Object[]> findLatestCompetencyLevelDistribution();
}
