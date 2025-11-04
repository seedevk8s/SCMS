# Controller 리팩토링 가이드

## 개요

View Layer 구현 중 Controller 구조를 개선하기 위한 리팩토링을 진행했습니다.
단일 MainViewController에 모든 View 매핑이 집중되어 있던 구조를 도메인별로 분리하여 유지보수성과 확장성을 향상시켰습니다.

**리팩토링 일자**: 2025-11-04  
**작업 브랜치**: feature/view-mileage-dashboard  
**목적**: View/API 분리 및 도메인별 책임 분리

---

## 리팩토링 배경

### 기존 구조의 문제점

**Before - 단일 Controller 구조**
```
src/main/java/com/university/scms/
└── controller/
    ├── HealthCheckController.java
    ├── MainViewController.java      ← 모든 View 매핑이 여기에
    └── TestController.java
```

**MainViewController의 비대화**
```java
@Controller
public class MainViewController {
    @GetMapping("/")                    // 메인
    @GetMapping("/programs")            // 프로그램 목록
    @GetMapping("/program/list")        // 프로그램 목록 (대체)
    @GetMapping("/programs/{id}")       // 프로그램 상세
    @GetMapping("/program/apply")       // 프로그램 신청
    @GetMapping("/program/history")     // 프로그램 이력
    @GetMapping("/mileage")             // 마일리지
    // ... 계속 증가
}
```

### 문제점 분석

1. **단일 책임 원칙(SRP) 위반**
   - 하나의 Controller가 여러 도메인을 담당
   - 파일 크기가 지속적으로 증가

2. **유지보수 어려움**
   - 프로그램 관련 수정 시 전체 Controller 열어야 함
   - 특정 도메인 로직 찾기 어려움

3. **팀 협업 충돌**
   - 여러 팀원이 동시에 같은 파일 수정
   - Git merge conflict 가능성 증가

4. **확장성 제한**
   - Phase 5 API 추가 시 View/API 분리 어려움
   - MSA 전환 준비 곤란

---

## 리팩토링 목표

### 1. View/API 명확한 분리

Phase 5에서 REST API 추가를 대비하여 View Controller를 별도 패키지로 분리

```
controller/
├── view/          ← View 렌더링 전용 (Phase 3)
└── api/           ← REST API 전용 (Phase 5)
```

### 2. 도메인별 책임 분리

각 Controller가 하나의 도메인만 담당하도록 분리

```
view/
├── MainViewController        ← 메인 페이지
├── ProgramViewController     ← 프로그램 도메인
└── MileageViewController     ← 마일리지 도메인
```

### 3. MSA 전환 준비

Domain 기반 구조로의 점진적 전환 가능성 확보

---

## 리팩토링 결과

### After - 도메인별 분리 구조

```
src/main/java/com/university/scms/
└── controller/
    ├── view/                               ← View 렌더링 전용
    │   ├── MainViewController.java         ← 메인, 공통 페이지
    │   ├── ProgramViewController.java      ← 프로그램 관련
    │   └── MileageViewController.java      ← 마일리지 관련
    │
    ├── HealthCheckController.java
    └── TestController.java
```

### 각 Controller 상세

#### 1. MainViewController

**역할**: 메인 페이지 및 공통 페이지 렌더링

```java
package com.university.scms.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainViewController {

    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    @GetMapping("/test")
    public String test() {
        return "test";
    }
}
```

**매핑 URL**:
- GET `/` → index.html
- GET `/test` → test.html

---

#### 2. ProgramViewController

**역할**: 비교과 프로그램 관련 모든 View 렌더링

```java
package com.university.scms.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProgramViewController {

    @GetMapping("/programs")
    public String list() {
        return "program/list";
    }
    
    @GetMapping("/program/list")
    public String listAlt() {
        return "program/list";
    }
    
    @GetMapping("/programs/{id}")
    public String detail(@PathVariable Long id) {
        return "program/detail";
    }
    
    @GetMapping("/program/apply")
    public String apply() {
        return "program/apply";
    }
    
    @GetMapping("/program/history")
    public String history() {
        return "program/history";
    }
}
```

