package com.university.scms.domain.mileage.repository;

import com.university.scms.config.JpaConfig;
import com.university.scms.domain.mileage.entity.MileageAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MileageAccountRepository 테스트
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaConfig.class)
class MileageAccountRepositoryTest {

    @Autowired
    private MileageAccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("사용자 ID로 계정 조회 테스트")
    void findByUserId() {
        // given
        Long userId = 1001L;
        MileageAccount account = MileageAccount.create(userId);
        accountRepository.save(account);

        // when
        Optional<MileageAccount> found = accountRepository.findByUserId(userId);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo(userId);
        assertThat(found.get().getAvailablePoints()).isEqualTo(0);
    }

    @Test
    @DisplayName("계정 존재 여부 확인 테스트")
    void existsByUserId() {
        // given
        Long userId = 1002L;
        MileageAccount account = MileageAccount.create(userId);
        accountRepository.save(account);

        // when
        boolean exists = accountRepository.existsByUserId(userId);
        boolean notExists = accountRepository.existsByUserId(9999L);

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("사용 가능 포인트 기준 조회 테스트")
    void findByAvailablePointsGreaterThanEqual() {
        // given
        MileageAccount account1 = MileageAccount.create(1001L);
        account1.earn(1000);
        MileageAccount account2 = MileageAccount.create(1002L);
        account2.earn(500);
        MileageAccount account3 = MileageAccount.create(1003L);
        account3.earn(2000);
        accountRepository.saveAll(List.of(account1, account2, account3));

        // when
        List<MileageAccount> result = accountRepository.findByAvailablePointsGreaterThanEqual(800);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(MileageAccount::getAvailablePoints)
                .containsExactlyInAnyOrder(1000, 2000);
    }

    @Test
    @DisplayName("포인트 순위 조회 테스트")
    void findTop10ByOrderByAvailablePointsDesc() {
        // given
        for (int i = 1; i <= 15; i++) {
            MileageAccount account = MileageAccount.create(1000L + i);
            account.earn(i * 100);
            accountRepository.save(account);
        }

        // when
        List<MileageAccount> result = accountRepository.findTop10ByOrderByAvailablePointsDesc();

        // then
        assertThat(result).hasSize(10);
        assertThat(result.get(0).getAvailablePoints()).isEqualTo(1500);
        assertThat(result.get(9).getAvailablePoints()).isEqualTo(600);
    }

    @Test
    @DisplayName("총 포인트 합계 계산 테스트")
    void getTotalAvailablePoints() {
        // given
        MileageAccount account1 = MileageAccount.create(1001L);
        account1.earn(1000);
        MileageAccount account2 = MileageAccount.create(1002L);
        account2.earn(2000);
        MileageAccount account3 = MileageAccount.create(1003L);
        account3.earn(3000);
        accountRepository.saveAll(List.of(account1, account2, account3));

        // when
        Long total = accountRepository.getTotalAvailablePoints();

        // then
        assertThat(total).isEqualTo(6000L);
    }

    @Test
    @DisplayName("평균 포인트 계산 테스트")
    void getAverageAvailablePoints() {
        // given
        MileageAccount account1 = MileageAccount.create(1001L);
        account1.earn(1000);
        MileageAccount account2 = MileageAccount.create(1002L);
        account2.earn(2000);
        MileageAccount account3 = MileageAccount.create(1003L);
        account3.earn(3000);
        accountRepository.saveAll(List.of(account1, account2, account3));

        // when
        Double average = accountRepository.getAverageAvailablePoints();

        // then
        assertThat(average).isEqualTo(2000.0);
    }

    @Test
    @DisplayName("사용자 순위 조회 테스트")
    void getRankByAvailablePoints() {
        // given
        MileageAccount account1 = MileageAccount.create(1001L);
        account1.earn(1000);
        MileageAccount account2 = MileageAccount.create(1002L);
        account2.earn(3000);
        MileageAccount account3 = MileageAccount.create(1003L);
        account3.earn(2000);
        accountRepository.saveAll(List.of(account1, account2, account3));

        // when
        Long rank1 = accountRepository.getRankByAvailablePoints(1002L); // 3000점
        Long rank2 = accountRepository.getRankByAvailablePoints(1003L); // 2000점
        Long rank3 = accountRepository.getRankByAvailablePoints(1001L); // 1000점

        // then
        assertThat(rank1).isEqualTo(1L);
        assertThat(rank2).isEqualTo(2L);
        assertThat(rank3).isEqualTo(3L);
    }

    @Test
    @DisplayName("사용률 조회 테스트")
    void findAccountsOrderByUsageRate() {
        // given
        MileageAccount account1 = MileageAccount.create(1001L);
        account1.earn(1000);
        account1.use(500); // 50% 사용

        MileageAccount account2 = MileageAccount.create(1002L);
        account2.earn(1000);
        account2.use(800); // 80% 사용

        MileageAccount account3 = MileageAccount.create(1003L);
        account3.earn(1000);
        account3.use(200); // 20% 사용

        accountRepository.saveAll(List.of(account1, account2, account3));

        // when
        List<MileageAccount> result = accountRepository.findAccountsOrderByUsageRate();

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getUserId()).isEqualTo(1002L); // 80%
        assertThat(result.get(1).getUserId()).isEqualTo(1001L); // 50%
        assertThat(result.get(2).getUserId()).isEqualTo(1003L); // 20%
    }

