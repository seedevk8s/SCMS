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
│   │   │   ├── config/              # 설정 클래스
│   │   │   ├── controller/          # REST API 컨트롤러
│   │   │   ├── domain/
│   │   │   │   ├── common/          # BaseEntity 등 공통 클래스
│   │   │   │   ├── auth/           
│   │   │   │   │   ├── entity/      # User, UserRole
│   │   │   │   │   └── repository/
│   │   │   │   ├── program/        
│   │   │   │   │   ├── entity/      # Program, Application, Participant
│   │   │   │   │   └── repository/
│   │   │   │   ├── mileage/        
│   │   │   │   │   ├── entity/      # MileageAccount, Transaction, Certification
│   │   │   │   │   └── repository/
│   │   │   │   ├── competency/     
│   │   │   │   │   ├── entity/      # Survey, Question, Response, Result
│   │   │   │   │   └── repository/
│   │   │   │   ├── career/         
│   │   │   │   │   ├── entity/      # CareerPlan, Goal, Milestone
│   │   │   │   │   └── repository/
│   │   │   │   ├── counseling/     
│   │   │   │   │   ├── entity/      # Reservation, Session, Availability
│   │   │   │   │   └── repository/
│   │   │   │   ├── file/           
│   │   │   │   │   ├── entity/      # FileMetadata
│   │   │   │   │   └── repository/
│   │   │   │   ├── notification/   
│   │   │   │   │   ├── entity/      # Notification, NotificationTemplate
│   │   │   │   │   └── repository/
│   │   │   │   └── system/         
│   │   │   │       ├── entity/      # SystemLog, AuditLog
│   │   │   │       └── repository/
│   │   │   ├── dto/                 # 데이터 전송 객체
│   │   │   ├── service/             # 비즈니스 로직
│   │   │   ├── security/            # 보안 관련 (JWT, Filter 등)
│   │   │   ├── util/                # 유틸리티
│   │   │   └── exception/           # 예외 처리
│   │   └── resources/
│   │       └── application.yml      # 애플리케이션 설정
│   └── test/                        # 테스트 코드
├── docs/                            # 프로젝트 문서
│   ├── 01-progress/                 # 진행 상황
│   ├── 02-design/                   # 설계 문서
│   └── 03-workflow/                 # 작업 흐름
└── build.gradle                     # Gradle 설정
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

## 📊 Entity 구현 현황 (100% 완료) ✅

### Domain별 Entity 구성

| Domain | Entity 수 | 주요 Entity | 상태 |
|--------|-----------|-------------|------|
| **Common** | 2 | BaseEntity, CommonCode | ✅ |
| **Auth** | 2 | User, UserRole | ✅ |
| **Program** | 7 | Program, Application, Participant, Category | ✅ |
| **Mileage** | 5 | Account, Transaction, Certification | ✅ |
| **Competency** | 6 | Survey, Question, Response, Result | ✅ |
| **Career** | 3 | CareerPlan, Goal, Milestone | ✅ |
| **Counseling** | 5 | Reservation, Session, Availability, Counselor | ✅ |
| **File** | 1 | FileMetadata | ✅ |
| **Notification** | 2 | Notification, Template | ✅ |
| **System** | 2 | SystemLog, AuditLog | ✅ |
| **Total** | **26** | | **✅ 완료** |

### MSA 준비 아키텍처
- 도메인별 패키지 분리
- ID 기반 참조 (외래키 제약조건 없음)
- 동일 도메인 내에서만 JPA 관계 매핑
- Rich Domain Model 패턴 적용

## 📋 주요 기능 모듈

### Phase 1: 기반 구축 ✅
- [x] 프로젝트 초기 설정
- [x] 데이터베이스 설계 (ERD)
- [x] Entity 레이어 구현 (23개 Entity 완료)
- [ ] JWT 인증/인가 시스템

### Phase 2: Repository & Service 레이어
- [ ] Repository 인터페이스 구현
- [ ] Service 비즈니스 로직 구현
- [ ] DTO 클래스 작성

### Phase 3: Controller & API
- [ ] REST API Controller 구현
- [ ] 예외 처리 및 Validation
- [ ] API 문서화 (Swagger)

### Phase 4: 핵심 기능 구현
- [ ] 사용자 관리 (마이페이지)
- [ ] 비교과 프로그램 관리
- [ ] 마일리지 시스템
- [ ] 역량 진단 시스템
- [ ] 상담 시스템
- [ ] 진로 관리 시스템

### Phase 5: 마무리
- [ ] 테스트 코드 작성
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
