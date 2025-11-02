package com.university.scms.domain.career.entity;

import com.university.scms.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 진로 계획 엔티티
 */
@Entity
@Table(name = "career_plans")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CareerPlan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "target_field", length = 100)
    private String targetField;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @Column(length = 20)
    @Builder.Default
    private String status = "IN_PROGRESS";

    @OneToMany(mappedBy = "careerPlan", cascade = CascadeType.ALL)
    @Builder.Default
    private List<CareerGoal> goals = new ArrayList<>();
}
