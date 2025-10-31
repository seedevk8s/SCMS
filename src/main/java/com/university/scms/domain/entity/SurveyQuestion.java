package com.university.scms.domain.entity;

import com.university.scms.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "survey_questions",
       indexes = {
           @Index(name = "idx_survey", columnList = "survey_id"),
           @Index(name = "idx_category", columnList = "competency_category"),
           @Index(name = "idx_order", columnList = "survey_id, question_order")
       })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SurveyQuestion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 도메인 내부: JPA 관계 (외래키 제약조건 제거)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false,
                foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private CompetencySurvey survey;

    @Column(name = "question_order")
    private Integer questionOrder;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false, length = 20)
    private QuestionType questionType;

    @Column(name = "competency_category", length = 100)
    private String competencyCategory;  // 의사소통, 문제해결 등

    @Column(name = "is_required", nullable = false)
    private Boolean isRequired = true;

    @Column(name = "options", columnDefinition = "TEXT")
    private String options;  // JSON 문자열로 저장

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<SurveyResponse> responses = new ArrayList<>();

    // === 생성 메서드 ===
    
    public static SurveyQuestion create(
            CompetencySurvey survey,
            Integer questionOrder,
            String questionText,
            QuestionType questionType,
            String competencyCategory,
            Boolean isRequired,
            String options) {
        
        SurveyQuestion question = new SurveyQuestion();
        question.survey = survey;
        question.questionOrder = questionOrder;
        question.questionText = questionText;
        question.questionType = questionType;
        question.competencyCategory = competencyCategory;
        question.isRequired = isRequired != null ? isRequired : true;
        question.options = options;
        
        survey.addQuestion(question);
        
        return question;
    }

    // === 비즈니스 메서드 ===
    
    /**
     * 문항 정보 수정
     */
    public void update(
            Integer questionOrder,
            String questionText,
            QuestionType questionType,
            String competencyCategory,
            Boolean isRequired,
            String options) {
        
        this.questionOrder = questionOrder;
        this.questionText = questionText;
        this.questionType = questionType;
        this.competencyCategory = competencyCategory;
        this.isRequired = isRequired;
        this.options = options;
    }

    /**
     * 순서 변경
     */
    public void changeOrder(Integer newOrder) {
        this.questionOrder = newOrder;
    }

    /**
     * 객관식 문항인지 확인
     */
    public boolean isChoice() {
        return this.questionType == QuestionType.SINGLE_CHOICE || 
               this.questionType == QuestionType.MULTIPLE_CHOICE;
    }

    /**
     * 척도 문항인지 확인
     */
    public boolean isScale() {
        return this.questionType == QuestionType.SCALE;
    }

    /**
     * 주관식 문항인지 확인
     */
    public boolean isText() {
        return this.questionType == QuestionType.TEXT;
    }
}
