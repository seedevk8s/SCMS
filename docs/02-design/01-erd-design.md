# SCMS ERD 설계 (Updated)

## 🎯 설계 목표

**MSA 전환 가능한 데이터베이스 구조 설계**

### 설계 원칙
1. **도메인별 독립성**: 각 도메인이 자신의 테이블 소유
2. **느슨한 결합**: 도메인 간 외래키 없음, ID 참조 방식
3. **확장 가능성**: 테이블 분리가 용이한 구조
4. **Audit 추적**: 모든 테이블에 생성/수정 시간 기록

### 🔧 JPA 하이브리드 전략

**도메인 내부 관계**
- JPA 관계 매핑 사용 (`@ManyToOne`, `@OneToMany`)
- 물리적 외래키 제약조건 제거: `@ForeignKey(ConstraintMode.NO_CONSTRAINT)`
- JPA 편의 기능 활용 (Lazy Loading, Cascade)

**도메인 간 관계**
- ID만 저장 (Long 타입)
- JPA 관계 매핑 없음
- Service 계층에서 명시적 조회

```java
// 예시: Program Domain
@Entity
public class Program {
    // 도메인 내부: JPA 관계 (외래키 제약 제거)
    @OneToMany(mappedBy = "program")
    private List<ProgramApplication> applications;
    
    // 도메인 간: ID만 저장
    @Column(name = "organizer_id")
    private Long organizerId;  // Auth Domain의 User
}
```

---

## 📋 도메인별 테이블 개요

### 1. Auth Domain (인증/인가)
- `users` - 사용자 기본 정보

### 2. Program Domain (비교과 프로그램)
- `programs` - 프로그램 정보
- `program_applications` - 프로그램 신청
- `program_participants` - 참여 확정자

### 3. Mileage Domain (마일리지)
- `mileage_accounts` - 사용자별 마일리지 계정
- `mileage_transactions` - 마일리지 거래 내역
- `competency_certifications` - 역량 인증 정보

### 4. Competency Domain (역량 진단)
- `competency_surveys` - 역량 진단 설문
- `survey_questions` - 설문 문항
- `survey_responses` - 설문 응답
- `competency_results` - 진단 결과

### 5. Counseling Domain (상담)
- `counseling_requests` - 상담 신청
- `counseling_sessions` - 상담 세션
- `counseling_notes` - 상담 기록

### 6. Career Domain (진로 설계) ⭐ NEW
- `portfolios` - 포트폴리오
- `job_postings` - 채용공고
- `job_bookmarks` - 채용공고 북마크

### 7. File Management Domain ⭐ NEW
- `files` - 파일 메타데이터

### 8. Notification Domain ⭐ NEW
- `notifications` - 알림
- `email_logs` - 이메일 발송 로그

### 9. System Domain ⭐ NEW
- `access_logs` - 접속 로그

---

## 🗄️ 테이블 상세 설계

### 1️⃣ Auth Domain

