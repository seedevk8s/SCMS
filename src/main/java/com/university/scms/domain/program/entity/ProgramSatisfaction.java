package com.university.scms.domain.program.entity;

import com.university.scms.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 프로그램 만족도 엔티티
 * 프로그램 참여 후 학생들의 만족도 조사 결과를 저장
 * 
 * MSA 전환 대비:
 * - userId: User ID만 저장 (다른 도메인, 외래키 없음)
 * - program: JPA 관계 (같은 도메인, 외래키 제약조건 제거)
 */
@Entity
@Table(name = "program_satisfactions", 
    indexes = {
        @Index(name = "idx_program_id", columnList = "program_id"),
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_program_user", columnList = "program_id, user_id", unique = true),
        @Index(name = "idx_overall_rating", columnList = "overall_rating")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramSatisfaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== 같은 도메인 관계 (Program Domain) ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false, 
        foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Program program;

    // ========== 다른 도메인 참조 (Auth Domain) ==========
    // User Entity를 직접 참조하지 않고 ID만 저장 (MSA 전환 대비)
    @Column(name = "user_id", nullable = false)
    private Long userId;  // 학생 ID

    // 만족도 평가 항목들 (1-5점 척도)
    @Column(name = "overall_rating", nullable = false)
    @Builder.Default
    private Integer overallRating = 0;  // 전반적 만족도

    @Column(name = "content_rating")
    @Builder.Default
    private Integer contentRating = 0;  // 내용 만족도

    @Column(name = "instructor_rating")
    @Builder.Default
    private Integer instructorRating = 0;  // 강사 만족도

    @Column(name = "facility_rating")
    @Builder.Default
    private Integer facilityRating = 0;  // 시설 만족도

    @Column(name = "usefulness_rating")
    @Builder.Default
    private Integer usefulnessRating = 0;  // 유용성 평가

    @Column(columnDefinition = "TEXT")
    private String strengths;  // 장점 (주관식)

    @Column(columnDefinition = "TEXT")
    private String improvements;  // 개선사항 (주관식)

    @Column(name = "would_recommend")
    @Builder.Default
    private Boolean wouldRecommend = false;  // 추천 의향

    // ========== 비즈니스 메서드 ==========

    /**
     * 만족도 생성 팩토리 메서드
     */
    public static ProgramSatisfaction create(
            Program program, 
            Long userId,
            Integer overallRating,
            Integer contentRating,
            Integer instructorRating,
            Integer facilityRating,
            Integer usefulnessRating,
            String strengths,
            String improvements,
            Boolean wouldRecommend) {
        
        return ProgramSatisfaction.builder()
                .program(program)
                .userId(userId)
                .overallRating(validateRating(overallRating))
                .contentRating(validateRating(contentRating))
                .instructorRating(validateRating(instructorRating))
                .facilityRating(validateRating(facilityRating))
                .usefulnessRating(validateRating(usefulnessRating))
                .strengths(strengths)
                .improvements(improvements)
                .wouldRecommend(wouldRecommend != null ? wouldRecommend : false)
                .build();
    }

    /**
     * 만족도 정보 업데이트
     */
    public void update(
            Integer overallRating,
            Integer contentRating,
            Integer instructorRating,
            Integer facilityRating,
            Integer usefulnessRating,
            String strengths,
            String improvements,
            Boolean wouldRecommend) {
        
        if (overallRating != null) {
            this.overallRating = validateRating(overallRating);
        }
        if (contentRating != null) {
            this.contentRating = validateRating(contentRating);
        }
        if (instructorRating != null) {
            this.instructorRating = validateRating(instructorRating);
        }
        if (facilityRating != null) {
            this.facilityRating = validateRating(facilityRating);
        }
        if (usefulnessRating != null) {
            this.usefulnessRating = validateRating(usefulnessRating);
        }
        if (strengths != null) {
            this.strengths = strengths;
        }
        if (improvements != null) {
            this.improvements = improvements;
        }
        if (wouldRecommend != null) {
            this.wouldRecommend = wouldRecommend;
        }
    }

    /**
     * 평균 만족도 계산
     */
    public double calculateAverageRating() {
        int sum = overallRating + contentRating + instructorRating + facilityRating + usefulnessRating;
        return sum / 5.0;
    }

    /**
     * 높은 만족도 여부 (4점 이상)
     */
    public boolean isHighSatisfaction() {
        return overallRating >= 4;
    }

    /**
     * 낮은 만족도 여부 (2점 이하)
     */
    public boolean isLowSatisfaction() {
        return overallRating <= 2;
    }

    /**
     * 평점 유효성 검증 (1-5점)
     */
    private static Integer validateRating(Integer rating) {
        if (rating == null) {
            return 0;
        }
        if (rating < 1) {
            return 1;
        }
        if (rating > 5) {
            return 5;
        }
        return rating;
    }
}
