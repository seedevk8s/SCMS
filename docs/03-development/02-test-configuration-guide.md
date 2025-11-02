# Spring Boot 테스트 설정 가이드

## 📅 작성일
**2025-11-02**

---

## 📚 목차
1. [개요](#개요)
2. [@ActiveProfiles와 설정 파일 매칭](#activeprofiles와-설정-파일-매칭)
3. [프로파일 네이밍 규칙](#프로파일-네이밍-규칙)
4. [현재 프로젝트 설정](#현재-프로젝트-설정)
5. [권장 설정](#권장-설정)
6. [테스트 실행 시 확인 방법](#테스트-실행-시-확인-방법)

---

## 개요

Spring Boot 테스트에서 `@ActiveProfiles` 어노테이션과 `application-test.yml` 설정 파일의 관계를 이해하는 것은 매우 중요합니다. 이 문서는 두 요소가 어떻게 연동되는지 자세히 설명합니다.

---

## @ActiveProfiles와 설정 파일 매칭

### 기본 원리

```java
@ActiveProfiles("test")
```

이 어노테이션은 Spring에게 **"test"라는 이름의 프로파일을 활성화하라**고 지시합니다.

Spring Boot는 다음 순서로 설정 파일을 찾습니다:

1. `application-{profile}.yml` 또는 `application-{profile}.properties` 파일 검색
2. `{profile}` = `@ActiveProfiles`에 지정된 값
3. 해당 프로파일 설정 파일을 로드하고 기본 `application.yml`보다 우선 적용

### 파일 위치와 네이밍

```
src/
├── main/
│   └── resources/
│       └── application.yml              ← 기본 설정
└── test/
    └── resources/
        └── application-test.yml         ← "test" 프로파일용 설정
```

### 프로파일 활성화 과정

```java
@DataJpaTest
@ActiveProfiles("test")  // ← "test" 프로파일 활성화
@Import(JpaConfig.class)
class ProgramRepositoryTest {
    // 테스트 코드...
}
```

**동작 순서:**

1. `@ActiveProfiles("test")` → Spring에게 "test" 프로파일 사용 지시
2. Spring Boot가 `src/test/resources/` 디렉토리에서 `application-test.yml` 검색
3. `application-test.yml` 파일의 설정을 로드
4. 기본 `application.yml` 설정 위에 `application-test.yml` 설정 오버라이드

---

## 프로파일 네이밍 규칙

### 매칭 규칙 표

| 프로파일 이름 | 설정 파일 이름 | 어노테이션 사용 예 |
|------------|-------------|----------------|
| `test` | `application-test.yml` | `@ActiveProfiles("test")` |
| `dev` | `application-dev.yml` | `@ActiveProfiles("dev")` |
| `prod` | `application-prod.yml` | `@ActiveProfiles("prod")` |
| `local` | `application-local.yml` | `@ActiveProfiles("local")` |

### 핵심 규칙

⚠️ **중요:** `@ActiveProfiles("프로파일명")`의 프로파일명과 `application-{프로파일명}.yml` 파일명이 **정확히 일치**해야 합니다!

**올바른 예:**
```java
@ActiveProfiles("test")  →  application-test.yml
@ActiveProfiles("dev")   →  application-dev.yml
```

**잘못된 예:**
```java
@ActiveProfiles("test")  →  application-testing.yml  ❌ (불일치)
@ActiveProfiles("dev")   →  application-development.yml  ❌ (불일치)
```

---

## 현재 프로젝트 설정

### 파일 위치
```
src/test/resources/application-test.yml
```

### 현재 설정 내용

```yaml
spring:
  application:
    name: scms-test
  
  datasource:
    url: jdbc:mysql://localhost:3306/scms_db?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
    username: root
    password: 12345
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: update  # 테스트 시 테이블 자동 생성/업데이트
    show-sql: true      # SQL 쿼리 로깅
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQLDialect

logging:
  level:
    com.university.scms: DEBUG
    org.springframework.test: INFO
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

### 현재 설정 분석

| 항목 | 설정값 | 설명 |
|-----|-------|------|
| 데이터베이스 | `scms_db` | ⚠️ 프로덕션 DB와 동일 |
| ddl-auto | `update` | 테스트 시 테이블 자동 생성/업데이트 |
| show-sql | `true` | SQL 쿼리 콘솔 출력 |
| format_sql | `true` | SQL 쿼리 포맷팅 |
| 로그 레벨 | `DEBUG` | 상세 로그 출력 |

---

## 권장 설정

### Option 1: 별도 테스트 DB 사용 (강력 권장)

프로덕션 데이터 보호를 위해 **별도 테스트 전용 DB 사용을 권장**합니다.

#### 1. 테스트 DB 생성

```sql
CREATE DATABASE scms_test_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 권한 부여 (필요시)
GRANT ALL PRIVILEGES ON scms_test_db.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

#### 2. application-test.yml 수정

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/scms_test_db?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
    username: root
    password: 12345
```

#### 장점
- ✅ 프로덕션 데이터 안전 보장
- ✅ 테스트 중 데이터 오염 방지
- ✅ 테스트 실패 시 롤백 없이 DB 초기화 가능

### Option 2: 현재 설정 유지

현재 `scms_db`를 그대로 사용해도 안전합니다:

- `@DataJpaTest`는 기본적으로 **트랜잭션 롤백** 제공
- 각 테스트 후 `@AfterEach`에서 명시적 데이터 정리
- 실제 프로덕션 데이터에는 영향 없음

#### 안전장치

```java
@AfterEach
void tearDown() {
    programRepository.deleteAll();  // 테스트 데이터 정리
}
```

### Option 3: H2 인메모리 DB 사용

빠른 테스트를 위해 H2 인메모리 DB 사용도 고려할 수 있습니다.

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
```

#### 장점
- ✅ 매우 빠른 테스트 속도
- ✅ 별도 DB 설치 불필요
- ✅ 테스트마다 깨끗한 DB 환경

#### 단점
- ⚠️ MySQL과 완전히 동일하지 않음 (방언 차이)
- ⚠️ MySQL 특화 기능 테스트 불가

---

## 테스트 실행 시 확인 방법

### 1. 프로파일 활성화 확인

테스트 실행 시 콘솔에서 다음 로그를 확인하세요:

```
The following 1 profile is active: "test"
```

이 메시지가 표시되면 `test` 프로파일이 정상적으로 활성화된 것입니다.

### 2. 데이터베이스 연결 확인

```
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
HikariPool-1 - Added connection com.mysql.cj.jdbc.ConnectionImpl@xxxxx
```

이 로그로 `application-test.yml`에 설정된 DB에 정상 연결되었는지 확인할 수 있습니다.

### 3. SQL 쿼리 로깅 확인

`show-sql: true` 설정으로 인해 실행되는 모든 SQL이 콘솔에 출력됩니다:

```sql
Hibernate: 
    insert 
    into
        programs
        (created_at, updated_at, application_end, ...)
    values
        (?, ?, ?, ...)
```

### 4. 잘못된 설정 시 증상

만약 프로파일이 제대로 매칭되지 않으면:

```
No active profile set, falling back to 1 default profile: "default"
```

이 경우 다음을 확인하세요:
1. `@ActiveProfiles("test")` 어노테이션 존재 여부
2. `src/test/resources/application-test.yml` 파일 존재 여부
3. 파일명 철자 확인 (대소문자, 하이픈 등)

---

## 테스트 클래스 템플릿

### 완전한 테스트 클래스 예제

```java
package com.university.scms.domain.program.repository;

import com.university.scms.config.JpaConfig;
import com.university.scms.domain.program.entity.Program;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository 테스트 클래스
 */
@DataJpaTest                                    // ① JPA 테스트 활성화
@AutoConfigureTestDatabase(replace = Replace.NONE)  // ② 실제 DB 사용
@ActiveProfiles("test")                        // ③ test 프로파일 활성화
@Import(JpaConfig.class)                       // ④ JPA Auditing 설정 임포트
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)  // ⑤ 테스트 순서 지정
class ProgramRepositoryTest {

    @Autowired
    private ProgramRepository programRepository;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
    }

    @AfterEach
    void tearDown() {
        programRepository.deleteAll();  // ⑥ 데이터 정리
    }

    @Test
    @Order(1)
    @DisplayName("프로그램 저장")
    void saveProgram() {
        // 테스트 코드...
    }
}
```

### 각 어노테이션 설명

| 어노테이션 | 역할 |
|----------|------|
| `@DataJpaTest` | JPA Repository 테스트 환경 구성 |
| `@AutoConfigureTestDatabase(replace = Replace.NONE)` | 내장 DB 대신 실제 DB 사용 |
| `@ActiveProfiles("test")` | **test 프로파일 활성화** ← 핵심! |
| `@Import(JpaConfig.class)` | JPA Auditing 설정 임포트 |
| `@TestMethodOrder` | 테스트 실행 순서 지정 |

---

## 문제 해결 (Troubleshooting)

### 문제 1: 프로파일이 활성화되지 않음

**증상:**
```
No active profile set, falling back to 1 default profile: "default"
```

**해결책:**
1. `@ActiveProfiles("test")` 어노테이션 추가 확인
2. 테스트 클래스 위에 어노테이션이 있는지 확인

### 문제 2: 설정 파일을 찾을 수 없음

**증상:**
```
Cannot load configuration class
```

**해결책:**
1. `src/test/resources/application-test.yml` 파일 존재 확인
2. 파일명 철자 확인 (하이픈, 확장자 등)
3. 파일이 `src/test/resources`에 있는지 확인

### 문제 3: DB 연결 실패

**증상:**
```
Communications link failure
```

**해결책:**
1. MySQL 서버 실행 확인: `netstat -ano | findstr :3306`
2. `application-test.yml`의 DB 연결 정보 확인
3. 테스트 DB가 생성되어 있는지 확인

### 문제 4: JPA Auditing 미작동

**증상:**
```
createdAt, updatedAt 필드가 null
```

**해결책:**
```java
@Import(JpaConfig.class)  // ← 이 어노테이션 추가 필수!
class YourRepositoryTest {
    // ...
}
```

---

## 체크리스트

테스트 실행 전 다음 항목들을 확인하세요:

- [ ] `@ActiveProfiles("test")` 어노테이션 추가
- [ ] `src/test/resources/application-test.yml` 파일 존재
- [ ] MySQL 서버 실행 중
- [ ] 테스트 DB 생성 (별도 DB 사용 시)
- [ ] `@Import(JpaConfig.class)` 추가
- [ ] `@AfterEach`에 데이터 정리 코드 작성

---

## 참고 자료

- [Spring Boot Testing Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [Spring Profiles Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.profiles)
- [DataJpaTest Annotation](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/test/autoconfigure/orm/jpa/DataJpaTest.html)

---

**작성일:** 2025-11-02  
**최종 수정일:** 2025-11-02  
**작성자:** AI Assistant
