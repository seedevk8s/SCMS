# SCMS Phase 3 View Layer - 세션 재개 문서

**작성일**: 2025-11-04
**세션 종료 시점**: 토큰 사용률 59.1% (112,371/190,000)
**Git 상태**: main 브랜치, 최신 커밋 3674fe7

---

## ✅ 이번 세션에서 완료한 작업

### 🎉 프로그램 신청 페이지 완성!

**완성된 페이지**: `program/apply.html`
**URL**: `/program/apply?id={programId}`
**JavaScript**: `program-apply.js`

#### 구현된 기능
1. **4단계 신청 프로세스**
   - STEP 1: 프로그램 정보 확인
   - STEP 2: 신청자 정보 입력
   - STEP 3: 신청 동기/기대효과
   - STEP 4: 개인정보 동의

2. **JavaScript 기능**
   - Mock 데이터 연동 (window.PROGRAMS)
   - 실시간 글자 수 카운터
   - 연락처 자동 포맷팅 (010-1234-5678)
   - 폼 유효성 검사
   - 전체 동의 체크박스
   - 모달 UI (개인정보, 운영규정)
   - LocalStorage 저장
   - 신청 완료 모달

3. **반응형 디자인**
   - 데스크톱/태블릿/모바일 대응
   - 모달 애니메이션

---

## 📊 Phase 3 View Layer 진행 상황

### ✅ 완료된 화면 (6개)
| 번호 | 페이지 | 파일 | URL | 상태 |
|------|--------|------|-----|------|
| 1 | 공통 모듈 | fragments/header.html, footer.html | - | ✅ 완료 |
| 2 | 메인 페이지 | index.html | `/` | ✅ 완료 |
| 3 | 프로그램 목록 | program/list.html | `/programs` | ✅ 완료 |
| 4 | 프로그램 상세 | program/detail.html | `/programs/{id}` | ✅ 완료 |
| 5 | 프로그램 신청 | program/apply.html | `/program/apply?id={id}` | ✅ 완료 |
| 6 | 마일리지 현황 | mileage/dashboard.html | `/mileage` | ✅ 완료 |

### ⏳ 다음 세션에서 완료할 화면 (3개)

#### 🔥 우선순위 1: 신청 이력 페이지
**파일**: `program/history.html`
**URL**: `/program/history` 또는 `/programs/my-applications`
**기능**:
- 내가 신청한 프로그램 목록 표시
- 신청 상태 (대기중/승인/반려/취소)
- 신청일시, 프로그램 정보
- 취소 버튼 (취소 가능한 경우)
- 필터링: 전체/대기중/승인/반려/취소
- Mock 데이터: LocalStorage의 applications 사용

**예상 시간**: 3-4시간

#### 🔥 우선순위 2: 신청 취소 페이지
**파일**: `program/cancel.html` (또는 모달로 처리)
**URL**: `/program/cancel?applicationId={id}`
**기능**:
- 신청 정보 확인
- 취소 사유 입력 (선택사항)
- 취소 확인 모달
- 취소 완료 처리 (LocalStorage 업데이트)

**예상 시간**: 2-3시간

#### 🔥 우선순위 3: 이수내역 페이지
**파일**: `program/completion.html`
**URL**: `/program/completion` 또는 `/programs/completed`
**기능**:
- 완료한 프로그램 목록
- 획득 마일리지 표시
- 이수증 다운로드 버튼 (Mock - alert)
- 학기별 필터
- Mock 데이터: 완료 상태인 프로그램만 표시

**예상 시간**: 2-3시간

---

## 🎯 다음 세션 작업 계획

### 목표: Phase 3 View Layer 100% 완성!

```
[현재] 6개 화면 완료 (66.7%)
    ↓
[다음 세션] 3개 화면 추가
    ↓
[완료] 9개 화면 완료 (100%)
```

### 작업 순서
1. **신청 이력 페이지** (3-4시간)
   - Mock 데이터 구조 설계
   - HTML/CSS 작성
   - JavaScript 구현
   - 상태별 필터링

