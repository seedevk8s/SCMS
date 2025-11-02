package com.university.scms.domain.competency.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "survey_responses",
       indexes = {
           @Index(name = "idx_survey_user", columnList = "survey_id, user_id"),
           @Index(name = "idx_question", columnList = "question_id"),
           @Index(name = "idx_user", columnList = "user_id")
       })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SurveyResponse {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false,
                foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private SurveyQuestion question;

    @Column(name = "response_value", columnDefinition = "TEXT")
    private String responseValue;  // 응답 값

    @Column(name = "response_score")
    private Integer responseScore;  // 점수화된 응답

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    // === 생성 메서드 ===
    
    public static SurveyResponse create(
            CompetencySurvey survey,
            Long userId,
            SurveyQuestion question,
            String responseValue,
            Integer responseScore) {
        
        SurveyResponse response = new SurveyResponse();
        response.survey = survey;
        response.userId = userId;
        response.question = question;
        response.responseValue = responseValue;
        response.responseScore = responseScore;
        response.submittedAt = LocalDateTime.now();
        return response;
    }

    // === 조회 메서드 ===
    
    /**
     * 응답이 점수화 가능한지 확인
     */
    public boolean hasScore() {
        return this.responseScore != null;
    }
}
