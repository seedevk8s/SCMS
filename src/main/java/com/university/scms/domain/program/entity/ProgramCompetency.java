package com.university.scms.domain.program.entity;

import com.university.scms.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 프로그램-역량 매핑 엔티티
 * 하나의 프로그램은 여러 역량과 연결될 수 있음
 * 
 * MSA 전환 대비:
 * - competencyId: Competency ID만 저장 (다른 도메인, 외래키 없음)
 * - program: JPA 관계 (같은 도메인, 외래키 제약조건 제거)
 */
@Entity
@Table(name = "program_competencies", 
    indexes = {
        @Index(name = "idx_program_id", columnList = "program_id"),
        @Index(name = "idx_competency_id", columnList = "competency_id"),
        @Index(name = "idx_program_competency", columnList = "program_id, competency_id", unique = true)
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramCompetency extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== 같은 도메인 관계 (Program Domain) ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false, 
        foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Program program;

    // ========== 다른 도메인 참조 (Competency Domain) ==========
    // Competency Entity를 직접 참조하지 않고 ID만 저장 (MSA 전환 대비)
    @Column(name = "competency_id", nullable = false)
    private Long competencyId;

    @Column(name = "weight")
    @Builder.Default
    private Integer weight = 1;  // 가중치 (기본값: 1)

    @Column(columnDefinition = "TEXT")
    private String description;  // 역량 향상 설명

    // ========== 비즈니스 메서드 ==========

    /**
     * 프로그램-역량 매핑 생성 팩토리 메서드
     */
    public static ProgramCompetency create(Program program, Long competencyId, Integer weight, String description) {
        return ProgramCompetency.builder()
                .program(program)
                .competencyId(competencyId)
                .weight(weight != null ? weight : 1)
                .description(description)
                .build();
    }

    /**
     * 가중치 업데이트
     */
    public void updateWeight(Integer weight) {
        if (weight != null && weight > 0) {
            this.weight = weight;
        }
    }

    /**
     * 설명 업데이트
     */
    public void updateDescription(String description) {
        this.description = description;
    }
}
