package com.university.scms.domain.counseling.repository;

import com.university.scms.domain.counseling.entity.CounselorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * CounselorAvailability 리포지토리
 * 상담사 가용 시간 정보에 대한 데이터 액세스를 담당합니다.
 * 
 * MSA 전환 대비:
 * - Counseling Domain의 리포지토리
 * - counselorId는 User Domain의 ID만 저장
 */
@Repository
public interface CounselorAvailabilityRepository extends JpaRepository<CounselorAvailability, Long> {

    // ========== 기본 조회 메서드 ==========
    
    /**
     * 상담사 ID로 가용 시간 목록 조회
     */
    List<CounselorAvailability> findByCounselorId(Long counselorId);
    
    /**
     * 요일별 가용 시간 목록 조회
     */
    List<CounselorAvailability> findByDayOfWeek(DayOfWeek dayOfWeek);
    
    /**
     * 가용 상태별 조회
     */
    List<CounselorAvailability> findByIsAvailable(Boolean isAvailable);

    // ========== 상담사별 조회 ==========
    
    /**
     * 상담사의 요일별 가용 시간 조회
     */
    List<CounselorAvailability> findByCounselorIdAndDayOfWeek(Long counselorId, DayOfWeek dayOfWeek);
    
    /**
     * 상담사의 활성화된 가용 시간 조회
     */
    @Query("SELECT ca FROM CounselorAvailability ca " +
           "WHERE ca.counselorId = :counselorId " +
           "AND ca.isAvailable = true " +
           "ORDER BY ca.dayOfWeek, ca.startTime")
    List<CounselorAvailability> findActiveByCounselorId(@Param("counselorId") Long counselorId);
    
    /**
     * 상담사의 특정 요일 활성화된 가용 시간 조회
     */
    @Query("SELECT ca FROM CounselorAvailability ca " +
           "WHERE ca.counselorId = :counselorId " +
           "AND ca.dayOfWeek = :dayOfWeek " +
           "AND ca.isAvailable = true " +
           "ORDER BY ca.startTime")
    List<CounselorAvailability> findActiveByCounselorIdAndDayOfWeek(
            @Param("counselorId") Long counselorId,
            @Param("dayOfWeek") DayOfWeek dayOfWeek);

    // ========== 시간 범위 검색 ==========
    
    /**
     * 특정 시간에 가용한 상담사 ID 목록 조회
     */
    @Query("SELECT DISTINCT ca.counselorId FROM CounselorAvailability ca " +
           "WHERE ca.dayOfWeek = :dayOfWeek " +
           "AND ca.isAvailable = true " +
           "AND ca.startTime <= :time " +
           "AND ca.endTime >= :time")
    List<Long> findAvailableCounselorIdsByDayAndTime(
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("time") LocalTime time);
    
    /**
     * 상담사의 특정 시간 가용 여부 확인
     */
    @Query("SELECT CASE WHEN COUNT(ca) > 0 THEN true ELSE false END " +
           "FROM CounselorAvailability ca " +
           "WHERE ca.counselorId = :counselorId " +
           "AND ca.dayOfWeek = :dayOfWeek " +
           "AND ca.isAvailable = true " +
           "AND ca.startTime <= :time " +
           "AND ca.endTime >= :time")
    boolean isCounselorAvailableAt(
            @Param("counselorId") Long counselorId,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("time") LocalTime time);
    
    /**
     * 상담사의 시간 범위 겹침 여부 확인
     */
    @Query("SELECT CASE WHEN COUNT(ca) > 0 THEN true ELSE false END " +
           "FROM CounselorAvailability ca " +
           "WHERE ca.counselorId = :counselorId " +
           "AND ca.dayOfWeek = :dayOfWeek " +
           "AND ca.id != :excludeId " +
           "AND ca.endTime >= :startTime " +
           "AND ca.startTime <= :endTime")
    boolean hasTimeOverlap(
            @Param("counselorId") Long counselorId,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("excludeId") Long excludeId);

    // ========== 통계 조회 ==========
    
    /**
     * 상담사의 전체 가용 시간 수 조회
     */
    long countByCounselorId(Long counselorId);
    
