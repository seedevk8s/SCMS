# SCMS 남은 개발 로드맵

## 📊 전체 진행 현황

**업데이트 일시**: 2025-11-03 (일) 20:00

```
전체 진행률: 30%

✅ 완료: Entity Layer (100%), Repository Layer (100%)
🔄 진행 예정: View Layer (Mock) → Service Layer → Controller (연동)
🎨 View First 전략 채택
```

---

## 🎯 현재까지 완료된 작업

### ✅ Phase 1: 데이터 모델링 (100%)
- [x] ERD 설계 완료 (30개 테이블)
- [x] Entity 클래스 구현 (30개)
- [x] 연관관계 매핑 완료
- [x] MSA 대비 설계 적용

### ✅ Phase 2: Repository Layer (100%)
- [x] 18개 Repository 구현
- [x] 523개 테스트 작성 및 통과 (99.8%)
- [x] 쿼리 메서드 구현
- [x] 통계 쿼리 구현

---

## 🚀 남은 개발 작업

### 🎨 View First 개발 전략

**채택 이유**: 혼자서 풀스택 개발을 진행하므로, 화면을 먼저 만들어 요구사항을 명확히 하고, Mock 데이터로 사용자 경험을 먼저 검증합니다.

#### 실제 개발 순서

```
1️⃣ Phase 3: View Layer (정적 화면 + Mock 데이터)
   └─ HTML/CSS/JavaScript로 화면 구현
   └─ Mock 데이터로 화면 동작 확인
   └─ 사용자 경험 검증
   
2️⃣ Phase 4: Service Layer  
   └─ 화면에서 필요한 데이터 구조 파악됨
   └─ 비즈니스 로직 구현
   └─ 단위 테스트
   
3️⃣ Phase 5: Controller Layer (API 연동)
   └─ REST API 구현
   └─ View에서 Mock 데이터 제거
   └─ 실제 Service와 연동
```

#### 🎯 View First의 장점

| 항목 | 설명 |
|-----|------|
| **요구사항 명확화** | 화면을 먼저 만들면서 어떤 데이터가 필요한지 자연스럽게 파악 |
| **빠른 피드백** | 화면으로 바로 확인하면서 개발 → 수정이 용이 |
| **API 스펙 최적화** | 화면 요구사항에 맞춘 API 설계 → 불필요한 수정 감소 |
| **동기 부여** | 동작하는 화면을 보면서 개발 → 진행 상황 체감 |

#### ⚠️ 주의사항

- Mock 데이터는 실제 데이터 구조와 최대한 유사하게 작성
- 화면 개발 중 필요한 API 스펙을 문서화
- Phase 5에서 Mock 제거하고 실제 API로 교체 시 철저히 테스트

---

### 📋 전체 개요

**🎨 View First 접근 방식 적용!**
화면을 먼저 만들고 Mock 데이터로 동작 확인 후, Service/Controller 구현

| Phase | 작업 내용 | 예상 소요 | 우선순위 |
|-------|----------|----------|---------|
| **Phase 3** | View Layer (정적 화면 + Mock) | 2-3주 | ⭐⭐⭐ 높음 |
| **Phase 4** | Service Layer | 2-3주 | ⭐⭐⭐ 높음 |
| **Phase 5** | Controller Layer (API 연동) | 2-3주 | ⭐⭐⭐ 높음 |
| **Phase 6** | Spring Security 통합 | 1주 | ⭐⭐⭐ 높음 |
| **Phase 7** | DTO & Validation | 1주 | ⭐⭐ 중간 |
| **Phase 8** | 예외 처리 & 로깅 | 1주 | ⭐⭐ 중간 |
| **Phase 9** | 통합 테스트 | 1-2주 | ⭐⭐ 중간 |
| **Phase 10** | API 문서화 | 3일 | ⭐ 낮음 |
| **Phase 11** | 배포 준비 | 1주 | ⭐ 낮음 |

**총 예상 소요 기간**: 약 11-15주

---

## 📝 Phase 3: View Layer (정적 화면 + Mock 데이터)

### 🎯 목표
**화면을 먼저 구현**하여 사용자 경험을 검증하고, 필요한 데이터 구조를 파악합니다.
Mock 데이터로 동작하는 완전한 화면을 만든 후, 나중에 실제 API와 연동합니다.

### 📦 구현 우선순위

#### 1단계: 핵심 화면 (Week 1-2)
- [ ] **레이아웃 & 공통 컴포넌트**
  - 헤더/푸터/사이드바
  - 페이지네이션, 테이블, 모달
  - 공통 CSS/JavaScript

- [ ] **Auth Views**
  - 로그인/회원가입
  - 프로필 페이지

- [ ] **Dashboard**
  - 메인 대시보드
  - 역할별 대시보드

