package com.university.scms.domain.auth.repository;

import com.university.scms.domain.auth.entity.User;
import com.university.scms.domain.auth.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * User 리포지토리
 * 사용자 정보에 대한 데이터 액세스를 담당합니다.
 * 
 * MSA 전환 대비:
 * - Auth Domain의 핵심 리포지토리
 * - 다른 도메인에서는 userId로만 참조
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ========== 기본 조회 메서드 ==========
    
    /**
     * username으로 사용자 조회
     */
    Optional<User> findByUsername(String username);
    
    /**
     * email로 사용자 조회
     */
    Optional<User> findByEmail(String email);
    
    /**
     * username 존재 여부 확인
     */
    boolean existsByUsername(String username);
    
    /**
     * email 존재 여부 확인
     */
    boolean existsByEmail(String email);

    // ========== 역할별 조회 ==========
    
    /**
     * 역할별 사용자 목록 조회
     */
    List<User> findByRole(UserRole role);
    
    /**
     * 활성화된 특정 역할의 사용자 목록 조회
     */
    List<User> findByRoleAndEnabled(UserRole role, Boolean enabled);

    // ========== 학생 관련 조회 ==========
    
    /**
     * 학번으로 학생 조회
     */
    Optional<User> findByStudentId(String studentId);
    
    /**
     * 학과별 학생 목록 조회
     */
    @Query("SELECT u FROM User u WHERE u.role = 'STUDENT' AND u.department = :department AND u.enabled = true")
    List<User> findActiveStudentsByDepartment(@Param("department") String department);
    
    /**
     * 학년별 학생 목록 조회
     */
    @Query("SELECT u FROM User u WHERE u.role = 'STUDENT' AND u.grade = :grade AND u.enabled = true")
    List<User> findActiveStudentsByGrade(@Param("grade") Integer grade);
    
    /**
     * 학과 및 학년별 학생 목록 조회
     */
    @Query("SELECT u FROM User u WHERE u.role = 'STUDENT' AND u.department = :department AND u.grade = :grade AND u.enabled = true")
    List<User> findActiveStudentsByDepartmentAndGrade(@Param("department") String department, @Param("grade") Integer grade);

    // ========== 교직원 관련 조회 ==========
    
    /**
     * 직원번호로 교직원 조회
     */
    Optional<User> findByEmployeeId(String employeeId);
    
    /**
     * 직위별 교직원 목록 조회
     */
    @Query("SELECT u FROM User u WHERE u.role = 'STAFF' AND u.position = :position AND u.enabled = true")
    List<User> findActiveStaffByPosition(@Param("position") String position);

    // ========== 계정 상태 관련 조회 ==========
    
    /**
     * 활성화된 사용자 목록 조회
     */
    List<User> findByEnabled(Boolean enabled);
    
    /**
     * 비활성화된 사용자 수 조회
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.enabled = false")
    long countDisabledUsers();

    // ========== 검색 메서드 ==========
    
    /**
     * 이름으로 사용자 검색 (부분 일치)
     */
    List<User> findByNameContaining(String name);
    
    /**
     * 역할과 이름으로 사용자 검색
     */
    List<User> findByRoleAndNameContaining(UserRole role, String name);
}
