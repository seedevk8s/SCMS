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
 * ProgramApplicationRepository 테스트
 * 실제 MySQL 데이터베이스를 사용하여 테스트합니다.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(JpaConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProgramApplicationRepositoryTest {

    @Autowired
    private ProgramApplicationRepository applicationRepository;

    @Autowired
    private ProgramRepository programRepository;

    private Program testProgram;
    private ProgramApplication pendingApplication;
    private ProgramApplication approvedApplication;
    private ProgramApplication rejectedApplication;

    @BeforeEach
    void setUp() {
        // 테스트용 프로그램 생성
        testProgram = Program.builder()
                .title("Spring Boot 실전 워크샵")
                .description("Spring Boot를 활용한 웹 개발 실전")
                .category("개발")
                .organizerId(100L)
                .location("공학관 401호")
                .capacity(30)
                .currentParticipants(0)
                .startDate(LocalDateTime.now().plusDays(7))
                .endDate(LocalDateTime.now().plusDays(14))
                .applicationStart(LocalDateTime.now().minusDays(1))
                .applicationEnd(LocalDateTime.now().plusDays(5))
                .mileagePoints(50)
                .status(ProgramStatus.OPEN)
                .build();
        testProgram = programRepository.save(testProgram);

        // 대기 중인 신청
        pendingApplication = ProgramApplication.builder()
                .program(testProgram)
                .userId(1001L)
                .status(ApplicationStatus.PENDING)
                .applicationDate(LocalDateTime.now())
                .motivation("Spring Boot를 깊이 있게 배우고 싶습니다.")
                .build();

        // 승인된 신청
        approvedApplication = ProgramApplication.builder()
                .program(testProgram)
                .userId(1002L)
                .status(ApplicationStatus.APPROVED)
                .applicationDate(LocalDateTime.now().minusDays(1))
                .motivation("웹 개발 실력을 향상시키고 싶습니다.")
                .reviewedBy(200L)
                .reviewedAt(LocalDateTime.now())
                .build();

        // 거부된 신청
        rejectedApplication = ProgramApplication.builder()
                .program(testProgram)
                .userId(1003L)
                .status(ApplicationStatus.REJECTED)
                .applicationDate(LocalDateTime.now().minusDays(2))
                .motivation("관심있습니다.")
                .reviewedBy(200L)
                .reviewedAt(LocalDateTime.now().minusDays(1))
                .rejectionReason("참가 동기가 불충분합니다.")
                .build();
    }

    @AfterEach
    void tearDown() {
        applicationRepository.deleteAll();
        programRepository.deleteAll();
    }

    // ========== 기본 CRUD 테스트 ==========

    @Test
    @Order(1)
    @DisplayName("신청 생성 테스트")
    void testCreateApplication() {
        // when
        ProgramApplication saved = applicationRepository.save(pendingApplication);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getProgram().getTitle()).isEqualTo("Spring Boot 실전 워크샵");
        assertThat(saved.getUserId()).isEqualTo(1001L);
        assertThat(saved.getStatus()).isEqualTo(ApplicationStatus.PENDING);
    }

    @Test
    @Order(2)
    @DisplayName("신청 조회 테스트")
    void testFindApplication() {
        // given
        ProgramApplication saved = applicationRepository.save(pendingApplication);

        // when
        Optional<ProgramApplication> found = applicationRepository.findById(saved.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo(1001L);
    }

    @Test
    @Order(3)
    @DisplayName("신청 수정 테스트")
    void testUpdateApplication() {
        // given
        ProgramApplication saved = applicationRepository.save(pendingApplication);

        // when
        saved.approve(200L);
        ProgramApplication updated = applicationRepository.save(saved);

        // then
        assertThat(updated.getStatus()).isEqualTo(ApplicationStatus.APPROVED);
        assertThat(updated.getReviewedBy()).isEqualTo(200L);
        assertThat(updated.getReviewedAt()).isNotNull();
    }

    @Test
    @Order(4)
    @DisplayName("신청 삭제 테스트")
    void testDeleteApplication() {
        // given
        ProgramApplication saved = applicationRepository.save(pendingApplication);
        Long id = saved.getId();

        // when
        applicationRepository.delete(saved);

        // then
        assertThat(applicationRepository.findById(id)).isEmpty();
    }

    // ========== 기본 조회 메서드 테스트 ==========

    @Test
    @Order(5)
    @DisplayName("프로그램과 사용자로 신청 조회")
    void testFindByProgramAndUserId() {
        // given
        applicationRepository.save(pendingApplication);

        // when
        Optional<ProgramApplication> found = 
                applicationRepository.findByProgramAndUserId(testProgram, 1001L);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getMotivation()).contains("깊이 있게");
    }

    @Test
    @Order(6)
    @DisplayName("프로그램 ID와 사용자 ID로 신청 조회")
    void testFindByProgramIdAndUserId() {
        // given
        applicationRepository.save(pendingApplication);

        // when
        Optional<ProgramApplication> found = 
                applicationRepository.findByProgramIdAndUserId(testProgram.getId(), 1001L);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo(1001L);
    }

    @Test
    @Order(7)
    @DisplayName("신청 ID 목록으로 조회")
    void testFindByIdIn() {
        // given
        ProgramApplication app1 = applicationRepository.save(pendingApplication);
        ProgramApplication app2 = applicationRepository.save(approvedApplication);

        // when
        List<ProgramApplication> found = 
                applicationRepository.findByIdIn(List.of(app1.getId(), app2.getId()));

        // then
        assertThat(found).hasSize(2);
    }

    // ========== 프로그램별 신청 조회 테스트 ==========

    @Test
    @Order(8)
    @DisplayName("프로그램의 모든 신청 조회")
    void testFindByProgram() {
        // given
        applicationRepository.save(pendingApplication);
        applicationRepository.save(approvedApplication);
        applicationRepository.save(rejectedApplication);

        // when
        List<ProgramApplication> applications = 
                applicationRepository.findByProgram(testProgram);

        // then
        assertThat(applications).hasSize(3);
    }

    @Test
    @Order(9)
    @DisplayName("프로그램 ID로 신청 목록 조회")
    void testFindByProgramId() {
        // given
        applicationRepository.save(pendingApplication);
        applicationRepository.save(approvedApplication);

        // when
        List<ProgramApplication> applications = 
                applicationRepository.findByProgramId(testProgram.getId());

        // then
        assertThat(applications).hasSize(2);
    }

    @Test
    @Order(10)
    @DisplayName("프로그램의 특정 상태 신청 조회")
    void testFindByProgramAndStatus() {
        // given
        applicationRepository.save(pendingApplication);
        applicationRepository.save(approvedApplication);

        // when
        List<ProgramApplication> approved = 
                applicationRepository.findByProgramAndStatus(testProgram, ApplicationStatus.APPROVED);

        // then
        assertThat(approved).hasSize(1);
        assertThat(approved.get(0).getUserId()).isEqualTo(1002L);
    }

    // ========== 사용자별 신청 조회 테스트 ==========

    @Test
    @Order(11)
    @DisplayName("사용자의 모든 신청 조회")
    void testFindByUserId() {
        // given
        applicationRepository.save(pendingApplication);

        // when
        List<ProgramApplication> applications = 
                applicationRepository.findByUserId(1001L);

        // then
        assertThat(applications).hasSize(1);
        assertThat(applications.get(0).getStatus()).isEqualTo(ApplicationStatus.PENDING);
    }

    @Test
    @Order(12)
    @DisplayName("사용자의 특정 상태 신청 조회")
    void testFindByUserIdAndStatus() {
        // given
        applicationRepository.save(pendingApplication);
        applicationRepository.save(approvedApplication);

        // when
        List<ProgramApplication> approved = 
                applicationRepository.findByUserIdAndStatus(1002L, ApplicationStatus.APPROVED);

        // then
        assertThat(approved).hasSize(1);
    }

    @Test
    @Order(13)
    @DisplayName("사용자의 신청 목록 신청일 기준 내림차순 조회")
    void testFindByUserIdOrderByApplicationDateDesc() {
        // given
        applicationRepository.save(pendingApplication);

        // when
        List<ProgramApplication> applications = 
                applicationRepository.findByUserIdOrderByApplicationDateDesc(1001L);

        // then
        assertThat(applications).hasSize(1);
        assertThat(applications.get(0).getUserId()).isEqualTo(1001L);
    }

    // ========== 상태별 조회 테스트 ==========

    @Test
    @Order(14)
    @DisplayName("특정 상태의 신청 목록 조회")
    void testFindByStatus() {
        // given
        applicationRepository.save(pendingApplication);
        applicationRepository.save(approvedApplication);
        applicationRepository.save(rejectedApplication);

        // when
        List<ProgramApplication> pending = 
                applicationRepository.findByStatus(ApplicationStatus.PENDING);

        // then
        assertThat(pending).hasSize(1);
    }

    @Test
    @Order(15)
    @DisplayName("대기 중인 신청 목록 조회")
    void testFindPendingApplicationsOrderByApplicationDate() {
        // given
        applicationRepository.save(pendingApplication);

        // when
        List<ProgramApplication> pending = 
                applicationRepository.findPendingApplicationsOrderByApplicationDate();

        // then
        assertThat(pending).hasSize(1);
        assertThat(pending.get(0).getStatus()).isEqualTo(ApplicationStatus.PENDING);
    }

    @Test
    @Order(16)
    @DisplayName("승인된 신청 목록 조회")
    void testFindApprovedApplications() {
        // given
        applicationRepository.save(approvedApplication);

        // when
        List<ProgramApplication> approved = 
                applicationRepository.findApprovedApplications();

        // then
        assertThat(approved).hasSize(1);
        assertThat(approved.get(0).isApproved()).isTrue();
    }

    // ========== 날짜 기반 조회 테스트 ==========

    @Test
    @Order(17)
    @DisplayName("특정 기간의 신청 조회")
    void testFindByApplicationDateBetween() {
        // given
        applicationRepository.save(pendingApplication);
        applicationRepository.save(approvedApplication);

        // when
        LocalDateTime start = LocalDateTime.now().minusDays(3);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        List<ProgramApplication> applications = 
                applicationRepository.findByApplicationDateBetween(start, end);

        // then
        assertThat(applications).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @Order(18)
    @DisplayName("특정 날짜 이후의 신청 조회")
    void testFindByApplicationDateAfter() {
        // given
        applicationRepository.save(pendingApplication);

        // when
        List<ProgramApplication> applications = 
                applicationRepository.findByApplicationDateAfter(LocalDateTime.now().minusHours(1));

        // then
        assertThat(applications).hasSize(1);
    }

    // ========== 검토 관련 조회 테스트 ==========

    @Test
    @Order(19)
    @DisplayName("특정 검토자가 처리한 신청 조회")
    void testFindByReviewedBy() {
        // given
        applicationRepository.save(approvedApplication);
        applicationRepository.save(rejectedApplication);

        // when
        List<ProgramApplication> reviewed = 
                applicationRepository.findByReviewedBy(200L);

        // then
        assertThat(reviewed).hasSize(2);
    }

    @Test
    @Order(20)
    @DisplayName("검토된 신청 목록 조회")
    void testFindReviewedApplications() {
        // given
        applicationRepository.save(pendingApplication);
        applicationRepository.save(approvedApplication);

        // when
        List<ProgramApplication> reviewed = 
                applicationRepository.findReviewedApplications();

        // then
        assertThat(reviewed).hasSize(1);
        assertThat(reviewed.get(0).getReviewedBy()).isNotNull();
    }

    @Test
    @Order(21)
    @DisplayName("미검토 신청 목록 조회")
    void testFindUnreviewedApplications() {
        // given
        applicationRepository.save(pendingApplication);
        applicationRepository.save(approvedApplication);

        // when
        List<ProgramApplication> unreviewed = 
                applicationRepository.findUnreviewedApplications();

        // then
        assertThat(unreviewed).hasSize(1);
        assertThat(unreviewed.get(0).getReviewedBy()).isNull();
    }

    // ========== 통계 및 집계 메서드 테스트 ==========

    @Test
    @Order(22)
    @DisplayName("프로그램의 신청 수 조회")
    void testCountByProgram() {
        // given
        applicationRepository.save(pendingApplication);
        applicationRepository.save(approvedApplication);

        // when
        long count = applicationRepository.countByProgram(testProgram);

        // then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @Order(23)
    @DisplayName("프로그램 ID로 신청 수 조회")
    void testCountByProgramId() {
        // given
        applicationRepository.save(pendingApplication);

        // when
        long count = applicationRepository.countByProgramId(testProgram.getId());

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    @Order(24)
    @DisplayName("프로그램의 특정 상태 신청 수 조회")
    void testCountByProgramAndStatus() {
        // given
        applicationRepository.save(pendingApplication);
        applicationRepository.save(approvedApplication);

        // when
        long pendingCount = applicationRepository.countByProgramAndStatus(testProgram, ApplicationStatus.PENDING);

        // then
        assertThat(pendingCount).isEqualTo(1);
    }

    @Test
    @Order(25)
    @DisplayName("사용자의 신청 수 조회")
    void testCountByUserId() {
        // given
        applicationRepository.save(pendingApplication);

        // when
        long count = applicationRepository.countByUserId(1001L);

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    @Order(26)
    @DisplayName("대기 중인 신청 수 조회")
    void testCountPendingApplications() {
        // given
        applicationRepository.save(pendingApplication);
        applicationRepository.save(approvedApplication);

        // when
        long pendingCount = applicationRepository.countPendingApplications();

        // then
        assertThat(pendingCount).isEqualTo(1);
    }

    // ========== 존재 여부 확인 테스트 ==========

    @Test
    @Order(27)
    @DisplayName("프로그램과 사용자의 신청 존재 여부 확인")
    void testExistsByProgramAndUserId() {
        // given
        applicationRepository.save(pendingApplication);

        // when & then
        assertThat(applicationRepository.existsByProgramAndUserId(testProgram, 1001L)).isTrue();
        assertThat(applicationRepository.existsByProgramAndUserId(testProgram, 9999L)).isFalse();
    }

    @Test
    @Order(28)
    @DisplayName("프로그램 ID와 사용자 ID로 신청 존재 여부 확인")
    void testExistsByProgramIdAndUserId() {
        // given
        applicationRepository.save(pendingApplication);

        // when & then
        assertThat(applicationRepository.existsByProgramIdAndUserId(testProgram.getId(), 1001L)).isTrue();
        assertThat(applicationRepository.existsByProgramIdAndUserId(testProgram.getId(), 9999L)).isFalse();
    }

    // ========== 비즈니스 메서드 테스트 ==========

    @Test
    @Order(29)
    @DisplayName("신청 승인 메서드 테스트")
    void testApproveApplication() {
        // given
        ProgramApplication saved = applicationRepository.save(pendingApplication);

        // when
        saved.approve(200L);
        ProgramApplication updated = applicationRepository.save(saved);

        // then
        assertThat(updated.isApproved()).isTrue();
        assertThat(updated.getReviewedBy()).isEqualTo(200L);
        assertThat(updated.getReviewedAt()).isNotNull();
    }

    @Test
    @Order(30)
    @DisplayName("신청 거부 메서드 테스트")
    void testRejectApplication() {
        // given
        ProgramApplication saved = applicationRepository.save(pendingApplication);

        // when
        saved.reject(200L, "정원 초과");
        ProgramApplication updated = applicationRepository.save(saved);

        // then
        assertThat(updated.isRejected()).isTrue();
        assertThat(updated.getRejectionReason()).isEqualTo("정원 초과");
    }

    @Test
    @Order(31)
    @DisplayName("신청 취소 메서드 테스트")
    void testCancelApplication() {
        // given
        ProgramApplication saved = applicationRepository.save(pendingApplication);

        // when
        saved.cancel();
        ProgramApplication updated = applicationRepository.save(saved);

        // then
        assertThat(updated.isCancelled()).isTrue();
    }
}
