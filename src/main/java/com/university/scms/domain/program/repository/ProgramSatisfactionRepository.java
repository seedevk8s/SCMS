package com.university.scms.domain.program.repository;

import com.university.scms.domain.program.entity.Program;
import com.university.scms.domain.program.entity.ProgramSatisfaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 프로그램 만족도 리포지토리
 * 프로그램 만족도 조사 결과에 대한 데이터 액세스를 담당합니다.
 * 
 * MSA 전환 대비:
 * - Program Domain 내부 리포지토리
 * - userId로 Auth Domain 참조
 */
@Repository
public interface ProgramSatisfactionRepository extends JpaRepository<ProgramSatisfaction, Long> {

    // ========== 기본 조회 메서드 ==========
    
    /**
     * 프로그램과 사용자로 만족도 조회
     */
    Optional<ProgramSatisfaction> findByProgramAndUserId(Program program, Long userId);
    
    /**
     * 프로그램 ID와 사용자 ID로 만족도 조회
     */
    @Query("SELECT ps FROM ProgramSatisfaction ps WHERE ps.program.id = :programId AND ps.userId = :userId")
    Optional<ProgramSatisfaction> findByProgramIdAndUserId(@Param("programId") Long programId, @Param("userId") Long userId);

    // ========== 프로그램별 만족도 조회 ==========
    
    /**
     * 프로그램의 모든 만족도 조회
     */
    List<ProgramSatisfaction> findByProgram(Program program);
    
    /**
     * 프로그램 ID로 만족도 목록 조회
     */
    List<ProgramSatisfaction> findByProgramId(Long programId);
    
    /**
     * 프로그램의 만족도를 전반적 평점 기준 내림차순 정렬 조회
     */
    @Query("SELECT ps FROM ProgramSatisfaction ps WHERE ps.program.id = :programId ORDER BY ps.overallRating DESC")
    List<ProgramSatisfaction> findByProgramIdOrderByOverallRatingDesc(@Param("programId") Long programId);

    // ========== 사용자별 만족도 조회 ==========
    
    /**
     * 사용자의 모든 만족도 조사 응답 조회
     */
    List<ProgramSatisfaction> findByUserId(Long userId);
    
    /**
     * 사용자의 만족도를 생성일 기준 내림차순 정렬 조회
     */
    @Query("SELECT ps FROM ProgramSatisfaction ps WHERE ps.userId = :userId ORDER BY ps.createdAt DESC")
    List<ProgramSatisfaction> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    // ========== 평점별 조회 ==========
    
    /**
     * 특정 전반적 평점의 만족도 조회
     */
    List<ProgramSatisfaction> findByOverallRating(Integer rating);
    
    /**
     * 전반적 평점이 특정 값 이상인 만족도 조회
     */
    @Query("SELECT ps FROM ProgramSatisfaction ps WHERE ps.overallRating >= :minRating")
    List<ProgramSatisfaction> findByOverallRatingGreaterThanEqual(@Param("minRating") Integer minRating);
    
    /**
     * 전반적 평점이 특정 값 이하인 만족도 조회
     */
    @Query("SELECT ps FROM ProgramSatisfaction ps WHERE ps.overallRating <= :maxRating")
    List<ProgramSatisfaction> findByOverallRatingLessThanEqual(@Param("maxRating") Integer maxRating);
    
    /**
     * 높은 만족도 응답 조회 (4점 이상)
     */
    @Query("SELECT ps FROM ProgramSatisfaction ps WHERE ps.overallRating >= 4")
    List<ProgramSatisfaction> findHighSatisfactionResponses();
    
    /**
     * 낮은 만족도 응답 조회 (2점 이하)
     */
    @Query("SELECT ps FROM ProgramSatisfaction ps WHERE ps.overallRating <= 2")
    List<ProgramSatisfaction> findLowSatisfactionResponses();

    // ========== 프로그램별 평점 조회 ==========
    
    /**
     * 프로그램의 높은 만족도 응답 조회
     */
    @Query("SELECT ps FROM ProgramSatisfaction ps WHERE ps.program.id = :programId AND ps.overallRating >= 4")
    List<ProgramSatisfaction> findHighSatisfactionResponsesByProgramId(@Param("programId") Long programId);
    