2. **신청 취소 기능** (2-3시간)
   - 취소 모달 또는 페이지
   - LocalStorage 업데이트
   - 이력 페이지 연동

3. **이수내역 페이지** (2-3시간)
   - 완료 프로그램 목록
   - 마일리지 표시
   - 이수증 다운로드 버튼

**총 예상 시간**: 7-10시간

---

## 📂 Mock 데이터 구조 (LocalStorage)

### 현재 저장된 데이터
```javascript
// 신청 데이터
localStorage.getItem('applications') = [
    {
        id: 1,  // 추가 필요
        programId: "3",
        programTitle: "명칭스피치&이미지메이킹",  // 추가 필요
        studentName: "김철수",
        studentId: "20231234",
        email: "student@example.com",
        phone: "010-1234-5678",
        motivation: "신청 동기...",
        expectations: "기대 효과...",
        status: "PENDING",  // 추가 필요: PENDING, APPROVED, REJECTED, CANCELLED
        appliedAt: "2025-11-04T12:30:00.000Z",
        cancelledAt: null  // 추가 필요
    }
]
```

### 추가로 필요한 Mock 데이터
```javascript
// 완료된 프로그램 (이수내역용)
localStorage.getItem('completions') = [
    {
        id: 1,
        programId: "1",
        programTitle: "2025-2학기 토익경시대회",
        completedAt: "2025-10-20T18:00:00.000Z",
        mileage: 10,
        certificateUrl: "mock-certificate-url"  // Mock
    }
]
```

---

## 🎨 화면 레이아웃 참고

### 신청 이력 페이지 (program/history.html)
```
┌─────────────────────────────────────┐
│  프로그램 신청 이력                    │
├─────────────────────────────────────┤
│ [전체] [대기중] [승인] [반려] [취소]   │  ← 탭/필터
├─────────────────────────────────────┤
│ ┌─────────────────────────────┐     │
│ │ 📋 명칭스피치&이미지메이킹     │     │
│ │ 신청일: 2025-11-04           │     │
│ │ 상태: 🟡 승인 대기           │     │
│ │ [취소하기]                   │     │
│ └─────────────────────────────┘     │
│ ┌─────────────────────────────┐     │
│ │ 📋 토익경시대회              │     │
│ │ 신청일: 2025-10-15           │     │
│ │ 상태: ✅ 승인 완료           │     │
│ └─────────────────────────────┘     │
└─────────────────────────────────────┘
```

### 신청 취소 모달
```
┌─────────────────────────────┐
│  신청 취소 확인               │
├─────────────────────────────┤
│                             │
│  다음 프로그램 신청을         │
│  취소하시겠습니까?           │
│                             │
│  • 명칭스피치&이미지메이킹    │
│  • 신청일: 2025-11-04        │
│                             │
│  [취소 사유 입력]            │
│  ┌─────────────────────┐   │
│  │                     │   │
│  └─────────────────────┘   │
│                             │
│  [돌아가기]  [취소하기]      │
└─────────────────────────────┘
```

### 이수내역 페이지 (program/completion.html)
```
┌─────────────────────────────────────┐
│  이수 프로그램                        │
├─────────────────────────────────────┤
│ 학기: [2025-1학기 ▼]                 │
├─────────────────────────────────────┤
│ ┌─────────────────────────────┐     │
│ │ 🎓 2025-2학기 토익경시대회   │     │
│ │ 완료일: 2025-10-20           │     │
│ │ 마일리지: +10점              │     │
│ │ [이수증 다운로드]            │     │
│ └─────────────────────────────┘     │
│ ┌─────────────────────────────┐     │
│ │ 🎓 AI 역량 강화 워크샵       │     │
│ │ 완료일: 2025-09-15           │     │
│ │ 마일리지: +15점              │     │
│ │ [이수증 다운로드]            │     │
│ └─────────────────────────────┘     │
└─────────────────────────────────────┘
```

---

## 🔧 Git 워크플로우

