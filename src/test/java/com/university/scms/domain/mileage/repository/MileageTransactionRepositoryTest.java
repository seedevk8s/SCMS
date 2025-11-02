package com.university.scms.domain.mileage.repository;

import com.university.scms.config.JpaConfig;
import com.university.scms.domain.entity.MileageAccount;
import com.university.scms.domain.entity.MileageTransaction;
import com.university.scms.domain.entity.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MileageTransactionRepository 테스트
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaConfig.class)
class MileageTransactionRepositoryTest {

    @Autowired
    private MileageTransactionRepository transactionRepository;

    @Autowired
    private MileageAccountRepository accountRepository;

    private MileageAccount testAccount;
    private Long testUserId;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();

        // 테스트 데이터 설정
        testUserId = 1000L;
        testAccount = MileageAccount.create(testUserId);
        testAccount = accountRepository.save(testAccount);
    }

    @Test
    @DisplayName("계정별 거래 내역 조회 테스트")
    void findByAccountIdOrderByCreatedAtDesc() {
        // given
        MileageTransaction tx1 = MileageTransaction.createEarn(testAccount, testUserId, 1000, "PROGRAM", 1L, "프로그램 참여");
        MileageTransaction tx2 = MileageTransaction.createUse(testAccount, testUserId, 500, "PROGRAM", 2L, "마일리지 사용");
        transactionRepository.saveAll(List.of(tx1, tx2));

        // when
        List<MileageTransaction> result = transactionRepository.findByAccountIdOrderByCreatedAtDesc(testAccount.getId());

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTransactionType()).isIn(TransactionType.EARN, TransactionType.USE);
    }

    @Test
    @DisplayName("사용자별 거래 내역 조회 테스트")
    void findByUserIdOrderByCreatedAtDesc() {
        // given
        MileageTransaction tx1 = MileageTransaction.createEarn(testAccount, testUserId, 1000, "PROGRAM", 1L, "프로그램 참여");
        MileageTransaction tx2 = MileageTransaction.createEarn(testAccount, testUserId, 500, "CERTIFICATION", 1L, "자격증 취득");
        transactionRepository.saveAll(List.of(tx1, tx2));

        // when
        List<MileageTransaction> result = transactionRepository.findByUserIdOrderByCreatedAtDesc(testUserId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(MileageTransaction::getPoints)
                .containsExactlyInAnyOrder(1000, 500);
    }

    @Test
    @DisplayName("거래 유형별 조회 테스트")
    void findByTransactionType() {
        // given
        MileageTransaction tx1 = MileageTransaction.createEarn(testAccount, testUserId, 1000, "PROGRAM", 1L, "적립");
        MileageTransaction tx2 = MileageTransaction.createUse(testAccount, testUserId, 500, "PROGRAM", 2L, "사용");
        MileageTransaction tx3 = MileageTransaction.createEarn(testAccount, testUserId, 800, "CERTIFICATION", 1L, "적립2");
        transactionRepository.saveAll(List.of(tx1, tx2, tx3));

        // when
        List<MileageTransaction> earnTxs = transactionRepository.findByTransactionType(TransactionType.EARN);
        List<MileageTransaction> useTxs = transactionRepository.findByTransactionType(TransactionType.USE);

        // then
        assertThat(earnTxs).hasSize(2);
        assertThat(useTxs).hasSize(1);
    }

    @Test
    @DisplayName("출처별 거래 조회 테스트")
    void findBySourceTypeAndSourceId() {
        // given
        MileageTransaction tx1 = MileageTransaction.createEarn(testAccount, testUserId, 1000, "PROGRAM", 1L, "프로그램1");
        MileageTransaction tx2 = MileageTransaction.createEarn(testAccount, testUserId, 500, "PROGRAM", 2L, "프로그램2");
        MileageTransaction tx3 = MileageTransaction.createEarn(testAccount, testUserId, 800, "CERTIFICATION", 1L, "자격증");
        transactionRepository.saveAll(List.of(tx1, tx2, tx3));

        // when
        List<MileageTransaction> programTxs = transactionRepository.findBySourceTypeAndSourceId("PROGRAM", 1L);
        List<MileageTransaction> certTxs = transactionRepository.findBySourceTypeAndSourceId("CERTIFICATION", 1L);

        // then
        assertThat(programTxs).hasSize(1);
        assertThat(programTxs.get(0).getPoints()).isEqualTo(1000);
        assertThat(certTxs).hasSize(1);
        assertThat(certTxs.get(0).getPoints()).isEqualTo(800);
    }

    @Test
    @DisplayName("기간별 거래 조회 테스트")
    void findByCreatedAtBetween() {
        // given
        MileageTransaction tx1 = MileageTransaction.createEarn(testAccount, testUserId, 1000, "PROGRAM", 1L, "거래1");
        transactionRepository.save(tx1);

        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        // when
        List<MileageTransaction> result = transactionRepository.findByCreatedAtBetween(start, end);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getPoints()).isEqualTo(1000);
    }

    @Test
    @DisplayName("총 적립 포인트 계산 테스트")
    void getTotalEarnedPoints() {
        // given
        MileageTransaction tx1 = MileageTransaction.createEarn(testAccount, testUserId, 1000, "PROGRAM", 1L, "적립1");
        MileageTransaction tx2 = MileageTransaction.createEarn(testAccount, testUserId, 500, "CERTIFICATION", 1L, "적립2");
        MileageTransaction tx3 = MileageTransaction.createUse(testAccount, testUserId, 200, "PROGRAM", 2L, "사용");
        transactionRepository.saveAll(List.of(tx1, tx2, tx3));

        // when
        Integer total = transactionRepository.getTotalEarnedPoints(testUserId);

        // then
        assertThat(total).isEqualTo(1500);
    }

    @Test
    @DisplayName("총 사용 포인트 계산 테스트")
    void getTotalUsedPoints() {
        // given
        MileageTransaction tx1 = MileageTransaction.createEarn(testAccount, testUserId, 2000, "PROGRAM", 1L, "적립");
        MileageTransaction tx2 = MileageTransaction.createUse(testAccount, testUserId, 500, "PROGRAM", 2L, "사용1");
        MileageTransaction tx3 = MileageTransaction.createUse(testAccount, testUserId, 300, "PROGRAM", 3L, "사용2");
        transactionRepository.saveAll(List.of(tx1, tx2, tx3));

        // when
        Integer total = transactionRepository.getTotalUsedPoints(testUserId);

        // then
        assertThat(total).isEqualTo(800);
    }

    @Test
    @DisplayName("거래 건수 조회 테스트")
    void countByUserId() {
        // given
        MileageTransaction tx1 = MileageTransaction.createEarn(testAccount, testUserId, 1000, "PROGRAM", 1L, "거래1");
        MileageTransaction tx2 = MileageTransaction.createEarn(testAccount, testUserId, 500, "PROGRAM", 2L, "거래2");
        MileageTransaction tx3 = MileageTransaction.createUse(testAccount, testUserId, 200, "PROGRAM", 3L, "거래3");
        transactionRepository.saveAll(List.of(tx1, tx2, tx3));

        // when
        long count = transactionRepository.countByUserId(testUserId);

        // then
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("최근 거래 조회 테스트")
    void findTop10ByUserIdOrderByCreatedAtDesc() {
        // given
        for (int i = 1; i <= 15; i++) {
            MileageTransaction tx = MileageTransaction.createEarn(testAccount, testUserId, i * 100, "PROGRAM", (long) i, "거래" + i);
            transactionRepository.save(tx);
        }

        // when
        List<MileageTransaction> result = transactionRepository.findTop10ByUserIdOrderByCreatedAtDesc(testUserId);

        // then
        assertThat(result).hasSize(10);
    }

    @Test
    @DisplayName("설명 키워드 검색 테스트")
    void findByDescriptionContaining() {
        // given
        MileageTransaction tx1 = MileageTransaction.createEarn(testAccount, testUserId, 1000, "PROGRAM", 1L, "AI 프로그램 참여");
        MileageTransaction tx2 = MileageTransaction.createEarn(testAccount, testUserId, 500, "PROGRAM", 2L, "블록체인 세미나");
        MileageTransaction tx3 = MileageTransaction.createEarn(testAccount, testUserId, 800, "PROGRAM", 3L, "AI 워크샵");
        transactionRepository.saveAll(List.of(tx1, tx2, tx3));

        // when
        List<MileageTransaction> result = transactionRepository.findByDescriptionContaining("AI");

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(MileageTransaction::getDescription)
                .allMatch(desc -> desc.contains("AI"));
    }

    @Test
    @DisplayName("계정의 특정 유형 거래 조회 테스트")
    void findByAccountIdAndTransactionTypeOrderByCreatedAtDesc() {
        // given
        MileageTransaction tx1 = MileageTransaction.createEarn(testAccount, testUserId, 1000, "PROGRAM", 1L, "적립1");
        MileageTransaction tx2 = MileageTransaction.createEarn(testAccount, testUserId, 500, "PROGRAM", 2L, "적립2");
        MileageTransaction tx3 = MileageTransaction.createUse(testAccount, testUserId, 200, "PROGRAM", 3L, "사용");
        transactionRepository.saveAll(List.of(tx1, tx2, tx3));

        // when
        List<MileageTransaction> earnTxs = transactionRepository.findByAccountIdAndTransactionTypeOrderByCreatedAtDesc(
                testAccount.getId(), TransactionType.EARN);

        // then
        assertThat(earnTxs).hasSize(2);
        assertThat(earnTxs).allMatch(tx -> tx.getTransactionType() == TransactionType.EARN);
    }

    @Test
    @DisplayName("사용자의 특정 유형 거래 조회 테스트")
    void findByUserIdAndTransactionTypeOrderByCreatedAtDesc() {
        // given
        MileageTransaction tx1 = MileageTransaction.createEarn(testAccount, testUserId, 1000, "PROGRAM", 1L, "적립");
        MileageTransaction tx2 = MileageTransaction.createUse(testAccount, testUserId, 500, "PROGRAM", 2L, "사용1");
        MileageTransaction tx3 = MileageTransaction.createUse(testAccount, testUserId, 200, "PROGRAM", 3L, "사용2");
        transactionRepository.saveAll(List.of(tx1, tx2, tx3));

        // when
        List<MileageTransaction> useTxs = transactionRepository.findByUserIdAndTransactionTypeOrderByCreatedAtDesc(
                testUserId, TransactionType.USE);

        // then
        assertThat(useTxs).hasSize(2);
        assertThat(useTxs).allMatch(tx -> tx.getTransactionType() == TransactionType.USE);
    }

    @Test
    @DisplayName("출처 타입별 거래 조회 테스트")
    void findBySourceType() {
        // given
        MileageTransaction tx1 = MileageTransaction.createEarn(testAccount, testUserId, 1000, "PROGRAM", 1L, "프로그램");
        MileageTransaction tx2 = MileageTransaction.createEarn(testAccount, testUserId, 500, "CERTIFICATION", 1L, "자격증");
        MileageTransaction tx3 = MileageTransaction.createEarn(testAccount, testUserId, 800, "PROGRAM", 2L, "프로그램2");
        transactionRepository.saveAll(List.of(tx1, tx2, tx3));

        // when
        List<MileageTransaction> programTxs = transactionRepository.findBySourceType("PROGRAM");
        List<MileageTransaction> certTxs = transactionRepository.findBySourceType("CERTIFICATION");

        // then
        assertThat(programTxs).hasSize(2);
        assertThat(certTxs).hasSize(1);
    }

    @Test
    @DisplayName("사용자의 출처 타입별 거래 조회 테스트")
    void findByUserIdAndSourceType() {
        // given
        MileageTransaction tx1 = MileageTransaction.createEarn(testAccount, testUserId, 1000, "PROGRAM", 1L, "프로그램1");
        MileageTransaction tx2 = MileageTransaction.createEarn(testAccount, testUserId, 500, "CERTIFICATION", 1L, "자격증");
        MileageTransaction tx3 = MileageTransaction.createEarn(testAccount, testUserId, 800, "PROGRAM", 2L, "프로그램2");
        transactionRepository.saveAll(List.of(tx1, tx2, tx3));

        // when
        List<MileageTransaction> result = transactionRepository.findByUserIdAndSourceType(testUserId, "PROGRAM");

        // then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(tx -> "PROGRAM".equals(tx.getSourceType()));
    }

    @Test
    @DisplayName("사용자의 기간별 거래 조회 테스트")
    void findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc() {
        // given
        MileageTransaction tx = MileageTransaction.createEarn(testAccount, testUserId, 1000, "PROGRAM", 1L, "거래");
        transactionRepository.save(tx);

        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        // when
        List<MileageTransaction> result = transactionRepository.findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
                testUserId, start, end);

        // then
        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("포인트 기준 거래 조회 테스트")
    void findByPointsGreaterThanEqual() {
        // given
        MileageTransaction tx1 = MileageTransaction.createEarn(testAccount, testUserId, 1000, "PROGRAM", 1L, "거래1");
        MileageTransaction tx2 = MileageTransaction.createEarn(testAccount, testUserId, 500, "PROGRAM", 2L, "거래2");
        MileageTransaction tx3 = MileageTransaction.createEarn(testAccount, testUserId, 1500, "PROGRAM", 3L, "거래3");
        transactionRepository.saveAll(List.of(tx1, tx2, tx3));

        // when
        List<MileageTransaction> result = transactionRepository.findByPointsGreaterThanEqual(1000);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(MileageTransaction::getPoints)
                .allMatch(points -> points >= 1000);
    }

    @Test
    @DisplayName("사용자의 포인트 범위 거래 조회 테스트")
    void findByUserIdAndPointsBetween() {
        // given
        MileageTransaction tx1 = MileageTransaction.createEarn(testAccount, testUserId, 500, "PROGRAM", 1L, "거래1");
        MileageTransaction tx2 = MileageTransaction.createEarn(testAccount, testUserId, 1000, "PROGRAM", 2L, "거래2");
        MileageTransaction tx3 = MileageTransaction.createEarn(testAccount, testUserId, 1500, "PROGRAM", 3L, "거래3");
        transactionRepository.saveAll(List.of(tx1, tx2, tx3));

        // when
        List<MileageTransaction> result = transactionRepository.findByUserIdAndPointsBetween(testUserId, 800, 1200);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPoints()).isEqualTo(1000);
    }

    @Test
    @DisplayName("총 소멸 포인트 계산 테스트")
    void getTotalExpiredPoints() {
        // given
        MileageTransaction tx1 = MileageTransaction.createEarn(testAccount, testUserId, 2000, "PROGRAM", 1L, "적립");
        MileageTransaction tx2 = MileageTransaction.createExpire(testAccount, testUserId, 500, "기간 만료");
        MileageTransaction tx3 = MileageTransaction.createExpire(testAccount, testUserId, 300, "기간 만료2");
        transactionRepository.saveAll(List.of(tx1, tx2, tx3));

        // when
        Integer total = transactionRepository.getTotalExpiredPoints(testUserId);

        // then
        assertThat(total).isEqualTo(800);
    }

    @Test
    @DisplayName("계정의 거래 건수 조회 테스트")
    void countByAccountId() {
        // given
        MileageTransaction tx1 = MileageTransaction.createEarn(testAccount, testUserId, 1000, "PROGRAM", 1L, "거래1");
        MileageTransaction tx2 = MileageTransaction.createEarn(testAccount, testUserId, 500, "PROGRAM", 2L, "거래2");
        transactionRepository.saveAll(List.of(tx1, tx2));

        // when
        long count = transactionRepository.countByAccountId(testAccount.getId());

        // then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("거래 유형별 건수 조회 테스트")
    void countByTransactionType() {
        // given
        MileageTransaction tx1 = MileageTransaction.createEarn(testAccount, testUserId, 1000, "PROGRAM", 1L, "적립1");
        MileageTransaction tx2 = MileageTransaction.createEarn(testAccount, testUserId, 500, "PROGRAM", 2L, "적립2");
        MileageTransaction tx3 = MileageTransaction.createUse(testAccount, testUserId, 200, "PROGRAM", 3L, "사용");
        transactionRepository.saveAll(List.of(tx1, tx2, tx3));

        // when
        long earnCount = transactionRepository.countByTransactionType(TransactionType.EARN);
        long useCount = transactionRepository.countByTransactionType(TransactionType.USE);

        // then
        assertThat(earnCount).isEqualTo(2);
        assertThat(useCount).isEqualTo(1);
    }

    @Test
    @DisplayName("사용자의 특정 유형 거래 건수 조회 테스트")
    void countByUserIdAndTransactionType() {
        // given
        MileageTransaction tx1 = MileageTransaction.createEarn(testAccount, testUserId, 1000, "PROGRAM", 1L, "적립");
        MileageTransaction tx2 = MileageTransaction.createUse(testAccount, testUserId, 500, "PROGRAM", 2L, "사용1");
        MileageTransaction tx3 = MileageTransaction.createUse(testAccount, testUserId, 200, "PROGRAM", 3L, "사용2");
        transactionRepository.saveAll(List.of(tx1, tx2, tx3));

        // when
        long useCount = transactionRepository.countByUserIdAndTransactionType(testUserId, TransactionType.USE);

        // then
        assertThat(useCount).isEqualTo(2);
    }

    @Test
    @DisplayName("기간별 총 적립 포인트 계산 테스트")
    void getTotalEarnedPointsByPeriod() {
        // given
        MileageTransaction tx1 = MileageTransaction.createEarn(testAccount, testUserId, 1000, "PROGRAM", 1L, "적립1");
        MileageTransaction tx2 = MileageTransaction.createEarn(testAccount, testUserId, 500, "PROGRAM", 2L, "적립2");
        transactionRepository.saveAll(List.of(tx1, tx2));

        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        // when
        Integer total = transactionRepository.getTotalEarnedPointsByPeriod(start, end);

        // then
        assertThat(total).isEqualTo(1500);
    }

    @Test
    @DisplayName("기간별 총 사용 포인트 계산 테스트")
    void getTotalUsedPointsByPeriod() {
        // given
        MileageTransaction tx1 = MileageTransaction.createEarn(testAccount, testUserId, 2000, "PROGRAM", 1L, "적립");
        MileageTransaction tx2 = MileageTransaction.createUse(testAccount, testUserId, 500, "PROGRAM", 2L, "사용1");
        MileageTransaction tx3 = MileageTransaction.createUse(testAccount, testUserId, 300, "PROGRAM", 3L, "사용2");
        transactionRepository.saveAll(List.of(tx1, tx2, tx3));

        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        // when
        Integer total = transactionRepository.getTotalUsedPointsByPeriod(start, end);

        // then
        assertThat(total).isEqualTo(800);
    }

    @Test
    @DisplayName("사용자의 기간별 적립 포인트 계산 테스트")
    void getUserEarnedPointsByPeriod() {
        // given
        MileageTransaction tx1 = MileageTransaction.createEarn(testAccount, testUserId, 1000, "PROGRAM", 1L, "적립1");
        MileageTransaction tx2 = MileageTransaction.createEarn(testAccount, testUserId, 500, "PROGRAM", 2L, "적립2");
        transactionRepository.saveAll(List.of(tx1, tx2));

        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        // when
        Integer total = transactionRepository.getUserEarnedPointsByPeriod(testUserId, start, end);

        // then
        assertThat(total).isEqualTo(1500);
    }

    @Test
    @DisplayName("계정의 최근 거래 조회 테스트")
    void findTop10ByAccountIdOrderByCreatedAtDesc() {
        // given
        for (int i = 1; i <= 15; i++) {
            MileageTransaction tx = MileageTransaction.createEarn(testAccount, testUserId, i * 100, "PROGRAM", (long) i, "거래" + i);
            transactionRepository.save(tx);
        }

        // when
        List<MileageTransaction> result = transactionRepository.findTop10ByAccountIdOrderByCreatedAtDesc(testAccount.getId());

        // then
        assertThat(result).hasSize(10);
    }

    @Test
    @DisplayName("전체 최근 거래 조회 테스트")
    void findTop20ByOrderByCreatedAtDesc() {
        // given
        for (int i = 1; i <= 25; i++) {
            MileageTransaction tx = MileageTransaction.createEarn(testAccount, testUserId, i * 100, "PROGRAM", (long) i, "거래" + i);
            transactionRepository.save(tx);
        }

        // when
        List<MileageTransaction> result = transactionRepository.findTop20ByOrderByCreatedAtDesc();

        // then
        assertThat(result).hasSize(20);
    }

    @Test
    @DisplayName("사용자의 설명 키워드 검색 테스트")
    void findByUserIdAndDescriptionContainingOrderByCreatedAtDesc() {
        // given
        MileageTransaction tx1 = MileageTransaction.createEarn(testAccount, testUserId, 1000, "PROGRAM", 1L, "AI 프로그램");
        MileageTransaction tx2 = MileageTransaction.createEarn(testAccount, testUserId, 500, "PROGRAM", 2L, "블록체인");
        MileageTransaction tx3 = MileageTransaction.createEarn(testAccount, testUserId, 800, "PROGRAM", 3L, "AI 워크샵");
        transactionRepository.saveAll(List.of(tx1, tx2, tx3));

        // when
        List<MileageTransaction> result = transactionRepository.findByUserIdAndDescriptionContainingOrderByCreatedAtDesc(
                testUserId, "AI");

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("출처 존재 여부 확인 테스트")
    void existsBySourceTypeAndSourceId() {
        // given
        MileageTransaction tx = MileageTransaction.createEarn(testAccount, testUserId, 1000, "PROGRAM", 1L, "프로그램");
        transactionRepository.save(tx);

        // when
        boolean exists = transactionRepository.existsBySourceTypeAndSourceId("PROGRAM", 1L);
        boolean notExists = transactionRepository.existsBySourceTypeAndSourceId("PROGRAM", 999L);

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("사용자의 거래 유형 존재 여부 확인 테스트")
    void existsByUserIdAndTransactionType() {
        // given
        MileageTransaction tx = MileageTransaction.createEarn(testAccount, testUserId, 1000, "PROGRAM", 1L, "적립");
        transactionRepository.save(tx);

        // when
        boolean hasEarn = transactionRepository.existsByUserIdAndTransactionType(testUserId, TransactionType.EARN);
        boolean hasUse = transactionRepository.existsByUserIdAndTransactionType(testUserId, TransactionType.USE);

        // then
        assertThat(hasEarn).isTrue();
        assertThat(hasUse).isFalse();
    }

    @Test
    @DisplayName("출처 타입별 총 포인트 합계 테스트")
    void getTotalPointsBySourceType() {
        // given
        MileageTransaction tx1 = MileageTransaction.createEarn(testAccount, testUserId, 1000, "PROGRAM", 1L, "프로그램1");
        MileageTransaction tx2 = MileageTransaction.createEarn(testAccount, testUserId, 500, "PROGRAM", 2L, "프로그램2");
        MileageTransaction tx3 = MileageTransaction.createEarn(testAccount, testUserId, 800, "CERTIFICATION", 1L, "자격증");
        transactionRepository.saveAll(List.of(tx1, tx2, tx3));

        // when
        Integer programTotal = transactionRepository.getTotalPointsBySourceType("PROGRAM");
        Integer certTotal = transactionRepository.getTotalPointsBySourceType("CERTIFICATION");

        // then
        assertThat(programTotal).isEqualTo(1500);
        assertThat(certTotal).isEqualTo(800);
    }

    @Test
    @DisplayName("사용자의 출처 타입별 포인트 합계 테스트")
    void getUserPointsBySourceType() {
        // given
        MileageTransaction tx1 = MileageTransaction.createEarn(testAccount, testUserId, 1000, "PROGRAM", 1L, "프로그램1");
        MileageTransaction tx2 = MileageTransaction.createEarn(testAccount, testUserId, 500, "PROGRAM", 2L, "프로그램2");
        transactionRepository.saveAll(List.of(tx1, tx2));

        // when
        Integer total = transactionRepository.getUserPointsBySourceType(testUserId, "PROGRAM");

        // then
        assertThat(total).isEqualTo(1500);
    }

    @Test
    @DisplayName("출처별 거래 건수 조회 테스트")
    void countBySourceType() {
        // given
        MileageTransaction tx1 = MileageTransaction.createEarn(testAccount, testUserId, 1000, "PROGRAM", 1L, "프로그램1");
        MileageTransaction tx2 = MileageTransaction.createEarn(testAccount, testUserId, 500, "PROGRAM", 2L, "프로그램2");
        MileageTransaction tx3 = MileageTransaction.createEarn(testAccount, testUserId, 800, "CERTIFICATION", 1L, "자격증");
        transactionRepository.saveAll(List.of(tx1, tx2, tx3));

        // when
        long programCount = transactionRepository.countBySourceType("PROGRAM");
        long certCount = transactionRepository.countBySourceType("CERTIFICATION");

        // then
        assertThat(programCount).isEqualTo(2);
        assertThat(certCount).isEqualTo(1);
    }
}