    /**
     * 프로그램의 낮은 만족도 응답 조회
     */
    @Query("SELECT ps FROM ProgramSatisfaction ps WHERE ps.program.id = :programId AND ps.overallRating <= 2")
    List<ProgramSatisfaction> findLowSatisfactionResponsesByProgramId(@Param("programId") Long programId);

    // ========== 주관식 응답 조회 ==========
    
    /**
     * 장점 피드백이 있는 만족도 조회
     */
    @Query("SELECT ps FROM ProgramSatisfaction ps WHERE ps.strengths IS NOT NULL AND ps.strengths <> ''")
    List<ProgramSatisfaction> findWithStrengths();
    
    /**
     * 개선사항 피드백이 있는 만족도 조회
     */
    @Query("SELECT ps FROM ProgramSatisfaction ps WHERE ps.improvements IS NOT NULL AND ps.improvements <> ''")
    List<ProgramSatisfaction> findWithImprovements();
    
    /**
     * 프로그램의 장점 피드백이 있는 만족도 조회
     */
    @Query("SELECT ps FROM ProgramSatisfaction ps WHERE ps.program.id = :programId AND ps.strengths IS NOT NULL AND ps.strengths <> ''")
    List<ProgramSatisfaction> findWithStrengthsByProgramId(@Param("programId") Long programId);
    
    /**
     * 프로그램의 개선사항 피드백이 있는 만족도 조회
     */
    @Query("SELECT ps FROM ProgramSatisfaction ps WHERE ps.program.id = :programId AND ps.improvements IS NOT NULL AND ps.improvements <> ''")
    List<ProgramSatisfaction> findWithImprovementsByProgramId(@Param("programId") Long programId);

    // ========== 추천 의향 조회 ==========
    
    /**
     * 추천 의향이 있는 만족도 조회
     */
    List<ProgramSatisfaction> findByWouldRecommend(Boolean wouldRecommend);
    
    /**
     * 프로그램의 추천 의향이 있는 만족도 조회
     */
    @Query("SELECT ps FROM ProgramSatisfaction ps WHERE ps.program.id = :programId AND ps.wouldRecommend = true")
    List<ProgramSatisfaction> findRecommendedByProgramId(@Param("programId") Long programId);
    
    /**
     * 프로그램의 추천 의향이 없는 만족도 조회
     */
    @Query("SELECT ps FROM ProgramSatisfaction ps WHERE ps.program.id = :programId AND ps.wouldRecommend = false")
    List<ProgramSatisfaction> findNotRecommendedByProgramId(@Param("programId") Long programId);

    // ========== 날짜 기반 조회 ==========
    
    /**
     * 특정 기간의 만족도 조회
     */
    @Query("SELECT ps FROM ProgramSatisfaction ps WHERE ps.createdAt BETWEEN :startDate AND :endDate")
    List<ProgramSatisfaction> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * 특정 날짜 이후의 만족도 조회
     */
    @Query("SELECT ps FROM ProgramSatisfaction ps WHERE ps.createdAt >= :date")
    List<ProgramSatisfaction> findByCreatedAtAfter(@Param("date") LocalDateTime date);

    // ========== 통계 및 집계 메서드 ==========
    
    /**
     * 프로그램의 만족도 응답 수 조회
     */
    long countByProgram(Program program);
    
    /**
     * 프로그램 ID로 만족도 응답 수 조회
     */
    @Query("SELECT COUNT(ps) FROM ProgramSatisfaction ps WHERE ps.program.id = :programId")
    long countByProgramId(@Param("programId") Long programId);
    
    /**
     * 사용자의 만족도 응답 수 조회
     */
    long countByUserId(Long userId);
    
    /**
     * 프로그램의 전반적 평균 평점 조회
     */
    @Query("SELECT AVG(ps.overallRating) FROM ProgramSatisfaction ps WHERE ps.program.id = :programId")
    Double getAverageOverallRatingByProgramId(@Param("programId") Long programId);
    
    /**
     * 프로그램의 내용 평균 평점 조회
     */
    @Query("SELECT AVG(ps.contentRating) FROM ProgramSatisfaction ps WHERE ps.program.id = :programId")
    Double getAverageContentRatingByProgramId(@Param("programId") Long programId);
    
