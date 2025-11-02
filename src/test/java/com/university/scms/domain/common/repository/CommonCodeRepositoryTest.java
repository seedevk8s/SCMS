package com.university.scms.domain.common.repository;

import com.university.scms.config.JpaConfig;
import com.university.scms.domain.common.CommonCode;
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
 * CommonCodeRepository 테스트
 * 실제 MySQL 데이터베이스를 사용하여 테스트합니다.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)  // 실제 MySQL 사용
@ActiveProfiles("test")
@Import(JpaConfig.class)  // JPA Auditing 활성화
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CommonCodeRepositoryTest {

    @Autowired
    private CommonCodeRepository commonCodeRepository;

    private CommonCode programType1;
    private CommonCode programType2;
    private CommonCode competencyType1;
    private CommonCode departmentCode;

    @BeforeEach
    void setUp() {
        // 프로그램 유형 코드
        programType1 = CommonCode.builder()
                .codeGroup("PROGRAM_TYPE")
                .codeValue("LECTURE")
                .codeName("강연")
                .description("특강 및 강연 프로그램")
                .displayOrder(1)
                .isActive(true)
                .build();

        programType2 = CommonCode.builder()
                .codeGroup("PROGRAM_TYPE")
                .codeValue("SEMINAR")
                .codeName("세미나")
                .description("세미나 및 워크샵")
                .displayOrder(2)
                .isActive(true)
                .build();

        // 역량 유형 코드
        competencyType1 = CommonCode.builder()
                .codeGroup("COMPETENCY_TYPE")
                .codeValue("COMMUNICATION")
                .codeName("의사소통")
                .description("의사소통 역량")
                .displayOrder(1)
                .isActive(true)
                .build();

        // 학과 코드
        departmentCode = CommonCode.builder()
                .codeGroup("DEPARTMENT")
                .codeValue("CS")
                .codeName("컴퓨터공학과")
                .description("컴퓨터공학 관련 학과")
                .displayOrder(1)
                .isActive(true)
                .build();
    }

    @AfterEach
    void tearDown() {
        // 테스트 후 데이터 정리
        commonCodeRepository.deleteAll();
    }

    // ========== 기본 CRUD 테스트 ==========

    @Test
    @Order(1)
    @DisplayName("공통 코드 생성 테스트")
    void testCreateCommonCode() {
        // given & when
        CommonCode savedCode = commonCodeRepository.save(programType1);

        // then
        assertThat(savedCode.getId()).isNotNull();
        assertThat(savedCode.getCodeGroup()).isEqualTo("PROGRAM_TYPE");
        assertThat(savedCode.getCodeValue()).isEqualTo("LECTURE");
        assertThat(savedCode.getCodeName()).isEqualTo("강연");
    }

    @Test
    @Order(2)
    @DisplayName("공통 코드 조회 테스트")
    void testFindCommonCode() {
        // given
        CommonCode savedCode = commonCodeRepository.save(programType1);

        // when
        Optional<CommonCode> foundCode = commonCodeRepository.findById(savedCode.getId());

        // then
        assertThat(foundCode).isPresent();
        assertThat(foundCode.get().getCodeValue()).isEqualTo("LECTURE");
    }

    @Test
    @Order(3)
    @DisplayName("공통 코드 수정 테스트")
    void testUpdateCommonCode() {
        // given
        CommonCode savedCode = commonCodeRepository.save(programType1);

        // when
        savedCode.setCodeName("특별강연");
        savedCode.setDescription("특별 초청 강연");
        CommonCode updatedCode = commonCodeRepository.save(savedCode);

        // then
        assertThat(updatedCode.getCodeName()).isEqualTo("특별강연");
        assertThat(updatedCode.getDescription()).isEqualTo("특별 초청 강연");
    }

    @Test
    @Order(4)
    @DisplayName("공통 코드 삭제 테스트")
    void testDeleteCommonCode() {
        // given
        CommonCode savedCode = commonCodeRepository.save(programType1);
        Long codeId = savedCode.getId();

        // when
        commonCodeRepository.delete(savedCode);

        // then
        Optional<CommonCode> deletedCode = commonCodeRepository.findById(codeId);
        assertThat(deletedCode).isEmpty();
    }

    // ========== 커스텀 쿼리 테스트 ==========

    @Test
    @Order(5)
    @DisplayName("코드 그룹과 코드 값으로 조회 테스트")
    void testFindByCodeGroupAndCodeValue() {
        // given
        commonCodeRepository.save(programType1);

        // when
        Optional<CommonCode> foundCode = commonCodeRepository.findByCodeGroupAndCodeValue("PROGRAM_TYPE", "LECTURE");

        // then
        assertThat(foundCode).isPresent();
        assertThat(foundCode.get().getCodeName()).isEqualTo("강연");
    }

    @Test
    @Order(6)
    @DisplayName("코드 그룹과 코드 값 존재 여부 확인 테스트")
    void testExistsByCodeGroupAndCodeValue() {
        // given
        commonCodeRepository.save(programType1);

        // when & then
        assertThat(commonCodeRepository.existsByCodeGroupAndCodeValue("PROGRAM_TYPE", "LECTURE")).isTrue();
        assertThat(commonCodeRepository.existsByCodeGroupAndCodeValue("PROGRAM_TYPE", "NONEXISTENT")).isFalse();
    }

    // ========== 코드 그룹별 조회 테스트 ==========

    @Test
    @Order(7)
    @DisplayName("특정 코드 그룹의 모든 코드 조회 테스트")
    void testFindByCodeGroup() {
        // given
        commonCodeRepository.save(programType1);
        commonCodeRepository.save(programType2);
        commonCodeRepository.save(competencyType1);

        // when
        List<CommonCode> programTypes = commonCodeRepository.findByCodeGroup("PROGRAM_TYPE");

        // then
        assertThat(programTypes).hasSize(2);
    }

    @Test
    @Order(8)
    @DisplayName("특정 코드 그룹의 활성화된 코드 조회 테스트")
    void testFindActiveCodesByGroup() {
        // given
        commonCodeRepository.save(programType1);
        commonCodeRepository.save(programType2);
        
        // 비활성 코드 추가
        CommonCode inactiveCode = CommonCode.builder()
                .codeGroup("PROGRAM_TYPE")
                .codeValue("WORKSHOP")
                .codeName("워크샵")
                .displayOrder(3)
                .isActive(false)
                .build();
        commonCodeRepository.save(inactiveCode);

        // when
        List<CommonCode> activeProgramTypes = commonCodeRepository.findActiveCodesByGroup("PROGRAM_TYPE");

        // then
        assertThat(activeProgramTypes).hasSize(2);
        assertThat(activeProgramTypes).extracting("codeName").containsExactly("강연", "세미나");
    }

    @Test
    @Order(9)
    @DisplayName("특정 코드 그룹의 활성화된 코드 개수 조회 테스트")
    void testCountActiveCodesByGroup() {
        // given
        commonCodeRepository.save(programType1);
        commonCodeRepository.save(programType2);

        // when
        long count = commonCodeRepository.countActiveCodesByGroup("PROGRAM_TYPE");

        // then
        assertThat(count).isEqualTo(2);
    }

    // ========== 코드 값별 조회 테스트 ==========

    @Test
    @Order(10)
    @DisplayName("특정 코드 값을 가진 모든 코드 조회 테스트")
    void testFindByCodeValue() {
        // given
        commonCodeRepository.save(programType1);

        // when
        List<CommonCode> codes = commonCodeRepository.findByCodeValue("LECTURE");

        // then
        assertThat(codes).hasSize(1);
        assertThat(codes.get(0).getCodeGroup()).isEqualTo("PROGRAM_TYPE");
    }

    // ========== 활성화 상태별 조회 테스트 ==========

    @Test
    @Order(11)
    @DisplayName("활성화 여부로 조회 테스트")
    void testFindByIsActive() {
        // given
        commonCodeRepository.save(programType1);
        
        CommonCode inactiveCode = CommonCode.builder()
                .codeGroup("PROGRAM_TYPE")
                .codeValue("WORKSHOP")
                .codeName("워크샵")
                .isActive(false)
                .build();
        commonCodeRepository.save(inactiveCode);

        // when
        List<CommonCode> activeCodes = commonCodeRepository.findByIsActive(true);
        List<CommonCode> inactiveCodes = commonCodeRepository.findByIsActive(false);

        // then
        assertThat(activeCodes).hasSize(1);
        assertThat(inactiveCodes).hasSize(1);
    }

    @Test
    @Order(12)
    @DisplayName("삭제되지 않은 모든 코드 조회 테스트")
    void testFindAllNotDeleted() {
        // given
        commonCodeRepository.save(programType1);
        commonCodeRepository.save(programType2);
        
        CommonCode deletedCode = CommonCode.builder()
                .codeGroup("PROGRAM_TYPE")
                .codeValue("DELETED")
                .codeName("삭제된코드")
                .isActive(false)
                .deletedAt(LocalDateTime.now())
                .build();
        commonCodeRepository.save(deletedCode);

        // when
        List<CommonCode> notDeletedCodes = commonCodeRepository.findAllNotDeleted();

        // then
        assertThat(notDeletedCodes).hasSize(2);
    }

    @Test
    @Order(13)
    @DisplayName("사용 가능한 모든 코드 조회 테스트")
    void testFindAllUsable() {
        // given
        commonCodeRepository.save(programType1);
        commonCodeRepository.save(programType2);
        
        // 비활성 코드
        CommonCode inactiveCode = CommonCode.builder()
                .codeGroup("PROGRAM_TYPE")
                .codeValue("INACTIVE")
                .codeName("비활성코드")
                .isActive(false)
                .build();
        commonCodeRepository.save(inactiveCode);
        
        // 삭제된 코드
        CommonCode deletedCode = CommonCode.builder()
                .codeGroup("PROGRAM_TYPE")
                .codeValue("DELETED")
                .codeName("삭제된코드")
                .isActive(true)
                .deletedAt(LocalDateTime.now())
                .build();
        commonCodeRepository.save(deletedCode);

        // when
        List<CommonCode> usableCodes = commonCodeRepository.findAllUsable();

        // then
        assertThat(usableCodes).hasSize(2);
    }

    // ========== 검색 메서드 테스트 ==========

    @Test
    @Order(14)
    @DisplayName("코드명으로 검색 테스트")
    void testFindByCodeNameContaining() {
        // given
        commonCodeRepository.save(programType1);
        commonCodeRepository.save(programType2);

        // when
        List<CommonCode> searchResults = commonCodeRepository.findByCodeNameContaining("세미나");

        // then
        assertThat(searchResults).hasSize(1);
        assertThat(searchResults.get(0).getCodeValue()).isEqualTo("SEMINAR");
    }

    @Test
    @Order(15)
    @DisplayName("코드 그룹과 코드명으로 검색 테스트")
    void testSearchByGroupAndName() {
        // given
        commonCodeRepository.save(programType1);
        commonCodeRepository.save(programType2);
        commonCodeRepository.save(competencyType1);

        // when
        List<CommonCode> searchResults = commonCodeRepository.searchByGroupAndName("PROGRAM_TYPE", "강연");

        // then
        assertThat(searchResults).hasSize(1);
        assertThat(searchResults.get(0).getCodeValue()).isEqualTo("LECTURE");
    }

    // ========== 통계 메서드 테스트 ==========

    @Test
    @Order(16)
    @DisplayName("전체 코드 그룹 목록 조회 테스트")
    void testFindAllCodeGroups() {
        // given
        commonCodeRepository.save(programType1);
        commonCodeRepository.save(competencyType1);
        commonCodeRepository.save(departmentCode);

        // when
        List<String> codeGroups = commonCodeRepository.findAllCodeGroups();

        // then
        assertThat(codeGroups).hasSize(3);
        assertThat(codeGroups).containsExactly("COMPETENCY_TYPE", "DEPARTMENT", "PROGRAM_TYPE");
    }

    @Test
    @Order(17)
    @DisplayName("비활성화된 코드 개수 조회 테스트")
    void testCountInactiveCodes() {
        // given
        commonCodeRepository.save(programType1);
        
        CommonCode inactiveCode = CommonCode.builder()
                .codeGroup("PROGRAM_TYPE")
                .codeValue("INACTIVE")
                .codeName("비활성코드")
                .isActive(false)
                .build();
        commonCodeRepository.save(inactiveCode);

        // when
        long inactiveCount = commonCodeRepository.countInactiveCodes();

        // then
        assertThat(inactiveCount).isEqualTo(1);
    }

    @Test
    @Order(18)
    @DisplayName("삭제된 코드 개수 조회 테스트")
    void testCountDeletedCodes() {
        // given
        commonCodeRepository.save(programType1);
        
        CommonCode deletedCode = CommonCode.builder()
                .codeGroup("PROGRAM_TYPE")
                .codeValue("DELETED")
                .codeName("삭제된코드")
                .isActive(false)
                .deletedAt(LocalDateTime.now())
                .build();
        commonCodeRepository.save(deletedCode);

        // when
        long deletedCount = commonCodeRepository.countDeletedCodes();

        // then
        assertThat(deletedCount).isEqualTo(1);
    }

    @Test
    @Order(19)
    @DisplayName("특정 코드 그룹 내 최대 정렬 순서 조회 테스트")
    void testFindMaxDisplayOrderByGroup() {
        // given
        commonCodeRepository.save(programType1);  // displayOrder = 1
        commonCodeRepository.save(programType2);  // displayOrder = 2

        // when
        Integer maxOrder = commonCodeRepository.findMaxDisplayOrderByGroup("PROGRAM_TYPE");

        // then
        assertThat(maxOrder).isEqualTo(2);
    }

    @Test
    @Order(20)
    @DisplayName("코드 그룹에 코드가 없을 때 최대 정렬 순서 조회 테스트")
    void testFindMaxDisplayOrderByGroupWithNoData() {
        // given - 아무 데이터도 저장하지 않음

        // when
        Integer maxOrder = commonCodeRepository.findMaxDisplayOrderByGroup("NONEXISTENT_GROUP");

        // then
        assertThat(maxOrder).isEqualTo(0);
    }

    // ========== 비즈니스 메서드 테스트 ==========

    @Test
    @Order(21)
    @DisplayName("코드 활성화 메서드 테스트")
    void testActivateMethod() {
        // given
        CommonCode code = commonCodeRepository.save(programType1);
        code.deactivate();
        code.softDelete();
        commonCodeRepository.save(code);

        // when
        code.activate();
        CommonCode activatedCode = commonCodeRepository.save(code);

        // then
        assertThat(activatedCode.getIsActive()).isTrue();
        assertThat(activatedCode.getDeletedAt()).isNull();
    }

    @Test
    @Order(22)
    @DisplayName("코드 비활성화 메서드 테스트")
    void testDeactivateMethod() {
        // given
        CommonCode code = commonCodeRepository.save(programType1);

        // when
        code.deactivate();
        CommonCode deactivatedCode = commonCodeRepository.save(code);

        // then
        assertThat(deactivatedCode.getIsActive()).isFalse();
    }

    @Test
    @Order(23)
    @DisplayName("코드 소프트 삭제 메서드 테스트")
    void testSoftDeleteMethod() {
        // given
        CommonCode code = commonCodeRepository.save(programType1);

        // when
        code.softDelete();
        CommonCode deletedCode = commonCodeRepository.save(code);

        // then
        assertThat(deletedCode.getIsActive()).isFalse();
        assertThat(deletedCode.getDeletedAt()).isNotNull();
        assertThat(deletedCode.isDeleted()).isTrue();
    }

    @Test
    @Order(24)
    @DisplayName("코드 사용 가능 여부 확인 메서드 테스트")
    void testIsUsableMethod() {
        // given
        CommonCode activeCode = commonCodeRepository.save(programType1);
        
        CommonCode inactiveCode = CommonCode.builder()
                .codeGroup("PROGRAM_TYPE")
                .codeValue("INACTIVE")
                .codeName("비활성코드")
                .isActive(false)
                .build();
        inactiveCode = commonCodeRepository.save(inactiveCode);
        
        CommonCode deletedCode = CommonCode.builder()
                .codeGroup("PROGRAM_TYPE")
                .codeValue("DELETED")
                .codeName("삭제된코드")
                .isActive(true)
                .deletedAt(LocalDateTime.now())
                .build();
        deletedCode = commonCodeRepository.save(deletedCode);

        // when & then
        assertThat(activeCode.isUsable()).isTrue();
        assertThat(inactiveCode.isUsable()).isFalse();
        assertThat(deletedCode.isUsable()).isFalse();
    }

    @Test
    @Order(25)
    @DisplayName("코드 그룹 소속 확인 메서드 테스트")
    void testBelongsToGroupMethod() {
        // given
        CommonCode code = commonCodeRepository.save(programType1);

        // when & then
        assertThat(code.belongsToGroup("PROGRAM_TYPE")).isTrue();
        assertThat(code.belongsToGroup("COMPETENCY_TYPE")).isFalse();
    }

    @Test
    @Order(26)
    @DisplayName("코드 값 일치 확인 메서드 테스트")
    void testHasValueMethod() {
        // given
        CommonCode code = commonCodeRepository.save(programType1);

        // when & then
        assertThat(code.hasValue("LECTURE")).isTrue();
        assertThat(code.hasValue("SEMINAR")).isFalse();
    }

    @Test
    @Order(27)
    @DisplayName("코드 전체 키 생성 메서드 테스트")
    void testGetFullCodeMethod() {
        // given
        CommonCode code = commonCodeRepository.save(programType1);

        // when
        String fullCode = code.getFullCode();

        // then
        assertThat(fullCode).isEqualTo("PROGRAM_TYPE:LECTURE");
    }
}
