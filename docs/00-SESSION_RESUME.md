# SCMS Phase 3 View Layer - 세션 재개 문서

**작성일**: 2025-11-04
**세션 종료 시점**: 토큰 사용률 55.4% (105,249/190,000)
**Git 상태**: main 브랜치, 최신 커밋 7d56d60

---

## ✅ 이번 세션에서 완료한 작업

### 🎉 프로그램 신청 페이지 완성!

**완성된 페이지**: `program/apply.html`
**URL**: `/program/apply?id={programId}`
**Controller**: `MainViewController.programApply()`
**JavaScript**: `program-apply.js`

#### 구현된 기능
1. **4단계 신청 프로세스**
   - STEP 1: 프로그램 정보 확인 (프로그램명, 일시, 장소, 모집인원)
   - STEP 2: 신청자 정보 (읽기전용 + 이메일/연락처 입력)
   - STEP 3: 신청 정보 (동기, 기대효과, 추가질문)
   - STEP 4: 동의 사항 (개인정보, 운영규정, 알림수신)

2. **JavaScript 고급 기능**
   - ✅ Mock 데이터 연동 (window.PROGRAMS 사용)
   - ✅ 실시간 글자 수 카운터 (색상 변경)
   - ✅ 연락처 자동 포맷팅 (010-1234-5678)
   - ✅ 폼 유효성 검사 (연락처, 이메일, 글자수, 동의항목)
   - ✅ 전체 동의 체크박스
   - ✅ 모달 UI (개인정보, 운영규정 상세보기)
   - ✅ LocalStorage 저장 (Mock)
   - ✅ 신청 완료 모달

3. **반응형 디자인**
   - ✅ 데스크톱/태블릿/모바일 대응
   - ✅ 모달 애니메이션 (슬라이드 인)
   - ✅ 그리드 레이아웃

#### 핵심 수정 사항
1. **MainViewController.java**
   ```java
   @GetMapping("/program/apply")
   public String programApply() {
       return "program/apply";
   }
   ```

2. **main.js - PROGRAMS 데이터 완성**
   - 6개 프로그램 데이터 완성 (id: 1-6)
   - `window.PROGRAMS` export 추가
   - 필수 필드 추가: `date`, `location`, `current`, `capacity`

3. **program-apply.js**
   - Mock 데이터 연동
   - 폼 유효성 검사
   - LocalStorage 저장
   - 성공 모달 표시

4. **program.css**
   - Apply 페이지 전용 스타일 465줄 추가
   - 폼 요소, 모달, 반응형 디자인

---

## 🎯 현재 완료 상태 (6개 화면)

### 완성된 화면
| 번호 | 페이지 | URL | 상태 |
|------|--------|-----|------|
| 1 | 메인 페이지 | `/` | ✅ 완료 |
| 2 | 프로그램 목록 | `/programs` | ✅ 완료 |
| 3 | 프로그램 상세 | `/programs/{id}` | ✅ 완료 |
| 4 | 프로그램 신청 | `/program/apply?id={id}` | ✅ **이번 세션 완료** |
| 5 | 마일리지 현황 | `/mileage` | ✅ 완료 |

### Phase 3 진행률
```
✅ 공통 모듈 (Fragment, Layout)
✅ 메인 페이지 (Hero Carousel, Icon Menu, Programs)
✅ 프로그램 목록 (검색, 필터, 카드 그리드)
✅ 프로그램 상세 (정보, 현황, 탭, 신청 버튼)
✅ 프로그램 신청 (4단계 폼, Mock 처리) ← NEW!
✅ 마일리지 현황 (통계, 차트, 적립내역)
⏳ 신청 취소 (향후)
⏳ 신청 이력 (향후)
⏳ 이수내역 (향후)
```

**현재 진행률**: Phase 3 View Layer 약 70% 완료

---

## 📊 Mock 데이터 구조

