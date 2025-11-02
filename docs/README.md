# SCMS 문서 디렉토리

## 📁 문서 구조

```
docs/
├── README.md                                    # 문서 디렉토리 안내 (이 파일)
├── 00-SESSION_RESUME.md                         # 세션 재개 가이드 ⭐ 우선 읽기
├── 01-progress/                                 # 진행사항
│   ├── 01-phase1-foundation.md                  # Phase 1: 초기 설정
│   └── 02-entity-implementation-status.md       # Entity 구현 현황
├── 02-design/                                   # 설계 문서
│   ├── 00-SESSION_RESUME.md                     # 설계 세션 재개 가이드
│   ├── 01-erd-design.md                         # ERD 설계
│   ├── 02-entity-implementation-guide.md        # Entity 구현 가이드
│   ├── 03-layer-architecture.md                 # Layer 아키텍처
│   └── 04-erd-vs-implementation-analysis.md     # ERD vs 구현 비교 분석 ⭐ NEW
└── 03-workflow/                                 # 워크플로우
    └── 01-git-workflow.md                       # Git 워크플로우
```

---

## 📖 문서 읽기 순서

### 1️⃣ 새로운 세션 시작 시
**`00-SESSION_RESUME.md`** 를 먼저 읽으세요!
- 현재 프로젝트 상태 (12/23 Entity 완료, 52%)
- 완료된 작업 요약
- 다음 작업 (Counseling Domain)
- 세션 재개 시 사용할 멘트

### 2️⃣ 상세 진행사항 확인
**`01-progress/02-entity-implementation-status.md`** ⭐ 최신
- Domain별 Entity 완료 현황
- 각 Entity의 주요 특징
- 남은 작업 상세
- 작업 이력

### 3️⃣ 설계 원칙 이해
**`02-design/02-entity-implementation-guide.md`** ⭐ 필독
- Hybrid JPA 전략
- BaseEntity 상속 전략
- NO_CONSTRAINT 전략
- Domain별 구현 패턴
- 구현 체크리스트

### 4️⃣ Git 워크플로우 숙지
**`03-workflow/01-git-workflow.md`**
- 브랜치 전략
- 커밋 메시지 규칙
- 실제 워크플로우
- 브랜치 이력

---

## 🗂️ 문서 넘버링 규칙

### 디렉토리 넘버링
- `00-` : 최우선 문서 (세션 재개 가이드)
- `01-` : 진행사항 (Progress)
- `02-` : 설계 문서 (Design)
- `03-` : 워크플로우 (Workflow)
- `04-` : API 문서 (API) - 추후 추가
- `05-` : 회의록 (Meeting) - 추후 추가

### 파일 넘버링
- `00-` : 해당 디렉토리의 핵심 문서
- `01-` : Phase 1 또는 첫 번째 주제
- `02-` : Phase 2 또는 두 번째 주제
- `03-` : Phase 3 또는 세 번째 주제

---

## 📋 각 문서 요약

### 📌 핵심 문서

#### `00-SESSION_RESUME.md`
현재 작업 상황과 다음 할 일을 한눈에 파악
- ✅ 완료: 12/23 Entity (52%)
- 🎯 다음: Counseling Domain 3개 Entity
- 💡 핵심 원칙: Hybrid JPA, NO_CONSTRAINT, BaseEntity

#### `02-design/00-SESSION_RESUME.md`
설계 관점에서의 세션 재개 가이드
- Entity 구현 현황
- 다음 작업 대상
- 설계 원칙 요약

### 📊 진행 상황

#### `01-progress/01-phase1-foundation.md`
초기 설정 및 기초 작업
- Spring Boot 프로젝트 생성
- BaseEntity, User, Program Domain 구현
- 진행률: 5/23 (22%)

#### `01-progress/02-entity-implementation-status.md` ⭐ 최신
Domain별 상세 진행 현황
- Auth Domain: 2개 완료
- Program Domain: 6개 완료
- Mileage Domain: 5개 완료
- Competency Domain: 6개 완료
- 🔄 Counseling Domain: 작업 중

### 🎨 설계 문서

#### `02-design/01-erd-design.md`
ERD 설계 및 테이블 구조
- 23개 테이블 설계
- 관계 정의
- 제약 조건

#### `02-design/02-entity-implementation-guide.md` ⭐ 필독
Entity 구현 시 참고 필수
- Hybrid JPA 전략 상세
- BaseEntity 상속 기준
- NO_CONSTRAINT 적용 방법
- Domain별 구현 패턴
- 체크리스트

#### `02-design/03-layer-architecture.md` ⭐ NEW
시스템 계층 구조 및 아키텍처
- Layered Architecture 다이어그램
- 4개 계층 상세 설명 (Presentation, Application, Domain, Infrastructure)
- 계층별 책임과 역할
- 패키지 구조 가이드
- MSA 전환 준비 전략
- Best Practices 및 체크리스트

