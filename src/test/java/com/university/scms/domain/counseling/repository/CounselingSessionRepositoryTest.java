package com.university.scms.domain.counseling.repository;

import com.university.scms.config.JpaConfig;
import com.university.scms.domain.counseling.entity.CounselingReservation;
import com.university.scms.domain.counseling.entity.CounselingSession;
import com.university.scms.domain.counseling.entity.CounselingStatus;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * CounselingSessionRepository 테스트
 * 실제 MySQL 데이터베이스를 사용하여 테스트합니다.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(JpaConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CounselingSessionRepositoryTest {

    @Autowired
    private CounselingSessionRepository sessionRepository;

    @Autowired
    private CounselingReservationRepository reservationRepository;

    private CounselingReservation testReservation1;
    private CounselingReservation testReservation2;
    private CounselingSession testSession1;
    private CounselingSession testSession2;
    private CounselingSession testSession3;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        // 테스트 예약 생성
        testReservation1 = CounselingReservation.builder()
                .studentId(1L)
                .counselorId(10L)
                .reservationDate(now.plusDays(1))
                .sessionDuration(60)
                .counselingType("CAREER")
                .requestReason("진로 상담")
                .status(CounselingStatus.CONFIRMED)
                .build();
        testReservation1 = reservationRepository.save(testReservation1);

        testReservation2 = CounselingReservation.builder()
                .studentId(2L)
                .counselorId(11L)
                .reservationDate(now.plusDays(2))
                .sessionDuration(90)
                .counselingType("ACADEMIC")
                .requestReason("학업 상담")
                .status(CounselingStatus.CONFIRMED)
                .build();
        testReservation2 = reservationRepository.save(testReservation2);

        // 테스트 세션 1: 완료된 세션
        testSession1 = CounselingSession.builder()
                .reservation(testReservation1)
                .startTime(now.minusHours(2))
                .endTime(now.minusHours(1))
                .actualDuration(60)
                .sessionNotes("진로 상담 완료")
                .counselorNotes("학생의 목표가 명확함")
                .followUpRequired(false)
                .build();

        // 테스트 세션 2: 진행 중인 세션
        testSession2 = CounselingSession.builder()
                .reservation(testReservation1)
                .startTime(now.minusMinutes(30))
                .sessionNotes("진행 중")
                .counselorNotes("")
                .followUpRequired(false)
                .build();

        // 테스트 세션 3: 후속 상담 필요
        testSession3 = CounselingSession.builder()
                .reservation(testReservation2)
                .startTime(now.minusDays(1))
                .endTime(now.minusDays(1).plusMinutes(90))
                .actualDuration(90)
                .sessionNotes("학업 상담 완료")
                .counselorNotes("추가 상담 필요")
                .followUpRequired(true)
                .nextSessionDate(now.plusDays(7))
                .build();
    }

    @AfterEach
    void tearDown() {
        sessionRepository.deleteAll();
        reservationRepository.deleteAll();
    }

    // ========== 기본 CRUD 테스트 ==========

    @Test
    @Order(1)
    @DisplayName("세션 생성 테스트")
    void testCreateSession() {
        // given & when
        CounselingSession savedSession = sessionRepository.save(testSession1);

        // then
        assertThat(savedSession.getId()).isNotNull();
        assertThat(savedSession.getReservation()).isNotNull();
        assertThat(savedSession.getReservation().getId()).isEqualTo(testReservation1.getId());
    }

    @Test
    @Order(2)
    @DisplayName("세션 조회 테스트")
    void testFindSession() {
        // given
        CounselingSession savedSession = sessionRepository.save(testSession1);

        // when
        CounselingSession foundSession = sessionRepository.findById(savedSession.getId()).orElse(null);

        // then
        assertThat(foundSession).isNotNull();
        assertThat(foundSession.getSessionNotes()).isEqualTo("진로 상담 완료");
    }

    @Test
    @Order(3)
    @DisplayName("세션 수정 테스트")
    void testUpdateSession() {
        // given
        CounselingSession savedSession = sessionRepository.save(testSession1);

        // when
        savedSession.updateSessionNotes("수정된 상담 내용");
        CounselingSession updatedSession = sessionRepository.save(savedSession);

        // then
        assertThat(updatedSession.getSessionNotes()).isEqualTo("수정된 상담 내용");
    }

    @Test
    @Order(4)
    @DisplayName("세션 삭제 테스트")
    void testDeleteSession() {
        // given
        CounselingSession savedSession = sessionRepository.save(testSession1);
        Long sessionId = savedSession.getId();

        // when
        sessionRepository.delete(savedSession);

        // then
        assertThat(sessionRepository.findById(sessionId)).isEmpty();
    }

    // ========== 기본 조회 메서드 테스트 ==========

    @Test
    @Order(5)
    @DisplayName("예약 ID로 세션 목록 조회 테스트")
    void testFindByReservationId() {
        // given
        sessionRepository.save(testSession1);
        sessionRepository.save(testSession2);

        // when
        List<CounselingSession> sessions = sessionRepository.findByReservationId(testReservation1.getId());

        // then
        assertThat(sessions).hasSize(2);
    }

    @Test
    @Order(6)
    @DisplayName("예약 ID로 세션 존재 여부 확인 테스트")
    void testExistsByReservationId() {
        // given
        sessionRepository.save(testSession1);

        // when
        boolean exists = sessionRepository.existsByReservationId(testReservation1.getId());
        boolean notExists = sessionRepository.existsByReservationId(999L);

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @Order(7)
    @DisplayName("예약 ID로 세션 개수 조회 테스트")
    void testCountByReservationId() {
        // given
        sessionRepository.save(testSession1);
        sessionRepository.save(testSession2);

        // when
        long count = sessionRepository.countByReservationId(testReservation1.getId());

        // then
        assertThat(count).isEqualTo(2);
    }

    // ========== 상태별 조회 테스트 ==========

    @Test
    @Order(8)
    @DisplayName("진행 중인 세션 목록 조회 테스트")
    void testFindInProgressSessions() {
        // given
        sessionRepository.save(testSession1);
        sessionRepository.save(testSession2);

        // when
        List<CounselingSession> inProgressSessions = sessionRepository.findInProgressSessions();

        // then
        assertThat(inProgressSessions).hasSize(1);
        assertThat(inProgressSessions.get(0).isInProgress()).isTrue();
    }

    @Test
    @Order(9)
    @DisplayName("완료된 세션 목록 조회 테스트")
    void testFindCompletedSessions() {
        // given
        sessionRepository.save(testSession1);
        sessionRepository.save(testSession2);
        sessionRepository.save(testSession3);

        // when
        List<CounselingSession> completedSessions = sessionRepository.findCompletedSessions();

        // then
        assertThat(completedSessions).hasSize(2);
        assertThat(completedSessions.get(0).isCompleted()).isTrue();
    }

    @Test
    @Order(10)
    @DisplayName("시작되지 않은 세션 목록 조회 테스트")
    void testFindNotStartedSessions() {
        // given
        CounselingSession notStartedSession = CounselingSession.builder()
                .reservation(testReservation1)
                .sessionNotes("")
                .counselorNotes("")
                .followUpRequired(false)
                .build();
        sessionRepository.save(notStartedSession);

        // when
        List<CounselingSession> notStartedSessions = sessionRepository.findNotStartedSessions();

        // then
        assertThat(notStartedSessions).hasSize(1);
        assertThat(notStartedSessions.get(0).isNotStarted()).isTrue();
    }

    // ========== 날짜 기반 조회 테스트 ==========

    @Test
    @Order(11)
    @DisplayName("특정 날짜에 시작된 세션 목록 조회 테스트")
    void testFindByStartDate() {
        // given
        LocalDateTime today = LocalDateTime.now();
        sessionRepository.save(testSession2);

        // when
        List<CounselingSession> sessions = sessionRepository.findByStartDate(today);

        // then
        assertThat(sessions).isNotEmpty();
    }

    @Test
    @Order(12)
    @DisplayName("특정 기간에 시작된 세션 목록 조회 테스트")
    void testFindByStartTimeBetween() {
        // given
        sessionRepository.save(testSession1);
        sessionRepository.save(testSession2);

        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        // when
        List<CounselingSession> sessions = sessionRepository.findByStartTimeBetween(startDate, endDate);

        // then
        assertThat(sessions).hasSize(2);
    }

    @Test
    @Order(13)
    @DisplayName("특정 기간에 종료된 세션 목록 조회 테스트")
    void testFindByEndTimeBetween() {
        // given
        sessionRepository.save(testSession1);
        sessionRepository.save(testSession3);

        LocalDateTime startDate = LocalDateTime.now().minusDays(2);
        LocalDateTime endDate = LocalDateTime.now();

        // when
        List<CounselingSession> sessions = sessionRepository.findByEndTimeBetween(startDate, endDate);

        // then
        assertThat(sessions).hasSize(2);
    }

    // ========== 후속 상담 관련 조회 테스트 ==========

    @Test
    @Order(14)
    @DisplayName("후속 상담이 필요한 세션 목록 조회 테스트")
    void testFindByFollowUpRequiredTrue() {
        // given
        sessionRepository.save(testSession1);
        sessionRepository.save(testSession3);

        // when
        List<CounselingSession> followUpSessions = sessionRepository.findByFollowUpRequiredTrue();

        // then
        assertThat(followUpSessions).hasSize(1);
        assertThat(followUpSessions.get(0).needsFollowUp()).isTrue();
    }

    @Test
    @Order(15)
    @DisplayName("후속 상담이 예정된 세션 목록 조회 테스트")
    void testFindSessionsWithScheduledFollowUp() {
        // given
        sessionRepository.save(testSession1);
        sessionRepository.save(testSession3);

        // when
        List<CounselingSession> scheduledSessions = sessionRepository.findSessionsWithScheduledFollowUp();

        // then
        assertThat(scheduledSessions).hasSize(1);
        assertThat(scheduledSessions.get(0).hasScheduledFollowUp()).isTrue();
    }

    @Test
    @Order(16)
    @DisplayName("임박한 후속 상담 세션 목록 조회 테스트")
    void testFindImminentFollowUpSessions() {
        // given
        sessionRepository.save(testSession3);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekLater = now.plusDays(7);

        // when
        List<CounselingSession> imminentSessions = 
                sessionRepository.findImminentFollowUpSessions(now, weekLater);

        // then
        assertThat(imminentSessions).hasSize(1);
    }

    // ========== 예약과 결합 조회 테스트 ==========

    @Test
    @Order(17)
    @DisplayName("학생의 모든 세션 조회 테스트")
    void testFindByStudentId() {
        // given
        sessionRepository.save(testSession1);
        sessionRepository.save(testSession2);
        sessionRepository.save(testSession3);

        // when
        List<CounselingSession> student1Sessions = sessionRepository.findByStudentId(1L);
        List<CounselingSession> student2Sessions = sessionRepository.findByStudentId(2L);

        // then
        assertThat(student1Sessions).hasSize(2);
        assertThat(student2Sessions).hasSize(1);
    }

    @Test
    @Order(18)
    @DisplayName("상담사의 모든 세션 조회 테스트")
    void testFindByCounselorId() {
        // given
        sessionRepository.save(testSession1);
        sessionRepository.save(testSession2);
        sessionRepository.save(testSession3);

        // when
        List<CounselingSession> counselor10Sessions = sessionRepository.findByCounselorId(10L);
        List<CounselingSession> counselor11Sessions = sessionRepository.findByCounselorId(11L);

        // then
        assertThat(counselor10Sessions).hasSize(2);
        assertThat(counselor11Sessions).hasSize(1);
    }

    @Test
    @Order(19)
    @DisplayName("학생의 완료된 세션 조회 테스트")
    void testFindCompletedSessionsByStudentId() {
        // given
        sessionRepository.save(testSession1);
        sessionRepository.save(testSession2);

        // when
        List<CounselingSession> completedSessions = 
                sessionRepository.findCompletedSessionsByStudentId(1L);

        // then
        assertThat(completedSessions).hasSize(1);
        assertThat(completedSessions.get(0).isCompleted()).isTrue();
    }

    @Test
    @Order(20)
    @DisplayName("상담사의 완료된 세션 조회 테스트")
    void testFindCompletedSessionsByCounselorId() {
        // given
        sessionRepository.save(testSession1);
        sessionRepository.save(testSession3);

        // when
        List<CounselingSession> completedSessions = 
                sessionRepository.findCompletedSessionsByCounselorId(10L);

        // then
        assertThat(completedSessions).hasSize(1);
    }

    // ========== 통계 조회 테스트 ==========

    @Test
    @Order(21)
    @DisplayName("전체 세션 수 조회 테스트")
    void testCountAllSessions() {
        // given
        sessionRepository.save(testSession1);
        sessionRepository.save(testSession2);
        sessionRepository.save(testSession3);

        // when
        long totalCount = sessionRepository.countAllSessions();

        // then
        assertThat(totalCount).isEqualTo(3);
    }

    @Test
    @Order(22)
    @DisplayName("완료된 세션 수 조회 테스트")
    void testCountCompletedSessions() {
        // given
        sessionRepository.save(testSession1);
        sessionRepository.save(testSession2);
        sessionRepository.save(testSession3);

        // when
        long completedCount = sessionRepository.countCompletedSessions();

        // then
        assertThat(completedCount).isEqualTo(2);
    }

    @Test
    @Order(23)
    @DisplayName("진행 중인 세션 수 조회 테스트")
    void testCountInProgressSessions() {
        // given
        sessionRepository.save(testSession1);
        sessionRepository.save(testSession2);

        // when
        long inProgressCount = sessionRepository.countInProgressSessions();

        // then
        assertThat(inProgressCount).isEqualTo(1);
    }

    @Test
    @Order(24)
    @DisplayName("학생의 총 세션 수 조회 테스트")
    void testCountByStudentId() {
        // given
        sessionRepository.save(testSession1);
        sessionRepository.save(testSession2);
        sessionRepository.save(testSession3);

        // when
        long student1Count = sessionRepository.countByStudentId(1L);
        long student2Count = sessionRepository.countByStudentId(2L);

        // then
        assertThat(student1Count).isEqualTo(2);
        assertThat(student2Count).isEqualTo(1);
    }

    @Test
    @Order(25)
    @DisplayName("상담사의 총 세션 수 조회 테스트")
    void testCountByCounselorId() {
        // given
        sessionRepository.save(testSession1);
        sessionRepository.save(testSession2);
        sessionRepository.save(testSession3);

        // when
        long counselor10Count = sessionRepository.countByCounselorId(10L);
        long counselor11Count = sessionRepository.countByCounselorId(11L);

        // then
        assertThat(counselor10Count).isEqualTo(2);
        assertThat(counselor11Count).isEqualTo(1);
    }

    // ========== 평균 통계 테스트 ==========

    @Test
    @Order(26)
    @DisplayName("전체 평균 상담 시간 조회 테스트")
    void testFindAverageSessionDuration() {
        // given
        sessionRepository.save(testSession1);
        sessionRepository.save(testSession3);

        // when
        Double averageDuration = sessionRepository.findAverageSessionDuration();

        // then
        assertThat(averageDuration).isNotNull();
        assertThat(averageDuration).isEqualTo(75.0); // (60 + 90) / 2
    }

    @Test
    @Order(27)
    @DisplayName("상담사별 평균 상담 시간 조회 테스트")
    void testFindAverageSessionDurationByCounselorId() {
        // given
        sessionRepository.save(testSession1);
        sessionRepository.save(testSession3);

        // when
        Double counselor10Average = sessionRepository.findAverageSessionDurationByCounselorId(10L);
        Double counselor11Average = sessionRepository.findAverageSessionDurationByCounselorId(11L);

        // then
        assertThat(counselor10Average).isEqualTo(60.0);
        assertThat(counselor11Average).isEqualTo(90.0);
    }

    // ========== 비즈니스 메서드 테스트 ==========

    @Test
    @Order(28)
    @DisplayName("세션 시작 메서드 테스트")
    void testStartSession() {
        // given
        CounselingSession session = CounselingSession.builder()
                .reservation(testReservation1)
                .sessionNotes("")
                .counselorNotes("")
                .followUpRequired(false)
                .build();
        session = sessionRepository.save(session);

        // when
        session.start();
        CounselingSession updatedSession = sessionRepository.save(session);

        // then
        assertThat(updatedSession.getStartTime()).isNotNull();
        assertThat(updatedSession.isInProgress()).isTrue();
    }

    @Test
    @Order(29)
    @DisplayName("세션 종료 메서드 테스트")
    void testEndSession() {
        // given
        sessionRepository.save(testSession2);

        // when
        testSession2.end();
        CounselingSession updatedSession = sessionRepository.save(testSession2);

        // then
        assertThat(updatedSession.getEndTime()).isNotNull();
        assertThat(updatedSession.getActualDuration()).isNotNull();
        assertThat(updatedSession.isCompleted()).isTrue();
    }

    @Test
    @Order(30)
    @DisplayName("후속 상담 예약 메서드 테스트")
    void testScheduleFollowUp() {
        // given
        CounselingSession session = sessionRepository.save(testSession1);

        // when
        LocalDateTime nextSession = LocalDateTime.now().plusDays(7);
        session.scheduleFollowUp(nextSession);
        CounselingSession updatedSession = sessionRepository.save(session);

        // then
        assertThat(updatedSession.getFollowUpRequired()).isTrue();
        assertThat(updatedSession.getNextSessionDate()).isEqualTo(nextSession);
        assertThat(updatedSession.hasScheduledFollowUp()).isTrue();
    }
}
