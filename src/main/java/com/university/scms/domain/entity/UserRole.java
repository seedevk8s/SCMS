package com.university.scms.domain.entity;

/**
 * 사용자 역할 열거형
 * 
 * STUDENT: 학생
 * STAFF: 교직원 (교수, 조교, 상담사 등)
 * ADMIN: 관리자
 */
public enum UserRole {
    STUDENT("학생"),
    STAFF("교직원"),
    ADMIN("관리자");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
