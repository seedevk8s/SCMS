package com.university.scms.domain.entity;

import com.university.scms.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "competency_surveys",
       indexes = {
           @Index(name = "idx_active", columnList = "is_active"),
           @Index(name = "idx_dates", columnList = "start_date, end_date"),
           @Index(name = "idx_created_by", columnList = "created_by")
       })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompetencySurvey extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "survey_type", length = 50)
    private String surveyType;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_role", length = 20, nullable = false)
    private TargetRole targetRole = TargetRole.ALL;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "created_by")
    private Long createdBy;  // Auth Domain 참조 (외래키 없음)

    // 도메인 내부: JPA 관계 (외래키 제약조건 제거)
    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SurveyQuestion> questions = new ArrayList<>();

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL)
    private List<SurveyResponse> responses = new ArrayList<>();

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL)
    private List<CompetencyResult> results = new ArrayList<>();

    // === 생성 메서드 ===
    
    public static CompetencySurvey create(
            String title,
            String description,
            String surveyType,
            TargetRole targetRole,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Long createdBy) {
        
        CompetencySurvey survey = new CompetencySurvey();
        survey.title = title;
        survey.description = description;
        survey.surveyType = surveyType;
        survey.targetRole = targetRole;
        survey.startDate = startDate;
        survey.endDate = endDate;
        survey.createdBy = createdBy;
        survey.isActive = true;
        return survey;
    }

    // === 비즈니스 메서드 ===
    
    /**
     * 설문 활성화
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * 설문 비활성화
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * 설문 기간 내인지 확인
     */
    public boolean isWithinPeriod() {
        LocalDateTime now = LocalDateTime.now();
        if (startDate != null && now.isBefore(startDate)) {
            return false;
        }
        if (endDate != null && now.isAfter(endDate)) {
            return false;
        }
        return true;
    }

    /**
     * 설문 응답 가능 여부
     */
    public boolean canRespond() {
        return this.isActive && isWithinPeriod();
    }

    /**
     * 설문 문항 추가
     */
    public void addQuestion(SurveyQuestion question) {
        this.questions.add(question);
    }

    /**
     * 설문 정보 수정
     */
    public void update(
            String title,
            String description,
            String surveyType,
            TargetRole targetRole,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        
        this.title = title;
        this.description = description;
        this.surveyType = surveyType;
        this.targetRole = targetRole;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * 특정 역할이 응답 가능한지 확인
     */
    public boolean canRespondByRole(UserRole userRole) {
        if (this.targetRole == TargetRole.ALL) {
            return true;
        }
        if (this.targetRole == TargetRole.STUDENT && userRole == UserRole.STUDENT) {
            return true;
        }
        if (this.targetRole == TargetRole.STAFF && userRole == UserRole.STAFF) {
            return true;
        }
        return false;
    }
}
