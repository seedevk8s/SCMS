# Phase 1: 기반 구축 - 진행 상황

## 📋 프로젝트 개요

### 프로젝트명
**SCMS (Student Competency Management System)**  
학생 역량 관리 시스템

### 목적
대학 비교과 프로그램, 역량 개발, 진로 상담을 통합 관리하는 웹 플랫폼

### 개발 기간
총 4주 (기획 1주 + 개발 2주 + 테스트 1주)

### 개발 방법론
애자일 스크럼

---

## 🏗️ 아키텍처 전략

### 현재: Modular Monolith (모듈러 모노리스)
- 단일 애플리케이션으로 개발
- 하나의 데이터베이스 사용
- 빠른 개발 및 배포

### 미래: MSA 전환 가능
- 도메인별 명확한 경계 설정
- 느슨한 결합 유지
- API 중심 설계
- 필요 시 선택적 서비스 분리

### 도메인 구분
1. **Auth Service** - 인증/인가
2. **Program Service** - 비교과 프로그램
3. **Mileage Service** - 마일리지 관리
4. **Competency Service** - 역량 진단
5. **Counseling Service** - 상담 시스템
6. **User Service** - 사용자 관리

---

## 💻 기술 스택

### Backend
- **Java 17**
- **Spring Boot 3.5.7**
- **Spring Security** - JWT 기반 인증
- **Spring Data JPA** - ORM
- **Gradle 8.5** - 빌드 도구

### Database
- **MySQL 8.0** - 메인 데이터베이스

### 보안
- **JWT (JSON Web Token)** - 토큰 기반 인증
- **BCrypt** - 비밀번호 암호화

### 개발 도구
- **IntelliJ IDEA** - IDE
- **Lombok** - 보일러플레이트 코드 감소
- **Spring Boot DevTools** - 핫 리로드

---

## ✅ 완료된 작업

### 1. 프로젝트 초기 설정 ✅
- [x] Spring Boot 3.5.7 프로젝트 생성
- [x] Gradle 빌드 설정 완료
- [x] 모든 필수 의존성 추가
  - Spring Web
  - Spring Data JPA
  - Spring Security
  - JWT (jjwt 0.12.3)
  - MySQL Connector
  - Lombok
  - DevTools
  - Validation

### 2. 프로젝트 구조 설정 ✅
```
src/main/java/com/university/scms/
├── ScmsApplication.java         # 메인 애플리케이션
├── config/
│   ├── JpaConfig.java           # JPA Auditing 설정
│   └── SecurityConfig.java      # Spring Security 설정
├── controller/
│   └── HealthCheckController.java  # 헬스체크 API
├── domain/
│   ├── entity/                  # JPA 엔티티
│   └── repository/              # 리포지토리
├── dto/                         # DTO (작업 예정)
├── service/                     # 비즈니스 로직 (작업 예정)
├── security/                    # JWT 관련 (작업 예정)
├── util/                        # 유틸리티 (작업 예정)
└── exception/
    └── GlobalExceptionHandler.java  # 전역 예외 처리
```

### 3. 데이터베이스 설정 ✅
- [x] MySQL 연결 설정
- [x] scms_db 데이터베이스 생성
- [x] JPA Hibernate ddl-auto: update 설정
- [x] 연결 테스트 완료

### 4. Spring Security 기본 설정 ✅
- [x] SecurityConfig 클래스 생성
- [x] JWT 기반 Stateless 세션 설정
- [x] CSRF 비활성화
- [x] 공개 엔드포인트 설정
  - `/api/health` - 헬스체크
  - `/api/auth/**` - 인증 API
- [x] PasswordEncoder(BCrypt) Bean 등록

### 5. 헬스체크 API 구현 ✅
- [x] HealthCheckController 생성
- [x] GET `/api/health` 엔드포인트
- [x] 응답 포맷:
```json
{
  "status": "UP",
  "timestamp": "2025-10-31T...",
  "message": "SCMS API is running"
}
```
- [x] 인증 없이 접근 가능하도록 설정
- [x] 테스트 완료

### 6. MSA 전환 가이드 문서 작성 ✅
- [x] MSA_ARCHITECTURE_GUIDE.md 생성
- [x] 도메인별 분리 전략 문서화
- [x] 설계 원칙 정의
- [x] 개발 체크리스트 작성
- [x] API 엔드포인트 설계 가이드

