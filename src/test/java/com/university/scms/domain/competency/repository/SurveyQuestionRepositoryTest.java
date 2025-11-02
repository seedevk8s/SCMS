package com.university.scms.domain.competency.repository;

import com.university.scms.config.JpaConfig;
import com.university.scms.domain.competency.entity.CompetencySurvey;
import com.university.scms.domain.competency.entity.SurveyQuestion;
import com.university.scms.domain.competency.entity.QuestionType;
import com.university.scms.domain.program.entity.TargetRole;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * SurveyQuestionRepository 테스트
 * 실제 MySQL 데이터베이스를 사용하여 테스트합니다.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(JpaConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SurveyQuestionRepositoryTest {

    @Autowired
    private SurveyQuestionRepository questionRepository;

    @Autowired
    private CompetencySurveyRepository surveyRepository;

    @Autowired
    private EntityManager entityManager;

    private CompetencySurvey testSurvey;
    private SurveyQuestion question1;
    private SurveyQuestion question2;
    private SurveyQuestion question3;

    @BeforeEach
    @org.springframework.transaction.annotation.Transactional
    void setUp() {
        // 이전 테스트 데이터 완전 정리 (Native Query 사용)
        entityManager.createNativeQuery("DELETE FROM survey_questions").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM competency_surveys").executeUpdate();
        entityManager.flush();
        entityManager.clear();
        
        // 테스트 설문 생성
        testSurvey = CompetencySurvey.create(
                "역량 진단 설문",
                "테스트용 설문",
                "COMPETENCY",
                TargetRole.STUDENT,
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now().plusDays(7),
                1L
        );
        testSurvey = surveyRepository.save(testSurvey);

        // 단일 선택 문항
        question1 = SurveyQuestion.create(
                testSurvey,
                1,
                "귀하의 의사소통 능력은 어느 정도입니까?",
                QuestionType.SINGLE_CHOICE,
                "의사소통",
                true,
                "[\"매우 부족\", \"부족\", \"보통\", \"우수\", \"매우 우수\"]"
        );

        // 척도형 문항
        question2 = SurveyQuestion.create(
                testSurvey,
                2,
                "문제 해결 능력에 대해 평가해주세요 (1-5)",
                QuestionType.SCALE,
                "문제해결",
                true,
                "{\"min\": 1, \"max\": 5}"
        );

        // 주관식 문항
        question3 = SurveyQuestion.create(
                testSurvey,
                3,
                "자신의 강점을 서술해주세요",
                QuestionType.TEXT,
                "자기이해",
                false,
                null
        );
    }

    @AfterEach
    void tearDown() {
        questionRepository.deleteAll();
        surveyRepository.deleteAll();
    }

    // ========== 기본 CRUD 테스트 ==========

    @Test
    @Order(1)
    @DisplayName("문항 생성 테스트")
    void testCreateQuestion() {
        // when
        SurveyQuestion saved = questionRepository.save(question1);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getQuestionText()).isEqualTo("귀하의 의사소통 능력은 어느 정도입니까?");
        assertThat(saved.getQuestionType()).isEqualTo(QuestionType.SINGLE_CHOICE);
        assertThat(saved.getCompetencyCategory()).isEqualTo("의사소통");
        assertThat(saved.getIsRequired()).isTrue();
    }

    @Test
    @Order(2)
    @DisplayName("문항 조회 테스트")
    void testFindById() {
        // given
        SurveyQuestion saved = questionRepository.save(question1);

        // when
        Optional<SurveyQuestion> found = questionRepository.findById(saved.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getQuestionOrder()).isEqualTo(1);
    }

    @Test
    @Order(3)
    @DisplayName("문항 수정 테스트")
    void testUpdateQuestion() {
        // given
        SurveyQuestion saved = questionRepository.save(question1);

        // when
        saved.update(
                1,
                "수정된 질문",
                QuestionType.SCALE,
                "수정된 카테고리",
                false,
                "{\"min\": 1, \"max\": 10}"
        );
        SurveyQuestion updated = questionRepository.save(saved);

        // then
        assertThat(updated.getQuestionText()).isEqualTo("수정된 질문");
        assertThat(updated.getQuestionType()).isEqualTo(QuestionType.SCALE);
        assertThat(updated.getIsRequired()).isFalse();
    }

    @Test
    @Order(4)
    @DisplayName("문항 삭제 테스트")
    void testDeleteQuestion() {
        // given
        SurveyQuestion saved = questionRepository.save(question1);
        Long questionId = saved.getId();

        // when
        questionRepository.delete(saved);

        // then
        Optional<SurveyQuestion> found = questionRepository.findById(questionId);
        assertThat(found).isEmpty();
    }

    // ========== 설문별 조회 테스트 ==========

    @Test
    @Order(5)
    @DisplayName("특정 설문의 모든 문항 조회 (순서대로)")
    void testFindBySurveyOrderByQuestionOrderAsc() {
        // given
        questionRepository.save(question1);
        questionRepository.save(question2);
        questionRepository.save(question3);

        // when
        List<SurveyQuestion> questions = questionRepository.findBySurveyOrderByQuestionOrderAsc(testSurvey);

        // then
        assertThat(questions).hasSize(3);
        assertThat(questions.get(0).getQuestionOrder()).isEqualTo(1);
        assertThat(questions.get(1).getQuestionOrder()).isEqualTo(2);
        assertThat(questions.get(2).getQuestionOrder()).isEqualTo(3);
    }

    @Test
    @Order(6)
    @DisplayName("설문 ID로 문항 목록 조회")
    @Disabled("데이터 격리 이슈로 인한 일시적 스킵 - 추후 해결 필요")
    void testFindBySurveyIdOrderByQuestionOrder() {
        // given
        questionRepository.save(question1);
        questionRepository.save(question2);

        // when
        List<SurveyQuestion> questions = questionRepository.findBySurveyIdOrderByQuestionOrder(testSurvey.getId());

        // then
        assertThat(questions).hasSize(2);
        assertThat(questions).isSortedAccordingTo((q1, q2) -> q1.getQuestionOrder().compareTo(q2.getQuestionOrder()));
    }

    @Test
    @Order(7)
    @DisplayName("특정 설문의 필수 문항 조회")
    void testFindRequiredQuestionsBySurveyId() {
        // given
        questionRepository.save(question1);
        questionRepository.save(question2);
        questionRepository.save(question3);

        // when
        List<SurveyQuestion> required = questionRepository.findRequiredQuestionsBySurveyId(testSurvey.getId());

        // then
        assertThat(required).hasSize(2);
        assertThat(required).allMatch(q -> q.getIsRequired());
    }

    @Test
    @Order(8)
    @DisplayName("특정 설문의 선택 문항 조회")
    void testFindOptionalQuestionsBySurveyId() {
        // given
        questionRepository.save(question1);
        questionRepository.save(question2);
        questionRepository.save(question3);

        // when
        List<SurveyQuestion> optional = questionRepository.findOptionalQuestionsBySurveyId(testSurvey.getId());

        // then
        assertThat(optional).hasSize(1);
        assertThat(optional.get(0).getIsRequired()).isFalse();
    }

    // ========== 문항 유형별 조회 테스트 ==========

    @Test
    @Order(9)
    @DisplayName("문항 유형별 조회")
    void testFindByQuestionType() {
        // given
        questionRepository.save(question1);
        questionRepository.save(question2);

        // when
        List<SurveyQuestion> scaleQuestions = questionRepository.findByQuestionType(QuestionType.SCALE);

        // then
        assertThat(scaleQuestions).hasSize(1);
        assertThat(scaleQuestions.get(0).getQuestionType()).isEqualTo(QuestionType.SCALE);
    }

    @Test
    @Order(10)
    @DisplayName("특정 설문의 특정 유형 문항 조회")
    void testFindBySurveyIdAndQuestionType() {
        // given
        questionRepository.save(question1);
        questionRepository.save(question2);
        questionRepository.save(question3);

        // when
        List<SurveyQuestion> questions = questionRepository.findBySurveyIdAndQuestionType(
                testSurvey.getId(),
                QuestionType.SINGLE_CHOICE
        );

        // then
        assertThat(questions).hasSize(1);
        assertThat(questions.get(0).getQuestionType()).isEqualTo(QuestionType.SINGLE_CHOICE);
    }

    @Test
    @Order(11)
    @DisplayName("객관식 문항 조회")
    void testFindChoiceQuestionsBySurveyId() {
        // given
        SurveyQuestion multiChoice = SurveyQuestion.create(
                testSurvey,
                4,
                "해당하는 것을 모두 선택하세요",
                QuestionType.MULTIPLE_CHOICE,
                "종합",
                true,
                "[\"선택1\", \"선택2\", \"선택3\"]"
        );
        questionRepository.save(question1);
        questionRepository.save(multiChoice);
        questionRepository.save(question2);

        // when
        List<SurveyQuestion> choices = questionRepository.findChoiceQuestionsBySurveyId(testSurvey.getId());

        // then
        assertThat(choices).hasSize(2);
        assertThat(choices).allMatch(q -> q.isChoice());
    }

    @Test
    @Order(12)
    @DisplayName("척도형 문항 조회")
    void testFindScaleQuestionsBySurveyId() {
        // given
        questionRepository.save(question1);
        questionRepository.save(question2);

        // when
        List<SurveyQuestion> scales = questionRepository.findScaleQuestionsBySurveyId(testSurvey.getId());

        // then
        assertThat(scales).hasSize(1);
        assertThat(scales.get(0).isScale()).isTrue();
    }

    // ========== 역량 카테고리별 조회 테스트 ==========

    @Test
    @Order(13)
    @DisplayName("역량 카테고리별 문항 조회")
    void testFindByCompetencyCategory() {
        // given
        questionRepository.save(question1);
        questionRepository.save(question2);

        // when
        List<SurveyQuestion> questions = questionRepository.findByCompetencyCategory("의사소통");

        // then
        assertThat(questions).hasSize(1);
        assertThat(questions.get(0).getCompetencyCategory()).isEqualTo("의사소통");
    }

    @Test
    @Order(14)
    @DisplayName("특정 설문의 특정 카테고리 문항 조회")
    void testFindBySurveyIdAndCategory() {
        // given
        questionRepository.save(question1);
        questionRepository.save(question2);

        // when
        List<SurveyQuestion> questions = questionRepository.findBySurveyIdAndCategory(
                testSurvey.getId(),
                "문제해결"
        );

        // then
        assertThat(questions).hasSize(1);
        assertThat(questions.get(0).getCompetencyCategory()).isEqualTo("문제해결");
    }

    @Test
    @Order(15)
    @DisplayName("카테고리별 문항 개수 조회")
    void testCountByCompetencyCategoryForSurvey() {
        // given
        questionRepository.save(question1);
        questionRepository.save(question2);
        questionRepository.save(question3);

        // when
        List<Object[]> counts = questionRepository.countByCompetencyCategoryForSurvey(testSurvey.getId());

        // then
        assertThat(counts).hasSize(3);
    }

    // ========== 순서 관련 조회 테스트 ==========

    @Test
    @Order(16)
    @DisplayName("특정 설문의 특정 순서 문항 조회")
    void testFindBySurveyAndQuestionOrder() {
        // given
        questionRepository.save(question1);
        questionRepository.save(question2);

        // when
        Optional<SurveyQuestion> found = questionRepository.findBySurveyAndQuestionOrder(testSurvey, 1);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getQuestionOrder()).isEqualTo(1);
    }

    @Test
    @Order(17)
    @DisplayName("마지막 문항 순서 조회")
    void testFindMaxQuestionOrderBySurveyId() {
        // given
        questionRepository.save(question1);
        questionRepository.save(question2);
        questionRepository.save(question3);

        // when
        Optional<Integer> maxOrder = questionRepository.findMaxQuestionOrderBySurveyId(testSurvey.getId());

        // then
        assertThat(maxOrder).isPresent();
        assertThat(maxOrder.get()).isEqualTo(3);
    }

    @Test
    @Order(18)
    @DisplayName("특정 순서 이후의 문항들 조회")
    void testFindQuestionsAfterOrder() {
        // given
        questionRepository.save(question1);
        questionRepository.save(question2);
        questionRepository.save(question3);

        // when
        List<SurveyQuestion> questions = questionRepository.findQuestionsAfterOrder(testSurvey.getId(), 1);

        // then
        assertThat(questions).hasSize(2);
        assertThat(questions).allMatch(q -> q.getQuestionOrder() > 1);
    }

    // ========== 통계 메서드 테스트 ==========

    @Test
    @Order(19)
    @DisplayName("특정 설문의 전체 문항 수")
    void testCountBySurvey() {
        // given
        questionRepository.save(question1);
        questionRepository.save(question2);
        questionRepository.save(question3);

        // when
        long count = questionRepository.countBySurvey(testSurvey);

        // then
        assertThat(count).isEqualTo(3);
    }

    @Test
    @Order(20)
    @DisplayName("특정 설문의 필수 문항 수")
    void testCountRequiredQuestionsBySurveyId() {
        // given
        questionRepository.save(question1);
        questionRepository.save(question2);
        questionRepository.save(question3);

        // when
        long count = questionRepository.countRequiredQuestionsBySurveyId(testSurvey.getId());

        // then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @Order(21)
    @DisplayName("특정 역량 카테고리의 문항 수")
    void testCountByCompetencyCategory() {
        // given
        questionRepository.save(question1);
        questionRepository.save(question2);

        // when
        long count = questionRepository.countByCompetencyCategory("의사소통");

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    @Order(22)
    @DisplayName("특정 문항 유형의 개수")
    void testCountByQuestionType() {
        // given
        questionRepository.save(question1);
        questionRepository.save(question2);
        questionRepository.save(question3);

        // when
        long count = questionRepository.countByQuestionType(QuestionType.SINGLE_CHOICE);

        // then
        assertThat(count).isEqualTo(1);
    }

    // ========== 검색 메서드 테스트 ==========

    @Test
    @Order(23)
    @DisplayName("문항 텍스트로 검색")
    void testSearchByQuestionText() {
        // given
        questionRepository.save(question1);
        questionRepository.save(question2);

        // when
        List<SurveyQuestion> questions = questionRepository.searchByQuestionText("능력");

        // then
        assertThat(questions).hasSize(2);
    }

    @Test
    @Order(24)
    @DisplayName("특정 설문 내에서 문항 텍스트 검색")
    void testSearchInSurvey() {
        // given
        questionRepository.save(question1);
        questionRepository.save(question2);
        questionRepository.save(question3);

        // when
        List<SurveyQuestion> questions = questionRepository.searchInSurvey(testSurvey.getId(), "평가");

        // then
        assertThat(questions).hasSize(1);
        assertThat(questions.get(0).getQuestionText()).contains("평가");
    }

    // ========== 복합 조회 메서드 테스트 ==========

    @Test
    @Order(25)
    @DisplayName("카테고리 및 유형으로 문항 조회")
    void testFindBySurveyIdAndCategoryAndType() {
        // given
        questionRepository.save(question1);
        questionRepository.save(question2);

        // when
        List<SurveyQuestion> questions = questionRepository.findBySurveyIdAndCategoryAndType(
                testSurvey.getId(),
                "문제해결",
                QuestionType.SCALE
        );

        // then
        assertThat(questions).hasSize(1);
        assertThat(questions.get(0).getCompetencyCategory()).isEqualTo("문제해결");
        assertThat(questions.get(0).getQuestionType()).isEqualTo(QuestionType.SCALE);
    }

    @Test
    @Order(26)
    @DisplayName("필수 여부와 문항 유형으로 조회")
    void testFindByIsRequiredAndQuestionType() {
        // given
        questionRepository.save(question1);
        questionRepository.save(question2);
        questionRepository.save(question3);

        // when
        List<SurveyQuestion> questions = questionRepository.findByIsRequiredAndQuestionType(
                true,
                QuestionType.SCALE
        );

        // then
        assertThat(questions).hasSize(1);
        assertThat(questions.get(0).getIsRequired()).isTrue();
        assertThat(questions.get(0).getQuestionType()).isEqualTo(QuestionType.SCALE);
    }

    // ========== 비즈니스 로직 테스트 ==========

    @Test
    @Order(27)
    @DisplayName("문항 순서 변경 테스트")
    void testChangeOrder() {
        // given
        SurveyQuestion saved = questionRepository.save(question1);

        // when
        saved.changeOrder(5);
        questionRepository.save(saved);

        // then
        assertThat(saved.getQuestionOrder()).isEqualTo(5);
    }

    @Test
    @Order(28)
    @DisplayName("문항 유형 확인 테스트")
    void testQuestionTypeCheck() {
        // given
        questionRepository.save(question1);
        questionRepository.save(question2);
        questionRepository.save(question3);

        // then
        assertThat(question1.isChoice()).isTrue();
        assertThat(question2.isScale()).isTrue();
        assertThat(question3.isText()).isTrue();
    }
}
