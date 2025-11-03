# SCMS 남은 개발 로드맵

## 📊 전체 진행 현황

**업데이트 일시**: 2025-11-03 (일) 18:00

```
전체 진행률: 30%

✅ 완료: Entity Layer (100%), Repository Layer (100%)
🔄 진행 예정: Service Layer, Controller Layer, Security, 통합 테스트
```

---

## 🎯 현재까지 완료된 작업

### ✅ Phase 1: 데이터 모델링 (100%)
- [x] ERD 설계 완료 (30개 테이블)
- [x] Entity 클래스 구현 (30개)
- [x] 연관관계 매핑 완료
- [x] MSA 대비 설계 적용

### ✅ Phase 2: Repository Layer (100%)
- [x] 18개 Repository 구현
- [x] 523개 테스트 작성 및 통과 (99.8%)
- [x] 쿼리 메서드 구현
- [x] 통계 쿼리 구현

---

## 🚀 남은 개발 작업

### 📋 전체 개요

| Phase | 작업 내용 | 예상 소요 | 우선순위 |
|-------|----------|----------|---------|
| **Phase 3** | Service Layer | 2-3주 | ⭐⭐⭐ 높음 |
| **Phase 4** | Controller Layer (REST API) | 2-3주 | ⭐⭐⭐ 높음 |
| **Phase 5** | Spring Security 통합 | 1주 | ⭐⭐⭐ 높음 |
| **Phase 6** | DTO & Validation | 1주 | ⭐⭐ 중간 |
| **Phase 7** | 예외 처리 & 로깅 | 1주 | ⭐⭐ 중간 |
| **Phase 8** | 통합 테스트 | 1-2주 | ⭐⭐ 중간 |
| **Phase 9** | API 문서화 | 3일 | ⭐ 낮음 |
| **Phase 10** | 배포 준비 | 1주 | ⭐ 낮음 |

**총 예상 소요 기간**: 약 8-12주

---

## 📝 Phase 3: Service Layer 구현

### 🎯 목표
비즈니스 로직을 구현하고 트랜잭션을 관리하는 서비스 계층 구축

### 📦 구현할 Service (18개)

#### Auth Domain (1개)
- [ ] **UserService**
  - 사용자 등록/조회/수정/삭제
  - 비밀번호 변경
  - 사용자 권한 관리
  - 학생/교직원 정보 관리

#### Common Domain (1개)
- [ ] **CommonCodeService**
  - 공통 코드 관리
  - 코드 그룹 관리
  - 캐시 관리

#### Program Domain (6개)
- [ ] **ProgramService**
  - 프로그램 CRUD
  - 프로그램 검색
  - 프로그램 상태 관리
- [ ] **ProgramCompetencyService**
  - 역량 매핑 관리
- [ ] **ProgramApplicationService**
  - 프로그램 신청 처리
  - 신청 승인/거부
  - 신청 취소
- [ ] **ProgramParticipantService**
  - 참가자 관리
  - 출석 체크
  - 이수 처리
- [ ] **ProgramSatisfactionService**
  - 만족도 조사 관리
  - 통계 생성
- [ ] **ProgramCategoryService**
  - 카테고리 관리
  - 계층 구조 관리

#### Mileage Domain (2개)
- [ ] **MileageTransactionService**
  - 마일리지 적립/차감
  - 거래 내역 조회
  - 잔액 계산
- [ ] **MileageRuleService**
  - 마일리지 규칙 관리
  - 자동 적립 로직

#### Competency Domain (3개)
- [ ] **CompetencySurveyService**
  - 설문 CRUD
  - 설문 배포
- [ ] **SurveyQuestionService**
  - 문항 관리
- [ ] **CompetencyResultService**
  - 역량 평가 처리
  - 결과 분석
  - 통계 생성

#### Counseling Domain (3개)
- [ ] **CounselingReservationService**
  - 예약 생성/조회/취소
  - 예약 확정
  - 알림 발송
- [ ] **CounselingSessionService**
  - 세션 기록 관리
  - 상담 내역 조회
- [ ] **CounselorAvailabilityService**
  - 상담사 일정 관리
  - 가용 시간 조회

#### Career Domain (2개)
- [ ] **CareerPlanService**
  - 진로 계획 CRUD
  - 계획 완료율 계산
- [ ] **CareerGoalService**
  - 목표 관리
  - 순서 변경
  - 완료 처리

### 🛠️ 주요 작업 내용

1. **비즈니스 로직 구현**
   - 도메인 규칙 검증
   - 복잡한 비즈니스 프로세스
   - 상태 전이 관리

2. **트랜잭션 관리**
   - @Transactional 적용
   - 격리 수준 설정
   - 롤백 조건 정의

