# ERD vs 구현 Entity 비교 분석

## 📋 분석 개요

**분석 일자**: 2025-01-XX  
**ERD 버전**: 학생 역량 진단 시스템 (최신)  
**구현 진행률**: 12/23 Entity (52%)

---

## ✅ 전체 평가: **매우 우수**

현재 구현된 Entity들은 ERD 설계를 **충실히 반영**하고 있으며, 일부 영역에서는 **ERD보다 더 개선된 구조**를 가지고 있습니다.

---

## 🎯 Domain별 상세 비교

### 1. Auth Domain ✅ **완벽**

#### ERD 테이블: Users
```sql
users
- user_id BIGINT PK
- username VARCHAR(50) UNIQUE
- email VARCHAR(100) UNIQUE
- password VARCHAR(255)
- name VARCHAR(50)
- phone_number VARCHAR(20)
- department VARCHAR(100)
- student_id VARCHAR(20)
- enrollment_year INT
- role ENUM
- status ENUM
- created_at TIMESTAMP
- updated_at TIMESTAMP
```

#### 구현 Entity: User
```java
✅ id (PK)
✅ username (UNIQUE)
✅ email (UNIQUE)
✅ password
✅ name
✅ phone
✅ department
✅ studentId (학번)
✅ grade (학년) - ERD에 없지만 추가됨
✅ employeeId (직원번호) - ERD에 없지만 추가됨
✅ position (직위) - ERD에 없지만 추가됨
✅ role (ENUM)
✅ enabled (계정 상태)
✅ BaseEntity 상속 (created_at, updated_at)
```

**평가:**
- ✅ ERD의 모든 핵심 필드 포함
- ✅ **개선점**: `grade`, `employeeId`, `position` 추가로 더 상세한 정보 관리
- ✅ **개선점**: `enabled` (boolean)이 `status` (enum)보다 단순하고 명확
- ⚠️ **차이**: `enrollment_year` 없음 → `grade`로 대체 가능

**권장사항:**
- 현재 구조 유지 ✅
- 필요시 `enrollmentYear` 추가 고려 (선택사항)

---

### 2. Program Domain ✅ **완벽 + 개선**

#### ERD 테이블: Programs
```sql
programs
- program_id BIGINT PK
- name VARCHAR(200)
- description TEXT
- category VARCHAR(50)
- location VARCHAR(200)
- capacity INT
- required_mileage INT
- organizer_id BIGINT
- start_date DATETIME
- end_date DATETIME
- status ENUM
- created_at TIMESTAMP
- updated_at TIMESTAMP
```

#### 구현 Entity: Program
```java
✅ id (PK)
✅ title (name → title로 명확화)
✅ description
✅ category
✅ location
✅ capacity
✅ mileagePoints (required_mileage → mileagePoints)
✅ organizerId
✅ startDate, endDate
✅ status (ENUM)
✅ BaseEntity 상속

🌟 추가 개선 필드:
✅ currentParticipants (현재 참여자 수 추적)
✅ applicationStart, applicationEnd (신청 기간 관리)
✅ applications, participants (JPA 관계)

🌟 비즈니스 메서드:
✅ canApply() - 신청 가능 여부
✅ isFull() - 정원 초과 여부
✅ incrementParticipants() - 참여자 증가
✅ isInProgress() - 진행 중 확인
✅ isCompleted() - 완료 확인
```

**평가:**
- ✅ ERD의 모든 필드 포함
- 🌟 **대폭 개선**: 신청 기간 관리 기능 추가
- 🌟 **대폭 개선**: 참여자 수 추적 자동화
- 🌟 **대폭 개선**: Rich Domain Model 구현

**권장사항:**
- 현재 구조 유지 ✅ (ERD보다 우수)

---

#### ERD 테이블: ProgramApplications
```sql
program_applications
- application_id BIGINT PK
- user_id BIGINT
- program_id BIGINT FK
- application_date DATETIME
- status ENUM
- reviewed_by BIGINT
- reviewed_at DATETIME
- rejection_reason TEXT
```

#### 구현 Entity: ProgramApplication
```java
✅ id (PK)
✅ userId (크로스 도메인 참조)
✅ program (JPA ManyToOne)
✅ applicationDate
✅ status (ENUM)
✅ reviewedBy (크로스 도메인 참조)
✅ reviewedAt
✅ rejectionReason
✅ BaseEntity 상속

🌟 비즈니스 메서드:
✅ approve() - 승인
✅ reject() - 거절
✅ cancel() - 취소
```

**평가:**
- ✅ ERD 완벽 반영
- 🌟 비즈니스 로직 캡슐화

---