    @Test
    @DisplayName("여러 사용자 계정 일괄 조회 테스트")
    void findByUserIdIn() {
        // given
        MileageAccount account1 = MileageAccount.create(1001L);
        MileageAccount account2 = MileageAccount.create(1002L);
        MileageAccount account3 = MileageAccount.create(1003L);
        MileageAccount account4 = MileageAccount.create(1004L);
        accountRepository.saveAll(List.of(account1, account2, account3, account4));

        // when
        List<MileageAccount> result = accountRepository.findByUserIdIn(List.of(1001L, 1003L, 1004L));

        // then
        assertThat(result).hasSize(3);
        assertThat(result).extracting(MileageAccount::getUserId)
                .containsExactlyInAnyOrder(1001L, 1003L, 1004L);
    }

    @Test
    @DisplayName("최근 생성 계정 조회 테스트")
    void findTop10ByOrderByCreatedAtDesc() throws InterruptedException {
        // given
        for (int i = 1; i <= 5; i++) {
            MileageAccount account = MileageAccount.create(1000L + i);
            accountRepository.save(account);
            Thread.sleep(10); // 생성 시간 차이를 위해
        }

        // when
        List<MileageAccount> result = accountRepository.findTop10ByOrderByCreatedAtDesc();

        // then
        assertThat(result).hasSize(5);
        assertThat(result.get(0).getUserId()).isEqualTo(1005L); // 가장 최근
        assertThat(result.get(4).getUserId()).isEqualTo(1001L); // 가장 오래됨
    }

