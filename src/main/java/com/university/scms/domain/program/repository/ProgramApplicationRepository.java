package com.university.scms.domain.program.repository;

import com.university.scms.domain.program.entity.ApplicationStatus;
import com.university.scms.domain.program.entity.Program;
import com.university.scms.domain.program.entity.ProgramApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 프로그램 신청 리포지토리
 * 프로그램 신청 정보에 대한 데이터 액세스를 담당합니다.
 * 
 * MSA 전환 대비:
 * - Program Domain 내부 리포지토리
 * - userId로 Auth Domain 참조
 */
@Repository
public interface ProgramApplicationRepository extends JpaRepository<ProgramApplication, Long> {

    // ========== 기본 조회 메서드 ==========
    
    /**
     * 프로그램과 사용자로 신청 조회
     */
    Optional<ProgramApplication> findByProgramAndUserId(Program program, Long userId);
    
    /**
     * 프로그램 ID와 사용자 ID로 신청 조회
     */
    @Query("SELECT pa FROM ProgramApplication pa WHERE pa.program.id = :programId AND pa.userId = :userId")
    Optional<ProgramApplication> findByProgramIdAndUserId(@Param("programId") Long programId, @Param("userId") Long userId);
    
    /**
     * 신청 ID 목록으로 신청들 조회
     */
    List<ProgramApplication> findByIdIn(List<Long> ids);

    // ========== 프로그램별 신청 조회 ==========
    
    /**
     * 프로그램의 모든 신청 조회
     */
    List<ProgramApplication> findByProgram(Program program);
    
    /**
     * 프로그램 ID로 신청 목록 조회
     */
    List<ProgramApplication> findByProgramId(Long programId);
    
    /**
     * 프로그램의 특정 상태 신청 조회
     */
    List<ProgramApplication> findByProgramAndStatus(Program program, ApplicationStatus status);
    
    /**
     * 프로그램 ID와 상태로 신청 목록 조회
     */
    @Query("SELECT pa FROM ProgramApplication pa WHERE pa.program.id = :programId AND pa.status = :status")
    List<ProgramApplication> findByProgramIdAndStatus(@Param("programId") Long programId, @Param("status") ApplicationStatus status);

    // ========== 사용자별 신청 조회 ==========
    
    /**
     * 사용자의 모든 신청 조회
     */
    List<ProgramApplication> findByUserId(Long userId);
    
    /**
     * 사용자의 특정 상태 신청 조회
     */
    List<ProgramApplication> findByUserIdAndStatus(Long userId, ApplicationStatus status);
    
    /**
     * 사용자의 신청 목록을 신청일 기준 내림차순 정렬 조회
     */
    @Query("SELECT pa FROM ProgramApplication pa WHERE pa.userId = :userId ORDER BY pa.applicationDate DESC")
    List<ProgramApplication> findByUserIdOrderByApplicationDateDesc(@Param("userId") Long userId);

    // ========== 상태별 조회 ==========
    
    /**
     * 특정 상태의 신청 목록 조회
     */
    List<ProgramApplication> findByStatus(ApplicationStatus status);
    
    /**
     * 대기 중인 신청 목록 조회 (신청일 오름차순)
     */
    @Query("SELECT pa FROM ProgramApplication pa WHERE pa.status = 'PENDING' ORDER BY pa.applicationDate ASC")
    List<ProgramApplication> findPendingApplicationsOrderByApplicationDate();
    
    /**
     * 승인된 신청 목록 조회
     */
    @Query("SELECT pa FROM ProgramApplication pa WHERE pa.status = 'APPROVED'")
    List<ProgramApplication> findApprovedApplications();

    // ========== 날짜 기반 조회 ==========
    