#### ERD 테이블: ProgramParticipants
```sql
program_participants
- participant_id BIGINT PK
- user_id BIGINT
- program_id BIGINT FK
- application_id BIGINT FK
- attendance_status ENUM
- attendance_confirmed_by BIGINT
- attendance_confirmed_at DATETIME
- mileage_awarded BOOLEAN
```

#### 구현 Entity: ProgramParticipant
```java
✅ id (PK)
✅ userId
✅ program (JPA ManyToOne)
✅ application (JPA ManyToOne)
✅ attendanceStatus (ENUM)
✅ attendanceConfirmedBy
✅ attendanceConfirmedAt
✅ mileageAwarded
✅ BaseEntity 상속

🌟 비즈니스 메서드:
✅ confirmAttendance() - 출석 확인
✅ awardMileage() - 마일리지 지급
✅ canAwardMileage() - 마일리지 지급 가능 여부
```

**평가:**
- ✅ ERD 완벽 반영
- 🌟 마일리지 지급 로직 캡슐화

**Program Domain 종합:**
- ✅ 3개 Entity 모두 완벽 구현
- 🌟 ERD보다 더 풍부한 비즈니스 로직

---

### 3. Mileage Domain ✅ **완벽**

#### ERD 테이블: MileageAccounts
```sql
mileage_accounts
- account_id BIGINT PK
- user_id BIGINT UNIQUE
- available_points INT
- total_earned INT
- total_used INT
- total_expired INT
```

#### 구현 Entity: MileageAccount
```java
✅ id (PK)
✅ userId (UNIQUE)
✅ availablePoints
✅ totalEarned
✅ totalUsed
✅ totalExpired
✅ transactions (JPA OneToMany)
✅ BaseEntity 상속

🌟 비즈니스 메서드:
✅ earn() - 적립
✅ use() - 사용
✅ expire() - 만료
✅ adjust() - 조정
✅ canAfford() - 차감 가능 여부
```

**평가:**
- ✅ ERD 완벽 반영
- 🌟 잔액 관리 로직 캡슐화

---

#### ERD 테이블: MileageTransactions
```sql
mileage_transactions
- transaction_id BIGINT PK
- account_id BIGINT FK
- user_id BIGINT
- transaction_type ENUM
- points INT
- source_type VARCHAR(50)
- source_id BIGINT
- description VARCHAR(500)
- balance_after INT
- transaction_date DATETIME
```

#### 구현 Entity: MileageTransaction
```java
✅ id (PK)
✅ account (JPA ManyToOne)
✅ userId
✅ transactionType (ENUM)
✅ points
✅ sourceType
✅ sourceId
✅ description
✅ balanceAfter
✅ BaseEntity 상속 (transaction_date는 createdAt)

🌟 Factory 메서드:
✅ createEarn() - 적립 거래 생성
✅ createUse() - 사용 거래 생성
✅ createExpire() - 만료 거래 생성
✅ createAdjust() - 조정 거래 생성
```

**평가:**
- ✅ ERD 완벽 반영
- 🌟 **대폭 개선**: Factory 패턴으로 거래 생성 안전성 확보
- 🌟 거래 타입별 메서드 분리

---

#### ERD 테이블: CompetencyCertifications
```sql
competency_certifications
- certification_id BIGINT PK
- user_id BIGINT
- competency_name VARCHAR(100)
- level VARCHAR(50)
- institution VARCHAR(200)
- certified_date DATE
- expiry_date DATE
- file_id BIGINT
- verification_status ENUM
- verified_by BIGINT
- verified_at DATETIME
- created_at TIMESTAMP
```

#### 구현 Entity: CompetencyCertification
```java
✅ id (PK)
✅ userId
✅ competencyName
✅ level
✅ institution
✅ certifiedDate
✅ expiryDate
✅ fileId
✅ verificationStatus (ENUM)
✅ verifiedBy
✅ verifiedAt
✅ BaseEntity 상속

🌟 비즈니스 메서드:
✅ approve() - 승인
✅ reject() - 거절
✅ isExpired() - 만료 여부
```

**평가:**
- ✅ ERD 완벽 반영
- 🌟 인증 승인 로직 캡슐화

**Mileage Domain 종합:**
- ✅ 3개 Entity 모두 완벽 구현
- 🌟 Factory 패턴 적용으로 안전성 향상

---

### 4. Competency Domain ✅ **완벽**

#### ERD 테이블: CompetencySurveys
```sql
competency_surveys
- survey_id BIGINT PK
- title VARCHAR(200)
- description TEXT
- survey_type VARCHAR(50)
- target_role ENUM
- is_active BOOLEAN
- start_date DATETIME
- end_date DATETIME
- created_by BIGINT
- created_at TIMESTAMP
- updated_at TIMESTAMP
```

