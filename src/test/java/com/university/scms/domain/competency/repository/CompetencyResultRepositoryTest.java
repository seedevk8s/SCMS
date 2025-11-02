package com.university.scms.domain.competency.repository;

import com.university.scms.config.JpaConfig;
import com.university.scms.domain.competency.entity.CompetencyResult;
import com.university.scms.domain.competency.entity.CompetencySurvey;
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
 * CompetencyResultRepository 테스트
 * 실제 MySQL 데이터베이스를 사용하여 테스트합니다.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(JpaConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CompetencyResultRepositoryTest {

    @Autowired
    private CompetencyResultRepository resultRepository;

    @Autowired
    private CompetencySurveyRepository surveyRepository;

    @Autowired
    private EntityManager entityManager;

    private CompetencySurvey testSurvey;
    private CompetencyResult result1;
    private CompetencyResult result2;
    private CompetencyResult result3;

    @BeforeEach
    @org.springframework.transaction.annotation.Transactional
    void setUp() {
        // 이전 테스트 데이터 완전 정리 (Native Query 사용)
        entityManager.createNativeQuery("DELETE FROM competency_results").executeUpdate();
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

        // 우수 결과 (고급)
        result1 = CompetencyResult.create(
                testSurvey,
                1001L,
                85,
                "{\"의사소통\": 90, \"문제해결\": 85, \"협업\": 80}",
                "고급",
                "의사소통 능력이 뛰어남",
                "시간 관리 개선 필요",
                "리더십 개발 프로그램 추천"
        );

        // 중급 결과
        result2 = CompetencyResult.create(
                testSurvey,
                1002L,
                68,
                "{\"의사소통\": 70, \"문제해결\": 65, \"협업\": 70}",
                "중급",
                "협업 능력 양호",
                "문제해결 능력 보완 필요",
                "문제해결 워크샵 참여 권장"
        );

        // 초급 결과
        result3 = CompetencyResult.create(
                testSurvey,
                1003L,
                45,
                "{\"의사소통\": 50, \"문제해결\": 40, \"협업\": 45}",
                "초급",
                "성실성",
                "전반적인 역량 개발 필요",
                "기초 역량 강화 프로그램 추천"
        );
    }

    @AfterEach
    void tearDown() {
        resultRepository.deleteAll();
        surveyRepository.deleteAll();
    }

    // ========== 기본 CRUD 테스트 ==========

    @Test
    @Order(1)
    @DisplayName("결과 생성 테스트")
    void testCreateResult() {
        // when
        CompetencyResult saved = resultRepository.save(result1);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUserId()).isEqualTo(1001L);
        assertThat(saved.getTotalScore()).isEqualTo(85);
        assertThat(saved.getCompetencyLevel()).isEqualTo("고급");
    }

    @Test
    @Order(2)
    @DisplayName("결과 조회 테스트")
    void testFindById() {
        // given
        CompetencyResult saved = resultRepository.save(result1);

        // when
        Optional<CompetencyResult> found = resultRepository.findById(saved.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo(1001L);
    }

    @Test
    @Order(3)
    @DisplayName("결과 수정 테스트")
    void testUpdateResult() {
        // given
        CompetencyResult saved = resultRepository.save(result1);

        // when
        saved.update(
                90,
                "{\"의사소통\": 95, \"문제해결\": 90, \"협업\": 85}",
                "고급",
                "모든 영역 우수",
                "없음",
                "멘토링 프로그램 참여 권장"
        );
        CompetencyResult updated = resultRepository.save(saved);

        // then
        assertThat(updated.getTotalScore()).isEqualTo(90);
        assertThat(updated.getStrengths()).isEqualTo("모든 영역 우수");
    }

    @Test
    @Order(4)
    @DisplayName("결과 삭제 테스트")
    void testDeleteResult() {
        // given
        CompetencyResult saved = resultRepository.save(result1);
        Long resultId = saved.getId();

        // when
        resultRepository.delete(saved);

        // then
        Optional<CompetencyResult> found = resultRepository.findById(resultId);
        assertThat(found).isEmpty();
    }

    // ========== 기본 조회 메서드 테스트 ==========

    @Test
    @Order(5)
    @DisplayName("설문과 사용자로 결과 조회")
    void testFindBySurveyAndUserId() {
        // given
        resultRepository.save(result1);

        // when
        Optional<CompetencyResult> found = resultRepository.findBySurveyAndUserId(testSurvey, 1001L);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getTotalScore()).isEqualTo(85);
    }

    @Test
    @Order(6)
    @DisplayName("설문 ID와 사용자 ID로 결과 조회")
    void testFindBySurveyIdAndUserId() {
        // given
        resultRepository.save(result1);

        // when
        Optional<CompetencyResult> found = resultRepository.findBySurveyIdAndUserId(testSurvey.getId(), 1001L);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getCompetencyLevel()).isEqualTo("고급");
    }

    @Test
    @Order(7)
    @DisplayName("결과 존재 여부 확인")
    void testExistsBySurveyAndUserId() {
        // given
        resultRepository.save(result1);

        // when
        boolean exists = resultRepository.existsBySurveyAndUserId(testSurvey, 1001L);
        boolean notExists = resultRepository.existsBySurveyAndUserId(testSurvey, 9999L);

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    // ========== 사용자별 조회 테스트 ==========

    @Test
    @Order(8)
    @DisplayName("특정 사용자의 모든 결과 조회")
    void testFindByUserIdOrderByCreatedDateDesc() {
        // given
        resultRepository.save(result1);
        
        // 같은 사용자의 다른 설문 결과
        CompetencySurvey survey2 = CompetencySurvey.create(
                "다른 설문",
                "테스트",
                "TEST",
                TargetRole.ALL,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().plusDays(5),
                1L
        );
        survey2 = surveyRepository.save(survey2);
        
        CompetencyResult result4 = CompetencyResult.create(
                survey2,
                1001L,
                75,
                "{}",
                "중급",
                "양호",
                "없음",
                "계속 노력"
        );
        resultRepository.save(result4);

        // when
        List<CompetencyResult> results = resultRepository.findByUserIdOrderByCreatedAtDesc(1001L);

        // then
        assertThat(results).hasSize(2);
    }

    @Test
    @Order(9)
    @DisplayName("특정 사용자의 최신 n개 결과 조회")
    void testFindTopNByUserId() {
        // given
        resultRepository.save(result1);

        // when
        List<CompetencyResult> results = resultRepository.findTopNByUserId(1001L, 1);

        // then
        assertThat(results).hasSize(1);
    }

    @Test
    @Order(10)
    @DisplayName("특정 사용자의 특정 역량 수준 결과 조회")
    void testFindByUserIdAndCompetencyLevel() {
        // given
        resultRepository.save(result1);
        resultRepository.save(result2);

        // when
        List<CompetencyResult> results = resultRepository.findByUserIdAndCompetencyLevel(1001L, "고급");

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getCompetencyLevel()).isEqualTo("고급");
    }

    // ========== 설문별 조회 테스트 ==========

    @Test
    @Order(11)
    @DisplayName("특정 설문의 모든 결과 조회")
    void testFindBySurvey() {
        // given
        resultRepository.save(result1);
        resultRepository.save(result2);
        resultRepository.save(result3);

        // when
        List<CompetencyResult> results = resultRepository.findBySurvey(testSurvey);

        // then
        assertThat(results).hasSize(3);
    }

    @Test
    @Order(12)
    @DisplayName("특정 설문 ID의 모든 결과 조회")
    void testFindBySurveyIdOrderByCreatedDateDesc() {
        // given
        resultRepository.save(result1);
        resultRepository.save(result2);

        // when
        List<CompetencyResult> results = resultRepository.findBySurveyIdOrderByCreatedAtDesc(testSurvey.getId());

        // then
        assertThat(results).hasSize(2);
    }

    @Test
    @Order(13)
    @DisplayName("특정 설문의 특정 역량 수준 결과 조회")
    void testFindBySurveyIdAndLevel() {
        // given
        resultRepository.save(result1);
        resultRepository.save(result2);
        resultRepository.save(result3);

        // when
        List<CompetencyResult> results = resultRepository.findBySurveyIdAndLevel(testSurvey.getId(), "중급");

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getCompetencyLevel()).isEqualTo("중급");
    }

    // ========== 역량 수준별 조회 테스트 ==========

    @Test
    @Order(14)
    @DisplayName("역량 수준별 결과 조회")
    void testFindByCompetencyLevel() {
        // given
        resultRepository.save(result1);
        resultRepository.save(result2);
        resultRepository.save(result3);

        // when
        List<CompetencyResult> results = resultRepository.findByCompetencyLevel("고급");

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTotalScore()).isEqualTo(85);
    }

    @Test
    @Order(15)
    @DisplayName("역량 수준별 개수 조회")
    void testCountByLevelForSurvey() {
        // given
        resultRepository.save(result1);
        resultRepository.save(result2);
        resultRepository.save(result3);

        // when
        List<Object[]> counts = resultRepository.countByLevelForSurvey(testSurvey.getId());

        // then
        assertThat(counts).hasSize(3);
    }

    // ========== 점수별 조회 테스트 ==========

    @Test
    @Order(16)
    @DisplayName("특정 점수 이상인 결과 조회")
    void testFindByTotalScoreGreaterThanEqual() {
        // given
        resultRepository.save(result1);
        resultRepository.save(result2);
        resultRepository.save(result3);

        // when
        List<CompetencyResult> results = resultRepository.findByTotalScoreGreaterThanEqual(60);

        // then
        assertThat(results).hasSize(2);
        assertThat(results).allMatch(r -> r.getTotalScore() >= 60);
    }

    @Test
    @Order(17)
    @DisplayName("점수 범위 결과 조회")
    void testFindBySurveyIdAndScoreRange() {
        // given
        resultRepository.save(result1);
        resultRepository.save(result2);
        resultRepository.save(result3);

        // when
        List<CompetencyResult> results = resultRepository.findBySurveyIdAndScoreRange(
                testSurvey.getId(),
                60,
                80
        );

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTotalScore()).isBetween(60, 80);
    }

    @Test
    @Order(18)
    @DisplayName("상위 n개 결과 조회")
    void testFindTopScoresBySurveyId() {
        // given
        resultRepository.save(result1);
        resultRepository.save(result2);
        resultRepository.save(result3);

        // when
        List<CompetencyResult> results = resultRepository.findTopScoresBySurveyId(testSurvey.getId(), 2);

        // then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getTotalScore()).isGreaterThanOrEqualTo(results.get(1).getTotalScore());
    }

    // ========== 통계 메서드 테스트 ==========

    @Test
    @Order(19)
    @DisplayName("특정 설문의 결과 개수")
    void testCountBySurvey() {
        // given
        resultRepository.save(result1);
        resultRepository.save(result2);
        resultRepository.save(result3);

        // when
        long count = resultRepository.countBySurvey(testSurvey);

        // then
        assertThat(count).isEqualTo(3);
    }

    @Test
    @Order(20)
    @DisplayName("특정 사용자의 결과 개수")
    void testCountByUserId() {
        // given
        resultRepository.save(result1);

        // when
        long count = resultRepository.countByUserId(1001L);

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    @Order(21)
    @DisplayName("특정 역량 수준의 결과 개수")
    void testCountByCompetencyLevel() {
        // given
        resultRepository.save(result1);
        resultRepository.save(result2);
        resultRepository.save(result3);

        // when
        long count = resultRepository.countByCompetencyLevel("중급");

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    @Order(22)
    @DisplayName("평균 점수 조회")
    void testFindAverageScoreBySurveyId() {
        // given
        resultRepository.save(result1);
        resultRepository.save(result2);
        resultRepository.save(result3);

        // when
        Optional<Double> avgScore = resultRepository.findAverageScoreBySurveyId(testSurvey.getId());

        // then
        assertThat(avgScore).isPresent();
        assertThat(avgScore.get()).isCloseTo(66.0, within(1.0));
    }

    @Test
    @Order(23)
    @DisplayName("최고 점수 조회")
    void testFindMaxScoreBySurveyId() {
        // given
        resultRepository.save(result1);
        resultRepository.save(result2);
        resultRepository.save(result3);

        // when
        Optional<Integer> maxScore = resultRepository.findMaxScoreBySurveyId(testSurvey.getId());

        // then
        assertThat(maxScore).isPresent();
        assertThat(maxScore.get()).isEqualTo(85);
    }

    @Test
    @Order(24)
    @DisplayName("최저 점수 조회")
    void testFindMinScoreBySurveyId() {
        // given
        resultRepository.save(result1);
        resultRepository.save(result2);
        resultRepository.save(result3);

        // when
        Optional<Integer> minScore = resultRepository.findMinScoreBySurveyId(testSurvey.getId());

        // then
        assertThat(minScore).isPresent();
        assertThat(minScore.get()).isEqualTo(45);
    }

    @Test
    @Order(25)
    @DisplayName("사용자 평균 점수 조회")
    void testFindAverageScoreByUserId() {
        // given
        resultRepository.save(result1);

        // when
        Optional<Double> avgScore = resultRepository.findAverageScoreByUserId(1001L);

        // then
        assertThat(avgScore).isPresent();
        assertThat(avgScore.get()).isEqualTo(85.0);
    }

    // ========== 사용자 역량 추이 조회 테스트 ==========

    @Test
    @Order(26)
    @DisplayName("사용자 역량 변화 추이 조회")
    void testFindUserCompetencyTrend() {
        // given
        resultRepository.save(result1);

        // when
        List<CompetencyResult> trend = resultRepository.findUserCompetencyTrend(1001L);

        // then
        assertThat(trend).hasSize(1);
    }

    @Test
    @Order(27)
    @DisplayName("최근 2개 결과 조회")
    void testFindRecentTwoResultsByUserId() {
        // given
        resultRepository.save(result1);

        // when
        List<CompetencyResult> recent = resultRepository.findRecentTwoResultsByUserId(1001L);

        // then
        assertThat(recent).hasSize(1);
    }

    // ========== 복합 조회 메서드 테스트 ==========

    @Test
    @Order(28)
    @DisplayName("우수 결과 조회")
    void testFindExcellentResultsBySurveyId() {
        // given
        resultRepository.save(result1);
        resultRepository.save(result2);
        resultRepository.save(result3);

        // when
        List<CompetencyResult> excellent = resultRepository.findExcellentResultsBySurveyId(testSurvey.getId());

        // then
        assertThat(excellent).hasSize(1);
        assertThat(excellent.get(0).getCompetencyLevel()).isEqualTo("고급");
    }

    @Test
    @Order(29)
    @DisplayName("개선 필요 결과 조회")
    void testFindImprovementNeededResultsBySurveyId() {
        // given
        resultRepository.save(result1);
        resultRepository.save(result2);
        resultRepository.save(result3);

        // when
        List<CompetencyResult> improvement = resultRepository.findImprovementNeededResultsBySurveyId(testSurvey.getId());

        // then
        assertThat(improvement).hasSize(1);
        assertThat(improvement.get(0).getCompetencyLevel()).isEqualTo("초급");
    }

    @Test
    @Order(30)
    @DisplayName("여러 사용자의 결과 일괄 조회")
    void testFindBySurveyIdAndUserIdIn() {
        // given
        resultRepository.save(result1);
        resultRepository.save(result2);
        resultRepository.save(result3);

        // when
        List<CompetencyResult> results = resultRepository.findBySurveyIdAndUserIdIn(
                testSurvey.getId(),
                List.of(1001L, 1002L)
        );

        // then
        assertThat(results).hasSize(2);
    }

    // ========== 분석용 메서드 테스트 ==========

    @Test
    @Order(31)
    @DisplayName("점수 분포 조회")
    void testFindScoreDistributionBySurveyId() {
        // given
        resultRepository.save(result1);
        resultRepository.save(result2);
        resultRepository.save(result3);

        // when
        List<Object[]> distribution = resultRepository.findScoreDistributionBySurveyId(testSurvey.getId());

        // then
        assertThat(distribution).isNotEmpty();
    }

    // ========== 비즈니스 로직 테스트 ==========

    @Test
    @Order(32)
    @DisplayName("역량 수준 판정 테스트")
    void testDetermineLevel() {
        // when & then
        assertThat(CompetencyResult.determineLevel(85, 100)).isEqualTo("고급");
        assertThat(CompetencyResult.determineLevel(65, 100)).isEqualTo("중급");
        assertThat(CompetencyResult.determineLevel(45, 100)).isEqualTo("초급");
    }

    @Test
    @Order(33)
    @DisplayName("Unique 제약 조건 테스트")
    void testUniqueConstraint() {
        // given
        resultRepository.save(result1);

        // when & then - 같은 설문, 같은 사용자로 중복 저장 시도
        CompetencyResult duplicate = CompetencyResult.create(
                testSurvey,
                1001L,
                90,
                "{}",
                "고급",
                "test",
                "test",
                "test"
        );

        // 중복 저장 시도 시 예외 발생
        assertThatThrownBy(() -> {
            resultRepository.saveAndFlush(duplicate);
        }).isInstanceOf(Exception.class);
        
        // 예외 발생 후 엔티티 매니저 세션 정리
        entityManager.clear();
    }
}
