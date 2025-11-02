package com.university.scms.domain.program.entity;

import com.university.scms.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 프로그램 카테고리 엔티티
 * 비교과 프로그램의 카테고리를 관리합니다.
 * 
 * 카테고리 예시:
 * - 취업역량강화 (면접, 이력서, 자기소개서)
 * - 진로탐색 (진로 워크샵, 멘토링)
 * - 학술/교양 (특강, 세미나)
 * - 문화/예술 (공연, 전시회)
 * - 봉사활동 (사회봉사, 교내봉사)
 * 
 * MSA 전환 대비:
 * - Program Domain 내부 엔티티
 * - 독립적으로 관리되며 Program에서 ID로만 참조
 */
@Entity
@Table(name = "program_categories", indexes = {
    @Index(name = "idx_category_name", columnList = "category_name"),
    @Index(name = "idx_is_active", columnList = "is_active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    // ========== 카테고리 기본 정보 ==========
    
    @Column(name = "category_name", nullable = false, unique = true, length = 100)
    private String categoryName;  // 카테고리명
    
    @Column(columnDefinition = "TEXT")
    private String description;  // 카테고리 설명

    // ========== 카테고리 표시 설정 ==========
    
    @Column(name = "display_order")
    private Integer displayOrder;  // 표시 순서
    
    @Column(length = 20)
    private String color;  // 표시 색상 (예: #FF5733)
    
    @Column(length = 50)
    private String icon;  // 아이콘 (예: icon-briefcase)

    // ========== 카테고리 상태 ==========
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;  // 활성화 여부
    
    @Column(name = "deleted_at")
    private java.time.LocalDateTime deletedAt;  // Soft Delete 시점

    // ========== 통계 정보 ==========
    
    @Column(name = "program_count")
    @Builder.Default
    private Integer programCount = 0;  // 해당 카테고리의 프로그램 수

    // ========== 비즈니스 메서드 ==========
    
    /**
     * 카테고리 활성화
     */
    public void activate() {
        this.isActive = true;
        this.deletedAt = null;
    }

    /**
     * 카테고리 비활성화
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * 카테고리 소프트 삭제
     */
    public void softDelete() {
        this.isActive = false;
        this.deletedAt = java.time.LocalDateTime.now();
    }

    /**
     * 삭제된 카테고리 여부 확인
     */
    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    /**
     * 현재 사용 가능한 카테고리인지 확인
     */
    public boolean isUsable() {
        return this.isActive && !this.isDeleted();
    }

    /**
     * 프로그램 수 증가
     */
    public void incrementProgramCount() {
        this.programCount++;
    }

    /**
     * 프로그램 수 감소
     */
    public void decrementProgramCount() {
        if (this.programCount > 0) {
            this.programCount--;
        }
    }

    /**
     * 프로그램이 있는지 확인
     */
    public boolean hasPrograms() {
        return this.programCount != null && this.programCount > 0;
    }

    /**
     * 표시 순서 설정
     */
    public void setDisplayOrder(int order) {
        this.displayOrder = order;
    }
}