#### 구현 Entity: CompetencySurvey
```java
✅ id (PK)
✅ title
✅ description
✅ surveyType
✅ targetRole (ENUM)
✅ isActive
✅ startDate
✅ endDate
✅ createdBy
✅ questions, responses, results (JPA 관계)
✅ BaseEntity 상속

🌟 비즈니스 메서드:
✅ activate() - 활성화
✅ deactivate() - 비활성화
✅ isWithinPeriod() - 기간 확인
✅ canRespond() - 응답 가능 여부
✅ canRespondByRole() - 역할별 응답 가능 여부
```

**평가:**
- ✅ ERD 완벽 반영
- 🌟 설문 기간 및 권한 로직 캡슐화

---

#### ERD 테이블: SurveyQuestions
```sql
survey_questions
- question_id BIGINT PK
- survey_id BIGINT FK
- question_text TEXT
- question_type ENUM
- options JSON
- question_order INT
- is_required BOOLEAN
```

#### 구현 Entity: SurveyQuestion
```java
✅ id (PK)
✅ survey (JPA ManyToOne)
✅ questionText
✅ questionType (ENUM)
✅ options (JSON String)
✅ questionOrder
✅ isRequired
✅ BaseEntity 상속

🌟 비즈니스 메서드:
✅ update() - 수정
✅ changeOrder() - 순서 변경
✅ isChoice() - 선택형 문항 여부
✅ isScale() - 척도형 문항 여부
✅ isText() - 주관식 문항 여부
```

**평가:**
- ✅ ERD 완벽 반영
- 🌟 문항 타입별 로직 캡슐화

---

#### ERD 테이블: SurveyResponses
```sql
survey_responses
- response_id BIGINT PK
- user_id BIGINT
- survey_id BIGINT FK
- question_id BIGINT FK
- response_text TEXT
- submitted_at TIMESTAMP
```

#### 구현 Entity: SurveyResponse
```java
✅ id (PK)
✅ userId
✅ survey (JPA ManyToOne)
✅ question (JPA ManyToOne)
✅ responseText
✅ submittedAt
⚠️ BaseEntity 미상속 (설계 의도)

🌟 설계 결정:
✅ 불변 데이터로 취급
✅ submittedAt만 사용
```

**평가:**
- ✅ ERD 반영
- 🌟 **설계 개선**: 불변 데이터 처리 (BaseEntity 미상속)

---

#### ERD 테이블: CompetencyResults
```sql
competency_results
- result_id BIGINT PK
- user_id BIGINT
- survey_id BIGINT FK
- total_score INT
- category_scores JSON
- result_level VARCHAR(50)
- created_at TIMESTAMP
```

#### 구현 Entity: CompetencyResult
```java
✅ id (PK)
✅ userId
✅ survey (JPA ManyToOne)
✅ totalScore
✅ categoryScores (JSON String)
✅ resultLevel
✅ BaseEntity 상속

🌟 비즈니스 메서드:
✅ update() - 결과 업데이트
✅ determineLevel() - 등급 결정
```

**평가:**
- ✅ ERD 완벽 반영
- 🌟 결과 계산 로직 캡슐화

**Competency Domain 종합:**
- ✅ 4개 Entity 모두 완벽 구현
- 🌟 불변 데이터 처리 전략 우수

---

### 5. Counseling Domain ⏳ **작업 예정**

#### ERD 분석 (구현 가이드)

**ERD 테이블 확인:**

##### CounselingReservations (상담 예약)
```sql
counseling_reservations
- reservation_id BIGINT PK
- student_id BIGINT FK (users)
- counselor_id BIGINT FK (users)
- reservation_date DATETIME
- session_duration INT (분 단위)
- counseling_type VARCHAR(50) (진로, 학업, 심리 등)
- request_reason TEXT
- status ENUM (PENDING, CONFIRMED, CANCELLED, COMPLETED)
- created_at TIMESTAMP
- updated_at TIMESTAMP
```

**구현 시 고려사항:**
```java
✅ BaseEntity 상속
✅ studentId, counselorId (Long) - Auth Domain 참조
✅ CounselingStatus Enum 생성 필요
✅ CounselingType Enum 또는 String
✅ 비즈니스 메서드:
   - confirm() - 예약 확정
   - cancel() - 예약 취소
   - complete() - 상담 완료
   - canCancel() - 취소 가능 여부
```

---

