package com.university.scms.domain.entity;

/**
 * 프로그램 신청 상태 열거형
 * 
 * PENDING: 대기중
 * APPROVED: 승인됨
 * REJECTED: 거부됨
 * CANCELLED: 취소됨
 */
public enum ApplicationStatus {
    PENDING("대기중"),
    APPROVED("승인됨"),
    REJECTED("거부됨"),
    CANCELLED("취소됨");

    private final String description;

    ApplicationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
