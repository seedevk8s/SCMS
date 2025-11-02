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
**main** - 도메인별 패키지 구조 리팩토링 완료

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
- ✅ 전체 도메인 테이블 설계 완료 (23개 테이블, 10개 도메인)
- ✅ ERD 다이어그램 작성
- ✅ 인덱스 전략 정의
- ✅ MSA 전환 시나리오 문서화

### 5. Entity 클래스 생성 - 100% 완료! ✅

#### ✅ 완료된 Entity (30개 - 100%)

##### Common (2개)
- ✅ **BaseEntity** - 공통 Audit 필드 (createdAt, updatedAt)
- ✅ **CommonCode** - 공통 코드 관리 ⭐ NEW

##### Auth Domain (2개)
- ✅ **User** - 사용자 엔티티 (학생, 교직원, 관리자)
- ✅ **UserRole** - 역할 enum

##### Program Domain (9개: 6 Entity + 3 Enum)
- ✅ **Program** - 비교과 프로그램
- ✅ **ProgramApplication** - 프로그램 신청
- ✅ **ProgramParticipant** - 프로그램 참여자
- ✅ **ProgramCategory** - 프로그램 카테고리 ⭐ NEW
- ✅ **ProgramCompetency** - 프로그램-역량 매핑 🆕 추가
- ✅ **ProgramSatisfaction** - 프로그램 만족도 🆕 추가
- ✅ **ProgramStatus**, **ApplicationStatus**, **AttendanceStatus** - Enum

##### Mileage Domain (5개: 3 Entity + 2 Enum)
- ✅ **MileageAccount** - 마일리지 계정
- ✅ **MileageTransaction** - 마일리지 거래 내역
- ✅ **CompetencyCertification** - 역량 인증 기록
- ✅ **TransactionType**, **VerificationStatus** - Enum

##### Competency Domain (7개: 5 Entity + 2 Enum)
- ✅ **CompetencySurvey** - 역량 진단 설문
- ✅ **SurveyQuestion** - 설문 문항
- ✅ **SurveyResponse** - 설문 응답
- ✅ **CompetencyResult** - 진단 결과
- ✅ **AssessmentOption** - 진단 선택지 🆕 추가
- ✅ **TargetRole**, **QuestionType** - Enum

##### Career Domain (3개) 
- ✅ **CareerPlan** - 진로 계획
- ✅ **CareerGoal** - 진로 목표
- ✅ **CareerMilestone** - 진로 마일스톤

##### Counseling Domain (6개: 5 Entity + 1 Enum)
- ✅ **CounselingReservation** - 상담 예약
- ✅ **CounselingSession** - 상담 세션
- ✅ **CounselorAvailability** - 상담사 가용 시간
- ✅ **Counselor** - 상담사 정보 ⭐ NEW
- ✅ **CounselingSatisfaction** - 상담 만족도 🆕 추가
- ✅ **CounselingStatus** - Enum

##### File Domain (1개)
- ✅ **FileMetadata** - 파일 메타데이터

##### Notification Domain (2개)
- ✅ **Notification** - 알림
- ✅ **NotificationTemplate** - 알림 템플릿

##### System Domain (2개)
- ✅ **SystemLog** - 시스템 로그
- ✅ **AuditLog** - 감사 로그
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

##### Counseling Domain (3개 Entity + 1개 Enum)
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

##### Career Domain (3개 Entity)
- ✅ **CareerPlan** - 진로 계획
  - userId (Long) - Auth Domain 참조
  - title, targetField, description - 계획 정보
  - targetDate, status - 목표 및 상태
  - goals - JPA 관계 (OneToMany)
- ✅ **CareerGoal** - 진로 목표
  - careerPlan - JPA 관계 (ManyToOne, 외래키 제약조건 제거)
  - title, description, targetDate
  - status, goalOrder - 상태 및 순서
  - milestones - JPA 관계 (OneToMany)
  - 비즈니스 메서드: isCompleted()
- ✅ **CareerMilestone** - 진로 마일스톤
  - careerGoal - JPA 관계 (ManyToOne, 외래키 제약조건 제거)
  - title, description, targetDate
  - completedDate, isCompleted - 완료 정보
  - milestoneOrder - 순서
  - 비즈니스 메서드: complete()

#### ✅ 모든 도메인 Entity 구현 완료 (26개)

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
- ✅ Counseling Domain 브랜치 생성 및 머지 (feature/entity-counseling-domain)
- ✅ Career Domain 브랜치 생성 및 머지 (feature/entity-career-domain)
- ✅ 도메인별 패키지 구조 리팩토링 완료 (refactor/domain-package-structure)

---

## 🎯 현재 작업: Repository & Service Layer 개발 준비

### 진행 상황: Entity Layer 100% 완료 ✅

#### ✅ 완료된 Entity (30개 - 100%)
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
13. ✅ CounselingReservation + CounselingStatus
14. ✅ CounselingSession
15. ✅ CounselorAvailability
16. ✅ CareerPlan
17. ✅ CareerGoal
18. ✅ CareerMilestone

#### ⭐ 도메인별 패키지 구조 리팩토링 완료
- 모든 엔티티를 도메인별 패키지로 재구성
- MSA 전환 준비 완료
- 패키지 구조: `domain/{domain-name}/entity/`

---

## 💬 세션 재개 시 사용할 멘트

### 📌 추천 멘트 (복사해서 사용)

