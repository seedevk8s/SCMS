package com.university.scms.domain.competency.repository;

import com.university.scms.domain.competency.entity.CompetencySurvey;
import com.university.scms.domain.competency.entity.SurveyQuestion;
import com.university.scms.domain.competency.entity.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * SurveyQuestion 리포지토리
 * 설문 문항 정보에 대한 데이터 액세스를 담당합니다.
 * 
 * MSA 전환 대비:
 * - Competency Domain 내부 리포지토리
 * - CompetencySurvey와 JPA 관계 유지
 */
@Repository
public interface SurveyQuestionRepository extends JpaRepository<SurveyQuestion, Long> {

    // ========== 설문별 조회 메서드 ==========
    
    /**
     * 특정 설문의 모든 문항 조회 (순서대로)
     */
    List<SurveyQuestion> findBySurveyOrderByQuestionOrderAsc(CompetencySurvey survey);
    
    /**
     * 설문 ID로 문항 목록 조회 (순서대로)
     */
    @Query("SELECT q FROM SurveyQuestion q WHERE q.survey.id = :surveyId " +
           "ORDER BY q.questionOrder ASC")
    List<SurveyQuestion> findBySurveyIdOrderByQuestionOrder(@Param("surveyId") Long surveyId);
    
    /**
     * 특정 설문의 필수 문항 조회
     */
    @Query("SELECT q FROM SurveyQuestion q WHERE q.survey.id = :surveyId " +
           "AND q.isRequired = true ORDER BY q.questionOrder ASC")
    List<SurveyQuestion> findRequiredQuestionsBySurveyId(@Param("surveyId") Long surveyId);
    
    /**
     * 특정 설문의 선택 문항 조회
     */
    @Query("SELECT q FROM SurveyQuestion q WHERE q.survey.id = :surveyId " +
           "AND q.isRequired = false ORDER BY q.questionOrder ASC")
    List<SurveyQuestion> findOptionalQuestionsBySurveyId(@Param("surveyId") Long surveyId);

    // ========== 문항 유형별 조회 ==========
    
    /**
     * 문항 유형별 조회
     */
    List<SurveyQuestion> findByQuestionType(QuestionType questionType);
    
    /**
     * 특정 설문의 특정 유형 문항 조회
     */
    @Query("SELECT q FROM SurveyQuestion q WHERE q.survey.id = :surveyId " +
           "AND q.questionType = :questionType ORDER BY q.questionOrder ASC")
    List<SurveyQuestion> findBySurveyIdAndQuestionType(
        @Param("surveyId") Long surveyId,
        @Param("questionType") QuestionType questionType);
    
    /**
     * 객관식 문항 조회 (단일선택 + 다중선택)
     */
    @Query("SELECT q FROM SurveyQuestion q WHERE q.survey.id = :surveyId " +
           "AND (q.questionType = 'SINGLE_CHOICE' OR q.questionType = 'MULTIPLE_CHOICE') " +
           "ORDER BY q.questionOrder ASC")
    List<SurveyQuestion> findChoiceQuestionsBySurveyId(@Param("surveyId") Long surveyId);
    
    /**
     * 척도형 문항 조회
     */
    @Query("SELECT q FROM SurveyQuestion q WHERE q.survey.id = :surveyId " +
           "AND q.questionType = 'SCALE' ORDER BY q.questionOrder ASC")
    List<SurveyQuestion> findScaleQuestionsBySurveyId(@Param("surveyId") Long surveyId);

    // ========== 역량 카테고리별 조회 ==========
    
    /**
     * 역량 카테고리별 문항 조회
     */
    List<SurveyQuestion> findByCompetencyCategory(String competencyCategory);
    
    /**
     * 특정 설문의 특정 카테고리 문항 조회
     */
    @Query("SELECT q FROM SurveyQuestion q WHERE q.survey.id = :surveyId " +
           "AND q.competencyCategory = :category ORDER BY q.questionOrder ASC")
    List<SurveyQuestion> findBySurveyIdAndCategory(
        @Param("surveyId") Long surveyId,
        @Param("category") String category);
    
    /**
     * 특정 설문의 카테고리별 문항 개수 조회
     */
    @Query("SELECT q.competencyCategory, COUNT(q) FROM SurveyQuestion q " +
           "WHERE q.survey.id = :surveyId GROUP BY q.competencyCategory")
    List<Object[]> countByCompetencyCategoryForSurvey(@Param("surveyId") Long surveyId);

    // ========== 순서 관련 조회 ==========
    
    /**
     * 특정 설문의 특정 순서 문항 조회
     */
    Optional<SurveyQuestion> findBySurveyAndQuestionOrder(CompetencySurvey survey, Integer questionOrder);
    
    /**
     * 특정 설문의 마지막 문항 순서 조회
     */
    @Query("SELECT MAX(q.questionOrder) FROM SurveyQuestion q WHERE q.survey.id = :surveyId")
    Optional<Integer> findMaxQuestionOrderBySurveyId(@Param("surveyId") Long surveyId);
    
    /**
     * 특정 순서 이후의 문항들 조회
     */
    @Query("SELECT q FROM SurveyQuestion q WHERE q.survey.id = :surveyId " +
           "AND q.questionOrder > :order ORDER BY q.questionOrder ASC")
    List<SurveyQuestion> findQuestionsAfterOrder(
        @Param("surveyId") Long surveyId,
        @Param("order") Integer order);

    // ========== 통계 메서드 ==========
    
    /**
     * 특정 설문의 전체 문항 수
     */
    long countBySurvey(CompetencySurvey survey);
    
    /**
     * 특정 설문의 필수 문항 수
     */
    @Query("SELECT COUNT(q) FROM SurveyQuestion q WHERE q.survey.id = :surveyId " +
           "AND q.isRequired = true")
    long countRequiredQuestionsBySurveyId(@Param("surveyId") Long surveyId);
    
    /**
     * 특정 역량 카테고리의 문항 수
     */
    long countByCompetencyCategory(String competencyCategory);
    
    /**
     * 특정 문항 유형의 개수
     */
    long countByQuestionType(QuestionType questionType);

    // ========== 검색 메서드 ==========
    
    /**
     * 문항 텍스트로 검색 (부분 일치)
     */
    @Query("SELECT q FROM SurveyQuestion q WHERE q.questionText LIKE %:keyword% " +
           "ORDER BY q.survey.id, q.questionOrder")
    List<SurveyQuestion> searchByQuestionText(@Param("keyword") String keyword);
    
    /**
     * 특정 설문 내에서 문항 텍스트 검색
     */
    @Query("SELECT q FROM SurveyQuestion q WHERE q.survey.id = :surveyId " +
           "AND q.questionText LIKE %:keyword% ORDER BY q.questionOrder ASC")
    List<SurveyQuestion> searchInSurvey(
        @Param("surveyId") Long surveyId,
        @Param("keyword") String keyword);

    // ========== 복합 조회 메서드 ==========
    
    /**
     * 특정 설문의 특정 카테고리 및 유형 문항 조회
     */
    @Query("SELECT q FROM SurveyQuestion q WHERE q.survey.id = :surveyId " +
           "AND q.competencyCategory = :category AND q.questionType = :questionType " +
           "ORDER BY q.questionOrder ASC")
    List<SurveyQuestion> findBySurveyIdAndCategoryAndType(
        @Param("surveyId") Long surveyId,
        @Param("category") String category,
        @Param("questionType") QuestionType questionType);
    
    /**
     * 필수 여부와 문항 유형으로 조회
     */
    List<SurveyQuestion> findByIsRequiredAndQuestionType(Boolean isRequired, QuestionType questionType);
}
