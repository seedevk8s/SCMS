# SCMS 프로젝트 세션 재개 가이드

## 🎯 현재 프로젝트 상태

### 프로젝트명
**SCMS (Student Competency Management System)** - 학생 역량 관리 시스템

### 프로젝트 위치
```
C:\Users\USER\Documents\choongang\Project\scms\scms-backend
```

### 현재 Phase
**Phase 1: 기반 구축** - 진행 중

### 현재 Git 브랜치
**feature/entity-counseling-domain** - Counseling Domain Entity 작업 완료

### GitHub 저장소
```
https://github.com/seedevk8s/SCMS.git
```

---

## ✅ 완료된 작업 요약

### 1. 프로젝트 설정
- ✅ Spring Boot 3.5.7 + Gradle 프로젝트 생성
- ✅ MySQL 연결 (scms_db)
- ✅ 모든 필수 의존성 설정 완료

### 2. 기본 구조
- ✅ 패키지 구조 설정
- ✅ SecurityConfig 생성 (JWT 준비)
- ✅ HealthCheckController 구현
- ✅ 전역 예외 처리 설정

### 3. 아키텍처 결정
- ✅ **Modular Monolith** 방식 채택
- ✅ MSA 전환 가능하도록 설계 원칙 적용
- ✅ 도메인별 명확한 경계 설정 방침
- ✅ **JPA 전략 확정: 하이브리드 방식**
  - 도메인 내부: JPA 관계 매핑 + 외래키 제약조건 제거
  - 도메인 간: ID만 참조 (JPA 관계 없음)

### 4. ERD 설계 ✅
- ✅ 전체 도메인 테이블 설계 완료 (23개 테이블, 9개 도메인)
- ✅ Auth Domain (1개 테이블)
- ✅ Program Domain (3개 테이블)
- ✅ Mileage Domain (3개 테이블)
- ✅ Competency Domain (4개 테이블)
- ✅ Counseling Domain (3개 테이블)
- ✅ **Career Domain 추가** (3개 테이블) - Portfolio, JobPosting, JobBookmark
- ✅ **File Domain 추가** (1개 테이블) - Files
- ✅ **Notification Domain 추가** (2개 테이블) - Notification, EmailLog
- ✅ **System Domain 추가** (1개 테이블) - AccessLog
- ✅ ERD 다이어그램 작성 (Mermaid)
- ✅ 인덱스 전략 정의
- ✅ MSA 전환 시나리오 문서화

### 5. Entity 클래스 생성 (진행 중)

#### ✅ 완료된 Entity (15개 - 65%)

##### Auth Domain (2개)
- ✅ **BaseEntity** - 공통 Audit 필드 (domain/common/BaseEntity.java)
- ✅ **User** - 사용자 엔티티 (학생, 교직원, 관리자)
- ✅ **UserRole** - 역할 enum

##### Program Domain (3개 Entity + 3개 Enum)
- ✅ **Program** - 비교과 프로그램
  - organizerId (Long) - Auth Domain 참조
  - applications, participants - JPA 관계
  - 비즈니스 메서드: canApply(), isFull(), changeStatus() 등
- ✅ **ProgramApplication** - 프로그램 신청
  - program - JPA 관계 (외래키 제약조건 제거)
  - userId, reviewedBy (Long) - Auth Domain 참조
  - 비즈니스 메서드: approve(), reject(), cancel() 등
- ✅ **ProgramParticipant** - 프로그램 참여자
  - program, application - JPA 관계
  - userId, attendanceConfirmedBy (Long) - Auth Domain 참조
  - 비즈니스 메서드: confirmAttendance(), awardMileage() 등

##### Program Domain Enums (3개)
- ✅ **ProgramStatus** - 프로그램 상태 (DRAFT, OPEN, CLOSED, COMPLETED, CANCELLED)
- ✅ **ApplicationStatus** - 신청 상태 (PENDING, APPROVED, REJECTED, CANCELLED)
- ✅ **AttendanceStatus** - 출석 상태 (REGISTERED, ATTENDED, ABSENT, CANCELLED)

##### Mileage Domain (3개 Entity + 2개 Enum)
- ✅ **MileageAccount** - 마일리지 계정
  - userId (Long) - Auth Domain 참조
  - transactions - JPA 관계 (OneToMany)
  - 비즈니스 메서드: earn(), use(), expire(), adjust()
