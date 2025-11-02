package com.university.scms.domain.program.repository;

import com.university.scms.domain.program.entity.AttendanceStatus;
import com.university.scms.domain.program.entity.Program;
import com.university.scms.domain.program.entity.ProgramParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 프로그램 참여자 리포지토리
 * 프로그램 참여자 정보에 대한 데이터 액세스를 담당합니다.
 * 
 * MSA 전환 대비:
 * - Program Domain 내부 리포지토리
 * - userId로 Auth Domain 참조
 */
@Repository
public interface ProgramParticipantRepository extends JpaRepository<ProgramParticipant, Long> {

    // ========== 기본 조회 메서드 ==========
    
    /**
     * 프로그램과 사용자로 참여자 조회
     */
    Optional<ProgramParticipant> findByProgramAndUserId(Program program, Long userId);
    
    /**
     * 프로그램 ID와 사용자 ID로 참여자 조회
     */
    @Query("SELECT pp FROM ProgramParticipant pp WHERE pp.program.id = :programId AND pp.userId = :userId")
    Optional<ProgramParticipant> findByProgramIdAndUserId(@Param("programId") Long programId, @Param("userId") Long userId);
    
    /**
     * 신청 ID로 참여자 조회
     */
    Optional<ProgramParticipant> findByApplicationId(Long applicationId);

    // ========== 프로그램별 참여자 조회 ==========
    
    /**
     * 프로그램의 모든 참여자 조회
     */
    List<ProgramParticipant> findByProgram(Program program);
    
    /**
     * 프로그램 ID로 참여자 목록 조회
     */
    List<ProgramParticipant> findByProgramId(Long programId);
    
    /**
     * 프로그램의 특정 출석 상태 참여자 조회
     */
    List<ProgramParticipant> findByProgramAndAttendanceStatus(Program program, AttendanceStatus status);
    
    /**
     * 프로그램 ID와 출석 상태로 참여자 목록 조회
     */
    @Query("SELECT pp FROM ProgramParticipant pp WHERE pp.program.id = :programId AND pp.attendanceStatus = :status")
    List<ProgramParticipant> findByProgramIdAndAttendanceStatus(@Param("programId") Long programId, @Param("status") AttendanceStatus status);

    // ========== 사용자별 참여 조회 ==========
    
    /**
     * 사용자의 모든 참여 이력 조회
     */
    List<ProgramParticipant> findByUserId(Long userId);
    
    /**
     * 사용자의 특정 출석 상태 참여 조회
     */
    List<ProgramParticipant> findByUserIdAndAttendanceStatus(Long userId, AttendanceStatus status);
    
    /**
     * 사용자의 참여 목록을 생성일 기준 내림차순 정렬 조회
     */
    @Query("SELECT pp FROM ProgramParticipant pp WHERE pp.userId = :userId ORDER BY pp.createdAt DESC")
    List<ProgramParticipant> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    // ========== 출석 상태별 조회 ==========
    
    /**
     * 특정 출석 상태의 참여자 목록 조회
     */
    List<ProgramParticipant> findByAttendanceStatus(AttendanceStatus status);
    
    /**
     * 출석 확인된 참여자 목록 조회
     */
    @Query("SELECT pp FROM ProgramParticipant pp WHERE pp.attendanceStatus = 'ATTENDED'")
    List<ProgramParticipant> findAttendedParticipants();
    
    /**
     * 결석 처리된 참여자 목록 조회
     */
    @Query("SELECT pp FROM ProgramParticipant pp WHERE pp.attendanceStatus = 'ABSENT'")
    List<ProgramParticipant> findAbsentParticipants();
    
    /**
     * 등록 상태의 참여자 목록 조회
     */
    @Query("SELECT pp FROM ProgramParticipant pp WHERE pp.attendanceStatus = 'REGISTERED'")
    List<ProgramParticipant> findRegisteredParticipants();

    // ========== 마일리지 관련 조회 ==========
    
    /**
     * 마일리지 미지급 참여자 조회
     */
    @Query("SELECT pp FROM ProgramParticipant pp WHERE pp.mileageAwarded = false AND pp.attendanceStatus = 'ATTENDED'")
    List<ProgramParticipant> findUnpaidMileageParticipants();
    
    /**
     * 프로그램의 마일리지 미지급 참여자 조회
     */
    @Query("SELECT pp FROM ProgramParticipant pp WHERE pp.program.id = :programId AND pp.mileageAwarded = false AND pp.attendanceStatus = 'ATTENDED'")
    List<ProgramParticipant> findUnpaidMileageParticipantsByProgramId(@Param("programId") Long programId);
    
    /**
     * 마일리지 지급 완료 참여자 조회
     */
    @Query("SELECT pp FROM ProgramParticipant pp WHERE pp.mileageAwarded = true")
    List<ProgramParticipant> findPaidMileageParticipants();

    // ========== 후기 및 평가 관련 조회 ==========
    
    /**
     * 후기를 작성한 참여자 조회
     */
    @Query("SELECT pp FROM ProgramParticipant pp WHERE pp.feedback IS NOT NULL AND pp.feedback <> ''")
    List<ProgramParticipant> findParticipantsWithFeedback();
    
    /**
     * 프로그램의 후기를 작성한 참여자 조회
     */
    @Query("SELECT pp FROM ProgramParticipant pp WHERE pp.program.id = :programId AND pp.feedback IS NOT NULL AND pp.feedback <> ''")
    List<ProgramParticipant> findParticipantsWithFeedbackByProgramId(@Param("programId") Long programId);
    
