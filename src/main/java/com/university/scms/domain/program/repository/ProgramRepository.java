package com.university.scms.domain.program.repository;

import com.university.scms.domain.program.entity.Program;
import com.university.scms.domain.program.entity.ProgramStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Program Repository
 * 비교과 프로그램 데이터 접근 계층
 */
@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> {

    // ========== 기본 조회 ==========

    /**
     * 제목으로 프로그램 조회
     */
    Optional<Program> findByTitle(String title);

    /**
     * 제목 존재 여부 확인
     */
    boolean existsByTitle(String title);

    /**
     * 상태로 프로그램 목록 조회
     */
    List<Program> findByStatus(ProgramStatus status);

    /**
     * 상태로 프로그램 목록 조회 (페이징)
     */
    Page<Program> findByStatus(ProgramStatus status, Pageable pageable);

    /**
     * 카테고리로 프로그램 목록 조회
     */
    List<Program> findByCategory(String category);

    /**
     * 카테고리로 프로그램 목록 조회 (페이징)
     */
    Page<Program> findByCategory(String category, Pageable pageable);

    /**
     * 담당자 ID로 프로그램 목록 조회
     */
    List<Program> findByOrganizerId(Long organizerId);

    /**
     * 담당자 ID로 프로그램 목록 조회 (페이징)
     */
    Page<Program> findByOrganizerId(Long organizerId, Pageable pageable);

    // ========== 날짜 기반 조회 ==========

    /**
     * 특정 기간 내 시작하는 프로그램 조회
     */
    @Query("SELECT p FROM Program p WHERE p.startDate BETWEEN :startDate AND :endDate")
    List<Program> findByStartDateBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * 특정 날짜 이후 시작하는 프로그램 조회
     */
    List<Program> findByStartDateAfter(LocalDateTime startDate);

    /**
     * 특정 날짜 이전 종료하는 프로그램 조회
     */
    List<Program> findByEndDateBefore(LocalDateTime endDate);

    // ========== 신청 관련 조회 ==========

    /**
     * 신청 가능한 프로그램 조회 (현재 시간 기준)
     */
    @Query("SELECT p FROM Program p " +
           "WHERE p.status = 'OPEN' " +
           "AND p.applicationStart <= :now " +
           "AND p.applicationEnd >= :now " +
           "AND p.currentParticipants < p.capacity")
    List<Program> findAvailablePrograms(@Param("now") LocalDateTime now);

    /**
     * 신청 기간 중인 프로그램 조회
     */
    @Query("SELECT p FROM Program p " +
           "WHERE p.applicationStart <= :now " +
           "AND p.applicationEnd >= :now")
    List<Program> findApplicationOpenPrograms(@Param("now") LocalDateTime now);

    // ========== 진행 상태 조회 ==========

    /**
     * 진행 중인 프로그램 조회
     */
    @Query("SELECT p FROM Program p " +
           "WHERE p.status = 'OPEN' " +
           "AND p.startDate <= :now " +
           "AND p.endDate >= :now")
    List<Program> findInProgressPrograms(@Param("now") LocalDateTime now);

    /**
     * 완료된 프로그램 조회
     */
    @Query("SELECT p FROM Program p " +
           "WHERE p.status = 'COMPLETED' " +
           "OR p.endDate < :now")
    List<Program> findCompletedPrograms(@Param("now") LocalDateTime now);

    // ========== 검색 ==========

    /**
     * 제목으로 프로그램 검색 (포함)
     */
    List<Program> findByTitleContaining(String title);

    /**
     * 제목으로 프로그램 검색 (포함, 페이징)
     */
    Page<Program> findByTitleContaining(String title, Pageable pageable);

    /**
     * 제목 또는 설명으로 프로그램 검색
     */
    @Query("SELECT p FROM Program p " +
           "WHERE p.title LIKE %:keyword% " +
           "OR p.description LIKE %:keyword%")
    List<Program> searchByKeyword(@Param("keyword") String keyword);

    /**
     * 제목 또는 설명으로 프로그램 검색 (페이징)
     */
    @Query("SELECT p FROM Program p " +
           "WHERE p.title LIKE %:keyword% " +
           "OR p.description LIKE %:keyword%")
    Page<Program> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // ========== 복합 조건 조회 ==========

    /**
     * 카테고리와 상태로 프로그램 조회
     */
    List<Program> findByCategoryAndStatus(String category, ProgramStatus status);

    /**
     * 담당자와 상태로 프로그램 조회
     */
    List<Program> findByOrganizerIdAndStatus(Long organizerId, ProgramStatus status);

    /**
     * 정원이 찬 프로그램 조회
     */
    @Query("SELECT p FROM Program p WHERE p.currentParticipants >= p.capacity")
    List<Program> findFullPrograms();

    /**
     * 정원이 남은 프로그램 조회
     */
    @Query("SELECT p FROM Program p WHERE p.currentParticipants < p.capacity")
    List<Program> findAvailableSpacePrograms();

    // ========== 통계 조회 ==========

    /**
     * 담당자별 프로그램 개수
     */
    @Query("SELECT COUNT(p) FROM Program p WHERE p.organizerId = :organizerId")
    Long countByOrganizerId(@Param("organizerId") Long organizerId);

    /**
     * 상태별 프로그램 개수
     */
    Long countByStatus(ProgramStatus status);

    /**
     * 카테고리별 프로그램 개수
     */
    Long countByCategory(String category);
}
