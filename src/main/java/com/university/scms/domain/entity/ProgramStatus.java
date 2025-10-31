package com.university.scms.domain.entity;

/**
 * 프로그램 상태 열거형
 * 
 * DRAFT: 임시저장
 * OPEN: 모집중
 * CLOSED: 모집마감
 * COMPLETED: 완료
 * CANCELLED: 취소
 */
public enum ProgramStatus {
    DRAFT("임시저장"),
    OPEN("모집중"),
    CLOSED("모집마감"),
    COMPLETED("완료"),
    CANCELLED("취소");

    private final String description;

    ProgramStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
