package com.university.scms.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 상담 예약 상태
 */
@Getter
@RequiredArgsConstructor
public enum CounselingStatus {
    
    PENDING("대기", "예약 신청 대기 중"),
    CONFIRMED("확정", "예약이 확정됨"),
    CANCELLED("취소", "예약이 취소됨"),
    COMPLETED("완료", "상담이 완료됨");
    
    private final String description;
    private final String detail;
}
