package com.university.scms.domain.counseling.entity;

import com.university.scms.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 상담 세션 엔티티
 * 실제 진행된 상담의 상세 기록을 관리합니다.
 * 
 * MSA 전환 대비:
 * - reservation: JPA 관계 (같은 도메인, 외래키 제약조건 제거)
 */
@Entity
@Table(name = "counseling_sessions",
       indexes = {
           @Index(name = "idx_reservation", columnList = "reservation_id"),
           @Index(name = "idx_start_time", columnList = "start_time"),
           @Index(name = "idx_follow_up", columnList = "follow_up_required")
       })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CounselingSession extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== 같은 도메인 관계 (Counseling Domain) ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false,
                foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private CounselingReservation reservation;

    // ========== 세션 시간 정보 ==========
    @Column(name = "start_time")
    private LocalDateTime startTime;  // 세션 시작 시간

    @Column(name = "end_time")
    private LocalDateTime endTime;  // 세션 종료 시간

    @Column(name = "actual_duration")
    private Integer actualDuration;  // 실제 상담 시간 (분 단위)

    // ========== 상담 기록 ==========
    @Column(name = "session_notes", columnDefinition = "TEXT")
    private String sessionNotes;  // 상담 내용 (학생/상담사 공유)

    @Column(name = "counselor_notes", columnDefinition = "TEXT")
    private String counselorNotes;  // 상담사 전용 메모 (비공개)

    // ========== 후속 조치 ==========
    @Column(name = "follow_up_required")
    @Builder.Default
    private Boolean followUpRequired = false;  // 후속 상담 필요 여부

    @Column(name = "next_session_date")
    private LocalDateTime nextSessionDate;  // 다음 상담 예정일

    // ========== 비즈니스 메서드 ==========

    /**
     * 세션 시작
     */
    public void start() {
        if (this.startTime != null) {
            throw new IllegalStateException("이미 시작된 세션입니다.");
        }
        this.startTime = LocalDateTime.now();
    }

    /**
     * 세션 종료
     */
    public void end() {
        if (this.startTime == null) {
            throw new IllegalStateException("시작되지 않은 세션입니다.");
        }
        if (this.endTime != null) {
            throw new IllegalStateException("이미 종료된 세션입니다.");
        }
        
        this.endTime = LocalDateTime.now();
        this.actualDuration = calculateDuration();
        
        // 예약도 완료 처리
        if (this.reservation != null && !this.reservation.isCompleted()) {
            this.reservation.complete();
        }
    }

    /**
     * 실제 상담 시간 계산 (분 단위)
     * 
     * @return 상담 시간 (분)
     */
    private Integer calculateDuration() {
        if (this.startTime == null || this.endTime == null) {
            return null;
        }
        Duration duration = Duration.between(this.startTime, this.endTime);
        return (int) duration.toMinutes();
    }

    /**
     * 상담 기록 업데이트
     * 
     * @param sessionNotes 상담 내용
     * @param counselorNotes 상담사 메모
     */
    public void updateNotes(String sessionNotes, String counselorNotes) {
        this.sessionNotes = sessionNotes;
        this.counselorNotes = counselorNotes;
    }

    /**
     * 상담 내용만 업데이트
     * 
     * @param sessionNotes 상담 내용
     */
    public void updateSessionNotes(String sessionNotes) {
        this.sessionNotes = sessionNotes;
    }

    /**
     * 상담사 메모만 업데이트
     * 
     * @param counselorNotes 상담사 메모
     */
    public void updateCounselorNotes(String counselorNotes) {
        this.counselorNotes = counselorNotes;
    }

    /**
     * 후속 상담 예약
     * 
     * @param nextSessionDate 다음 상담 예정일
     */
    public void scheduleFollowUp(LocalDateTime nextSessionDate) {
        if (nextSessionDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("다음 상담 일시는 현재 시간 이후여야 합니다.");
        }
        this.followUpRequired = true;
        this.nextSessionDate = nextSessionDate;
    }

    /**
     * 후속 상담 취소
     */
    public void cancelFollowUp() {
        this.followUpRequired = false;
        this.nextSessionDate = null;
    }

    /**
     * 세션 진행 중 여부
     * 
     * @return 진행 중이면 true
     */
    public boolean isInProgress() {
        return this.startTime != null && this.endTime == null;
    }

    /**
     * 세션 완료 여부
     * 
     * @return 완료되었으면 true
     */
    public boolean isCompleted() {
        return this.startTime != null && this.endTime != null;
    }

    /**
     * 세션 시작 전 여부
     * 
     * @return 시작 전이면 true
     */
    public boolean isNotStarted() {
        return this.startTime == null;
    }

    /**
     * 후속 상담 필요 여부
     * 
     * @return 필요하면 true
     */
    public boolean needsFollowUp() {
        return this.followUpRequired != null && this.followUpRequired;
    }

    /**
     * 예정된 후속 상담이 있는지 확인
     * 
     * @return 예정된 후속 상담이 있으면 true
     */
    public boolean hasScheduledFollowUp() {
        return this.needsFollowUp() && this.nextSessionDate != null;
    }

    /**
     * 상담 시간 수정 (종료 전에만 가능)
     * 
     * @param startTime 시작 시간
     * @param endTime 종료 시간
     */
    public void updateTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (this.isCompleted()) {
            throw new IllegalStateException("완료된 세션의 시간은 수정할 수 없습니다.");
        }
        
        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("시작 시간은 종료 시간보다 이전이어야 합니다.");
        }
        
        this.startTime = startTime;
        this.endTime = endTime;
        
        if (startTime != null && endTime != null) {
            this.actualDuration = calculateDuration();
        }
    }
}
