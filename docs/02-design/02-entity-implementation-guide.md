# Entity 구현 가이드

## 🎯 Entity 구현 원칙

### 1. Hybrid JPA 전략

#### 동일 도메인 내 관계
- **JPA 관계 매핑 사용**: `@OneToMany`, `@ManyToOne`, `@OneToOne`
- **외래키 제약 제거**: `@JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))`
- **양방향 관계 구현**: 부모-자식 관계에서 편의 메서드 제공

**예시 (Program Domain)**
```java
// Program Entity
@OneToMany(mappedBy = "program")
private List<ProgramApplication> applications = new ArrayList<>();

@OneToMany(mappedBy = "program")
private List<ProgramParticipant> participants = new ArrayList<>();

// ProgramApplication Entity
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "program_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
private Program program;
```

#### 크로스 도메인 참조
- **ID만 저장**: Long 타입으로 다른 도메인의 Entity ID 참조
- **JPA 관계 없음**: 느슨한 결합 유지

**예시**
```java
// ProgramApplication - User 참조
private Long userId;  // Auth Domain의 User ID

// MileageAccount - User 참조
private Long userId;  // Auth Domain의 User ID
```

---

### 2. BaseEntity 상속 전략

#### 상속하는 경우
- 생성일시/수정일시가 필요한 Entity
- 변경 이력을 추적해야 하는 Entity
- 대부분의 Entity가 해당

```java
@Entity
@Table(name = "programs")
public class Program extends BaseEntity {
    // ...
}
```

#### 상속하지 않는 경우
- **불변 데이터**: 한번 생성되면 수정되지 않는 Entity
- **로그성 데이터**: 기록만 남기는 Entity

**예시: SurveyResponse**
```java
@Entity
@Table(name = "survey_responses")
public class SurveyResponse {  // BaseEntity 상속 X
    
    @Column(nullable = false)
    private LocalDateTime submittedAt;  // 제출 시간만 기록
}
```

---

### 3. NO_CONSTRAINT 전략

모든 JPA 관계에서 외래키 제약 제거:

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(
    name = "program_id",
    foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
)
private Program program;
```

**이유:**
- MSA 전환 시 도메인 간 독립성 확보
- 데이터베이스 수준의 결합도 최소화
- 서비스 레벨에서 참조 무결성 관리

---

## 📋 Domain별 구현 패턴

### Program Domain (완료)

**특징:**
- 프로그램 생명주기 관리
- 신청 → 승인 → 참가 프로세스
- 상태 전환 비즈니스 로직

**핵심 패턴:**
```java
// 1. Enum 활용
private ProgramStatus status;
private ApplicationStatus applicationStatus;

// 2. 양방향 관계
@OneToMany(mappedBy = "program")
private List<ProgramApplication> applications = new ArrayList<>();

// 3. 비즈니스 메서드
public void recruit() {
    if (this.status != ProgramStatus.PLANNED) {
        throw new IllegalStateException("...");
    }
    this.status = ProgramStatus.RECRUITING;
}
```

---

### Mileage Domain (완료)

**특징:**
- 계좌-거래 이중 구조
- 잔액 스냅샷 저장
- 역량 인증과 연계

**핵심 패턴:**
```java
// 1. 잔액 추적
@Column(nullable = false)
private Integer balance;  // 현재 잔액

@Column(nullable = false)
private Integer totalEarned;  // 누적 적립

// 2. 거래 이력
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "account_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
private MileageAccount account;

// 3. 비즈니스 메서드
public void earnPoints(Integer amount, String description) {
    MileageTransaction transaction = MileageTransaction.builder()
        .account(this)
        .type(TransactionType.EARN)
        .amount(amount)
        .description(description)
        .balance(this.balance + amount)
        .build();
    
    this.balance += amount;
    this.totalEarned += amount;
    this.transactions.add(transaction);
}
```

---

### Competency Domain (완료)

**특징:**
- 설문-문항-응답-결과 완전한 프로세스
- JSON 데이터 저장 (options, categoryScores)
- 불변 응답 데이터 (BaseEntity 미상속)

**핵심 패턴:**
```java
// 1. JSON 데이터 저장
@Column(columnDefinition = "JSON")
private String options;  // 선택지 JSON

@Column(columnDefinition = "JSON")
private String categoryScores;  // 영역별 점수 JSON

// 2. 불변 Entity (SurveyResponse)
@Entity
public class SurveyResponse {  // BaseEntity 상속 X
    private LocalDateTime submittedAt;
}

// 3. 비즈니스 메서드
public void activate() {
    this.isActive = true;
}

public boolean canRespond() {
    return isActive && isWithinPeriod();
}

public boolean isWithinPeriod() {
    LocalDateTime now = LocalDateTime.now();
    return (startDate == null || !now.isBefore(startDate))
        && (endDate == null || !now.isAfter(endDate));
}
```

---

## 🔧 구현 체크리스트

### Entity 생성 시
- [ ] 적절한 테이블명 지정 (`@Table(name = "...")`)
- [ ] BaseEntity 상속 여부 결정
- [ ] 기본키 전략 (GenerationType.IDENTITY)
- [ ] 필수 필드 not null 설정
- [ ] 적절한 컬럼 타입 지정

### 관계 매핑 시
- [ ] 동일/크로스 도메인 판단
- [ ] 동일 도메인: JPA 관계 + NO_CONSTRAINT
- [ ] 크로스 도메인: Long ID 참조
- [ ] FetchType.LAZY 기본 사용
- [ ] 양방향 관계: 편의 메서드 제공

### Enum 생성 시
- [ ] 명확한 이름과 설명
- [ ] 생성자와 getter
- [ ] 필요시 from() 메서드

### 비즈니스 로직
- [ ] 상태 전환 메서드
- [ ] 유효성 검증 메서드
- [ ] 계산 로직 (가능하면 Entity에)

---

## 📦 패키지 구조

```
com.university.scms.domain.entity/
├── BaseEntity.java
├── User.java
├── UserRole.java
├── Program.java
├── ProgramApplication.java
├── ProgramParticipant.java
├── ProgramStatus.java
├── ApplicationStatus.java
├── AttendanceStatus.java
├── MileageAccount.java
├── MileageTransaction.java
├── CompetencyCertification.java
├── TransactionType.java
├── VerificationStatus.java
├── CompetencySurvey.java
├── SurveyQuestion.java
├── SurveyResponse.java
├── CompetencyResult.java
├── TargetRole.java
└── QuestionType.java
```

**원칙:**
- 모든 Entity와 Enum을 단일 패키지에 배치
- 향후 도메인별 패키지 분리 가능 (MSA 전환 시)

---

## 🚀 다음 구현 대상

### Counseling Domain
- CounselingReservation
- CounselingSession
- CounselorAvailability

### 예상 패턴
- 예약-세션 관계
- 상담사 가용 시간 관리
- userId, counselorId로 크로스 도메인 참조
- 상태 전환 비즈니스 로직

