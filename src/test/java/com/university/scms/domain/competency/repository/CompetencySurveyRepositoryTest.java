package com.university.scms.domain.competency.repository;

import com.university.scms.config.JpaConfig;
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
 * CompetencySurveyRepository 테스트
 * 실제 MySQL 데이터베이스를 사용하여 테스트합니다.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(JpaConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CompetencySurveyRepositoryTest {

    @Autowired
    private CompetencySurveyRepository surveyRepository;

    @Autowired
    private EntityManager entityManager;

    private CompetencySurvey activeSurvey;
    private CompetencySurvey inactiveSurvey;
    private CompetencySurvey studentSurvey;
    private CompetencySurvey staffSurvey;

    @BeforeEach
    @org.springframework.transaction.annotation.Transactional
    void setUp() {
        // 이전 테스트 데이터 완전 정리 (Native Query 사용)
        entityManager.createNativeQuery("DELETE FROM competency_surveys").executeUpdate();
        entityManager.flush();
        entityManager.clear();
        
        LocalDateTime now = LocalDateTime.now();
        
        // 활성 설문 (진행 중)
        activeSurvey = CompetencySurvey.create(
                "2024 역량 진단 설문",
                "학생들의 핵심 역량을 진단하는 설문입니다.",
                "COMPETENCY_ASSESSMENT",
                TargetRole.STUDENT,
                now.minusDays(7),
                now.plusDays(7),
                1L
        );

        // 비활성 설문
        inactiveSurvey = CompetencySurvey.create(
                "2023 역량 진단 설문",
                "작년 설문입니다.",
                "COMPETENCY_ASSESSMENT",
                TargetRole.ALL,
                now.minusMonths(2),
                now.minusMonths(1),
                1L
        );
        inactiveSurvey.deactivate();

        // 학생 전용 설문
        studentSurvey = CompetencySurvey.create(
                "학생 만족도 조사",
                "학생 대상 만족도 조사",
                "SATISFACTION",
                TargetRole.STUDENT,
                now.minusDays(3),
                now.plusDays(10),
                1L
        );

        // 교직원 전용 설문
        staffSurvey = CompetencySurvey.create(
                "교직원 역량 평가",
                "교직원 대상 역량 평가",
                "STAFF_EVALUATION",
                TargetRole.STAFF,
                now.minusDays(5),
                now.plusDays(5),
                2L
        );
    }

    @AfterEach
    void tearDown() {
        surveyRepository.deleteAll();
    }

    // ========== 기본 CRUD 테스트 ==========

    @Test
    @Order(1)
    @DisplayName("설문 생성 테스트")
    void testCreateSurvey() {
        // when
        CompetencySurvey saved = surveyRepository.save(activeSurvey);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("2024 역량 진단 설문");
        assertThat(saved.getIsActive()).isTrue();
        assertThat(saved.getTargetRole()).isEqualTo(TargetRole.STUDENT);
        assertThat(saved.getCreatedBy()).isEqualTo(1L);
    }

    @Test
    @Order(2)
    @DisplayName("설문 조회 테스트")
    void testFindById() {
        // given
        CompetencySurvey saved = surveyRepository.save(activeSurvey);

        // when
        Optional<CompetencySurvey> found = surveyRepository.findById(saved.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("2024 역량 진단 설문");
    }

    @Test
    @Order(3)
    @DisplayName("설문 수정 테스트")
    void testUpdateSurvey() {
        // given
        CompetencySurvey saved = surveyRepository.save(activeSurvey);

        // when
        saved.update(
                "수정된 설문 제목",
                "수정된 설명",
                "MODIFIED_TYPE",
                TargetRole.ALL,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(14)
        );
        CompetencySurvey updated = surveyRepository.save(saved);

        // then
        assertThat(updated.getTitle()).isEqualTo("수정된 설문 제목");
        assertThat(updated.getSurveyType()).isEqualTo("MODIFIED_TYPE");
        assertThat(updated.getTargetRole()).isEqualTo(TargetRole.ALL);
    }

    @Test
    @Order(4)
    @DisplayName("설문 삭제 테스트")
    void testDeleteSurvey() {
        // given
        CompetencySurvey saved = surveyRepository.save(activeSurvey);
        Long surveyId = saved.getId();

        // when
        surveyRepository.delete(saved);

        // then
        Optional<CompetencySurvey> found = surveyRepository.findById(surveyId);
        assertThat(found).isEmpty();
    }

    // ========== 기본 조회 메서드 테스트 ==========

    @Test
    @Order(5)
    @DisplayName("제목으로 설문 조회")
    void testFindByTitle() {
        // given
        surveyRepository.save(activeSurvey);

        // when
        Optional<CompetencySurvey> found = surveyRepository.findByTitle("2024 역량 진단 설문");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getDescription()).contains("핵심 역량");
    }

    @Test
    @Order(6)
    @DisplayName("활성화 상태로 설문 조회")
    void testFindByIsActive() {
        // given
        surveyRepository.save(activeSurvey);
        surveyRepository.save(inactiveSurvey);

        // when
        List<CompetencySurvey> activeSurveys = surveyRepository.findByIsActive(true);

        // then
        assertThat(activeSurveys).hasSize(1);
        assertThat(activeSurveys.get(0).getIsActive()).isTrue();
    }

    @Test
    @Order(7)
    @DisplayName("설문 유형별 조회")
    void testFindBySurveyType() {
        // given
        surveyRepository.save(activeSurvey);
        surveyRepository.save(studentSurvey);

        // when
        List<CompetencySurvey> surveys = surveyRepository.findBySurveyType("COMPETENCY_ASSESSMENT");

        // then
        assertThat(surveys).hasSize(1);
        assertThat(surveys.get(0).getSurveyType()).isEqualTo("COMPETENCY_ASSESSMENT");
    }

    // ========== 대상 역할별 조회 테스트 ==========

    @Test
    @Order(8)
    @DisplayName("대상 역할별 설문 조회")
    void testFindByTargetRole() {
        // given
        surveyRepository.save(studentSurvey);
        surveyRepository.save(staffSurvey);

        // when
        List<CompetencySurvey> studentSurveys = surveyRepository.findByTargetRole(TargetRole.STUDENT);

        // then
        assertThat(studentSurveys).hasSize(1);
        assertThat(studentSurveys.get(0).getTargetRole()).isEqualTo(TargetRole.STUDENT);
    }

    @Test
    @Order(9)
    @DisplayName("활성화된 특정 역할 대상 설문 조회")
    void testFindByTargetRoleAndIsActive() {
        // given
        surveyRepository.save(activeSurvey);
        surveyRepository.save(inactiveSurvey);

        // when
        List<CompetencySurvey> surveys = surveyRepository.findByTargetRoleAndIsActive(TargetRole.STUDENT, true);

        // then
        assertThat(surveys).hasSize(1);
        assertThat(surveys.get(0).getIsActive()).isTrue();
        assertThat(surveys.get(0).getTargetRole()).isEqualTo(TargetRole.STUDENT);
    }

    @Test
    @Order(10)
    @DisplayName("특정 역할이 응답 가능한 활성 설문 조회 (ALL 포함)")
    void testFindActiveSurveysForRole() {
        // given
        CompetencySurvey allSurvey = CompetencySurvey.create(
                "전체 대상 설문",
                "모든 사용자 대상",
                "GENERAL",
                TargetRole.ALL,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                1L
        );
        surveyRepository.save(activeSurvey);
        surveyRepository.save(allSurvey);
        surveyRepository.save(staffSurvey);

        // when
        List<CompetencySurvey> surveys = surveyRepository.findActiveSurveysForRole(TargetRole.STUDENT);

        // then
        assertThat(surveys).hasSize(2); // STUDENT용 + ALL용
    }

    // ========== 기간별 조회 테스트 ==========

    @Test
    @Order(11)
    @DisplayName("현재 진행 중인 설문 조회")
    void testFindOngoingSurveys() {
        // given
        surveyRepository.save(activeSurvey);
        surveyRepository.save(inactiveSurvey);

        // when
        List<CompetencySurvey> ongoing = surveyRepository.findOngoingSurveys(LocalDateTime.now());

        // then
        assertThat(ongoing).isNotEmpty();
        assertThat(ongoing).allMatch(s -> s.isWithinPeriod());
    }

    @Test
    @Order(12)
    @DisplayName("특정 기간에 시작하는 설문 조회")
    void testFindSurveysByStartDateBetween() {
        // given
        surveyRepository.save(activeSurvey);
        surveyRepository.save(studentSurvey);

        LocalDateTime start = LocalDateTime.now().minusDays(10);
        LocalDateTime end = LocalDateTime.now();

        // when
        List<CompetencySurvey> surveys = surveyRepository.findSurveysByStartDateBetween(start, end);

        // then
        assertThat(surveys).isNotEmpty();
    }

    @Test
    @Order(13)
    @DisplayName("종료 예정 설문 조회")
    void testFindSurveysEndingSoon() {
        // given
        surveyRepository.save(activeSurvey);
        surveyRepository.save(studentSurvey);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = now.plusDays(10);

        // when
        List<CompetencySurvey> surveys = surveyRepository.findSurveysEndingSoon(now, deadline);

        // then
        assertThat(surveys).isNotEmpty();
    }

    // ========== 생성자별 조회 테스트 ==========

    @Test
    @Order(14)
    @DisplayName("생성자별 설문 조회")
    void testFindByCreatedBy() {
        // given
        surveyRepository.save(activeSurvey);
        surveyRepository.save(staffSurvey);

        // when
        List<CompetencySurvey> surveys = surveyRepository.findByCreatedBy(1L);

        // then
        assertThat(surveys).hasSize(1);
        assertThat(surveys.get(0).getCreatedBy()).isEqualTo(1L);
    }

    @Test
    @Order(15)
    @DisplayName("생성자별 활성 설문 조회")
    void testFindByCreatedByAndIsActive() {
        // given
        surveyRepository.save(activeSurvey);
        surveyRepository.save(inactiveSurvey);

        // when
        List<CompetencySurvey> surveys = surveyRepository.findByCreatedByAndIsActive(1L, true);

        // then
        assertThat(surveys).hasSize(1);
        assertThat(surveys.get(0).getIsActive()).isTrue();
    }

    // ========== 복합 조회 메서드 테스트 ==========

    @Test
    @Order(16)
    @DisplayName("설문 유형 및 활성화 상태로 조회")
    void testFindBySurveyTypeAndIsActive() {
        // given
        surveyRepository.save(activeSurvey);
        surveyRepository.save(inactiveSurvey);

        // when
        List<CompetencySurvey> surveys = surveyRepository.findBySurveyTypeAndIsActive("COMPETENCY_ASSESSMENT", true);

        // then
        assertThat(surveys).hasSize(1);
        assertThat(surveys.get(0).getIsActive()).isTrue();
    }

    @Test
    @Order(17)
    @DisplayName("응답 가능한 설문 조회")
    void testFindAvailableSurveysForRole() {
        // given
        surveyRepository.save(activeSurvey);
        surveyRepository.save(studentSurvey);
        surveyRepository.save(inactiveSurvey);

        // when
        List<CompetencySurvey> surveys = surveyRepository.findAvailableSurveysForRole(
                TargetRole.STUDENT,
                LocalDateTime.now()
        );

        // then
        assertThat(surveys).isNotEmpty();
        assertThat(surveys).allMatch(s -> s.canRespond());
    }

    // ========== 통계 메서드 테스트 ==========

    @Test
    @Order(18)
    @DisplayName("활성 설문 수 조회")
    void testCountByIsActive() {
        // given
        surveyRepository.save(activeSurvey);
        surveyRepository.save(studentSurvey);
        surveyRepository.save(inactiveSurvey);

        // when
        long count = surveyRepository.countByIsActive(true);

        // then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @Order(19)
    @DisplayName("특정 역할 대상 설문 수 조회")
    void testCountByTargetRole() {
        // given
        surveyRepository.save(activeSurvey);
        surveyRepository.save(studentSurvey);
        surveyRepository.save(staffSurvey);

        // when
        long count = surveyRepository.countByTargetRole(TargetRole.STUDENT);

        // then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @Order(20)
    @DisplayName("진행 중인 설문 수 조회")
    void testCountOngoingSurveys() {
        // given
        surveyRepository.save(activeSurvey);
        surveyRepository.save(inactiveSurvey);

        // when
        long count = surveyRepository.countOngoingSurveys(LocalDateTime.now());

        // then
        assertThat(count).isGreaterThanOrEqualTo(1);
    }

    // ========== 검색 메서드 테스트 ==========

    @Test
    @Order(21)
    @DisplayName("제목으로 설문 검색")
    void testFindByTitleContaining() {
        // given
        surveyRepository.save(activeSurvey);
        surveyRepository.save(studentSurvey);

        // when
        List<CompetencySurvey> surveys = surveyRepository.findByTitleContaining("역량");

        // then
        assertThat(surveys).hasSize(1);
        assertThat(surveys.get(0).getTitle()).contains("역량");
    }

    @Test
    @Order(22)
    @DisplayName("키워드로 설문 검색")
    void testSearchByKeyword() {
        // given
        surveyRepository.save(activeSurvey);
        surveyRepository.save(studentSurvey);

        // when
        List<CompetencySurvey> surveys = surveyRepository.searchByKeyword("학생");

        // then
        assertThat(surveys).hasSize(2);
    }

    @Test
    @Order(23)
    @DisplayName("활성 설문에서 키워드 검색")
    void testSearchActiveByKeyword() {
        // given
        surveyRepository.save(activeSurvey);
        surveyRepository.save(inactiveSurvey);

        // when
        List<CompetencySurvey> surveys = surveyRepository.searchActiveByKeyword("역량");

        // then
        assertThat(surveys).hasSize(1);
        assertThat(surveys.get(0).getIsActive()).isTrue();
    }

    // ========== 비즈니스 로직 테스트 ==========

    @Test
    @Order(24)
    @DisplayName("설문 활성화/비활성화 테스트")
    void testActivateDeactivate() {
        // given
        CompetencySurvey saved = surveyRepository.save(activeSurvey);

        // when - 비활성화
        saved.deactivate();
        surveyRepository.save(saved);

        // then
        assertThat(saved.getIsActive()).isFalse();

        // when - 활성화
        saved.activate();
        surveyRepository.save(saved);

        // then
        assertThat(saved.getIsActive()).isTrue();
    }

    @Test
    @Order(25)
    @DisplayName("설문 기간 내 확인 테스트")
    void testIsWithinPeriod() {
        // given
        CompetencySurvey saved = surveyRepository.save(activeSurvey);

        // then
        assertThat(saved.isWithinPeriod()).isTrue();
    }

    @Test
    @Order(26)
    @DisplayName("설문 응답 가능 여부 테스트")
    void testCanRespond() {
        // given
        CompetencySurvey saved = surveyRepository.save(activeSurvey);

        // then
        assertThat(saved.canRespond()).isTrue();

        // when - 비활성화
        saved.deactivate();

        // then
        assertThat(saved.canRespond()).isFalse();
    }
}