- ✅ **MileageTransaction** - 마일리지 거래 내역
  - account - JPA 관계 (ManyToOne, 외래키 제약조건 제거)
  - userId (Long) - Auth Domain 참조
  - 출처 추적: sourceType, sourceId
  - 거래 후 잔액 기록: balanceAfter
- ✅ **CompetencyCertification** - 역량 인증
  - userId, verifiedBy, fileId (Long) - 다른 Domain 참조
  - 비즈니스 메서드: approve(), reject(), isExpired()

##### Mileage Domain Enums (2개)
- ✅ **TransactionType** - 거래 유형 (EARN, USE, EXPIRE, ADJUST)
- ✅ **VerificationStatus** - 검증 상태 (PENDING, APPROVED, REJECTED)

##### Competency Domain (4개 Entity + 2개 Enum) ⭐ NEW
- ✅ **CompetencySurvey** - 역량 진단 설문
  - questions, responses, results - JPA 관계 (OneToMany)
  - createdBy (Long) - Auth Domain 참조
  - 비즈니스 메서드: activate(), deactivate(), canRespond(), isWithinPeriod()
- ✅ **SurveyQuestion** - 설문 문항
  - survey - JPA 관계 (ManyToOne, 외래키 제약조건 제거)
  - options (String) - JSON 저장
  - 비즈니스 메서드: update(), changeOrder(), isChoice(), isScale()
- ✅ **SurveyResponse** - 설문 응답
  - BaseEntity 상속 X (수정 불가 데이터)
  - survey, question - JPA 관계
  - userId (Long) - Auth Domain 참조
- ✅ **CompetencyResult** - 진단 결과
  - survey - JPA 관계
  - categoryScores (String) - JSON 저장
  - 비즈니스 메서드: update(), determineLevel()

##### Competency Domain Enums (2개)
- ✅ **TargetRole** - 설문 대상 (STUDENT, STAFF, ALL)
- ✅ **QuestionType** - 문항 유형 (SINGLE_CHOICE, MULTIPLE_CHOICE, SCALE, TEXT)

##### Counseling Domain (3개 Entity + 1개 Enum) ⭐ NEW
- ✅ **CounselingReservation** - 상담 예약
  - studentId, counselorId (Long) - Auth Domain 참조
  - reservationDate, sessionDuration - 예약 정보
  - counselingType, requestReason - 상담 정보
  - status (CounselingStatus) - 예약 상태
  - sessions - JPA 관계 (OneToMany)
  - 비즈니스 메서드: confirm(), cancel(), complete(), canCancel(), canModify()
- ✅ **CounselingSession** - 상담 세션
  - reservation - JPA 관계 (ManyToOne, 외래키 제약조건 제거)
  - startTime, endTime, actualDuration - 세션 시간
  - sessionNotes, counselorNotes - 상담 기록
  - followUpRequired, nextSessionDate - 후속 조치
  - 비즈니스 메서드: start(), end(), updateNotes(), scheduleFollowUp()
- ✅ **CounselorAvailability** - 상담사 가용 시간
  - counselorId (Long) - Auth Domain 참조
  - dayOfWeek, startTime, endTime - 가용 시간
  - isAvailable - 가용 여부
  - 비즈니스 메서드: setAvailable(), isAvailableAt(), overlaps()

##### Counseling Domain Enums (1개)
- ✅ **CounselingStatus** - 예약 상태 (PENDING, CONFIRMED, CANCELLED, COMPLETED)

#### ⏳ 다음 작업: Career Domain (3개)
#### ⏳ 다음 작업: Career Domain (3개)
- [ ] CareerPlan
- [ ] CareerGoal
- [ ] CareerMilestone

#### ⏳ 남은 Entity (8개)
- [ ] Career Domain (3개)
- [ ] File Domain (1개)
- [ ] Notification Domain (2개)
- [ ] System Domain (2개)

### 6. 문서화
- ✅ README.md
- ✅ PROJECT_SETUP_GUIDE.md
- ✅ MSA_ARCHITECTURE_GUIDE.md
- ✅ **docs/02-design/01-erd-design.md** - ERD 설계 완료
- ✅ Phase 1 진행사항 문서

### 7. 테스트
- ✅ 애플리케이션 정상 실행 확인
- ✅ 헬스체크 API 테스트 완료
  - URL: `http://localhost:8080/api/health`
  - 응답: `{"status":"UP", "timestamp":"...", "message":"SCMS API is running"}`