3. **DTO 변환**
   - Entity → DTO 매핑
   - DTO → Entity 매핑
   - ModelMapper 활용

4. **예외 처리**
   - Custom Exception 정의
   - 비즈니스 예외 처리
   - 에러 메시지 관리

5. **테스트 작성**
   - Unit Test (Mockito)
   - 비즈니스 로직 검증
   - 예외 케이스 테스트

### 📊 예상 산출물
- Service 인터페이스: 18개
- Service 구현체: 18개
- Service 테스트: 약 300개
- Custom Exception: 약 20개

---

## 📝 Phase 4: Controller Layer (REST API)

### 🎯 목표
RESTful API 엔드포인트 구현 및 HTTP 통신 처리

### 📦 구현할 Controller (18개)

#### Auth API
- [ ] **AuthController** (`/api/auth`)
  - `POST /register` - 회원가입
  - `POST /login` - 로그인
  - `POST /logout` - 로그아웃
  - `GET /me` - 내 정보 조회
  - `PUT /password` - 비밀번호 변경

- [ ] **UserController** (`/api/users`)
  - `GET /users` - 사용자 목록
  - `GET /users/{id}` - 사용자 상세
  - `PUT /users/{id}` - 사용자 수정
  - `DELETE /users/{id}` - 사용자 삭제

#### Program API
- [ ] **ProgramController** (`/api/programs`)
  - `POST /programs` - 프로그램 생성
  - `GET /programs` - 프로그램 목록
  - `GET /programs/{id}` - 프로그램 상세
  - `PUT /programs/{id}` - 프로그램 수정
  - `DELETE /programs/{id}` - 프로그램 삭제
  - `GET /programs/search` - 프로그램 검색

- [ ] **ProgramApplicationController** (`/api/programs/{id}/applications`)
  - `POST /apply` - 신청
  - `GET /applications` - 신청 목록
  - `PUT /applications/{id}/approve` - 승인
  - `PUT /applications/{id}/reject` - 거부
  - `DELETE /applications/{id}` - 취소

- [ ] **ProgramParticipantController** (`/api/programs/{id}/participants`)
  - `GET /participants` - 참가자 목록
  - `POST /participants/{id}/attendance` - 출석 체크
  - `PUT /participants/{id}/complete` - 이수 처리

- [ ] **ProgramSatisfactionController** (`/api/programs/{id}/satisfaction`)
  - `POST /submit` - 만족도 제출
  - `GET /results` - 결과 조회

#### Mileage API
- [ ] **MileageController** (`/api/mileage`)
  - `GET /balance` - 잔액 조회
  - `GET /transactions` - 거래 내역
  - `POST /earn` - 적립
  - `POST /use` - 사용

#### Competency API
- [ ] **CompetencyController** (`/api/competency`)
  - `GET /surveys` - 설문 목록
  - `POST /surveys/{id}/submit` - 응답 제출
  - `GET /results` - 역량 결과 조회

#### Counseling API
- [ ] **CounselingController** (`/api/counseling`)
  - `POST /reservations` - 예약 생성
  - `GET /reservations` - 예약 목록
  - `DELETE /reservations/{id}` - 예약 취소
  - `GET /sessions` - 상담 내역

#### Career API
- [ ] **CareerController** (`/api/career`)
  - `POST /plans` - 진로 계획 생성
  - `GET /plans` - 진로 계획 목록
  - `POST /plans/{id}/goals` - 목표 추가
  - `PUT /goals/{id}/complete` - 목표 완료

### 🛠️ 주요 작업 내용

1. **RESTful API 설계**
   - URI 설계 (Resource 기반)
   - HTTP 메서드 활용 (GET, POST, PUT, DELETE)
   - 상태 코드 정의 (200, 201, 400, 404, 500)

2. **요청/응답 처리**
   - @RequestBody 검증
   - @PathVariable, @RequestParam 처리
   - ResponseEntity 활용

3. **페이징 & 정렬**
   - Pageable 적용
   - 커스텀 페이징 응답

4. **파일 업로드**
   - MultipartFile 처리
   - 파일 저장 로직

5. **API 문서화**
   - Swagger/OpenAPI 적용
   - API 명세 자동 생성

### 📊 예상 산출물
- Controller 클래스: 18개
- API 엔드포인트: 약 100개
- Controller 테스트: 약 200개

---

## 📝 Phase 5: Spring Security 통합

### 🎯 목표
인증/인가 시스템 구축 및 보안 강화

### 📦 구현 내용

