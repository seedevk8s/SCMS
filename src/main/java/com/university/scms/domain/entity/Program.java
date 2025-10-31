package com.university.scms.domain.entity;

import com.university.scms.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 비교과 프로그램 엔티티
 * 
 * MSA 전환 대비:
 * - organizerId: User ID만 저장 (다른 도메인, 외래키 없음)
 * - applications, participants: JPA 관계 (같은 도메인, 외래키 제약조건 제거)
 */
@Entity
@Table(name = "programs", indexes = {
    @Index(name = "idx_organizer", columnList = "organizer_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_dates", columnList = "start_date, end_date"),
    @Index(name = "idx_category", columnList = "category")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Program extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String category;  // 프로그램 카테고리

    // ========== 다른 도메인 참조 (Auth Domain) ==========
    // User Entity를 직접 참조하지 않고 ID만 저장 (MSA 전환 대비)
    @Column(name = "organizer_id", nullable = false)
    private Long organizerId;  // 담당 교직원 ID

    @Column(length = 200)
    private String location;

    @Column
    private Integer capacity;  // 정원

    @Column(name = "current_participants")
    @Builder.Default
    private Integer currentParticipants = 0;  // 현재 참여자 수

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "application_start")
    private LocalDateTime applicationStart;  // 신청 시작일

    @Column(name = "application_end")
    private LocalDateTime applicationEnd;  // 신청 마감일

    @Column(name = "mileage_points")
    @Builder.Default
    private Integer mileagePoints = 0;  // 참여 시 지급 마일리지

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ProgramStatus status = ProgramStatus.DRAFT;

    // ========== 같은 도메인 관계 (Program Domain) ==========
    // JPA 관계 매핑 사용 (외래키 제약조건 제거)
    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProgramApplication> applications = new ArrayList<>();

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProgramParticipant> participants = new ArrayList<>();

    // ========== 비즈니스 메서드 ==========

    /**
     * 신청 가능 여부 확인
     */
    public boolean canApply() {
        LocalDateTime now = LocalDateTime.now();
        return this.status == ProgramStatus.OPEN
                && this.applicationStart != null
                && this.applicationEnd != null
                && now.isAfter(this.applicationStart)
                && now.isBefore(this.applicationEnd)
                && this.currentParticipants < this.capacity;
    }

    /**
     * 정원 초과 여부
     */
    public boolean isFull() {
        return this.capacity != null && this.currentParticipants >= this.capacity;
    }

    /**
     * 참여자 수 증가
     */
    public void incrementParticipants() {
        this.currentParticipants++;
    }

    /**
     * 참여자 수 감소
     */
    public void decrementParticipants() {
        if (this.currentParticipants > 0) {
            this.currentParticipants--;
        }
    }

    /**
     * 프로그램 상태 변경
     */
    public void changeStatus(ProgramStatus newStatus) {
        this.status = newStatus;
    }

    /**
     * 프로그램 진행 중 여부
     */
    public boolean isInProgress() {
        LocalDateTime now = LocalDateTime.now();
        return this.status == ProgramStatus.OPEN
                && this.startDate != null
                && this.endDate != null
                && now.isAfter(this.startDate)
                && now.isBefore(this.endDate);
    }

    /**
     * 프로그램 완료 여부
     */
    public boolean isCompleted() {
        return this.status == ProgramStatus.COMPLETED
                || (this.endDate != null && LocalDateTime.now().isAfter(this.endDate));
    }
}
