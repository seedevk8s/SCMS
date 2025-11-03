package com.university.scms.domain.career.repository;

import com.university.scms.domain.career.entity.CareerGoal;
import com.university.scms.domain.career.entity.CareerPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * CareerGoal 리포지토리
 * 진로 목표 정보에 대한 데이터 액세스를 담당합니다.
 * 
 * MSA 전환 대비:
 * - Career Domain 내부 리포지토리
 * - CareerPlan과의 관계는 같은 도메인이므로 JPA 매핑 사용
 */
@Repository
public interface CareerGoalRepository extends JpaRepository<CareerGoal, Long> {

    // ========== 기본 조회 메서드 ==========
    
    /**
     * 진로 계획 ID로 목표 목록 조회
     */
    List<CareerGoal> findByCareerPlanId(Long careerPlanId);
    
    /**
     * 진로 계획으로 목표 목록 조회 (정렬)
     */
    @Query("SELECT cg FROM CareerGoal cg WHERE cg.careerPlan.id = :careerPlanId ORDER BY cg.goalOrder ASC")
    List<CareerGoal> findByCareerPlanIdOrderByGoalOrder(@Param("careerPlanId") Long careerPlanId);
    
    /**
     * 진로 계획으로 목표 조회 (엔티티 참조)
     */
    List<CareerGoal> findByCareerPlan(CareerPlan careerPlan);

    // ========== 상태별 조회 ==========
    
    /**
     * 진로 계획의 상태별 목표 조회
     */
    List<CareerGoal> findByCareerPlanIdAndStatus(Long careerPlanId, String status);
    
    /**
     * 진로 계획의 완료된 목표 조회
     */
    @Query("SELECT cg FROM CareerGoal cg WHERE cg.careerPlan.id = :careerPlanId AND cg.status = 'COMPLETED' ORDER BY cg.goalOrder")
    List<CareerGoal> findCompletedGoalsByCareerPlan(@Param("careerPlanId") Long careerPlanId);
    
    /**
     * 진로 계획의 미완료 목표 조회
     */
    @Query("SELECT cg FROM CareerGoal cg WHERE cg.careerPlan.id = :careerPlanId AND cg.status != 'COMPLETED' ORDER BY cg.goalOrder")
    List<CareerGoal> findIncompleteGoalsByCareerPlan(@Param("careerPlanId") Long careerPlanId);
    
    /**
     * 상태별 전체 목표 조회
     */
    List<CareerGoal> findByStatus(String status);

    // ========== 날짜 관련 조회 ==========
    
    /**
     * 목표 날짜 이전의 미완료 목표 조회
     */
    @Query("SELECT cg FROM CareerGoal cg WHERE cg.targetDate <= :targetDate AND cg.status != 'COMPLETED'")
    List<CareerGoal> findUncompletedBeforeDate(@Param("targetDate") LocalDate targetDate);
    
    /**
     * 특정 기간 내 목표 날짜를 가진 목표 조회
     */
    @Query("SELECT cg FROM CareerGoal cg WHERE cg.targetDate BETWEEN :startDate AND :endDate ORDER BY cg.targetDate ASC")
    List<CareerGoal> findByTargetDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * 진로 계획의 특정 기간 내 목표 조회
     */
    @Query("SELECT cg FROM CareerGoal cg WHERE cg.careerPlan.id = :careerPlanId AND cg.targetDate BETWEEN :startDate AND :endDate")
    List<CareerGoal> findByCareerPlanIdAndTargetDateBetween(
        @Param("careerPlanId") Long careerPlanId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    // ========== 순서 관련 조회 ==========
    
    /**
     * 진로 계획의 다음 목표 순서 조회
     */
    @Query("SELECT COALESCE(MAX(cg.goalOrder), 0) + 1 FROM CareerGoal cg WHERE cg.careerPlan.id = :careerPlanId")
    Integer findNextGoalOrder(@Param("careerPlanId") Long careerPlanId);
    
    /**
     * 특정 순서의 목표 조회
     */
    Optional<CareerGoal> findByCareerPlanIdAndGoalOrder(Long careerPlanId, Integer goalOrder);

    // ========== 검색 메서드 ==========
    
    /**
     * 제목으로 목표 검색 (부분 일치)
     */
    List<CareerGoal> findByTitleContaining(String title);
    
    /**
     * 진로 계획 내에서 제목으로 목표 검색
     */
    List<CareerGoal> findByCareerPlanIdAndTitleContaining(Long careerPlanId, String title);

    // ========== 통계 메서드 ==========
    
    /**
     * 진로 계획의 목표 개수 조회
     */
    long countByCareerPlanId(Long careerPlanId);
    
    /**
     * 진로 계획의 상태별 목표 개수 조회
     */
    long countByCareerPlanIdAndStatus(Long careerPlanId, String status);
    
    /**
     * 진로 계획의 완료율 계산
     */
    @Query("SELECT CASE WHEN COUNT(cg) = 0 THEN 0.0 ELSE " +
           "CAST(COUNT(CASE WHEN cg.status = 'COMPLETED' THEN 1 END) AS double) / COUNT(cg) * 100 END " +
           "FROM CareerGoal cg WHERE cg.careerPlan.id = :careerPlanId")
    Double calculateCompletionRate(@Param("careerPlanId") Long careerPlanId);
    
    /**
     * 상태별 전체 목표 개수 조회
     */
    long countByStatus(String status);

    // ========== 존재 여부 확인 ==========
    
    /**
     * 진로 계획의 특정 제목 목표 존재 여부 확인
     */
    boolean existsByCareerPlanIdAndTitle(Long careerPlanId, String title);
    
    /**
     * 진로 계획의 특정 순서 목표 존재 여부 확인
     */
    boolean existsByCareerPlanIdAndGoalOrder(Long careerPlanId, Integer goalOrder);

    // ========== 삭제 메서드 ==========
    
    /**
     * 진로 계획의 모든 목표 삭제
     */
    void deleteByCareerPlanId(Long careerPlanId);
}
