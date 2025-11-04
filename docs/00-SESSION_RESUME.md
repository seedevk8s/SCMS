# SCMS Phase 3 View Layer - 세션 재개 문서

**작성일**: 2025-11-04
**세션 종료 시점**: 토큰 사용률 56.8% (107,844/190,000)

---

## ✅ 현재까지 완료된 작업 (5개 화면)

### 1. 공통 모듈
- ✅ Header Fragment (`fragments/header.html`)
- ✅ Footer Fragment (`fragments/footer.html`)
- ✅ Default Layout (`layouts/default.html`)

### 2. 메인 페이지
- ✅ `index.html` - 메인 홈 화면
- ✅ Hero Section (3개 자동 슬라이드, 5초 간격)
- ✅ Icon Menu (4개 역량 아이콘)
- ✅ 전체 프로그램 섹션

### 3. 프로그램 목록 페이지
- ✅ `program/list.html` - URL: `/programs`
- ✅ 검색/필터링 UI
- ✅ 프로그램 카드 그리드
- ✅ 페이징 섹션

### 4. 프로그램 상세 페이지
- ✅ `program/detail.html` - URL: `/programs/{id}`
- ✅ 프로그램 정보, 신청 현황, 탭 메뉴
- ✅ 신청 버튼 (아직 Mock 처리만)

### 5. 마일리지 현황 페이지
- ✅ `mileage/dashboard.html`
- ✅ 학기별 통계, 차트, 적립 내역

---

## 🔧 방금 완료한 중요 작업

### Mock 데이터 동기화 완료!

**문제**: 메인 페이지와 상세 페이지의 Mock 데이터가 불일치
- 메인에서 2번 프로그램 클릭 → 다른 내용 표시됨

**해결**: `main.js` 수정 완료
```javascript
// main.js의 ID 2번을 수정
{
    id: 2,
    title: 'AI 역량 강화 워크샵',  // 변경됨
    description: '인공지능 기초부터 응용까지 배우는 워크샵',
    center: '진로개발센터',
    badge: '마감',
    ...
}
```

**현재 상태**: 3개 파일 Mock 데이터 일치 ✅
- `main.js` (메인 페이지)
- `program-list.js` (목록 페이지)
- `program-detail.js` (상세 페이지)

---

## 🎯 다음 작업: 프로그램 신청 페이지

### 구현할 페이지
**파일**: `src/main/resources/templates/program/apply.html`
**URL**: `/programs/{id}/apply` 또는 `/programs/apply?id={id}`

### 연결 흐름
```
프로그램 상세 페이지 (program/detail.html)
    ↓
신청 버튼 클릭 (#applyButton, #applyButtonBottom)
    ↓
프로그램 신청 페이지 (program/apply.html) ← 이것을 만들어야 함!
```

### 구현 내용
1. **신청 폼**
   - 학생 정보 (자동 입력 - 김철수, 2024001234)
   - 신청 사유 (Textarea)
   - 개인정보 수집 동의 (Checkbox)

2. **Mock 처리**
   - 제출 버튼 클릭 → alert('신청이 완료되었습니다')
   - 신청 이력 페이지로 이동 (나중에 구현)

3. **유효성 검사**
   - 필수 입력 체크
   - 신청 사유 최소 10자 이상

---

## 📂 주요 파일 위치

### Templates
```
src/main/resources/templates/
├── fragments/
│   ├── header.html
│   └── footer.html
├── layouts/
│   └── default.html
├── index.html (메인)
├── program/
│   ├── list.html (목록)
│   ├── detail.html (상세)
│   └── apply.html (신청) ← 다음 작업
└── mileage/
    └── dashboard.html
```

### JavaScript
```
src/main/resources/static/js/
├── common.js
├── main.js (메인 페이지)
├── program-list.js (목록)
├── program-detail.js (상세)
└── program-apply.js (신청) ← 다음 작업
```

### CSS
```
src/main/resources/static/css/
├── common.css
├── layout.css
├── main.css
└── program.css
```

---

## 📊 화면 플로우 다이어그램

완성된 SVG: `docs/03-implementation/03-current-progress-flow.svg`