#### 2단계: 주요 기능 화면 (Week 3-4)
- [ ] **Program Views**
  - 프로그램 목록/상세
  - 프로그램 신청
  - 참가자 관리

- [ ] **Mileage Views**
  - 마일리지 현황
  - 거래 내역

#### 3단계: 나머지 화면 (Week 5-6)
- [ ] **Competency Views** - 역량 설문/결과
- [ ] **Counseling Views** - 상담 예약/내역
- [ ] **Career Views** - 진로 계획/목표
- [ ] **Admin Views** - 관리자 페이지

---

### 🎨 Mock 데이터 작성 방법

#### JavaScript Mock API 패턴
```javascript
// /static/js/mock/program-mock.js
const MockProgramAPI = {
    // 프로그램 목록
    getPrograms: function(page = 0, size = 10) {
        return Promise.resolve({
            content: [
                {
                    id: 1,
                    title: "Spring Boot 워크샵",
                    description: "Spring Boot 실전 개발",
                    category: "개발",
                    status: "RECRUITING",
                    startDate: "2025-12-01",
                    endDate: "2025-12-15",
                    maxParticipants: 30,
                    currentParticipants: 15
                },
                {
                    id: 2,
                    title: "AI 역량 강화",
                    description: "머신러닝 기초",
                    category: "AI",
                    status: "IN_PROGRESS",
                    startDate: "2025-11-15",
                    endDate: "2025-11-30",
                    maxParticipants: 25,
                    currentParticipants: 25
                }
            ],
            totalElements: 20,
            totalPages: 2,
            number: page,
            size: size
        });
    },
    
    // 프로그램 상세
    getProgram: function(id) {
        const programs = {
            1: {
                id: 1,
                title: "Spring Boot 워크샵",
                description: "Spring Boot 기초부터 실전까지 배우는 워크샵",
                category: "개발",
                status: "RECRUITING",
                startDate: "2025-12-01",
                endDate: "2025-12-15",
                maxParticipants: 30,
                currentParticipants: 15,
                competencies: ["문제해결", "창의력"],
                instructor: "김교수"
            }
        };
        return Promise.resolve(programs[id] || null);
    },
    
    // 프로그램 신청
    applyProgram: function(programId) {
        return Promise.resolve({
            success: true,
            message: "신청이 완료되었습니다.",
            applicationId: Date.now()
        });
    }
};
```

#### HTML에서 Mock API 사용
```html
<!-- program/list.html -->
<script src="/js/mock/program-mock.js"></script>
<script>
document.addEventListener('DOMContentLoaded', function() {
    loadPrograms();
});

function loadPrograms() {
    // TODO: Phase 5에서 실제 API로 교체
    // fetch('/api/programs')
    
    // Mock API 사용
    MockProgramAPI.getPrograms(0, 10)
        .then(data => {
            renderPrograms(data.content);
            renderPagination(data);
        });
}

function renderPrograms(programs) {
    const container = document.getElementById('program-list');
    container.innerHTML = programs.map(program => `
        <div class="program-card">
            <h3>${program.title}</h3>
            <p>${program.description}</p>
            <span class="badge badge-${program.status}">${getStatusText(program.status)}</span>
            <div class="program-info">
                <span>정원: ${program.currentParticipants}/${program.maxParticipants}</span>
                <span>기간: ${program.startDate} ~ ${program.endDate}</span>
            </div>
            <a href="/views/programs/${program.id}" class="btn btn-primary">상세보기</a>
        </div>
    `).join('');
}
</script>
```

---

### 📊 예상 산출물

| 항목 | 개수 | 비고 |
|-----|------|------|
| **Thymeleaf Templates** | 50개 | HTML 화면 |
| **Layout/Fragment** | 10개 | 공통 레이아웃 |
| **Mock JavaScript** | 20개 | Mock API |
| **CSS 파일** | 15개 | 스타일시트 |
| **JavaScript 파일** | 20개 | 화면 로직 |

---

### ✅ Phase 3 완료 기준

- [x] 모든 화면이 Mock 데이터로 동작
- [x] 사용자가 클릭/입력/탐색 가능
- [x] 필요한 API 스펙이 문서화됨
- [x] 화면 흐름이 자연스러움

---

## 📝 Phase 4: Service Layer 구현

### 🎯 목표
비즈니스 로직을 구현하고 트랜잭션을 관리하는 서비스 계층 구축

### 📦 구현할 Service (18개)

#### Auth Domain (1개)
- [ ] **UserService**
  - 사용자 등록/조회/수정/삭제
  - 비밀번호 변경
  - 사용자 권한 관리
  - 학생/교직원 정보 관리

#### Common Domain (1개)
- [ ] **CommonCodeService**
  - 공통 코드 관리
  - 코드 그룹 관리
  - 캐시 관리