#### users (사용자)
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    role ENUM('STUDENT', 'STAFF', 'ADMIN') NOT NULL,
    student_id VARCHAR(20),           -- 학번 (학생인 경우)
    department VARCHAR(100),          -- 학과
    grade INT,                        -- 학년 (학생인 경우)
    employee_id VARCHAR(20),          -- 직원번호 (교직원인 경우)
    position VARCHAR(50),             -- 직위 (교직원인 경우)
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_role (role),
    INDEX idx_student_id (student_id),
    INDEX idx_employee_id (employee_id)
);
```

**설계 포인트:**
- `role`을 ENUM으로 직접 저장 (간단한 권한 구조)
- 학생/교직원 정보를 한 테이블에 (NULL 허용)
- MSA 전환 시 Auth Service의 핵심 테이블

---

### 2️⃣ Program Domain

#### programs (비교과 프로그램)
```sql
CREATE TABLE programs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    category VARCHAR(50),             -- 프로그램 카테고리
    organizer_id BIGINT NOT NULL,     -- 담당 교직원 ID (users 참조, 외래키 X)
    location VARCHAR(200),
    capacity INT,                      -- 정원
    current_participants INT DEFAULT 0, -- 현재 참여자 수
    start_date DATETIME,
    end_date DATETIME,
    application_start DATETIME,        -- 신청 시작일
    application_end DATETIME,          -- 신청 마감일
    mileage_points INT DEFAULT 0,      -- 참여 시 지급 마일리지
    status ENUM('DRAFT', 'OPEN', 'CLOSED', 'COMPLETED', 'CANCELLED') DEFAULT 'DRAFT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_organizer (organizer_id),
    INDEX idx_status (status),
    INDEX idx_dates (start_date, end_date),
    INDEX idx_category (category)
);
```

**설계 포인트:**
- `organizer_id`는 ID만 저장 (외래키 제약 X)
- MSA 전환 시 User Service에 API 호출하여 정보 조회
- 상태 관리로 프로그램 생명주기 추적

#### program_applications (프로그램 신청)
```sql
CREATE TABLE program_applications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    program_id BIGINT NOT NULL,       -- programs.id (도메인 내부, JPA 관계)
    user_id BIGINT NOT NULL,          -- users.id 참조 (도메인 간, ID만 저장)
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED') DEFAULT 'PENDING',
    application_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    motivation TEXT,                   -- 신청 동기
    reviewed_by BIGINT,                -- 검토자 ID (도메인 간, ID만 저장)
    reviewed_at DATETIME,
    rejection_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_program_user (program_id, user_id),
    INDEX idx_user (user_id),
    INDEX idx_status (status),
    INDEX idx_reviewed_by (reviewed_by)
);
```

#### program_participants (프로그램 참여자)
```sql
CREATE TABLE program_participants (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    program_id BIGINT NOT NULL,       -- programs.id (도메인 내부)
    user_id BIGINT NOT NULL,          -- users.id (도메인 간)
    application_id BIGINT,            -- program_applications.id (도메인 내부)
    attendance_status ENUM('REGISTERED', 'ATTENDED', 'ABSENT', 'CANCELLED') DEFAULT 'REGISTERED',
    attendance_confirmed_at DATETIME,
    attendance_confirmed_by BIGINT,   -- 출석 확인자 ID (도메인 간)
    feedback TEXT,                     -- 참여 후기
    rating INT CHECK (rating BETWEEN 1 AND 5),
    mileage_awarded BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_program_participant (program_id, user_id),
    INDEX idx_user (user_id),
    INDEX idx_attendance (attendance_status),
    INDEX idx_application (application_id)
);
```

**설계 포인트:**
- 신청(`applications`)과 참여(`participants`) 분리
- 참여 확정 후 출석 관리
- 마일리지 지급 여부 추적

---

### 3️⃣ Mileage Domain

#### mileage_accounts (마일리지 계정)
```sql
CREATE TABLE mileage_accounts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT UNIQUE NOT NULL,   -- users.id 참조 (도메인 간)
    total_points INT DEFAULT 0,        -- 누적 마일리지
    available_points INT DEFAULT 0,    -- 사용 가능 마일리지
    used_points INT DEFAULT 0,         -- 사용한 마일리지
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_user (user_id),
    INDEX idx_available_points (available_points)
);
```

#### mileage_transactions (마일리지 거래 내역)
```sql
CREATE TABLE mileage_transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_id BIGINT NOT NULL,        -- mileage_accounts.id (도메인 내부)
    user_id BIGINT NOT NULL,           -- users.id (도메인 간, 조회 편의)
    transaction_type ENUM('EARN', 'USE', 'EXPIRE', 'ADJUST') NOT NULL,
    points INT NOT NULL,                -- 적립/사용 포인트 (+ 또는 -)
    source_type VARCHAR(50),            -- 출처 타입 (PROGRAM, CERTIFICATION, ADJUSTMENT)
    source_id BIGINT,                   -- 출처 ID
    description VARCHAR(500),
    balance_after INT,                  -- 거래 후 잔액
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_account (account_id),
    INDEX idx_user (user_id),
    INDEX idx_type (transaction_type),
    INDEX idx_created (created_at),
    INDEX idx_source (source_type, source_id)
);
```

#### competency_certifications (역량 인증)
```sql
CREATE TABLE competency_certifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,           -- users.id (도메인 간)
    certification_type VARCHAR(100),   -- 인증 유형 (어학, 자격증 등)
    certification_name VARCHAR(200),   -- 인증 명칭
    issuer VARCHAR(200),               -- 발급 기관
    score VARCHAR(50),                 -- 점수/등급
    issue_date DATE,
    expiry_date DATE,
    verification_status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    verified_by BIGINT,                -- 검증자 ID (도메인 간)
    verified_at DATETIME,
    mileage_points INT,                -- 지급된 마일리지
    file_id BIGINT,                    -- 증빙 서류 (File Domain)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_user (user_id),
    INDEX idx_status (verification_status),
    INDEX idx_verified_by (verified_by),
    INDEX idx_file (file_id)
);
```

**설계 포인트:**
- 계정(`accounts`)과 거래내역(`transactions`) 분리
- 거래 후 잔액 기록으로 무결성 검증 가능
- 포인트 출처 추적 (`source_type`, `source_id`)

---

### 4️⃣ Competency Domain

#### competency_surveys (역량 진단 설문)
```sql
CREATE TABLE competency_surveys (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    survey_type VARCHAR(50),           -- 설문 유형
    target_role ENUM('STUDENT', 'STAFF', 'ALL') DEFAULT 'ALL',
    is_active BOOLEAN DEFAULT TRUE,
    start_date DATETIME,
    end_date DATETIME,
    created_by BIGINT,                 -- 생성자 ID (도메인 간)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_active (is_active),
    INDEX idx_dates (start_date, end_date),
    INDEX idx_created_by (created_by)
);
```

#### survey_questions (설문 문항)
```sql
CREATE TABLE survey_questions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    survey_id BIGINT NOT NULL,         -- competency_surveys.id (도메인 내부)
    question_order INT,
    question_text TEXT NOT NULL,
    question_type ENUM('SINGLE_CHOICE', 'MULTIPLE_CHOICE', 'SCALE', 'TEXT') NOT NULL,
    competency_category VARCHAR(100),  -- 역량 카테고리 (의사소통, 문제해결 등)
    is_required BOOLEAN DEFAULT TRUE,
    options JSON,                       -- 선택지 (JSON 배열)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_survey (survey_id),
    INDEX idx_category (competency_category),
    INDEX idx_order (survey_id, question_order)
);
```

#### survey_responses (설문 응답)
```sql
CREATE TABLE survey_responses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    survey_id BIGINT NOT NULL,         -- competency_surveys.id (도메인 내부)
    user_id BIGINT NOT NULL,           -- users.id (도메인 간)
    question_id BIGINT NOT NULL,       -- survey_questions.id (도메인 내부)
    response_value TEXT,               -- 응답 값
    response_score INT,                -- 점수화된 응답
    submitted_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_survey_user (survey_id, user_id),
    INDEX idx_question (question_id),
    INDEX idx_user (user_id)
);
```

#### competency_results (역량 진단 결과)
```sql
CREATE TABLE competency_results (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    survey_id BIGINT NOT NULL,         -- competency_surveys.id (도메인 내부)
    user_id BIGINT NOT NULL,           -- users.id (도메인 간)
    total_score INT,
    category_scores JSON,              -- 카테고리별 점수 (JSON)
    competency_level VARCHAR(50),      -- 역량 수준 (초급, 중급, 고급)
    strengths TEXT,                     -- 강점
    weaknesses TEXT,                    -- 약점
    recommendations TEXT,               -- 추천 사항
    completed_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_survey_user (survey_id, user_id),
    INDEX idx_user (user_id),
    INDEX idx_level (competency_level)
);
```

**설계 포인트:**
- 설문(`surveys`), 문항(`questions`), 응답(`responses`), 결과(`results`) 분리
- JSON 활용으로 유연한 데이터 구조
- 카테고리별 역량 평가 가능

---

### 5️⃣ Counseling Domain

#### counseling_requests (상담 신청)
```sql
CREATE TABLE counseling_requests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,        -- users.id 참조 (도메인 간)
    counselor_id BIGINT,               -- users.id 참조 (도메인 간, 배정 전 NULL)
    counseling_type VARCHAR(50),       -- 상담 유형 (진로, 학업, 심리 등)
    preferred_date DATETIME,
    preferred_time VARCHAR(50),
    topic VARCHAR(200),                -- 상담 주제
    content TEXT,                      -- 상담 내용 (사전 작성)
    urgency ENUM('LOW', 'MEDIUM', 'HIGH') DEFAULT 'MEDIUM',
    status ENUM('PENDING', 'ASSIGNED', 'SCHEDULED', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_student (student_id),
    INDEX idx_counselor (counselor_id),
    INDEX idx_status (status),
    INDEX idx_urgency (urgency)
);
```

#### counseling_sessions (상담 세션)
```sql
CREATE TABLE counseling_sessions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    request_id BIGINT NOT NULL,        -- counseling_requests.id (도메인 내부)
    student_id BIGINT NOT NULL,        -- users.id (도메인 간)
    counselor_id BIGINT NOT NULL,      -- users.id (도메인 간)
    session_date DATETIME NOT NULL,
    duration INT,                       -- 상담 시간 (분)
    location VARCHAR(200),
    session_type ENUM('FACE_TO_FACE', 'ONLINE', 'PHONE') DEFAULT 'FACE_TO_FACE',
    status ENUM('SCHEDULED', 'COMPLETED', 'NO_SHOW', 'CANCELLED') DEFAULT 'SCHEDULED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_request (request_id),
    INDEX idx_student (student_id),
    INDEX idx_counselor (counselor_id),
    INDEX idx_date (session_date),
    INDEX idx_status (status)
);
```

#### counseling_notes (상담 기록)
```sql
CREATE TABLE counseling_notes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id BIGINT NOT NULL,        -- counseling_sessions.id (도메인 내부)
    counselor_id BIGINT NOT NULL,      -- users.id (도메인 간)
    student_id BIGINT NOT NULL,        -- users.id (도메인 간)
    main_issue TEXT,                   -- 주요 상담 내용
    counseling_notes TEXT,             -- 상담 기록
    action_items TEXT,                 -- 조치 사항
    follow_up_needed BOOLEAN DEFAULT FALSE,
    next_session_recommendation TEXT,
    is_confidential BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_session (session_id),
    INDEX idx_student (student_id),
    INDEX idx_counselor (counselor_id),
    INDEX idx_follow_up (follow_up_needed)
);
```

**설계 포인트:**
- 신청(`requests`), 세션(`sessions`), 기록(`notes`) 분리
- 민감 정보 관리 (`is_confidential`)
- 후속 상담 추적 가능

---

### 6️⃣ Career Domain ⭐

#### portfolios (포트폴리오)
```sql
CREATE TABLE portfolios (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,           -- users.id (도메인 간)
    title VARCHAR(200) NOT NULL,
    description TEXT,
    portfolio_type ENUM('PROJECT', 'AWARD', 'CERTIFICATE', 'ACTIVITY', 'OTHER'),
    start_date DATE,
    end_date DATE,
    organization VARCHAR(200),
    role VARCHAR(100),
    technologies JSON,                 -- 사용 기술/역량 (JSON 배열)
    file_ids JSON,                     -- 첨부파일 ID 배열 (File Domain)
    url VARCHAR(500),                  -- 관련 URL (GitHub, 웹사이트 등)
    is_public BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_user (user_id),
    INDEX idx_type (portfolio_type),
    INDEX idx_public (is_public),
    INDEX idx_dates (start_date, end_date)
);
```

#### job_postings (채용공고)
```sql
CREATE TABLE job_postings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(300) NOT NULL,
    company_name VARCHAR(200) NOT NULL,
    job_type VARCHAR(50),              -- 정규직, 계약직, 인턴, 알바
    employment_type VARCHAR(50),       -- 신입, 경력, 인턴
    location VARCHAR(200),
    salary_range VARCHAR(100),
    required_skills JSON,              -- 요구 기술/역량 (JSON 배열)
    preferred_skills JSON,             -- 우대 사항
    description TEXT,
    responsibilities TEXT,              -- 주요 업무
    qualifications TEXT,                -- 지원 자격
    benefits TEXT,                      -- 복리후생
    external_url VARCHAR(500),         -- 원본 공고 링크
    external_id VARCHAR(100),          -- 외부 API ID
    application_method VARCHAR(200),   -- 지원 방법
    deadline DATE,
    is_active BOOLEAN DEFAULT TRUE,
    view_count INT DEFAULT 0,
    created_by BIGINT,                 -- 등록자 ID (도메인 간)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_company (company_name),
    INDEX idx_active (is_active),
    INDEX idx_deadline (deadline),
    INDEX idx_job_type (job_type),
    INDEX idx_created_by (created_by)
);
```

#### job_bookmarks (채용공고 북마크)
```sql
CREATE TABLE job_bookmarks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,           -- users.id (도메인 간)
    job_posting_id BIGINT NOT NULL,    -- job_postings.id (도메인 내부)
    notes TEXT,                         -- 메모
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_user_job (user_id, job_posting_id),
    INDEX idx_user (user_id),
    INDEX idx_job (job_posting_id)
);
```

**설계 포인트:**
- 포트폴리오 관리로 진로 설계 지원
- 외부 채용공고 API 연동 준비 (`external_id`, `external_url`)
- 북마크로 관심 공고 관리

---

### 7️⃣ File Management Domain ⭐

#### files (파일 메타데이터)
```sql
CREATE TABLE files (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,           -- users.id (도메인 간)
    original_filename VARCHAR(255) NOT NULL,
    stored_filename VARCHAR(255) NOT NULL,  -- UUID 기반 저장명
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,                  -- 바이트 단위
    mime_type VARCHAR(100),
    file_extension VARCHAR(10),
    file_type ENUM('DOCUMENT', 'IMAGE', 'PDF', 'VIDEO', 'AUDIO', 'OTHER'),
    related_type VARCHAR(50),          -- 연관 엔티티 타입
    related_id BIGINT,                 -- 연관 엔티티 ID
    storage_type ENUM('LOCAL', 'S3', 'NAS') DEFAULT 'LOCAL',
    download_count INT DEFAULT 0,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_user (user_id),
    INDEX idx_related (related_type, related_id),
    INDEX idx_type (file_type),
    INDEX idx_deleted (is_deleted)
);
```

**설계 포인트:**
- 모든 파일 업로드의 중앙 관리
- 논리적 삭제 지원 (`is_deleted`)
- 다양한 스토리지 타입 지원 (로컬, S3, NAS)
- 연관 엔티티 추적 (`related_type`, `related_id`)

---

### 8️⃣ Notification Domain ⭐

#### notifications (알림)
```sql
CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,           -- users.id (도메인 간)
    notification_type VARCHAR(50) NOT NULL,  -- PROGRAM_APPROVED, MILEAGE_EARNED, etc.
    title VARCHAR(200) NOT NULL,
    content TEXT,
    related_type VARCHAR(50),          -- 연관 엔티티 타입
    related_id BIGINT,                 -- 연관 엔티티 ID
    priority ENUM('LOW', 'NORMAL', 'HIGH') DEFAULT 'NORMAL',
    is_read BOOLEAN DEFAULT FALSE,
    read_at DATETIME,
    is_deleted BOOLEAN DEFAULT FALSE,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_user (user_id),
    INDEX idx_read (is_read),
    INDEX idx_type (notification_type),
    INDEX idx_deleted (is_deleted),
    INDEX idx_sent (sent_at)
);
```

#### email_logs (이메일 발송 로그)
```sql
CREATE TABLE email_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,                    -- users.id (도메인 간, NULL 가능)
    email VARCHAR(100) NOT NULL,
    subject VARCHAR(200),
    content TEXT,
    email_type VARCHAR(50),            -- VERIFICATION, NOTIFICATION, MARKETING
    related_type VARCHAR(50),
    related_id BIGINT,
    status ENUM('PENDING', 'SENT', 'FAILED', 'BOUNCED') DEFAULT 'PENDING',
    sent_at DATETIME,
    error_message TEXT,
    retry_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_user (user_id),
    INDEX idx_email (email),
    INDEX idx_status (status),
    INDEX idx_type (email_type),
    INDEX idx_sent (sent_at)
);
```

**설계 포인트:**
- 인앱 알림과 이메일 알림 분리
- 알림 읽음 상태 추적
- 이메일 발송 실패 추적 및 재시도 지원

---

### 9️⃣ System Domain ⭐

#### access_logs (접속 로그)
```sql
CREATE TABLE access_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,                    -- users.id (도메인 간, NULL 가능)
    ip_address VARCHAR(50),
    request_url VARCHAR(500),
    http_method VARCHAR(10),
    user_agent TEXT,
    referer VARCHAR(500),
    status_code INT,
    response_time INT,                 -- 응답 시간 (ms)
    accessed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_user (user_id),
    INDEX idx_accessed (accessed_at),
    INDEX idx_user_date (user_id, accessed_at),
    INDEX idx_url (request_url(255)),
    INDEX idx_status (status_code)
);
```

**설계 포인트:**
- 사용자 활동 추적
- 통계 데이터 수집 기반
- 보안 감사 로그

---

## 🔑 도메인별 외래키 전략 요약

| 도메인 | 도메인 내부 관계 | 도메인 간 관계 |
|--------|----------------|---------------|
| **Auth** | - | - |
| **Program** | ✅ JPA (NO_CONSTRAINT) | ❌ ID만 (user_id) |
| **Mileage** | ✅ JPA (NO_CONSTRAINT) | ❌ ID만 (user_id) |
| **Competency** | ✅ JPA (NO_CONSTRAINT) | ❌ ID만 (user_id) |
| **Counseling** | ✅ JPA (NO_CONSTRAINT) | ❌ ID만 (user_id) |
| **Career** | ✅ JPA (NO_CONSTRAINT) | ❌ ID만 (user_id) |
| **File** | - | ❌ ID만 (user_id, related_id) |
| **Notification** | - | ❌ ID만 (user_id, related_id) |
| **System** | - | ❌ ID만 (user_id) |

---

## 🚀 MSA 전환 시나리오

### 현재 (Monolith)
```
┌─────────────────────────────────────────┐
│         SCMS Application                │
│  ┌─────────────────────────────────┐   │
│  │      MySQL (scms_db)            │   │
│  │  • users                        │   │
│  │  • programs                     │   │
│  │  • mileage_accounts             │   │
│  │  • competency_surveys           │   │
│  │  • counseling_requests          │   │
│  │  • portfolios                   │   │
│  │  • files                        │   │
│  │  • notifications                │   │
│  └─────────────────────────────────┘   │
└─────────────────────────────────────────┘
```

### 미래 (MSA)
```
┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│ Auth Service │   │Program Service│   │Mileage Service│
│   (8081)     │   │    (8082)     │   │    (8083)     │
│ ┌──────────┐ │   │ ┌──────────┐ │   │ ┌──────────┐ │
│ │ auth_db  │ │   │ │program_db│ │   │ │mileage_db│ │
│ │• users   │ │   │ │• programs│ │   │ │• accounts│ │
│ └──────────┘ │   │ └──────────┘ │   │ └──────────┘ │
└──────────────┘   └──────────────┘   └──────────────┘

┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│Competency Svc│   │Counseling Svc│   │ Career Svc   │
│   (8084)     │   │    (8085)     │   │    (8086)    │
│ ┌──────────┐ │   │ ┌──────────┐ │   │ ┌──────────┐ │
│ │comp_db   │ │   │ │counsel_db│ │   │ │career_db │ │
│ │• surveys │ │   │ │• requests│ │   │ │• portfolio│ │
│ └──────────┘ │   │ └──────────┘ │   │ └──────────┘ │
└──────────────┘   └──────────────┘   └──────────────┘

┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│  File Svc    │   │Notification  │   │ System Svc   │
│   (8087)     │   │     Svc      │   │    (8089)    │
│ ┌──────────┐ │   │    (8088)    │   │ ┌──────────┐ │
│ │ file_db  │ │   │ ┌──────────┐ │   │ │system_db │ │
│ │• files   │ │   │ │notif_db  │ │   │ │• logs    │ │
│ └──────────┘ │   │ │• notif   │ │   │ └──────────┘ │
└──────────────┘   │ └──────────┘ │   └──────────────┘
                   └──────────────┘
```

---

## 📝 다음 단계

### Phase 1-2: Entity 클래스 생성

**우선순위 순서:**
1. ✅ BaseEntity (공통 Audit)
2. ✅ Auth Domain (User)
3. ✅ Program Domain (3개)
4. ✅ Mileage Domain (3개)
5. ✅ Competency Domain (4개)
6. ✅ Counseling Domain (3개)
7. ✅ Career Domain (3개)
8. ✅ File Management Domain (1개)
9. ✅ Notification Domain (2개)

**총 22개 Entity 클래스**

---

**작성일**: 2025-10-31  
**수정일**: 2025-10-31  
**작성자**: Claude AI  
**버전**: 2.0