### 8. Git 버전 관리
- ✅ Git 저장소 초기화
- ✅ 원격 저장소 연결 (GitHub)
- ✅ Phase 1 초기 설정 커밋
- ✅ main 브랜치에 푸시 완료
- ✅ ERD 설계 브랜치 생성 및 머지 (feature/phase1-erd-design)
- ✅ Mileage & Competency Domain 브랜치 생성 및 머지 (feature/entity-mileage-domain)
- ✅ Counseling Domain 작업용 브랜치 생성 (feature/entity-counseling-domain)

---

## 🎯 현재 작업: Entity 클래스 생성

### 진행 상황: 15/23 완료 (65%)

#### ✅ 완료 (15개)
1. ✅ BaseEntity
2. ✅ User + UserRole
3. ✅ Program + ProgramStatus
4. ✅ ProgramApplication + ApplicationStatus
5. ✅ ProgramParticipant + AttendanceStatus
6. ✅ MileageAccount + TransactionType
7. ✅ MileageTransaction
8. ✅ CompetencyCertification + VerificationStatus
9. ✅ CompetencySurvey + TargetRole
10. ✅ SurveyQuestion + QuestionType
11. ✅ SurveyResponse
12. ✅ CompetencyResult
13. ✅ CounselingReservation + CounselingStatus ⭐ NEW
14. ✅ CounselingSession ⭐ NEW
15. ✅ CounselorAvailability ⭐ NEW

#### ⏳ 다음 작업: Career Domain (3개)
1. CareerPlan - 진로 계획
2. CareerGoal - 진로 목표
3. CareerMilestone - 진로 마일스톤

---

## 💬 세션 재개 시 사용할 멘트

### 📌 추천 멘트 (복사해서 사용)

```
SCMS 프로젝트 이어서 진행하자!

완료:
- ERD 설계 완료 (23개 테이블, 9개 도메인)
- Entity 생성: Auth, Program, Mileage, Competency, Counseling Domain 완료 (15/23, 65%)
- Git: feature/entity-counseling-domain 작업 완료

현재 브랜치: feature/entity-counseling-domain
현재 작업: Counseling Domain 완료, Career Domain 시작 준비
진행률: 15/23 (65%)

프로젝트 위치: C:\Users\USER\Documents\choongang\Project\scms\scms-backend
GitHub: https://github.com/seedevk8s/SCMS.git

Career Domain Entity 생성 시작!
```

### 간단 버전

```
SCMS Entity 작성 계속!
완료: Auth, Program, Mileage, Competency, Counseling Domain (15/23, 65%)
다음: CareerPlan, CareerGoal, CareerMilestone
브랜치: feature/entity-counseling-domain (완료)
```

---

## 📂 주요 파일 위치

### 문서 파일
```
docs/
├── 00-SESSION_RESUME.md           # 이 문서
├── 01-progress/
│   └── 01-phase1-foundation.md    # Phase 1 진행사항
└── 02-design/
    └── 01-erd-design.md           # ERD 설계 (완료)
```

### Entity 파일 (진행 중)
```
src/main/java/com/university/scms/domain/
├── common/
│   └── BaseEntity.java                           # ✅ 완료
└── entity/
    ├── User.java                                 # ✅ 완료
    ├── UserRole.java                             # ✅ 완료
    ├── Program.java                              # ✅ 완료
    ├── ProgramStatus.java                        # ✅ 완료
    ├── ProgramApplication.java                   # ✅ 완료
    ├── ApplicationStatus.java                    # ✅ 완료
    ├── ProgramParticipant.java                   # ✅ 완료
    ├── AttendanceStatus.java                     # ✅ 완료
    ├── MileageAccount.java                       # ✅ 완료
    ├── MileageTransaction.java                   # ✅ 완료
    ├── TransactionType.java                      # ✅ 완료
    ├── CompetencyCertification.java              # ✅ 완료
    ├── VerificationStatus.java                   # ✅ 완료
    ├── CompetencySurvey.java                     # ✅ 완료
    ├── TargetRole.java                           # ✅ 완료
    ├── SurveyQuestion.java                       # ✅ 완료
    ├── QuestionType.java                         # ✅ 완료
    ├── SurveyResponse.java                       # ✅ 완료
    ├── CompetencyResult.java                     # ✅ 완료
    ├── CounselingStatus.java                     # ✅ 완료 ⭐ NEW
    ├── CounselingReservation.java                # ✅ 완료 ⭐ NEW
    ├── CounselingSession.java                    # ✅ 완료 ⭐ NEW
    ├── CounselorAvailability.java                # ✅ 완료 ⭐ NEW
    ├── CareerPlan.java                           # ⏳ 다음
    ├── CareerGoal.java                           # ⏳ 예정
    └── CareerMilestone.java                      # ⏳ 예정
```

