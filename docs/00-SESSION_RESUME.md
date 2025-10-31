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
**feature/phase1-erd-design** - Entity 클래스 생성 작업 중

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

#### ✅ 완료된 Entity (5개 - 22%)

##### Auth Domain (2개)
- ✅ **BaseEntity** - 공통 Audit 필드 (domain/common/BaseEntity.java)
- ✅ **User** - 사용자 엔티티 (학생, 교직원, 관리자)
- ✅ **UserRole** - 역할 enum

##### Program Domain (3개)
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

#### ⏳ 다음 작업: Mileage Domain (3개)
- [ ] MileageAccount
- [ ] MileageTransaction
- [ ] CompetencyCertification

#### ⏳ 남은 Entity (18개)
- [ ] Competency Domain (4개)
- [ ] Counseling Domain (3개)
- [ ] Career Domain (3개)
- [ ] File Domain (1개)
- [ ] Notification Domain (2개)
- [ ] System Domain (1개)

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
- ✅ ERD 설계용 브랜치 생성 (feature/phase1-erd-design)

---

## 🎯 현재 작업: Entity 클래스 생성

### 진행 상황: 5/23 완료 (22%)

#### ✅ 완료 (5개)
1. ✅ BaseEntity
2. ✅ User
3. ✅ Program
4. ✅ ProgramApplication
5. ✅ ProgramParticipant

#### ⏳ 다음 작업: Mileage Domain (3개)
1. MileageAccount - 마일리지 계정
2. MileageTransaction - 마일리지 거래 내역
3. CompetencyCertification - 역량 인증

### JPA 하이브리드 전략 구현 예시

#### Program Entity (완료)
```java
@Entity
public class Program extends BaseEntity {
    // ✅ 다른 도메인: ID만 저장
    @Column(name = "organizer_id")
    private Long organizerId;  // User ID (외래키 없음)
    
    // ✅ 같은 도메인: JPA 관계 (외래키 제약조건 제거)
    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL)
    private List<ProgramApplication> applications;
}
```

#### ProgramApplication Entity (완료)
```java
@Entity
public class ProgramApplication extends BaseEntity {
    // ✅ 같은 도메인: JPA 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id",
                foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Program program;
    
    // ✅ 다른 도메인: ID만 저장
    @Column(name = "user_id")
    private Long userId;  // User ID (외래키 없음)
}
```

### 남은 Entity 목록 (18개)

#### Mileage Domain (3개) ← 현재 위치
- [ ] MileageAccount
- [ ] MileageTransaction
- [ ] CompetencyCertification

#### Competency Domain (4개)
- [ ] CompetencySurvey
- [ ] SurveyQuestion
- [ ] SurveyResponse
- [ ] CompetencyResult

#### Counseling Domain (3개)
- [ ] CounselingRequest
- [ ] CounselingSession
- [ ] CounselingNote

#### Career Domain (3개)
- [ ] Portfolio
- [ ] JobPosting
- [ ] JobBookmark

#### File Domain (1개)
- [ ] FileEntity

#### Notification Domain (2개)
- [ ] Notification
- [ ] EmailLog

#### System Domain (1개)
- [ ] AccessLog

---

## 💬 세션 재개 시 사용할 멘트

### 📌 추천 멘트 (복사해서 사용)

```
안녕! SCMS 프로젝트 이어서 진행하자.

지난 세션에서:
- Spring Boot 3.5.7 프로젝트 초기 설정 완료
- ERD 설계 완료 (23개 테이블, 9개 도메인)
- JPA 하이브리드 전략 확정
- Entity 생성: BaseEntity, User, Program Domain 완료 (5/23)

현재 브랜치: feature/phase1-erd-design
현재 작업: Entity 클래스 생성 중 (Mileage Domain부터)
진행률: 5/23 (22%)

프로젝트 위치: C:\Users\USER\Documents\choongang\Project\scms\scms-backend
GitHub: https://github.com/seedevk8s/SCMS.git

Mileage Domain Entity 생성부터 시작하자!
```

### 또는 간단하게

```
SCMS 프로젝트 계속하자!
현재: Entity 생성 중 (5/23 완료, Mileage Domain부터 시작)
브랜치: feature/phase1-erd-design
경로: C:\Users\USER\Documents\choongang\Project\scms\scms-backend
```