### PROGRAMS 배열 (6개)
```javascript
window.PROGRAMS = [
    { id: 1, title: '2025-2학기 토익경시대회', ... },
    { id: 2, title: 'AI 역량 강화 워크샵', ... },
    { id: 3, title: '명칭스피치&이미지메이킹', ... },
    { id: 4, title: '2학기 계슈탑트 자기이해 성장캠프', ... },
    { id: 5, title: '창업 아이디어 경진대회', ... },
    { id: 6, title: '글로벌 리더십 프로그램', ... }
];
```

### 필수 필드
- `id`, `title`, `description`, `center`, `category`
- `startDate`, `endDate`, `date`, `eventDate`
- `location`, `current`, `currentParticipants`, `capacity`, `maxParticipants`
- `hits`, `badge`, `badgeColor`

### LocalStorage 저장 구조
```javascript
localStorage.getItem('applications') = [
    {
        programId: "3",
        studentName: "김철수",
        studentId: "20231234",
        email: "student@example.com",
        phone: "010-1234-5678",
        motivation: "신청 동기 50자 이상...",
        expectations: "기대 효과...",
        toeicScore: "850",
        timestamp: "2025-11-04T12:30:00.000Z"
    }
]
```

---

## 🔄 화면 플로우

```
[메인 페이지]
    ↓ 프로그램 카드 클릭
[프로그램 목록]
    ↓ 카드 클릭
[프로그램 상세]
    ↓ 신청하기 버튼 클릭
[프로그램 신청] ← 이번 세션에서 완성!
    ↓ 폼 작성 → 유효성 검사 → 제출
[신청 완료 모달]
    ↓ 목록으로 / 마이페이지
[프로그램 목록] or [마일리지 현황]
```

---

## 🎨 "View First" 전략 - 현재 위치

### ✅ Phase 3: View Layer (Mock Implementation)
**목적**: 사용자 경험(UX) 먼저 확정
**구현 완료**:
- ✅ HTML/CSS/JavaScript
- ✅ Mock 데이터로 전체 흐름 동작
- ✅ LocalStorage 활용
- ✅ 폼 유효성 검사
- ✅ 모달 UI

**장점**:
- 화면 디자인과 흐름 검증 완료
- 팀원/교수님께 시연 가능
- 필요한 데이터 구조 파악 완료
- Backend 개발 가이드 확보

### ⏳ Phase 4: Service Layer (다음 단계)
**목적**: 비즈니스 로직 구현
**구현 예정**:
```java
@Service
public class ProgramApplicationService {
    // 프로그램 신청 로직
    public ApplicationResponse applyProgram(ApplicationRequest request) {
        // 1. 프로그램 존재 여부 확인
        // 2. 신청 가능 여부 체크 (마감, 정원)
        // 3. 중복 신청 확인
        // 4. DB 저장
        // 5. 마일리지 적립 (프로그램 참여)
        return new ApplicationResponse(...)
    }
}
```

**필요한 작업**:
1. Entity 설계 (Application, ApplicationStatus enum)
2. Repository 구현
3. Service 구현
4. 테스트 코드 작성

### ⏳ Phase 5: Controller + API Integration
**목적**: 프론트엔드 ↔ 백엔드 연결
**구현 예정**:
```java
@RestController
@RequestMapping("/api/programs")
public class ProgramApplicationController {
    
    @PostMapping("/{programId}/apply")
    public ResponseEntity<ApplicationResponse> applyProgram(
        @PathVariable Long programId,
        @RequestBody ApplicationRequest request
    ) {
        return ResponseEntity.ok(service.applyProgram(programId, request));
    }
}
```

**JavaScript 수정**:
```javascript
// Mock 제거
const response = await fetch(`/api/programs/${programId}/apply`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(formData)
});
```

---

## 🔧 중요 기술 결정사항

### 1. URL 패턴 결정
```
❌ /programs/{id}/apply (PathVariable)
✅ /program/apply?id={id} (QueryParameter)
```
**이유**: Controller 매핑 단순화, 향후 API 전환 용이

### 2. JavaScript 로드 순서
```html
<!-- main.js를 먼저 로드 (window.PROGRAMS export) -->
<script th:src="@{/js/main.js}"></script>
<!-- 그 다음 program-apply.js 로드 -->
<script th:src="@{/js/program-apply.js}"></script>
```
**이유**: program-apply.js에서 window.PROGRAMS 사용

