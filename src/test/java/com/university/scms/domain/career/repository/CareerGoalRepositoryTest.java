package com.university.scms.domain.career.repository;

import com.university.scms.config.JpaConfig;
import com.university.scms.domain.career.entity.CareerGoal;
import com.university.scms.domain.career.entity.CareerPlan;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * CareerGoalRepository 테스트
 * 실제 MySQL 데이터베이스를 사용하여 테스트합니다.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(JpaConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CareerGoalRepositoryTest {

    @Autowired
    private CareerGoalRepository careerGoalRepository;

    @Autowired
    private CareerPlanRepository careerPlanRepository;

    private CareerPlan testPlan;
    private CareerGoal testGoal1;
    private CareerGoal testGoal2;
    private CareerGoal testGoal3;

    @BeforeEach
    void setUp() {
        // 진로 계획 생성
        testPlan = CareerPlan.builder()
                .userId(1L)
                .title("소프트웨어 개발자 진로 계획")
                .targetField("소프트웨어 개발")
                .description("백엔드 개발자로 성장하기")
                .targetDate(LocalDate.now().plusYears(2))
                .status("IN_PROGRESS")
                .build();
        testPlan = careerPlanRepository.save(testPlan);

        // 진로 목표 생성
        testGoal1 = CareerGoal.builder()
                .careerPlan(testPlan)
                .title("Java 심화 학습")
                .description("Java 전문가 수준으로 성장")
                .targetDate(LocalDate.now().plusMonths(6))
                .status("IN_PROGRESS")
                .goalOrder(1)
                .build();

        testGoal2 = CareerGoal.builder()
                .careerPlan(testPlan)
                .title("Spring Framework 마스터")
                .description("Spring Boot 프로젝트 완성")
                .targetDate(LocalDate.now().plusMonths(12))
                .status("NOT_STARTED")
                .goalOrder(2)
                .build();

        testGoal3 = CareerGoal.builder()
                .careerPlan(testPlan)
                .title("알고리즘 문제 해결")
                .description("코딩테스트 준비 완료")
                .targetDate(LocalDate.now().plusMonths(3))
                .status("COMPLETED")
                .goalOrder(3)
                .build();
    }

    @AfterEach
    void tearDown() {
        careerGoalRepository.deleteAll();
        careerPlanRepository.deleteAll();
    }

    // ========== 기본 CRUD 테스트 ==========

    @Test
    @Order(1)
    @DisplayName("진로 목표 생성 테스트")
    void testCreateCareerGoal() {
        // when
        CareerGoal saved = careerGoalRepository.save(testGoal1);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCareerPlan().getId()).isEqualTo(testPlan.getId());
        assertThat(saved.getTitle()).isEqualTo("Java 심화 학습");
        assertThat(saved.getStatus()).isEqualTo("IN_PROGRESS");
        assertThat(saved.getGoalOrder()).isEqualTo(1);
    }

    @Test
    @Order(2)
    @DisplayName("진로 목표 조회 테스트")
    void testFindCareerGoal() {
        // given
        CareerGoal saved = careerGoalRepository.save(testGoal1);

        // when
        Optional<CareerGoal> found = careerGoalRepository.findById(saved.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Java 심화 학습");
    }

    @Test
    @Order(3)
    @DisplayName("진로 목표 수정 테스트")
    void testUpdateCareerGoal() {
        // given
        CareerGoal saved = careerGoalRepository.save(testGoal1);

        // when
        saved = CareerGoal.builder()
                .id(saved.getId())
                .careerPlan(saved.getCareerPlan())
                .title("Java 및 JVM 심화 학습")
                .description("Java와 JVM 내부 동작 이해")
                .targetDate(saved.getTargetDate())
                .status("IN_PROGRESS")
                .goalOrder(saved.getGoalOrder())
                .build();
        CareerGoal updated = careerGoalRepository.save(saved);

        // then
        assertThat(updated.getTitle()).isEqualTo("Java 및 JVM 심화 학습");
        assertThat(updated.getDescription()).isEqualTo("Java와 JVM 내부 동작 이해");
    }

    @Test
    @Order(4)
    @DisplayName("진로 목표 삭제 테스트")
    void testDeleteCareerGoal() {
        // given
        CareerGoal saved = careerGoalRepository.save(testGoal1);

        // when
        careerGoalRepository.deleteById(saved.getId());

        // then
        Optional<CareerGoal> found = careerGoalRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }

    // ========== 진로 계획별 조회 테스트 ==========

    @Test
    @Order(5)
    @DisplayName("진로 계획 ID로 목표 목록 조회")
    void testFindByCareerPlanId() {
        // given
        careerGoalRepository.save(testGoal1);
        careerGoalRepository.save(testGoal2);
        careerGoalRepository.save(testGoal3);

        // when
        List<CareerGoal> goals = careerGoalRepository.findByCareerPlanId(testPlan.getId());

        // then
        assertThat(goals).hasSize(3);
    }

    @Test
    @Order(6)
    @DisplayName("진로 계획 ID로 목표 목록 조회 (정렬)")
    void testFindByCareerPlanIdOrderByGoalOrder() {
        // given
        careerGoalRepository.save(testGoal2);
        careerGoalRepository.save(testGoal3);
        careerGoalRepository.save(testGoal1);

        // when
        List<CareerGoal> goals = careerGoalRepository.findByCareerPlanIdOrderByGoalOrder(testPlan.getId());

        // then
        assertThat(goals).hasSize(3);
        assertThat(goals.get(0).getGoalOrder()).isEqualTo(1);
        assertThat(goals.get(1).getGoalOrder()).isEqualTo(2);
        assertThat(goals.get(2).getGoalOrder()).isEqualTo(3);
    }

    @Test
    @Order(7)
    @DisplayName("진로 계획으로 목표 조회 (엔티티 참조)")
    void testFindByCareerPlan() {
        // given
        careerGoalRepository.save(testGoal1);
        careerGoalRepository.save(testGoal2);

        // when
        List<CareerGoal> goals = careerGoalRepository.findByCareerPlan(testPlan);

        // then
        assertThat(goals).hasSize(2);
    }

    // ========== 상태별 조회 테스트 ==========

    @Test
    @Order(8)
    @DisplayName("진로 계획의 상태별 목표 조회")
    void testFindByCareerPlanIdAndStatus() {
        // given
        careerGoalRepository.save(testGoal1);
        careerGoalRepository.save(testGoal2);
        careerGoalRepository.save(testGoal3);

        // when
        List<CareerGoal> inProgress = careerGoalRepository.findByCareerPlanIdAndStatus(
            testPlan.getId(), "IN_PROGRESS"
        );
        List<CareerGoal> notStarted = careerGoalRepository.findByCareerPlanIdAndStatus(
            testPlan.getId(), "NOT_STARTED"
        );

        // then
        assertThat(inProgress).hasSize(1);
        assertThat(notStarted).hasSize(1);
    }

    @Test
    @Order(9)
    @DisplayName("진로 계획의 완료된 목표 조회")
    void testFindCompletedGoalsByCareerPlan() {
        // given
        careerGoalRepository.save(testGoal1);
        careerGoalRepository.save(testGoal2);
        careerGoalRepository.save(testGoal3);

        // when
        List<CareerGoal> completed = careerGoalRepository.findCompletedGoalsByCareerPlan(testPlan.getId());

        // then
        assertThat(completed).hasSize(1);
        assertThat(completed.get(0).getStatus()).isEqualTo("COMPLETED");
    }

    @Test
    @Order(10)
    @DisplayName("진로 계획의 미완료 목표 조회")
    void testFindIncompleteGoalsByCareerPlan() {
        // given
        careerGoalRepository.save(testGoal1);
        careerGoalRepository.save(testGoal2);
        careerGoalRepository.save(testGoal3);

        // when
        List<CareerGoal> incomplete = careerGoalRepository.findIncompleteGoalsByCareerPlan(testPlan.getId());

        // then
        assertThat(incomplete).hasSize(2);
        assertThat(incomplete).noneMatch(g -> g.getStatus().equals("COMPLETED"));
    }

    @Test
    @Order(11)
    @DisplayName("상태별 전체 목표 조회")
    void testFindByStatus() {
        // given
        careerGoalRepository.save(testGoal1);
        careerGoalRepository.save(testGoal2);
        careerGoalRepository.save(testGoal3);

        // when
        List<CareerGoal> completed = careerGoalRepository.findByStatus("COMPLETED");

        // then
        assertThat(completed).hasSize(1);
    }

    // ========== 날짜 관련 조회 테스트 ==========

    @Test
    @Order(12)
    @DisplayName("목표 날짜 이전의 미완료 목표 조회")
    void testFindUncompletedBeforeDate() {
        // given
        CareerGoal pastGoal = CareerGoal.builder()
                .careerPlan(testPlan)
                .title("과거 목표")
                .targetDate(LocalDate.now().minusDays(1))
                .status("IN_PROGRESS")
                .goalOrder(4)
                .build();
        careerGoalRepository.save(pastGoal);
        careerGoalRepository.save(testGoal1);

        // when
        List<CareerGoal> goals = careerGoalRepository.findUncompletedBeforeDate(LocalDate.now());

        // then
        assertThat(goals).hasSize(1);
        assertThat(goals.get(0).getTitle()).isEqualTo("과거 목표");
    }

    @Test
    @Order(13)
    @DisplayName("특정 기간 내 목표 날짜를 가진 목표 조회")
    void testFindByTargetDateBetween() {
        // given
        careerGoalRepository.save(testGoal1);
        careerGoalRepository.save(testGoal2);
        careerGoalRepository.save(testGoal3);

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusMonths(6);

        // when
        List<CareerGoal> goals = careerGoalRepository.findByTargetDateBetween(startDate, endDate);

        // then
        assertThat(goals).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @Order(14)
    @DisplayName("진로 계획의 특정 기간 내 목표 조회")
    void testFindByCareerPlanIdAndTargetDateBetween() {
        // given
        careerGoalRepository.save(testGoal1);
        careerGoalRepository.save(testGoal2);
        careerGoalRepository.save(testGoal3);

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusMonths(12);

        // when
        List<CareerGoal> goals = careerGoalRepository.findByCareerPlanIdAndTargetDateBetween(
            testPlan.getId(), startDate, endDate
        );

        // then
        assertThat(goals).hasSize(3);
    }

    // ========== 순서 관련 조회 테스트 ==========

    @Test
    @Order(15)
    @DisplayName("진로 계획의 다음 목표 순서 조회")
    void testFindNextGoalOrder() {
        // given
        careerGoalRepository.save(testGoal1);
        careerGoalRepository.save(testGoal2);
        careerGoalRepository.save(testGoal3);

        // when
        Integer nextOrder = careerGoalRepository.findNextGoalOrder(testPlan.getId());

        // then
        assertThat(nextOrder).isEqualTo(4);
    }

    @Test
    @Order(16)
    @DisplayName("진로 계획의 다음 목표 순서 조회 (빈 계획)")
    void testFindNextGoalOrderWhenEmpty() {
        // given
        CareerPlan emptyPlan = CareerPlan.builder()
                .userId(2L)
                .title("빈 계획")
                .targetField("테스트")
                .status("IN_PROGRESS")
                .build();
        emptyPlan = careerPlanRepository.save(emptyPlan);

        // when
        Integer nextOrder = careerGoalRepository.findNextGoalOrder(emptyPlan.getId());

        // then
        assertThat(nextOrder).isEqualTo(1);
    }

    @Test
    @Order(17)
    @DisplayName("특정 순서의 목표 조회")
    void testFindByCareerPlanIdAndGoalOrder() {
        // given
        careerGoalRepository.save(testGoal1);
        careerGoalRepository.save(testGoal2);

        // when
        Optional<CareerGoal> goal = careerGoalRepository.findByCareerPlanIdAndGoalOrder(
            testPlan.getId(), 2
        );

        // then
        assertThat(goal).isPresent();
        assertThat(goal.get().getTitle()).isEqualTo("Spring Framework 마스터");
    }

    // ========== 검색 테스트 ==========

    @Test
    @Order(18)
    @DisplayName("제목으로 목표 검색")
    void testFindByTitleContaining() {
        // given
        careerGoalRepository.save(testGoal1);
        careerGoalRepository.save(testGoal2);
        careerGoalRepository.save(testGoal3);

        // when
        List<CareerGoal> goals = careerGoalRepository.findByTitleContaining("학습");

        // then
        assertThat(goals).hasSizeGreaterThanOrEqualTo(1);
        assertThat(goals).allMatch(g -> g.getTitle().contains("학습"));
    }

    @Test
    @Order(19)
    @DisplayName("진로 계획 내에서 제목으로 목표 검색")
    void testFindByCareerPlanIdAndTitleContaining() {
        // given
        careerGoalRepository.save(testGoal1);
        careerGoalRepository.save(testGoal2);

        // when
        List<CareerGoal> goals = careerGoalRepository.findByCareerPlanIdAndTitleContaining(
            testPlan.getId(), "Java"
        );

        // then
        assertThat(goals).hasSize(1);
        assertThat(goals.get(0).getTitle()).contains("Java");
    }

    // ========== 통계 테스트 ==========

    @Test
    @Order(20)
    @DisplayName("진로 계획의 목표 개수 조회")
    void testCountByCareerPlanId() {
        // given
        careerGoalRepository.save(testGoal1);
        careerGoalRepository.save(testGoal2);
        careerGoalRepository.save(testGoal3);

        // when
        long count = careerGoalRepository.countByCareerPlanId(testPlan.getId());

        // then
        assertThat(count).isEqualTo(3);
    }

    @Test
    @Order(21)
    @DisplayName("진로 계획의 상태별 목표 개수 조회")
    void testCountByCareerPlanIdAndStatus() {
        // given
        careerGoalRepository.save(testGoal1);
        careerGoalRepository.save(testGoal2);
        careerGoalRepository.save(testGoal3);

        // when
        long inProgressCount = careerGoalRepository.countByCareerPlanIdAndStatus(
            testPlan.getId(), "IN_PROGRESS"
        );
        long completedCount = careerGoalRepository.countByCareerPlanIdAndStatus(
            testPlan.getId(), "COMPLETED"
        );

        // then
        assertThat(inProgressCount).isEqualTo(1);
        assertThat(completedCount).isEqualTo(1);
    }

    @Test
    @Order(22)
    @DisplayName("진로 계획의 완료율 계산")
    void testCalculateCompletionRate() {
        // given
        careerGoalRepository.save(testGoal1);
        careerGoalRepository.save(testGoal2);
        careerGoalRepository.save(testGoal3);

        // when
        Double completionRate = careerGoalRepository.calculateCompletionRate(testPlan.getId());

        // then
        assertThat(completionRate).isNotNull();
        assertThat(completionRate).isGreaterThanOrEqualTo(0.0);
        assertThat(completionRate).isLessThanOrEqualTo(100.0);
        // 3개 중 1개 완료이므로 33.33%
        assertThat(completionRate).isCloseTo(33.33, within(0.1));
    }

    @Test
    @Order(23)
    @DisplayName("상태별 전체 목표 개수 조회")
    void testCountByStatus() {
        // given
        careerGoalRepository.save(testGoal1);
        careerGoalRepository.save(testGoal2);
        careerGoalRepository.save(testGoal3);

        // when
        long completedCount = careerGoalRepository.countByStatus("COMPLETED");

        // then
        assertThat(completedCount).isEqualTo(1);
    }

    // ========== 존재 여부 확인 테스트 ==========

    @Test
    @Order(24)
    @DisplayName("진로 계획의 특정 제목 목표 존재 여부 확인")
    void testExistsByCareerPlanIdAndTitle() {
        // given
        careerGoalRepository.save(testGoal1);

        // when
        boolean exists = careerGoalRepository.existsByCareerPlanIdAndTitle(
            testPlan.getId(), "Java 심화 학습"
        );
        boolean notExists = careerGoalRepository.existsByCareerPlanIdAndTitle(
            testPlan.getId(), "존재하지 않는 목표"
        );

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @Order(25)
    @DisplayName("진로 계획의 특정 순서 목표 존재 여부 확인")
    void testExistsByCareerPlanIdAndGoalOrder() {
        // given
        careerGoalRepository.save(testGoal1);

        // when
        boolean exists = careerGoalRepository.existsByCareerPlanIdAndGoalOrder(
            testPlan.getId(), 1
        );
        boolean notExists = careerGoalRepository.existsByCareerPlanIdAndGoalOrder(
            testPlan.getId(), 99
        );

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    // ========== 삭제 테스트 ==========

    @Test
    @Order(26)
    @DisplayName("진로 계획의 모든 목표 삭제")
    void testDeleteByCareerPlanId() {
        // given
        careerGoalRepository.save(testGoal1);
        careerGoalRepository.save(testGoal2);
        careerGoalRepository.save(testGoal3);

        // when
        careerGoalRepository.deleteByCareerPlanId(testPlan.getId());

        // then
        List<CareerGoal> goals = careerGoalRepository.findByCareerPlanId(testPlan.getId());
        assertThat(goals).isEmpty();
    }

    // ========== 비즈니스 로직 테스트 ==========

    @Test
    @Order(27)
    @DisplayName("목표 완료 여부 확인")
    void testIsCompleted() {
        // given
        CareerGoal saved = careerGoalRepository.save(testGoal3);

        // when & then
        assertThat(saved.isCompleted()).isTrue();
    }
}
