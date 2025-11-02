package com.university.scms.domain.notification.entity;

import com.university.scms.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 알림 템플릿 엔티티
 * 반복적으로 사용되는 알림 템플릿을 저장합니다.
 * 
 * MSA 전환 대비:
 * - 독립적인 엔티티로 다른 도메인과 직접 연결 없음
 */
@Entity
@Table(name = "notification_templates", indexes = {
    @Index(name = "idx_code", columnList = "code"),
    @Index(name = "idx_type", columnList = "type"),
    @Index(name = "idx_is_active", columnList = "is_active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationTemplate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== 템플릿 기본 정보 ==========
    
    @Column(nullable = false, unique = true, length = 50)
    private String code;  // 템플릿 코드 (PROGRAM_APPROVAL, MILEAGE_EARNED 등)
    
    @Column(nullable = false, length = 50)
    private String type;  // 알림 유형 (PROGRAM, MILEAGE, COMPETENCY, COUNSELING, SYSTEM 등)
    
    @Column(nullable = false, length = 100)
    private String name;  // 템플릿 이름

    // ========== 템플릿 내용 ==========
    
    @Column(nullable = false, length = 200)
    private String titleTemplate;  // 제목 템플릿 (변수 포함 가능: {{programName}})
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contentTemplate;  // 내용 템플릿 (변수 포함 가능)

    // ========== 발송 설정 ==========
    
    @Column(name = "send_methods", length = 100)
    private String sendMethods;  // 발송 방법 (쉼표로 구분: WEB,EMAIL,SMS)
    
    @Column(name = "link_url_template", length = 500)
    private String linkUrlTemplate;  // URL 템플릿 (변수 포함 가능: /programs/{{programId}})

    // ========== 템플릿 메타 정보 ==========
    
    @Column(columnDefinition = "TEXT")
    private String description;  // 템플릿 설명
    
    @Column(name = "variables", length = 500)
    private String variables;  // 사용 가능한 변수 목록 (쉼표로 구분: programName,userName)

    // ========== 템플릿 상태 ==========
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;  // 활성화 여부

    // ========== 비즈니스 메서드 ==========
    
    /**
     * 템플릿 활성화
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * 템플릿 비활성화
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * 변수를 실제 값으로 치환하여 제목 생성
     */
    public String generateTitle(java.util.Map<String, String> variables) {
        String result = this.titleTemplate;
        for (java.util.Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }

    /**
     * 변수를 실제 값으로 치환하여 내용 생성
     */
    public String generateContent(java.util.Map<String, String> variables) {
        String result = this.contentTemplate;
        for (java.util.Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }

    /**
     * 변수를 실제 값으로 치환하여 URL 생성
     */
    public String generateUrl(java.util.Map<String, String> variables) {
        if (this.linkUrlTemplate == null) {
            return null;
        }
        String result = this.linkUrlTemplate;
        for (java.util.Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }

    /**
     * 특정 발송 방법 지원 여부 확인
     */
    public boolean supportsSendMethod(String method) {
        return this.sendMethods != null && this.sendMethods.contains(method);
    }
}
