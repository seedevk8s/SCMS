package com.university.scms.domain.entity;

/**
 * 출석 상태 열거형
 * 
 * REGISTERED: 등록됨
 * ATTENDED: 출석
 * ABSENT: 결석
 * CANCELLED: 취소됨
 */
public enum AttendanceStatus {
    REGISTERED("등록됨"),
    ATTENDED("출석"),
    ABSENT("결석"),
    CANCELLED("취소됨");

    private final String description;

    AttendanceStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