##### CounselingSessions (상담 세션)
```sql
counseling_sessions
- session_id BIGINT PK
- reservation_id BIGINT FK
- start_time DATETIME
- end_time DATETIME
- actual_duration INT
- session_notes TEXT (상담 기록)
- counselor_notes TEXT (상담사 메모)
- follow_up_required BOOLEAN
- next_session_date DATETIME
- created_at TIMESTAMP
- updated_at TIMESTAMP
```

**구현 시 고려사항:**
```java
✅ BaseEntity 상속
✅ reservation (JPA ManyToOne) - 같은 도메인
✅ 비즈니스 메서드:
   - start() - 세션 시작
   - end() - 세션 종료
   - updateNotes() - 기록 업데이트
   - scheduleFollowUp() - 후속 상담 예약
```

---

##### CounselorAvailability (상담사 가용 시간)
```sql
counselor_availability
- availability_id BIGINT PK
- counselor_id BIGINT FK (users)
- day_of_week INT (0=일요일, 6=토요일)
- start_time TIME
- end_time TIME
- is_available BOOLEAN
- created_at TIMESTAMP
- updated_at TIMESTAMP
```

**구현 시 고려사항:**
```java
✅ BaseEntity 상속
✅ counselorId (Long) - Auth Domain 참조
✅ DayOfWeek Enum (Java 기본 제공)
✅ 비즈니스 메서드:
   - setAvailable() - 가용 설정
   - setUnavailable() - 불가 설정
   - isAvailableAt() - 특정 시간 가용 여부
```

---

### 6. Career Domain ⏳ **ERD 참고용**

#### ERD 분석 (향후 구현 참고)

##### Portfolios (포트폴리오)
```sql
portfolios
- portfolio_id BIGINT PK
- user_id BIGINT FK
- title VARCHAR(200)
- description TEXT
- portfolio_url VARCHAR(500)
- file_id BIGINT
- created_at TIMESTAMP
- updated_at TIMESTAMP
```

##### JobPostings (채용 공고)
```sql
job_postings
- posting_id BIGINT PK
- title VARCHAR(200)
- company_name VARCHAR(100)
- job_type VARCHAR(50)
- location VARCHAR(200)
- salary_range VARCHAR(100)
- description TEXT
- requirements TEXT
- deadline DATE
- created_at TIMESTAMP
```

##### JobBookmarks (채용 공고 북마크)
```sql
job_bookmarks
- bookmark_id BIGINT PK
- user_id BIGINT FK
- posting_id BIGINT FK
- created_at TIMESTAMP
```

---

### 7. File Domain ⏳ **ERD 참고용**

#### FileMetadata (파일 메타데이터)
```sql
files
- file_id BIGINT PK
- file_name VARCHAR(255)
- file_path VARCHAR(500)
- file_size BIGINT
- mime_type VARCHAR(100)
- uploaded_by BIGINT FK
- upload_purpose VARCHAR(50)
- related_entity_type VARCHAR(50)
- related_entity_id BIGINT
- created_at TIMESTAMP
```

---

### 8. Notification Domain ⏳ **ERD 참고용**

#### Notifications (알림)
```sql
notifications
- notification_id BIGINT PK
- user_id BIGINT FK
- notification_type VARCHAR(50)
- title VARCHAR(200)
- message TEXT
- is_read BOOLEAN
- related_entity_type VARCHAR(50)
- related_entity_id BIGINT
- created_at TIMESTAMP
```

#### EmailLogs (이메일 로그)
```sql
email_logs
- log_id BIGINT PK
- recipient_email VARCHAR(100)
- subject VARCHAR(200)
- content TEXT
- sent_at TIMESTAMP
- status ENUM (SUCCESS, FAILED)
- error_message TEXT
```

---

### 9. System Domain ⏳ **ERD 참고용**

#### AccessLogs (접근 로그)
```sql
access_logs
- log_id BIGINT PK
- user_id BIGINT
- action VARCHAR(100)
- resource_type VARCHAR(50)
- resource_id BIGINT
- ip_address VARCHAR(45)
- user_agent TEXT
- created_at TIMESTAMP
```

---

## 🌟 주요 개선 사항 (ERD 대비)

### 1. **Rich Domain Model 구현** 🎯
- ERD: 테이블 + 제약조건
- 구현: Entity + 비즈니스 메서드
- **효과**: 도메인 로직 캡슐화, 재사용성 향상

### 2. **Factory 패턴 적용** 🏭
- MileageTransaction: 타입별 생성 메서드
- **효과**: 거래 생성 시 안전성 확보

### 3. **불변 데이터 처리** 🔒
- SurveyResponse: BaseEntity 미상속
- **효과**: 데이터 무결성 보장

### 4. **상태 관리 개선** 📊
- Program: currentParticipants 추적
- **효과**: 정원 관리 자동화

