package com.university.scms.domain.entity;

import com.university.scms.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * 진로 마일스톤 엔티티
 */
@Entity
@Table(name = "career_milestones")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CareerMilestone extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "career_goal_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private CareerGoal careerGoal;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @Column(name = "completed_date")
    private LocalDate completedDate;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isCompleted = false;

    @Column(name = "milestone_order")
    private Integer milestoneOrder;

    public void complete() {
        this.isCompleted = true;
        this.completedDate = LocalDate.now();
    }
}
