package com.university.scms.domain.counseling.entity;

import com.university.scms.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 상담 예약 엔티티
 * 학생의 상담 예약 신청 정보를 관리합니다.
 * 
 * MSA 전환 대비:
 * - studentId, counselorId: User ID만 저장 (다른 도메인, 외래키 없음)
 * - sessions: JPA 관계 (같은 도메인, 외래키 제약조건 제거)
 */
@Entity
@Table(name = "counseling_reservations",
       indexes = {
           @Index(name = "idx_student", columnList = "student_id"),
           @Index(name = "idx_counselor", columnList = "counselor_id"),
           @Index(name = "idx_status", columnList = "status"),
           @Index(name = "idx_reservation_date", columnList = "reservation_date"),
           @Index(name = "idx_counseling_type", columnList = "counseling_type")
       })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CounselingReservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== 다른 도메인 참조 (Auth Domain) ==========
    @Column(name = "student_id", nullable = false)
    private Long studentId;  // 학생 ID

    @Column(name = "counselor_id", nullable = false)
    private Long counselorId;  // 상담사 ID

    // ========== 예약 정보 ==========
    @Column(name = "reservation_date", nullable = false)
    private LocalDateTime reservationDate;  // 예약 일시

    @Column(name = "session_duration", nullable = false)
    @Builder.Default
    private Integer sessionDuration = 60;  // 상담 시간 (분 단위, 기본 60분)

    @Column(name = "counseling_type", length = 50)
    @Builder.Default
    private String counselingType = "GENERAL";  // 상담 유형 (진로, 학업, 심리 등)

    @Column(name = "request_reason", columnDefinition = "TEXT")
    private String requestReason;  // 상담 신청 사유

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private CounselingStatus status = CounselingStatus.PENDING;  // 예약 상태

    // ========== 같은 도메인 관계 (Counseling Domain) ==========
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CounselingSession> sessions = new ArrayList<>();

    // ========== 비즈니스 메서드 ==========

    /**
     * 예약 확정
     */
    public void confirm() {
        if (this.status != CounselingStatus.PENDING) {
            throw new IllegalStateException("대기 중인 예약만 확정할 수 있습니다.");
        }
        this.status = CounselingStatus.CONFIRMED;
    }

    /**
     * 예약 취소
     * 
     * @param reason 취소 사유 (선택사항)
     */
    public void cancel(String reason) {
        if (this.status == CounselingStatus.COMPLETED) {
            throw new IllegalStateException("완료된 상담은 취소할 수 없습니다.");
        }
        if (this.status == CounselingStatus.CANCELLED) {
            throw new IllegalStateException("이미 취소된 예약입니다.");
        }
        this.status = CounselingStatus.CANCELLED;
    }

    /**
     * 상담 완료 처리
     */
    public void complete() {
        if (this.status != CounselingStatus.CONFIRMED) {
            throw new IllegalStateException("확정된 예약만 완료 처리할 수 있습니다.");
        }
        this.status = CounselingStatus.COMPLETED;
    }

    /**
     * 예약 취소 가능 여부 확인
     * 
     * @return 취소 가능하면 true
     */
    public boolean canCancel() {
        // 완료 또는 이미 취소된 예약은 취소 불가
        if (this.status == CounselingStatus.COMPLETED || this.status == CounselingStatus.CANCELLED) {
            return false;
        }
        
        // 예약 일시 24시간 전까지만 취소 가능
        LocalDateTime cancellationDeadline = this.reservationDate.minusHours(24);
        return LocalDateTime.now().isBefore(cancellationDeadline);
    }

    /**
     * 예약 수정 가능 여부 확인
     * 
     * @return 수정 가능하면 true
     */
    public boolean canModify() {
        // 대기 중이거나 확정된 예약만 수정 가능
        if (this.status != CounselingStatus.PENDING && this.status != CounselingStatus.CONFIRMED) {
            return false;
        }
        
        // 예약 일시 48시간 전까지만 수정 가능
        LocalDateTime modificationDeadline = this.reservationDate.minusHours(48);
        return LocalDateTime.now().isBefore(modificationDeadline);
    }

    /**
     * 예약 시간이 지났는지 확인
     * 
     * @return 예약 시간이 지났으면 true
     */
    public boolean isPast() {
        return LocalDateTime.now().isAfter(this.reservationDate);
    }

    /**
     * 예약 임박 여부 확인 (24시간 이내)
     * 
     * @return 24시간 이내면 true
     */
    public boolean isImminent() {
        LocalDateTime imminentThreshold = this.reservationDate.minusHours(24);
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(imminentThreshold) && now.isBefore(this.reservationDate);
    }

    /**
     * 예약 정보 수정
     * 
     * @param reservationDate 새 예약 일시
     * @param sessionDuration 새 상담 시간
     * @param counselingType 새 상담 유형
     * @param requestReason 새 신청 사유
     */
    public void update(
            LocalDateTime reservationDate,
            Integer sessionDuration,
            String counselingType,
            String requestReason) {
        
        if (!canModify()) {
            throw new IllegalStateException("예약을 수정할 수 없습니다.");
        }
        
        this.reservationDate = reservationDate;
        this.sessionDuration = sessionDuration;
        this.counselingType = counselingType;
        this.requestReason = requestReason;
    }

    /**
     * 상담 세션 추가
     * 
     * @param session 추가할 세션
     */
    public void addSession(CounselingSession session) {
        this.sessions.add(session);
    }

    /**
     * 대기 중 여부
     */
    public boolean isPending() {
        return this.status == CounselingStatus.PENDING;
    }

    /**
     * 확정 여부
     */
    public boolean isConfirmed() {
        return this.status == CounselingStatus.CONFIRMED;
    }

    /**
     * 취소 여부
     */
    public boolean isCancelled() {
        return this.status == CounselingStatus.CANCELLED;
    }

    /**
     * 완료 여부
     */
    public boolean isCompleted() {
        return this.status == CounselingStatus.COMPLETED;
    }
}