### 또는 매우 간단하게

```
SCMS Entity 작성 계속!
완료: BaseEntity, User, Program Domain (5/23)
다음: MileageAccount, MileageTransaction, CompetencyCertification
경로: C:\Users\USER\Documents\choongang\Project\scms\scms-backend
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

MSA_ARCHITECTURE_GUIDE.md          # MSA 가이드
PROJECT_SETUP_GUIDE.md             # 초기 설정 가이드
README.md                          # 프로젝트 소개
```

### Entity 파일 (진행 중)
```
src/main/java/com/university/scms/domain/
├── common/
│   └── BaseEntity.java                    # ✅ 완료
└── entity/
    ├── User.java                          # ✅ 완료
    ├── UserRole.java                      # ✅ 완료
    ├── Program.java                       # ✅ 완료
    ├── ProgramStatus.java                 # ✅ 완료
    ├── ProgramApplication.java            # ✅ 완료
    ├── ApplicationStatus.java             # ✅ 완료
    ├── ProgramParticipant.java            # ✅ 완료
    ├── AttendanceStatus.java              # ✅ 완료
    ├── MileageAccount.java                # ⏳ 다음
    ├── MileageTransaction.java            # ⏳ 예정
    └── CompetencyCertification.java       # ⏳ 예정
```

### 주요 소스 파일
```
src/main/java/com/university/scms/
├── ScmsApplication.java
├── config/
│   ├── JpaConfig.java
│   └── SecurityConfig.java
├── controller/
│   └── HealthCheckController.java
└── exception/
    └── GlobalExceptionHandler.java
```

---

## 🔍 현재 프로젝트 확인 방법

### IntelliJ에서 프로젝트 열기
1. IntelliJ IDEA 실행
2. File → Open
3. `C:\Users\USER\Documents\choongang\Project\scms\scms-backend` 선택

### 완성된 Entity 확인
```
src/main/java/com/university/scms/domain/
├── common/BaseEntity.java              # Audit 필드
├── entity/User.java                    # 사용자 (학생, 교직원, 관리자)
├── entity/UserRole.java                # 역할 enum
├── entity/Program.java                 # 비교과 프로그램
├── entity/ProgramApplication.java      # 프로그램 신청
└── entity/ProgramParticipant.java      # 프로그램 참여자
```

### ERD 설계 확인
```
docs/02-design/01-erd-design.md    # 전체 테이블 구조 및 다이어그램
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
- [x] main 브랜치 푸시
- [x] ERD 설계 브랜치 생성
- [x] **ERD 설계 완료** (23개 테이블)
- [x] **JPA 전략 확정** (하이브리드)
- [x] **BaseEntity 생성**
- [x] **Auth Domain Entity 완성** (User, UserRole)
- [x] **Program Domain Entity 완성** (Program, ProgramApplication, ProgramParticipant)

### 🔄 진행 중
- [ ] **Entity 클래스 작성** ← 현재 단계 (5/23 완료, 22%)
  - [x] BaseEntity
  - [x] Auth Domain (2개)
  - [x] Program Domain (3개)
  - [ ] Mileage Domain (3개) ← 다음 작업
  - [ ] Competency Domain (4개)
  - [ ] Counseling Domain (3개)
  - [ ] Career Domain (3개)
  - [ ] File Domain (1개)
  - [ ] Notification Domain (2개)
  - [ ] System Domain (1개)

### ⏳ 예정
- [ ] Repository 작성
- [ ] Service 계층 구현
- [ ] JWT 인증 시스템 구현
- [ ] 로그인/회원가입 API
- [ ] Phase 2 진행

---

## 💡 중요 설계 원칙 (재확인)

### Program Domain 구현 예시 (완료)

#### 1. 도메인 내부 관계 (JPA 사용)
```java
@Entity
public class Program extends BaseEntity {
    // 같은 도메인: JPA 관계 + 외래키 제약조건 제거
    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL)
    private List<ProgramApplication> applications;
}

@Entity
public class ProgramApplication extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id",
                foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Program program;
}
```

#### 2. 도메인 간 참조 (ID만 저장)
```java
@Entity
public class Program extends BaseEntity {
    // 다른 도메인(Auth): ID만 저장
    @Column(name = "organizer_id")
    private Long organizerId;  // User ID (외래키 없음)
}

