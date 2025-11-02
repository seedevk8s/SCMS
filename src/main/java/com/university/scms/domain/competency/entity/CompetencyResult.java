package com.university.scms.domain.competency.entity;

import com.university.scms.domain.common.BaseEntity;
import com.university.scms.domain.competency.entity.CompetencySurvey;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "competency_results",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_survey_user", columnNames = {"survey_id", "user_id"})
       },
       indexes = {
           @Index(name = "idx_user", columnList = "user_id"),
           @Index(name = "idx_level", columnList = "competency_level")
       })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompetencyResult extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 도메인 내부: JPA 관계 (외래키 제약조건 제거)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false,
                foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private CompetencySurvey survey;

    @Column(name = "user_id", nullable = false)
    private Long userId;  // Auth Domain 참조 (외래키 없음)

    @Column(name = "total_score")
    private Integer totalScore;

    @Column(name = "category_scores", columnDefinition = "TEXT")
    private String categoryScores;  // JSON 문자열로 저장

    @Column(name = "competency_level", length = 50)
    private String competencyLevel;  // 초급, 중급, 고급

    @Column(name = "strengths", columnDefinition = "TEXT")
    private String strengths;  // 강점

    @Column(name = "weaknesses", columnDefinition = "TEXT")
    private String weaknesses;  // 약점

    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations;  // 추천 사항

    // === 생성 메서드 ===
    
    public static CompetencyResult create(
            CompetencySurvey survey,
            Long userId,
            Integer totalScore,
            String categoryScores,
            String competencyLevel,
            String strengths,
            String weaknesses,
            String recommendations) {
        
        CompetencyResult result = new CompetencyResult();
        result.survey = survey;
        result.userId = userId;
        result.totalScore = totalScore;
        result.categoryScores = categoryScores;
        result.competencyLevel = competencyLevel;
        result.strengths = strengths;
        result.weaknesses = weaknesses;
        result.recommendations = recommendations;
        return result;
    }

    // === 비즈니스 메서드 ===
    
    /**
     * 결과 업데이트
     */
    public void update(
            Integer totalScore,
            String categoryScores,
            String competencyLevel,
            String strengths,
            String weaknesses,
            String recommendations) {
        
        this.totalScore = totalScore;
        this.categoryScores = categoryScores;
        this.competencyLevel = competencyLevel;
        this.strengths = strengths;
        this.weaknesses = weaknesses;
        this.recommendations = recommendations;
    }

    /**
     * 역량 수준 판정
     */
    public static String determineLevel(int totalScore, int maxScore) {
        double percentage = (double) totalScore / maxScore * 100;
        
        if (percentage >= 80) {
            return "고급";
        } else if (percentage >= 60) {
            return "중급";
        } else {
            return "초급";
        }
    }
}
