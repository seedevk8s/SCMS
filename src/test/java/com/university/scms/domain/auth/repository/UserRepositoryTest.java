package com.university.scms.domain.auth.repository;

import com.university.scms.config.JpaConfig;
import com.university.scms.domain.auth.entity.User;
import com.university.scms.domain.auth.entity.UserRole;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * UserRepository 테스트
 * 실제 MySQL 데이터베이스를 사용하여 테스트합니다.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)  // 실제 MySQL 사용
@ActiveProfiles("test")
@Import(JpaConfig.class)  // JPA Auditing 활성화
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testStudent;
    private User testStaff;
    private User testAdmin;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        testStudent = User.builder()
                .username("student001")
                .email("student001@university.ac.kr")
                .password("password123")
                .name("김학생")
                .phone("010-1234-5678")
                .role(UserRole.STUDENT)
                .studentId("2024001")
                .department("컴퓨터공학과")
                .grade(2)
                .enabled(true)
                .build();

        testStaff = User.builder()
                .username("staff001")
                .email("staff001@university.ac.kr")
                .password("password123")
                .name("박교수")
                .phone("010-2345-6789")
                .role(UserRole.STAFF)
                .employeeId("E2024001")
                .position("교수")
                .enabled(true)
                .build();

        testAdmin = User.builder()
                .username("admin001")
                .email("admin001@university.ac.kr")
                .password("password123")
                .name("이관리")
                .phone("010-3456-7890")
                .role(UserRole.ADMIN)
                .enabled(true)
                .build();
    }

    @AfterEach
    void tearDown() {
        // 테스트 후 데이터 정리
        userRepository.deleteAll();
    }

    // ========== 기본 CRUD 테스트 ==========

    @Test
    @Order(1)
    @DisplayName("사용자 생성 테스트")
    void testCreateUser() {
        // given & when
        User savedUser = userRepository.save(testStudent);

        // then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("student001");
        assertThat(savedUser.getRole()).isEqualTo(UserRole.STUDENT);
        assertThat(savedUser.getStudentId()).isEqualTo("2024001");
    }

    @Test
    @Order(2)
    @DisplayName("사용자 조회 테스트")
    void testFindUser() {
        // given
        User savedUser = userRepository.save(testStudent);

        // when
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("student001");
    }

    @Test
    @Order(3)
    @DisplayName("사용자 수정 테스트")
    void testUpdateUser() {
        // given
        User savedUser = userRepository.save(testStudent);

        // when
        savedUser.setPhone("010-9999-9999");
        savedUser.setGrade(3);
        User updatedUser = userRepository.save(savedUser);

        // then
        assertThat(updatedUser.getPhone()).isEqualTo("010-9999-9999");
        assertThat(updatedUser.getGrade()).isEqualTo(3);
    }

    @Test
    @Order(4)
    @DisplayName("사용자 삭제 테스트")
    void testDeleteUser() {
        // given
        User savedUser = userRepository.save(testStudent);
        Long userId = savedUser.getId();

        // when
        userRepository.delete(savedUser);

        // then
        Optional<User> deletedUser = userRepository.findById(userId);
        assertThat(deletedUser).isEmpty();
    }

    // ========== 커스텀 쿼리 테스트 ==========

    @Test
    @Order(5)
    @DisplayName("Username으로 사용자 조회 테스트")
    void testFindByUsername() {
        // given
        userRepository.save(testStudent);

        // when
        Optional<User> foundUser = userRepository.findByUsername("student001");

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("김학생");
    }

    @Test
    @Order(6)
    @DisplayName("Email로 사용자 조회 테스트")
    void testFindByEmail() {
        // given
        userRepository.save(testStudent);

        // when
        Optional<User> foundUser = userRepository.findByEmail("student001@university.ac.kr");

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("student001");
    }

    @Test
    @Order(7)
    @DisplayName("Username 존재 여부 확인 테스트")
    void testExistsByUsername() {
        // given
        userRepository.save(testStudent);

        // when & then
        assertThat(userRepository.existsByUsername("student001")).isTrue();
        assertThat(userRepository.existsByUsername("nonexistent")).isFalse();
    }

    @Test
    @Order(8)
    @DisplayName("Email 존재 여부 확인 테스트")
    void testExistsByEmail() {
        // given
        userRepository.save(testStudent);

        // when & then
        assertThat(userRepository.existsByEmail("student001@university.ac.kr")).isTrue();
        assertThat(userRepository.existsByEmail("nonexistent@university.ac.kr")).isFalse();
    }

    // ========== 역할별 조회 테스트 ==========

    @Test
    @Order(9)
    @DisplayName("역할별 사용자 조회 테스트")
    void testFindByRole() {
        // given
        userRepository.save(testStudent);
        userRepository.save(testStaff);
        userRepository.save(testAdmin);

        // when
        List<User> students = userRepository.findByRole(UserRole.STUDENT);
        List<User> staffs = userRepository.findByRole(UserRole.STAFF);

        // then
        assertThat(students).hasSize(1);
        assertThat(staffs).hasSize(1);
        assertThat(students.get(0).getName()).isEqualTo("김학생");
        assertThat(staffs.get(0).getName()).isEqualTo("박교수");
    }

    @Test
    @Order(10)
    @DisplayName("활성화된 특정 역할 사용자 조회 테스트")
    void testFindByRoleAndEnabled() {
        // given
        userRepository.save(testStudent);
        
        User disabledStudent = User.builder()
                .username("student002")
                .email("student002@university.ac.kr")
                .password("password123")
                .name("최학생")
                .role(UserRole.STUDENT)
                .enabled(false)
                .build();
        userRepository.save(disabledStudent);

        // when
        List<User> activeStudents = userRepository.findByRoleAndEnabled(UserRole.STUDENT, true);

        // then
        assertThat(activeStudents).hasSize(1);
        assertThat(activeStudents.get(0).getName()).isEqualTo("김학생");
    }

    // ========== 학생 관련 조회 테스트 ==========

    @Test
    @Order(11)
    @DisplayName("학번으로 학생 조회 테스트")
    void testFindByStudentId() {
        // given
        userRepository.save(testStudent);

        // when
        Optional<User> foundStudent = userRepository.findByStudentId("2024001");

        // then
        assertThat(foundStudent).isPresent();
        assertThat(foundStudent.get().getName()).isEqualTo("김학생");
    }

    @Test
    @Order(12)
    @DisplayName("학과별 활성 학생 조회 테스트")
    void testFindActiveStudentsByDepartment() {
        // given
        userRepository.save(testStudent);
        
        User anotherStudent = User.builder()
                .username("student002")
                .email("student002@university.ac.kr")
                .password("password123")
                .name("정학생")
                .role(UserRole.STUDENT)
                .studentId("2024002")
                .department("컴퓨터공학과")
                .grade(1)
                .enabled(true)
                .build();
        userRepository.save(anotherStudent);

        // when
        List<User> csStudents = userRepository.findActiveStudentsByDepartment("컴퓨터공학과");

        // then
        assertThat(csStudents).hasSize(2);
    }

    @Test
    @Order(13)
    @DisplayName("학년별 활성 학생 조회 테스트")
    void testFindActiveStudentsByGrade() {
        // given
        userRepository.save(testStudent);

        // when
        List<User> secondGradeStudents = userRepository.findActiveStudentsByGrade(2);

        // then
        assertThat(secondGradeStudents).hasSize(1);
        assertThat(secondGradeStudents.get(0).getName()).isEqualTo("김학생");
    }

    @Test
    @Order(14)
    @DisplayName("학과 및 학년별 활성 학생 조회 테스트")
    void testFindActiveStudentsByDepartmentAndGrade() {
        // given
        userRepository.save(testStudent);

        // when
        List<User> targetStudents = userRepository.findActiveStudentsByDepartmentAndGrade("컴퓨터공학과", 2);

        // then
        assertThat(targetStudents).hasSize(1);
        assertThat(targetStudents.get(0).getName()).isEqualTo("김학생");
    }

    // ========== 교직원 관련 조회 테스트 ==========

    @Test
    @Order(15)
    @DisplayName("직원번호로 교직원 조회 테스트")
    void testFindByEmployeeId() {
        // given
        userRepository.save(testStaff);

        // when
        Optional<User> foundStaff = userRepository.findByEmployeeId("E2024001");

        // then
        assertThat(foundStaff).isPresent();
        assertThat(foundStaff.get().getName()).isEqualTo("박교수");
    }

    @Test
    @Order(16)
    @DisplayName("직위별 활성 교직원 조회 테스트")
    void testFindActiveStaffByPosition() {
        // given
        userRepository.save(testStaff);

        // when
        List<User> professors = userRepository.findActiveStaffByPosition("교수");

        // then
        assertThat(professors).hasSize(1);
        assertThat(professors.get(0).getName()).isEqualTo("박교수");
    }

    // ========== 계정 상태 관련 테스트 ==========

    @Test
    @Order(17)
    @DisplayName("활성화 여부로 사용자 조회 테스트")
    void testFindByEnabled() {
        // given
        userRepository.save(testStudent);
        
        User disabledUser = User.builder()
                .username("disabled001")
                .email("disabled001@university.ac.kr")
                .password("password123")
                .name("비활성사용자")
                .role(UserRole.STUDENT)
                .enabled(false)
                .build();
        userRepository.save(disabledUser);

        // when
        List<User> activeUsers = userRepository.findByEnabled(true);
        List<User> inactiveUsers = userRepository.findByEnabled(false);

        // then
        assertThat(activeUsers).hasSize(1);
        assertThat(inactiveUsers).hasSize(1);
    }

    @Test
    @Order(18)
    @DisplayName("비활성화된 사용자 수 조회 테스트")
    void testCountDisabledUsers() {
        // given
        userRepository.save(testStudent);
        
        User disabledUser = User.builder()
                .username("disabled001")
                .email("disabled001@university.ac.kr")
                .password("password123")
                .name("비활성사용자")
                .role(UserRole.STUDENT)
                .enabled(false)
                .build();
        userRepository.save(disabledUser);

        // when
        long disabledCount = userRepository.countDisabledUsers();

        // then
        assertThat(disabledCount).isEqualTo(1);
    }

    // ========== 검색 메서드 테스트 ==========

    @Test
    @Order(19)
    @DisplayName("이름으로 사용자 검색 테스트")
    void testFindByNameContaining() {
        // given
        userRepository.save(testStudent);
        userRepository.save(testStaff);

        // when
        List<User> usersWithKim = userRepository.findByNameContaining("김");

        // then
        assertThat(usersWithKim).hasSize(1);
        assertThat(usersWithKim.get(0).getName()).isEqualTo("김학생");
    }

    @Test
    @Order(20)
    @DisplayName("역할과 이름으로 사용자 검색 테스트")
    void testFindByRoleAndNameContaining() {
        // given
        userRepository.save(testStudent);
        userRepository.save(testStaff);

        // when
        List<User> studentWithKim = userRepository.findByRoleAndNameContaining(UserRole.STUDENT, "김");

        // then
        assertThat(studentWithKim).hasSize(1);
        assertThat(studentWithKim.get(0).getUsername()).isEqualTo("student001");
    }

    // ========== 비즈니스 메서드 테스트 ==========

    @Test
    @Order(21)
    @DisplayName("사용자 역할 확인 메서드 테스트")
    void testUserRoleCheckMethods() {
        // given & when & then
        assertThat(testStudent.isStudent()).isTrue();
        assertThat(testStudent.isStaff()).isFalse();
        assertThat(testStudent.isAdmin()).isFalse();

        assertThat(testStaff.isStudent()).isFalse();
        assertThat(testStaff.isStaff()).isTrue();
        assertThat(testStaff.isAdmin()).isFalse();

        assertThat(testAdmin.isStudent()).isFalse();
        assertThat(testAdmin.isStaff()).isFalse();
        assertThat(testAdmin.isAdmin()).isTrue();
    }

    @Test
    @Order(22)
    @DisplayName("계정 활성화/비활성화 메서드 테스트")
    void testEnableDisableMethods() {
        // given
        User user = userRepository.save(testStudent);

        // when - 비활성화
        user.disable();
        User updatedUser = userRepository.save(user);

        // then
        assertThat(updatedUser.getEnabled()).isFalse();

        // when - 활성화
        updatedUser.enable();
        User reactivatedUser = userRepository.save(updatedUser);

        // then
        assertThat(reactivatedUser.getEnabled()).isTrue();
    }
}