```
SCMS 프로젝트 이어서 진행하자!

완료:
- ERD 설계 완료 (23개 테이블, 9개 도메인)
- Entity Layer 100% 완료 (26개 엔티티)
- 도메인별 패키지 구조 리팩토링 완료
- Git: main 브랜치에 모든 작업 머지 및 푸시 완료

현재 브랜치: main
현재 작업: Repository & Service Layer 개발 준비
진행률: Entity Layer 100% 완료

프로젝트 위치: C:\Users\USER\Documents\choongang\Project\scms\scms-backend
GitHub: https://github.com/seedevk8s/SCMS.git

다음 단계: Repository 계층 구현 시작!
```

### 간단 버전

```
SCMS Entity Layer 완료! 🎉
완료: 모든 도메인 Entity 26개 + 리팩토링 (100%)
다음: Repository Layer 개발
브랜치: main
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

### Entity 파일 (도메인별 패키지 구조) ✅
```
src/main/java/com/university/scms/domain/
├── common/
│   └── BaseEntity.java                           # ✅ 완료
├── auth/
│   └── entity/
│       ├── User.java                             # ✅ 완료
│       └── UserRole.java                         # ✅ 완료
├── program/
│   └── entity/
│       ├── Program.java                          # ✅ 완료
│       ├── ProgramApplication.java               # ✅ 완료
│       ├── ProgramParticipant.java               # ✅ 완료
│       ├── ProgramStatus.java                    # ✅ 완료
│       ├── ApplicationStatus.java                # ✅ 완료
│       ├── AttendanceStatus.java                 # ✅ 완료
│       └── TargetRole.java                       # ✅ 완료
├── mileage/
│   └── entity/
│       ├── MileageAccount.java                   # ✅ 완료
│       ├── MileageTransaction.java               # ✅ 완료
│       └── TransactionType.java                  # ✅ 완료
├── competency/
│   └── entity/
│       ├── CompetencyCertification.java          # ✅ 완료
│       ├── CompetencySurvey.java                 # ✅ 완료
│       ├── CompetencyResult.java                 # ✅ 완료
│       ├── SurveyQuestion.java                   # ✅ 완료
│       ├── SurveyResponse.java                   # ✅ 완료
│       ├── VerificationStatus.java               # ✅ 완료
│       └── QuestionType.java                     # ✅ 완료
├── counseling/
│   └── entity/
│       ├── CounselingReservation.java            # ✅ 완료
│       ├── CounselingSession.java                # ✅ 완료
│       ├── CounselorAvailability.java            # ✅ 완료
│       └── CounselingStatus.java                 # ✅ 완료
└── career/
    └── entity/
        ├── CareerPlan.java                       # ✅ 완료
        ├── CareerGoal.java                       # ✅ 완료
        └── CareerMilestone.java                  # ✅ 완료
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
- [x] **Entity Layer 100% 완료** (26개 엔티티) ✅
  - [x] BaseEntity
  - [x] Auth Domain (2개)
  - [x] Program Domain (3개 + 4개 Enum)
  - [x] Mileage Domain (3개 + 1개 Enum)
  - [x] Competency Domain (4개 + 3개 Enum)
  - [x] Counseling Domain (3개 + 1개 Enum)
  - [x] Career Domain (3개)
- [x] **도메인별 패키지 구조 리팩토링 완료** ⭐ NEW

### 🔄 진행 중
- [ ] **Repository 계층 구현** ← 다음 단계

### ⏳ 예정
- [ ] Repository 작성
- [ ] Service 계층 구현
- [ ] JWT 인증 시스템 구현
- [ ] 로그인/회원가입 API
- [ ] Phase 2 진행

---

## 🚀 Entity Layer 완료 현황

### ✅ 완료 (26개 - 100%) 🎉

#### Auth Domain (2개)
- [x] User, UserRole

#### Program Domain (7개)
- [x] Program, ProgramApplication, ProgramParticipant
- [x] ProgramStatus, ApplicationStatus, AttendanceStatus, TargetRole

#### Mileage Domain (3개)
- [x] MileageAccount, MileageTransaction, TransactionType

#### Competency Domain (7개)
- [x] CompetencyCertification, CompetencySurvey, CompetencyResult
- [x] SurveyQuestion, SurveyResponse
- [x] VerificationStatus, QuestionType

#### Counseling Domain (4개)
- [x] CounselingReservation, CounselingSession, CounselorAvailability
- [x] CounselingStatus

#### Career Domain (3개)
- [x] CareerPlan, CareerGoal, CareerMilestone

### ⭐ 리팩토링 완료
- [x] 도메인별 패키지 구조로 재구성
- [x] MSA 전환 준비 완료

### ⏳ 다음 단계: Repository Layer
- [ ] 도메인별 Repository 인터페이스 구현
- [ ] 기본 CRUD 메서드 정의
- [ ] Custom Query 메서드 추가

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
- **현재 브랜치**: main
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
**다음 작업**: Repository Layer 구현  
**Phase 1 진행률**: Entity Layer 100% 완료  
**최종 업데이트**: 2025-11-02 (도메인별 패키지 구조 리팩토링 완료)

---

**세션을 재개할 준비가 되었습니다! 🚀**
**진행률: Entity Layer 100% 완료 🎉**
**현재 브랜치: main**
**다음 단계: Repository Layer 개발**
