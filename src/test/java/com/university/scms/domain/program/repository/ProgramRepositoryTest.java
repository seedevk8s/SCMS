package com.university.scms.domain.program.repository;

import com.university.scms.config.JpaConfig;
import com.university.scms.domain.program.entity.Program;
import com.university.scms.domain.program.entity.ProgramStatus;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ProgramRepository 테스트
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
@Import(JpaConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProgramRepositoryTest {

    @Autowired
    private ProgramRepository programRepository;

    private Program testProgram1;
    private Program testProgram2;
    private Program testProgram3;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        LocalDateTime now = LocalDateTime.now();

        testProgram1 = Program.builder()
                .title("AI 특강")
                .description("인공지능 기초 교육")
                .category("교육")
                .organizerId(1L)
                .location("공학관 101호")
                .capacity(30)
                .currentParticipants(15)
                .startDate(now.plusDays(7))
                .endDate(now.plusDays(8))
                .applicationStart(now.minusDays(1))
                .applicationEnd(now.plusDays(5))
                .mileagePoints(10)
                .status(ProgramStatus.OPEN)
                .build();

        testProgram2 = Program.builder()
                .title("진로 특강")
                .description("진로 탐색 워크샵")
                .category("진로")
                .organizerId(2L)
                .location("학생회관 202호")
                .capacity(50)
                .currentParticipants(50)
                .startDate(now.plusDays(14))
                .endDate(now.plusDays(15))
                .applicationStart(now.minusDays(7))
                .applicationEnd(now.plusDays(10))
                .mileagePoints(15)
                .status(ProgramStatus.OPEN)
                .build();

        testProgram3 = Program.builder()
                .title("봉사활동")
                .description("지역사회 봉사활동")
                .category("봉사")
                .organizerId(1L)
                .location("외부")
                .capacity(20)
                .currentParticipants(10)
                .startDate(now.minusDays(1))
                .endDate(now.minusDays(1).plusHours(3))
                .applicationStart(now.minusDays(14))
                .applicationEnd(now.minusDays(2))
                .mileagePoints(20)
                .status(ProgramStatus.COMPLETED)
                .build();
    }

    @AfterEach
    void tearDown() {
        programRepository.deleteAll();
    }

    // ========== 기본 CRUD 테스트 ==========

    @Test
    @Order(1)
    @DisplayName("프로그램 저장")
    void saveProgram() {
        // when
        Program saved = programRepository.save(testProgram1);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("AI 특강");
        assertThat(saved.getOrganizerId()).isEqualTo(1L);
    }

    @Test
    @Order(2)
    @DisplayName("ID로 프로그램 조회")
    void findById() {
        // given
        Program saved = programRepository.save(testProgram1);

        // when
        Optional<Program> found = programRepository.findById(saved.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("AI 특강");
    }

    @Test
    @Order(3)
    @DisplayName("제목으로 프로그램 조회")
    void findByTitle() {
        // given
        programRepository.save(testProgram1);

        // when
        Optional<Program> found = programRepository.findByTitle("AI 특강");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getDescription()).isEqualTo("인공지능 기초 교육");
    }

    @Test
    @Order(4)
    @DisplayName("제목 존재 여부 확인")
    void existsByTitle() {
        // given
        programRepository.save(testProgram1);

        // when & then
        assertThat(programRepository.existsByTitle("AI 특강")).isTrue();
        assertThat(programRepository.existsByTitle("없는 프로그램")).isFalse();
    }

    // ========== 상태 조회 테스트 ==========

    @Test
    @Order(5)
    @DisplayName("상태로 프로그램 조회")
    void findByStatus() {
        // given
        programRepository.save(testProgram1);
        programRepository.save(testProgram2);
        programRepository.save(testProgram3);

        // when
        List<Program> openPrograms = programRepository.findByStatus(ProgramStatus.OPEN);
        List<Program> completedPrograms = programRepository.findByStatus(ProgramStatus.COMPLETED);

        // then
        assertThat(openPrograms).hasSize(2);
        assertThat(completedPrograms).hasSize(1);
    }

    @Test
    @Order(6)
    @DisplayName("상태로 프로그램 조회 (페이징)")
    void findByStatusWithPaging() {
        // given
        programRepository.save(testProgram1);
        programRepository.save(testProgram2);

        // when
        Pageable pageable = PageRequest.of(0, 10);
        Page<Program> page = programRepository.findByStatus(ProgramStatus.OPEN, pageable);

        // then
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(2);
    }

    // ========== 카테고리 조회 테스트 ==========

    @Test
    @Order(7)
    @DisplayName("카테고리로 프로그램 조회")
    void findByCategory() {
        // given
        programRepository.save(testProgram1);
        programRepository.save(testProgram2);
        programRepository.save(testProgram3);

        // when
        List<Program> educationPrograms = programRepository.findByCategory("교육");
        List<Program> careerPrograms = programRepository.findByCategory("진로");

        // then
        assertThat(educationPrograms).hasSize(1);
        assertThat(careerPrograms).hasSize(1);
    }

    // ========== 담당자 조회 테스트 ==========

    @Test
    @Order(8)
    @DisplayName("담당자 ID로 프로그램 조회")
    void findByOrganizerId() {
        // given
        programRepository.save(testProgram1);
        programRepository.save(testProgram2);
        programRepository.save(testProgram3);

        // when
        List<Program> organizer1Programs = programRepository.findByOrganizerId(1L);
        List<Program> organizer2Programs = programRepository.findByOrganizerId(2L);

        // then
        assertThat(organizer1Programs).hasSize(2);
        assertThat(organizer2Programs).hasSize(1);
    }

    // ========== 날짜 조회 테스트 ==========

    @Test
    @Order(9)
    @DisplayName("특정 기간 내 시작하는 프로그램 조회")
    void findByStartDateBetween() {
        // given
        programRepository.save(testProgram1);
        programRepository.save(testProgram2);
        programRepository.save(testProgram3);

        // when
        LocalDateTime start = LocalDateTime.now().plusDays(5);
        LocalDateTime end = LocalDateTime.now().plusDays(10);
        List<Program> programs = programRepository.findByStartDateBetween(start, end);

        // then
        assertThat(programs).hasSize(1);
        assertThat(programs.get(0).getTitle()).isEqualTo("AI 특강");
    }

    @Test
    @Order(10)
    @DisplayName("특정 날짜 이후 시작하는 프로그램 조회")
    void findByStartDateAfter() {
        // given
        programRepository.save(testProgram1);
        programRepository.save(testProgram2);
        programRepository.save(testProgram3);

        // when
        LocalDateTime threshold = LocalDateTime.now();
        List<Program> programs = programRepository.findByStartDateAfter(threshold);

        // then
        assertThat(programs).hasSize(2);
    }

    // ========== 신청 가능 프로그램 조회 테스트 ==========

    @Test
    @Order(11)
    @DisplayName("신청 가능한 프로그램 조회")
    void findAvailablePrograms() {
        // given
        programRepository.save(testProgram1);
        programRepository.save(testProgram2); // 정원 초과
        programRepository.save(testProgram3); // 완료

        // when
        List<Program> available = programRepository.findAvailablePrograms(LocalDateTime.now());

        // then
        assertThat(available).hasSize(1);
        assertThat(available.get(0).getTitle()).isEqualTo("AI 특강");
    }

    @Test
    @Order(12)
    @DisplayName("신청 기간 중인 프로그램 조회")
    void findApplicationOpenPrograms() {
        // given
        programRepository.save(testProgram1);
        programRepository.save(testProgram2);
        programRepository.save(testProgram3);

        // when
        List<Program> programs = programRepository.findApplicationOpenPrograms(LocalDateTime.now());

        // then
        assertThat(programs).hasSize(2);
    }

    // ========== 진행 상태 조회 테스트 ==========

    @Test
    @Order(13)
    @DisplayName("진행 중인 프로그램 조회")
    void findInProgressPrograms() {
        // given
        Program inProgress = Program.builder()
                .title("진행 중 프로그램")
                .description("현재 진행 중")
                .category("교육")
                .organizerId(1L)
                .capacity(30)
                .currentParticipants(15)
                .startDate(LocalDateTime.now().minusHours(1))
                .endDate(LocalDateTime.now().plusHours(1))
                .status(ProgramStatus.OPEN)
                .build();
        programRepository.save(inProgress);

        // when
        List<Program> programs = programRepository.findInProgressPrograms(LocalDateTime.now());

        // then
        assertThat(programs).hasSize(1);
    }

    @Test
    @Order(14)
    @DisplayName("완료된 프로그램 조회")
    void findCompletedPrograms() {
        // given
        programRepository.save(testProgram1);
        programRepository.save(testProgram3);

        // when
        List<Program> completed = programRepository.findCompletedPrograms(LocalDateTime.now());

        // then
        assertThat(completed).hasSize(1);
        assertThat(completed.get(0).getTitle()).isEqualTo("봉사활동");
    }

    // ========== 검색 테스트 ==========

    @Test
    @Order(15)
    @DisplayName("제목으로 프로그램 검색 (포함)")
    void findByTitleContaining() {
        // given
        programRepository.save(testProgram1);
        programRepository.save(testProgram2);

        // when
        List<Program> programs = programRepository.findByTitleContaining("특강");

        // then
        assertThat(programs).hasSize(2);
    }

    @Test
    @Order(16)
    @DisplayName("키워드로 프로그램 검색 (제목 또는 설명)")
    void searchByKeyword() {
        // given
        programRepository.save(testProgram1);
        programRepository.save(testProgram2);
        programRepository.save(testProgram3);

        // when
        List<Program> programs = programRepository.searchByKeyword("워크샵");

        // then
        assertThat(programs).hasSize(1);
        assertThat(programs.get(0).getTitle()).isEqualTo("진로 특강");
    }

    // ========== 복합 조건 조회 테스트 ==========

    @Test
    @Order(17)
    @DisplayName("카테고리와 상태로 프로그램 조회")
    void findByCategoryAndStatus() {
        // given
        programRepository.save(testProgram1);
        programRepository.save(testProgram3);

        // when
        List<Program> programs = programRepository.findByCategoryAndStatus("봉사", ProgramStatus.COMPLETED);

        // then
        assertThat(programs).hasSize(1);
        assertThat(programs.get(0).getTitle()).isEqualTo("봉사활동");
    }

    @Test
    @Order(18)
    @DisplayName("담당자와 상태로 프로그램 조회")
    void findByOrganizerIdAndStatus() {
        // given
        programRepository.save(testProgram1);
        programRepository.save(testProgram3);

        // when
        List<Program> programs = programRepository.findByOrganizerIdAndStatus(1L, ProgramStatus.OPEN);

        // then
        assertThat(programs).hasSize(1);
        assertThat(programs.get(0).getTitle()).isEqualTo("AI 특강");
    }

    @Test
    @Order(19)
    @DisplayName("정원이 찬 프로그램 조회")
    void findFullPrograms() {
        // given
        programRepository.save(testProgram1);
        programRepository.save(testProgram2);

        // when
        List<Program> fullPrograms = programRepository.findFullPrograms();

        // then
        assertThat(fullPrograms).hasSize(1);
        assertThat(fullPrograms.get(0).getTitle()).isEqualTo("진로 특강");
    }

    @Test
    @Order(20)
    @DisplayName("정원이 남은 프로그램 조회")
    void findAvailableSpacePrograms() {
        // given
        programRepository.save(testProgram1);
        programRepository.save(testProgram2);
        programRepository.save(testProgram3);

        // when
        List<Program> availablePrograms = programRepository.findAvailableSpacePrograms();

        // then
        assertThat(availablePrograms).hasSize(2);
    }

    // ========== 통계 테스트 ==========

    @Test
    @Order(21)
    @DisplayName("담당자별 프로그램 개수 조회")
    void countByOrganizerId() {
        // given
        programRepository.save(testProgram1);
        programRepository.save(testProgram2);
        programRepository.save(testProgram3);

        // when
        Long count = programRepository.countByOrganizerId(1L);

        // then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @Order(22)
    @DisplayName("상태별 프로그램 개수 조회")
    void countByStatus() {
        // given
        programRepository.save(testProgram1);
        programRepository.save(testProgram2);
        programRepository.save(testProgram3);

        // when
        Long openCount = programRepository.countByStatus(ProgramStatus.OPEN);
        Long completedCount = programRepository.countByStatus(ProgramStatus.COMPLETED);

        // then
        assertThat(openCount).isEqualTo(2);
        assertThat(completedCount).isEqualTo(1);
    }

    @Test
    @Order(23)
    @DisplayName("카테고리별 프로그램 개수 조회")
    void countByCategory() {
        // given
        programRepository.save(testProgram1);
        programRepository.save(testProgram2);
        programRepository.save(testProgram3);

        // when
        Long educationCount = programRepository.countByCategory("교육");
        Long careerCount = programRepository.countByCategory("진로");

        // then
        assertThat(educationCount).isEqualTo(1);
        assertThat(careerCount).isEqualTo(1);
    }
}