@Entity
public class ProgramApplication extends BaseEntity {
    @Column(name = "user_id")
    private Long userId;  // User ID (외래키 없음)
    
    @Column(name = "reviewed_by")
    private Long reviewedBy;  // User ID (외래키 없음)
}
```

#### 3. 비즈니스 메서드 포함
```java
@Entity
public class Program extends BaseEntity {
    public boolean canApply() { ... }
    public boolean isFull() { ... }
    public void changeStatus(ProgramStatus newStatus) { ... }
}

@Entity
public class ProgramApplication extends BaseEntity {
    public void approve(Long reviewerId) { ... }
    public void reject(Long reviewerId, String reason) { ... }
}
```

---

## 🎓 참고할 문서

### 프로젝트 내부 문서
1. **docs/02-design/01-erd-design.md** - 전체 ERD 설계
2. **MSA_ARCHITECTURE_GUIDE.md** - MSA 전환 전략
3. **docs/01-progress/01-phase1-foundation.md** - 상세 진행사항

### Google Drive 문서
- 원본 기획 문서: `1LPxYcGUIk_J7sn4BlCQeZrpfCZGavj8dZMRhIfEAAh4`

---

## 🚀 Entity 생성 진행 순서

### ✅ 완료 (5개)
- [x] BaseEntity
- [x] User
- [x] UserRole (enum)
- [x] Program
- [x] ProgramApplication
- [x] ProgramParticipant

### ⏳ 다음 단계

#### 1. Mileage Domain (3개) ← 현재 위치
- [ ] MileageAccount - 마일리지 계정
- [ ] MileageTransaction - 마일리지 거래 내역
- [ ] CompetencyCertification - 역량 인증

#### 2. Competency Domain (4개)
- [ ] CompetencySurvey
- [ ] SurveyQuestion
- [ ] SurveyResponse
- [ ] CompetencyResult

#### 3. Counseling Domain (3개)
- [ ] CounselingRequest
- [ ] CounselingSession
- [ ] CounselingNote

#### 4. Career Domain (3개)
- [ ] Portfolio
- [ ] JobPosting
- [ ] JobBookmark

#### 5. File Domain (1개)
- [ ] FileEntity

#### 6. Notification Domain (2개)
- [ ] Notification
- [ ] EmailLog

#### 7. System Domain (1개)
- [ ] AccessLog

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
- **현재 브랜치**: feature/phase1-erd-design
- **저장소**: https://github.com/seedevk8s/SCMS.git

---

## 📞 문제 해결

### 애플리케이션이 실행 안 될 때
1. MySQL 서버 실행 확인
2. application.yml의 DB 정보 확인
3. Gradle 동기화: `./gradlew clean build`

### ERD 확인이 필요할 때
```
docs/02-design/01-erd-design.md  # 전체 테이블 구조
```

### 완성된 Entity 참고
```
src/main/java/com/university/scms/domain/entity/
├── Program.java              # 비즈니스 메서드 참고
├── ProgramApplication.java   # 상태 관리 참고
└── ProgramParticipant.java   # 출석/평가 로직 참고
```

---

## ✨ 다음 세션 준비사항

### Claude에게 알려줄 정보
1. 프로젝트 경로
2. 현재 작업 단계 (Entity 생성 중 - 5/23 완료)
3. 다음 작업: Mileage Domain Entity 3개

### 필요한 도구
- IntelliJ IDEA 실행
- MySQL 서버 실행
- ERD 설계 문서 참고 (docs/02-design/01-erd-design.md)

---

## 🎯 목표

**Phase 1 완료까지 남은 작업:**
1. Entity 클래스 작성 (진행 중 - 5/23 완료, 22%)
2. Repository 인터페이스 작성
3. Service 계층 기본 구조
4. JWT 인증 시스템 구현

**Phase 1 완료 후:**
- Phase 2: 핵심 기능 개발 (비교과 프로그램, 마일리지 등)

---

**작성일**: 2025-10-31  
**다음 작업**: Mileage Domain Entity 생성  
**예상 소요시간**: Entity 18개 남음 (약 50-70분)  
**최종 업데이트**: 2025-10-31 (Program Domain 완성, Mileage Domain 시작 직전)

---

**세션을 재개할 준비가 되었습니다! 🚀**
**진행률: 5/23 Entity 완료 (22%)**
