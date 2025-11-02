package com.university.scms.domain.file.entity;

import com.university.scms.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 파일 메타데이터 엔티티
 * 시스템에서 사용되는 모든 파일의 메타정보를 저장합니다.
 * 
 * MSA 전환 대비:
 * - userId는 Auth Domain의 User ID만 저장 (외래키 제약조건 없음)
 * - 다른 도메인과의 연결은 ID만 저장
 */
@Entity
@Table(name = "file_metadata", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_file_type", columnList = "file_type"),
    @Index(name = "idx_reference_type_id", columnList = "reference_type,reference_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileMetadata extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== 업로드 사용자 정보 ==========
    
    @Column(name = "user_id", nullable = false)
    private Long userId;  // Auth Domain의 User ID

    // ========== 파일 기본 정보 ==========
    
    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;  // 원본 파일명
    
    @Column(name = "stored_filename", nullable = false, unique = true, length = 255)
    private String storedFilename;  // 저장된 파일명 (UUID 등)
    
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;  // 파일 저장 경로
    
    @Column(name = "file_size", nullable = false)
    private Long fileSize;  // 파일 크기 (bytes)
    
    @Column(name = "mime_type", length = 100)
    private String mimeType;  // MIME 타입 (image/jpeg, application/pdf 등)
    
    @Column(name = "file_extension", length = 20)
    private String fileExtension;  // 파일 확장자

    // ========== 파일 분류 ==========
    
    @Column(name = "file_type", nullable = false, length = 50)
    private String fileType;  // 파일 유형 (PROFILE, DOCUMENT, IMAGE, ATTACHMENT 등)

    // ========== 참조 정보 (어떤 엔티티와 연결되었는지) ==========
    
    @Column(name = "reference_type", length = 50)
    private String referenceType;  // 참조 엔티티 타입 (Program, Competency, Counseling 등)
    
    @Column(name = "reference_id")
    private Long referenceId;  // 참조 엔티티 ID

    // ========== 파일 상태 ==========
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;  // 삭제 여부 (소프트 삭제)

    // ========== 비즈니스 메서드 ==========
    
    /**
     * 파일 소프트 삭제
     */
    public void softDelete() {
        this.isDeleted = true;
    }

    /**
     * 파일 복원
     */
    public void restore() {
        this.isDeleted = false;
    }

    /**
     * 파일 크기를 MB 단위로 반환
     */
    public double getFileSizeInMB() {
        return this.fileSize / (1024.0 * 1024.0);
    }

    /**
     * 이미지 파일 여부 확인
     */
    public boolean isImage() {
        return this.mimeType != null && this.mimeType.startsWith("image/");
    }

    /**
     * PDF 파일 여부 확인
     */
    public boolean isPdf() {
        return "application/pdf".equals(this.mimeType);
    }
}