#### Program Domain (6개)
- [ ] **ProgramService**
  - 프로그램 CRUD
  - 프로그램 검색
  - 프로그램 상태 관리
- [ ] **ProgramCompetencyService**
  - 역량 매핑 관리
- [ ] **ProgramApplicationService**
  - 프로그램 신청 처리
  - 신청 승인/거부
  - 신청 취소
- [ ] **ProgramParticipantService**
  - 참가자 관리
  - 출석 체크
  - 이수 처리
- [ ] **ProgramSatisfactionService**
  - 만족도 조사 관리
  - 통계 생성
- [ ] **ProgramCategoryService**
  - 카테고리 관리
  - 계층 구조 관리

#### Mileage Domain (2개)
- [ ] **MileageTransactionService**
  - 마일리지 적립/차감
  - 거래 내역 조회
  - 잔액 계산
- [ ] **MileageRuleService**
  - 마일리지 규칙 관리
  - 자동 적립 로직

#### Competency Domain (3개)
- [ ] **CompetencySurveyService**
  - 설문 CRUD
  - 설문 배포
- [ ] **SurveyQuestionService**
  - 문항 관리
- [ ] **CompetencyResultService**
  - 역량 평가 처리
  - 결과 분석
  - 통계 생성

#### Counseling Domain (3개)
- [ ] **CounselingReservationService**
  - 예약 생성/조회/취소
  - 예약 확정
  - 알림 발송
- [ ] **CounselingSessionService**
  - 세션 기록 관리
  - 상담 내역 조회
- [ ] **CounselorAvailabilityService**
  - 상담사 일정 관리
  - 가용 시간 조회

#### Career Domain (2개)
- [ ] **CareerPlanService**
  - 진로 계획 CRUD
  - 계획 완료율 계산
- [ ] **CareerGoalService**
  - 목표 관리
  - 순서 변경
  - 완료 처리

### 🛠️ 주요 작업 내용

1. **비즈니스 로직 구현**
   - 도메인 규칙 검증
   - 복잡한 비즈니스 프로세스
   - 상태 전이 관리

2. **트랜잭션 관리**
   - @Transactional 적용
   - 격리 수준 설정
   - 롤백 조건 정의

3. **DTO 변환**
   - Entity → DTO 매핑
   - DTO → Entity 매핑
   - ModelMapper 활용

4. **예외 처리**
   - Custom Exception 정의
   - 비즈니스 예외 처리
   - 에러 메시지 관리

5. **테스트 작성**
   - Unit Test (Mockito)
   - 비즈니스 로직 검증
   - 예외 케이스 테스트

### 📊 예상 산출물
- Service 인터페이스: 18개
- Service 구현체: 18개
- Service 테스트: 약 300개
- Custom Exception: 약 20개

---

## 📝 Phase 5: Controller Layer (REST API + View 연동)

### 🎯 목표
RESTful API 엔드포인트 구현 및 **Phase 3에서 만든 View와 연동**

**주요 작업:**
1. REST API Controller 구현
2. View Controller에서 Service 호출
3. **Mock 데이터 제거**
4. **실제 API로 교체**
5. 통합 동작 확인

### 📦 구현할 Controller (18개)

#### Auth API
- [ ] **AuthController** (`/api/auth`)
  - `POST /register` - 회원가입
  - `POST /login` - 로그인
  - `POST /logout` - 로그아웃
  - `GET /me` - 내 정보 조회
  - `PUT /password` - 비밀번호 변경

- [ ] **UserController** (`/api/users`)
  - `GET /users` - 사용자 목록
  - `GET /users/{id}` - 사용자 상세
  - `PUT /users/{id}` - 사용자 수정
  - `DELETE /users/{id}` - 사용자 삭제

#### Program API
- [ ] **ProgramController** (`/api/programs`)
  - `POST /programs` - 프로그램 생성
  - `GET /programs` - 프로그램 목록
  - `GET /programs/{id}` - 프로그램 상세
  - `PUT /programs/{id}` - 프로그램 수정
  - `DELETE /programs/{id}` - 프로그램 삭제
  - `GET /programs/search` - 프로그램 검색

- [ ] **ProgramApplicationController** (`/api/programs/{id}/applications`)
  - `POST /apply` - 신청
  - `GET /applications` - 신청 목록
  - `PUT /applications/{id}/approve` - 승인
  - `PUT /applications/{id}/reject` - 거부
  - `DELETE /applications/{id}` - 취소

- [ ] **ProgramParticipantController** (`/api/programs/{id}/participants`)
  - `GET /participants` - 참가자 목록
  - `POST /participants/{id}/attendance` - 출석 체크
  - `PUT /participants/{id}/complete` - 이수 처리