#### `02-design/04-erd-vs-implementation-analysis.md` ⭐ NEW
ERD와 구현 Entity 비교 분석
- Domain별 ERD vs Entity 상세 비교
- 구현 품질 평가 (모두 ⭐⭐⭐⭐⭐)
- ERD 대비 개선 사항 (Rich Domain Model, Factory 패턴 등)
- Counseling Domain 구현 가이드
- 남은 Domain별 ERD 참고 자료
- 구현 체크리스트

### 🔄 워크플로우

#### `03-workflow/01-git-workflow.md`
Git 사용 규칙 및 이력
- 브랜치 전략 (feature/xxx)
- 커밋 메시지 규칙
- 머지 전략 (--no-ff)
- 브랜치 이력 및 통계

---

## 📊 현재 프로젝트 상태 요약

### 진행률: 100% (26/26 Entity) ✅ 완료!

**✅ 완료 (26개)**
- Common: BaseEntity, CommonCode (2개)
- Auth Domain: User + UserRole (2개)
- Program Domain: Program, Application, Participant, Category + 3 Enum (7개)
- Mileage Domain: Account, Transaction, Certification + 2 Enum (5개)
- Competency Domain: Survey, Question, Response, Result + 2 Enum (6개)
- Career Domain: Plan, Goal, Milestone (3개)
- Counseling Domain: Reservation, Session, Availability, Counselor + Status (5개)
- File Domain: FileMetadata (1개)
- Notification Domain: Notification, Template (2개)
- System Domain: SystemLog, AuditLog (2개)

### 현재 브랜치
```
feature/entity-essential-missing (ERD 필수 엔티티 확인 완료)
```

### 다음 단계
**Repository 레이어 구현 시작**

---

## 💡 문서 활용 가이드

### 🔄 세션 시작 전
1. `00-SESSION_RESUME.md` 읽기
2. 현재 상태 파악 (12/23 완료, 52%)
3. `03-layer-architecture.md` 복습 (아키텍처 이해)

### 💻 개발 시작 전  
1. `03-layer-architecture.md`로 전체 구조 이해
2. `02-entity-implementation-guide.md` 참고
3. 체크리스트 활용

### 💻 Controller/Service 개발 시
1. `03-layer-architecture.md`의 계층별 책임 확인
2. Best Practices 섹션 참고
3. 패키지 구조 준수

### 🏗️ Entity 구현 중
1. `02-entity-implementation-guide.md`의 체크리스트 활용
2. Domain별 구현 패턴 참고

### 🔀 Git 작업 전
1. `03-workflow/01-git-workflow.md`의 커밋 규칙 확인
2. 체크리스트 점검

### 📈 진행 상황 확인 시
1. `01-progress/02-entity-implementation-status.md`
2. Domain별 상세 현황 확인

### ✅ 작업 완료 후
1. `01-progress/02-entity-implementation-status.md` 업데이트
2. `00-SESSION_RESUME.md` 업데이트

---

## 📋 향후 추가될 문서 (예정)

### 04-api/ (API 문서)
```
04-api/
├── 01-auth-api.md                 # 인증 API
├── 02-program-api.md              # 비교과 프로그램 API
├── 03-mileage-api.md              # 마일리지 API
├── 04-competency-api.md           # 역량 진단 API
├── 05-counseling-api.md           # 상담 API
└── 06-career-api.md               # 진로 상담 API
```

### 05-meeting/ (회의록)
```
05-meeting/
├── 01-kickoff-20251031.md         # 킥오프 회의
├── 02-sprint1-planning.md         # 스프린트 1 계획
└── ...
```

### 06-deployment/ (배포)
```
06-deployment/
├── 01-docker-setup.md             # Docker 설정
├── 02-ci-cd-pipeline.md           # CI/CD 파이프라인
└── 03-production-checklist.md     # 프로덕션 체크리스트
```

---

## 🔄 문서 업데이트 규칙

### Phase 완료 시
- `01-progress/` 에 새 문서 추가
- `00-SESSION_RESUME.md` 업데이트

### Entity 구현 완료 시
- `01-progress/02-entity-implementation-status.md` 업데이트
- 완료 Entity 수 업데이트

### 새 Domain 작업 시작 시
- `02-design/00-SESSION_RESUME.md` 업데이트
- Git 브랜치 생성 기록

### 설계 문서 추가 시
- `02-design/` 에 문서 추가
- README에 문서 요약 추가

---

## 📞 문서 관련 참고

### 프로젝트 전체 개요
- 프로젝트 루트의 `README.md`
- Google Docs 프로젝트 계획서

### 아키텍처 가이드
- Layer 아키텍처: `02-design/03-layer-architecture.md` ⭐
- MSA 전환 준비: `MSA_ARCHITECTURE_GUIDE.md`
- ERD 설계: `02-design/01-erd-design.md`

### 개발 가이드
- Entity 구현: `02-design/02-entity-implementation-guide.md`
- Git 워크플로우: `03-workflow/01-git-workflow.md`

---

**작성일**: 2025-01-XX  
**최종 수정**: 2025-01-XX  
**현재 진행률**: 12/23 Entity (52%)  
**다음 작업**: Counseling Domain (3개 Entity)