### 3. Mock 데이터 저장소
- **현재**: LocalStorage
- **Phase 5**: MySQL Database
- **이유**: 브라우저에서 신청 내역 확인 가능, Phase 5 전환 용이

### 4. 폼 유효성 검사
- **클라이언트**: JavaScript (즉시 피드백)
- **서버**: Spring Validation (Phase 5에서 추가)
- **이유**: 사용자 경험 + 보안

---

## 📂 프로젝트 구조

```
scms-backend/
├── src/main/
│   ├── java/.../scms/
│   │   ├── controller/
│   │   │   ├── MainViewController.java ← /program/apply 추가
│   │   │   └── ...
│   │   ├── service/  ← Phase 4에서 구현 예정
│   │   └── ...
│   └── resources/
│       ├── templates/
│       │   ├── fragments/
│       │   │   ├── header.html
│       │   │   └── footer.html
│       │   ├── layouts/
│       │   │   └── default.html
│       │   ├── index.html
│       │   ├── program/
│       │   │   ├── list.html
│       │   │   ├── detail.html
│       │   │   └── apply.html ← NEW!
│       │   └── mileage/
│       │       └── dashboard.html
│       └── static/
│           ├── css/
│           │   ├── common.css
│           │   ├── layout.css
│           │   ├── main.css
│           │   └── program.css ← Apply 스타일 추가
│           └── js/
│               ├── common.js
│               ├── main.js ← PROGRAMS export 추가
│               ├── program-list.js
│               ├── program-detail.js ← 신청 버튼 연결
│               └── program-apply.js ← NEW!
```

---

## 🐛 해결한 주요 이슈

### Issue 1: "프로그램 정보를 찾을 수 없습니다" 에러
**원인**: window.PROGRAMS가 undefined
**해결**: 
1. main.js에서 `window.PROGRAMS = PROGRAMS` export
2. apply.html에서 main.js 먼저 로드

### Issue 2: `/program/list` 404 에러
**원인**: Controller 매핑은 `/programs`인데 JavaScript에서 `/program/list` 호출
**해결**: program-apply.js에서 3곳 수정
- `window.location.href = '/programs'`

### Issue 3: 프로그램 5, 6번 없음
**원인**: main.js의 PROGRAMS 배열에 1-4번만 존재
**해결**: 프로그램 5, 6 데이터 추가

### Issue 4: NoResourceFoundException for program/apply
**원인**: Controller 매핑 누락
**해결**: MainViewController에 `@GetMapping("/program/apply")` 추가

---

## 📈 다음 단계 옵션

### Option 1: Phase 3 View Layer 계속 진행
**남은 화면** (우선순위순):
1. **신청 이력 페이지** (`program/history.html`)
   - 신청한 프로그램 목록
   - 신청 상태 (대기/승인/거절)
   - 취소 버튼

2. **신청 취소 페이지** (`program/cancel.html`)
   - 취소 사유 입력
   - 취소 확인 모달

3. **이수내역 페이지** (`program/completion.html`)
   - 완료한 프로그램 목록
   - 마일리지 적립 내역
   - 이수증 다운로드 버튼

**예상 소요 시간**: 3개 화면 약 8-10시간

### Option 2: Phase 4 Service Layer 시작 (추천)
**이유**:
- 핵심 화면 완성됨 (신청 페이지까지)
- Backend 로직 구현 시작 가능
- Phase 3 잔여 화면은 Phase 5와 병행 가능

**구현 순서**:
1. **ApplicationEntity 설계**
   ```java
   @Entity
   public class Application {
       @Id @GeneratedValue
       private Long id;
       
       private Long programId;  // Program FK
       private Long studentId;  // User FK
       private String motivation;
       private String expectations;
       
       @Enumerated(EnumType.STRING)
       private ApplicationStatus status; // PENDING, APPROVED, REJECTED, CANCELLED
       
       private LocalDateTime appliedAt;
   }
   ```