---

## 📊 Phase 1 체크리스트

### ✅ 완료
- [x] 프로젝트 초기 설정
- [x] 데이터베이스 설정
- [x] Spring Security 기본 설정
- [x] 헬스체크 API
- [x] MSA 가이드 문서
- [x] Git 저장소 초기화
- [x] **ERD 설계 완료** (23개 테이블)
- [x] **JPA 전략 확정** (하이브리드)
- [x] **BaseEntity 생성**
- [x] **Auth Domain Entity 완성**
- [x] **Program Domain Entity 완성**
- [x] **Mileage Domain Entity 완성**
- [x] **Competency Domain Entity 완성**
- [x] **Counseling Domain Entity 완성** ⭐ NEW

### 🔄 진행 중
- [ ] **Entity 클래스 작성** ← 현재 단계 (15/23 완료, 65%)
  - [x] BaseEntity
  - [x] Auth Domain (2개)
  - [x] Program Domain (3개)
  - [x] Mileage Domain (3개)
  - [x] Competency Domain (4개)
  - [x] Counseling Domain (4개) ⭐ 완료
  - [ ] Career Domain (3개) ← 다음 작업
  - [ ] File Domain (1개)
  - [ ] Notification Domain (2개)
  - [ ] System Domain (2개)

### ⏳ 예정
- [ ] Repository 작성
- [ ] Service 계층 구현
- [ ] JWT 인증 시스템 구현
- [ ] 로그인/회원가입 API
- [ ] Phase 2 진행

---

## 🚀 Entity 생성 진행 순서

### ✅ 완료 (15개 - 65%)
- [x] BaseEntity
- [x] User, UserRole
- [x] Program, ProgramStatus
- [x] ProgramApplication, ApplicationStatus
- [x] ProgramParticipant, AttendanceStatus
- [x] MileageAccount, TransactionType
- [x] MileageTransaction
- [x] CompetencyCertification, VerificationStatus
- [x] CompetencySurvey, TargetRole
- [x] SurveyQuestion, QuestionType
- [x] SurveyResponse
- [x] CompetencyResult
- [x] CounselingReservation, CounselingStatus ⭐ NEW
- [x] CounselingSession ⭐ NEW
- [x] CounselorAvailability ⭐ NEW

### ⏳ 다음 단계

#### 1. Career Domain (3개) ← 현재 위치
- [ ] CareerPlan
- [ ] CareerGoal
- [ ] CareerMilestone

#### 2. File Domain (1개)
- [ ] FileMetadata

#### 3. Notification Domain (2개)
- [ ] Notification
- [ ] NotificationTemplate

#### 4. System Domain (2개)
- [ ] SystemLog
- [ ] AuditLog

---

## ⚙️ 환경 정보

### 개발 환경
- **IDE**: IntelliJ IDEA
- **Java**: 17
- **Gradle**: 8.5
- **Spring Boot**: 3.5.7

### 데이터베이스
- **DBMS**: MySQL 8.0
- **Database**: scms_db
- **Port**: 3306
- **Username**: root

### 서버
- **Port**: 8080
- **Base URL**: http://localhost:8080

### Git
- **현재 브랜치**: feature/entity-mileage-domain
- **저장소**: https://github.com/seedevk8s/SCMS.git

---

## 🎯 목표

**Phase 1 완료까지 남은 작업:**
1. Entity 클래스 작성 (진행 중 - 15/23 완료, 65%)
2. Repository 인터페이스 작성
3. Service 계층 기본 구조
4. JWT 인증 시스템 구현

**Phase 1 완료 후:**
- Phase 2: 핵심 기능 개발 (비교과 프로그램, 마일리지 등)

---

**작성일**: 2025-01-XX  
**다음 작업**: Career Domain Entity 생성 (3개)  
**예상 소요시간**: Entity 8개 남음  
**최종 업데이트**: 2025-01-XX (Counseling Domain 완성, Career Domain 시작 준비)

---

**세션을 재개할 준비가 되었습니다! 🚀**
**진행률: 15/23 Entity 완료 (65%)**
**현재 브랜치: feature/entity-counseling-domain**