#### 1. 인증 (Authentication)
- [ ] **JWT 기반 인증**
  - JWT 토큰 생성/검증
  - Access Token / Refresh Token
  - 토큰 저장소 (Redis)

- [ ] **로그인 처리**
  - UserDetailsService 구현
  - 비밀번호 암호화 (BCrypt)
  - 로그인 실패 처리

#### 2. 인가 (Authorization)
- [ ] **역할 기반 접근 제어 (RBAC)**
  - ROLE_STUDENT
  - ROLE_STAFF
  - ROLE_ADMIN

- [ ] **메서드 레벨 보안**
  - @PreAuthorize
  - @PostAuthorize
  - @Secured

#### 3. Security Filter Chain
- [ ] **필터 설정**
  - JWT 인증 필터
  - CORS 설정
  - CSRF 설정
  - 예외 처리

#### 4. 보안 강화
- [ ] **API 보안**
  - Rate Limiting
  - IP 화이트리스트
  - 로그인 시도 제한

### 📊 예상 산출물
- Security Configuration: 1개
- JWT 관련 클래스: 5개
- Filter 클래스: 3개
- Security 테스트: 약 50개

---

## 📝 Phase 6: DTO & Validation

### 🎯 목표
데이터 전송 객체 정의 및 입력 검증 구현

### 📦 구현 내용

#### 1. DTO 설계
- [ ] **Request DTO** (약 50개)
  - 생성 요청 DTO
  - 수정 요청 DTO
  - 검색 조건 DTO

- [ ] **Response DTO** (약 50개)
  - 상세 응답 DTO
  - 목록 응답 DTO
  - 통계 응답 DTO

#### 2. Validation
- [ ] **Bean Validation**
  - @NotNull, @NotBlank
  - @Size, @Min, @Max
  - @Email, @Pattern
  - 커스텀 Validator

- [ ] **비즈니스 규칙 검증**
  - 날짜 범위 검증
  - 상태 전이 검증
  - 권한 검증

### 📊 예상 산출물
- Request DTO: 약 50개
- Response DTO: 약 50개
- Custom Validator: 약 10개

---

## 📝 Phase 7: 예외 처리 & 로깅

### 🎯 목표
일관된 예외 처리 및 효과적인 로깅 시스템 구축

### 📦 구현 내용

#### 1. 예외 처리
- [ ] **Custom Exception**
  - BusinessException
  - EntityNotFoundException
  - DuplicateException
  - UnauthorizedException
  - 도메인별 Exception (약 20개)

- [ ] **Global Exception Handler**
  - @ControllerAdvice
  - 예외별 응답 포맷
  - 에러 코드 체계

#### 2. 로깅
- [ ] **Logback 설정**
  - 로그 레벨 설정
  - 파일 로깅
  - 로그 로테이션

- [ ] **AOP 로깅**
  - 메서드 실행 로깅
  - 실행 시간 측정
  - 파라미터 로깅

### 📊 예상 산출물
- Custom Exception: 약 20개
- Exception Handler: 1개
- AOP 로깅 클래스: 3개

---

## 📝 Phase 8: 통합 테스트

### 🎯 목표
전체 시스템의 통합 테스트 및 E2E 시나리오 검증

### 📦 구현 내용

#### 1. API 통합 테스트
- [ ] **@SpringBootTest**
  - 전체 컨텍스트 로딩
  - 실제 HTTP 요청/응답
  - 데이터베이스 트랜잭션

#### 2. 시나리오 테스트
- [ ] **사용자 플로우**
  - 회원가입 → 로그인 → 프로그램 신청 → 참가 → 이수
  - 마일리지 적립 → 사용
  - 상담 예약 → 상담 진행 → 기록

#### 3. 성능 테스트
- [ ] **부하 테스트**
  - JMeter 활용
  - 동시 접속자 테스트
  - 응답 시간 측정

### 📊 예상 산출물
- 통합 테스트: 약 100개
- 시나리오 테스트: 약 20개
- 성능 테스트 시나리오: 10개

---

## 📝 Phase 9: API 문서화

### 🎯 목표
API 명세 자동화 및 개발자 가이드 작성

### 📦 구현 내용

#### 1. Swagger/OpenAPI
- [ ] **Springdoc OpenAPI 적용**
  - API 자동 문서화
  - Swagger UI 설정
  - 모델 스키마 정의

#### 2. 문서 작성
- [ ] **개발자 가이드**
  - API 사용법
  - 인증 방법
  - 에러 코드 설명
  - 예제 코드

### 📊 예상 산출물
- OpenAPI Specification: 1개
- 개발자 가이드: 1개
- API 예제: 약 50개

---

## 📝 Phase 10: 배포 준비

### 🎯 목표
운영 환경 배포를 위한 설정 및 최적화

