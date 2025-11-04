# View Layer 구현 이슈 해결 가이드

> **작성일:** 2025-11-04  
> **작성자:** 호진 (with Claude)  
> **Phase:** View Layer - Program History & Dropdown Menu

---

## 📋 목차
1. [개요](#개요)
2. [이슈 1: 드롭다운 메뉴 CSS 미적용](#이슈-1-드롭다운-메뉴-css-미적용)
3. [이슈 2: Controller 매핑 누락](#이슈-2-controller-매핑-누락)
4. [이슈 3: Fragment 내 CSS 링크 무시](#이슈-3-fragment-내-css-링크-무시)
5. [학습 내용 및 Best Practices](#학습-내용-및-best-practices)

---

## 개요

**구현 내용:**
- 프로그램 신청 이력 페이지 (`program/history.html`)
- 헤더 드롭다운 메뉴 (비교과 프로그램)

**발생한 주요 이슈:**
1. 드롭다운 메뉴 CSS가 브라우저에 적용되지 않는 문제
2. Controller 매핑 누락으로 404 에러
3. Thymeleaf Fragment 내 CSS 링크 무시 문제

---

## 이슈 1: 드롭다운 메뉴 CSS 미적용

### 🔴 문제 상황

**증상:**
```
- JavaScript: ✅ 정상 작동 (클릭 시 'active' 클래스 토글됨)
- CSS: ❌ 드롭다운 메뉴가 화면에 표시 안 됨
- 브라우저 DevTools: CSS 규칙이 존재하지만 적용 안 됨
```

**시도한 해결 방법:**
1. ❌ 브라우저 캐시 삭제 → 효과 없음 (시크릿 창 사용 중)
2. ❌ `layout.css` z-index 수정 → 효과 없음
3. ❌ `overflow: visible` 추가 → 효과 없음
4. ❌ `default.html`에 인라인 `<style>` 추가 → 효과 없음

### ✅ 최종 해결 방법

**JavaScript에서 CSS를 동적으로 주입:**

```javascript
// common.js
document.addEventListener('DOMContentLoaded', function() {
    // CSS 동적 주입
    const style = document.createElement('style');
    style.textContent = `
        .dropdown-menu {
            position: absolute !important;
            top: 100% !important;
            left: 50% !important;
            transform: translateX(-50%) !important;
            background: white !important;
            border-radius: 8px !important;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15) !important;
            padding: 0.5rem 0 !important;
            margin-top: 1rem !important;
            min-width: 220px !important;
            opacity: 0 !important;
            visibility: hidden !important;
            transition: all 0.3s ease !important;
            z-index: 9999 !important;
            list-style: none !important;
            display: block !important;
        }
        .has-dropdown.active .dropdown-menu {
            opacity: 1 !important;
            visibility: visible !important;
            margin-top: 0.5rem !important;
        }
        // ... 추가 스타일
    `;
    document.head.appendChild(style);
    
    initSearch();
    initDropdown();
});
```

### 📊 진단 과정

1. **HTML 구조 확인:**
   - Elements 탭에서 `<li class="has-dropdown">` 존재 확인 ✅
   - `<ul class="dropdown-menu">` 존재 확인 ✅

2. **JavaScript 동작 확인:**
   ```javascript
   // Console 테스트
   document.querySelectorAll('.has-dropdown').length // 1 ✅
   typeof initDropdown // "function" ✅
   ```

3. **CSS 로드 확인:**
   ```javascript
   // Console 테스트
   const styles = Array.from(document.styleSheets);
   const layoutCSS = styles.find(s => s.href && s.href.includes('layout.css'));
   console.log('layout.css loaded?', layoutCSS ? 'YES' : 'NO'); // YES ✅
   ```

4. **CSS 규칙 존재 확인:**
   ```javascript
   const rules = Array.from(layoutCSS.cssRules || []);
   const dropdownRule = rules.find(r => r.selectorText && r.selectorText.includes('dropdown-menu'));
   console.log('dropdown-menu rule found?', dropdownRule ? 'YES' : 'NO'); // YES ✅
   ```

5. **Computed Style 확인:**
   ```javascript
   const menu = document.querySelector('.dropdown-menu');
   const computed = getComputedStyle(menu);
   console.log('Opacity:', computed.opacity); // 1 (기본값) ❌
   // 예상: 0, 실제: 1 → CSS 규칙이 적용 안 됨!
   ```

### 🔍 근본 원인 (추정)

**가능성 1: CSS 우선순위 문제**
- Spring Boot 정적 리소스 처리 순서 이슈
- Bootstrap CSS와의 충돌

**가능성 2: Thymeleaf 템플릿 렌더링 타이밍**
- Fragment 병합 시 CSS 규칙 손실

**가능성 3: 브라우저 렌더링 엔진 이슈**
- 특정 CSS 속성 조합이 무시됨

### 💡 왜 동적 주입이 작동했나?

```javascript
// JavaScript에서 <style> 태그를 직접 생성하여 <head>에 추가
// → 브라우저 렌더링 엔진이 즉시 적용
// → !important 플래그로 다른 스타일보다 우선순위 확보
```

---

## 이슈 2: Controller 매핑 누락

### 🔴 문제 상황

**증상:**
```
드롭다운 메뉴 링크 클릭 시:
- /program/list → 404 에러
- /program/history → 404 에러
```

**서버 로그:**
```
WARN: Resolved [org.springframework.web.servlet.resource.NoResourceFoundException: 
No static resource program/list.]
```

### ✅ 해결 방법

**Controller에 매핑 추가:**

```java
// MainViewController.java
@Controller
public class MainViewController {
    
    /**
     * 프로그램 목록 페이지 (대체 경로)
     */
    @GetMapping("/program/list")
    public String programListAlt() {
        return "program/list";
    }
    
    /**
     * 프로그램 신청 이력 페이지
     */
    @GetMapping("/program/history")
    public String programHistory() {
        return "program/history";
    }
}
```

### 📚 학습 포인트

**View First 개발 방식의 함정:**
- ✅ 장점: UI를 먼저 확인하며 개발 가능
- ⚠️ 주의: **Controller 매핑을 반드시 함께 작성해야 함**

**체크리스트:**
1. [ ] HTML 템플릿 작성
2. [ ] CSS 스타일 작성
3. [ ] JavaScript 로직 작성
4. [ ] **Controller 매핑 추가** ← 잊지 말 것!
5. [ ] 브라우저 테스트

---

## 이슈 3: Fragment 내 CSS 링크 무시

### 🔴 문제 상황

**증상:**
```html
<!-- history.html -->
<head>
    <link rel="stylesheet" th:href="@{/css/program-history.css}">
</head>
<body>
    <div th:fragment="content">
        <!-- 페이지 내용 -->
    </div>
</body>
```

위 방식으로 CSS를 추가했지만:
- `program-history.css`가 로드되지 않음
- 페이지 스타일이 깨짐

### ✅ 해결 방법

**CSS 링크를 Fragment 내부로 이동:**

```html
<!-- history.html -->
<body>
    <div th:fragment="content">
        <!-- Page Specific CSS -->
        <link rel="stylesheet" th:href="@{/css/program-history.css}">
        
        <!-- 페이지 내용 -->
    </div>
</body>
```

### 🔍 근본 원인

**Thymeleaf Fragment 교체 동작:**

```html
<!-- default.html -->
<html th:fragment="layout (title, content)">
<head>
    <title th:replace="${title}">Default Title</title>
    <!-- 공통 CSS들 -->
</head>
<body>
    <div th:replace="${content}">
        <!-- Fragment 내용이 여기 삽입됨 -->
    </div>
</body>
</html>
```

```html
<!-- history.html -->
<html th:replace="~{layouts/default :: layout(~{::title}, ~{::content})}">
<head>
    <title>신청 이력</title>
    <link rel="stylesheet" th:href="@{/css/program-history.css}"> ← 이 부분이 무시됨!
</head>
<body>
    <div th:fragment="content">
        <!-- 이 부분만 default.html에 삽입됨 -->
    </div>
</body>
</html>
```

**동작 원리:**
1. Thymeleaf가 `th:replace`를 만나면 **전체 HTML을 교체**
2. `default.html`의 레이아웃만 사용
3. `history.html`의 `<head>` 내용은 **버려짐**
4. **오직 `th:fragment="content"` 안의 내용만 삽입됨**

### 💡 페이지별 CSS 추가하는 방법

**방법 1: Fragment 내부에 CSS 링크 (권장)**
```html
<div th:fragment="content">
    <link rel="stylesheet" th:href="@{/css/page-specific.css}">
    <!-- 페이지 내용 -->
</div>
```

**방법 2: 조건부 CSS 로드 (복잡함)**
```html
<!-- default.html -->
<head>
    <link th:if="${pageName == 'history'}" 
          rel="stylesheet" th:href="@{/css/program-history.css}">
</head>
```

**방법 3: JavaScript에서 동적 로드**
```javascript
const link = document.createElement('link');
link.rel = 'stylesheet';
link.href = '/css/program-history.css';
document.head.appendChild(link);
```

---

## 학습 내용 및 Best Practices

### 🎯 View First 개발 시 주의사항

1. **Controller 매핑 먼저 확인**
   ```
   [ ] HTML 작성
   [ ] CSS 작성
   [ ] JavaScript 작성
   [ ] Controller 매핑 ← 필수!
   [ ] 테스트
   ```

2. **정적 리소스 로드 확인**
   - F12 → Network 탭에서 CSS/JS 파일 로드 확인
   - 404 에러 없는지 체크

3. **Thymeleaf Fragment 동작 이해**
   - `th:replace`는 전체 교체
   - 페이지별 CSS는 Fragment 내부에 작성

### 🛠️ 디버깅 체크리스트

**CSS가 적용 안 될 때:**
```javascript
// 1. CSS 파일 로드 확인
document.styleSheets

// 2. CSS 규칙 존재 확인
Array.from(document.styleSheets[0].cssRules)

// 3. Computed Style 확인
getComputedStyle(document.querySelector('.element'))

// 4. 강제 적용 테스트
element.style.cssText = 'opacity: 1 !important;'
```

**JavaScript가 작동 안 할 때:**
```javascript
// 1. 함수 존재 확인
typeof functionName

// 2. DOM 요소 존재 확인
document.querySelector('.selector')

// 3. 이벤트 리스너 확인
element.onclick // null이면 안 붙은 것

// 4. 에러 확인
// Console 탭에서 빨간 에러 메시지 확인
```

### 📝 개발 워크플로우 개선

**Before (문제 많음):**
```
1. HTML 작성
2. CSS 작성
3. JavaScript 작성
4. 브라우저 확인 → 404 에러! → Controller 추가
5. 다시 확인 → CSS 안 보임! → CSS 위치 수정
```

**After (효율적):**
```
1. Controller 매핑 먼저 추가
2. HTML 기본 구조 작성 → 즉시 테스트 (404 체크)
3. CSS 추가 (Fragment 내부) → 즉시 테스트 (스타일 체크)
4. JavaScript 추가 → 즉시 테스트 (동작 체크)
5. 최종 통합 테스트
```

### 🔥 긴급 상황 대응

**CSS가 절대 적용 안 될 때:**
```javascript
// 최후의 수단: JavaScript에서 스타일 강제 주입
const style = document.createElement('style');
style.textContent = `/* CSS 규칙 */`;
document.head.appendChild(style);
```

**주의:** 이 방법은 임시방편이며, 근본 원인 파악 필요!

---

## 📌 요약

| 이슈 | 원인 | 해결 방법 |
|------|------|-----------|
| 드롭다운 CSS 미적용 | 정확한 원인 미파악 (CSS 우선순위/렌더링 이슈 추정) | JavaScript에서 CSS 동적 주입 |
| Controller 매핑 누락 | View First 개발 시 Controller 작성 누락 | `@GetMapping` 추가 |
| Fragment CSS 무시 | Thymeleaf `th:replace`가 `<head>` 무시 | CSS 링크를 Fragment 내부로 이동 |

---

## 🎓 결론

**핵심 교훈:**
1. View First 개발 시 **Controller 매핑을 절대 잊지 말 것**
2. Thymeleaf Fragment 사용 시 **페이지별 CSS는 Fragment 내부에 작성**
3. CSS 적용 문제 발생 시 **체계적인 진단 프로세스 따르기**
4. 긴급 상황에는 **JavaScript 동적 주입으로 우회 가능**

**다음 작업 시 개선 사항:**
- [ ] Controller 매핑을 HTML 작성과 동시에 진행
- [ ] 페이지별 CSS는 항상 Fragment 내부에 작성
- [ ] Network 탭으로 정적 리소스 로드 항상 확인

---

**문서 버전:** 1.0  
**최종 수정:** 2025-11-04  
**관련 커밋:** `8a8b3f9` - feat(view): 프로그램 신청 이력 페이지 + 헤더 드롭다운 메뉴 구현
