package com.university.scms.domain.mileage.repository;

import com.university.scms.domain.entity.MileageAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 마일리지 계정 Repository
 *
 * <p>사용자별 마일리지 계정 정보를 관리합니다.</p>
 *
 * <p><b>주요 기능:</b></p>
 * <ul>
 *   <li>사용자별 계정 조회</li>
 *   <li>포인트 기준 조회</li>
 *   <li>포인트 통계 및 순위</li>
 *   <li>계정 상태 관리</li>
 * </ul>
 *
 * @since 2025-11-02
 */
@Repository
public interface MileageAccountRepository extends JpaRepository<MileageAccount, Long> {

    // ========== 사용자별 조회 ==========

    /**
     * 사용자 ID로 마일리지 계정 조회
     * @param userId 사용자 ID
     * @return 마일리지 계정 (Optional)
     */
    Optional<MileageAccount> findByUserId(Long userId);

    /**
     * 사용자 ID로 계정 존재 여부 확인
     */
    boolean existsByUserId(Long userId);

    // ========== 포인트 기준 조회 ==========

    /**
     * 사용 가능 포인트가 특정 값 이상인 계정 조회
     */
    List<MileageAccount> findByAvailablePointsGreaterThanEqual(Integer points);

    /**
     * 사용 가능 포인트가 특정 값 이하인 계정 조회
     */
    List<MileageAccount> findByAvailablePointsLessThanEqual(Integer points);

    /**
     * 사용 가능 포인트가 특정 범위인 계정 조회
     */
    List<MileageAccount> findByAvailablePointsBetween(Integer minPoints, Integer maxPoints);

    /**
     * 누적 포인트가 특정 값 이상인 계정 조회
     */
    List<MileageAccount> findByTotalPointsGreaterThanEqual(Integer points);

    /**
     * 누적 포인트가 특정 범위인 계정 조회
     */
    List<MileageAccount> findByTotalPointsBetween(Integer minPoints, Integer maxPoints);

    // ========== 정렬 조회 ==========

    /**
     * 사용 가능 포인트 순 상위 N개 계정 조회 (내림차순)
     */
    List<MileageAccount> findTop10ByOrderByAvailablePointsDesc();

    /**
     * 누적 포인트 순 상위 N개 계정 조회 (내림차순)
     */
    List<MileageAccount> findTop10ByOrderByTotalPointsDesc();

    /**
     * 사용한 포인트 순 상위 N개 계정 조회 (내림차순)
     */
    List<MileageAccount> findTop10ByOrderByUsedPointsDesc();

    /**
     * 전체 계정 사용 가능 포인트 순 조회
     */
    List<MileageAccount> findAllByOrderByAvailablePointsDesc();

    /**
     * 전체 계정 누적 포인트 순 조회
     */
    List<MileageAccount> findAllByOrderByTotalPointsDesc();

    // ========== 통계 및 집계 ==========

    /**
     * 전체 계정의 총 사용 가능 포인트 합계
     */
    @Query("SELECT COALESCE(SUM(a.availablePoints), 0) FROM MileageAccount a")
    Long getTotalAvailablePoints();

    /**
     * 전체 계정의 총 누적 포인트 합계
     */
    @Query("SELECT COALESCE(SUM(a.totalPoints), 0) FROM MileageAccount a")
    Long getTotalAccumulatedPoints();

    /**
     * 전체 계정의 총 사용 포인트 합계
     */
    @Query("SELECT COALESCE(SUM(a.usedPoints), 0) FROM MileageAccount a")
    Long getTotalUsedPoints();

    /**
     * 사용 가능 포인트의 평균
     */
    @Query("SELECT AVG(a.availablePoints) FROM MileageAccount a")
    Double getAverageAvailablePoints();

    /**
     * 누적 포인트의 평균
     */
    @Query("SELECT AVG(a.totalPoints) FROM MileageAccount a")
    Double getAverageTotalPoints();

    /**
     * 사용한 포인트의 평균
     */
    @Query("SELECT AVG(a.usedPoints) FROM MileageAccount a")
    Double getAverageUsedPoints();

    // ========== 특정 조건 카운트 ==========

    /**
     * 사용 가능 포인트가 0보다 큰 활성 계정 수
     */
    long countByAvailablePointsGreaterThan(Integer points);

    /**
     * 누적 포인트가 특정 값 이상인 계정 수
     */
    long countByTotalPointsGreaterThanEqual(Integer points);

    /**
     * 사용 가능 포인트 범위 내 계정 수
     */
    long countByAvailablePointsBetween(Integer minPoints, Integer maxPoints);

    // ========== 순위 조회 ==========

    /**
     * 특정 사용자의 사용 가능 포인트 순위 조회
     * @param userId 사용자 ID
     * @return 순위 (1부터 시작)
     */
    @Query("SELECT COUNT(a) + 1 FROM MileageAccount a " +
           "WHERE a.availablePoints > " +
           "(SELECT ma.availablePoints FROM MileageAccount ma WHERE ma.userId = :userId)")
    Long getRankByAvailablePoints(@Param("userId") Long userId);

    /**
     * 특정 사용자의 누적 포인트 순위 조회
     */
    @Query("SELECT COUNT(a) + 1 FROM MileageAccount a " +
           "WHERE a.totalPoints > " +
           "(SELECT ma.totalPoints FROM MileageAccount ma WHERE ma.userId = :userId)")
    Long getRankByTotalPoints(@Param("userId") Long userId);

    // ========== 포인트 없는 계정 조회 ==========

    /**
     * 사용 가능 포인트가 0인 계정 조회
     */
    List<MileageAccount> findByAvailablePoints(Integer points);

    /**
     * 누적 포인트가 0인 계정 조회
     */
    List<MileageAccount> findByTotalPoints(Integer points);

    /**
     * 사용한 적 없는 계정 조회 (usedPoints = 0)
     */
    List<MileageAccount> findByUsedPoints(Integer points);

    // ========== 포인트 비율 조회 ==========

    /**
     * 사용률이 높은 계정 조회 (usedPoints / totalPoints 비율)
     */
    @Query("SELECT a FROM MileageAccount a " +
           "WHERE a.totalPoints > 0 " +
           "ORDER BY (CAST(a.usedPoints AS double) / a.totalPoints) DESC")
    List<MileageAccount> findAccountsOrderByUsageRate();

    /**
     * 특정 사용률 이상인 계정 조회
     */
    @Query("SELECT a FROM MileageAccount a " +
           "WHERE a.totalPoints > 0 " +
           "AND (CAST(a.usedPoints AS double) / a.totalPoints) >= :rate")
    List<MileageAccount> findByUsageRateGreaterThanEqual(@Param("rate") Double rate);

    // ========== 특정 사용자 목록 조회 ==========

    /**
     * 여러 사용자의 마일리지 계정 일괄 조회
     */
    List<MileageAccount> findByUserIdIn(List<Long> userIds);

    // ========== 최근 생성 계정 ==========

    /**
     * 최근 생성된 N개 계정 조회
     */
    List<MileageAccount> findTop10ByOrderByCreatedAtDesc();

    /**
     * 최근 수정된 N개 계정 조회
     */
    List<MileageAccount> findTop10ByOrderByUpdatedAtDesc();
}
