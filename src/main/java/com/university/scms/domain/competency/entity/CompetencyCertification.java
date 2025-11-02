package com.university.scms.domain.entity;

import com.university.scms.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "competency_certifications",
       indexes = {
           @Index(name = "idx_user", columnList = "user_id"),
           @Index(name = "idx_status", columnList = "verification_status"),
           @Index(name = "idx_verified_by", columnList = "verified_by"),
           @Index(name = "idx_file", columnList = "file_id")
       })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompetencyCertification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;  // Auth Domain 참조 (외래키 없음)

    @Column(name = "certification_type", length = 100)
    private String certificationType;  // 인증 유형 (어학, 자격증 등)

    @Column(name = "certification_name", length = 200)
    private String certificationName;  // 인증 명칭

    @Column(name = "issuer", length = 200)
    private String issuer;  // 발급 기관

    @Column(name = "score", length = 50)
    private String score;  // 점수/등급

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", length = 20, nullable = false)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @Column(name = "verified_by")
    private Long verifiedBy;  // 검증자 ID (Auth Domain 참조)

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "mileage_points")
    private Integer mileagePoints;  // 지급된 마일리지

    @Column(name = "file_id")
    private Long fileId;  // 증빙 서류 (File Domain 참조)

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;  // 거부 사유

    // === 생성 메서드 ===
    
    public static CompetencyCertification create(
            Long userId,
            String certificationType,
            String certificationName,
            String issuer,
            String score,
            LocalDate issueDate,
            LocalDate expiryDate,
            Long fileId) {
        
        CompetencyCertification certification = new CompetencyCertification();
        certification.userId = userId;
        certification.certificationType = certificationType;
        certification.certificationName = certificationName;
        certification.issuer = issuer;
        certification.score = score;
        certification.issueDate = issueDate;
        certification.expiryDate = expiryDate;
        certification.verificationStatus = VerificationStatus.PENDING;
        certification.fileId = fileId;
        return certification;
    }

    // === 비즈니스 메서드 ===
    
    /**
     * 인증 승인
     */
    public void approve(Long verifiedBy, Integer mileagePoints) {
        if (this.verificationStatus != VerificationStatus.PENDING) {
            throw new IllegalStateException("대기 중인 인증만 승인할 수 있습니다.");
        }
        this.verificationStatus = VerificationStatus.APPROVED;
        this.verifiedBy = verifiedBy;
        this.verifiedAt = LocalDateTime.now();
        this.mileagePoints = mileagePoints;
        this.rejectionReason = null;
    }

    /**
     * 인증 거부
     */
    public void reject(Long verifiedBy, String reason) {
        if (this.verificationStatus != VerificationStatus.PENDING) {
            throw new IllegalStateException("대기 중인 인증만 거부할 수 있습니다.");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("거부 사유는 필수입니다.");
        }
        this.verificationStatus = VerificationStatus.REJECTED;
        this.verifiedBy = verifiedBy;
        this.verifiedAt = LocalDateTime.now();
        this.rejectionReason = reason;
        this.mileagePoints = 0;
    }

    /**
     * 인증서 만료 여부
     */
    public boolean isExpired() {
        if (this.expiryDate == null) {
            return false;  // 만료일이 없으면 만료 안됨
        }
        return LocalDate.now().isAfter(this.expiryDate);
    }

    /**
     * 승인 대기 중인지 확인
     */
    public boolean isPending() {
        return this.verificationStatus == VerificationStatus.PENDING;
    }

    /**
     * 승인되었는지 확인
     */
    public boolean isApproved() {
        return this.verificationStatus == VerificationStatus.APPROVED;
    }

    /**
     * 거부되었는지 확인
     */
    public boolean isRejected() {
        return this.verificationStatus == VerificationStatus.REJECTED;
    }

    /**
     * 인증서 정보 수정
     */
    public void update(
            String certificationType,
            String certificationName,
            String issuer,
            String score,
            LocalDate issueDate,
            LocalDate expiryDate) {
        
        if (this.verificationStatus != VerificationStatus.PENDING && 
            this.verificationStatus != VerificationStatus.REJECTED) {
            throw new IllegalStateException("승인된 인증은 수정할 수 없습니다.");
        }
        
        this.certificationType = certificationType;
        this.certificationName = certificationName;
        this.issuer = issuer;
        this.score = score;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        
        // 거부 상태였다면 다시 대기 상태로
        if (this.verificationStatus == VerificationStatus.REJECTED) {
            this.verificationStatus = VerificationStatus.PENDING;
            this.rejectionReason = null;
            this.verifiedBy = null;
            this.verifiedAt = null;
        }
    }

    /**
     * 증빙 서류 업데이트
     */
    public void updateFile(Long fileId) {
        this.fileId = fileId;
    }
}
