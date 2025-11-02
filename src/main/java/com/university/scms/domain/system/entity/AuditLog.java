package com.university.scms.domain.system.entity;

import com.university.scms.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 감사 로그 엔티티
 * 사용자의 주요 행위와 데이터 변경 이력을 추적합니다.
 * 
 * MSA 전환 대비:
 * - 독립적인 감사 시스템으로 운영
 * - userId는 행위 주체 정보만 저장 (외래키 제약조건 없음)
 */
@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_action", columnList = "action"),
    @Index(name = "idx_entity_type", columnList = "entity_type"),
    @Index(name = "idx_entity_id", columnList = "entity_id"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== 행위 주체 정보 ==========
    
    @Column(name = "user_id", nullable = false)
    private Long userId;  // Auth Domain의 User ID (행위자)
    
    @Column(nullable = false, length = 50)
    private String username;  // 사용자명 (조회 편의를 위해 중복 저장)
    
    @Column(name = "user_role", length = 20)
    private String userRole;  // 사용자 역할 (STUDENT, STAFF, ADMIN)

    // ========== 행위 정보 ==========
    
    @Column(nullable = false, length = 50)
    private String action;  // 행위 타입 (CREATE, READ, UPDATE, DELETE, APPROVE, REJECT 등)
    
    @Column(nullable = false, length = 50)
    private String entityType;  // 대상 엔티티 타입 (User, Program, Application 등)
    
    @Column(name = "entity_id", nullable = false)
    private Long entityId;  // 대상 엔티티 ID
    
    @Column(length = 200)
    private String description;  // 행위 설명

    // ========== 변경 데이터 ==========
    
    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;  // 변경 전 데이터 (JSON 형태)
    
    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;  // 변경 후 데이터 (JSON 형태)
    
    @Column(name = "changed_fields", length = 500)
    private String changedFields;  // 변경된 필드 목록 (쉼표로 구분)

    // ========== 요청 정보 ==========
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;  // IP 주소 (IPv6 지원)
    
    @Column(name = "user_agent", length = 500)
    private String userAgent;  // User Agent
    
    @Column(name = "request_uri", length = 500)
    private String requestUri;  // 요청 URI
    
    @Column(name = "request_method", length = 10)
    private String requestMethod;  // HTTP 메서드

    // ========== 결과 정보 ==========
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean success = true;  // 성공 여부
    
    @Column(name = "error_message", length = 500)
    private String errorMessage;  // 에러 메시지 (실패 시)

    // ========== 추가 정보 ==========
    
    @Column(columnDefinition = "TEXT")
    private String metadata;  // 추가 메타데이터 (JSON 형태)

    // ========== 비즈니스 메서드 ==========
    
    /**
     * 생성 행위 여부 확인
     */
    public boolean isCreate() {
        return "CREATE".equals(this.action);
    }

    /**
     * 수정 행위 여부 확인
     */
    public boolean isUpdate() {
        return "UPDATE".equals(this.action);
    }

    /**
     * 삭제 행위 여부 확인
     */
    public boolean isDelete() {
        return "DELETE".equals(this.action);
    }

    /**
     * 승인 행위 여부 확인
     */
    public boolean isApprove() {
        return "APPROVE".equals(this.action);
    }

    /**
     * 거부 행위 여부 확인
     */
    public boolean isReject() {
        return "REJECT".equals(this.action);
    }

    /**
     * 실패한 행위 여부 확인
     */
    public boolean isFailed() {
        return !this.success;
    }

    /**
     * 관리자 행위 여부 확인
     */
    public boolean isAdminAction() {
        return "ADMIN".equals(this.userRole);
    }

    /**
     * 데이터가 변경되었는지 확인
     */
    public boolean hasDataChanged() {
        return this.changedFields != null && !this.changedFields.isEmpty();
    }
}