### 7. 개발 로드맵 시각화 ✅
- [x] SVG 형식 로드맵 생성
- [x] 4개 Phase 구조화
- [x] 각 단계별 작업 항목 정리

---

## 📁 주요 파일 목록

### 설정 파일
```
build.gradle                     # Gradle 빌드 설정
settings.gradle                  # 프로젝트 설정
src/main/resources/application.yml  # 애플리케이션 설정
```

### Java 소스 파일
```
ScmsApplication.java             # 메인 애플리케이션
config/JpaConfig.java            # JPA 설정
config/SecurityConfig.java       # 보안 설정
controller/HealthCheckController.java  # 헬스체크
exception/GlobalExceptionHandler.java  # 예외 처리
```

### 문서 파일
```
README.md                        # 프로젝트 소개
PROJECT_SETUP_GUIDE.md           # 초기 설정 가이드
MSA_ARCHITECTURE_GUIDE.md        # MSA 전환 가이드
scms_development_roadmap.svg     # 개발 로드맵
docs/
├── 00-SESSION_RESUME.md         # 세션 재개 가이드
└── 01-progress/
    └── 01-phase1-foundation.md  # 이 문서
```

---

## 🎯 설계 원칙

### 1. 도메인 주도 설계 (DDD)
- 도메인별 명확한 경계
- 각 도메인이 자신의 데이터와 로직 소유

### 2. 느슨한 결합 (Loose Coupling)
- Entity 간 직접 참조 최소화
- ID 참조 방식 사용
- 인터페이스 기반 통신

### 3. API 중심 설계
- RESTful API 원칙 준수
- 명확한 엔드포인트 네이밍
- 도메인별 경로 구분: `/api/{domain}/**`

### 4. 보안 우선
- JWT 기반 무상태(Stateless) 인증
- 역할 기반 접근 제어 (RBAC)
- 비밀번호 암호화

### 5. 독립적 배포 가능성
- 각 도메인이 독립적으로 실행 가능하도록 구조화
- 공통 모듈 최소화

---

## 🔐 보안 설정

### 인증 방식
- JWT (JSON Web Token) 기반
- Access Token 유효기간: 24시간

### 권한 구분
- **STUDENT** - 학생
- **STAFF** - 교직원
- **ADMIN** - 관리자

### 공개 엔드포인트
- `/api/health` - 헬스체크
- `/api/auth/**` - 로그인/회원가입

---

## 📊 API 설계 가이드

### 엔드포인트 네이밍 규칙
```
/api/{domain}/{resource}
/api/{domain}/{resource}/{id}
/api/{domain}/{resource}/{id}/{action}
```

### 예정된 API 엔드포인트

#### 인증 & 사용자
```
POST   /api/auth/login           # 로그인
POST   /api/auth/register        # 회원가입
POST   /api/auth/refresh         # 토큰 갱신
GET    /api/users/me             # 내 정보 조회
PUT    /api/users/me             # 내 정보 수정
PUT    /api/users/me/password    # 비밀번호 변경
```

#### 비교과 프로그램
```
GET    /api/programs             # 프로그램 목록
POST   /api/programs             # 프로그램 생성 (교직원)
GET    /api/programs/{id}        # 프로그램 상세
PUT    /api/programs/{id}        # 프로그램 수정
DELETE /api/programs/{id}        # 프로그램 삭제
POST   /api/programs/{id}/apply  # 프로그램 신청
DELETE /api/programs/{id}/apply  # 신청 취소
PUT    /api/programs/{id}/confirm # 참여 확인
```

#### 마일리지
```
GET    /api/mileage              # 내 마일리지 조회
GET    /api/mileage/history      # 마일리지 내역
POST   /api/mileage/certify      # 역량 인증
GET    /api/mileage/statistics   # 통계
```

#### 역량 진단
```
GET    /api/competency/surveys           # 설문 목록
GET    /api/competency/surveys/{id}      # 설문 상세
POST   /api/competency/surveys/{id}/submit  # 설문 제출
GET    /api/competency/results           # 내 진단 결과
GET    /api/competency/results/{id}      # 특정 결과 상세
```

#### 상담
```
GET    /api/counseling           # 상담 목록
POST   /api/counseling           # 상담 신청
GET    /api/counseling/{id}      # 상담 상세
PUT    /api/counseling/{id}      # 상담 승인/거절
DELETE /api/counseling/{id}      # 상담 취소
```