    /**
     * 특정 기간의 신청 조회
     */
    @Query("SELECT pa FROM ProgramApplication pa WHERE pa.applicationDate BETWEEN :startDate AND :endDate")
    List<ProgramApplication> findByApplicationDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * 특정 날짜 이후의 신청 조회
     */
    List<ProgramApplication> findByApplicationDateAfter(LocalDateTime date);
    
    /**
     * 특정 날짜 이전의 신청 조회
     */
    List<ProgramApplication> findByApplicationDateBefore(LocalDateTime date);

    // ========== 검토 관련 조회 ==========
    
    /**
     * 특정 검토자가 처리한 신청 조회
     */
    List<ProgramApplication> findByReviewedBy(Long reviewerId);
    
    /**
     * 검토된 신청 목록 조회
     */
    @Query("SELECT pa FROM ProgramApplication pa WHERE pa.reviewedBy IS NOT NULL")
    List<ProgramApplication> findReviewedApplications();
    
    /**
     * 미검토 신청 목록 조회
     */
    @Query("SELECT pa FROM ProgramApplication pa WHERE pa.reviewedBy IS NULL AND pa.status = 'PENDING'")
    List<ProgramApplication> findUnreviewedApplications();

    // ========== 통계 및 집계 메서드 ==========
    
    /**
     * 프로그램의 신청 수 조회
     */
    long countByProgram(Program program);
    
    /**
     * 프로그램 ID로 신청 수 조회
     */
    @Query("SELECT COUNT(pa) FROM ProgramApplication pa WHERE pa.program.id = :programId")
    long countByProgramId(@Param("programId") Long programId);
    
    /**
     * 프로그램의 특정 상태 신청 수 조회
     */
    long countByProgramAndStatus(Program program, ApplicationStatus status);
    
    /**
     * 프로그램 ID와 상태로 신청 수 조회
     */
    @Query("SELECT COUNT(pa) FROM ProgramApplication pa WHERE pa.program.id = :programId AND pa.status = :status")
    long countByProgramIdAndStatus(@Param("programId") Long programId, @Param("status") ApplicationStatus status);
    
    /**
     * 사용자의 신청 수 조회
     */
    long countByUserId(Long userId);
    
    /**
     * 사용자의 특정 상태 신청 수 조회
     */
    long countByUserIdAndStatus(Long userId, ApplicationStatus status);
    
    /**
     * 특정 상태의 총 신청 수 조회
     */
    long countByStatus(ApplicationStatus status);
    
    /**
     * 대기 중인 신청 수 조회
     */
    @Query("SELECT COUNT(pa) FROM ProgramApplication pa WHERE pa.status = 'PENDING'")
    long countPendingApplications();

    // ========== 존재 여부 확인 ==========
    
    /**
     * 프로그램과 사용자의 신청 존재 여부 확인
     */
    boolean existsByProgramAndUserId(Program program, Long userId);
    
    /**
     * 프로그램 ID와 사용자 ID로 신청 존재 여부 확인
     */
    @Query("SELECT CASE WHEN COUNT(pa) > 0 THEN true ELSE false END FROM ProgramApplication pa WHERE pa.program.id = :programId AND pa.userId = :userId")
    boolean existsByProgramIdAndUserId(@Param("programId") Long programId, @Param("userId") Long userId);

    // ========== 복합 조건 조회 ==========
    
    /**
     * 사용자의 특정 프로그램 목록에 대한 신청 조회
     */
    @Query("SELECT pa FROM ProgramApplication pa WHERE pa.userId = :userId AND pa.program.id IN :programIds")
    List<ProgramApplication> findByUserIdAndProgramIdIn(@Param("userId") Long userId, @Param("programIds") List<Long> programIds);
    
    /**
     * 특정 기간 동안 특정 상태의 신청 조회
     */
    @Query("SELECT pa FROM ProgramApplication pa WHERE pa.status = :status AND pa.applicationDate BETWEEN :startDate AND :endDate")
    List<ProgramApplication> findByStatusAndApplicationDateBetween(
            @Param("status") ApplicationStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