- [ ] **ProgramSatisfactionController** (`/api/programs/{id}/satisfaction`)
  - `POST /submit` - 만족도 제출
  - `GET /results` - 결과 조회

#### Mileage API
- [ ] **MileageController** (`/api/mileage`)
  - `GET /balance` - 잔액 조회
  - `GET /transactions` - 거래 내역
  - `POST /earn` - 적립
  - `POST /use` - 사용

#### Competency API
- [ ] **CompetencyController** (`/api/competency`)
  - `GET /surveys` - 설문 목록
  - `POST /surveys/{id}/submit` - 응답 제출
  - `GET /results` - 역량 결과 조회

#### Counseling API
- [ ] **CounselingController** (`/api/counseling`)
  - `POST /reservations` - 예약 생성
  - `GET /reservations` - 예약 목록
  - `DELETE /reservations/{id}` - 예약 취소
  - `GET /sessions` - 상담 내역

#### Career API
- [ ] **CareerController** (`/api/career`)
  - `POST /plans` - 진로 계획 생성
  - `GET /plans` - 진로 계획 목록
  - `POST /plans/{id}/goals` - 목표 추가
  - `PUT /goals/{id}/complete` - 목표 완료

### 🛠️ 주요 작업 내용

1. **RESTful API 설계**
   - URI 설계 (Resource 기반)
   - HTTP 메서드 활용 (GET, POST, PUT, DELETE)
   - 상태 코드 정의 (200, 201, 400, 404, 500)

2. **요청/응답 처리**
   - @RequestBody 검증
   - @PathVariable, @RequestParam 처리
   - ResponseEntity 활용

3. **페이징 & 정렬**
   - Pageable 적용
   - 커스텀 페이징 응답

4. **파일 업로드**
   - MultipartFile 처리
   - 파일 저장 로직

5. **API 문서화**
   - Swagger/OpenAPI 적용
   - API 명세 자동 생성

---

### 🔄 Mock 제거 및 실제 API 연동

#### Before (Phase 3 - Mock 사용)
```javascript
// program.js
function loadPrograms() {
    // Mock API 사용
    MockProgramAPI.getPrograms(0, 10)
        .then(data => {
            renderPrograms(data.content);
        });
}
```

#### After (Phase 5 - 실제 API 연동)
```javascript
// program.js
function loadPrograms() {
    // 실제 REST API 호출
    fetch('/api/programs?page=0&size=10')
        .then(response => response.json())
        .then(data => {
            renderPrograms(data.content);
        })
        .catch(error => {
            console.error('프로그램 목록 조회 실패:', error);
            showErrorMessage('프로그램 목록을 불러올 수 없습니다.');
        });
}
```

#### View Controller 구현
```java
@Controller
@RequestMapping("/views/programs")
@RequiredArgsConstructor
public class ProgramViewController {
    
    private final ProgramService programService;
    
    @GetMapping
    public String listPrograms(Model model, Pageable pageable) {
        // Service에서 실제 데이터 조회
        Page<ProgramResponse> programs = programService.findAll(pageable);
        model.addAttribute("programs", programs);
        return "program/list";
    }
    
    @GetMapping("/{id}")
    public String programDetail(@PathVariable Long id, Model model) {
        ProgramResponse program = programService.findById(id);
        model.addAttribute("program", program);
        return "program/detail";
    }
}
```

---

### 📊 예상 산출물
- Controller 클래스: 18개 (REST API)
- View Controller: 18개 (Thymeleaf)
- API 엔드포인트: 약 100개
- Controller 테스트: 약 200개
- **Mock 제거 작업**: 20개 파일

---

## 📝 Phase 5: View Layer 상세 내용

**⚠️ 이 Phase는 개발 순서 조정으로 Phase 3으로 이동되었습니다.**

**View First 개발 전략**을 채택하여, View Layer를 먼저 구현합니다.

👉 **상세 내용은 "Phase 3: View Layer (정적 화면 + Mock 데이터)" 섹션을 참조하세요.**

---

### 📦 구현할 View Controller & Templates

#### Auth Views
- [ ] **AuthViewController** (`/views/auth`)
  - `GET /login` - 로그인 페이지
  - `GET /register` - 회원가입 페이지
  - `GET /profile` - 프로필 페이지

**Templates**:
- `auth/login.html`
- `auth/register.html`
- `auth/profile.html`

---

#### Program Views
- [ ] **ProgramViewController** (`/views/programs`)
  - `GET /programs` - 프로그램 목록 페이지
  - `GET /programs/{id}` - 프로그램 상세 페이지
  - `GET /programs/create` - 프로그램 생성 페이지 (관리자)
  - `GET /programs/{id}/edit` - 프로그램 수정 페이지 (관리자)