**매핑 URL**:
- GET `/programs` → program/list.html
- GET `/program/list` → program/list.html (대체 경로)
- GET `/programs/{id}` → program/detail.html
- GET `/program/apply` → program/apply.html
- GET `/program/history` → program/history.html

**특징**:
- `/programs`와 `/program` 두 가지 경로 스타일 지원
- 향후 통일 필요 (Phase 4)

---

#### 3. MileageViewController

**역할**: 마일리지 관련 View 렌더링

```java
package com.university.scms.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mileage")
public class MileageViewController {

    @GetMapping("")
    public String dashboard() {
        return "mileage/dashboard";
    }
}
```

**매핑 URL**:
- GET `/mileage` → mileage/dashboard.html

**특징**:
- @RequestMapping으로 Base Path 설정
- 향후 `/mileage/history`, `/mileage/usage` 등 추가 예정

---

## 리팩토링 장점

### 1. 책임 분리 (SRP)

각 Controller가 하나의 도메인만 담당
```
MainViewController     → 메인 페이지만
ProgramViewController  → 프로그램 도메인만
MileageViewController  → 마일리지 도메인만
```

### 2. 유지보수성 향상

프로그램 관련 수정 시 ProgramViewController만 열면 됨
```java
// Before: MainViewController에서 프로그램 관련 메서드 찾기
// After: ProgramViewController에 모든 프로그램 메서드 집중
```

### 3. 팀 협업 개선

각자 다른 Controller 작업 가능
```
팀원 A: ProgramViewController 작업
팀원 B: MileageViewController 작업
→ 충돌 최소화
```

### 4. 확장성 확보

새 도메인 추가 시 새 Controller만 생성
```java
// 상담 기능 추가 시
CounselingViewController.java 생성만 하면 됨
```

### 5. Phase 5 준비

REST API 추가 시 명확한 구조
```
controller/
├── view/
│   └── ProgramViewController.java    ← HTML 렌더링
└── api/
    └── ProgramApiController.java     ← JSON 응답
```

---

## Phase별 확장 계획

### Phase 3 (현재)

View Layer 완성
```
controller/view/
├── MainViewController
├── ProgramViewController
├── MileageViewController
├── CounselingViewController     (예정)
├── CompetencyViewController     (예정)
└── CareerViewController          (예정)
```

### Phase 4 (Service Layer)

Service 추가하지만 Controller 구조 유지
```
service/
├── ProgramService
├── MileageService
└── ...

controller/view/
└── (구조 유지)
```

### Phase 5 (API Layer)

REST API Controller 추가
```
controller/
├── view/
│   ├── ProgramViewController.java       ← HTML 렌더링
│   └── MileageViewController.java
└── api/
    ├── ProgramApiController.java        ← JSON 응답
    └── MileageApiController.java
```

**역할 분리**:
- view/: Thymeleaf 템플릿 렌더링 (return "template")
- api/: JSON 응답 (@RestController, return ResponseEntity)

---

## MSA 전환 준비

### 현재 구조의 MSA-Ready 특징

#### 1. 도메인별 명확한 경계

각 Controller가 도메인을 대표
```
ProgramViewController   → Program Domain
MileageViewController   → Mileage Domain
```

#### 2. Cross-Domain FK 없음 (이미 완료)

Entity 설계 시 이미 준비됨
```java
// Program과 Mileage 간 직접 FK 없음
// ID 참조만 사용
```

#### 3. Phase 5+ Domain 구조 전환 가능

```
domain/
├── program/
│   ├── entity/              ← 이미 존재
│   ├── repository/          ← Phase 2 완료
│   ├── service/             ← Phase 4
│   └── controller/
│       ├── view/            ← Phase 3
│       └── api/             ← Phase 5
```

