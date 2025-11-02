package com.university.scms.domain.counseling.entity;

import com.university.scms.domain.common.BaseEntity;
import com.university.scms.domain.entity.CounselingSession;
import jakarta.persistence.*;
import lombok.*;

/**
 * 상담 만족도 엔티티
 * 상담 세션 후 학생들의 만족도 조사 결과를 저장
 * 
 * MSA 전환 대비:
 * - userId: User ID만 저장 (다른 도메인, 외래키 없음)
 * - counselingSession: JPA 관계 (같은 도메인, 외래키 제약조건 제거)
 */
@Entity
@Table(name = "counseling_satisfactions", 
    indexes = {
        @Index(name = "idx_session_id", columnList = "session_id"),
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_session_user", columnList = "session_id, user_id", unique = true),
        @Index(name = "idx_overall_rating", columnList = "overall_rating")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CounselingSatisfaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== 같은 도메인 관계 (Counseling Domain) ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false, 
        foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private CounselingSession counselingSession;

    // ========== 다른 도메인 참조 (Auth Domain) ==========
    // User Entity를 직접 참조하지 않고 ID만 저장 (MSA 전환 대비)
    @Column(name = "user_id", nullable = false)
    private Long userId;  // 학생 ID

    // 만족도 평가 항목들 (1-5점 척도)
    @Column(name = "overall_rating", nullable = false)
    @Builder.Default
    private Integer overallRating = 0;  // 전반적 만족도

    @Column(name = "counselor_rating")
    @Builder.Default
    private Integer counselorRating = 0;  // 상담사 만족도

    @Column(name = "environment_rating")
    @Builder.Default
    private Integer environmentRating = 0;  // 환경 만족도

    @Column(name = "problem_solving_rating")
    @Builder.Default
    private Integer problemSolvingRating = 0;  // 문제해결도

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
    public static CounselingSatisfaction create(
            CounselingSession counselingSession, 
            Long userId,
            Integer overallRating,
            Integer counselorRating,
            Integer environmentRating,
            Integer problemSolvingRating,
            Integer usefulnessRating,
            String strengths,
            String improvements,
            Boolean wouldRecommend) {
        
        return CounselingSatisfaction.builder()
                .counselingSession(counselingSession)
                .userId(userId)
                .overallRating(validateRating(overallRating))
                .counselorRating(validateRating(counselorRating))
                .environmentRating(validateRating(environmentRating))
                .problemSolvingRating(validateRating(problemSolvingRating))
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
            Integer counselorRating,
            Integer environmentRating,
            Integer problemSolvingRating,
            Integer usefulnessRating,
            String strengths,
            String improvements,
            Boolean wouldRecommend) {
        
        if (overallRating != null) {
            this.overallRating = validateRating(overallRating);
        }
        if (counselorRating != null) {
            this.counselorRating = validateRating(counselorRating);
        }
        if (environmentRating != null) {
            this.environmentRating = validateRating(environmentRating);
        }
        if (problemSolvingRating != null) {
            this.problemSolvingRating = validateRating(problemSolvingRating);
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
        int sum = overallRating + counselorRating + environmentRating + problemSolvingRating + usefulnessRating;
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
