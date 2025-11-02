package com.university.scms.domain.common;

import jakarta.persistence.*;
import lombok.*;

/**
 * 공통 코드 엔티티
 * 시스템 전반에서 사용되는 코드성 데이터를 관리합니다.
 * 
 * 코드 그룹 예시:
 * - PROGRAM_TYPE: 프로그램 유형
 * - COMPETENCY_TYPE: 역량 유형
 * - COUNSELING_TYPE: 상담 유형
 * - DEPARTMENT: 학과
 * - GRADE: 학년
 * 
 * MSA 전환 대비:
 * - 독립적인 엔티티로 모든 도메인에서 참조 가능
 */
@Entity
@Table(name = "common_codes", 
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_code_group_value", columnNames = {"code_group", "code_value"})
    },
    indexes = {
        @Index(name = "idx_code_group", columnList = "code_group"),
        @Index(name = "idx_code_value", columnList = "code_value"),
        @Index(name = "idx_is_active", columnList = "is_active")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommonCode extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code_id")
    private Long id;

    // ========== 코드 기본 정보 ==========
    
    @Column(name = "code_group", nullable = false, length = 50)
    private String codeGroup;  // 코드 그룹 (예: PROGRAM_TYPE, COMPETENCY_TYPE)
    
    @Column(name = "code_value", nullable = false, length = 50)
    private String codeValue;  // 코드 값 (예: LECTURE, SEMINAR)
    
    @Column(name = "code_name", nullable = false, length = 100)
    private String codeName;  // 코드명 (예: 강연, 세미나)

    // ========== 코드 상세 정보 ==========
    
    @Column(columnDefinition = "TEXT")
    private String description;  // 코드 설명
    
    @Column(name = "display_order")
    private Integer displayOrder;  // 정렬 순서

    // ========== 코드 상태 ==========
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;  // 활성화 여부
    
    @Column(name = "deleted_at")
    private java.time.LocalDateTime deletedAt;  // Soft Delete 시점

    // ========== 비즈니스 메서드 ==========
    
    /**
     * 코드 활성화
     */
    public void activate() {
        this.isActive = true;
        this.deletedAt = null;
    }

    /**
     * 코드 비활성화
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * 코드 소프트 삭제
     */
    public void softDelete() {
        this.isActive = false;
        this.deletedAt = java.time.LocalDateTime.now();
    }

    /**
     * 삭제된 코드 여부 확인
     */
    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    /**
     * 특정 코드 그룹에 속하는지 확인
     */
    public boolean belongsToGroup(String group) {
        return this.codeGroup != null && this.codeGroup.equals(group);
    }

    /**
     * 특정 코드 값과 일치하는지 확인
     */
    public boolean hasValue(String value) {
        return this.codeValue != null && this.codeValue.equals(value);
    }

    /**
     * 현재 사용 가능한 코드인지 확인
     */
    public boolean isUsable() {
        return this.isActive && !this.isDeleted();
    }

    /**
     * 코드 전체 키 생성 (그룹:값)
     */
    public String getFullCode() {
        return this.codeGroup + ":" + this.codeValue;
    }
}