- [ ] **ProgramApplicationViewController** (`/views/programs/{id}`)
  - `GET /apply` - 신청 페이지
  - `GET /applications` - 내 신청 목록 페이지

- [ ] **ProgramParticipantViewController** (`/views/programs/{id}`)
  - `GET /participants` - 참가자 관리 페이지 (관리자)

**Templates**:
- `program/list.html`
- `program/detail.html`
- `program/create.html`
- `program/edit.html`
- `program/apply.html`
- `program/my-applications.html`
- `program/participants.html`

---

#### Mileage Views
- [ ] **MileageViewController** (`/views/mileage`)
  - `GET /mileage` - 마일리지 현황 페이지
  - `GET /mileage/transactions` - 거래 내역 페이지
  - `GET /mileage/rules` - 마일리지 규칙 페이지 (관리자)

**Templates**:
- `mileage/balance.html`
- `mileage/transactions.html`
- `mileage/rules.html`

---

#### Competency Views
- [ ] **CompetencyViewController** (`/views/competency`)
  - `GET /surveys` - 역량 설문 목록 페이지
  - `GET /surveys/{id}` - 설문 응답 페이지
  - `GET /results` - 내 역량 결과 페이지

**Templates**:
- `competency/survey-list.html`
- `competency/survey-form.html`
- `competency/results.html`

---

#### Counseling Views
- [ ] **CounselingViewController** (`/views/counseling`)
  - `GET /counseling` - 상담 예약 메인 페이지
  - `GET /counseling/reservations` - 내 예약 목록
  - `GET /counseling/sessions` - 상담 내역 페이지
  - `GET /counseling/availability` - 상담사 일정 관리 페이지 (상담사)

**Templates**:
- `counseling/main.html`
- `counseling/reservations.html`
- `counseling/sessions.html`
- `counseling/availability.html`

---

#### Career Views
- [ ] **CareerViewController** (`/views/career`)
  - `GET /career/plans` - 진로 계획 목록
  - `GET /career/plans/{id}` - 진로 계획 상세
  - `GET /career/plans/create` - 진로 계획 생성

**Templates**:
- `career/plan-list.html`
- `career/plan-detail.html`
- `career/plan-create.html`

---

#### Dashboard & Common Views
- [ ] **DashboardViewController** (`/views`)
  - `GET /` - 메인 대시보드
  - `GET /dashboard` - 역할별 대시보드

- [ ] **AdminViewController** (`/views/admin`)
  - `GET /admin` - 관리자 메인
  - `GET /admin/users` - 사용자 관리
  - `GET /admin/programs` - 프로그램 관리

**Templates**:
- `index.html`
- `dashboard/student.html`
- `dashboard/staff.html`
- `dashboard/admin.html`
- `admin/main.html`
- `admin/users.html`
- `admin/programs.html`

---

### 🎨 Layout & Fragments

#### 공통 레이아웃
- [ ] **fragments/layout.html**
  - 헤더 (네비게이션)
  - 푸터
  - 사이드바

- [ ] **fragments/components.html**
  - 페이지네이션
  - 검색 폼
  - 테이블
  - 모달
  - 알림 메시지

**Layout Templates**:
- `layouts/default.html` - 기본 레이아웃
- `layouts/admin.html` - 관리자 레이아웃
- `fragments/header.html` - 헤더
- `fragments/footer.html` - 푸터
- `fragments/sidebar.html` - 사이드바
- `fragments/pagination.html` - 페이지네이션

---

### 📂 정적 리소스

#### CSS
- [ ] **스타일시트 구성**
  - `/static/css/common.css` - 공통 스타일
  - `/static/css/layout.css` - 레이아웃
  - `/static/css/components.css` - 컴포넌트
  - `/static/css/pages/*.css` - 페이지별 스타일

#### JavaScript
- [ ] **스크립트 구성**
  - `/static/js/common.js` - 공통 기능
  - `/static/js/validation.js` - 폼 검증
  - `/static/js/api.js` - API 호출
  - `/static/js/pages/*.js` - 페이지별 스크립트

#### 외부 라이브러리
- [ ] **프론트엔드 라이브러리**
  - Bootstrap 5 (UI 프레임워크)
  - jQuery (DOM 조작)
  - Chart.js (차트)
  - DataTables (테이블)
  - Moment.js (날짜 처리)

---

### 🛠️ 주요 작업 내용

#### 1. View Controller 구현
```java
@Controller
@RequestMapping("/views/programs")
public class ProgramViewController {
    
    @GetMapping
    public String listPrograms(Model model, Pageable pageable) {
        Page<ProgramResponse> programs = programService.findAll(pageable);
        model.addAttribute("programs", programs);
        return "program/list";
    }
    
    @GetMapping("/{id}")
    public String programDetail(@PathVariable Long id, Model model) {
        ProgramResponse program = programService.findById(id);
        model.addAttribute("program", program);
        return "program/detail";
    }
}
```

