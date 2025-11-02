package com.university.scms.domain.program.repository;

import com.university.scms.domain.program.entity.ProgramCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 프로그램 카테고리 리포지토리
 * 프로그램 카테고리 정보에 대한 데이터 액세스를 담당합니다.
 * 
 * MSA 전환 대비:
 * - Program Domain 내부 리포지토리
 * - 독립적으로 관리되는 기준정보
 */
@Repository
public interface ProgramCategoryRepository extends JpaRepository<ProgramCategory, Long> {

    // ========== 기본 조회 메서드 ==========
    
    /**
     * 카테고리명으로 조회
     */
    Optional<ProgramCategory> findByCategoryName(String categoryName);
    
    /**
     * 카테고리명 존재 여부 확인
     */
    boolean existsByCategoryName(String categoryName);
    
    /**
     * 카테고리 ID 목록으로 카테고리들 조회
     */
    List<ProgramCategory> findByIdIn(List<Long> ids);

    // ========== 활성화 상태별 조회 ==========
    
    /**
     * 활성화된 카테고리 목록 조회
     */
    List<ProgramCategory> findByIsActive(Boolean isActive);
    
    /**
     * 활성화된 카테고리 목록을 표시 순서대로 조회
     */
    @Query("SELECT pc FROM ProgramCategory pc WHERE pc.isActive = true ORDER BY pc.displayOrder ASC NULLS LAST, pc.categoryName ASC")
    List<ProgramCategory> findActiveOrderByDisplayOrder();
    
    /**
     * 비활성화된 카테고리 목록 조회
     */
    @Query("SELECT pc FROM ProgramCategory pc WHERE pc.isActive = false")
    List<ProgramCategory> findInactiveCategories();

    // ========== 삭제 상태별 조회 ==========
    
    /**
     * 삭제되지 않은 카테고리 목록 조회
     */
    @Query("SELECT pc FROM ProgramCategory pc WHERE pc.deletedAt IS NULL")
    List<ProgramCategory> findNotDeleted();
    
    /**
     * 삭제된 카테고리 목록 조회
     */
    @Query("SELECT pc FROM ProgramCategory pc WHERE pc.deletedAt IS NOT NULL")
    List<ProgramCategory> findDeleted();
    
    /**
     * 활성화되고 삭제되지 않은 카테고리 목록 조회 (사용 가능한 카테고리)
     */
    @Query("SELECT pc FROM ProgramCategory pc WHERE pc.isActive = true AND pc.deletedAt IS NULL ORDER BY pc.displayOrder ASC NULLS LAST")
    List<ProgramCategory> findUsableCategories();

    // ========== 검색 메서드 ==========
    
    /**
     * 카테고리명으로 검색 (부분 일치)
     */
    List<ProgramCategory> findByCategoryNameContaining(String keyword);
    
    /**
     * 활성화된 카테고리 중 카테고리명으로 검색
     */
    @Query("SELECT pc FROM ProgramCategory pc WHERE pc.isActive = true AND pc.categoryName LIKE %:keyword%")
    List<ProgramCategory> findActiveByCategoryNameContaining(@Param("keyword") String keyword);
    
    /**
     * 설명으로 검색 (부분 일치)
     */
    @Query("SELECT pc FROM ProgramCategory pc WHERE pc.description LIKE %:keyword%")
    List<ProgramCategory> findByDescriptionContaining(@Param("keyword") String keyword);

    // ========== 표시 순서 관련 조회 ==========
    
    /**
     * 모든 카테고리를 표시 순서대로 조회
     */
    @Query("SELECT pc FROM ProgramCategory pc ORDER BY pc.displayOrder ASC NULLS LAST, pc.categoryName ASC")
    List<ProgramCategory> findAllOrderByDisplayOrder();
    
    /**
     * 특정 표시 순서의 카테고리 조회
     */
    Optional<ProgramCategory> findByDisplayOrder(Integer displayOrder);
    
    /**
     * 특정 표시 순서 범위의 카테고리 조회
     */
    @Query("SELECT pc FROM ProgramCategory pc WHERE pc.displayOrder BETWEEN :startOrder AND :endOrder ORDER BY pc.displayOrder ASC")
    List<ProgramCategory> findByDisplayOrderBetween(@Param("startOrder") Integer startOrder, @Param("endOrder") Integer endOrder);
    
    /**
     * 표시 순서가 설정되지 않은 카테고리 조회
     */
    @Query("SELECT pc FROM ProgramCategory pc WHERE pc.displayOrder IS NULL")
    List<ProgramCategory> findWithoutDisplayOrder();

    // ========== 프로그램 수 기반 조회 ==========
    
    /**
     * 프로그램이 있는 카테고리 조회
     */
    @Query("SELECT pc FROM ProgramCategory pc WHERE pc.programCount > 0")
    List<ProgramCategory> findCategoriesWithPrograms();
    
    /**
     * 프로그램이 없는 카테고리 조회
     */
    @Query("SELECT pc FROM ProgramCategory pc WHERE pc.programCount = 0 OR pc.programCount IS NULL")
    List<ProgramCategory> findEmptyCategories();
    
    /**
     * 프로그램 수 기준 내림차순 정렬 조회
     */
    @Query("SELECT pc FROM ProgramCategory pc ORDER BY pc.programCount DESC NULLS LAST")
    List<ProgramCategory> findAllOrderByProgramCountDesc();
    
