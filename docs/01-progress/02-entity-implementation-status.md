# Entity 구현 진행 상황

## 📊 전체 진행률: 100% (23/23) ✅ 완료!

### ✅ 완료된 Entity (23개)

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

#### 6. Career Domain (3개) ⭐ NEW

##### Entities (3개)
| Entity | 설명 | 주요 필드 | 관계 |
|--------|------|-----------|------|
| CareerPlan | 진로 계획 | userId, title, description, targetDate | goals (OneToMany) |
| CareerGoal | 진로 목표 | careerPlan, goalType, description, priority | careerPlan (ManyToOne), milestones (OneToMany) |
| CareerMilestone | 진로 마일스톤 | goal, description, targetDate, status | goal (ManyToOne) |

##### 주요 특징
- 계획-목표-마일스톤 3단계 구조
- 진행률 자동 계산 (calculateProgress)
- 우선순위 기반 목표 관리
- 마일스톤 완료 추적

---

#### 7. Counseling Domain (4개)

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

#### 8. File Domain (1개) ⭐ NEW

##### Entities (1개)
| Entity | 설명 | 주요 필드 | 관계 |
|--------|------|-----------|------|
| FileMetadata | 파일 메타데이터 | userId, originalFilename, storedFilename, filePath, fileSize | - |

##### 주요 특징
- 파일 업로드/다운로드 정보 관리
- MIME 타입 및 확장자 추적
- 참조 엔티티 연결 (referenceType, referenceId)
- 소프트 삭제 지원
- 파일 크기 MB 변환 메서드

---

#### 9. Notification Domain (2개) ⭐ NEW

##### Entities (2개)
| Entity | 설명 | 주요 필드 | 관계 |
|--------|------|-----------|------|
| Notification | 알림 | userId, type, title, content, isRead | - |
| NotificationTemplate | 알림 템플릿 | code, type, titleTemplate, contentTemplate | - |

##### 주요 특징
- 알림 읽음/미읽음 상태 관리
- 다양한 발송 방법 지원 (WEB, EMAIL, SMS, PUSH)
- 템플릿 기반 알림 생성
- 변수 치환 기능 ({{변수명}})
- 참조 엔티티 연결

---

#### 10. System Domain (2개) ⭐ NEW

##### Entities (2개)
| Entity | 설명 | 주요 필드 | 관계 |
|--------|------|-----------|------|
| SystemLog | 시스템 로그 | logLevel, category, message, stackTrace | - |
| AuditLog | 감사 로그 | userId, action, entityType, entityId, oldValue, newValue | - |

##### 주요 특징
- **SystemLog**: 시스템 레벨 이벤트/에러 추적
  - 로그 레벨별 분류 (DEBUG, INFO, WARN, ERROR, FATAL)
  - 요청 정보 및 스택 트레이스 저장
  - 처리 시간 추적
- **AuditLog**: 사용자 행위 감사
  - CRUD 행위 추적
  - 데이터 변경 이력 (oldValue, newValue)
  - 승인/거부 행위 기록

---

## ✅ 모든 Entity 구현 완료!

### 📦 Domain별 구현 현황

| Domain | Entity 수 | 상태 | 주요 기능 |
|--------|-----------|------|-----------|
| Common | 1 | ✅ | BaseEntity (감사 필드) |
| Auth | 2 | ✅ | 사용자 인증/권한 |
| Program | 6 | ✅ | 프로그램 신청/관리 |
| Mileage | 5 | ✅ | 마일리지 적립/사용 |
| Competency | 6 | ✅ | 역량 진단 설문 |
| Career | 3 | ✅ | 진로 계획 수립 |
| Counseling | 4 | ✅ | 상담 예약/세션 |
| File | 1 | ✅ | 파일 메타데이터 |
| Notification | 2 | ✅ | 알림 관리 |
| System | 2 | ✅ | 로그/감사 |
| **Total** | **23** | **✅** | **전체 완료** |

---

## 🎯 구현 완료 내역

### Phase 1: 기초 인프라 (2개)
- ✅ BaseEntity, UserRole

### Phase 2: 핵심 도메인 (11개)
- ✅ Auth Domain (2개)
- ✅ Program Domain (6개)
- ✅ Mileage Domain (5개)

### Phase 3: 확장 도메인 (10개)
- ✅ Competency Domain (6개)
- ✅ Counseling Domain (4개)

### Phase 4: 최종 도메인 (8개) 🎉
- ✅ Career Domain (3개)
- ✅ File Domain (1개)
- ✅ Notification Domain (2개)
- ✅ System Domain (2개)

---

## 📅 작업 이력

| 날짜 | 작업 내용 | 브랜치 | 완료 Entity |
|------|-----------|--------|-------------|
| 2025-10-31 | 초기 설정 및 기초 Entity | feature/phase1-erd-design | 5개 (BaseEntity, User, UserRole, Program Domain) |
| 2025-10-31 | Mileage Domain 구현 | feature/entity-mileage-domain | 5개 (Mileage Domain 전체) |
| 2025-10-31 | Competency Domain 구현 | feature/entity-competency-domain | 6개 (Competency Domain 전체) |
| 2025-10-31 | Counseling Domain 구현 | feature/entity-counseling-domain | 4개 (Counseling Domain 전체) |
| 2025-10-31 | Career Domain 구현 | feature/entity-career-domain | 3개 (Career Domain 전체) |
| 2025-11-02 | 나머지 Domain 구현 | feature/entity-remaining-domains | 8개 (File, Notification, System) ⭐ 완료 |

---

## 🚀 다음 단계

Entity 레이어 구현이 완료되었습니다! 이제 다음 레이어 구현을 시작할 수 있습니다:

### 1. Repository 레이어
- JpaRepository 인터페이스 작성
- 커스텀 쿼리 메서드 정의
- QueryDSL 설정 (필요시)

### 2. Service 레이어
- 비즈니스 로직 구현
- 트랜잭션 관리
- 도메인 간 협력

### 3. Controller 레이어
- REST API 엔드포인트
- 요청/응답 처리
- 예외 처리

### 4. DTO 및 기타
- DTO 클래스 작성
- Mapper 구현
- Validation 규칙

### 5. 테스트
- 단위 테스트
- 통합 테스트
- API 테스트

---

## 💡 주요 설계 원칙

### 1. MSA 준비 아키텍처
- 도메인별 패키지 분리
- ID 기반 참조 (외래키 제약조건 없음)
- 동일 도메인 내에서만 JPA 관계 매핑

### 2. Rich Domain Model
- 비즈니스 로직을 Entity에 캡슐화
- 도메인 규칙 강제
- 불변성 보장 (필요시)

### 3. 일관된 코딩 스타일
- Lombok 활용 (@Builder, @Getter, @Setter)
- BaseEntity 상속
- 명확한 메서드명과 주석

