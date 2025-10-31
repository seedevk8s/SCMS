# 학생 역량 관리 시스템 (SCMS - Student Competency Management System)

대학 비교과 프로그램, 역량 개발, 진로 상담을 통합 관리하는 웹 플랫폼

## 🚀 기술 스택

### Backend
- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** - JWT 기반 인증/인가
- **Spring Data JPA** - ORM
- **MySQL** - 데이터베이스
- **Gradle** - 빌드 도구

## 📁 프로젝트 구조

```
scms/
├── src/
│   ├── main/
│   │   ├── java/com/university/scms/
│   │   │   ├── config/          # 설정 클래스
│   │   │   ├── controller/      # REST API 컨트롤러
│   │   │   ├── domain/
│   │   │   │   ├── entity/      # JPA 엔티티
│   │   │   │   └── repository/  # JPA 리포지토리
│   │   │   ├── dto/             # 데이터 전송 객체
│   │   │   ├── service/         # 비즈니스 로직
│   │   │   ├── security/        # 보안 관련 (JWT, Filter 등)
│   │   │   ├── util/            # 유틸리티
│   │   │   └── exception/       # 예외 처리
│   │   └── resources/
│   │       └── application.yml  # 애플리케이션 설정
│   └── test/                    # 테스트 코드
└── build.gradle                 # Gradle 설정
```

## 🔧 개발 환경 설정

### 1. 필수 요구사항
- JDK 17 이상
- MySQL 8.0 이상
- IntelliJ IDEA (권장)

### 2. 데이터베이스 설정

MySQL에 데이터베이스 생성:
```sql
CREATE DATABASE scms_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. application.yml 설정

`src/main/resources/application.yml` 파일에서 DB 연결 정보 수정:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/scms_db
    username: your_username
    password: your_password
```

### 4. 프로젝트 실행

```bash
# Gradle을 사용한 빌드
./gradlew build

# 애플리케이션 실행
./gradlew bootRun
```

또는 IntelliJ IDEA에서 `ScmsApplication` 클래스의 main 메서드를 직접 실행

## 📋 주요 기능 모듈

### Phase 1: 기반 구축
- [x] 프로젝트 초기 설정
- [ ] 데이터베이스 설계 (ERD)
- [ ] JWT 인증/인가 시스템

### Phase 2: 핵심 기능
- [ ] 사용자 관리 (마이페이지)
- [ ] 비교과 프로그램 관리
- [ ] 마일리지 시스템

### Phase 3: 부가 기능
- [ ] 역량 진단 시스템
- [ ] 상담 시스템
- [ ] 관리자 대시보드

### Phase 4: 마무리
- [ ] 테스트 코드 작성
- [ ] API 문서화
- [ ] 배포 준비

## 🔐 보안

- JWT(JSON Web Token) 기반 인증
- Spring Security를 통한 권한 관리
- 역할 기반 접근 제어 (RBAC)
  - STUDENT: 학생
  - STAFF: 교직원
  - ADMIN: 관리자

## 📌 API 엔드포인트

### 인증 API
- `POST /api/auth/login` - 로그인
- `POST /api/auth/register` - 회원가입
- `POST /api/auth/refresh` - 토큰 갱신

### 사용자 API
- `GET /api/users/me` - 내 정보 조회
- `PUT /api/users/me` - 내 정보 수정
- `PUT /api/users/me/password` - 비밀번호 변경

*(개발 진행에 따라 추가 예정)*

## 👥 개발자

- 개발 기간: 4주
- 방법론: 애자일 스크럼

## 📝 라이선스

This project is for educational purposes.