2. **ApplicationRepository 구현**
   ```java
   public interface ApplicationRepository extends JpaRepository<Application, Long> {
       List<Application> findByStudentId(Long studentId);
       boolean existsByProgramIdAndStudentId(Long programId, Long studentId);
       // 추가 쿼리 메서드
   }
   ```

3. **ProgramApplicationService 구현**
   - 신청 로직
   - 중복 체크
   - 정원 체크
   - 마일리지 적립

4. **테스트 코드 작성**
   - Repository 테스트
   - Service 테스트

**예상 소요 시간**: 약 6-8시간

---

## ⚠️ 주의사항

### Phase 3 → Phase 4 전환 시
1. **데이터 구조 검증**
   - Mock 데이터의 필드가 Entity 설계에 반영되어야 함
   - LocalStorage 구조 → DTO 구조 매핑

2. **테스트 전략**
   - Repository 테스트: 실제 MySQL 사용
   - Service 테스트: Mock Repository 사용

3. **Git 브랜치 전략**
   - Phase 4: `feature/application-service`
   - Phase 5: `feature/application-api`

### Mock 데이터 유지
- Phase 5까지는 Mock 데이터 유지
- API 완성 후 Mock 제거
- 개발 초기에는 Mock으로 빠른 테스트

---

## 🔗 참고 문서

### 이번 세션에서 생성한 문서
- `docs/03-implementation/02-screen-implementation-roadmap.svg`
- `docs/03-implementation/03-current-progress-flow.svg`

### 기존 문서
- `docs/03-implementation/01-thymeleaf-fragment-guide.md`
- `docs/01-progress/05-phase3-view-layer-step1.md`

---

## 💾 Git 상태

```bash
# 현재 브랜치
main

# 최근 커밋
7d56d60 - Merge feature/program-apply-page

# 원격 저장소
✅ origin/main 동기화 완료

# Feature 브랜치
✅ feature/program-apply-page 삭제됨 (작업 완료)
```

---

## 📞 다음 세션 시작 시

### 즉시 확인할 것
```bash
# 1. 프로젝트 경로 확인
cd C:/Users/USER/Documents/choongang/Project/scms/scms-backend

# 2. Git 상태 확인
git status
git branch

# 3. 브라우저 테스트
http://localhost:8080/program/apply?id=3
# → 명칭스피치&이미지메이킹 신청 페이지 확인
```

### 시작 멘트 옵션

**Option 1 (Phase 3 계속):**
"이전 세션에서 프로그램 신청 페이지 완성했습니다! 다음은 신청 이력 페이지(program/history.html) 만들까요?"

**Option 2 (Phase 4 시작 - 추천):**
"프로그램 신청 페이지까지 완성했으니, 이제 Phase 4 Service Layer 시작해서 실제 비즈니스 로직을 구현할까요? ApplicationEntity와 Repository부터 시작하면 됩니다!"

---

## 🎯 개발 진행률

### Overall Progress
```
✅ Phase 1: Entity Layer          (100%)
✅ Phase 2: Repository Layer      (88.9%)
✅ Phase 3: View Layer            (70% - 6/9 화면 완료)
⏳ Phase 4: Service Layer         (0%)
⏳ Phase 5: Controller/API Layer  (0%)
⏳ Phase 6: Security              (0%)
⏳ Phase 7: Testing               (0%)
⏳ Phase 8: Deployment            (0%)
```

### Phase 3 Detailed Progress
- ✅ 공통 모듈 (100%)
- ✅ 메인 페이지 (100%)
- ✅ 프로그램 목록 (100%)
- ✅ 프로그램 상세 (100%)
- ✅ 프로그램 신청 (100%) ← NEW!
- ✅ 마일리지 현황 (100%)
- ⏳ 신청 이력 (0%)
- ⏳ 신청 취소 (0%)
- ⏳ 이수내역 (0%)

**예상 완료 시점**: 
- Phase 3 완료: +3 세션 (약 10시간)
- Phase 4 완료: +2 세션 (약 8시간)

---

**마지막 업데이트**: 2025-11-04 12:30
**다음 세션 권장 시작 시간**: Phase 4 시작 또는 Phase 3 계속
**예상 남은 개발 기간**: 약 22주 (전체 시스템 완성)
