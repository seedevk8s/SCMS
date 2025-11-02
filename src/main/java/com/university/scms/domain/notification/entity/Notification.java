package com.university.scms.domain.notification.entity;

import com.university.scms.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 알림 엔티티
 * 사용자에게 전송되는 알림 정보를 저장합니다.
 * 
 * MSA 전환 대비:
 * - userId는 Auth Domain의 User ID만 저장 (외래키 제약조건 없음)
 * - templateId는 NotificationTemplate ID만 저장
 */
@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_type", columnList = "type"),
    @Index(name = "idx_is_read", columnList = "is_read"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== 수신자 정보 ==========
    
    @Column(name = "user_id", nullable = false)
    private Long userId;  // Auth Domain의 User ID (수신자)

    // ========== 알림 기본 정보 ==========
    
    @Column(nullable = false, length = 50)
    private String type;  // 알림 유형 (PROGRAM, MILEAGE, COMPETENCY, COUNSELING, SYSTEM 등)
    
    @Column(nullable = false, length = 200)
    private String title;  // 알림 제목
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;  // 알림 내용

    // ========== 템플릿 참조 ==========
    
    @Column(name = "template_id")
    private Long templateId;  // NotificationTemplate ID (템플릿 사용 시)

    // ========== 참조 정보 ==========
    
    @Column(name = "reference_type", length = 50)
    private String referenceType;  // 참조 엔티티 타입 (Program, Application, Transaction 등)
    
    @Column(name = "reference_id")
    private Long referenceId;  // 참조 엔티티 ID
    
    @Column(name = "link_url", length = 500)
    private String linkUrl;  // 관련 페이지 URL

    // ========== 알림 상태 ==========
    
    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = false;  // 읽음 여부
    
    @Column(name = "read_at")
    private LocalDateTime readAt;  // 읽은 시간

    // ========== 발송 정보 ==========
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean isSent = false;  // 발송 여부
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;  // 발송 시간
    
    @Column(name = "send_method", length = 20)
    private String sendMethod;  // 발송 방법 (WEB, EMAIL, SMS, PUSH 등)

    // ========== 비즈니스 메서드 ==========
    
    /**
     * 알림 읽음 처리
     */
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    /**
     * 알림 미읽음 처리
     */
    public void markAsUnread() {
        this.isRead = false;
        this.readAt = null;
    }

    /**
     * 알림 발송 완료 처리
     */
    public void markAsSent(String method) {
        this.isSent = true;
        this.sentAt = LocalDateTime.now();
        this.sendMethod = method;
    }

    /**
     * 읽지 않은 알림 여부 확인
     */
    public boolean isUnread() {
        return !this.isRead;
    }

    /**
     * 알림이 오래되었는지 확인 (30일 기준)
     */
    public boolean isOld() {
        return this.getCreatedAt().plusDays(30).isBefore(LocalDateTime.now());
    }
}