### 📦 구현 내용

#### 1. 프로파일 설정
- [ ] **환경별 설정**
  - dev (개발)
  - test (테스트)
  - prod (운영)

#### 2. 데이터베이스
- [ ] **마이그레이션 도구**
  - Flyway 또는 Liquibase
  - 버전 관리
  - 롤백 전략

#### 3. 모니터링
- [ ] **Actuator 설정**
  - Health Check
  - Metrics
  - 로그 수집

#### 4. 최적화
- [ ] **성능 최적화**
  - 쿼리 최적화
  - 캐시 적용 (Redis)
  - 인덱스 최적화

### 📊 예상 산출물
- 환경 설정 파일: 3개
- 마이그레이션 스크립트: 30개
- 모니터링 대시보드: 1개

---

## 📅 예상 개발 일정

### 단계별 타임라인

```
Week 1-2:   Service Layer (Auth, Common)
Week 3-4:   Service Layer (Program, Mileage)
Week 5-6:   Service Layer (Competency, Counseling, Career)
Week 7-8:   Controller Layer (Auth, Program, Mileage)
Week 9-10:  Controller Layer (Competency, Counseling, Career)
Week 11:    Spring Security 통합
Week 12:    DTO & Validation
Week 13:    예외 처리 & 로깅
Week 14-15: 통합 테스트
Week 16:    API 문서화
Week 17:    배포 준비 & 최적화
Week 18:    최종 테스트 & 버그 픽스
```

**총 예상 기간**: 약 18주 (4.5개월)

---

## 🎯 우선순위별 작업

### ⭐⭐⭐ 높음 (Core 기능)
1. Service Layer 구현
2. Controller Layer (REST API)
3. Spring Security 통합
4. DTO & Validation

### ⭐⭐ 중간 (필수 기능)
1. 예외 처리 & 로깅
2. 통합 테스트
3. 성능 최적화

### ⭐ 낮음 (부가 기능)
1. API 문서화
2. 모니터링 설정
3. 배포 자동화

---

## 📊 전체 진행률 시각화

```
[████████░░░░░░░░░░░░░░░░░░░░] 30% - 현재

완료:
├─ Entity Layer      [████████████████████] 100%
└─ Repository Layer  [████████████████████] 100%

진행 예정:
├─ Service Layer     [░░░░░░░░░░░░░░░░░░░░]   0%
├─ Controller Layer  [░░░░░░░░░░░░░░░░░░░░]   0%
├─ Security          [░░░░░░░░░░░░░░░░░░░░]   0%
├─ DTO & Validation  [░░░░░░░░░░░░░░░░░░░░]   0%
├─ Exception/Logging [░░░░░░░░░░░░░░░░░░░░]   0%
├─ Integration Test  [░░░░░░░░░░░░░░░░░░░░]   0%
├─ API Docs          [░░░░░░░░░░░░░░░░░░░░]   0%
└─ Deployment        [░░░░░░░░░░░░░░░░░░░░]   0%
```

---

## 🎓 학습 목표

각 Phase를 진행하면서 다음 기술들을 익히게 됩니다:

### Service Layer
- 트랜잭션 관리 (@Transactional)
- 비즈니스 로직 설계
- DTO 패턴
- 예외 처리 전략

### Controller Layer
- RESTful API 설계
- HTTP 통신
- 페이징/정렬
- 파일 업로드

### Security
- JWT 인증
- Spring Security
- 역할 기반 접근 제어
- 보안 베스트 프랙티스

### Testing
- 단위 테스트 (Mockito)
- 통합 테스트 (@SpringBootTest)
- 시나리오 테스트
- 성능 테스트

---

## 📝 참고 문서

- [Repository Layer 구현 상태](./03-repository-implementation-status.md)
- [ERD 설계](../02-design/01-erd-design.md)
- [프로젝트 수행 계획서](https://docs.google.com/document/d/1LPxYcGUIk_J7sn4BlCQeZrpfCZGavj8dZMRhIfEAAh4/edit)

---

## 🤝 팀 협업 가이드

### 브랜치 전략
```
main
├─ feature/service-auth
├─ feature/service-program
├─ feature/controller-auth
└─ ...
```

### 커밋 메시지 규칙
```
feat: 새로운 기능 추가
fix: 버그 수정
refactor: 코드 리팩토링
test: 테스트 추가/수정
docs: 문서 수정
style: 코드 포맷팅
chore: 기타 변경사항
```

---

**작성일**: 2025-11-03  
**작성자**: Development Team  
**프로젝트**: SCMS v1.0  
**마지막 업데이트**: 2025-11-03 18:00
