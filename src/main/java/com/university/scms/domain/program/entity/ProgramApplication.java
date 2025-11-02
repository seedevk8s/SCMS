package com.university.scms.domain.program.entity;

import com.university.scms.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 프로그램 신청 엔티티
 * 
 * MSA 전환 대비:
 * - program: JPA 관계 (같은 도메인, 외래키 제약조건 제거)
 * - userId, reviewedBy: User ID만 저장 (다른 도메인, 외래키 없음)
 */
@Entity
@Table(name = "program_applications", 
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_program_user", columnNames = {"program_id", "user_id"})
    },
    indexes = {
        @Index(name = "idx_user", columnList = "user_id"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_application_date", columnList = "application_date")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramApplication extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== 같은 도메인 관계 (Program Domain) ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false,
                foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Program program;

    // ========== 다른 도메인 참조 (Auth Domain) ==========
    @Column(name = "user_id", nullable = false)
    private Long userId;  // 신청자 ID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(name = "application_date", nullable = false)
    @Builder.Default
    private LocalDateTime applicationDate = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String motivation;  // 신청 동기

    @Column(name = "reviewed_by")
    private Long reviewedBy;  // 검토자 ID (교직원)

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    // ========== 비즈니스 메서드 ==========

    /**
     * 신청 승인
     */
    public void approve(Long reviewerId) {
        this.status = ApplicationStatus.APPROVED;
        this.reviewedBy = reviewerId;
        this.reviewedAt = LocalDateTime.now();
    }

    /**
     * 신청 거부
     */
    public void reject(Long reviewerId, String reason) {
        this.status = ApplicationStatus.REJECTED;
        this.reviewedBy = reviewerId;
        this.reviewedAt = LocalDateTime.now();
        this.rejectionReason = reason;
    }

    /**
     * 신청 취소
     */
    public void cancel() {
        this.status = ApplicationStatus.CANCELLED;
    }

    /**
     * 승인 여부
     */
    public boolean isApproved() {
        return this.status == ApplicationStatus.APPROVED;
    }

    /**
     * 대기 중 여부
     */
    public boolean isPending() {
        return this.status == ApplicationStatus.PENDING;
    }

    /**
     * 거부 여부
     */
    public boolean isRejected() {
        return this.status == ApplicationStatus.REJECTED;
    }

    /**
     * 취소 여부
     */
    public boolean isCancelled() {
        return this.status == ApplicationStatus.CANCELLED;
    }
}