```
메인 (localhost:8080)
    ↓ [비교과 프로그램 메뉴]
프로그램 목록 (localhost:8080/programs)
    ↓ [카드 클릭]
프로그램 상세 (localhost:8080/programs/2)
    ↓ [신청 버튼 클릭]
프로그램 신청 (localhost:8080/programs/2/apply) ← 다음 구현
    ↓ [제출 완료]
신청 이력 (localhost:8080/programs/history) ← 향후 구현
```

---

## 📈 진행률

**Week 1-2 목표: 비교과 프로그램 완성 (9개 화면)**

| 항목 | 상태 | 비고 |
|------|------|------|
| 공통 모듈 | ✅ 완료 | Header/Footer/Layout |
| 메인 페이지 | ✅ 완료 | index.html |
| 프로그램 목록 | ✅ 완료 | program/list.html |
| 프로그램 상세 | ✅ 완료 | program/detail.html |
| 마일리지 현황 | ✅ 완료 | mileage/dashboard.html |
| **프로그램 신청** | 🔄 다음 작업 | program/apply.html |
| 신청 취소 | ⏳ 대기 | program/cancel.html |
| 신청 이력 | ⏳ 대기 | program/history.html |
| 이수내역 | ⏳ 대기 | program/completion.html |

**현재 진행률**: 55.6% (5/9 완료)

---

## 🎨 프로그램 신청 페이지 레퍼런스

### UI 구성 (참고: https://champ.woosuk.ac.kr/ko/)
1. **Page Header**
   - 제목: "프로그램 신청"
   - 프로그램명 표시

2. **신청 정보 박스**
   - 프로그램명
   - 신청 기간
   - 운영 일시
   - 장소

3. **신청자 정보 (읽기 전용)**
   - 이름: 김철수
   - 학번: 2024001234
   - 학과: 컴퓨터공학과
   - 연락처: 010-1234-5678

4. **신청 사유 (필수)**
   - Textarea (최소 10자)

5. **개인정보 수집 동의 (필수)**
   - Checkbox

6. **제출 버튼**
   - "신청하기" (파란색)
   - "취소" (회색)

---

## 💡 코딩 시작 방법

### 1단계: Feature Branch 생성
```bash
cd C:/Users/USER/Documents/choongang/Project/scms/scms-backend
git checkout -b feature/program-apply-page
```

### 2단계: HTML 파일 생성
```
src/main/resources/templates/program/apply.html
```

### 3단계: JavaScript 파일 생성
```
src/main/resources/static/js/program-apply.js
```

### 4단계: Mock 데이터 준비
- program-detail.js에서 프로그램 정보 가져오기
- 학생 정보는 하드코딩

### 5단계: 신청 버튼 연결
`program-detail.js` 수정:
```javascript
applyButtons.forEach(button => {
    button.addEventListener('click', () => {
        const programId = getProgramId();
        window.location.href = `/programs/${programId}/apply`;
    });
});
```

---

## ⚠️ 주의사항

1. **Thymeleaf Fragment 패턴 사용**
   - `th:replace="~{layouts/default :: layout(~{::title}, ~{::content})}"`

2. **Mock 데이터 일관성 유지**
   - 3개 JS 파일의 프로그램 정보 동일하게 유지

3. **Phase 3 원칙**
   - 실제 API 연동 없음
   - alert()로 완료 처리
   - Mock 데이터만 사용

4. **Git 워크플로우**
   - Feature Branch 사용
   - 완성 후 test → commit → merge → push

---

## 🔗 참고 문서

- `docs/03-implementation/01-thymeleaf-fragment-guide.md`
- `docs/03-implementation/02-screen-implementation-roadmap.svg`
- `docs/03-implementation/03-current-progress-flow.svg`
- `docs/01-progress/05-phase3-view-layer-step1.md`

---

## 📞 문의사항

**다음 세션 시작 시 확인사항:**
1. Mock 데이터 동기화 확인 (메인 → 목록 → 상세)
2. 프로그램 신청 페이지 UI 디자인 확정
3. 신청 완료 후 이동 경로 결정

**예상 소요 시간**: 프로그램 신청 페이지 약 4시간

---

**다음 세션 시작 멘트:**
"이전 세션에서 Mock 데이터 동기화 완료했고, 지금부터 프로그램 신청 페이지(program/apply.html) 만들면 됩니다!"
