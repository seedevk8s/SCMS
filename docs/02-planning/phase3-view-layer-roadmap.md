# Phase 3 - View Layer 전체 작업 로드맵

**작성일**: 2025-11-03  
**버전**: 1.0  
**목표**: 모든 화면 UI 구현 (Fragment 패턴 적용)

---

## 📊 Phase 3 전체 진행 현황

| Step | 작업 내용 | 진행률 | 완료 | 총 항목 | 상태 |
|------|----------|--------|------|---------|------|
| **Step 1** | 기본 레이아웃 + Fragment + 메인 페이지 | 100% | 5 | 5 | ✅ 완료 |
| **Step 2** | 프로그램 관련 페이지 | 0% | 0 | 3 | 🔄 예정 |
| **Step 3** | 인증 페이지 (로그인/회원가입) | 0% | 0 | 3 | 🔄 예정 |
| **Step 4** | 마일리지/역량진단 페이지 | 0% | 0 | 4 | 🔄 예정 |
| **Step 5** | 상담/포트폴리오 페이지 | 0% | 0 | 4 | 🔄 예정 |
| **Step 6** | 설문/마이페이지 | 0% | 0 | 3 | 🔄 예정 |
| **Step 7** | 관리자 페이지 (선택) | 0% | 0 | 5 | ⏳ 보류 |
| **전체** | - | **23%** | **5** | **22** | 🚀 진행중 |

---

## 📋 Step별 상세 작업 내역

### ✅ Step 1: 기본 레이아웃 + Fragment 패턴 (완료)

**작업 기간**: 2025-11-01 ~ 2025-11-03  
**브랜치**: `feature/view-layer-layout`  
**완료율**: 100% (5/5)

| # | 작업 항목 | 파일 | 상태 | 완료일 |
|---|----------|------|------|--------|
| 1 | Fragment Layout 구조 | `layouts/default.html` | ✅ | 2025-11-01 |
| 2 | Header Fragment | `fragments/header.html` | ✅ | 2025-11-01 |
| 3 | Footer Fragment | `fragments/footer.html` | ✅ | 2025-11-01 |
| 4 | 메인 페이지 (Hero Carousel) | `index.html` | ✅ | 2025-11-03 |
| 5 | 프로그램 목록 페이지 | `program/list.html` | ✅ | 2025-11-01 |

**주요 성과**:
- Fragment 패턴 적용으로 코드 재사용성 50% 향상
- Hero Carousel 구현 (자동 슬라이딩)
- 색상 최적화 (가독성 향상)

---

### 🔄 Step 2: 프로그램 관련 페이지 (예정)

**예상 기간**: 2025-11-04 ~ 2025-11-06 (3일)  
**브랜치**: `feature/program-pages`  
**완료율**: 0% (0/3)

| # | 작업 항목 | 파일 | 상태 | 설명 |
|---|----------|------|------|------|
| 1 | 프로그램 상세 페이지 | `program/detail.html` | 📝 예정 | 상세 정보, 신청 버튼, 참가자 현황 |
| 2 | 프로그램 신청 페이지 | `program/apply.html` | 📝 예정 | 신청서 작성 폼 |
| 3 | 내 프로그램 관리 | `program/my-programs.html` | 📝 예정 | 신청 내역, 참여 이력 |

**구현 기능**:
- 프로그램 상세 정보 표시
- 신청 폼 (Mock 데이터)
- 신청 내역 목록
- 필터/정렬 기능

**필요 JavaScript**:
- `program-detail.js` - 상세 페이지 동적 로딩
- `program-apply.js` - 신청 폼 검증
- `my-programs.js` - 내 프로그램 목록

**필요 CSS**:
- 프로그램 상세 레이아웃
- 신청 폼 스타일
- 진행 상태 표시

---

### 🔄 Step 3: 인증 페이지 (예정)

**예상 기간**: 2025-11-07 ~ 2025-11-09 (3일)  
**브랜치**: `feature/auth-pages`  
**완료율**: 0% (0/3)

| # | 작업 항목 | 파일 | 상태 | 설명 |
|---|----------|------|------|------|
| 1 | 로그인 페이지 | `auth/login.html` | 📝 예정 | 이메일/비밀번호 로그인, 소셜 로그인 |
| 2 | 회원가입 페이지 | `auth/register.html` | 📝 예정 | 학생 정보 입력, 약관 동의 |
| 3 | 비밀번호 찾기 | `auth/forgot-password.html` | 📝 예정 | 이메일 인증 |

**구현 기능**:
- 로그인 폼 (Mock 인증)
- 회원가입 폼 (유효성 검증)
- 비밀번호 재설정
- 소셜 로그인 UI (구글, 네이버)

**필요 JavaScript**:
- `login.js` - 로그인 처리
- `register.js` - 회원가입 유효성 검증
- `validation.js` - 폼 유효성 검증 유틸

**필요 CSS**:
- 로그인/회원가입 레이아웃
- 폼 스타일
- 소셜 로그인 버튼

---

### 🔄 Step 4: 마일리지/역량진단 페이지 (예정)