    /**
     * 활성화된 카테고리를 프로그램 수 기준 내림차순 정렬 조회
     */
    @Query("SELECT pc FROM ProgramCategory pc WHERE pc.isActive = true ORDER BY pc.programCount DESC NULLS LAST")
    List<ProgramCategory> findActiveOrderByProgramCountDesc();
    
    /**
     * 특정 프로그램 수 이상의 카테고리 조회
     */
    @Query("SELECT pc FROM ProgramCategory pc WHERE pc.programCount >= :minCount")
    List<ProgramCategory> findByProgramCountGreaterThanEqual(@Param("minCount") Integer minCount);

    // ========== 색상 및 아이콘 기반 조회 ==========
    
    /**
     * 특정 색상의 카테고리 조회
     */
    List<ProgramCategory> findByColor(String color);
    
    /**
     * 특정 아이콘의 카테고리 조회
     */
    List<ProgramCategory> findByIcon(String icon);
    
    /**
     * 색상이 설정된 카테고리 조회
     */
    @Query("SELECT pc FROM ProgramCategory pc WHERE pc.color IS NOT NULL AND pc.color <> ''")
    List<ProgramCategory> findWithColor();
    
    /**
     * 아이콘이 설정된 카테고리 조회
     */
    @Query("SELECT pc FROM ProgramCategory pc WHERE pc.icon IS NOT NULL AND pc.icon <> ''")
    List<ProgramCategory> findWithIcon();

    // ========== 날짜 기반 조회 ==========
    
    /**
     * 특정 기간에 생성된 카테고리 조회
     */
    @Query("SELECT pc FROM ProgramCategory pc WHERE pc.createdAt BETWEEN :startDate AND :endDate")
    List<ProgramCategory> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * 특정 날짜 이후에 생성된 카테고리 조회
     */
    @Query("SELECT pc FROM ProgramCategory pc WHERE pc.createdAt >= :date")
    List<ProgramCategory> findByCreatedAtAfter(@Param("date") LocalDateTime date);
    
    /**
     * 특정 기간에 수정된 카테고리 조회
     */
    @Query("SELECT pc FROM ProgramCategory pc WHERE pc.updatedAt BETWEEN :startDate AND :endDate")
    List<ProgramCategory> findByUpdatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * 특정 날짜 이후에 삭제된 카테고리 조회
     */
    @Query("SELECT pc FROM ProgramCategory pc WHERE pc.deletedAt >= :date")
    List<ProgramCategory> findByDeletedAtAfter(@Param("date") LocalDateTime date);

    // ========== 통계 및 집계 메서드 ==========
    
    /**
     * 활성화된 카테고리 수 조회
     */
    @Query("SELECT COUNT(pc) FROM ProgramCategory pc WHERE pc.isActive = true")
    long countActiveCategories();
    
    /**
     * 비활성화된 카테고리 수 조회
     */
    @Query("SELECT COUNT(pc) FROM ProgramCategory pc WHERE pc.isActive = false")
    long countInactiveCategories();
    
    /**
     * 사용 가능한 카테고리 수 조회 (활성화되고 삭제되지 않은)
     */
    @Query("SELECT COUNT(pc) FROM ProgramCategory pc WHERE pc.isActive = true AND pc.deletedAt IS NULL")
    long countUsableCategories();
    
    /**
     * 프로그램이 있는 카테고리 수 조회
     */
    @Query("SELECT COUNT(pc) FROM ProgramCategory pc WHERE pc.programCount > 0")
    long countCategoriesWithPrograms();
    
    /**
     * 삭제된 카테고리 수 조회
     */
    @Query("SELECT COUNT(pc) FROM ProgramCategory pc WHERE pc.deletedAt IS NOT NULL")
    long countDeletedCategories();
    
    /**
     * 전체 프로그램 수 합계 조회
     */
    @Query("SELECT SUM(pc.programCount) FROM ProgramCategory pc WHERE pc.isActive = true")
    Long getTotalProgramCount();
    
    /**
     * 카테고리별 평균 프로그램 수 조회
     */
    @Query("SELECT AVG(pc.programCount) FROM ProgramCategory pc WHERE pc.isActive = true AND pc.programCount > 0")
    Double getAverageProgramCountPerCategory();

    // ========== 복합 조건 조회 ==========
    
    /**
     * 활성화되고 프로그램이 있는 카테고리 조회
     */
    @Query("SELECT pc FROM ProgramCategory pc WHERE pc.isActive = true AND pc.programCount > 0 ORDER BY pc.displayOrder ASC NULLS LAST")
    List<ProgramCategory> findActiveWithPrograms();
    
    /**
     * 카테고리명과 활성화 상태로 조회
     */
    Optional<ProgramCategory> findByCategoryNameAndIsActive(String categoryName, Boolean isActive);
    
    /**
     * 활성화 상태와 삭제 여부로 조회
     */
    @Query("SELECT pc FROM ProgramCategory pc WHERE pc.isActive = :isActive AND " +
           "((:includeDeleted = true) OR (:includeDeleted = false AND pc.deletedAt IS NULL))")
    List<ProgramCategory> findByActiveStatus(@Param("isActive") Boolean isActive, @Param("includeDeleted") Boolean includeDeleted);
}
