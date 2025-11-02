package com.university.scms.domain.mileage.repository;

import com.university.scms.domain.mileage.entity.MileageTransaction;
import com.university.scms.domain.mileage.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 마일리지 거래 내역 Repository
 *
 * <p>마일리지 적립, 사용, 소멸, 조정 등의 거래 내역을 관리합니다.</p>
 *
 * <p><b>주요 기능:</b></p>
 * <ul>
 *   <li>계정별 거래 내역 조회</li>
 *   <li>사용자별 거래 내역 조회</li>
 *   <li>거래 유형별 조회 (적립, 사용, 소멸, 조정)</li>
 *   <li>출처별 거래 조회 (프로그램, 인증 등)</li>
 *   <li>기간별 거래 통계</li>
 * </ul>
 *
 * @since 2025-11-02
 */
@Repository
public interface MileageTransactionRepository extends JpaRepository<MileageTransaction, Long> {

    // ========== 계정별 조회 ==========

    /**
     * 특정 마일리지 계정의 모든 거래 내역 조회 (최신순)
     */
    List<MileageTransaction> findByAccountIdOrderByCreatedAtDesc(Long accountId);

    /**
     * 특정 마일리지 계정의 특정 유형 거래 내역 조회
     */
    List<MileageTransaction> findByAccountIdAndTransactionTypeOrderByCreatedAtDesc(
            Long accountId,
            TransactionType transactionType
    );

    // ========== 사용자별 조회 ==========

    /**
     * 특정 사용자의 모든 거래 내역 조회 (최신순)
     */
    List<MileageTransaction> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 특정 사용자의 특정 유형 거래 내역 조회
     */
    List<MileageTransaction> findByUserIdAndTransactionTypeOrderByCreatedAtDesc(
            Long userId,
            TransactionType transactionType
    );

    // ========== 거래 유형별 조회 ==========

    /**
     * 특정 유형의 모든 거래 내역 조회
     */
    List<MileageTransaction> findByTransactionType(TransactionType transactionType);

    /**
     * 적립(EARN) 거래만 조회
     */
    List<MileageTransaction> findByTransactionTypeOrderByCreatedAtDesc(TransactionType transactionType);

    // ========== 출처별 조회 ==========

    /**
     * 특정 출처 타입의 거래 내역 조회
     * @param sourceType 출처 타입 (PROGRAM, CERTIFICATION, ADJUSTMENT 등)
     */
    List<MileageTransaction> findBySourceType(String sourceType);

    /**
     * 특정 출처의 거래 내역 조회
     * @param sourceType 출처 타입
     * @param sourceId 출처 ID
     */
    List<MileageTransaction> findBySourceTypeAndSourceId(String sourceType, Long sourceId);

    /**
     * 사용자의 특정 출처 타입 거래 내역 조회
     */
    List<MileageTransaction> findByUserIdAndSourceType(Long userId, String sourceType);

    // ========== 기간별 조회 ==========

