package com.university.scms.domain.counseling.entity;

import com.university.scms.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 상담사 엔티티
 * User 엔티티를 확장하여 상담사 전용 정보를 저장합니다.
 * 
 * MSA 전환 대비:
 * - counselorId는 Auth Domain의 User ID 참조 (외래키 제약조건 없음)
 */
@Entity
@Table(name = "counselors", indexes = {
    @Index(name = "idx_counselor_id", columnList = "counselor_id"),
    @Index(name = "idx_available_days", columnList = "available_days")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Counselor extends BaseEntity {

    @Id
    @Column(name = "counselor_id")
    private Long counselorId;  // Auth Domain의 User ID (상담사의 User ID)

    // ========== 상담사 전문 정보 ==========
    
    @Column(name = "specialization", length = 100)
    private String specialization;  // 전문분야 (진로, 심리, 학업 등)
    
    @Column(columnDefinition = "TEXT")
    private String introduction;  // 상담사 소개
    
    @Column(name = "available_days", length = 100)
    private String availableDays;  // 가능 요일 (예: "월,수,금" 또는 JSON)

    // ========== 상담사 상태 ==========
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;  // 활성화 여부
    
    @Column(name = "deleted_at")
    private java.time.LocalDateTime deletedAt;  // Soft Delete 시점

    // ========== 비즈니스 메서드 ==========
    
    /**
     * 상담사 활성화
     */
    public void activate() {
        this.isActive = true;
        this.deletedAt = null;
    }

    /**
     * 상담사 비활성화
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * 상담사 소프트 삭제
     */
    public void softDelete() {
        this.isActive = false;
        this.deletedAt = java.time.LocalDateTime.now();
    }

    /**
     * 특정 요일에 상담 가능한지 확인
     * @param dayOfWeek 요일 (예: "월", "화")
     * @return 가능 여부
     */
    public boolean isAvailableOn(String dayOfWeek) {
        if (this.availableDays == null) {
            return false;
        }
        return this.availableDays.contains(dayOfWeek);
    }

    /**
     * 삭제된 상담사 여부 확인
     */
    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    /**
     * 현재 활동 중인 상담사 여부 확인
     */
    public boolean isActiveCounselor() {
        return this.isActive && !this.isDeleted();
    }
}