    /**
     * 프로그램의 강사 평균 평점 조회
     */
    @Query("SELECT AVG(ps.instructorRating) FROM ProgramSatisfaction ps WHERE ps.program.id = :programId")
    Double getAverageInstructorRatingByProgramId(@Param("programId") Long programId);
    
    /**
     * 프로그램의 시설 평균 평점 조회
     */
    @Query("SELECT AVG(ps.facilityRating) FROM ProgramSatisfaction ps WHERE ps.program.id = :programId")
    Double getAverageFacilityRatingByProgramId(@Param("programId") Long programId);
    
    /**
     * 프로그램의 유용성 평균 평점 조회
     */
    @Query("SELECT AVG(ps.usefulnessRating) FROM ProgramSatisfaction ps WHERE ps.program.id = :programId")
    Double getAverageUsefulnessRatingByProgramId(@Param("programId") Long programId);
    
    /**
     * 프로그램의 전체 평균 만족도 조회 (모든 항목의 평균)
     */
    @Query("SELECT AVG((ps.overallRating + ps.contentRating + ps.instructorRating + ps.facilityRating + ps.usefulnessRating) / 5.0) " +
           "FROM ProgramSatisfaction ps WHERE ps.program.id = :programId")
    Double getTotalAverageRatingByProgramId(@Param("programId") Long programId);
    
    /**
     * 프로그램의 추천 비율 조회 (추천 수 / 전체 응답 수)
     */
    @Query("SELECT CAST(SUM(CASE WHEN ps.wouldRecommend = true THEN 1 ELSE 0 END) AS double) / COUNT(ps) " +
           "FROM ProgramSatisfaction ps WHERE ps.program.id = :programId")
    Double getRecommendationRateByProgramId(@Param("programId") Long programId);
    
    /**
     * 특정 평점 이상의 만족도 응답 수 조회
     */
    @Query("SELECT COUNT(ps) FROM ProgramSatisfaction ps WHERE ps.overallRating >= :minRating")
    long countByOverallRatingGreaterThanEqual(@Param("minRating") Integer minRating);
    
    /**
     * 프로그램의 특정 평점 이상 만족도 응답 수 조회
     */
    @Query("SELECT COUNT(ps) FROM ProgramSatisfaction ps WHERE ps.program.id = :programId AND ps.overallRating >= :minRating")
    long countByProgramIdAndOverallRatingGreaterThanEqual(@Param("programId") Long programId, @Param("minRating") Integer minRating);

    // ========== 존재 여부 확인 ==========
    
    /**
     * 프로그램과 사용자의 만족도 존재 여부 확인
     */
    boolean existsByProgramAndUserId(Program program, Long userId);
    
    /**
     * 프로그램 ID와 사용자 ID로 만족도 존재 여부 확인
     */
    @Query("SELECT CASE WHEN COUNT(ps) > 0 THEN true ELSE false END FROM ProgramSatisfaction ps WHERE ps.program.id = :programId AND ps.userId = :userId")
    boolean existsByProgramIdAndUserId(@Param("programId") Long programId, @Param("userId") Long userId);

    // ========== 복합 조건 조회 ==========
    
    /**
     * 사용자의 특정 프로그램 목록에 대한 만족도 조회
     */
    @Query("SELECT ps FROM ProgramSatisfaction ps WHERE ps.userId = :userId AND ps.program.id IN :programIds")
    List<ProgramSatisfaction> findByUserIdAndProgramIdIn(@Param("userId") Long userId, @Param("programIds") List<Long> programIds);
    
    /**
     * 특정 평점 범위의 만족도 조회
     */
    @Query("SELECT ps FROM ProgramSatisfaction ps WHERE ps.overallRating BETWEEN :minRating AND :maxRating")
    List<ProgramSatisfaction> findByOverallRatingBetween(@Param("minRating") Integer minRating, @Param("maxRating") Integer maxRating);
    
    /**
     * 프로그램의 특정 평점 범위 만족도 조회
     */
    @Query("SELECT ps FROM ProgramSatisfaction ps WHERE ps.program.id = :programId AND ps.overallRating BETWEEN :minRating AND :maxRating")
    List<ProgramSatisfaction> findByProgramIdAndOverallRatingBetween(
            @Param("programId") Long programId, 
            @Param("minRating") Integer minRating, 
            @Param("maxRating") Integer maxRating);
}
