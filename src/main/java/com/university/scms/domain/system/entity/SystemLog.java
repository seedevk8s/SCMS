package com.university.scms.domain.system.entity;

import com.university.scms.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 시스템 로그 엔티티
 * 시스템 레벨의 이벤트, 에러, 경고 등을 기록합니다.
 * 
 * MSA 전환 대비:
 * - 독립적인 로깅 시스템으로 운영
 * - userId는 발생 주체가 있는 경우에만 저장
 */
@Entity
@Table(name = "system_logs", indexes = {
    @Index(name = "idx_log_level", columnList = "log_level"),
    @Index(name = "idx_category", columnList = "category"),
    @Index(name = "idx_created_at", columnList = "created_at"),
    @Index(name = "idx_user_id", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== 로그 기본 정보 ==========
    
    @Column(name = "log_level", nullable = false, length = 20)
    private String logLevel;  // 로그 레벨 (DEBUG, INFO, WARN, ERROR, FATAL)
    
    @Column(nullable = false, length = 50)
    private String category;  // 로그 카테고리 (AUTH, PROGRAM, MILEAGE, COMPETENCY, COUNSELING, SYSTEM 등)
    
    @Column(nullable = false, length = 200)
    private String message;  // 로그 메시지

    // ========== 상세 정보 ==========
    
    @Column(columnDefinition = "TEXT")
    private String details;  // 상세 정보 (JSON 형태)
    
    @Column(name = "stack_trace", columnDefinition = "TEXT")
    private String stackTrace;  // 에러 스택 트레이스 (에러인 경우)

    // ========== 발생 위치 ==========
    
    @Column(name = "class_name", length = 200)
    private String className;  // 클래스명
    
    @Column(name = "method_name", length = 100)
    private String methodName;  // 메서드명
    
    @Column(name = "line_number")
    private Integer lineNumber;  // 라인 번호

    // ========== 요청 정보 ==========
    
    @Column(name = "request_uri", length = 500)
    private String requestUri;  // 요청 URI
    
    @Column(name = "request_method", length = 10)
    private String requestMethod;  // HTTP 메서드 (GET, POST 등)
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;  // IP 주소 (IPv6 지원)
    
    @Column(name = "user_agent", length = 500)
    private String userAgent;  // User Agent

    // ========== 사용자 정보 ==========
    
    @Column(name = "user_id")
    private Long userId;  // Auth Domain의 User ID (있는 경우)
    
    @Column(length = 50)
    private String username;  // 사용자명 (로그 조회 편의를 위해 중복 저장)

    // ========== 참조 정보 ==========
    
    @Column(name = "reference_type", length = 50)
    private String referenceType;  // 참조 엔티티 타입
    
    @Column(name = "reference_id")
    private Long referenceId;  // 참조 엔티티 ID

    // ========== 처리 정보 ==========
    
    @Column(name = "processing_time")
    private Long processingTime;  // 처리 시간 (밀리초)
    
    @Column(name = "is_resolved")
    @Builder.Default
    private Boolean isResolved = false;  // 해결 여부 (에러인 경우)

    // ========== 비즈니스 메서드 ==========
    
    /**
     * 에러 로그 여부 확인
     */
    public boolean isError() {
        return "ERROR".equals(this.logLevel) || "FATAL".equals(this.logLevel);
    }

    /**
     * 경고 로그 여부 확인
     */
    public boolean isWarning() {
        return "WARN".equals(this.logLevel);
    }

    /**
     * 정보 로그 여부 확인
     */
    public boolean isInfo() {
        return "INFO".equals(this.logLevel);
    }

    /**
     * 에러 해결 처리
     */
    public void markAsResolved() {
        if (this.isError()) {
            this.isResolved = true;
        }
    }

    /**
     * 처리 시간이 느린지 확인 (1초 기준)
     */
    public boolean isSlow() {
        return this.processingTime != null && this.processingTime > 1000;
    }
}
