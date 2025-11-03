package com.university.scms.domain.counseling.repository;

import com.university.scms.config.JpaConfig;
import com.university.scms.domain.counseling.entity.CounselorAvailability;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * CounselorAvailabilityRepository 테스트
 * 실제 MySQL 데이터베이스를 사용하여 테스트합니다.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(JpaConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CounselorAvailabilityRepositoryTest {

    @Autowired
    private CounselorAvailabilityRepository availabilityRepository;

    private CounselorAvailability testAvailability1;
    private CounselorAvailability testAvailability2;
    private CounselorAvailability testAvailability3;

    @BeforeEach
    void setUp() {
        // 테스트 가용 시간 1: 상담사 10의 월요일 오전
        testAvailability1 = CounselorAvailability.builder()
                .counselorId(10L)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(12, 0))
                .isAvailable(true)
                .build();

        // 테스트 가용 시간 2: 상담사 10의 월요일 오후
        testAvailability2 = CounselorAvailability.builder()
                .counselorId(10L)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(18, 0))
                .isAvailable(true)
                .build();

        // 테스트 가용 시간 3: 상담사 11의 화요일
        testAvailability3 = CounselorAvailability.builder()
                .counselorId(11L)
                .dayOfWeek(DayOfWeek.TUESDAY)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(16, 0))
                .isAvailable(true)
                .build();
    }

    @AfterEach
    void tearDown() {
        availabilityRepository.deleteAll();
    }

    // ========== 기본 CRUD 테스트 ==========

    @Test
    @Order(1)
    @DisplayName("가용 시간 생성 테스트")
    void testCreateAvailability() {
        // given & when
        CounselorAvailability savedAvailability = availabilityRepository.save(testAvailability1);

        // then
        assertThat(savedAvailability.getId()).isNotNull();
        assertThat(savedAvailability.getCounselorId()).isEqualTo(10L);
        assertThat(savedAvailability.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
    }

    @Test
    @Order(2)
    @DisplayName("가용 시간 조회 테스트")
    void testFindAvailability() {
        // given
        CounselorAvailability savedAvailability = availabilityRepository.save(testAvailability1);

        // when
        CounselorAvailability foundAvailability = 
                availabilityRepository.findById(savedAvailability.getId()).orElse(null);

        // then
        assertThat(foundAvailability).isNotNull();
        assertThat(foundAvailability.getStartTime()).isEqualTo(LocalTime.of(9, 0));
        assertThat(foundAvailability.getEndTime()).isEqualTo(LocalTime.of(12, 0));
    }

    @Test
    @Order(3)
    @DisplayName("가용 시간 수정 테스트")
    void testUpdateAvailability() {
        // given
        CounselorAvailability savedAvailability = availabilityRepository.save(testAvailability1);

        // when
        savedAvailability.updateTime(LocalTime.of(10, 0), LocalTime.of(13, 0));
        CounselorAvailability updatedAvailability = availabilityRepository.save(savedAvailability);

        // then
        assertThat(updatedAvailability.getStartTime()).isEqualTo(LocalTime.of(10, 0));
        assertThat(updatedAvailability.getEndTime()).isEqualTo(LocalTime.of(13, 0));
    }

    @Test
    @Order(4)
    @DisplayName("가용 시간 삭제 테스트")
    void testDeleteAvailability() {
        // given
        CounselorAvailability savedAvailability = availabilityRepository.save(testAvailability1);
        Long availabilityId = savedAvailability.getId();

        // when
        availabilityRepository.delete(savedAvailability);

        // then
        assertThat(availabilityRepository.findById(availabilityId)).isEmpty();
    }

    // ========== 기본 조회 메서드 테스트 ==========

    @Test
    @Order(5)
    @DisplayName("상담사 ID로 가용 시간 목록 조회 테스트")
    void testFindByCounselorId() {
        // given
        availabilityRepository.save(testAvailability1);
        availabilityRepository.save(testAvailability2);
        availabilityRepository.save(testAvailability3);

        // when
        List<CounselorAvailability> counselor10Availabilities = 
                availabilityRepository.findByCounselorId(10L);
        List<CounselorAvailability> counselor11Availabilities = 
                availabilityRepository.findByCounselorId(11L);

        // then
        assertThat(counselor10Availabilities).hasSize(2);
        assertThat(counselor11Availabilities).hasSize(1);
    }

    @Test
    @Order(6)
    @DisplayName("요일별 가용 시간 목록 조회 테스트")
    void testFindByDayOfWeek() {
        // given
        availabilityRepository.save(testAvailability1);
        availabilityRepository.save(testAvailability2);
        availabilityRepository.save(testAvailability3);

        // when
        List<CounselorAvailability> mondayAvailabilities = 
                availabilityRepository.findByDayOfWeek(DayOfWeek.MONDAY);
        List<CounselorAvailability> tuesdayAvailabilities = 
                availabilityRepository.findByDayOfWeek(DayOfWeek.TUESDAY);

        // then
        assertThat(mondayAvailabilities).hasSize(2);
        assertThat(tuesdayAvailabilities).hasSize(1);
    }

    @Test
    @Order(7)
    @DisplayName("가용 상태별 조회 테스트")
    void testFindByIsAvailable() {
        // given
        availabilityRepository.save(testAvailability1);
        
        CounselorAvailability unavailableSlot = CounselorAvailability.builder()
                .counselorId(10L)
                .dayOfWeek(DayOfWeek.WEDNESDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(12, 0))
                .isAvailable(false)
                .build();
        availabilityRepository.save(unavailableSlot);

        // when
        List<CounselorAvailability> availableSlots = 
                availabilityRepository.findByIsAvailable(true);
        List<CounselorAvailability> unavailableSlots = 
                availabilityRepository.findByIsAvailable(false);

        // then
        assertThat(availableSlots).hasSize(1);
        assertThat(unavailableSlots).hasSize(1);
    }

    // ========== 상담사별 조회 테스트 ==========

    @Test
    @Order(8)
    @DisplayName("상담사의 요일별 가용 시간 조회 테스트")
    void testFindByCounselorIdAndDayOfWeek() {
        // given
        availabilityRepository.save(testAvailability1);
        availabilityRepository.save(testAvailability2);
        availabilityRepository.save(testAvailability3);

        // when
        List<CounselorAvailability> mondaySlots = 
                availabilityRepository.findByCounselorIdAndDayOfWeek(10L, DayOfWeek.MONDAY);

        // then
        assertThat(mondaySlots).hasSize(2);
    }

    @Test
    @Order(9)
    @DisplayName("상담사의 활성화된 가용 시간 조회 테스트")
    void testFindActiveByCounselorId() {
        // given
        availabilityRepository.save(testAvailability1);
        availabilityRepository.save(testAvailability2);
        
        CounselorAvailability inactiveSlot = CounselorAvailability.builder()
                .counselorId(10L)
                .dayOfWeek(DayOfWeek.TUESDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(12, 0))
                .isAvailable(false)
                .build();
        availabilityRepository.save(inactiveSlot);

        // when
        List<CounselorAvailability> activeSlots = 
                availabilityRepository.findActiveByCounselorId(10L);

        // then
        assertThat(activeSlots).hasSize(2);
    }

    @Test
    @Order(10)
    @DisplayName("상담사의 특정 요일 활성화된 가용 시간 조회 테스트")
    void testFindActiveByCounselorIdAndDayOfWeek() {
        // given
        availabilityRepository.save(testAvailability1);
        availabilityRepository.save(testAvailability2);

        // when
        List<CounselorAvailability> activeMonday = 
                availabilityRepository.findActiveByCounselorIdAndDayOfWeek(10L, DayOfWeek.MONDAY);

        // then
        assertThat(activeMonday).hasSize(2);
    }

    // ========== 시간 범위 검색 테스트 ==========

    @Test
    @Order(11)
    @DisplayName("특정 시간에 가용한 상담사 ID 목록 조회 테스트")
    void testFindAvailableCounselorIdsByDayAndTime() {
        // given
        availabilityRepository.save(testAvailability1);
        availabilityRepository.save(testAvailability2);
        availabilityRepository.save(testAvailability3);

        // when
        List<Long> availableCounselors = availabilityRepository
                .findAvailableCounselorIdsByDayAndTime(DayOfWeek.MONDAY, LocalTime.of(10, 0));

        // then
        assertThat(availableCounselors).hasSize(1);
        assertThat(availableCounselors).contains(10L);
    }

    @Test
    @Order(12)
    @DisplayName("상담사의 특정 시간 가용 여부 확인 테스트")
    void testIsCounselorAvailableAt() {
        // given
        availabilityRepository.save(testAvailability1);

        // when
        boolean available = availabilityRepository
                .isCounselorAvailableAt(10L, DayOfWeek.MONDAY, LocalTime.of(10, 0));
        boolean notAvailable = availabilityRepository
                .isCounselorAvailableAt(10L, DayOfWeek.MONDAY, LocalTime.of(13, 0));

        // then
        assertThat(available).isTrue();
        assertThat(notAvailable).isFalse();
    }

    @Test
    @Order(13)
    @DisplayName("상담사의 시간 범위 겹침 여부 확인 테스트")
    void testHasTimeOverlap() {
        // given
        CounselorAvailability saved = availabilityRepository.save(testAvailability1);

        // when - 겹치는 경우
        boolean overlaps = availabilityRepository.hasTimeOverlap(
                10L, 
                DayOfWeek.MONDAY, 
                LocalTime.of(11, 0), 
                LocalTime.of(13, 0),
                saved.getId()
        );

        // then
        assertThat(overlaps).isFalse(); // exclude ID가 자신이므로 false

        // when - 다른 시간대로 겹침 확인
        CounselorAvailability newSlot = CounselorAvailability.builder()
                .counselorId(10L)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(11, 0))
                .endTime(LocalTime.of(13, 0))
                .isAvailable(true)
                .build();
        CounselorAvailability saved2 = availabilityRepository.save(newSlot);

        boolean actualOverlap = availabilityRepository.hasTimeOverlap(
                10L,
                DayOfWeek.MONDAY,
                LocalTime.of(11, 30),
                LocalTime.of(14, 0),
                saved2.getId()
        );

        // then
        assertThat(actualOverlap).isTrue();
    }

    // ========== 통계 조회 테스트 ==========

    @Test
    @Order(14)
    @DisplayName("상담사의 전체 가용 시간 수 조회 테스트")
    void testCountByCounselorId() {
        // given
        availabilityRepository.save(testAvailability1);
        availabilityRepository.save(testAvailability2);
        availabilityRepository.save(testAvailability3);

        // when
        long counselor10Count = availabilityRepository.countByCounselorId(10L);
        long counselor11Count = availabilityRepository.countByCounselorId(11L);

        // then
        assertThat(counselor10Count).isEqualTo(2);
        assertThat(counselor11Count).isEqualTo(1);
    }

    @Test
    @Order(15)
    @DisplayName("상담사의 활성화된 가용 시간 수 조회 테스트")
    void testCountActiveByCounselorId() {
        // given
        availabilityRepository.save(testAvailability1);
        
        CounselorAvailability inactiveSlot = CounselorAvailability.builder()
                .counselorId(10L)
                .dayOfWeek(DayOfWeek.TUESDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(12, 0))
                .isAvailable(false)
                .build();
        availabilityRepository.save(inactiveSlot);

        // when
        long activeCount = availabilityRepository.countActiveByCounselorId(10L);

        // then
        assertThat(activeCount).isEqualTo(1);
    }

    @Test
    @Order(16)
    @DisplayName("요일별 전체 가용 시간 수 조회 테스트")
    void testCountByDayOfWeek() {
        // given
        availabilityRepository.save(testAvailability1);
        availabilityRepository.save(testAvailability2);
        availabilityRepository.save(testAvailability3);

        // when
        long mondayCount = availabilityRepository.countByDayOfWeek(DayOfWeek.MONDAY);
        long tuesdayCount = availabilityRepository.countByDayOfWeek(DayOfWeek.TUESDAY);

        // then
        assertThat(mondayCount).isEqualTo(2);
        assertThat(tuesdayCount).isEqualTo(1);
    }

    @Test
    @Order(17)
    @DisplayName("요일별 활성화된 가용 시간 수 조회 테스트")
    void testCountActiveByDayOfWeek() {
        // given
        availabilityRepository.save(testAvailability1);
        availabilityRepository.save(testAvailability2);

        // when
        long activeMondayCount = availabilityRepository.countActiveByDayOfWeek(DayOfWeek.MONDAY);

        // then
        assertThat(activeMondayCount).isEqualTo(2);
    }

    // ========== 상담사 목록 조회 테스트 ==========

    @Test
    @Order(18)
    @DisplayName("특정 요일에 가용한 상담사 ID 목록 조회 테스트")
    void testFindAvailableCounselorIdsByDay() {
        // given
        availabilityRepository.save(testAvailability1);
        availabilityRepository.save(testAvailability2);
        availabilityRepository.save(testAvailability3);

        // when
        List<Long> mondayCounselors = 
                availabilityRepository.findAvailableCounselorIdsByDay(DayOfWeek.MONDAY);
        List<Long> tuesdayCounselors = 
                availabilityRepository.findAvailableCounselorIdsByDay(DayOfWeek.TUESDAY);

        // then
        assertThat(mondayCounselors).hasSize(1);
        assertThat(mondayCounselors).contains(10L);
        assertThat(tuesdayCounselors).hasSize(1);
        assertThat(tuesdayCounselors).contains(11L);
    }

    @Test
    @Order(19)
    @DisplayName("가용 시간이 있는 모든 상담사 ID 목록 조회 테스트")
    void testFindAllAvailableCounselorIds() {
        // given
        availabilityRepository.save(testAvailability1);
        availabilityRepository.save(testAvailability3);

        // when
        List<Long> allCounselors = availabilityRepository.findAllAvailableCounselorIds();

        // then
        assertThat(allCounselors).hasSize(2);
        assertThat(allCounselors).containsExactlyInAnyOrder(10L, 11L);
    }

    // ========== 존재 여부 확인 테스트 ==========

    @Test
    @Order(20)
    @DisplayName("상담사의 가용 시간 존재 여부 확인 테스트")
    void testExistsByCounselorId() {
        // given
        availabilityRepository.save(testAvailability1);

        // when
        boolean exists = availabilityRepository.existsByCounselorId(10L);
        boolean notExists = availabilityRepository.existsByCounselorId(999L);

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @Order(21)
    @DisplayName("상담사의 특정 요일 가용 시간 존재 여부 확인 테스트")
    void testExistsByCounselorIdAndDayOfWeek() {
        // given
        availabilityRepository.save(testAvailability1);

        // when
        boolean exists = availabilityRepository
                .existsByCounselorIdAndDayOfWeek(10L, DayOfWeek.MONDAY);
        boolean notExists = availabilityRepository
                .existsByCounselorIdAndDayOfWeek(10L, DayOfWeek.FRIDAY);

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @Order(22)
    @DisplayName("상담사의 활성화된 가용 시간 존재 여부 확인 테스트")
    void testHasActiveAvailability() {
        // given
        availabilityRepository.save(testAvailability1);

        // when
        boolean hasActive = availabilityRepository.hasActiveAvailability(10L);
        boolean noActive = availabilityRepository.hasActiveAvailability(999L);

        // then
        assertThat(hasActive).isTrue();
        assertThat(noActive).isFalse();
    }

    // ========== 일괄 조회 테스트 ==========

    @Test
    @Order(23)
    @DisplayName("상담사의 주간 스케줄 조회 테스트")
    void testFindWeeklyScheduleByCounselorId() {
        // given
        availabilityRepository.save(testAvailability1);
        availabilityRepository.save(testAvailability2);
        
        CounselorAvailability tuesdaySlot = CounselorAvailability.builder()
                .counselorId(10L)
                .dayOfWeek(DayOfWeek.TUESDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(12, 0))
                .isAvailable(true)
                .build();
        availabilityRepository.save(tuesdaySlot);

        // when
        List<CounselorAvailability> weeklySchedule = 
                availabilityRepository.findWeeklyScheduleByCounselorId(10L);

        // then
        assertThat(weeklySchedule).hasSize(3);
    }

    @Test
    @Order(24)
    @DisplayName("여러 상담사의 특정 요일 가용 시간 조회 테스트")
    void testFindByCounselorIdsAndDayOfWeek() {
        // given
        availabilityRepository.save(testAvailability1);
        
        CounselorAvailability counselor11Monday = CounselorAvailability.builder()
                .counselorId(11L)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(15, 0))
                .isAvailable(true)
                .build();
        availabilityRepository.save(counselor11Monday);

        // when
        List<CounselorAvailability> mondaySlots = availabilityRepository
                .findByCounselorIdsAndDayOfWeek(List.of(10L, 11L), DayOfWeek.MONDAY);

        // then
        assertThat(mondaySlots).hasSize(2);
    }

    // ========== 시간대별 분석 테스트 ==========

    @Test
    @Order(25)
    @DisplayName("특정 시간대에 가용한 상담사 수 조회 테스트")
    void testCountAvailableCounselorsAt() {
        // given
        availabilityRepository.save(testAvailability1);
        availabilityRepository.save(testAvailability3);

        // when
        long mondayMorningCount = availabilityRepository
                .countAvailableCounselorsAt(DayOfWeek.MONDAY, LocalTime.of(10, 0));
        long tuesdayAfternoonCount = availabilityRepository
                .countAvailableCounselorsAt(DayOfWeek.TUESDAY, LocalTime.of(14, 0));

        // then
        assertThat(mondayMorningCount).isEqualTo(1);
        assertThat(tuesdayAfternoonCount).isEqualTo(1);
    }

    // ========== 비즈니스 메서드 테스트 ==========

    @Test
    @Order(26)
    @DisplayName("가용 상태 설정 메서드 테스트")
    void testSetAvailable() {
        // given
        CounselorAvailability availability = availabilityRepository.save(testAvailability1);
        availability.setUnavailable();
        availability = availabilityRepository.save(availability);

        // when
        availability.setAvailable();
        CounselorAvailability updated = availabilityRepository.save(availability);

        // then
        assertThat(updated.getIsAvailable()).isTrue();
    }

    @Test
    @Order(27)
    @DisplayName("불가 상태 설정 메서드 테스트")
    void testSetUnavailable() {
        // given
        CounselorAvailability availability = availabilityRepository.save(testAvailability1);

        // when
        availability.setUnavailable();
        CounselorAvailability updated = availabilityRepository.save(availability);

        // then
        assertThat(updated.getIsAvailable()).isFalse();
    }

    @Test
    @Order(28)
    @DisplayName("가용 상태 토글 메서드 테스트")
    void testToggleAvailability() {
        // given
        CounselorAvailability availability = availabilityRepository.save(testAvailability1);

        // when - 첫 번째 토글
        availability.toggleAvailability();
        CounselorAvailability toggled1 = availabilityRepository.save(availability);

        // then
        assertThat(toggled1.getIsAvailable()).isFalse();

        // when - 두 번째 토글
        toggled1.toggleAvailability();
        CounselorAvailability toggled2 = availabilityRepository.save(toggled1);

        // then
        assertThat(toggled2.getIsAvailable()).isTrue();
    }

    @Test
    @Order(29)
    @DisplayName("특정 시간이 가용 범위에 포함되는지 확인 테스트")
    void testIsAvailableAt() {
        // given
        CounselorAvailability availability = availabilityRepository.save(testAvailability1);

        // when & then
        assertThat(availability.isAvailableAt(LocalTime.of(10, 0))).isTrue();
        assertThat(availability.isAvailableAt(LocalTime.of(13, 0))).isFalse();
    }

    @Test
    @Order(30)
    @DisplayName("시간 범위 겹침 확인 메서드 테스트")
    void testOverlaps() {
        // given
        CounselorAvailability availability = availabilityRepository.save(testAvailability1);

        // when & then
        assertThat(availability.overlaps(LocalTime.of(11, 0), LocalTime.of(13, 0))).isTrue();
        assertThat(availability.overlaps(LocalTime.of(13, 0), LocalTime.of(15, 0))).isFalse();
    }
}
