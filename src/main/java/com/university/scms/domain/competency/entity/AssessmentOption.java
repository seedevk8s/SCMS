package com.university.scms.domain.competency.entity;

import com.university.scms.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 역량 진단 선택지 엔티티
 * 객관식 또는 척도형 문항의 개별 선택지를 관리
 * 
 * MSA 전환 대비:
 * - question: JPA 관계 (같은 도메인, 외래키 제약조건 제거)
 */
@Entity
@Table(name = "assessment_options", 
    indexes = {
        @Index(name = "idx_question_id", columnList = "question_id"),
        @Index(name = "idx_question_order", columnList = "question_id, option_order")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== 같은 도메인 관계 (Competency Domain) ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false, 
        foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private SurveyQuestion question;

    @Column(name = "option_order", nullable = false)
    private Integer optionOrder;  // 선택지 순서

    @Column(name = "option_text", nullable = false, length = 500)
    private String optionText;  // 선택지 텍스트

    @Column(name = "option_value")
    @Builder.Default
    private Integer optionValue = 0;  // 선택지 점수/값 (기본값: 0)

    @Column(name = "is_correct")
    @Builder.Default
    private Boolean isCorrect = false;  // 정답 여부 (퀴즈형의 경우)

    // ========== 비즈니스 메서드 ==========

    /**
     * 선택지 생성 팩토리 메서드
     */
    public static AssessmentOption create(
            SurveyQuestion question, 
            Integer optionOrder, 
            String optionText, 
            Integer optionValue,
            Boolean isCorrect) {
        return AssessmentOption.builder()
                .question(question)
                .optionOrder(optionOrder)
                .optionText(optionText)
                .optionValue(optionValue != null ? optionValue : 0)
                .isCorrect(isCorrect != null ? isCorrect : false)
                .build();
    }

    /**
     * 선택지 정보 업데이트
     */
    public void update(Integer optionOrder, String optionText, Integer optionValue, Boolean isCorrect) {
        if (optionOrder != null) {
            this.optionOrder = optionOrder;
        }
        if (optionText != null && !optionText.isBlank()) {
            this.optionText = optionText;
        }
        if (optionValue != null) {
            this.optionValue = optionValue;
        }
        if (isCorrect != null) {
            this.isCorrect = isCorrect;
        }
    }

    /**
     * 순서 변경
     */
    public void changeOrder(Integer newOrder) {
        if (newOrder != null && newOrder > 0) {
            this.optionOrder = newOrder;
        }
    }

    /**
     * 점수 업데이트
     */
    public void updateValue(Integer value) {
        this.optionValue = value;
    }

    /**
     * 정답 설정
     */
    public void markAsCorrect() {
        this.isCorrect = true;
    }

    /**
     * 정답 해제
     */
    public void markAsIncorrect() {
        this.isCorrect = false;
    }
}