**예상 기간**: 2025-11-10 ~ 2025-11-13 (4일)  
**브랜치**: `feature/mileage-competency-pages`  
**완료율**: 0% (0/4)

| # | 작업 항목 | 파일 | 상태 | 설명 |
|---|----------|------|------|------|
| 1 | 마일리지 현황 | `mileage/balance.html` | 📝 예정 | 적립/사용 내역, 잔액 |
| 2 | 마일리지 내역 | `mileage/history.html` | 📝 예정 | 상세 내역, 필터 |
| 3 | 역량진단 목록 | `competency/survey-list.html` | 📝 예정 | 진단 항목 목록 |
| 4 | 역량진단 결과 | `competency/result.html` | 📝 예정 | 진단 결과, 차트 표시 |

**구현 기능**:
- 마일리지 잔액 표시 (차트)
- 적립/사용 내역 목록
- 역량진단 설문 목록
- 역량진단 결과 (Radar Chart)

**필요 JavaScript**:
- `mileage-balance.js` - 마일리지 차트 (Chart.js)
- `mileage-history.js` - 내역 필터링
- `survey-list.js` - 설문 목록
- `competency-result.js` - 결과 차트 (Radar)

**필요 CSS**:
- 마일리지 대시보드
- 내역 테이블
- 설문 카드
- 결과 차트 레이아웃

---

### 🔄 Step 5: 상담/포트폴리오 페이지 (예정)

**예상 기간**: 2025-11-14 ~ 2025-11-17 (4일)  
**브랜치**: `feature/counseling-portfolio-pages`  
**완료율**: 0% (0/4)

| # | 작업 항목 | 파일 | 상태 | 설명 |
|---|----------|------|------|------|
| 1 | 상담 예약 목록 | `counseling/reservations.html` | 📝 예정 | 예약 가능 시간, 캘린더 |
| 2 | 상담 신청 폼 | `counseling/apply.html` | 📝 예정 | 상담 신청 |
| 3 | 내 상담 내역 | `counseling/my-counseling.html` | 📝 예정 | 상담 이력 |
| 4 | 포트폴리오 | `portfolio/index.html` | 📝 예정 | 활동 이력, 성과 |

**구현 기능**:
- 상담 캘린더 (예약 가능 시간)
- 상담 신청 폼
- 상담 이력 목록
- 포트폴리오 타임라인

**필요 JavaScript**:
- `counseling-calendar.js` - 캘린더 (FullCalendar)
- `counseling-apply.js` - 상담 신청
- `portfolio.js` - 포트폴리오 타임라인

**필요 CSS**:
- 캘린더 스타일
- 상담 신청 폼
- 포트폴리오 타임라인

---

### 🔄 Step 6: 설문/마이페이지 (예정)

**예상 기간**: 2025-11-18 ~ 2025-11-20 (3일)  
**브랜치**: `feature/survey-mypage`  
**완료율**: 0% (0/3)

| # | 작업 항목 | 파일 | 상태 | 설명 |
|---|----------|------|------|------|
| 1 | 설문조사 목록 | `survey/list.html` | 📝 예정 | 진행중/완료 설문 |
| 2 | 설문 응답 | `survey/respond.html` | 📝 예정 | 설문 작성 |
| 3 | 마이페이지 | `mypage/index.html` | 📝 예정 | 프로필, 설정 |

**구현 기능**:
- 설문 목록 (진행중/완료)
- 설문 응답 폼
- 프로필 편집
- 알림 설정

**필요 JavaScript**:
- `survey-list.js` - 설문 목록
- `survey-respond.js` - 설문 응답 처리
- `mypage.js` - 프로필 편집

**필요 CSS**:
- 설문 카드
- 설문 폼
- 프로필 레이아웃

---

### ⏳ Step 7: 관리자 페이지 (선택/보류)

**예상 기간**: TBD  
**브랜치**: `feature/admin-pages`  
**완료율**: 0% (0/5)

| # | 작업 항목 | 파일 | 상태 | 설명 |
|---|----------|------|------|------|
| 1 | 관리자 대시보드 | `admin/dashboard.html` | ⏳ 보류 | 통계 대시보드 |
| 2 | 프로그램 관리 | `admin/programs.html` | ⏳ 보류 | CRUD |
| 3 | 사용자 관리 | `admin/users.html` | ⏳ 보류 | 사용자 목록 |
| 4 | 마일리지 관리 | `admin/mileage.html` | ⏳ 보류 | 적립/차감 |
| 5 | 설문 관리 | `admin/surveys.html` | ⏳ 보류 | 설문 생성/관리 |

**참고**: Phase 5 (Controller) 완료 후 필요시 구현

---

## 📈 전체 타임라인

