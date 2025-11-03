package com.university.scms.domain.counseling.repository;

import com.university.scms.config.JpaConfig;
import com.university.scms.domain.counseling.entity.CounselingReservation;
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
 * CounselingReservationRepository 테스트
 * 실제 MySQL 데이터베이스를 사용하여 테스트합니다.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(JpaConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CounselingReservationRepositoryTest {

    @Autowired
    private CounselingReservationRepository reservationRepository;

    private CounselingReservation testReservation1;
    private CounselingReservation testReservation2;
    private CounselingReservation testReservation3;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        
        // 테스트 예약 1: 학생1의 대기 중 예약
        testReservation1 = CounselingReservation.builder()
                .studentId(1L)
                .counselorId(10L)
                .reservationDate(now.plusDays(3))
                .sessionDuration(60)
                .counselingType("CAREER")
                .requestReason("진로 상담이 필요합니다.")
                .status(CounselingStatus.PENDING)
                .build();

        // 테스트 예약 2: 학생1의 확정 예약
        testReservation2 = CounselingReservation.builder()
                .studentId(1L)
                .counselorId(10L)
                .reservationDate(now.plusDays(5))
                .sessionDuration(90)
                .counselingType("ACADEMIC")
                .requestReason("학업 관련 상담")
                .status(CounselingStatus.CONFIRMED)
                .build();

        // 테스트 예약 3: 학생2의 완료 예약
        testReservation3 = CounselingReservation.builder()
                .studentId(2L)
                .counselorId(11L)
                .reservationDate(now.minusDays(2))
                .sessionDuration(60)
                .counselingType("PSYCHOLOGICAL")
                .requestReason("심리 상담")
                .status(CounselingStatus.COMPLETED)
                .build();
    }

    @AfterEach
    void tearDown() {
        reservationRepository.deleteAll();
    }

    // ========== 기본 CRUD 테스트 ==========

    @Test
    @Order(1)
    @DisplayName("예약 생성 테스트")
    void testCreateReservation() {
        // given & when
        CounselingReservation savedReservation = reservationRepository.save(testReservation1);

        // then
        assertThat(savedReservation.getId()).isNotNull();
        assertThat(savedReservation.getStudentId()).isEqualTo(1L);
        assertThat(savedReservation.getCounselorId()).isEqualTo(10L);
        assertThat(savedReservation.getStatus()).isEqualTo(CounselingStatus.PENDING);
    }

    @Test
    @Order(2)
    @DisplayName("예약 조회 테스트")
    void testFindReservation() {
        // given
        CounselingReservation savedReservation = reservationRepository.save(testReservation1);

        // when
        CounselingReservation foundReservation = reservationRepository.findById(savedReservation.getId()).orElse(null);

        // then
        assertThat(foundReservation).isNotNull();
        assertThat(foundReservation.getRequestReason()).isEqualTo("진로 상담이 필요합니다.");
    }

    @Test
    @Order(3)
    @DisplayName("예약 수정 테스트")
    void testUpdateReservation() {
        // given
        CounselingReservation savedReservation = reservationRepository.save(testReservation1);

        // when
        savedReservation.confirm();
        CounselingReservation updatedReservation = reservationRepository.save(savedReservation);

        // then
        assertThat(updatedReservation.getStatus()).isEqualTo(CounselingStatus.CONFIRMED);
    }

    @Test
    @Order(4)
    @DisplayName("예약 삭제 테스트")
    void testDeleteReservation() {
        // given
        CounselingReservation savedReservation = reservationRepository.save(testReservation1);
        Long reservationId = savedReservation.getId();

        // when
        reservationRepository.delete(savedReservation);

        // then
        assertThat(reservationRepository.findById(reservationId)).isEmpty();
    }

    // ========== 기본 조회 메서드 테스트 ==========

    @Test
    @Order(5)
    @DisplayName("학생 ID로 예약 목록 조회 테스트")
    void testFindByStudentId() {
        // given
        reservationRepository.save(testReservation1);
        reservationRepository.save(testReservation2);
        reservationRepository.save(testReservation3);

        // when
        List<CounselingReservation> student1Reservations = reservationRepository.findByStudentId(1L);

        // then
        assertThat(student1Reservations).hasSize(2);
    }

    @Test
    @Order(6)
    @DisplayName("상담사 ID로 예약 목록 조회 테스트")
    void testFindByCounselorId() {
        // given
        reservationRepository.save(testReservation1);
        reservationRepository.save(testReservation2);
        reservationRepository.save(testReservation3);

        // when
        List<CounselingReservation> counselor10Reservations = reservationRepository.findByCounselorId(10L);

        // then
        assertThat(counselor10Reservations).hasSize(2);
    }

    @Test
    @Order(7)
    @DisplayName("상태별 예약 목록 조회 테스트")
    void testFindByStatus() {
        // given
        reservationRepository.save(testReservation1);
        reservationRepository.save(testReservation2);
        reservationRepository.save(testReservation3);

        // when
        List<CounselingReservation> pendingReservations = reservationRepository.findByStatus(CounselingStatus.PENDING);
        List<CounselingReservation> confirmedReservations = reservationRepository.findByStatus(CounselingStatus.CONFIRMED);

        // then
        assertThat(pendingReservations).hasSize(1);
        assertThat(confirmedReservations).hasSize(1);
    }

    // ========== 학생 관련 조회 테스트 ==========

    @Test
    @Order(8)
    @DisplayName("학생의 상태별 예약 목록 조회 테스트")
    void testFindByStudentIdAndStatus() {
        // given
        reservationRepository.save(testReservation1);
        reservationRepository.save(testReservation2);

        // when
        List<CounselingReservation> pendingReservations = 
                reservationRepository.findByStudentIdAndStatus(1L, CounselingStatus.PENDING);

        // then
        assertThat(pendingReservations).hasSize(1);
        assertThat(pendingReservations.get(0).getCounselingType()).isEqualTo("CAREER");
    }

    @Test
    @Order(9)
    @DisplayName("학생의 특정 기간 예약 목록 조회 테스트")
    void testFindByStudentIdAndDateRange() {
        // given
        reservationRepository.save(testReservation1);
        reservationRepository.save(testReservation2);

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(10);

        // when
        List<CounselingReservation> reservations = 
                reservationRepository.findByStudentIdAndDateRange(1L, startDate, endDate);

        // then
        assertThat(reservations).hasSize(2);
    }

    @Test
    @Order(10)
    @DisplayName("학생의 예약 건수 조회 테스트")
    void testCountByStudentId() {
        // given
        reservationRepository.save(testReservation1);
        reservationRepository.save(testReservation2);
        reservationRepository.save(testReservation3);

        // when
        long student1Count = reservationRepository.countByStudentId(1L);
        long student2Count = reservationRepository.countByStudentId(2L);

        // then
        assertThat(student1Count).isEqualTo(2);
        assertThat(student2Count).isEqualTo(1);
    }

    @Test
    @Order(11)
    @DisplayName("학생의 상태별 예약 건수 조회 테스트")
    void testCountByStudentIdAndStatus() {
        // given
        reservationRepository.save(testReservation1);
        reservationRepository.save(testReservation2);

        // when
        long pendingCount = reservationRepository.countByStudentIdAndStatus(1L, CounselingStatus.PENDING);
        long confirmedCount = reservationRepository.countByStudentIdAndStatus(1L, CounselingStatus.CONFIRMED);

        // then
        assertThat(pendingCount).isEqualTo(1);
        assertThat(confirmedCount).isEqualTo(1);
    }

    // ========== 상담사 관련 조회 테스트 ==========

    @Test
    @Order(12)
    @DisplayName("상담사의 상태별 예약 목록 조회 테스트")
    void testFindByCounselorIdAndStatus() {
        // given
        reservationRepository.save(testReservation1);
        reservationRepository.save(testReservation2);

        // when
        List<CounselingReservation> confirmedReservations = 
                reservationRepository.findByCounselorIdAndStatus(10L, CounselingStatus.CONFIRMED);

        // then
        assertThat(confirmedReservations).hasSize(1);
        assertThat(confirmedReservations.get(0).getCounselingType()).isEqualTo("ACADEMIC");
    }

    @Test
    @Order(13)
    @DisplayName("상담사의 특정 기간 예약 목록 조회 테스트")
    void testFindByCounselorIdAndDateRange() {
        // given
        reservationRepository.save(testReservation1);
        reservationRepository.save(testReservation2);

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(10);

        // when
        List<CounselingReservation> reservations = 
                reservationRepository.findByCounselorIdAndDateRange(10L, startDate, endDate);

        // then
        assertThat(reservations).hasSize(2);
    }

    @Test
    @Order(14)
    @DisplayName("상담사의 예약 건수 조회 테스트")
    void testCountByCounselorId() {
        // given
        reservationRepository.save(testReservation1);
        reservationRepository.save(testReservation2);
        reservationRepository.save(testReservation3);

        // when
        long counselor10Count = reservationRepository.countByCounselorId(10L);
        long counselor11Count = reservationRepository.countByCounselorId(11L);

        // then
        assertThat(counselor10Count).isEqualTo(2);
        assertThat(counselor11Count).isEqualTo(1);
    }

    // ========== 날짜 기반 조회 테스트 ==========

    @Test
    @Order(15)
    @DisplayName("특정 날짜 이후의 예약 목록 조회 테스트")
    void testFindByReservationDateAfter() {
        // given
        reservationRepository.save(testReservation1);
        reservationRepository.save(testReservation2);
        reservationRepository.save(testReservation3);

        // when
        List<CounselingReservation> futureReservations = 
                reservationRepository.findByReservationDateAfter(LocalDateTime.now());

        // then
        assertThat(futureReservations).hasSize(2);
    }

    @Test
    @Order(16)
    @DisplayName("특정 날짜 이전의 예약 목록 조회 테스트")
    void testFindByReservationDateBefore() {
        // given
        reservationRepository.save(testReservation1);
        reservationRepository.save(testReservation2);
        reservationRepository.save(testReservation3);

        // when
        List<CounselingReservation> pastReservations = 
                reservationRepository.findByReservationDateBefore(LocalDateTime.now());

        // then
        assertThat(pastReservations).hasSize(1);
    }

    @Test
    @Order(17)
    @DisplayName("특정 기간의 예약 목록 조회 테스트")
    void testFindByDateRange() {
        // given
        reservationRepository.save(testReservation1);
        reservationRepository.save(testReservation2);
        reservationRepository.save(testReservation3);

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(10);

        // when
        List<CounselingReservation> reservations = 
                reservationRepository.findByDateRange(startDate, endDate);

        // then
        assertThat(reservations).hasSize(2);
    }

    // ========== 상담 유형별 조회 테스트 ==========

    @Test
    @Order(18)
    @DisplayName("상담 유형별 예약 목록 조회 테스트")
    void testFindByCounselingType() {
        // given
        reservationRepository.save(testReservation1);
        reservationRepository.save(testReservation2);
        reservationRepository.save(testReservation3);

        // when
        List<CounselingReservation> careerReservations = 
                reservationRepository.findByCounselingType("CAREER");

        // then
        assertThat(careerReservations).hasSize(1);
        assertThat(careerReservations.get(0).getRequestReason()).isEqualTo("진로 상담이 필요합니다.");
    }

    @Test
    @Order(19)
    @DisplayName("상담 유형별 예약 건수 조회 테스트")
    void testCountByCounselingType() {
        // given
        reservationRepository.save(testReservation1);
        reservationRepository.save(testReservation2);
        reservationRepository.save(testReservation3);

        // when
        long careerCount = reservationRepository.countByCounselingType("CAREER");
        long academicCount = reservationRepository.countByCounselingType("ACADEMIC");

        // then
        assertThat(careerCount).isEqualTo(1);
        assertThat(academicCount).isEqualTo(1);
    }

    // ========== 복합 조회 테스트 ==========

    @Test
    @Order(20)
    @DisplayName("특정 날짜에 특정 상담사의 예약 목록 조회 테스트")
    void testFindByCounselorIdAndDate() {
        // given
        LocalDateTime targetDate = LocalDateTime.now().plusDays(3);
        testReservation1 = CounselingReservation.builder()
                .studentId(1L)
                .counselorId(10L)
                .reservationDate(targetDate)
                .sessionDuration(60)
                .counselingType("CAREER")
                .requestReason("진로 상담")
                .status(CounselingStatus.PENDING)
                .build();
        reservationRepository.save(testReservation1);

        // when
        List<CounselingReservation> reservations = 
                reservationRepository.findByCounselorIdAndDate(10L, targetDate);

        // then
        assertThat(reservations).isNotEmpty();
    }

    @Test
    @Order(21)
    @DisplayName("임박한 예약 목록 조회 테스트")
    void testFindImminentReservations() {
        // given
        LocalDateTime now = LocalDateTime.now();
        CounselingReservation imminentReservation = CounselingReservation.builder()
                .studentId(1L)
                .counselorId(10L)
                .reservationDate(now.plusHours(12))
                .sessionDuration(60)
                .counselingType("CAREER")
                .requestReason("임박 상담")
                .status(CounselingStatus.CONFIRMED)
                .build();
        reservationRepository.save(imminentReservation);

        // when
        List<CounselingReservation> imminentReservations = 
                reservationRepository.findImminentReservations(now, now.plusHours(24));

        // then
        assertThat(imminentReservations).hasSize(1);
    }

    @Test
    @Order(22)
    @DisplayName("과거 완료되지 않은 예약 목록 조회 테스트")
    void testFindPastIncompleteReservations() {
        // given
        LocalDateTime pastDate = LocalDateTime.now().minusDays(1);
        CounselingReservation pastIncompleteReservation = CounselingReservation.builder()
                .studentId(1L)
                .counselorId(10L)
                .reservationDate(pastDate)
                .sessionDuration(60)
                .counselingType("CAREER")
                .requestReason("과거 상담")
                .status(CounselingStatus.CONFIRMED)
                .build();
        reservationRepository.save(pastIncompleteReservation);

        // when
        List<CounselingReservation> pastIncomplete = 
                reservationRepository.findPastIncompleteReservations(LocalDateTime.now());

        // then
        assertThat(pastIncomplete).hasSize(1);
    }

    // ========== 통계 조회 테스트 ==========

    @Test
    @Order(23)
    @DisplayName("전체 예약 건수 조회 테스트")
    void testCountAllReservations() {
        // given
        reservationRepository.save(testReservation1);
        reservationRepository.save(testReservation2);
        reservationRepository.save(testReservation3);

        // when
        long totalCount = reservationRepository.countAllReservations();

        // then
        assertThat(totalCount).isEqualTo(3);
    }

    @Test
    @Order(24)
    @DisplayName("상태별 예약 건수 조회 테스트")
    void testCountByStatus() {
        // given
        reservationRepository.save(testReservation1);
        reservationRepository.save(testReservation2);
        reservationRepository.save(testReservation3);

        // when
        long pendingCount = reservationRepository.countByStatus(CounselingStatus.PENDING);
        long confirmedCount = reservationRepository.countByStatus(CounselingStatus.CONFIRMED);
        long completedCount = reservationRepository.countByStatus(CounselingStatus.COMPLETED);

        // then
        assertThat(pendingCount).isEqualTo(1);
        assertThat(confirmedCount).isEqualTo(1);
        assertThat(completedCount).isEqualTo(1);
    }

    // ========== 존재 여부 확인 테스트 ==========

    @Test
    @Order(25)
    @DisplayName("학생의 특정 시간 예약 존재 여부 확인 테스트")
    void testExistsByStudentIdAndReservationDate() {
        // given
        LocalDateTime reservationDate = LocalDateTime.now().plusDays(3);
        testReservation1.update(reservationDate, 60, "CAREER", "진로 상담");
        reservationRepository.save(testReservation1);

        // when
        boolean exists = reservationRepository.existsByStudentIdAndReservationDate(1L, reservationDate);
        boolean notExists = reservationRepository.existsByStudentIdAndReservationDate(1L, LocalDateTime.now().plusDays(10));

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @Order(26)
    @DisplayName("상담사의 특정 시간 예약 존재 여부 확인 테스트")
    void testExistsByCounselorIdAndReservationDate() {
        // given
        LocalDateTime reservationDate = LocalDateTime.now().plusDays(3);
        testReservation1.update(reservationDate, 60, "CAREER", "진로 상담");
        reservationRepository.save(testReservation1);

        // when
        boolean exists = reservationRepository.existsByCounselorIdAndReservationDate(10L, reservationDate);
        boolean notExists = reservationRepository.existsByCounselorIdAndReservationDate(10L, LocalDateTime.now().plusDays(10));

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    // ========== 비즈니스 메서드 테스트 ==========

    @Test
    @Order(27)
    @DisplayName("예약 확정 메서드 테스트")
    void testConfirmReservation() {
        // given
        CounselingReservation reservation = reservationRepository.save(testReservation1);

        // when
        reservation.confirm();
        CounselingReservation updatedReservation = reservationRepository.save(reservation);

        // then
        assertThat(updatedReservation.getStatus()).isEqualTo(CounselingStatus.CONFIRMED);
        assertThat(updatedReservation.isConfirmed()).isTrue();
    }

    @Test
    @Order(28)
    @DisplayName("예약 취소 메서드 테스트")
    void testCancelReservation() {
        // given
        CounselingReservation reservation = reservationRepository.save(testReservation1);

        // when
        reservation.cancel("학생 사유로 취소");
        CounselingReservation updatedReservation = reservationRepository.save(reservation);

        // then
        assertThat(updatedReservation.getStatus()).isEqualTo(CounselingStatus.CANCELLED);
        assertThat(updatedReservation.isCancelled()).isTrue();
    }

    @Test
    @Order(29)
    @DisplayName("예약 완료 처리 메서드 테스트")
    void testCompleteReservation() {
        // given
        testReservation2 = reservationRepository.save(testReservation2);

        // when
        testReservation2.complete();
        CounselingReservation updatedReservation = reservationRepository.save(testReservation2);

        // then
        assertThat(updatedReservation.getStatus()).isEqualTo(CounselingStatus.COMPLETED);
        assertThat(updatedReservation.isCompleted()).isTrue();
    }

    @Test
    @Order(30)
    @DisplayName("예약 취소 가능 여부 확인 테스트")
    void testCanCancel() {
        // given
        LocalDateTime futureDate = LocalDateTime.now().plusDays(2);
        testReservation1.update(futureDate, 60, "CAREER", "진로 상담");
        CounselingReservation reservation = reservationRepository.save(testReservation1);

        // when & then
        assertThat(reservation.canCancel()).isTrue();
    }
}
