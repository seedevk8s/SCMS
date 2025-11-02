package com.university.scms.domain.program.entity;

import com.university.scms.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 프로그램 참여자 엔티티
 * 신청 승인 후 참여 확정된 사용자
 * 
 * MSA 전환 대비:
 * - program, application: JPA 관계 (같은 도메인, 외래키 제약조건 제거)
 * - userId, attendanceConfirmedBy: User ID만 저장 (다른 도메인, 외래키 없음)
 */
@Entity
@Table(name = "program_participants",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_program_participant", columnNames = {"program_id", "user_id"})
    },
    indexes = {
        @Index(name = "idx_user", columnList = "user_id"),
        @Index(name = "idx_attendance", columnList = "attendance_status"),
        @Index(name = "idx_mileage_awarded", columnList = "mileage_awarded")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramParticipant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== 같은 도메인 관계 (Program Domain) ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false,
                foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Program program;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id",
                foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ProgramApplication application;

    // ========== 다른 도메인 참조 (Auth Domain) ==========
    @Column(name = "user_id", nullable = false)
    private Long userId;  // 참여자 ID

    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_status", nullable = false, length = 20)
    @Builder.Default
    private AttendanceStatus attendanceStatus = AttendanceStatus.REGISTERED;

    @Column(name = "attendance_confirmed_at")
    private LocalDateTime attendanceConfirmedAt;

    @Column(name = "attendance_confirmed_by")
    private Long attendanceConfirmedBy;  // 출석 확인자 ID

    @Column(columnDefinition = "TEXT")
    private String feedback;  // 참여 후기

    @Column
    private Integer rating;  // 1-5점 평가

    @Column(name = "mileage_awarded", nullable = false)
    @Builder.Default
    private Boolean mileageAwarded = false;  // 마일리지 지급 여부

    // ========== 비즈니스 메서드 ==========

    /**
     * 출석 확인
     */
    public void confirmAttendance(Long confirmerId) {
        this.attendanceStatus = AttendanceStatus.ATTENDED;
        this.attendanceConfirmedAt = LocalDateTime.now();
        this.attendanceConfirmedBy = confirmerId;
    }

    /**
     * 결석 처리
     */
    public void markAbsent(Long confirmerId) {
        this.attendanceStatus = AttendanceStatus.ABSENT;
        this.attendanceConfirmedAt = LocalDateTime.now();
        this.attendanceConfirmedBy = confirmerId;
    }

    /**
     * 참여 취소
     */
    public void cancel() {
        this.attendanceStatus = AttendanceStatus.CANCELLED;
    }

    /**
     * 마일리지 지급 완료 처리
     */
    public void awardMileage() {
        this.mileageAwarded = true;
    }

    /**
     * 후기 작성
     */
    public void writeFeedback(String feedback, Integer rating) {
        this.feedback = feedback;
        this.rating = rating;
    }

    /**
     * 출석 여부
     */
    public boolean isAttended() {
        return this.attendanceStatus == AttendanceStatus.ATTENDED;
    }

    /**
     * 결석 여부
     */
    public boolean isAbsent() {
        return this.attendanceStatus == AttendanceStatus.ABSENT;
    }

    /**
     * 등록 상태 여부
     */
    public boolean isRegistered() {
        return this.attendanceStatus == AttendanceStatus.REGISTERED;
    }

    /**
     * 마일리지 지급 필요 여부
     */
    public boolean needsMileageAward() {
        return this.isAttended() && !this.mileageAwarded;
    }

    /**
     * 평가 유효성 검증
     */
    public boolean isValidRating() {
        return this.rating != null && this.rating >= 1 && this.rating <= 5;
    }
}