### MSA 분리 시나리오

**독립 서비스로 분리 가능**:
```bash
# Program 서비스
scms-program-service/
└── domain/program/
    ├── entity/
    ├── repository/
    ├── service/
    └── controller/

# Mileage 서비스
scms-mileage-service/
└── domain/mileage/
    ├── entity/
    ├── repository/
    ├── service/
    └── controller/
```

---

## 작업 과정

### 1. feature/view-program-history 브랜치 Merge

```bash
git checkout main
git merge feature/view-program-history
```

**포함 내용**:
- 프로그램 신청 이력 페이지 (/program/history)
- 헤더 드롭다운 메뉴 구현
- 관련 CSS, JS 파일

### 2. controller/view 패키지 생성

```bash
mkdir src/main/java/com/university/scms/controller/view
```

### 3. 도메인별 Controller 생성

```java
// MainViewController.java
// ProgramViewController.java
// MileageViewController.java
```

### 4. 기존 MainViewController 삭제

```bash
rm src/main/java/com/university/scms/controller/MainViewController.java
```

### 5. 서버 테스트

```bash
./gradlew bootRun
```

**검증 URL**:
- http://localhost:8080/
- http://localhost:8080/programs
- http://localhost:8080/program/history
- http://localhost:8080/mileage

---

## URL 경로 통일 이슈

### 현재 상태

프로그램 관련 URL이 두 가지 스타일로 혼재:
```
/programs         ← 복수형
/program/apply    ← 단수형
/program/history  ← 단수형
```

### 통일 필요성

**Option 1: 복수형으로 통일**
```
/programs              (현재)
/programs/apply        (변경 필요)
/programs/history      (변경 필요)
```

**Option 2: 단수형으로 통일**
```
/program              (변경 필요)
/program/apply        (현재)
/program/history      (현재)
```

### 권장사항

**복수형 사용 권장** (RESTful 관례)
```
GET    /programs              목록
GET    /programs/{id}         상세
POST   /programs              생성
PUT    /programs/{id}         수정
DELETE /programs/{id}         삭제
```

**Phase 4 진행 시 통일 예정**

---

## 향후 작업

### Phase 3 (View Layer 완성)

나머지 도메인 Controller 추가:
```
controller/view/
├── CounselingViewController.java
├── CompetencyViewController.java
└── CareerViewController.java
```

### Phase 4 (Service Layer)

- Service 계층 구현
- Controller에서 Service 호출
- URL 경로 통일 리팩토링

### Phase 5 (API Layer)

- REST API Controller 추가
- View/API 완전 분리
- API 문서 생성 (Swagger)

### 장기 (MSA 전환)

- Domain 기반 구조로 전환
- 독립 서비스 분리
- API Gateway 구성

---

## 참고사항

### Controller 네이밍 규칙

```java
// View Controller
{Domain}ViewController.java
예: ProgramViewController, MileageViewController

// API Controller (Phase 5)
{Domain}ApiController.java
예: ProgramApiController, MileageApiController
```

### Package 구조

```
controller/
├── view/           ← @Controller, return "template"
└── api/            ← @RestController, return ResponseEntity
```

### Import 주의사항

```java
// View Controller
import org.springframework.stereotype.Controller;

// API Controller (Phase 5)
import org.springframework.web.bind.annotation.RestController;
```

---

## 결론

Controller 리팩토링을 통해 다음을 달성했습니다:

1. **책임 분리**: 도메인별 Controller 분리
2. **유지보수성**: 코드 찾기 쉬워짐
3. **팀 협업**: 충돌 최소화
4. **확장성**: 새 도메인 추가 용이
5. **MSA 준비**: 점진적 전환 가능

이 구조는 Phase 5 API 추가와 향후 MSA 전환을 대비한 확장 가능한 설계입니다.

---

**작성일**: 2025-11-04  
**작성자**: AI Assistant (Claude)  
**최종 수정**: 2025-11-04
