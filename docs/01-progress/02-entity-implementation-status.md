# Entity 구현 진행 상황

## 📊 전체 진행률: 65% (15/23)

### ✅ 완료된 Entity (15개)

#### 1. Core Infrastructure (1개)
- **BaseEntity**: 공통 감사 필드 (createdAt, createdBy, updatedAt, updatedBy)
  - 모든 Entity의 기본 클래스
  - 생성/수정 시간 및 작업자 추적

---

#### 2. Auth Domain (2개)

##### Entities (1개)
| Entity | 설명 | 주요 필드 | 관계 |
|--------|------|-----------|------|
| User | 사용자 계정 | username, password, name, email, role | - |

##### Enums (1개)
- **UserRole**: 사용자 역할 (STUDENT, ADMIN, COUNSELOR)

---

#### 3. Program Domain (6개)

##### Entities (3개)
| Entity | 설명 | 주요 필드 | 관계 |
|--------|------|-----------|------|
| Program | 프로그램 정보 | name, description, status, capacity | applications (OneToMany), participants (OneToMany) |
| ProgramApplication | 프로그램 신청 | userId, applicationDate, status | program (ManyToOne) |
| ProgramParticipant | 프로그램 참가자 | userId, attendanceStatus | program (ManyToOne) |

##### Enums (3개)
- **ProgramStatus**: 프로그램 상태 (PLANNED, RECRUITING, IN_PROGRESS, COMPLETED, CANCELLED)
- **ApplicationStatus**: 신청 상태 (PENDING, APPROVED, REJECTED, CANCELLED)
- **AttendanceStatus**: 출석 상태 (ENROLLED, ATTENDED, ABSENT, WITHDRAWN)

##### 주요 특징
- Program-Application-Participant 3단계 프로세스
- 동일 도메인 내 JPA 관계 매핑
- userId는 Long 타입으로 크로스 도메인 참조

---

#### 4. Mileage Domain (5개)

##### Entities (3개)
| Entity | 설명 | 주요 필드 | 관계 |
|--------|------|-----------|------|
| MileageAccount | 마일리지 계좌 | userId, balance, totalEarned | transactions (OneToMany) |
| MileageTransaction | 마일리지 거래 | type, amount, description, balance | account (ManyToOne) |
| CompetencyCertification | 역량 인증 | userId, competencyName, level | account (ManyToOne) |

##### Enums (2개)
- **TransactionType**: 거래 유형 (EARN, SPEND, REFUND, ADJUSTMENT)
- **VerificationStatus**: 인증 상태 (PENDING, VERIFIED, REJECTED, EXPIRED)

##### 주요 특징
- 마일리지 적립/사용 추적
- 잔액 스냅샷 저장 (balance)
- 역량 인증과 마일리지 연계

---

#### 5. Competency Domain (6개)

##### Entities (4개)
| Entity | 설명 | 주요 필드 | 관계 |
|--------|------|-----------|------|
| CompetencySurvey | 역량 진단 설문 | title, description, targetRole, isActive | questions (OneToMany), responses (OneToMany), results (OneToMany) |
| SurveyQuestion | 설문 문항 | survey, questionText, questionType, options | survey (ManyToOne) |
| SurveyResponse | 설문 응답 | userId, responseText, submittedAt | survey (ManyToOne), question (ManyToOne) |
| CompetencyResult | 진단 결과 | userId, totalScore, categoryScores | survey (ManyToOne) |

##### Enums (2개)
- **TargetRole**: 설문 대상 (STUDENT, STAFF, ALL)
- **QuestionType**: 문항 유형 (SINGLE_CHOICE, MULTIPLE_CHOICE, SCALE, TEXT)

##### 주요 특징
- 설문-문항-응답-결과 완전한 프로세스
- JSON 형태로 options, categoryScores 저장
- SurveyResponse는 BaseEntity 미상속 (불변 데이터)
- 비즈니스 메서드 구현 (activate, deactivate, canRespond 등)

---

#### 6. Counseling Domain (4개) ⭐ NEW

##### Entities (3개)
| Entity | 설명 | 주요 필드 | 관계 |
|--------|------|-----------|------|
| CounselingReservation | 상담 예약 | studentId, counselorId, reservationDate, status | sessions (OneToMany) |
| CounselingSession | 상담 세션 | startTime, endTime, sessionNotes, followUpRequired | reservation (ManyToOne) |
| CounselorAvailability | 상담사 가용 시간 | counselorId, dayOfWeek, startTime, endTime | - |

##### Enums (1개)
- **CounselingStatus**: 예약 상태 (PENDING, CONFIRMED, CANCELLED, COMPLETED)

##### 주요 특징
- 예약-세션 2단계 프로세스
- 시간 기반 비즈니스 로직 (취소/수정 가능 여부)
- 상담사 가용 시간 관리
- 후속 상담 예약 기능

---

## ⏳ 남은 Entity (8개)

### 7. Career Domain (3개)
- CareerPlan: 진로 계획
- CareerGoal: 진로 목표
- CareerMilestone: 진로 마일스톤

### 8. File Domain (1개)
- FileMetadata: 파일 메타데이터

### 9. Notification Domain (2개)
- Notification: 알림
- NotificationTemplate: 알림 템플릿

### 10. System Domain (2개)
- SystemLog: 시스템 로그
- AuditLog: 감사 로그

---

## 🎯 다음 작업

**현재 브랜치**: `feature/entity-counseling-domain`

**작업 대상**: Career Domain 3개 Entity 구현
1. CareerPlan
2. CareerGoal
3. CareerMilestone

**예상 완료 후 진행률**: 18/23 (78%)

---

## 📅 작업 이력

| 날짜 | 작업 내용 | 브랜치 | 완료 Entity |
|------|-----------|--------|-------------|
| 2025-01-XX | 초기 설정 및 기초 Entity | feature/phase1-erd-design | 5개 (BaseEntity, User, UserRole, Program Domain) |
| 2025-01-XX | Mileage Domain 구현 | feature/entity-mileage-domain | 5개 (Mileage Domain 전체) |
| 2025-01-XX | Competency Domain 구현 | feature/entity-mileage-domain | 6개 (Competency Domain 전체) |
| 2025-01-XX | Counseling Domain 구현 | feature/entity-counseling-domain | 4개 (Counseling Domain 전체) ⭐ 완료 |
| 진행 중 | Career Domain 구현 | feature/entity-counseling-domain | - |