    /**
     * 평가를 남긴 참여자 조회
     */
    @Query("SELECT pp FROM ProgramParticipant pp WHERE pp.rating IS NOT NULL")
    List<ProgramParticipant> findParticipantsWithRating();
    
    /**
     * 프로그램의 평가를 남긴 참여자 조회
     */
    @Query("SELECT pp FROM ProgramParticipant pp WHERE pp.program.id = :programId AND pp.rating IS NOT NULL")
    List<ProgramParticipant> findParticipantsWithRatingByProgramId(@Param("programId") Long programId);
    
    /**
     * 특정 평점 이상의 참여자 조회
     */
    @Query("SELECT pp FROM ProgramParticipant pp WHERE pp.rating >= :minRating")
    List<ProgramParticipant> findByRatingGreaterThanEqual(@Param("minRating") Integer minRating);

    // ========== 출석 확인 관련 조회 ==========
    
    /**
     * 특정 확인자가 처리한 참여자 조회
     */
    List<ProgramParticipant> findByAttendanceConfirmedBy(Long confirmerId);
    
    /**
     * 출석 확인이 완료된 참여자 조회
     */
    @Query("SELECT pp FROM ProgramParticipant pp WHERE pp.attendanceConfirmedBy IS NOT NULL")
    List<ProgramParticipant> findConfirmedParticipants();
    
    /**
     * 출석 미확인 참여자 조회
     */
    @Query("SELECT pp FROM ProgramParticipant pp WHERE pp.attendanceConfirmedBy IS NULL AND pp.attendanceStatus = 'REGISTERED'")
    List<ProgramParticipant> findUnconfirmedParticipants();
    
    /**
     * 특정 기간 동안 출석 확인된 참여자 조회
     */
    @Query("SELECT pp FROM ProgramParticipant pp WHERE pp.attendanceConfirmedAt BETWEEN :startDate AND :endDate")
    List<ProgramParticipant> findByAttendanceConfirmedAtBetween(
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);

    // ========== 통계 및 집계 메서드 ==========
    
    /**
     * 프로그램의 참여자 수 조회
     */
    long countByProgram(Program program);
    
    /**
     * 프로그램 ID로 참여자 수 조회
     */
    @Query("SELECT COUNT(pp) FROM ProgramParticipant pp WHERE pp.program.id = :programId")
    long countByProgramId(@Param("programId") Long programId);
    
    /**
     * 프로그램의 특정 출석 상태 참여자 수 조회
     */
    long countByProgramAndAttendanceStatus(Program program, AttendanceStatus status);
    
    /**
     * 프로그램 ID와 출석 상태로 참여자 수 조회
     */
    @Query("SELECT COUNT(pp) FROM ProgramParticipant pp WHERE pp.program.id = :programId AND pp.attendanceStatus = :status")
    long countByProgramIdAndAttendanceStatus(@Param("programId") Long programId, @Param("status") AttendanceStatus status);
    
    /**
     * 사용자의 참여 수 조회
     */
    long countByUserId(Long userId);
    
    /**
     * 사용자의 특정 출석 상태 참여 수 조회
     */
    long countByUserIdAndAttendanceStatus(Long userId, AttendanceStatus status);
    
    /**
     * 특정 출석 상태의 총 참여자 수 조회
     */
    long countByAttendanceStatus(AttendanceStatus status);
    
    /**
     * 출석한 참여자 수 조회
     */
    @Query("SELECT COUNT(pp) FROM ProgramParticipant pp WHERE pp.attendanceStatus = 'ATTENDED'")
    long countAttendedParticipants();
    
    /**
     * 프로그램의 평균 평점 조회
     */
    @Query("SELECT AVG(pp.rating) FROM ProgramParticipant pp WHERE pp.program.id = :programId AND pp.rating IS NOT NULL")
    Double getAverageRatingByProgramId(@Param("programId") Long programId);
    
    /**
     * 마일리지 미지급 참여자 수 조회
     */
    @Query("SELECT COUNT(pp) FROM ProgramParticipant pp WHERE pp.mileageAwarded = false AND pp.attendanceStatus = 'ATTENDED'")
    long countUnpaidMileageParticipants();

    // ========== 존재 여부 확인 ==========
    
    /**
     * 프로그램과 사용자의 참여 존재 여부 확인
     */
    boolean existsByProgramAndUserId(Program program, Long userId);
    
    /**
     * 프로그램 ID와 사용자 ID로 참여 존재 여부 확인
     */
    @Query("SELECT CASE WHEN COUNT(pp) > 0 THEN true ELSE false END FROM ProgramParticipant pp WHERE pp.program.id = :programId AND pp.userId = :userId")
    boolean existsByProgramIdAndUserId(@Param("programId") Long programId, @Param("userId") Long userId);

    // ========== 복합 조건 조회 ==========
    
    /**
     * 사용자의 특정 프로그램 목록에 대한 참여 조회
     */
    @Query("SELECT pp FROM ProgramParticipant pp WHERE pp.userId = :userId AND pp.program.id IN :programIds")
    List<ProgramParticipant> findByUserIdAndProgramIdIn(@Param("userId") Long userId, @Param("programIds") List<Long> programIds);
    
    /**
     * 특정 출석 상태이면서 마일리지 미지급인 참여자 조회
     */
    @Query("SELECT pp FROM ProgramParticipant pp WHERE pp.attendanceStatus = :status AND pp.mileageAwarded = false")
    List<ProgramParticipant> findByAttendanceStatusAndMileageNotAwarded(@Param("status") AttendanceStatus status);
}
