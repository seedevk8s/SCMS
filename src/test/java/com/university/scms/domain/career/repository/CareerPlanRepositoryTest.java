package com.university.scms.domain.career.repository;

import com.university.scms.config.JpaConfig;
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
 * CareerPlanRepository 테스트
 * 실제 MySQL 데이터베이스를 사용하여 테스트합니다.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(JpaConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CareerPlanRepositoryTest {

    @Autowired
    private CareerPlanRepository careerPlanRepository;

    private CareerPlan testPlan1;
    private CareerPlan testPlan2;
    private CareerPlan testPlan3;
    
    private final Long TEST_USER_ID_1 = 1L;
    private final Long TEST_USER_ID_2 = 2L;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        testPlan1 = CareerPlan.builder()
                .userId(TEST_USER_ID_1)
                .title("소프트웨어 개발자 진로 계획")
                .targetField("소프트웨어 개발")
                .description("백엔드 개발자로 성장하기 위한 진로 계획")
                .targetDate(LocalDate.now().plusYears(2))
                .status("IN_PROGRESS")
                .build();

        testPlan2 = CareerPlan.builder()
                .userId(TEST_USER_ID_1)
                .title("데이터 과학자 진로 계획")
                .targetField("데이터 과학")
                .description("데이터 분석 전문가로 성장하기")
                .targetDate(LocalDate.now().plusYears(3))
                .status("IN_PROGRESS")
                .build();

        testPlan3 = CareerPlan.builder()
                .userId(TEST_USER_ID_2)
                .title("AI 엔지니어 진로 계획")
                .targetField("인공지능")
                .description("AI 연구 및 개발 전문가")
                .targetDate(LocalDate.now().plusYears(1))
                .status("COMPLETED")
                .build();
    }

    @AfterEach
    void tearDown() {
        careerPlanRepository.deleteAll();
    }

    // ========== 기본 CRUD 테스트 ==========

    @Test
    @Order(1)
    @DisplayName("진로 계획 생성 테스트")
    void testCreateCareerPlan() {
        // when
        CareerPlan saved = careerPlanRepository.save(testPlan1);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUserId()).isEqualTo(TEST_USER_ID_1);
        assertThat(saved.getTitle()).isEqualTo("소프트웨어 개발자 진로 계획");
        assertThat(saved.getTargetField()).isEqualTo("소프트웨어 개발");
        assertThat(saved.getStatus()).isEqualTo("IN_PROGRESS");
    }

    @Test
    @Order(2)
    @DisplayName("진로 계획 조회 테스트")
    void testFindCareerPlan() {
        // given
        CareerPlan saved = careerPlanRepository.save(testPlan1);

        // when
        Optional<CareerPlan> found = careerPlanRepository.findById(saved.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("소프트웨어 개발자 진로 계획");
    }

    @Test
    @Order(3)
    @DisplayName("진로 계획 수정 테스트")
    void testUpdateCareerPlan() {
        // given
        CareerPlan saved = careerPlanRepository.save(testPlan1);
        
        // when
        saved = CareerPlan.builder()
                .id(saved.getId())
                .userId(saved.getUserId())
                .title("시니어 소프트웨어 개발자 진로 계획")
                .targetField(saved.getTargetField())
                .description("시니어 백엔드 개발자로 성장하기")
                .targetDate(saved.getTargetDate())
                .status("IN_PROGRESS")
                .build();
        CareerPlan updated = careerPlanRepository.save(saved);

        // then
        assertThat(updated.getTitle()).isEqualTo("시니어 소프트웨어 개발자 진로 계획");
        assertThat(updated.getDescription()).isEqualTo("시니어 백엔드 개발자로 성장하기");
    }

    @Test
    @Order(4)
    @DisplayName("진로 계획 삭제 테스트")
    void testDeleteCareerPlan() {
        // given
        CareerPlan saved = careerPlanRepository.save(testPlan1);

        // when
        careerPlanRepository.deleteById(saved.getId());

        // then
        Optional<CareerPlan> found = careerPlanRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }

    // ========== 사용자별 조회 테스트 ==========

    @Test
    @Order(5)
    @DisplayName("사용자 ID로 진로 계획 목록 조회")
    void testFindByUserId() {
        // given
        careerPlanRepository.save(testPlan1);
        careerPlanRepository.save(testPlan2);
        careerPlanRepository.save(testPlan3);

        // when
        List<CareerPlan> plans = careerPlanRepository.findByUserId(TEST_USER_ID_1);

        // then
        assertThat(plans).hasSize(2);
        assertThat(plans).extracting("userId").containsOnly(TEST_USER_ID_1);
    }

    @Test
    @Order(6)
    @DisplayName("사용자 ID와 상태로 진로 계획 조회")
    void testFindByUserIdAndStatus() {
        // given
        careerPlanRepository.save(testPlan1);
        careerPlanRepository.save(testPlan2);
        careerPlanRepository.save(testPlan3);

        // when
        List<CareerPlan> plans = careerPlanRepository.findByUserIdAndStatus(TEST_USER_ID_1, "IN_PROGRESS");

        // then
        assertThat(plans).hasSize(2);
        assertThat(plans).allMatch(p -> p.getStatus().equals("IN_PROGRESS"));
    }

    @Test
    @Order(7)
    @DisplayName("사용자의 최신 진로 계획 조회")
    void testFindLatestByUserId() throws Exception {
        // given
        careerPlanRepository.save(testPlan1);
        Thread.sleep(100); // 생성 시간 차이를 위한 대기
        careerPlanRepository.save(testPlan2);

        // when
        Optional<CareerPlan> latest = careerPlanRepository.findLatestByUserId(TEST_USER_ID_1);

        // then
        assertThat(latest).isPresent();
        assertThat(latest.get().getTitle()).isEqualTo("데이터 과학자 진로 계획");
    }

    // ========== 상태별 조회 테스트 ==========

    @Test
    @Order(8)
    @DisplayName("상태별 진로 계획 조회")
    void testFindByStatus() {
        // given
        careerPlanRepository.save(testPlan1);
        careerPlanRepository.save(testPlan2);
        careerPlanRepository.save(testPlan3);

        // when
        List<CareerPlan> inProgress = careerPlanRepository.findByStatus("IN_PROGRESS");
        List<CareerPlan> completed = careerPlanRepository.findByStatus("COMPLETED");

        // then
        assertThat(inProgress).hasSize(2);
        assertThat(completed).hasSize(1);
    }

    @Test
    @Order(9)
    @DisplayName("진행 중인 진로 계획 목록 조회")
    void testFindAllInProgress() {
        // given
        careerPlanRepository.save(testPlan1);
        careerPlanRepository.save(testPlan2);
        careerPlanRepository.save(testPlan3);

        // when
        List<CareerPlan> plans = careerPlanRepository.findAllInProgress();

        // then
        assertThat(plans).hasSize(2);
        assertThat(plans).allMatch(p -> p.getStatus().equals("IN_PROGRESS"));
    }

    @Test
    @Order(10)
    @DisplayName("완료된 진로 계획 목록 조회")
    void testFindAllCompleted() {
        // given
        careerPlanRepository.save(testPlan1);
        careerPlanRepository.save(testPlan2);
        careerPlanRepository.save(testPlan3);

        // when
        List<CareerPlan> plans = careerPlanRepository.findAllCompleted();

        // then
        assertThat(plans).hasSize(1);
        assertThat(plans).allMatch(p -> p.getStatus().equals("COMPLETED"));
    }

    // ========== 목표 분야별 조회 테스트 ==========

    @Test
    @Order(11)
    @DisplayName("목표 분야로 진로 계획 검색")
    void testFindByTargetFieldContaining() {
        // given
        careerPlanRepository.save(testPlan1);
        careerPlanRepository.save(testPlan2);
        careerPlanRepository.save(testPlan3);

        // when
        List<CareerPlan> plans = careerPlanRepository.findByTargetFieldContaining("개발");

        // then
        assertThat(plans).hasSize(1);
        assertThat(plans.get(0).getTargetField()).contains("개발");
    }

    @Test
    @Order(12)
    @DisplayName("사용자 ID와 목표 분야로 진로 계획 조회")
    void testFindByUserIdAndTargetFieldContaining() {
        // given
        careerPlanRepository.save(testPlan1);
        careerPlanRepository.save(testPlan2);

        // when
        List<CareerPlan> plans = careerPlanRepository.findByUserIdAndTargetFieldContaining(TEST_USER_ID_1, "소프트웨어");

        // then
        assertThat(plans).hasSize(1);
        assertThat(plans.get(0).getTargetField()).contains("소프트웨어");
    }

    // ========== 날짜 관련 조회 테스트 ==========

    @Test
    @Order(13)
    @DisplayName("목표 날짜 이전의 미완료 진로 계획 조회")
    void testFindUncompletedBeforeDate() {
        // given
        CareerPlan pastPlan = CareerPlan.builder()
                .userId(TEST_USER_ID_1)
                .title("과거 진로 계획")
                .targetField("테스트")
                .targetDate(LocalDate.now().minusDays(1))
                .status("IN_PROGRESS")
                .build();
        careerPlanRepository.save(pastPlan);
        careerPlanRepository.save(testPlan1);

        // when
        List<CareerPlan> plans = careerPlanRepository.findUncompletedBeforeDate(LocalDate.now());

        // then
        assertThat(plans).hasSize(1);
        assertThat(plans.get(0).getTitle()).isEqualTo("과거 진로 계획");
    }

    @Test
    @Order(14)
    @DisplayName("특정 기간 내 목표 날짜를 가진 진로 계획 조회")
    void testFindByTargetDateBetween() {
        // given
        careerPlanRepository.save(testPlan1);
        careerPlanRepository.save(testPlan2);
        careerPlanRepository.save(testPlan3);

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusYears(2);

        // when
        List<CareerPlan> plans = careerPlanRepository.findByTargetDateBetween(startDate, endDate);

        // then
        assertThat(plans).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @Order(15)
    @DisplayName("사용자의 특정 기간 내 진로 계획 조회")
    void testFindByUserIdAndTargetDateBetween() {
        // given
        careerPlanRepository.save(testPlan1);
        careerPlanRepository.save(testPlan2);
        careerPlanRepository.save(testPlan3);

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusYears(3);

        // when
        List<CareerPlan> plans = careerPlanRepository.findByUserIdAndTargetDateBetween(
            TEST_USER_ID_1, startDate, endDate
        );

        // then
        assertThat(plans).hasSize(2);
        assertThat(plans).extracting("userId").containsOnly(TEST_USER_ID_1);
    }

    // ========== 검색 테스트 ==========

    @Test
    @Order(16)
    @DisplayName("제목으로 진로 계획 검색")
    void testFindByTitleContaining() {
        // given
        careerPlanRepository.save(testPlan1);
        careerPlanRepository.save(testPlan2);
        careerPlanRepository.save(testPlan3);

        // when
        List<CareerPlan> plans = careerPlanRepository.findByTitleContaining("개발자");

        // then
        assertThat(plans).hasSizeGreaterThanOrEqualTo(1);
        assertThat(plans).allMatch(p -> p.getTitle().contains("개발자"));
    }

    @Test
    @Order(17)
    @DisplayName("사용자 ID와 제목으로 진로 계획 검색")
    void testFindByUserIdAndTitleContaining() {
        // given
        careerPlanRepository.save(testPlan1);
        careerPlanRepository.save(testPlan2);

        // when
        List<CareerPlan> plans = careerPlanRepository.findByUserIdAndTitleContaining(TEST_USER_ID_1, "소프트웨어");

        // then
        assertThat(plans).hasSize(1);
        assertThat(plans.get(0).getTitle()).contains("소프트웨어");
    }

    // ========== 통계 테스트 ==========

    @Test
    @Order(18)
    @DisplayName("사용자의 진로 계획 개수 조회")
    void testCountByUserId() {
        // given
        careerPlanRepository.save(testPlan1);
        careerPlanRepository.save(testPlan2);
        careerPlanRepository.save(testPlan3);

        // when
        long count = careerPlanRepository.countByUserId(TEST_USER_ID_1);

        // then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @Order(19)
    @DisplayName("사용자의 상태별 진로 계획 개수 조회")
    void testCountByUserIdAndStatus() {
        // given
        careerPlanRepository.save(testPlan1);
        careerPlanRepository.save(testPlan2);

        // when
        long count = careerPlanRepository.countByUserIdAndStatus(TEST_USER_ID_1, "IN_PROGRESS");

        // then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @Order(20)
    @DisplayName("상태별 전체 진로 계획 개수 조회")
    void testCountByStatus() {
        // given
        careerPlanRepository.save(testPlan1);
        careerPlanRepository.save(testPlan2);
        careerPlanRepository.save(testPlan3);

        // when
        long inProgressCount = careerPlanRepository.countByStatus("IN_PROGRESS");
        long completedCount = careerPlanRepository.countByStatus("COMPLETED");

        // then
        assertThat(inProgressCount).isEqualTo(2);
        assertThat(completedCount).isEqualTo(1);
    }

    @Test
    @Order(21)
    @DisplayName("목표 분야별 진로 계획 개수 조회")
    void testCountByTargetField() {
        // given
        careerPlanRepository.save(testPlan1);
        careerPlanRepository.save(testPlan2);
        careerPlanRepository.save(testPlan3);

        // when
        long count = careerPlanRepository.countByTargetField("소프트웨어 개발");

        // then
        assertThat(count).isEqualTo(1);
    }

    // ========== 존재 여부 확인 테스트 ==========

    @Test
    @Order(22)
    @DisplayName("사용자의 특정 제목 진로 계획 존재 여부 확인")
    void testExistsByUserIdAndTitle() {
        // given
        careerPlanRepository.save(testPlan1);

        // when
        boolean exists = careerPlanRepository.existsByUserIdAndTitle(
            TEST_USER_ID_1, "소프트웨어 개발자 진로 계획"
        );
        boolean notExists = careerPlanRepository.existsByUserIdAndTitle(
            TEST_USER_ID_1, "존재하지 않는 계획"
        );

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}
