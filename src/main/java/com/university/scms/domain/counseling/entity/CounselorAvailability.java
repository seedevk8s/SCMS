package com.university.scms.domain.counseling.entity;

import com.university.scms.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * 상담사 가용 시간 엔티티
 * 상담사의 요일별 상담 가능 시간을 관리합니다.
 * 
 * MSA 전환 대비:
 * - counselorId: User ID만 저장 (다른 도메인, 외래키 없음)
 */
@Entity
@Table(name = "counselor_availability",
       indexes = {
           @Index(name = "idx_counselor", columnList = "counselor_id"),
           @Index(name = "idx_day_of_week", columnList = "day_of_week"),
           @Index(name = "idx_available", columnList = "is_available")
       },
       uniqueConstraints = {
           @UniqueConstraint(
               name = "uk_counselor_day_time",
               columnNames = {"counselor_id", "day_of_week", "start_time"}
           )
       })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CounselorAvailability extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== 다른 도메인 참조 (Auth Domain) ==========
    @Column(name = "counselor_id", nullable = false)
    private Long counselorId;  // 상담사 ID

    // ========== 가용 시간 정보 ==========
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 10)
    private DayOfWeek dayOfWeek;  // 요일 (MONDAY, TUESDAY, ...)

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;  // 시작 시간

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;  // 종료 시간

    @Column(name = "is_available", nullable = false)
    @Builder.Default
    private Boolean isAvailable = true;  // 가용 여부

    // ========== 비즈니스 메서드 ==========

    /**
     * 가용 상태로 설정
     */
    public void setAvailable() {
        this.isAvailable = true;
    }

    /**
     * 불가 상태로 설정
     */
    public void setUnavailable() {
        this.isAvailable = false;
    }

    /**
     * 가용 상태 토글
     */
    public void toggleAvailability() {
        this.isAvailable = !this.isAvailable;
    }

    /**
     * 특정 시간이 이 가용 시간 범위에 포함되는지 확인
     * 
     * @param time 확인할 시간
     * @return 가용 시간 범위에 포함되면 true
     */
    public boolean isAvailableAt(LocalTime time) {
        if (!this.isAvailable) {
            return false;
        }
        return !time.isBefore(this.startTime) && !time.isAfter(this.endTime);
    }

    /**
     * 시간 범위가 겹치는지 확인
     * 
     * @param otherStart 다른 시작 시간
     * @param otherEnd 다른 종료 시간
     * @return 겹치면 true
     */
    public boolean overlaps(LocalTime otherStart, LocalTime otherEnd) {
        return !this.endTime.isBefore(otherStart) && !this.startTime.isAfter(otherEnd);
    }

    /**
     * 가용 시간 범위 유효성 검증
     * 
     * @return 유효하면 true (시작 시간이 종료 시간보다 이전)
     */
    public boolean isValidTimeRange() {
        return this.startTime.isBefore(this.endTime);
    }

    /**
     * 가용 시간 정보 수정
     * 
     * @param startTime 새 시작 시간
     * @param endTime 새 종료 시간
     */
    public void updateTime(LocalTime startTime, LocalTime endTime) {
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new IllegalArgumentException("시작 시간은 종료 시간보다 이전이어야 합니다.");
        }
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * 요일 변경
     * 
     * @param dayOfWeek 새 요일
     */
    public void changeDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
}
