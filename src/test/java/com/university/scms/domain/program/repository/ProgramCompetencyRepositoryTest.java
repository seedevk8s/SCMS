package com.university.scms.domain.program.repository;

import com.university.scms.config.JpaConfig;
import com.university.scms.domain.program.entity.Program;
import com.university.scms.domain.program.entity.ProgramCompetency;
import com.university.scms.domain.program.entity.ProgramStatus;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ProgramCompetencyRepository 테스트
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
@Import(JpaConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProgramCompetencyRepositoryTest {

    @Autowired
    private ProgramCompetencyRepository programCompetencyRepository;

    @Autowired
    private ProgramRepository programRepository;

    private Program testProgram1;
    private Program testProgram2;
    private ProgramCompetency testMapping1;
    private ProgramCompetency testMapping2;
    private ProgramCompetency testMapping3;

    @BeforeEach
    void setUp() {
        // 테스트 프로그램 준비
        LocalDateTime now = LocalDateTime.now();

        testProgram1 = Program.builder()
                .title("AI 특강")
                .description("인공지능 기초 교육")
                .category("교육")
                .organizerId(1L)
                .capacity(30)
                .startDate(now.plusDays(7))
                .endDate(now.plusDays(8))
                .status(ProgramStatus.OPEN)
                .build();

        testProgram2 = Program.builder()
                .title("진로 특강")
                .description("진로 탐색 워크샵")
                .category("진로")
                .organizerId(2L)
                .capacity(50)
                .startDate(now.plusDays(14))
                .endDate(now.plusDays(15))
                .status(ProgramStatus.OPEN)
                .build();

        // 프로그램 저장
        testProgram1 = programRepository.save(testProgram1);
        testProgram2 = programRepository.save(testProgram2);

        // 테스트 매핑 준비
        testMapping1 = ProgramCompetency.builder()
                .program(testProgram1)
                .competencyId(1L)  // 문제해결역량
                .weight(3)
                .description("AI 문제 해결 능력 향상")
                .build();

        testMapping2 = ProgramCompetency.builder()
                .program(testProgram1)
                .competencyId(2L)  // 창의역량
                .weight(5)
                .description("창의적 사고력 증진")
                .build();

        testMapping3 = ProgramCompetency.builder()
                .program(testProgram2)
                .competencyId(3L)  // 진로개발역량
                .weight(4)
                .description("진로 탐색 및 개발")
                .build();
    }

    @AfterEach
    void tearDown() {
        programCompetencyRepository.deleteAll();
        programRepository.deleteAll();
    }

    // ========== 기본 CRUD 테스트 ==========

    @Test
    @Order(1)
    @DisplayName("프로그램-역량 매핑 저장")
    void saveProgramCompetency() {
        // when
        ProgramCompetency saved = programCompetencyRepository.save(testMapping1);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getProgram().getTitle()).isEqualTo("AI 특강");
        assertThat(saved.getCompetencyId()).isEqualTo(1L);
        assertThat(saved.getWeight()).isEqualTo(3);
    }

    @Test
    @Order(2)
    @DisplayName("ID로 매핑 조회")
    void findById() {
        // given
        ProgramCompetency saved = programCompetencyRepository.save(testMapping1);

        // when
        Optional<ProgramCompetency> found = programCompetencyRepository.findById(saved.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getCompetencyId()).isEqualTo(1L);
    }

    // ========== 프로그램 기반 조회 테스트 ==========

    @Test
    @Order(3)
    @DisplayName("프로그램으로 매핑 목록 조회")
    void findByProgram() {
        // given
        programCompetencyRepository.save(testMapping1);
        programCompetencyRepository.save(testMapping2);
        programCompetencyRepository.save(testMapping3);

        // when
        List<ProgramCompetency> mappings = programCompetencyRepository.findByProgram(testProgram1);

        // then
        assertThat(mappings).hasSize(2);
    }

    @Test
    @Order(4)
    @DisplayName("프로그램 ID로 매핑 목록 조회")
    void findByProgramId() {
        // given
        programCompetencyRepository.save(testMapping1);
        programCompetencyRepository.save(testMapping2);
        programCompetencyRepository.save(testMapping3);

        // when
        List<ProgramCompetency> mappings = programCompetencyRepository.findByProgramId(testProgram1.getId());

        // then
        assertThat(mappings).hasSize(2);
    }

    // ========== 역량 기반 조회 테스트 ==========

    @Test
    @Order(5)
    @DisplayName("역량 ID로 매핑 목록 조회")
    void findByCompetencyId() {
        // given
        programCompetencyRepository.save(testMapping1);
        programCompetencyRepository.save(testMapping2);
        programCompetencyRepository.save(testMapping3);

        // when
        List<ProgramCompetency> mappings = programCompetencyRepository.findByCompetencyId(1L);

        // then
        assertThat(mappings).hasSize(1);
        assertThat(mappings.get(0).getProgram().getTitle()).isEqualTo("AI 특강");
    }

    // ========== 복합 조건 조회 테스트 ==========

    @Test
    @Order(6)
    @DisplayName("프로그램 ID와 역량 ID로 매핑 조회")
    void findByProgramIdAndCompetencyId() {
        // given
        programCompetencyRepository.save(testMapping1);
        programCompetencyRepository.save(testMapping2);

        // when
        Optional<ProgramCompetency> found = programCompetencyRepository
                .findByProgramIdAndCompetencyId(testProgram1.getId(), 1L);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getWeight()).isEqualTo(3);
    }

    @Test
    @Order(7)
    @DisplayName("프로그램 ID와 역량 ID로 매핑 존재 여부 확인")
    void existsByProgramIdAndCompetencyId() {
        // given
        programCompetencyRepository.save(testMapping1);

        // when & then
        assertThat(programCompetencyRepository
                .existsByProgramIdAndCompetencyId(testProgram1.getId(), 1L)).isTrue();
        assertThat(programCompetencyRepository
                .existsByProgramIdAndCompetencyId(testProgram1.getId(), 999L)).isFalse();
    }

    // ========== 가중치 기반 조회 테스트 ==========

    @Test
    @Order(8)
    @DisplayName("프로그램 ID로 가중치 내림차순 정렬 조회")
    void findByProgramIdOrderByWeightDesc() {
        // given
        programCompetencyRepository.save(testMapping1);  // weight: 3
        programCompetencyRepository.save(testMapping2);  // weight: 5

        // when
        List<ProgramCompetency> mappings = programCompetencyRepository
                .findByProgramIdOrderByWeightDesc(testProgram1.getId());

        // then
        assertThat(mappings).hasSize(2);
        assertThat(mappings.get(0).getWeight()).isEqualTo(5);
        assertThat(mappings.get(1).getWeight()).isEqualTo(3);
    }

    @Test
    @Order(9)
    @DisplayName("역량 ID로 가중치 내림차순 정렬 조회")
    void findByCompetencyIdOrderByWeightDesc() {
        // given
        ProgramCompetency mapping4 = ProgramCompetency.builder()
                .program(testProgram2)
                .competencyId(1L)
                .weight(2)
                .description("다른 프로그램의 문제해결역량")
                .build();
        
        programCompetencyRepository.save(testMapping1);  // competencyId: 1, weight: 3
        programCompetencyRepository.save(mapping4);      // competencyId: 1, weight: 2

        // when
        List<ProgramCompetency> mappings = programCompetencyRepository
                .findByCompetencyIdOrderByWeightDesc(1L);

        // then
        assertThat(mappings).hasSize(2);
        assertThat(mappings.get(0).getWeight()).isEqualTo(3);
        assertThat(mappings.get(1).getWeight()).isEqualTo(2);
    }

    @Test
    @Order(10)
    @DisplayName("최소 가중치 이상인 매핑 조회")
    void findByProgramIdAndWeightGreaterThanEqual() {
        // given
        programCompetencyRepository.save(testMapping1);  // weight: 3
        programCompetencyRepository.save(testMapping2);  // weight: 5

        // when
        List<ProgramCompetency> mappings = programCompetencyRepository
                .findByProgramIdAndWeightGreaterThanEqual(testProgram1.getId(), 4);

        // then
        assertThat(mappings).hasSize(1);
        assertThat(mappings.get(0).getWeight()).isEqualTo(5);
    }

    // ========== 통계 조회 테스트 ==========

    @Test
    @Order(11)
    @DisplayName("프로그램 ID로 매핑 개수 조회")
    void countByProgramId() {
        // given
        programCompetencyRepository.save(testMapping1);
        programCompetencyRepository.save(testMapping2);
        programCompetencyRepository.save(testMapping3);

        // when
        Long count = programCompetencyRepository.countByProgramId(testProgram1.getId());

        // then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @Order(12)
    @DisplayName("역량 ID로 매핑 개수 조회")
    void countByCompetencyId() {
        // given
        programCompetencyRepository.save(testMapping1);
        programCompetencyRepository.save(testMapping2);
        programCompetencyRepository.save(testMapping3);

        // when
        Long count = programCompetencyRepository.countByCompetencyId(1L);

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    @Order(13)
    @DisplayName("프로그램별 총 가중치 합계")
    void sumWeightByProgramId() {
        // given
        programCompetencyRepository.save(testMapping1);  // weight: 3
        programCompetencyRepository.save(testMapping2);  // weight: 5

        // when
        Long totalWeight = programCompetencyRepository.sumWeightByProgramId(testProgram1.getId());

        // then
        assertThat(totalWeight).isEqualTo(8);
    }

    // ========== 삭제 테스트 ==========

    @Test
    @Order(14)
    @DisplayName("프로그램으로 모든 매핑 삭제")
    void deleteByProgram() {
        // given
        programCompetencyRepository.save(testMapping1);
        programCompetencyRepository.save(testMapping2);
        programCompetencyRepository.save(testMapping3);

        // when
        programCompetencyRepository.deleteByProgram(testProgram1);
        programCompetencyRepository.flush();

        // then
        List<ProgramCompetency> remaining = programCompetencyRepository
                .findByProgramId(testProgram1.getId());
        assertThat(remaining).isEmpty();
    }

    @Test
    @Order(15)
    @DisplayName("역량 ID로 모든 매핑 삭제")
    void deleteByCompetencyId() {
        // given
        programCompetencyRepository.save(testMapping1);
        programCompetencyRepository.save(testMapping2);
        programCompetencyRepository.save(testMapping3);

        // when
        programCompetencyRepository.deleteByCompetencyId(1L);
        programCompetencyRepository.flush();

        // then
        List<ProgramCompetency> remaining = programCompetencyRepository.findByCompetencyId(1L);
        assertThat(remaining).isEmpty();
    }

    // ========== Factory 메서드 테스트 ==========

    @Test
    @Order(16)
    @DisplayName("Factory 메서드로 매핑 생성")
    void createWithFactory() {
        // when
        ProgramCompetency created = ProgramCompetency.create(
                testProgram1, 4L, 3, "의사소통역량 향상"
        );
        ProgramCompetency saved = programCompetencyRepository.save(created);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCompetencyId()).isEqualTo(4L);
        assertThat(saved.getWeight()).isEqualTo(3);
        assertThat(saved.getDescription()).isEqualTo("의사소통역량 향상");
    }

    // ========== 비즈니스 메서드 테스트 ==========

    @Test
    @Order(17)
    @DisplayName("가중치 업데이트")
    void updateWeight() {
        // given
        ProgramCompetency saved = programCompetencyRepository.save(testMapping1);

        // when
        saved.updateWeight(7);
        programCompetencyRepository.flush();

        // then
        ProgramCompetency updated = programCompetencyRepository.findById(saved.getId()).get();
        assertThat(updated.getWeight()).isEqualTo(7);
    }

    @Test
    @Order(18)
    @DisplayName("설명 업데이트")
    void updateDescription() {
        // given
        ProgramCompetency saved = programCompetencyRepository.save(testMapping1);

        // when
        saved.updateDescription("새로운 설명");
        programCompetencyRepository.flush();

        // then
        ProgramCompetency updated = programCompetencyRepository.findById(saved.getId()).get();
        assertThat(updated.getDescription()).isEqualTo("새로운 설명");
    }
}
