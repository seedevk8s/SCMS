package com.university.scms.domain.program.repository;

import com.university.scms.config.JpaConfig;
import com.university.scms.domain.program.entity.ProgramCategory;
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
 * ProgramCategoryRepository 테스트
 * 실제 MySQL 데이터베이스를 사용하여 테스트합니다.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(JpaConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProgramCategoryRepositoryTest {

    @Autowired
    private ProgramCategoryRepository categoryRepository;

    private ProgramCategory activeCategory;
    private ProgramCategory inactiveCategory;
    private ProgramCategory deletedCategory;

    @BeforeEach
    void setUp() {
        // 활성화된 카테고리
        activeCategory = ProgramCategory.builder()
                .categoryName("취업역량강화")
                .description("면접, 이력서 작성, 자기소개서 등 취업 관련 프로그램")
                .displayOrder(1)
                .color("#FF5733")
                .icon("icon-briefcase")
                .isActive(true)
                .programCount(10)
                .build();

        // 비활성화된 카테고리
        inactiveCategory = ProgramCategory.builder()
                .categoryName("봉사활동")
                .description("사회봉사 및 교내봉사 활동")
                .displayOrder(5)
                .color("#28A745")
                .icon("icon-heart")
                .isActive(false)
                .programCount(0)
                .build();

        // 삭제된 카테고리
        deletedCategory = ProgramCategory.builder()
                .categoryName("폐지된카테고리")
                .description("더 이상 사용하지 않는 카테고리")
                .isActive(false)
                .deletedAt(LocalDateTime.now())
                .programCount(0)
                .build();
    }

    @AfterEach
    void tearDown() {
        categoryRepository.deleteAll();
    }

    // ========== 기본 CRUD 테스트 ==========

    @Test
    @Order(1)
    @DisplayName("카테고리 생성 테스트")
    void testCreateCategory() {
        // when
        ProgramCategory saved = categoryRepository.save(activeCategory);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCategoryName()).isEqualTo("취업역량강화");
        assertThat(saved.getIsActive()).isTrue();
        assertThat(saved.getProgramCount()).isEqualTo(10);
    }

    @Test
    @Order(2)
    @DisplayName("카테고리 조회 테스트")
    void testFindCategory() {
        // given
        ProgramCategory saved = categoryRepository.save(activeCategory);

        // when
        Optional<ProgramCategory> found = categoryRepository.findById(saved.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getCategoryName()).isEqualTo("취업역량강화");
    }

    @Test
    @Order(3)
    @DisplayName("카테고리 수정 테스트")
    void testUpdateCategory() {
        // given
        ProgramCategory saved = categoryRepository.save(activeCategory);

        // when
        saved.setDescription("업데이트된 설명");
        saved.incrementProgramCount();
        ProgramCategory updated = categoryRepository.save(saved);

        // then
        assertThat(updated.getDescription()).isEqualTo("업데이트된 설명");
        assertThat(updated.getProgramCount()).isEqualTo(11);
    }

    @Test
    @Order(4)
    @DisplayName("카테고리 삭제 테스트")
    void testDeleteCategory() {
        // given
        ProgramCategory saved = categoryRepository.save(activeCategory);
        Long id = saved.getId();

        // when
        categoryRepository.delete(saved);

        // then
        assertThat(categoryRepository.findById(id)).isEmpty();
    }

    // ========== 기본 조회 메서드 테스트 ==========

    @Test
    @Order(5)
    @DisplayName("카테고리명으로 조회")
    void testFindByCategoryName() {
        // given
        categoryRepository.save(activeCategory);

        // when
        Optional<ProgramCategory> found = categoryRepository.findByCategoryName("취업역량강화");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getDescription()).contains("면접");
    }

    @Test
    @Order(6)
    @DisplayName("카테고리명 존재 여부 확인")
    void testExistsByCategoryName() {
        // given
        categoryRepository.save(activeCategory);

        // when & then
        assertThat(categoryRepository.existsByCategoryName("취업역량강화")).isTrue();
        assertThat(categoryRepository.existsByCategoryName("존재하지않는카테고리")).isFalse();
    }

    // ========== 활성화 상태별 조회 테스트 ==========

    @Test
    @Order(7)
    @DisplayName("활성화된 카테고리 목록 조회")
    void testFindByIsActive() {
        // given
        categoryRepository.save(activeCategory);
        categoryRepository.save(inactiveCategory);

        // when
        List<ProgramCategory> activeCategories = categoryRepository.findByIsActive(true);

        // then
        assertThat(activeCategories).hasSize(1);
        assertThat(activeCategories.get(0).getCategoryName()).isEqualTo("취업역량강화");
    }

    @Test
    @Order(8)
    @DisplayName("활성화된 카테고리 목록을 표시 순서대로 조회")
    void testFindActiveOrderByDisplayOrder() {
        // given
        ProgramCategory category1 = ProgramCategory.builder()
                .categoryName("카테고리1")
                .displayOrder(3)
                .isActive(true)
                .programCount(0)
                .build();
        
        ProgramCategory category2 = ProgramCategory.builder()
                .categoryName("카테고리2")
                .displayOrder(1)
                .isActive(true)
                .programCount(0)
                .build();
        
        categoryRepository.save(activeCategory);  // displayOrder: 1
        categoryRepository.save(category1);       // displayOrder: 3
        categoryRepository.save(category2);       // displayOrder: 1

        // when
        List<ProgramCategory> categories = categoryRepository.findActiveOrderByDisplayOrder();

        // then
        assertThat(categories).hasSize(3);
        // displayOrder가 같으면 categoryName 오름차순
    }

    @Test
    @Order(9)
    @DisplayName("비활성화된 카테고리 목록 조회")
    void testFindInactiveCategories() {
        // given
        categoryRepository.save(activeCategory);
        categoryRepository.save(inactiveCategory);

        // when
        List<ProgramCategory> inactive = categoryRepository.findInactiveCategories();

        // then
        assertThat(inactive).hasSize(1);
        assertThat(inactive.get(0).getCategoryName()).isEqualTo("봉사활동");
    }

    // ========== 삭제 상태별 조회 테스트 ==========

    @Test
    @Order(10)
    @DisplayName("삭제되지 않은 카테고리 목록 조회")
    void testFindNotDeleted() {
        // given
        categoryRepository.save(activeCategory);
        categoryRepository.save(deletedCategory);

        // when
        List<ProgramCategory> notDeleted = categoryRepository.findNotDeleted();

        // then
        assertThat(notDeleted).hasSize(1);
        assertThat(notDeleted.get(0).isDeleted()).isFalse();
    }

    @Test
    @Order(11)
    @DisplayName("삭제된 카테고리 목록 조회")
    void testFindDeleted() {
        // given
        categoryRepository.save(activeCategory);
        categoryRepository.save(deletedCategory);

        // when
        List<ProgramCategory> deleted = categoryRepository.findDeleted();

        // then
        assertThat(deleted).hasSize(1);
        assertThat(deleted.get(0).isDeleted()).isTrue();
    }

    @Test
    @Order(12)
    @DisplayName("활성화되고 삭제되지 않은 카테고리 목록 조회")
    void testFindUsableCategories() {
        // given
        categoryRepository.save(activeCategory);
        categoryRepository.save(inactiveCategory);
        categoryRepository.save(deletedCategory);

        // when
        List<ProgramCategory> usable = categoryRepository.findUsableCategories();

        // then
        assertThat(usable).hasSize(1);
        assertThat(usable.get(0).isUsable()).isTrue();
    }

    // ========== 검색 메서드 테스트 ==========

    @Test
    @Order(13)
    @DisplayName("카테고리명으로 검색 (부분 일치)")
    void testFindByCategoryNameContaining() {
        // given
        categoryRepository.save(activeCategory);
        categoryRepository.save(inactiveCategory);

        // when
        List<ProgramCategory> found = categoryRepository.findByCategoryNameContaining("역량");

        // then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getCategoryName()).contains("역량");
    }

    @Test
    @Order(14)
    @DisplayName("활성화된 카테고리 중 카테고리명으로 검색")
    void testFindActiveByCategoryNameContaining() {
        // given
        categoryRepository.save(activeCategory);

        // when
        List<ProgramCategory> found = categoryRepository.findActiveByCategoryNameContaining("취업");

        // then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getIsActive()).isTrue();
    }

    // ========== 표시 순서 관련 조회 테스트 ==========

    @Test
    @Order(15)
    @DisplayName("모든 카테고리를 표시 순서대로 조회")
    void testFindAllOrderByDisplayOrder() {
        // given
        categoryRepository.save(activeCategory);    // order: 1
        categoryRepository.save(inactiveCategory);  // order: 5

        // when
        List<ProgramCategory> categories = categoryRepository.findAllOrderByDisplayOrder();

        // then
        assertThat(categories).hasSize(2);
        assertThat(categories.get(0).getDisplayOrder()).isEqualTo(1);
    }

    @Test
    @Order(16)
    @DisplayName("특정 표시 순서의 카테고리 조회")
    void testFindByDisplayOrder() {
        // given
        categoryRepository.save(activeCategory);

        // when
        Optional<ProgramCategory> found = categoryRepository.findByDisplayOrder(1);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getCategoryName()).isEqualTo("취업역량강화");
    }

    @Test
    @Order(17)
    @DisplayName("표시 순서가 설정되지 않은 카테고리 조회")
    void testFindWithoutDisplayOrder() {
        // given
        ProgramCategory noOrder = ProgramCategory.builder()
                .categoryName("순서없음")
                .isActive(true)
                .programCount(0)
                .build();
        categoryRepository.save(noOrder);
        categoryRepository.save(activeCategory);

        // when
        List<ProgramCategory> withoutOrder = categoryRepository.findWithoutDisplayOrder();

        // then
        assertThat(withoutOrder).hasSize(1);
        assertThat(withoutOrder.get(0).getDisplayOrder()).isNull();
    }

    // ========== 프로그램 수 기반 조회 테스트 ==========

    @Test
    @Order(18)
    @DisplayName("프로그램이 있는 카테고리 조회")
    void testFindCategoriesWithPrograms() {
        // given
        categoryRepository.save(activeCategory);      // programCount: 10
        categoryRepository.save(inactiveCategory);    // programCount: 0

        // when
        List<ProgramCategory> withPrograms = categoryRepository.findCategoriesWithPrograms();

        // then
        assertThat(withPrograms).hasSize(1);
        assertThat(withPrograms.get(0).hasPrograms()).isTrue();
    }

    @Test
    @Order(19)
    @DisplayName("프로그램이 없는 카테고리 조회")
    void testFindEmptyCategories() {
        // given
        categoryRepository.save(activeCategory);
        categoryRepository.save(inactiveCategory);

        // when
        List<ProgramCategory> empty = categoryRepository.findEmptyCategories();

        // then
        assertThat(empty).hasSize(1);
        assertThat(empty.get(0).getProgramCount()).isEqualTo(0);
    }

    @Test
    @Order(20)
    @DisplayName("프로그램 수 기준 내림차순 정렬 조회")
    void testFindAllOrderByProgramCountDesc() {
        // given
        ProgramCategory cat1 = ProgramCategory.builder()
                .categoryName("카테고리1")
                .programCount(5)
                .isActive(true)
                .build();
        
        categoryRepository.save(activeCategory);   // 10개
        categoryRepository.save(cat1);             // 5개
        categoryRepository.save(inactiveCategory); // 0개

        // when
        List<ProgramCategory> categories = categoryRepository.findAllOrderByProgramCountDesc();

        // then
        assertThat(categories).hasSize(3);
        assertThat(categories.get(0).getProgramCount()).isEqualTo(10);
    }

    @Test
    @Order(21)
    @DisplayName("활성화된 카테고리를 프로그램 수 기준 내림차순 정렬 조회")
    void testFindActiveOrderByProgramCountDesc() {
        // given
        categoryRepository.save(activeCategory);

        // when
        List<ProgramCategory> active = categoryRepository.findActiveOrderByProgramCountDesc();

        // then
        assertThat(active).hasSize(1);
        assertThat(active.get(0).getIsActive()).isTrue();
    }

    // ========== 색상 및 아이콘 기반 조회 테스트 ==========

    @Test
    @Order(22)
    @DisplayName("특정 색상의 카테고리 조회")
    void testFindByColor() {
        // given
        categoryRepository.save(activeCategory);

        // when
        List<ProgramCategory> categories = categoryRepository.findByColor("#FF5733");

        // then
        assertThat(categories).hasSize(1);
    }

    @Test
    @Order(23)
    @DisplayName("특정 아이콘의 카테고리 조회")
    void testFindByIcon() {
        // given
        categoryRepository.save(activeCategory);

        // when
        List<ProgramCategory> categories = categoryRepository.findByIcon("icon-briefcase");

        // then
        assertThat(categories).hasSize(1);
    }

    @Test
    @Order(24)
    @DisplayName("색상이 설정된 카테고리 조회")
    void testFindWithColor() {
        // given
        categoryRepository.save(activeCategory);

        // when
        List<ProgramCategory> withColor = categoryRepository.findWithColor();

        // then
        assertThat(withColor).hasSize(1);
        assertThat(withColor.get(0).getColor()).isNotNull();
    }

    @Test
    @Order(25)
    @DisplayName("아이콘이 설정된 카테고리 조회")
    void testFindWithIcon() {
        // given
        categoryRepository.save(activeCategory);

        // when
        List<ProgramCategory> withIcon = categoryRepository.findWithIcon();

        // then
        assertThat(withIcon).hasSize(1);
        assertThat(withIcon.get(0).getIcon()).isNotNull();
    }

    // ========== 통계 및 집계 메서드 테스트 ==========

    @Test
    @Order(26)
    @DisplayName("활성화된 카테고리 수 조회")
    void testCountActiveCategories() {
        // given
        categoryRepository.save(activeCategory);
        categoryRepository.save(inactiveCategory);

        // when
        long count = categoryRepository.countActiveCategories();

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    @Order(27)
    @DisplayName("비활성화된 카테고리 수 조회")
    void testCountInactiveCategories() {
        // given
        categoryRepository.save(activeCategory);
        categoryRepository.save(inactiveCategory);

        // when
        long count = categoryRepository.countInactiveCategories();

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    @Order(28)
    @DisplayName("사용 가능한 카테고리 수 조회")
    void testCountUsableCategories() {
        // given
        categoryRepository.save(activeCategory);
        categoryRepository.save(inactiveCategory);
        categoryRepository.save(deletedCategory);

        // when
        long count = categoryRepository.countUsableCategories();

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    @Order(29)
    @DisplayName("프로그램이 있는 카테고리 수 조회")
    void testCountCategoriesWithPrograms() {
        // given
        categoryRepository.save(activeCategory);
        categoryRepository.save(inactiveCategory);

        // when
        long count = categoryRepository.countCategoriesWithPrograms();

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    @Order(30)
    @DisplayName("삭제된 카테고리 수 조회")
    void testCountDeletedCategories() {
        // given
        categoryRepository.save(activeCategory);
        categoryRepository.save(deletedCategory);

        // when
        long count = categoryRepository.countDeletedCategories();

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    @Order(31)
    @DisplayName("전체 프로그램 수 합계 조회")
    void testGetTotalProgramCount() {
        // given
        ProgramCategory cat1 = ProgramCategory.builder()
                .categoryName("카테고리1")
                .programCount(5)
                .isActive(true)
                .build();
        
        categoryRepository.save(activeCategory);  // 10개
        categoryRepository.save(cat1);            // 5개

        // when
        Long total = categoryRepository.getTotalProgramCount();

        // then
        assertThat(total).isEqualTo(15);
    }

    @Test
    @Order(32)
    @DisplayName("카테고리별 평균 프로그램 수 조회")
    void testGetAverageProgramCountPerCategory() {
        // given
        ProgramCategory cat1 = ProgramCategory.builder()
                .categoryName("카테고리1")
                .programCount(5)
                .isActive(true)
                .build();
        
        categoryRepository.save(activeCategory);  // 10개
        categoryRepository.save(cat1);            // 5개

        // when
        Double average = categoryRepository.getAverageProgramCountPerCategory();

        // then
        assertThat(average).isEqualTo(7.5);  // (10 + 5) / 2
    }

    // ========== 복합 조건 조회 테스트 ==========

    @Test
    @Order(33)
    @DisplayName("활성화되고 프로그램이 있는 카테고리 조회")
    void testFindActiveWithPrograms() {
        // given
        categoryRepository.save(activeCategory);
        categoryRepository.save(inactiveCategory);

        // when
        List<ProgramCategory> activeWithPrograms = categoryRepository.findActiveWithPrograms();

        // then
        assertThat(activeWithPrograms).hasSize(1);
        assertThat(activeWithPrograms.get(0).isUsable()).isTrue();
        assertThat(activeWithPrograms.get(0).hasPrograms()).isTrue();
    }

    // ========== 비즈니스 메서드 테스트 ==========

    @Test
    @Order(34)
    @DisplayName("카테고리 활성화 메서드 테스트")
    void testActivate() {
        // given
        ProgramCategory saved = categoryRepository.save(inactiveCategory);

        // when
        saved.activate();
        ProgramCategory updated = categoryRepository.save(saved);

        // then
        assertThat(updated.getIsActive()).isTrue();
        assertThat(updated.getDeletedAt()).isNull();
    }

    @Test
    @Order(35)
    @DisplayName("카테고리 비활성화 메서드 테스트")
    void testDeactivate() {
        // given
        ProgramCategory saved = categoryRepository.save(activeCategory);

        // when
        saved.deactivate();
        ProgramCategory updated = categoryRepository.save(saved);

        // then
        assertThat(updated.getIsActive()).isFalse();
    }

    @Test
    @Order(36)
    @DisplayName("카테고리 소프트 삭제 메서드 테스트")
    void testSoftDelete() {
        // given
        ProgramCategory saved = categoryRepository.save(activeCategory);

        // when
        saved.softDelete();
        ProgramCategory updated = categoryRepository.save(saved);

        // then
        assertThat(updated.getIsActive()).isFalse();
        assertThat(updated.isDeleted()).isTrue();
        assertThat(updated.getDeletedAt()).isNotNull();
    }

    @Test
    @Order(37)
    @DisplayName("프로그램 수 증가 메서드 테스트")
    void testIncrementProgramCount() {
        // given
        ProgramCategory saved = categoryRepository.save(activeCategory);
        int initialCount = saved.getProgramCount();

        // when
        saved.incrementProgramCount();
        ProgramCategory updated = categoryRepository.save(saved);

        // then
        assertThat(updated.getProgramCount()).isEqualTo(initialCount + 1);
    }

    @Test
    @Order(38)
    @DisplayName("프로그램 수 감소 메서드 테스트")
    void testDecrementProgramCount() {
        // given
        ProgramCategory saved = categoryRepository.save(activeCategory);
        int initialCount = saved.getProgramCount();

        // when
        saved.decrementProgramCount();
        ProgramCategory updated = categoryRepository.save(saved);

        // then
        assertThat(updated.getProgramCount()).isEqualTo(initialCount - 1);
    }

    @Test
    @Order(39)
    @DisplayName("프로그램 수 감소 시 음수 방지 테스트")
    void testDecrementProgramCountWithZero() {
        // given
        inactiveCategory.setProgramCount(0);
        ProgramCategory saved = categoryRepository.save(inactiveCategory);

        // when
        saved.decrementProgramCount();
        ProgramCategory updated = categoryRepository.save(saved);

        // then
        assertThat(updated.getProgramCount()).isEqualTo(0);
    }

    @Test
    @Order(40)
    @DisplayName("사용 가능한 카테고리 여부 확인 메서드 테스트")
    void testIsUsable() {
        // given & when & then
        assertThat(activeCategory.isUsable()).isTrue();
        assertThat(inactiveCategory.isUsable()).isFalse();
        assertThat(deletedCategory.isUsable()).isFalse();
    }
}