#### 2. Thymeleaf 템플릿 작성
```html
<!-- program/list.html -->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      th:replace="~{layouts/default :: layout(~{::title}, ~{::content})}">
<head>
    <title>프로그램 목록</title>
</head>
<body>
    <div th:fragment="content">
        <h1>프로그램 목록</h1>
        
        <div class="program-list">
            <div th:each="program : ${programs}" class="program-card">
                <h3 th:text="${program.title}">프로그램 제목</h3>
                <p th:text="${program.description}">설명</p>
                <a th:href="@{/views/programs/{id}(id=${program.id})}">
                    상세보기
                </a>
            </div>
        </div>
        
        <!-- 페이지네이션 -->
        <div th:replace="~{fragments/pagination :: pagination}"></div>
    </div>
</body>
</html>
```

#### 3. 폼 처리
```html
<!-- program/create.html -->
<form th:action="@{/api/programs}" method="post" th:object="${programForm}">
    <div class="form-group">
        <label for="title">제목</label>
        <input type="text" 
               th:field="*{title}" 
               class="form-control"
               th:errorclass="is-invalid">
        <div class="invalid-feedback" th:errors="*{title}"></div>
    </div>
    
    <button type="submit" class="btn btn-primary">생성</button>
</form>
```

#### 4. AJAX 통신
```javascript
// program.js
function applyProgram(programId) {
    $.ajax({
        url: `/api/programs/${programId}/apply`,
        method: 'POST',
        success: function(response) {
            alert('신청이 완료되었습니다.');
            location.reload();
        },
        error: function(xhr) {
            alert('신청에 실패했습니다: ' + xhr.responseJSON.message);
        }
    });
}
```

---

### 📊 예상 산출물

| 항목 | 개수 | 비고 |
|-----|------|------|
| **View Controller** | 18개 | 도메인별 |
| **Thymeleaf Templates** | 약 50개 | 페이지 화면 |
| **Layout/Fragment** | 약 10개 | 공통 레이아웃 |
| **CSS 파일** | 약 15개 | 스타일시트 |
| **JavaScript 파일** | 약 20개 | 동적 기능 |
| **View 테스트** | 약 100개 | Controller 테스트 |

---

### 🎨 UI/UX 고려사항

#### 1. 반응형 디자인
- Bootstrap Grid System 활용
- 모바일/태블릿/데스크톱 대응

#### 2. 접근성
- 시맨틱 HTML
- ARIA 속성
- 키보드 네비게이션

#### 3. 사용자 경험
- 로딩 인디케이터
- 에러 메시지
- 성공 알림
- 폼 검증 피드백

#### 4. 성능 최적화
- CSS/JS 번들링
- 이미지 최적화
- 캐시 활용

---

### 🔐 보안 고려사항

#### 1. CSRF 토큰
```html
<form th:action="@{/api/programs}" method="post">
    <input type="hidden" th:name="${_csrf.parameterName}" 
           th:value="${_csrf.token}">
    <!-- 폼 필드 -->
</form>
```

#### 2. XSS 방지
- Thymeleaf 기본 이스케이프 활용
- `th:text` vs `th:utext` 주의

#### 3. 권한 체크
```html
<div sec:authorize="hasRole('ADMIN')">
    <!-- 관리자만 볼 수 있는 내용 -->
</div>
```

---

## 📝 Phase 6: Spring Security 통합

### 🎯 목표
인증/인가 시스템 구축 및 보안 강화

### 📦 구현 내용

#### 1. 인증 (Authentication)
- [ ] **JWT 기반 인증**
  - JWT 토큰 생성/검증
  - Access Token / Refresh Token
  - 토큰 저장소 (Redis)

- [ ] **로그인 처리**
  - UserDetailsService 구현
  - 비밀번호 암호화 (BCrypt)
  - 로그인 실패 처리

#### 2. 인가 (Authorization)
- [ ] **역할 기반 접근 제어 (RBAC)**
  - ROLE_STUDENT
  - ROLE_STAFF
  - ROLE_ADMIN

- [ ] **메서드 레벨 보안**
  - @PreAuthorize
  - @PostAuthorize
  - @Secured

#### 3. Security Filter Chain
- [ ] **필터 설정**
  - JWT 인증 필터
  - CORS 설정
  - CSRF 설정
  - 예외 처리

#### 4. 보안 강화
- [ ] **API 보안**
  - Rate Limiting
  - IP 화이트리스트
  - 로그인 시도 제한