    @Test
    @DisplayName("포인트 범위 조회 테스트")
    void findByAvailablePointsBetween() {
        // given
        MileageAccount account1 = MileageAccount.create(1001L);
        account1.earn(500);
        MileageAccount account2 = MileageAccount.create(1002L);
        account2.earn(1500);
        MileageAccount account3 = MileageAccount.create(1003L);
        account3.earn(2500);
        accountRepository.saveAll(List.of(account1, account2, account3));

        // when
        List<MileageAccount> result = accountRepository.findByAvailablePointsBetween(1000, 2000);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAvailablePoints()).isEqualTo(1500);
    }

    @Test
    @DisplayName("누적 포인트 순위 조회 테스트")
    void findTop10ByOrderByTotalPointsDesc() {
        // given
        MileageAccount account1 = MileageAccount.create(1001L);
        account1.earn(1000);
        MileageAccount account2 = MileageAccount.create(1002L);
        account2.earn(3000);
        MileageAccount account3 = MileageAccount.create(1003L);
        account3.earn(2000);
        accountRepository.saveAll(List.of(account1, account2, account3));

        // when
        List<MileageAccount> result = accountRepository.findTop10ByOrderByTotalPointsDesc();

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getTotalPoints()).isEqualTo(3000);
        assertThat(result.get(1).getTotalPoints()).isEqualTo(2000);
        assertThat(result.get(2).getTotalPoints()).isEqualTo(1000);
    }

    @Test
    @DisplayName("사용한 포인트 순위 조회 테스트")
    void findTop10ByOrderByUsedPointsDesc() {
        // given
        MileageAccount account1 = MileageAccount.create(1001L);
        account1.earn(2000);
        account1.use(500);

        MileageAccount account2 = MileageAccount.create(1002L);
        account2.earn(3000);
        account2.use(1500);

        MileageAccount account3 = MileageAccount.create(1003L);
        account3.earn(2000);
        account3.use(1000);

        accountRepository.saveAll(List.of(account1, account2, account3));

        // when
        List<MileageAccount> result = accountRepository.findTop10ByOrderByUsedPointsDesc();

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getUsedPoints()).isEqualTo(1500);
        assertThat(result.get(1).getUsedPoints()).isEqualTo(1000);
        assertThat(result.get(2).getUsedPoints()).isEqualTo(500);
    }

    @Test
    @DisplayName("활성 계정 수 조회 테스트")
    void countByAvailablePointsGreaterThan() {
        // given
        MileageAccount account1 = MileageAccount.create(1001L);
        account1.earn(1000);
        MileageAccount account2 = MileageAccount.create(1002L);
        account2.earn(500);
        MileageAccount account3 = MileageAccount.create(1003L); // 0 포인트
        accountRepository.saveAll(List.of(account1, account2, account3));

        // when
        long count = accountRepository.countByAvailablePointsGreaterThan(0);

        // then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("포인트 범위 계정 수 조회 테스트")
    void countByAvailablePointsBetween() {
        // given
        MileageAccount account1 = MileageAccount.create(1001L);
        account1.earn(500);
        MileageAccount account2 = MileageAccount.create(1002L);
        account2.earn(1500);
        MileageAccount account3 = MileageAccount.create(1003L);
        account3.earn(2500);
        accountRepository.saveAll(List.of(account1, account2, account3));

        // when
        long count = accountRepository.countByAvailablePointsBetween(1000, 2000);

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("총 누적 포인트 합계 계산 테스트")
    void getTotalAccumulatedPoints() {
        // given
        MileageAccount account1 = MileageAccount.create(1001L);
        account1.earn(1000);
        account1.use(200);

        MileageAccount account2 = MileageAccount.create(1002L);
        account2.earn(2000);
        account2.use(500);

        accountRepository.saveAll(List.of(account1, account2));

        // when
        Long total = accountRepository.getTotalAccumulatedPoints();

        // then
        assertThat(total).isEqualTo(3000L);
    }

    @Test
    @DisplayName("총 사용 포인트 합계 계산 테스트")
    void getTotalUsedPoints() {
        // given
        MileageAccount account1 = MileageAccount.create(1001L);
        account1.earn(1000);
        account1.use(200);

        MileageAccount account2 = MileageAccount.create(1002L);
        account2.earn(2000);
        account2.use(500);

        accountRepository.saveAll(List.of(account1, account2));

        // when
        Long total = accountRepository.getTotalUsedPoints();

        // then
        assertThat(total).isEqualTo(700L);
    }

    @Test
    @DisplayName("누적 포인트 평균 계산 테스트")
    void getAverageTotalPoints() {
        // given
        MileageAccount account1 = MileageAccount.create(1001L);
        account1.earn(1000);
        MileageAccount account2 = MileageAccount.create(1002L);
        account2.earn(2000);
        MileageAccount account3 = MileageAccount.create(1003L);
        account3.earn(3000);
        accountRepository.saveAll(List.of(account1, account2, account3));

        // when
        Double average = accountRepository.getAverageTotalPoints();

        // then
        assertThat(average).isEqualTo(2000.0);
    }

    @Test
    @DisplayName("사용 포인트 평균 계산 테스트")
    void getAverageUsedPoints() {
        // given
        MileageAccount account1 = MileageAccount.create(1001L);
        account1.earn(1000);
        account1.use(300);

        MileageAccount account2 = MileageAccount.create(1002L);
        account2.earn(2000);
        account2.use(600);

        MileageAccount account3 = MileageAccount.create(1003L);
        account3.earn(3000);
        account3.use(900);

        accountRepository.saveAll(List.of(account1, account2, account3));

        // when
        Double average = accountRepository.getAverageUsedPoints();

        // then
        assertThat(average).isEqualTo(600.0);
    }

    @Test
    @DisplayName("누적 포인트 기준 순위 조회 테스트")
    void getRankByTotalPoints() {
        // given
        MileageAccount account1 = MileageAccount.create(1001L);
        account1.earn(1000);
        account1.use(500);

        MileageAccount account2 = MileageAccount.create(1002L);
        account2.earn(3000);
        account2.use(1000);

        MileageAccount account3 = MileageAccount.create(1003L);
        account3.earn(2000);
        account3.use(500);

        accountRepository.saveAll(List.of(account1, account2, account3));

        // when
        Long rank1 = accountRepository.getRankByTotalPoints(1002L); // 3000점
        Long rank2 = accountRepository.getRankByTotalPoints(1003L); // 2000점
        Long rank3 = accountRepository.getRankByTotalPoints(1001L); // 1000점

        // then
        assertThat(rank1).isEqualTo(1L);
        assertThat(rank2).isEqualTo(2L);
        assertThat(rank3).isEqualTo(3L);
    }

    @Test
    @DisplayName("사용 가능 포인트 0인 계정 조회 테스트")
    void findByAvailablePoints() {
        // given
        MileageAccount account1 = MileageAccount.create(1001L);
        account1.earn(1000);

        MileageAccount account2 = MileageAccount.create(1002L); // 0 포인트
        MileageAccount account3 = MileageAccount.create(1003L); // 0 포인트

        accountRepository.saveAll(List.of(account1, account2, account3));

        // when
        List<MileageAccount> result = accountRepository.findByAvailablePoints(0);

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("특정 사용률 이상 계정 조회 테스트")
    void findByUsageRateGreaterThanEqual() {
        // given
        MileageAccount account1 = MileageAccount.create(1001L);
        account1.earn(1000);
        account1.use(600); // 60%

        MileageAccount account2 = MileageAccount.create(1002L);
        account2.earn(1000);
        account2.use(300); // 30%

        MileageAccount account3 = MileageAccount.create(1003L);
        account3.earn(1000);
        account3.use(800); // 80%

        accountRepository.saveAll(List.of(account1, account2, account3));

        // when
        List<MileageAccount> result = accountRepository.findByUsageRateGreaterThanEqual(0.5);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(MileageAccount::getUserId)
                .containsExactlyInAnyOrder(1001L, 1003L);
    }
}