---

## 🗄️ 데이터베이스 설정

### 연결 정보
```yaml
Database: scms_db
Host: localhost:3306
Charset: utf8mb4
Collation: utf8mb4_unicode_ci
```

### JPA 설정
```yaml
hibernate.ddl-auto: update       # 자동 테이블 생성/수정
show-sql: true                   # SQL 로깅
format_sql: true                 # SQL 포맷팅
```

---

## 🧪 테스트 현황

### 완료된 테스트
- ✅ 애플리케이션 정상 실행
- ✅ MySQL 연결 확인
- ✅ 헬스체크 API 동작 확인
- ✅ Spring Security 공개 엔드포인트 접근 확인

### 테스트 URL
```
http://localhost:8080/api/health
```

### 예상 응답
```json
{
  "status": "UP",
  "timestamp": "2025-10-31T11:05:12.789",
  "message": "SCMS API is running"
}
```

---

## 📝 개발 표준

### 코드 스타일
- Java 17 문법 사용
- Lombok 적극 활용
- 명확한 네이밍 컨벤션

### 패키지 네이밍
- 도메인별 패키지 분리 (향후)
- 계층별 명확한 구분

### 주석
- JavaDoc 스타일
- 클래스/메서드 목적 명시
- 복잡한 로직은 인라인 주석

---

## 🔄 다음 단계 (Phase 1 계속)

### 즉시 진행: ERD 설계
- [ ] 전체 데이터베이스 구조 설계
- [ ] 도메인별 테이블 정의
- [ ] 관계(Relationship) 정의
- [ ] ERD 다이어그램 작성

### 이후 작업: Entity 클래스 작성
- [ ] User 엔티티 개선
- [ ] Program 엔티티
- [ ] Application 엔티티
- [ ] Mileage 엔티티
- [ ] Competency 엔티티
- [ ] Counseling 엔티티
- [ ] 연관관계 설정

### JWT 인증 시스템 구현
- [ ] JwtTokenProvider 유틸리티
- [ ] JwtAuthenticationFilter
- [ ] UserDetails 구현
- [ ] 로그인 API
- [ ] 회원가입 API

---

## 🎓 학습 포인트

### 완료된 학습
- ✅ Spring Boot 3.x 프로젝트 구조
- ✅ Gradle 빌드 설정
- ✅ Spring Security 기본 설정
- ✅ JWT 개념 및 필요성
- ✅ MSA vs Monolith 아키텍처
- ✅ Modular Monolith 패턴
- ✅ 도메인 주도 설계 기초

### 앞으로 학습할 내용
- ERD 설계 방법론
- JPA 연관관계 매핑
- JWT 실전 구현
- REST API 설계 베스트 프랙티스
- 트랜잭션 관리

---

## 📊 프로젝트 메트릭

### 코드 통계
- Java 파일: 6개
- 설정 파일: 3개
- 문서 파일: 6개

### 패키지 구조
- config: 2개
- controller: 1개
- exception: 1개
- domain: 0개 (작업 예정)
- service: 0개 (작업 예정)

---

## 💡 주요 의사결정

### 1. Spring Boot 버전
- **결정**: 3.5.7 사용
- **이유**: 최신 기능 활용, 학습 목적

### 2. context-path 제거
- **결정**: context-path 사용 안 함
- **이유**: MSA 전환 용이성, 명확한 API 설계

### 3. 아키텍처 패턴
- **결정**: Modular Monolith
- **이유**: 빠른 개발, MSA 전환 가능성 유지

### 4. 인증 방식
- **결정**: JWT 기반 Stateless
- **이유**: MSA 호환성, 확장성

---

## 📚 참고 자료

### 공식 문서
- Spring Boot: https://spring.io/projects/spring-boot
- Spring Security: https://spring.io/projects/spring-security
- Spring Data JPA: https://spring.io/projects/spring-data-jpa

### 아키텍처
- MSA Pattern: https://microservices.io/patterns/
- Domain-Driven Design

### 도구
- Lombok: https://projectlombok.org/
- JWT: https://jwt.io/

---

**작성일**: 2025-10-31  
**작성자**: Claude AI  
**최종 수정**: 2025-10-31