### 📊 예상 산출물
- Security Configuration: 1개
- JWT 관련 클래스: 5개
- Filter 클래스: 3개
- Security 테스트: 약 50개

---

## 📝 Phase 7: DTO & Validation

### 🎯 목표
데이터 전송 객체 정의 및 입력 검증 구현

### 📦 구현 내용

#### 1. DTO 설계
- [ ] **Request DTO** (약 50개)
  - 생성 요청 DTO
  - 수정 요청 DTO
  - 검색 조건 DTO

- [ ] **Response DTO** (약 50개)
  - 상세 응답 DTO
  - 목록 응답 DTO
  - 통계 응답 DTO

#### 2. Validation
- [ ] **Bean Validation**
  - @NotNull, @NotBlank
  - @Size, @Min, @Max
  - @Email, @Pattern
  - 커스텀 Validator

- [ ] **비즈니스 규칙 검증**
  - 날짜 범위 검증
  - 상태 전이 검증
  - 권한 검증

### 📊 예상 산출물
- Request DTO: 약 50개
- Response DTO: 약 50개
- Custom Validator: 약 10개

---

## 📝 Phase 8: 예외 처리 & 로깅

### 🎯 목표
일관된 예외 처리 및 효과적인 로깅 시스템 구축

### 📦 구현 내용

#### 1. 예외 처리
- [ ] **Custom Exception**
  - BusinessException
  - EntityNotFoundException
  - DuplicateException
  - UnauthorizedException
  - 도메인별 Exception (약 20개)

- [ ] **Global Exception Handler**
  - @ControllerAdvice
  - 예외별 응답 포맷
  - 에러 코드 체계

#### 2. 로깅
- [ ] **Logback 설정**
  - 로그 레벨 설정
  - 파일 로깅
  - 로그 로테이션

- [ ] **AOP 로깅**
  - 메서드 실행 로깅
  - 실행 시간 측정
  - 파라미터 로깅

### 📊 예상 산출물
- Custom Exception: 약 20개
- Exception Handler: 1개
- AOP 로깅 클래스: 3개

---

## 📝 Phase 9: 통합 테스트

### 🎯 목표
전체 시스템의 통합 테스트 및 E2E 시나리오 검증

### 📦 구현 내용

#### 1. API 통합 테스트
- [ ] **@SpringBootTest**
  - 전체 컨텍스트 로딩
  - 실제 HTTP 요청/응답
  - 데이터베이스 트랜잭션

#### 2. 시나리오 테스트
- [ ] **사용자 플로우**
  - 회원가입 → 로그인 → 프로그램 신청 → 참가 → 이수
  - 마일리지 적립 → 사용
  - 상담 예약 → 상담 진행 → 기록

#### 3. 성능 테스트
- [ ] **부하 테스트**
  - JMeter 활용
  - 동시 접속자 테스트
  - 응답 시간 측정

### 📊 예상 산출물
- 통합 테스트: 약 100개
- 시나리오 테스트: 약 20개
- 성능 테스트 시나리오: 10개

---

## 📝 Phase 10: API 문서화

### 🎯 목표
API 명세 자동화 및 개발자 가이드 작성

### 📦 구현 내용

#### 1. Swagger/OpenAPI
- [ ] **Springdoc OpenAPI 적용**
  - API 자동 문서화
  - Swagger UI 설정
  - 모델 스키마 정의

#### 2. 문서 작성
- [ ] **개발자 가이드**
  - API 사용법
  - 인증 방법
  - 에러 코드 설명
  - 예제 코드

### 📊 예상 산출물
- OpenAPI Specification: 1개
- 개발자 가이드: 1개
- API 예제: 약 50개

---

## 📝 Phase 11: 배포 준비

### 🎯 목표
운영 환경 배포를 위한 설정 및 최적화

### 📦 구현 내용

#### 1. 프로파일 설정
- [ ] **환경별 설정**
  - dev (개발)
  - test (테스트)
  - prod (운영)

#### 2. 데이터베이스
- [ ] **마이그레이션 도구**
  - Flyway 또는 Liquibase
  - 버전 관리
  - 롤백 전략

#### 3. 모니터링
- [ ] **Actuator 설정**
  - Health Check
  - Metrics
  - 로그 수집

#### 4. 최적화
- [ ] **성능 최적화**
  - 쿼리 최적화
  - 캐시 적용 (Redis)
  - 인덱스 최적화

### 📊 예상 산출물
- 환경 설정 파일: 3개
- 마이그레이션 스크립트: 30개
- 모니터링 대시보드: 1개

---

## 📅 예상 개발 일정

### 🎨 View First 개발 순서