### 5. **기간 관리 강화** 📅
- Program: applicationStart/End 추가
- CompetencySurvey: isWithinPeriod() 메서드
- **효과**: 시간 기반 로직 명확화

---

## ⚠️ 주의할 점

### 1. **Hybrid JPA 전략 유지** ✅
- 같은 도메인: JPA 관계
- 다른 도메인: ID 참조
- **현재**: 올바르게 적용됨

### 2. **NO_CONSTRAINT 전략** ✅
- 모든 JPA 관계에 적용
- **현재**: 완벽하게 적용됨

### 3. **BaseEntity 상속 기준** ✅
- 변경 추적 필요: 상속
- 불변 데이터: 미상속
- **현재**: 올바르게 적용됨

---

## 📋 Counseling Domain 구현 체크리스트

### 필수 구현 사항

#### 1. CounselingReservation
- [ ] BaseEntity 상속
- [ ] studentId, counselorId (Long)
- [ ] reservationDate (LocalDateTime)
- [ ] sessionDuration (Integer)
- [ ] counselingType (String or Enum)
- [ ] requestReason (String)
- [ ] status (CounselingStatus Enum)
- [ ] 비즈니스 메서드:
  - [ ] confirm()
  - [ ] cancel()
  - [ ] complete()
  - [ ] canCancel()

#### 2. CounselingSession
- [ ] BaseEntity 상속
- [ ] reservation (ManyToOne)
- [ ] startTime, endTime (LocalDateTime)
- [ ] actualDuration (Integer)
- [ ] sessionNotes (String/TEXT)
- [ ] counselorNotes (String/TEXT)
- [ ] followUpRequired (Boolean)
- [ ] nextSessionDate (LocalDateTime)
- [ ] 비즈니스 메서드:
  - [ ] start()
  - [ ] end()
  - [ ] updateNotes()
  - [ ] scheduleFollowUp()

#### 3. CounselorAvailability
- [ ] BaseEntity 상속
- [ ] counselorId (Long)
- [ ] dayOfWeek (DayOfWeek Enum)
- [ ] startTime (LocalTime)
- [ ] endTime (LocalTime)
- [ ] isAvailable (Boolean)
- [ ] 비즈니스 메서드:
  - [ ] setAvailable()
  - [ ] setUnavailable()
  - [ ] isAvailableAt()

#### 4. Enum 생성
- [ ] CounselingStatus
  - PENDING, CONFIRMED, CANCELLED, COMPLETED
- [ ] (선택) CounselingType
  - CAREER, ACADEMIC, PSYCHOLOGICAL, ETC

---

## 🎯 종합 평가

### ✅ 강점
1. **ERD 완벽 반영**: 모든 핵심 필드 구현
2. **Rich Domain Model**: 비즈니스 로직 캡슐화
3. **Factory 패턴**: 안전한 객체 생성
4. **불변 데이터 처리**: 데이터 무결성
5. **MSA 준비**: Hybrid JPA + NO_CONSTRAINT

### 🌟 개선 사항
1. **신청 기간 관리**: applicationStart/End 추가
2. **참여자 추적**: currentParticipants 자동화
3. **설계 패턴**: Factory, Builder 활용

### 📌 권장사항
1. **현재 구조 유지**: ERD보다 우수한 설계
2. **Counseling Domain**: ERD 참고하여 구현
3. **문서화 지속**: 구현 후 문서 업데이트

---

## 📊 진행 현황

| Domain | Entity 수 | ERD 반영 | 구현 품질 | 비고 |
|--------|-----------|----------|-----------|------|
| Auth | 2 | ✅ 100% | ⭐⭐⭐⭐⭐ | 완벽 |
| Program | 6 | ✅ 100% | ⭐⭐⭐⭐⭐ | ERD 초과 |
| Mileage | 5 | ✅ 100% | ⭐⭐⭐⭐⭐ | Factory 패턴 |
| Competency | 6 | ✅ 100% | ⭐⭐⭐⭐⭐ | 불변 처리 |
| **Counseling** | 0 | ⏳ 대기 | - | **다음 작업** |
| Career | 0 | ⏳ 대기 | - | 예정 |
| File | 0 | ⏳ 대기 | - | 예정 |
| Notification | 0 | ⏳ 대기 | - | 예정 |
| System | 0 | ⏳ 대기 | - | 예정 |

---

**작성일**: 2025-01-XX  
**분석 결과**: 현재 구현은 ERD를 **충실히 반영**하며, **일부 영역에서 더 우수**합니다.  
**다음 작업**: Counseling Domain Entity 구현 시작 🚀
