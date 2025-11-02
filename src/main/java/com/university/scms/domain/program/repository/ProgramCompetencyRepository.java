package com.university.scms.domain.program.repository;

import com.university.scms.domain.program.entity.Program;
import com.university.scms.domain.program.entity.ProgramCompetency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ProgramCompetency Repository
 * 프로그램-역량 매핑 데이터 접근 계층
 */
@Repository
public interface ProgramCompetencyRepository extends JpaRepository<ProgramCompetency, Long> {

    // ========== 기본 조회 ==========

    /**
     * 프로그램으로 매핑 목록 조회
     */
    List<ProgramCompetency> findByProgram(Program program);

    /**
     * 프로그램 ID로 매핑 목록 조회
     */
    @Query("SELECT pc FROM ProgramCompetency pc WHERE pc.program.id = :programId")
    List<ProgramCompetency> findByProgramId(@Param("programId") Long programId);

    /**
     * 역량 ID로 매핑 목록 조회
     */
    List<ProgramCompetency> findByCompetencyId(Long competencyId);

    /**
     * 프로그램 ID와 역량 ID로 매핑 조회
     */
    @Query("SELECT pc FROM ProgramCompetency pc " +
           "WHERE pc.program.id = :programId " +
           "AND pc.competencyId = :competencyId")
    Optional<ProgramCompetency> findByProgramIdAndCompetencyId(
            @Param("programId") Long programId,
            @Param("competencyId") Long competencyId
    );

    /**
     * 프로그램 ID와 역량 ID로 매핑 존재 여부 확인
     */
    @Query("SELECT CASE WHEN COUNT(pc) > 0 THEN true ELSE false END " +
           "FROM ProgramCompetency pc " +
           "WHERE pc.program.id = :programId " +
           "AND pc.competencyId = :competencyId")
    boolean existsByProgramIdAndCompetencyId(
            @Param("programId") Long programId,
            @Param("competencyId") Long competencyId
    );

    // ========== 가중치 기반 조회 ==========

    /**
     * 프로그램 ID로 가중치 내림차순 정렬 조회
     */
    @Query("SELECT pc FROM ProgramCompetency pc " +
           "WHERE pc.program.id = :programId " +
           "ORDER BY pc.weight DESC")
    List<ProgramCompetency> findByProgramIdOrderByWeightDesc(@Param("programId") Long programId);

    /**
     * 역량 ID로 가중치 내림차순 정렬 조회
     */
    List<ProgramCompetency> findByCompetencyIdOrderByWeightDesc(Long competencyId);

    /**
     * 최소 가중치 이상인 매핑 조회
     */
    @Query("SELECT pc FROM ProgramCompetency pc " +
           "WHERE pc.program.id = :programId " +
           "AND pc.weight >= :minWeight")
    List<ProgramCompetency> findByProgramIdAndWeightGreaterThanEqual(
            @Param("programId") Long programId,
            @Param("minWeight") Integer minWeight
    );

    // ========== 통계 조회 ==========

    /**
     * 프로그램 ID로 매핑 개수 조회
     */
    @Query("SELECT COUNT(pc) FROM ProgramCompetency pc WHERE pc.program.id = :programId")
    Long countByProgramId(@Param("programId") Long programId);

    /**
     * 역량 ID로 매핑 개수 조회
     */
    Long countByCompetencyId(Long competencyId);

    /**
     * 프로그램별 총 가중치 합계
     */
    @Query("SELECT SUM(pc.weight) FROM ProgramCompetency pc WHERE pc.program.id = :programId")
    Long sumWeightByProgramId(@Param("programId") Long programId);

    // ========== 삭제 ==========

    /**
     * 프로그램으로 모든 매핑 삭제
     */
    void deleteByProgram(Program program);

    /**
     * 프로그램 ID로 모든 매핑 삭제
     */
    @Query("DELETE FROM ProgramCompetency pc WHERE pc.program.id = :programId")
    void deleteByProgramId(@Param("programId") Long programId);

    /**
     * 역량 ID로 모든 매핑 삭제
     */
    void deleteByCompetencyId(Long competencyId);

    /**
     * 프로그램 ID와 역량 ID로 매핑 삭제
     */
    @Query("DELETE FROM ProgramCompetency pc " +
           "WHERE pc.program.id = :programId " +
           "AND pc.competencyId = :competencyId")
    void deleteByProgramIdAndCompetencyId(
            @Param("programId") Long programId,
            @Param("competencyId") Long competencyId
    );
}