    /**
     * 특정 기간의 거래 내역 조회
     */
    List<MileageTransaction> findByCreatedAtBetween(
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    /**
     * 사용자의 특정 기간 거래 내역 조회
     */
    List<MileageTransaction> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long userId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    /**
     * 계정의 특정 기간 거래 내역 조회
     */
    List<MileageTransaction> findByAccountIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long accountId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    // ========== 포인트 기준 조회 ==========

    /**
     * 특정 포인트 이상의 거래 조회
     */
    List<MileageTransaction> findByPointsGreaterThanEqual(Integer points);

    /**
     * 특정 포인트 이하의 거래 조회
     */
    List<MileageTransaction> findByPointsLessThanEqual(Integer points);

    /**
     * 사용자의 특정 포인트 범위 거래 조회
     */
    List<MileageTransaction> findByUserIdAndPointsBetween(
            Long userId,
            Integer minPoints,
            Integer maxPoints
    );

    // ========== 통계 및 집계 ==========

    /**
     * 사용자의 총 적립 포인트 계산 (EARN만)
     */
    @Query("SELECT COALESCE(SUM(t.points), 0) FROM MileageTransaction t " +
           "WHERE t.userId = :userId AND t.transactionType = 'EARN'")
    Integer getTotalEarnedPoints(@Param("userId") Long userId);

    /**
     * 사용자의 총 사용 포인트 계산 (USE만, 절대값)
     */
    @Query("SELECT COALESCE(SUM(ABS(t.points)), 0) FROM MileageTransaction t " +
           "WHERE t.userId = :userId AND t.transactionType = 'USE'")
    Integer getTotalUsedPoints(@Param("userId") Long userId);

    /**
     * 사용자의 총 소멸 포인트 계산 (EXPIRE만, 절대값)
     */
    @Query("SELECT COALESCE(SUM(ABS(t.points)), 0) FROM MileageTransaction t " +
           "WHERE t.userId = :userId AND t.transactionType = 'EXPIRE'")
    Integer getTotalExpiredPoints(@Param("userId") Long userId);

    /**
     * 계정의 거래 건수 조회
     */
    long countByAccountId(Long accountId);

    /**
     * 사용자의 거래 건수 조회
     */
    long countByUserId(Long userId);

    /**
     * 특정 유형의 거래 건수 조회
     */
    long countByTransactionType(TransactionType transactionType);

    /**
     * 사용자의 특정 유형 거래 건수
     */
    long countByUserIdAndTransactionType(Long userId, TransactionType transactionType);

    // ========== 기간별 통계 ==========

    /**
     * 특정 기간의 총 적립 포인트
     */
    @Query("SELECT COALESCE(SUM(t.points), 0) FROM MileageTransaction t " +
           "WHERE t.transactionType = 'EARN' " +
           "AND t.createdAt BETWEEN :startDate AND :endDate")
    Integer getTotalEarnedPointsByPeriod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * 특정 기간의 총 사용 포인트
     */
    @Query("SELECT COALESCE(SUM(ABS(t.points)), 0) FROM MileageTransaction t " +
           "WHERE t.transactionType = 'USE' " +
           "AND t.createdAt BETWEEN :startDate AND :endDate")
    Integer getTotalUsedPointsByPeriod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * 사용자의 기간별 적립 포인트
     */
    @Query("SELECT COALESCE(SUM(t.points), 0) FROM MileageTransaction t " +
           "WHERE t.userId = :userId AND t.transactionType = 'EARN' " +
           "AND t.createdAt BETWEEN :startDate AND :endDate")
    Integer getUserEarnedPointsByPeriod(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // ========== 최근 거래 조회 ==========

    /**
     * 사용자의 최근 N개 거래 조회
     */
    List<MileageTransaction> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 계정의 최근 N개 거래 조회
     */
    List<MileageTransaction> findTop10ByAccountIdOrderByCreatedAtDesc(Long accountId);

    /**
     * 전체 최근 N개 거래 조회
     */
    List<MileageTransaction> findTop20ByOrderByCreatedAtDesc();

    // ========== 설명 검색 ==========

    /**
     * 설명에 특정 키워드가 포함된 거래 조회
     */
    List<MileageTransaction> findByDescriptionContaining(String keyword);

    /**
     * 사용자의 설명 키워드 검색
     */
    List<MileageTransaction> findByUserIdAndDescriptionContainingOrderByCreatedAtDesc(
            Long userId,
            String keyword
    );

    // ========== 존재 여부 확인 ==========

    /**
     * 특정 출처의 거래가 존재하는지 확인
     */
    boolean existsBySourceTypeAndSourceId(String sourceType, Long sourceId);

    /**
     * 사용자의 특정 유형 거래가 존재하는지 확인
     */
    boolean existsByUserIdAndTransactionType(Long userId, TransactionType transactionType);

    // ========== 출처별 통계 ==========

    /**
     * 특정 출처 타입의 총 포인트 합계
     */
    @Query("SELECT COALESCE(SUM(t.points), 0) FROM MileageTransaction t " +
           "WHERE t.sourceType = :sourceType")
    Integer getTotalPointsBySourceType(@Param("sourceType") String sourceType);

    /**
     * 사용자의 출처 타입별 포인트 합계
     */
    @Query("SELECT COALESCE(SUM(t.points), 0) FROM MileageTransaction t " +
           "WHERE t.userId = :userId AND t.sourceType = :sourceType")
    Integer getUserPointsBySourceType(
            @Param("userId") Long userId,
            @Param("sourceType") String sourceType
    );

    /**
     * 출처별 거래 건수
     */
    long countBySourceType(String sourceType);
}
