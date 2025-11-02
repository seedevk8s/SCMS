package com.university.scms.domain.entity;

import com.university.scms.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 진로 목표 엔티티
 */
@Entity
@Table(name = "career_goals")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CareerGoal extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "career_plan_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private CareerPlan careerPlan;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @Column(length = 20)
    @Builder.Default
    private String status = "NOT_STARTED";

    @Column(name = "goal_order")
    private Integer goalOrder;

    @OneToMany(mappedBy = "careerGoal", cascade = CascadeType.ALL)
    @Builder.Default
    private List<CareerMilestone> milestones = new ArrayList<>();

    public boolean isCompleted() {
        return "COMPLETED".equals(this.status);
    }
}