```
Week 1-2:   Phase 3 - View Layer (Auth, Dashboard, 공통)
Week 3-4:   Phase 3 - View Layer (Program, Mileage, Competency)
Week 5-6:   Phase 3 - View Layer (Counseling, Career, Admin)
Week 7-8:   Phase 4 - Service Layer (Auth, Common, Program)
Week 9-10:  Phase 4 - Service Layer (Mileage, Competency, Counseling, Career)
Week 11-12: Phase 5 - Controller & API 연동 (Auth, Program, Mileage)
Week 13-14: Phase 5 - Controller & API 연동 (Competency, Counseling, Career)
Week 15:    Phase 6 - Spring Security 통합
Week 16:    Phase 7 - DTO & Validation
Week 17:    Phase 8 - 예외 처리 & 로깅
Week 18-19: Phase 9 - 통합 테스트
Week 20:    Phase 10 - API 문서화
Week 21:    Phase 11 - 배포 준비 & 최적화
Week 22:    최종 테스트 & 버그 픽스
```

**총 예상 기간**: 약 22주 (5.5개월)

### 📌 핵심 마일스톤

| 주차 | 마일스톤 | 완료 기준 |
|-----|---------|---------|
| Week 6 | **화면 완성** | 모든 화면이 Mock으로 동작 |
| Week 10 | **비즈니스 로직 완성** | Service Layer 완료 & 테스트 통과 |
| Week 14 | **API 연동 완료** | Mock 제거, 실제 데이터로 동작 |
| Week 15 | **보안 적용** | 인증/인가 완료 |
| Week 19 | **테스트 완료** | 통합 테스트 통과 |
| Week 22 | **배포 준비** | 운영 환경 준비 완료 |

---

## 🎯 우선순위별 작업

### ⭐⭐⭐ 높음 (Core 기능)
1. Service Layer 구현
2. Controller Layer (REST API)
3. Spring Security 통합
4. DTO & Validation

### ⭐⭐ 중간 (필수 기능)
1. 예외 처리 & 로깅
2. 통합 테스트
3. 성능 최적화

### ⭐ 낮음 (부가 기능)
1. API 문서화
2. 모니터링 설정
3. 배포 자동화

---

## 📊 전체 진행률 시각화

```
[████████░░░░░░░░░░░░░░░░░░░░] 30% - 현재

완료:
├─ Entity Layer      [████████████████████] 100%
└─ Repository Layer  [████████████████████] 100%

진행 예정:
├─ Service Layer     [░░░░░░░░░░░░░░░░░░░░]   0%
├─ Controller Layer  [░░░░░░░░░░░░░░░░░░░░]   0%
├─ View Layer        [░░░░░░░░░░░░░░░░░░░░]   0%
├─ Security          [░░░░░░░░░░░░░░░░░░░░]   0%
├─ DTO & Validation  [░░░░░░░░░░░░░░░░░░░░]   0%
├─ Exception/Logging [░░░░░░░░░░░░░░░░░░░░]   0%
├─ Integration Test  [░░░░░░░░░░░░░░░░░░░░]   0%
├─ API Docs          [░░░░░░░░░░░░░░░░░░░░]   0%
└─ Deployment        [░░░░░░░░░░░░░░░░░░░░]   0%
```

---

## 🎓 학습 목표

각 Phase를 진행하면서 다음 기술들을 익히게 됩니다:

### Service Layer
- 트랜잭션 관리 (@Transactional)
- 비즈니스 로직 설계
- DTO 패턴
- 예외 처리 전략

### Controller Layer
- RESTful API 설계
- HTTP 통신
- 페이징/정렬
- 파일 업로드

### Security
- JWT 인증
- Spring Security
- 역할 기반 접근 제어
- 보안 베스트 프랙티스

### Testing
- 단위 테스트 (Mockito)
- 통합 테스트 (@SpringBootTest)
- 시나리오 테스트
- 성능 테스트

---

## 📝 참고 문서

- [Repository Layer 구현 상태](./03-repository-implementation-status.md)
- [ERD 설계](../02-design/01-erd-design.md)
- [프로젝트 수행 계획서](https://docs.google.com/document/d/1LPxYcGUIk_J7sn4BlCQeZrpfCZGavj8dZMRhIfEAAh4/edit)

---

## 🤝 팀 협업 가이드

### 브랜치 전략
```
main
├─ feature/service-auth
├─ feature/service-program
├─ feature/controller-auth
└─ ...
```

### 커밋 메시지 규칙
```
feat: 새로운 기능 추가
fix: 버그 수정
refactor: 코드 리팩토링
test: 테스트 추가/수정
docs: 문서 수정
style: 코드 포맷팅
chore: 기타 변경사항
```

---

**작성일**: 2025-11-03  
**작성자**: Development Team  
**프로젝트**: SCMS v1.0  
**마지막 업데이트**: 2025-11-03 20:00  
**개발 전략**: View First Approach ⭐
