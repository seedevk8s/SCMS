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
 * ProgramParticipantRepository 테스트
 * 실제 MySQL 데이터베이스를 사용하여 테스트합니다.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(JpaConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProgramParticipantRepositoryTest {

    @Autowired
    private ProgramParticipantRepository participantRepository;

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private ProgramApplicationRepository applicationRepository;

    private Program testProgram;
    private ProgramApplication testApplication;
    private ProgramParticipant registeredParticipant;
    private ProgramParticipant attendedParticipant;
    private ProgramParticipant absentParticipant;

    @BeforeEach
    void setUp() {
        // 테스트용 프로그램 생성
        testProgram = Program.builder()
                .title("Python 데이터 분석 특강")
                .description("Python을 활용한 데이터 분석 실습")
                .category("데이터과학")
                .organizerId(100L)
                .location("IT관 301호")
                .capacity(25)
                .currentParticipants(0)
                .startDate(LocalDateTime.now().plusDays(3))
                .endDate(LocalDateTime.now().plusDays(10))
                .mileagePoints(40)
                .status(ProgramStatus.OPEN)
                .build();
        testProgram = programRepository.save(testProgram);

        // 테스트용 신청 생성
        testApplication = ProgramApplication.builder()
                .program(testProgram)
                .userId(2001L)
                .status(ApplicationStatus.APPROVED)
                .applicationDate(LocalDateTime.now().minusDays(2))
                .build();
        testApplication = applicationRepository.save(testApplication);

        // 등록 상태 참여자
        registeredParticipant = ProgramParticipant.builder()
                .program(testProgram)
                .application(testApplication)
                .userId(2001L)
                .attendanceStatus(AttendanceStatus.REGISTERED)
                .mileageAwarded(false)
                .build();

        // 출석 확인된 참여자
        attendedParticipant = ProgramParticipant.builder()
                .program(testProgram)
                .userId(2002L)
                .attendanceStatus(AttendanceStatus.ATTENDED)
                .attendanceConfirmedAt(LocalDateTime.now())
                .attendanceConfirmedBy(300L)
                .mileageAwarded(false)
                .feedback("매우 유익한 강의였습니다.")
                .rating(5)
                .build();

        // 결석 처리된 참여자
        absentParticipant = ProgramParticipant.builder()
                .program(testProgram)
                .userId(2003L)
                .attendanceStatus(AttendanceStatus.ABSENT)
                .attendanceConfirmedAt(LocalDateTime.now())
                .attendanceConfirmedBy(300L)
                .mileageAwarded(false)
                .build();
    }

    @AfterEach
    void tearDown() {
        participantRepository.deleteAll();
        applicationRepository.deleteAll();
        programRepository.deleteAll();
    }

    // ========== 기본 CRUD 테스트 ==========

    @Test
    @Order(1)
    @DisplayName("참여자 생성 테스트")
    void testCreateParticipant() {
        // when
        ProgramParticipant saved = participantRepository.save(registeredParticipant);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getProgram().getTitle()).isEqualTo("Python 데이터 분석 특강");
        assertThat(saved.getUserId()).isEqualTo(2001L);
        assertThat(saved.getAttendanceStatus()).isEqualTo(AttendanceStatus.REGISTERED);
    }

    @Test
    @Order(2)
    @DisplayName("참여자 조회 테스트")
    void testFindParticipant() {
        // given
        ProgramParticipant saved = participantRepository.save(registeredParticipant);

        // when
        Optional<ProgramParticipant> found = participantRepository.findById(saved.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo(2001L);
    }

    @Test
    @Order(3)
    @DisplayName("참여자 수정 테스트")
    void testUpdateParticipant() {
        // given
        ProgramParticipant saved = participantRepository.save(registeredParticipant);

        // when
        saved.confirmAttendance(300L);
        ProgramParticipant updated = participantRepository.save(saved);

        // then
        assertThat(updated.getAttendanceStatus()).isEqualTo(AttendanceStatus.ATTENDED);
        assertThat(updated.getAttendanceConfirmedBy()).isEqualTo(300L);
    }

    @Test
    @Order(4)
    @DisplayName("참여자 삭제 테스트")
    void testDeleteParticipant() {
        // given
        ProgramParticipant saved = participantRepository.save(registeredParticipant);
        Long id = saved.getId();

        // when
        participantRepository.delete(saved);

        // then
        assertThat(participantRepository.findById(id)).isEmpty();
    }

    // ========== 기본 조회 메서드 테스트 ==========

    @Test
    @Order(5)
    @DisplayName("프로그램과 사용자로 참여자 조회")
    void testFindByProgramAndUserId() {
        // given
        participantRepository.save(registeredParticipant);

        // when
        Optional<ProgramParticipant> found = 
                participantRepository.findByProgramAndUserId(testProgram, 2001L);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getApplication()).isNotNull();
    }

    @Test
    @Order(6)
    @DisplayName("프로그램 ID와 사용자 ID로 참여자 조회")
    void testFindByProgramIdAndUserId() {
        // given
        participantRepository.save(registeredParticipant);

        // when
        Optional<ProgramParticipant> found = 
                participantRepository.findByProgramIdAndUserId(testProgram.getId(), 2001L);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo(2001L);
    }

    @Test
    @Order(7)
    @DisplayName("신청 ID로 참여자 조회")
    void testFindByApplicationId() {
        // given
        ProgramParticipant saved = participantRepository.save(registeredParticipant);

        // when
        Optional<ProgramParticipant> found = 
                participantRepository.findByApplicationId(testApplication.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
    }

    // ========== 프로그램별 참여자 조회 테스트 ==========

    @Test
    @Order(8)
    @DisplayName("프로그램의 모든 참여자 조회")
    void testFindByProgram() {
        // given
        participantRepository.save(registeredParticipant);
        participantRepository.save(attendedParticipant);
        participantRepository.save(absentParticipant);

        // when
        List<ProgramParticipant> participants = 
                participantRepository.findByProgram(testProgram);

        // then
        assertThat(participants).hasSize(3);
    }

    @Test
    @Order(9)
    @DisplayName("프로그램 ID로 참여자 목록 조회")
    void testFindByProgramId() {
        // given
        participantRepository.save(registeredParticipant);
        participantRepository.save(attendedParticipant);

        // when
        List<ProgramParticipant> participants = 
                participantRepository.findByProgramId(testProgram.getId());

        // then
        assertThat(participants).hasSize(2);
    }

    @Test
    @Order(10)
    @DisplayName("프로그램의 특정 출석 상태 참여자 조회")
    void testFindByProgramAndAttendanceStatus() {
        // given
        participantRepository.save(attendedParticipant);
        participantRepository.save(absentParticipant);

        // when
        List<ProgramParticipant> attended = 
                participantRepository.findByProgramAndAttendanceStatus(testProgram, AttendanceStatus.ATTENDED);

        // then
        assertThat(attended).hasSize(1);
        assertThat(attended.get(0).getUserId()).isEqualTo(2002L);
    }

    // ========== 사용자별 참여 조회 테스트 ==========

    @Test
    @Order(11)
    @DisplayName("사용자의 모든 참여 이력 조회")
    void testFindByUserId() {
        // given
        participantRepository.save(registeredParticipant);

        // when
        List<ProgramParticipant> participants = 
                participantRepository.findByUserId(2001L);

        // then
        assertThat(participants).hasSize(1);
        assertThat(participants.get(0).getAttendanceStatus()).isEqualTo(AttendanceStatus.REGISTERED);
    }

    @Test
    @Order(12)
    @DisplayName("사용자의 특정 출석 상태 참여 조회")
    void testFindByUserIdAndAttendanceStatus() {
        // given
        participantRepository.save(attendedParticipant);

        // when
        List<ProgramParticipant> attended = 
                participantRepository.findByUserIdAndAttendanceStatus(2002L, AttendanceStatus.ATTENDED);

        // then
        assertThat(attended).hasSize(1);
    }

    @Test
    @Order(13)
    @DisplayName("사용자의 참여 목록 생성일 기준 내림차순 조회")
    void testFindByUserIdOrderByCreatedAtDesc() {
        // given
        participantRepository.save(registeredParticipant);

        // when
        List<ProgramParticipant> participants = 
                participantRepository.findByUserIdOrderByCreatedAtDesc(2001L);

        // then
        assertThat(participants).hasSize(1);
    }

    // ========== 출석 상태별 조회 테스트 ==========

    @Test
    @Order(14)
    @DisplayName("특정 출석 상태의 참여자 목록 조회")
    void testFindByAttendanceStatus() {
        // given
        participantRepository.save(registeredParticipant);
        participantRepository.save(attendedParticipant);
        participantRepository.save(absentParticipant);

        // when
        List<ProgramParticipant> registered = 
                participantRepository.findByAttendanceStatus(AttendanceStatus.REGISTERED);

        // then
        assertThat(registered).hasSize(1);
    }

    @Test
    @Order(15)
    @DisplayName("출석 확인된 참여자 목록 조회")
    void testFindAttendedParticipants() {
        // given
        participantRepository.save(attendedParticipant);

        // when
        List<ProgramParticipant> attended = 
                participantRepository.findAttendedParticipants();

        // then
        assertThat(attended).hasSize(1);
        assertThat(attended.get(0).isAttended()).isTrue();
    }

    @Test
    @Order(16)
    @DisplayName("결석 처리된 참여자 목록 조회")
    void testFindAbsentParticipants() {
        // given
        participantRepository.save(absentParticipant);

        // when
        List<ProgramParticipant> absent = 
                participantRepository.findAbsentParticipants();

        // then
        assertThat(absent).hasSize(1);
        assertThat(absent.get(0).isAbsent()).isTrue();
    }

    @Test
    @Order(17)
    @DisplayName("등록 상태의 참여자 목록 조회")
    void testFindRegisteredParticipants() {
        // given
        participantRepository.save(registeredParticipant);

        // when
        List<ProgramParticipant> registered = 
                participantRepository.findRegisteredParticipants();

        // then
        assertThat(registered).hasSize(1);
        assertThat(registered.get(0).isRegistered()).isTrue();
    }

    // ========== 마일리지 관련 조회 테스트 ==========

    @Test
    @Order(18)
    @DisplayName("마일리지 미지급 참여자 조회")
    void testFindUnpaidMileageParticipants() {
        // given
        participantRepository.save(attendedParticipant);

        // when
        List<ProgramParticipant> unpaid = 
                participantRepository.findUnpaidMileageParticipants();

        // then
        assertThat(unpaid).hasSize(1);
        assertThat(unpaid.get(0).needsMileageAward()).isTrue();
    }

    @Test
    @Order(19)
    @DisplayName("프로그램의 마일리지 미지급 참여자 조회")
    void testFindUnpaidMileageParticipantsByProgramId() {
        // given
        participantRepository.save(attendedParticipant);

        // when
        List<ProgramParticipant> unpaid = 
                participantRepository.findUnpaidMileageParticipantsByProgramId(testProgram.getId());

        // then
        assertThat(unpaid).hasSize(1);
    }

    @Test
    @Order(20)
    @DisplayName("마일리지 지급 완료 참여자 조회")
    void testFindPaidMileageParticipants() {
        // given
        attendedParticipant.awardMileage();
        participantRepository.save(attendedParticipant);

        // when
        List<ProgramParticipant> paid = 
                participantRepository.findPaidMileageParticipants();

        // then
        assertThat(paid).hasSize(1);
        assertThat(paid.get(0).getMileageAwarded()).isTrue();
    }

    // ========== 후기 및 평가 관련 조회 테스트 ==========

    @Test
    @Order(21)
    @DisplayName("후기를 작성한 참여자 조회")
    void testFindParticipantsWithFeedback() {
        // given
        participantRepository.save(attendedParticipant);

        // when
        List<ProgramParticipant> withFeedback = 
                participantRepository.findParticipantsWithFeedback();

        // then
        assertThat(withFeedback).hasSize(1);
        assertThat(withFeedback.get(0).getFeedback()).isNotEmpty();
    }

    @Test
    @Order(22)
    @DisplayName("프로그램의 후기를 작성한 참여자 조회")
    void testFindParticipantsWithFeedbackByProgramId() {
        // given
        participantRepository.save(attendedParticipant);

        // when
        List<ProgramParticipant> withFeedback = 
                participantRepository.findParticipantsWithFeedbackByProgramId(testProgram.getId());

        // then
        assertThat(withFeedback).hasSize(1);
    }

    @Test
    @Order(23)
    @DisplayName("평가를 남긴 참여자 조회")
    void testFindParticipantsWithRating() {
        // given
        participantRepository.save(attendedParticipant);

        // when
        List<ProgramParticipant> withRating = 
                participantRepository.findParticipantsWithRating();

        // then
        assertThat(withRating).hasSize(1);
        assertThat(withRating.get(0).getRating()).isNotNull();
    }

    @Test
    @Order(24)
    @DisplayName("특정 평점 이상의 참여자 조회")
    void testFindByRatingGreaterThanEqual() {
        // given
        participantRepository.save(attendedParticipant);

        // when
        List<ProgramParticipant> highRated = 
                participantRepository.findByRatingGreaterThanEqual(4);

        // then
        assertThat(highRated).hasSize(1);
        assertThat(highRated.get(0).getRating()).isGreaterThanOrEqualTo(4);
    }

    // ========== 출석 확인 관련 조회 테스트 ==========

    @Test
    @Order(25)
    @DisplayName("특정 확인자가 처리한 참여자 조회")
    void testFindByAttendanceConfirmedBy() {
        // given
        participantRepository.save(attendedParticipant);
        participantRepository.save(absentParticipant);

        // when
        List<ProgramParticipant> confirmed = 
                participantRepository.findByAttendanceConfirmedBy(300L);

        // then
        assertThat(confirmed).hasSize(2);
    }

    @Test
    @Order(26)
    @DisplayName("출석 확인이 완료된 참여자 조회")
    void testFindConfirmedParticipants() {
        // given
        participantRepository.save(registeredParticipant);
        participantRepository.save(attendedParticipant);

        // when
        List<ProgramParticipant> confirmed = 
                participantRepository.findConfirmedParticipants();

        // then
        assertThat(confirmed).hasSize(1);
        assertThat(confirmed.get(0).getAttendanceConfirmedBy()).isNotNull();
    }

    @Test
    @Order(27)
    @DisplayName("출석 미확인 참여자 조회")
    void testFindUnconfirmedParticipants() {
        // given
        participantRepository.save(registeredParticipant);
        participantRepository.save(attendedParticipant);

        // when
        List<ProgramParticipant> unconfirmed = 
                participantRepository.findUnconfirmedParticipants();

        // then
        assertThat(unconfirmed).hasSize(1);
        assertThat(unconfirmed.get(0).getAttendanceConfirmedBy()).isNull();
    }

    // ========== 통계 및 집계 메서드 테스트 ==========

    @Test
    @Order(28)
    @DisplayName("프로그램의 참여자 수 조회")
    void testCountByProgram() {
        // given
        participantRepository.save(registeredParticipant);
        participantRepository.save(attendedParticipant);

        // when
        long count = participantRepository.countByProgram(testProgram);

        // then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @Order(29)
    @DisplayName("프로그램 ID로 참여자 수 조회")
    void testCountByProgramId() {
        // given
        participantRepository.save(registeredParticipant);

        // when
        long count = participantRepository.countByProgramId(testProgram.getId());

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    @Order(30)
    @DisplayName("프로그램의 특정 출석 상태 참여자 수 조회")
    void testCountByProgramAndAttendanceStatus() {
        // given
        participantRepository.save(registeredParticipant);
        participantRepository.save(attendedParticipant);

        // when
        long registeredCount = participantRepository.countByProgramAndAttendanceStatus(
                testProgram, AttendanceStatus.REGISTERED);

        // then
        assertThat(registeredCount).isEqualTo(1);
    }

    @Test
    @Order(31)
    @DisplayName("출석한 참여자 수 조회")
    void testCountAttendedParticipants() {
        // given
        participantRepository.save(attendedParticipant);

        // when
        long attendedCount = participantRepository.countAttendedParticipants();

        // then
        assertThat(attendedCount).isEqualTo(1);
    }

    @Test
    @Order(32)
    @DisplayName("프로그램의 평균 평점 조회")
    void testGetAverageRatingByProgramId() {
        // given
        participantRepository.save(attendedParticipant);

        // when
        Double avgRating = participantRepository.getAverageRatingByProgramId(testProgram.getId());

        // then
        assertThat(avgRating).isEqualTo(5.0);
    }

    @Test
    @Order(33)
    @DisplayName("마일리지 미지급 참여자 수 조회")
    void testCountUnpaidMileageParticipants() {
        // given
        participantRepository.save(attendedParticipant);

        // when
        long unpaidCount = participantRepository.countUnpaidMileageParticipants();

        // then
        assertThat(unpaidCount).isEqualTo(1);
    }

    // ========== 존재 여부 확인 테스트 ==========

    @Test
    @Order(34)
    @DisplayName("프로그램과 사용자의 참여 존재 여부 확인")
    void testExistsByProgramAndUserId() {
        // given
        participantRepository.save(registeredParticipant);

        // when & then
        assertThat(participantRepository.existsByProgramAndUserId(testProgram, 2001L)).isTrue();
        assertThat(participantRepository.existsByProgramAndUserId(testProgram, 9999L)).isFalse();
    }

    @Test
    @Order(35)
    @DisplayName("프로그램 ID와 사용자 ID로 참여 존재 여부 확인")
    void testExistsByProgramIdAndUserId() {
        // given
        participantRepository.save(registeredParticipant);

        // when & then
        assertThat(participantRepository.existsByProgramIdAndUserId(testProgram.getId(), 2001L)).isTrue();
        assertThat(participantRepository.existsByProgramIdAndUserId(testProgram.getId(), 9999L)).isFalse();
    }

    // ========== 비즈니스 메서드 테스트 ==========

    @Test
    @Order(36)
    @DisplayName("출석 확인 메서드 테스트")
    void testConfirmAttendance() {
        // given
        ProgramParticipant saved = participantRepository.save(registeredParticipant);

        // when
        saved.confirmAttendance(300L);
        ProgramParticipant updated = participantRepository.save(saved);

        // then
        assertThat(updated.isAttended()).isTrue();
        assertThat(updated.getAttendanceConfirmedBy()).isEqualTo(300L);
        assertThat(updated.getAttendanceConfirmedAt()).isNotNull();
    }

    @Test
    @Order(37)
    @DisplayName("결석 처리 메서드 테스트")
    void testMarkAbsent() {
        // given
        ProgramParticipant saved = participantRepository.save(registeredParticipant);

        // when
        saved.markAbsent(300L);
        ProgramParticipant updated = participantRepository.save(saved);

        // then
        assertThat(updated.isAbsent()).isTrue();
        assertThat(updated.getAttendanceConfirmedBy()).isEqualTo(300L);
    }

    @Test
    @Order(38)
    @DisplayName("마일리지 지급 완료 처리 메서드 테스트")
    void testAwardMileage() {
        // given
        ProgramParticipant saved = participantRepository.save(attendedParticipant);

        // when
        saved.awardMileage();
        ProgramParticipant updated = participantRepository.save(saved);

        // then
        assertThat(updated.getMileageAwarded()).isTrue();
        assertThat(updated.needsMileageAward()).isFalse();
    }

    @Test
    @Order(39)
    @DisplayName("후기 작성 메서드 테스트")
    void testWriteFeedback() {
        // given
        ProgramParticipant saved = participantRepository.save(registeredParticipant);

        // when
        saved.writeFeedback("정말 유익한 강의였습니다!", 5);
        ProgramParticipant updated = participantRepository.save(saved);

        // then
        assertThat(updated.getFeedback()).isEqualTo("정말 유익한 강의였습니다!");
        assertThat(updated.getRating()).isEqualTo(5);
        assertThat(updated.isValidRating()).isTrue();
    }
}