### 다음 세션 시작
```bash
cd C:/Users/USER/Documents/choongang/Project/scms/scms-backend

# 1. 신청 이력 페이지
git checkout -b feature/program-history-page
# 작업...
git add .
git commit -m "feat: Implement program application history page"
git checkout main
git merge feature/program-history-page --no-ff
git push origin main
git branch -d feature/program-history-page

# 2. 신청 취소 기능
git checkout -b feature/program-cancel
# 작업...
git add .
git commit -m "feat: Implement program application cancel feature"
git checkout main
git merge feature/program-cancel --no-ff
git push origin main
git branch -d feature/program-cancel

# 3. 이수내역 페이지
git checkout -b feature/program-completion-page
# 작업...
git add .
git commit -m "feat: Implement program completion history page"
git checkout main
git merge feature/program-completion-page --no-ff
git push origin main
git branch -d feature/program-completion-page
```

---

## 📝 Controller 추가 예정

### MainViewController.java
```java
/**
 * 프로그램 신청 이력 페이지
 */
@GetMapping("/program/history")
public String programHistory() {
    return "program/history";
}

/**
 * 프로그램 신청 취소 페이지
 */
@GetMapping("/program/cancel")
public String programCancel() {
    return "program/cancel";
}

/**
 * 프로그램 이수내역 페이지
 */
@GetMapping("/program/completion")
public String programCompletion() {
    return "program/completion";
}
```

---

## 🎯 Phase 3 완료 후 다음 단계

### Phase 3 완료 시점
- ✅ 9개 화면 모두 완성
- ✅ Mock 데이터로 전체 플로우 동작
- ✅ 사용자 경험(UX) 검증 완료

### Phase 4-5 (Backend Integration)
**그때 진행할 것**:
- Service Layer 구현 (비즈니스 로직)
- REST API 구현
- Mock 데이터 제거
- 실제 DB 연동
- Phase 3에서 만든 화면은 그대로 유지!

---

## 🔗 참고 화면 플로우

```
[메인] → [프로그램 목록] → [프로그램 상세] → [신청]
                                              ↓
                                        [신청 완료]
                                              ↓
                         ┌────────────────────┴────────────────────┐
                         ↓                                         ↓
                  [신청 이력] ← 다음 세션                    [마이페이지]
                         ↓
                  [신청 취소] ← 다음 세션
                         
[마이페이지] → [이수내역] ← 다음 세션
```

---

## ⚠️ 중요 포인트

### Phase 3 View Layer 완성이 목표!
- ❌ Service Layer 구현 NO
- ❌ API 연동 NO
- ❌ 실제 DB 저장 NO
- ✅ HTML/CSS/JavaScript만 완성
- ✅ Mock 데이터로 전체 흐름 동작

### "View First" 전략
1. **Phase 3**: 화면 완성 (Mock) ← **지금 여기!**
2. **Phase 4-5**: Backend 구현 후 연결

---

## 📞 다음 세션 시작 멘트

**"이전 세션에서 프로그램 신청 페이지까지 완성했습니다! 이제 Phase 3 View Layer를 마무리하기 위해 신청 이력 페이지(program/history.html)부터 만들겠습니다. 3개 화면만 더 완성하면 Phase 3가 100% 완료됩니다!"**

---

## 📊 최종 목표

```
Phase 3 View Layer 완료 체크리스트
✅ 공통 모듈
✅ 메인 페이지
✅ 프로그램 목록
✅ 프로그램 상세
✅ 프로그램 신청
✅ 마일리지 현황
⏳ 신청 이력 ← 다음 세션 #1
⏳ 신청 취소 ← 다음 세션 #2
⏳ 이수내역 ← 다음 세션 #3

완료 후: Phase 3 (100%) → Phase 4 시작
```

**예상 완료 시점**: 다음 세션 (7-10시간 작업)

---

**마지막 업데이트**: 2025-11-04 12:45
**다음 작업**: 신청 이력 페이지 구현
**Git 브랜치**: main → feature/program-history-page 생성 예정