    /**
     * 상담사의 활성화된 가용 시간 수 조회
     */
    @Query("SELECT COUNT(ca) FROM CounselorAvailability ca " +
           "WHERE ca.counselorId = :counselorId " +
           "AND ca.isAvailable = true")
    long countActiveByCounselorId(@Param("counselorId") Long counselorId);
    
    /**
     * 요일별 전체 가용 시간 수 조회
     */
    long countByDayOfWeek(DayOfWeek dayOfWeek);
    
    /**
     * 요일별 활성화된 가용 시간 수 조회
     */
    @Query("SELECT COUNT(ca) FROM CounselorAvailability ca " +
           "WHERE ca.dayOfWeek = :dayOfWeek " +
           "AND ca.isAvailable = true")
    long countActiveByDayOfWeek(@Param("dayOfWeek") DayOfWeek dayOfWeek);

    // ========== 상담사 목록 조회 ==========
    
    /**
     * 특정 요일에 가용한 상담사 ID 목록 조회
     */
    @Query("SELECT DISTINCT ca.counselorId FROM CounselorAvailability ca " +
           "WHERE ca.dayOfWeek = :dayOfWeek " +
           "AND ca.isAvailable = true")
    List<Long> findAvailableCounselorIdsByDay(@Param("dayOfWeek") DayOfWeek dayOfWeek);
    
    /**
     * 가용 시간이 있는 모든 상담사 ID 목록 조회
     */
    @Query("SELECT DISTINCT ca.counselorId FROM CounselorAvailability ca " +
           "WHERE ca.isAvailable = true")
    List<Long> findAllAvailableCounselorIds();

    // ========== 존재 여부 확인 ==========
    
    /**
     * 상담사의 가용 시간 존재 여부 확인
     */
    boolean existsByCounselorId(Long counselorId);
    
    /**
     * 상담사의 특정 요일 가용 시간 존재 여부 확인
     */
    boolean existsByCounselorIdAndDayOfWeek(Long counselorId, DayOfWeek dayOfWeek);
    
    /**
     * 상담사의 활성화된 가용 시간 존재 여부 확인
     */
    @Query("SELECT CASE WHEN COUNT(ca) > 0 THEN true ELSE false END " +
           "FROM CounselorAvailability ca " +
           "WHERE ca.counselorId = :counselorId " +
           "AND ca.isAvailable = true")
    boolean hasActiveAvailability(@Param("counselorId") Long counselorId);

    // ========== 삭제 메서드 ==========
    
    /**
     * 상담사의 모든 가용 시간 삭제
     */
    void deleteByCounselorId(Long counselorId);
    
    /**
     * 상담사의 특정 요일 가용 시간 삭제
     */
    void deleteByCounselorIdAndDayOfWeek(Long counselorId, DayOfWeek dayOfWeek);

    // ========== 일괄 조회 ==========
    
    /**
     * 상담사의 주간 스케줄 조회 (모든 요일)
     */
    @Query("SELECT ca FROM CounselorAvailability ca " +
           "WHERE ca.counselorId = :counselorId " +
           "ORDER BY ca.dayOfWeek, ca.startTime")
    List<CounselorAvailability> findWeeklyScheduleByCounselorId(@Param("counselorId") Long counselorId);
    
    /**
     * 여러 상담사의 특정 요일 가용 시간 조회
     */
    @Query("SELECT ca FROM CounselorAvailability ca " +
           "WHERE ca.counselorId IN :counselorIds " +
           "AND ca.dayOfWeek = :dayOfWeek " +
           "AND ca.isAvailable = true " +
           "ORDER BY ca.counselorId, ca.startTime")
    List<CounselorAvailability> findByCounselorIdsAndDayOfWeek(
            @Param("counselorIds") List<Long> counselorIds,
            @Param("dayOfWeek") DayOfWeek dayOfWeek);

    // ========== 시간대별 분석 ==========
    
    /**
     * 특정 시간대에 가용한 상담사 수 조회
     */
    @Query("SELECT COUNT(DISTINCT ca.counselorId) FROM CounselorAvailability ca " +
           "WHERE ca.dayOfWeek = :dayOfWeek " +
           "AND ca.isAvailable = true " +
           "AND ca.startTime <= :time " +
           "AND ca.endTime >= :time")
    long countAvailableCounselorsAt(
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("time") LocalTime time);
}