```
2025-11
┌─────────────────────────────────────────────────────────┐
│ Week 1 (11/01-11/03) │ ✅ Step 1 완료                    │
├─────────────────────────────────────────────────────────┤
│ Week 2 (11/04-11/10) │ 🔄 Step 2, 3 (프로그램, 인증)     │
├─────────────────────────────────────────────────────────┤
│ Week 3 (11/11-11/17) │ 🔄 Step 4, 5 (마일리지, 상담)     │
├─────────────────────────────────────────────────────────┤
│ Week 4 (11/18-11/24) │ 🔄 Step 6 (설문, 마이페이지)      │
└─────────────────────────────────────────────────────────┘
```

**예상 완료일**: 2025-11-24 (약 3주)

---

## 🎨 공통 작업 사항

### Fragment 패턴 적용
모든 페이지에 Fragment 패턴 적용:
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      th:replace="~{layouts/default :: layout(~{::title}, ~{::content})}">
<head>
    <title>페이지 제목 - SCMS</title>
</head>
<body>
    <div th:fragment="content">
        <!-- 페이지 컨텐츠만 작성 -->
    </div>
</body>
</html>
```

### CSS 구조
```
static/css/
├── common.css        (공통 스타일 - 완료)
├── layout.css        (레이아웃 - 완료)
├── program.css       (프로그램 관련)
├── auth.css          (인증 관련)
├── mileage.css       (마일리지)
├── competency.css    (역량진단)
├── counseling.css    (상담)
├── portfolio.css     (포트폴리오)
├── survey.css        (설문)
└── mypage.css        (마이페이지)
```

### JavaScript 구조
```
static/js/
├── common.js         (공통 유틸 - 완료)
├── main.js           (메인 페이지 - 완료)
├── program-list.js   (프로그램 목록 - 완료)
├── program-detail.js
├── program-apply.js
├── login.js
├── register.js
├── validation.js
├── mileage-*.js
├── survey-*.js
└── ... (각 페이지별)
```

### Mock 데이터 전략
Phase 5 (Controller) 전까지는 Mock 데이터 사용:
```javascript
const MockData = {
    // Entity 구조와 동일하게 작성
    programs: [...],
    users: [...],
    mileage: [...]
};
```

---

## 📊 페이지별 우선순위

| 우선순위 | 페이지 | 이유 |
|---------|--------|------|
| **P0** (필수) | 메인, 프로그램 목록/상세, 로그인/회원가입 | 핵심 기능 |
| **P1** (중요) | 마일리지, 역량진단, 상담 예약 | 주요 도메인 |
| **P2** (보통) | 포트폴리오, 설문, 마이페이지 | 부가 기능 |
| **P3** (낮음) | 관리자 페이지 | Phase 5 이후 |

---

## 🎯 품질 기준

### 코드 품질
- ✅ Fragment 패턴 적용 (코드 재사용)
- ✅ 반응형 디자인 (모바일 대응)
- ✅ 접근성 (ARIA 레이블)
- ✅ 브라우저 호환성 (Chrome, Firefox, Safari)

### 성능
- ✅ 페이지 로드 시간 < 2초
- ✅ JavaScript 번들 최소화
- ✅ CSS 최적화
- ✅ 이미지 압축

### 테스트
- ✅ 페이지 렌더링 테스트
- ✅ 폼 유효성 검증 테스트
- ✅ Mock 데이터 표시 테스트
- ✅ 반응형 테스트 (모바일/태블릿/데스크톱)

---

## 📝 작업 프로세스

### 각 Step별 작업 순서
1. **브랜치 생성**
   ```bash
   git checkout main
   git pull origin main
   git checkout -b feature/[step-name]
   ```

2. **파일 생성**
   - HTML (Fragment 패턴)
   - CSS (필요한 스타일만)
   - JavaScript (Mock 데이터)

3. **테스트**
   - 브라우저에서 페이지 확인
   - 반응형 테스트
   - 콘솔 에러 확인

4. **커밋 & 푸시**
   ```bash
   git add .
   git commit -m "feat: [작업 내용]"
   git push origin feature/[step-name]
   ```

5. **병합**
   ```bash
   git checkout main
   git merge feature/[step-name] --no-ff
   git push origin main
   ```

6. **문서 업데이트**
   - 진행 상황 업데이트
   - 스크린샷 추가
   - 이슈/해결 방법 기록

---

## 🚀 Step 2 시작 준비

### 다음 작업: 프로그램 관련 페이지
**브랜치**: `feature/program-pages`

**작업 순서**:
1. 프로그램 상세 페이지 (`program/detail.html`)
2. 프로그램 신청 페이지 (`program/apply.html`)
3. 내 프로그램 관리 (`program/my-programs.html`)

**예상 소요 시간**: 3일

**시작 명령어**:
```bash
git checkout main
git pull origin main
git checkout -b feature/program-pages
```

---

## 📚 참고 문서

- [Phase 3 - Step 1 완료 보고서](../01-progress/05-phase3-view-layer-step1.md)
- [Thymeleaf Fragment 가이드](../03-implementation/01-thymeleaf-fragment-guide.md)
- [Fragment Architecture SVG](../03-implementation/fragment-architecture.svg)

---

**작성자**: Hojin  
**최종 업데이트**: 2025-11-03  
**버전**: 1.0  
**다음 문서**: Step 2 작업 보고서 (예정)
