package com.university.scms.domain.program.repository;

import com.university.scms.config.JpaConfig;
import com.university.scms.domain.program.entity.*;
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
 * ProgramSatisfactionRepository 테스트
 * 실제 MySQL 데이터베이스를 사용하여 테스트합니다.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(JpaConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProgramSatisfactionRepositoryTest {

    @Autowired
    private ProgramSatisfactionRepository satisfactionRepository;

    @Autowired
    private ProgramRepository programRepository;

    private Program testProgram;
    private ProgramSatisfaction highSatisfaction;
    private ProgramSatisfaction lowSatisfaction;
    private ProgramSatisfaction mediumSatisfaction;

    @BeforeEach
    void setUp() {
        // 테스트용 프로그램 생성
        testProgram = Program.builder()
                .title("머신러닝 기초 워크샵")
                .description("머신러닝 개념과 실습")
                .category("인공지능")
                .organizerId(100L)
                .location("AI센터 501호")
                .capacity(30)
                .startDate(LocalDateTime.now().minusDays(10))
                .endDate(LocalDateTime.now().minusDays(3))
                .mileagePoints(60)
                .status(ProgramStatus.COMPLETED)
                .build();
        testProgram = programRepository.save(testProgram);

        // 높은 만족도 응답
        highSatisfaction = ProgramSatisfaction.builder()
                .program(testProgram)
                .userId(3001L)
                .overallRating(5)
                .contentRating(5)
                .instructorRating(5)
                .facilityRating(4)
                .usefulnessRating(5)
                .strengths("강사님의 설명이 명확하고 실습 예제가 좋았습니다.")
                .improvements("시간이 조금 더 길었으면 좋겠습니다.")
                .wouldRecommend(true)
                .build();

        // 낮은 만족도 응답
        lowSatisfaction = ProgramSatisfaction.builder()
                .program(testProgram)
                .userId(3002L)
                .overallRating(2)
                .contentRating(2)
                .instructorRating(3)
                .facilityRating(2)
                .usefulnessRating(2)
                .strengths("")
                .improvements("내용이 너무 어려웠고 진도가 빨랐습니다.")
                .wouldRecommend(false)
                .build();

        // 중간 만족도 응답
        mediumSatisfaction = ProgramSatisfaction.builder()
                .program(testProgram)
                .userId(3003L)
                .overallRating(3)
                .contentRating(4)
                .instructorRating(3)
                .facilityRating(3)
                .usefulnessRating(4)
                .strengths("실습이 많아서 좋았습니다.")
                .improvements("이론 설명이 더 필요합니다.")
                .wouldRecommend(true)
                .build();
    }

    @AfterEach
    void tearDown() {
        satisfactionRepository.deleteAll();
        programRepository.deleteAll();
    }

    // ========== 기본 CRUD 테스트 ==========

    @Test
    @Order(1)
    @DisplayName("만족도 생성 테스트")
    void testCreateSatisfaction() {
        // when
        ProgramSatisfaction saved = satisfactionRepository.save(highSatisfaction);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getProgram().getTitle()).isEqualTo("머신러닝 기초 워크샵");
        assertThat(saved.getUserId()).isEqualTo(3001L);
        assertThat(saved.getOverallRating()).isEqualTo(5);
    }

    @Test
    @Order(2)
    @DisplayName("만족도 조회 테스트")
    void testFindSatisfaction() {
        // given
        ProgramSatisfaction saved = satisfactionRepository.save(highSatisfaction);

        // when
        Optional<ProgramSatisfaction> found = satisfactionRepository.findById(saved.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo(3001L);
    }

    @Test
    @Order(3)
    @DisplayName("만족도 수정 테스트")
    void testUpdateSatisfaction() {
        // given
        ProgramSatisfaction saved = satisfactionRepository.save(highSatisfaction);

        // when
        saved.update(4, 4, 5, 4, 5, "수정된 장점", "수정된 개선사항", false);
        ProgramSatisfaction updated = satisfactionRepository.save(saved);

        // then
        assertThat(updated.getOverallRating()).isEqualTo(4);
        assertThat(updated.getStrengths()).isEqualTo("수정된 장점");
    }

    @Test
    @Order(4)
    @DisplayName("만족도 삭제 테스트")
    void testDeleteSatisfaction() {
        // given
        ProgramSatisfaction saved = satisfactionRepository.save(highSatisfaction);
        Long id = saved.getId();

        // when
        satisfactionRepository.delete(saved);

        // then
        assertThat(satisfactionRepository.findById(id)).isEmpty();
    }

    // ========== 기본 조회 메서드 테스트 ==========

    @Test
    @Order(5)
    @DisplayName("프로그램과 사용자로 만족도 조회")
    void testFindByProgramAndUserId() {
        // given
        satisfactionRepository.save(highSatisfaction);

        // when
        Optional<ProgramSatisfaction> found = 
                satisfactionRepository.findByProgramAndUserId(testProgram, 3001L);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getWouldRecommend()).isTrue();
    }

    @Test
    @Order(6)
    @DisplayName("프로그램 ID와 사용자 ID로 만족도 조회")
    void testFindByProgramIdAndUserId() {
        // given
        satisfactionRepository.save(highSatisfaction);

        // when
        Optional<ProgramSatisfaction> found = 
                satisfactionRepository.findByProgramIdAndUserId(testProgram.getId(), 3001L);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo(3001L);
    }

    // ========== 프로그램별 만족도 조회 테스트 ==========

    @Test
    @Order(7)
    @DisplayName("프로그램의 모든 만족도 조회")
    void testFindByProgram() {
        // given
        satisfactionRepository.save(highSatisfaction);
        satisfactionRepository.save(lowSatisfaction);
        satisfactionRepository.save(mediumSatisfaction);

        // when
        List<ProgramSatisfaction> satisfactions = 
                satisfactionRepository.findByProgram(testProgram);

        // then
        assertThat(satisfactions).hasSize(3);
    }

    @Test
    @Order(8)
    @DisplayName("프로그램 ID로 만족도 목록 조회")
    void testFindByProgramId() {
        // given
        satisfactionRepository.save(highSatisfaction);
        satisfactionRepository.save(lowSatisfaction);

        // when
        List<ProgramSatisfaction> satisfactions = 
                satisfactionRepository.findByProgramId(testProgram.getId());

        // then
        assertThat(satisfactions).hasSize(2);
    }

    @Test
    @Order(9)
    @DisplayName("프로그램의 만족도를 전반적 평점 기준 내림차순 정렬 조회")
    void testFindByProgramIdOrderByOverallRatingDesc() {
        // given
        satisfactionRepository.save(highSatisfaction);
        satisfactionRepository.save(lowSatisfaction);
        satisfactionRepository.save(mediumSatisfaction);

        // when
        List<ProgramSatisfaction> satisfactions = 
                satisfactionRepository.findByProgramIdOrderByOverallRatingDesc(testProgram.getId());

        // then
        assertThat(satisfactions).hasSize(3);
        assertThat(satisfactions.get(0).getOverallRating()).isEqualTo(5);
        assertThat(satisfactions.get(2).getOverallRating()).isEqualTo(2);
    }

    // ========== 사용자별 만족도 조회 테스트 ==========

    @Test
    @Order(10)
    @DisplayName("사용자의 모든 만족도 조사 응답 조회")
    void testFindByUserId() {
        // given
        satisfactionRepository.save(highSatisfaction);

        // when
        List<ProgramSatisfaction> satisfactions = 
                satisfactionRepository.findByUserId(3001L);

        // then
        assertThat(satisfactions).hasSize(1);
        assertThat(satisfactions.get(0).getOverallRating()).isEqualTo(5);
    }

    // ========== 평점별 조회 테스트 ==========

    @Test
    @Order(11)
    @DisplayName("특정 전반적 평점의 만족도 조회")
    void testFindByOverallRating() {
        // given
        satisfactionRepository.save(highSatisfaction);
        satisfactionRepository.save(lowSatisfaction);

        // when
        List<ProgramSatisfaction> fiveStars = 
                satisfactionRepository.findByOverallRating(5);

        // then
        assertThat(fiveStars).hasSize(1);
        assertThat(fiveStars.get(0).getUserId()).isEqualTo(3001L);
    }

    @Test
    @Order(12)
    @DisplayName("전반적 평점이 특정 값 이상인 만족도 조회")
    void testFindByOverallRatingGreaterThanEqual() {
        // given
        satisfactionRepository.save(highSatisfaction);
        satisfactionRepository.save(mediumSatisfaction);
        satisfactionRepository.save(lowSatisfaction);

        // when
        List<ProgramSatisfaction> highRated = 
                satisfactionRepository.findByOverallRatingGreaterThanEqual(4);

        // then
        assertThat(highRated).hasSize(1);
    }

    @Test
    @Order(13)
    @DisplayName("전반적 평점이 특정 값 이하인 만족도 조회")
    void testFindByOverallRatingLessThanEqual() {
        // given
        satisfactionRepository.save(highSatisfaction);
        satisfactionRepository.save(lowSatisfaction);

        // when
        List<ProgramSatisfaction> lowRated = 
                satisfactionRepository.findByOverallRatingLessThanEqual(3);

        // then
        assertThat(lowRated).hasSize(1);
    }

    @Test
    @Order(14)
    @DisplayName("높은 만족도 응답 조회 (4점 이상)")
    void testFindHighSatisfactionResponses() {
        // given
        satisfactionRepository.save(highSatisfaction);
        satisfactionRepository.save(lowSatisfaction);

        // when
        List<ProgramSatisfaction> highSatisfactions = 
                satisfactionRepository.findHighSatisfactionResponses();

        // then
        assertThat(highSatisfactions).hasSize(1);
        assertThat(highSatisfactions.get(0).isHighSatisfaction()).isTrue();
    }

    @Test
    @Order(15)
    @DisplayName("낮은 만족도 응답 조회 (2점 이하)")
    void testFindLowSatisfactionResponses() {
        // given
        satisfactionRepository.save(highSatisfaction);
        satisfactionRepository.save(lowSatisfaction);

        // when
        List<ProgramSatisfaction> lowSatisfactions = 
                satisfactionRepository.findLowSatisfactionResponses();

        // then
        assertThat(lowSatisfactions).hasSize(1);
        assertThat(lowSatisfactions.get(0).isLowSatisfaction()).isTrue();
    }

    // ========== 프로그램별 평점 조회 테스트 ==========

    @Test
    @Order(16)
    @DisplayName("프로그램의 높은 만족도 응답 조회")
    void testFindHighSatisfactionResponsesByProgramId() {
        // given
        satisfactionRepository.save(highSatisfaction);
        satisfactionRepository.save(lowSatisfaction);

        // when
        List<ProgramSatisfaction> highSatisfactions = 
                satisfactionRepository.findHighSatisfactionResponsesByProgramId(testProgram.getId());

        // then
        assertThat(highSatisfactions).hasSize(1);
    }

    @Test
    @Order(17)
    @DisplayName("프로그램의 낮은 만족도 응답 조회")
    void testFindLowSatisfactionResponsesByProgramId() {
        // given
        satisfactionRepository.save(highSatisfaction);
        satisfactionRepository.save(lowSatisfaction);

        // when
        List<ProgramSatisfaction> lowSatisfactions = 
                satisfactionRepository.findLowSatisfactionResponsesByProgramId(testProgram.getId());

        // then
        assertThat(lowSatisfactions).hasSize(1);
    }

    // ========== 주관식 응답 조회 테스트 ==========

    @Test
    @Order(18)
    @DisplayName("장점 피드백이 있는 만족도 조회")
    void testFindWithStrengths() {
        // given
        satisfactionRepository.save(highSatisfaction);
        satisfactionRepository.save(mediumSatisfaction);
        satisfactionRepository.save(lowSatisfaction);

        // when
        List<ProgramSatisfaction> withStrengths = 
                satisfactionRepository.findWithStrengths();

        // then
        assertThat(withStrengths).hasSize(2);
    }

    @Test
    @Order(19)
    @DisplayName("개선사항 피드백이 있는 만족도 조회")
    void testFindWithImprovements() {
        // given
        satisfactionRepository.save(highSatisfaction);
        satisfactionRepository.save(lowSatisfaction);

        // when
        List<ProgramSatisfaction> withImprovements = 
                satisfactionRepository.findWithImprovements();

        // then
        assertThat(withImprovements).hasSize(2);
    }

    @Test
    @Order(20)
    @DisplayName("프로그램의 장점 피드백이 있는 만족도 조회")
    void testFindWithStrengthsByProgramId() {
        // given
        satisfactionRepository.save(highSatisfaction);
        satisfactionRepository.save(mediumSatisfaction);

        // when
        List<ProgramSatisfaction> withStrengths = 
                satisfactionRepository.findWithStrengthsByProgramId(testProgram.getId());

        // then
        assertThat(withStrengths).hasSize(2);
    }

    @Test
    @Order(21)
    @DisplayName("프로그램의 개선사항 피드백이 있는 만족도 조회")
    void testFindWithImprovementsByProgramId() {
        // given
        satisfactionRepository.save(highSatisfaction);
        satisfactionRepository.save(lowSatisfaction);
        satisfactionRepository.save(mediumSatisfaction);

        // when
        List<ProgramSatisfaction> withImprovements = 
                satisfactionRepository.findWithImprovementsByProgramId(testProgram.getId());

        // then
        assertThat(withImprovements).hasSize(3);
    }

    // ========== 추천 의향 조회 테스트 ==========

    @Test
    @Order(22)
    @DisplayName("추천 의향이 있는 만족도 조회")
    void testFindByWouldRecommend() {
        // given
        satisfactionRepository.save(highSatisfaction);
        satisfactionRepository.save(lowSatisfaction);
        satisfactionRepository.save(mediumSatisfaction);

        // when
        List<ProgramSatisfaction> wouldRecommend = 
                satisfactionRepository.findByWouldRecommend(true);

        // then
        assertThat(wouldRecommend).hasSize(2);
    }

    @Test
    @Order(23)
    @DisplayName("프로그램의 추천 의향이 있는 만족도 조회")
    void testFindRecommendedByProgramId() {
        // given
        satisfactionRepository.save(highSatisfaction);
        satisfactionRepository.save(mediumSatisfaction);

        // when
        List<ProgramSatisfaction> recommended = 
                satisfactionRepository.findRecommendedByProgramId(testProgram.getId());

        // then
        assertThat(recommended).hasSize(2);
    }

    @Test
    @Order(24)
    @DisplayName("프로그램의 추천 의향이 없는 만족도 조회")
    void testFindNotRecommendedByProgramId() {
        // given
        satisfactionRepository.save(lowSatisfaction);

        // when
        List<ProgramSatisfaction> notRecommended = 
                satisfactionRepository.findNotRecommendedByProgramId(testProgram.getId());

        // then
        assertThat(notRecommended).hasSize(1);
    }

    // ========== 통계 및 집계 메서드 테스트 ==========

    @Test
    @Order(25)
    @DisplayName("프로그램의 만족도 응답 수 조회")
    void testCountByProgram() {
        // given
        satisfactionRepository.save(highSatisfaction);
        satisfactionRepository.save(lowSatisfaction);

        // when
        long count = satisfactionRepository.countByProgram(testProgram);

        // then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @Order(26)
    @DisplayName("프로그램 ID로 만족도 응답 수 조회")
    void testCountByProgramId() {
        // given
        satisfactionRepository.save(highSatisfaction);

        // when
        long count = satisfactionRepository.countByProgramId(testProgram.getId());

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    @Order(27)
    @DisplayName("프로그램의 전반적 평균 평점 조회")
    void testGetAverageOverallRatingByProgramId() {
        // given
        satisfactionRepository.save(highSatisfaction);  // 5점
        satisfactionRepository.save(mediumSatisfaction);  // 3점

        // when
        Double avgRating = satisfactionRepository.getAverageOverallRatingByProgramId(testProgram.getId());

        // then
        assertThat(avgRating).isEqualTo(4.0);
    }

    @Test
    @Order(28)
    @DisplayName("프로그램의 내용 평균 평점 조회")
    void testGetAverageContentRatingByProgramId() {
        // given
        satisfactionRepository.save(highSatisfaction);  // 5점
        satisfactionRepository.save(mediumSatisfaction);  // 4점

        // when
        Double avgRating = satisfactionRepository.getAverageContentRatingByProgramId(testProgram.getId());

        // then
        assertThat(avgRating).isEqualTo(4.5);
    }

    @Test
    @Order(29)
    @DisplayName("프로그램의 강사 평균 평점 조회")
    void testGetAverageInstructorRatingByProgramId() {
        // given
        satisfactionRepository.save(highSatisfaction);  // 5점

        // when
        Double avgRating = satisfactionRepository.getAverageInstructorRatingByProgramId(testProgram.getId());

        // then
        assertThat(avgRating).isEqualTo(5.0);
    }

    @Test
    @Order(30)
    @DisplayName("프로그램의 시설 평균 평점 조회")
    void testGetAverageFacilityRatingByProgramId() {
        // given
        satisfactionRepository.save(highSatisfaction);  // 4점

        // when
        Double avgRating = satisfactionRepository.getAverageFacilityRatingByProgramId(testProgram.getId());

        // then
        assertThat(avgRating).isEqualTo(4.0);
    }

    @Test
    @Order(31)
    @DisplayName("프로그램의 유용성 평균 평점 조회")
    void testGetAverageUsefulnessRatingByProgramId() {
        // given
        satisfactionRepository.save(highSatisfaction);  // 5점
        satisfactionRepository.save(mediumSatisfaction);  // 4점

        // when
        Double avgRating = satisfactionRepository.getAverageUsefulnessRatingByProgramId(testProgram.getId());

        // then
        assertThat(avgRating).isEqualTo(4.5);
    }

    @Test
    @Order(32)
    @DisplayName("프로그램의 전체 평균 만족도 조회")
    void testGetTotalAverageRatingByProgramId() {
        // given
        satisfactionRepository.save(highSatisfaction);  // (5+5+5+4+5)/5 = 4.8

        // when
        Double totalAvg = satisfactionRepository.getTotalAverageRatingByProgramId(testProgram.getId());

        // then
        assertThat(totalAvg).isEqualTo(4.8);
    }

    @Test
    @Order(33)
    @DisplayName("프로그램의 추천 비율 조회")
    void testGetRecommendationRateByProgramId() {
        // given
        satisfactionRepository.save(highSatisfaction);  // 추천
        satisfactionRepository.save(lowSatisfaction);   // 비추천
        satisfactionRepository.save(mediumSatisfaction);  // 추천

        // when
        Double recommendRate = satisfactionRepository.getRecommendationRateByProgramId(testProgram.getId());

        // then
        assertThat(recommendRate).isEqualTo(2.0 / 3.0);
    }

    @Test
    @Order(34)
    @DisplayName("특정 평점 이상의 만족도 응답 수 조회")
    void testCountByOverallRatingGreaterThanEqual() {
        // given
        satisfactionRepository.save(highSatisfaction);  // 5점
        satisfactionRepository.save(mediumSatisfaction);  // 3점
        satisfactionRepository.save(lowSatisfaction);  // 2점

        // when
        long count = satisfactionRepository.countByOverallRatingGreaterThanEqual(4);

        // then
        assertThat(count).isEqualTo(1);
    }

    // ========== 존재 여부 확인 테스트 ==========

    @Test
    @Order(35)
    @DisplayName("프로그램과 사용자의 만족도 존재 여부 확인")
    void testExistsByProgramAndUserId() {
        // given
        satisfactionRepository.save(highSatisfaction);

        // when & then
        assertThat(satisfactionRepository.existsByProgramAndUserId(testProgram, 3001L)).isTrue();
        assertThat(satisfactionRepository.existsByProgramAndUserId(testProgram, 9999L)).isFalse();
    }

    @Test
    @Order(36)
    @DisplayName("프로그램 ID와 사용자 ID로 만족도 존재 여부 확인")
    void testExistsByProgramIdAndUserId() {
        // given
        satisfactionRepository.save(highSatisfaction);

        // when & then
        assertThat(satisfactionRepository.existsByProgramIdAndUserId(testProgram.getId(), 3001L)).isTrue();
        assertThat(satisfactionRepository.existsByProgramIdAndUserId(testProgram.getId(), 9999L)).isFalse();
    }

    // ========== 비즈니스 메서드 테스트 ==========

    @Test
    @Order(37)
    @DisplayName("만족도 생성 팩토리 메서드 테스트")
    void testCreateSatisfactionFactory() {
        // when
        ProgramSatisfaction satisfaction = ProgramSatisfaction.create(
                testProgram, 3004L, 4, 4, 5, 4, 4,
                "좋았습니다", "개선이 필요합니다", true);

        // then
        assertThat(satisfaction.getUserId()).isEqualTo(3004L);
        assertThat(satisfaction.getOverallRating()).isEqualTo(4);
    }

    @Test
    @Order(38)
    @DisplayName("만족도 정보 업데이트 메서드 테스트")
    void testUpdateSatisfactionMethod() {
        // given
        ProgramSatisfaction saved = satisfactionRepository.save(highSatisfaction);

        // when
        saved.update(4, 4, 5, 3, 4, "변경된 장점", "변경된 개선사항", false);
        ProgramSatisfaction updated = satisfactionRepository.save(saved);

        // then
        assertThat(updated.getOverallRating()).isEqualTo(4);
        assertThat(updated.getStrengths()).isEqualTo("변경된 장점");
        assertThat(updated.getWouldRecommend()).isFalse();
    }

    @Test
    @Order(39)
    @DisplayName("평균 만족도 계산 메서드 테스트")
    void testCalculateAverageRating() {
        // given
        ProgramSatisfaction saved = satisfactionRepository.save(highSatisfaction);

        // when
        double avgRating = saved.calculateAverageRating();

        // then
        assertThat(avgRating).isEqualTo(4.8);  // (5+5+5+4+5)/5
    }

    @Test
    @Order(40)
    @DisplayName("높은 만족도 여부 확인 메서드 테스트")
    void testIsHighSatisfaction() {
        // given & when & then
        assertThat(highSatisfaction.isHighSatisfaction()).isTrue();
        assertThat(lowSatisfaction.isHighSatisfaction()).isFalse();
    }

    @Test
    @Order(41)
    @DisplayName("낮은 만족도 여부 확인 메서드 테스트")
    void testIsLowSatisfaction() {
        // given & when & then
        assertThat(lowSatisfaction.isLowSatisfaction()).isTrue();
        assertThat(highSatisfaction.isLowSatisfaction()).isFalse();
    }
}
