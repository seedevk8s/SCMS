package com.university.scms.domain.entity;

import com.university.scms.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 사용자 엔티티
 * 학생, 교직원, 관리자의 기본 정보를 저장합니다.
 * 
 * MSA 전환 대비: Auth Domain의 핵심 엔티티
 * - 다른 도메인에서는 userId(Long)로만 참조
 * - 외래키 제약조건 없음
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_username", columnList = "username"),
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_role", columnList = "role"),
    @Index(name = "idx_student_id", columnList = "student_id"),
    @Index(name = "idx_employee_id", columnList = "employee_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    // ========== 학생 정보 (학생인 경우) ==========
    
    @Column(name = "student_id", length = 20)
    private String studentId;  // 학번
    
    @Column(length = 100)
    private String department;  // 학과
    
    @Column
    private Integer grade;  // 학년 (1, 2, 3, 4)

    // ========== 교직원 정보 (교직원인 경우) ==========
    
    @Column(name = "employee_id", length = 20)
    private String employeeId;  // 직원번호
    
    @Column(length = 50)
    private String position;  // 직위 (교수, 조교, 상담사 등)

    // ========== 계정 상태 ==========
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;  // 계정 활성화 여부

    // ========== 비즈니스 메서드 ==========
    
    /**
     * 학생 여부 확인
     */
    public boolean isStudent() {
        return this.role == UserRole.STUDENT;
    }

    /**
     * 교직원 여부 확인
     */
    public boolean isStaff() {
        return this.role == UserRole.STAFF;
    }

    /**
     * 관리자 여부 확인
     */
    public boolean isAdmin() {
        return this.role == UserRole.ADMIN;
    }

    /**
     * 계정 활성화
     */
    public void enable() {
        this.enabled = true;
    }

    /**
     * 계정 비활성화
     */
    public void disable() {
        this.enabled = false;
    }
}
