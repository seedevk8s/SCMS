package com.university.scms.domain.common.repository;

import com.university.scms.domain.common.CommonCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * CommonCode 리포지토리
 * 시스템 전반의 공통 코드 데이터 액세스를 담당합니다.
 * 
 * MSA 전환 대비:
 * - 모든 도메인에서 참조 가능한 독립적 리포지토리
 * - 코드 그룹별 관리 지원
 */
@Repository
public interface CommonCodeRepository extends JpaRepository<CommonCode, Long> {

    // ========== 기본 조회 메서드 ==========
    
    /**
     * 코드 그룹과 코드 값으로 조회
     */
    Optional<CommonCode> findByCodeGroupAndCodeValue(String codeGroup, String codeValue);
    
    /**
     * 코드 그룹과 코드 값 존재 여부 확인
     */
    boolean existsByCodeGroupAndCodeValue(String codeGroup, String codeValue);

    // ========== 코드 그룹별 조회 ==========
    
    /**
     * 특정 코드 그룹의 모든 코드 조회
     */
    List<CommonCode> findByCodeGroup(String codeGroup);
    
    /**
     * 특정 코드 그룹의 활성화된 코드 조회 (정렬 순서대로)
     */
    @Query("SELECT c FROM CommonCode c WHERE c.codeGroup = :codeGroup AND c.isActive = true AND c.deletedAt IS NULL ORDER BY c.displayOrder ASC, c.codeName ASC")
    List<CommonCode> findActiveCodesByGroup(@Param("codeGroup") String codeGroup);
    
    /**
     * 특정 코드 그룹의 활성화된 코드 개수 조회
     */
    @Query("SELECT COUNT(c) FROM CommonCode c WHERE c.codeGroup = :codeGroup AND c.isActive = true AND c.deletedAt IS NULL")
    long countActiveCodesByGroup(@Param("codeGroup") String codeGroup);

    // ========== 코드 값별 조회 ==========
    
    /**
     * 특정 코드 값을 가진 모든 코드 조회
     */
    List<CommonCode> findByCodeValue(String codeValue);

    // ========== 활성화 상태별 조회 ==========
    
    /**
     * 활성화 여부로 조회
     */
    List<CommonCode> findByIsActive(Boolean isActive);
    
    /**
     * 삭제되지 않은 모든 코드 조회
     */
    @Query("SELECT c FROM CommonCode c WHERE c.deletedAt IS NULL ORDER BY c.codeGroup, c.displayOrder ASC")
    List<CommonCode> findAllNotDeleted();
    
    /**
     * 사용 가능한 모든 코드 조회 (활성화 + 미삭제)
     */
    @Query("SELECT c FROM CommonCode c WHERE c.isActive = true AND c.deletedAt IS NULL ORDER BY c.codeGroup, c.displayOrder ASC")
    List<CommonCode> findAllUsable();

    // ========== 검색 메서드 ==========
    
    /**
     * 코드명으로 검색 (부분 일치)
     */
    List<CommonCode> findByCodeNameContaining(String codeName);
    
    /**
     * 코드 그룹과 코드명으로 검색
     */
    @Query("SELECT c FROM CommonCode c WHERE c.codeGroup = :codeGroup AND c.codeName LIKE %:codeName% AND c.isActive = true AND c.deletedAt IS NULL")
    List<CommonCode> searchByGroupAndName(@Param("codeGroup") String codeGroup, @Param("codeName") String codeName);

    // ========== 통계 메서드 ==========
    
    /**
     * 전체 코드 그룹 목록 조회
     */
    @Query("SELECT DISTINCT c.codeGroup FROM CommonCode c WHERE c.deletedAt IS NULL ORDER BY c.codeGroup")
    List<String> findAllCodeGroups();
    
    /**
     * 비활성화된 코드 개수 조회
     */
    @Query("SELECT COUNT(c) FROM CommonCode c WHERE c.isActive = false")
    long countInactiveCodes();
    
    /**
     * 삭제된 코드 개수 조회
     */
    @Query("SELECT COUNT(c) FROM CommonCode c WHERE c.deletedAt IS NOT NULL")
    long countDeletedCodes();

    // ========== 정렬 순서 관련 ==========
    
    /**
     * 특정 코드 그룹 내 최대 정렬 순서 조회
     */
    @Query("SELECT COALESCE(MAX(c.displayOrder), 0) FROM CommonCode c WHERE c.codeGroup = :codeGroup")
    Integer findMaxDisplayOrderByGroup(@Param("codeGroup") String codeGroup);
}
