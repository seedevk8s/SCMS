# SCMS 프로젝트 세션 재개 가이드

## 🎯 현재 프로젝트 상태

### 프로젝트명
**SCMS (Student Competency Management System)** - 학생 역량 관리 시스템

### 프로젝트 위치
```
C:\Users\USER\Documents\choongang\Project\scms\scms-backend
```

### 현재 Phase
**Phase 1: 기반 구축** - 진행 중

---

## ✅ 완료된 작업 요약

### 1. 프로젝트 설정
- ✅ Spring Boot 3.5.7 + Gradle 프로젝트 생성
- ✅ MySQL 연결 (scms_db)
- ✅ 모든 필수 의존성 설정 완료

### 2. 기본 구조
- ✅ 패키지 구조 설정
- ✅ SecurityConfig 생성 (JWT 준비)
- ✅ HealthCheckController 구현
- ✅ 전역 예외 처리 설정

### 3. 아키텍처 결정
- ✅ **Modular Monolith** 방식 채택
- ✅ MSA 전환 가능하도록 설계 원칙 적용
- ✅ 도메인별 명확한 경계 설정 방침

### 4. 문서화
- ✅ README.md
- ✅ PROJECT_SETUP_GUIDE.md
- ✅ MSA_ARCHITECTURE_GUIDE.md
- ✅ 개발 로드맵 (SVG)
- ✅ Phase 1 진행사항 문서

### 5. 테스트
- ✅ 애플리케이션 정상 실행 확인
- ✅ 헬스체크 API 테스트 완료
  - URL: `http://localhost:8080/api/health`
  - 응답: `{"status":"UP", "timestamp":"...", "message":"SCMS API is running"}`

---

## 🎯 다음 작업: ERD 설계

### 목표
전체 데이터베이스 구조를 MSA 전환 가능하도록 설계

### 설계할 도메인

#### 1. Auth Domain (인증)
```
- users (사용자)
- roles (역할)
- user_roles (사용자-역할 매핑)
```

#### 2. Program Domain (비교과 프로그램)
```
- programs (프로그램)
- program_applications (프로그램 신청)
- program_participants (참여자)
```

#### 3. Mileage Domain (마일리지)
```
- mileage_accounts (마일리지 계정)
- mileage_transactions (마일리지 거래 내역)
- competency_certifications (역량 인증)
```

#### 4. Competency Domain (역량 진단)
```
- competency_surveys (역량 진단 설문)
- survey_questions (설문 문항)
- survey_responses (설문 응답)
- competency_results (진단 결과)
```

#### 5. Counseling Domain (상담)
```
- counseling_requests (상담 신청)
- counseling_sessions (상담 세션)
- counseling_notes (상담 노트)
```

### 설계 원칙
1. **도메인별 독립성**: 각 도메인이 자신의 테이블 소유
2. **느슨한 결합**: 외래키 대신 ID 참조 사용
3. **MSA 준비**: 나중에 DB 분리 가능하도록
4. **Audit 필드**: created_at, updated_at 모든 테이블에 포함

---

## 💬 세션 재개 시 사용할 멘트

### 📌 추천 멘트 (복사해서 사용)

```
안녕! SCMS 프로젝트 이어서 진행하자.

지난 세션에서:
- Spring Boot 3.5.7 프로젝트 초기 설정 완료
- MySQL 연결 및 SecurityConfig 설정 완료
- Modular Monolith 아키텍처로 개발 중 (MSA 전환 가능하도록)
- 헬스체크 API 테스트 완료

이제 Phase 1의 다음 단계인 ERD 설계를 시작하려고 해.
MSA 전환을 염두에 둔 도메인별 테이블 설계를 도와줘.

프로젝트 위치: C:\Users\USER\Documents\choongang\Project\scms\scms-backend

ERD 설계 시작하자!
```

### 또는 간단하게

```
SCMS 프로젝트 계속하자!
Phase 1 - ERD 설계 단계부터 시작해줘.
프로젝트 경로: C:\Users\USER\Documents\choongang\Project\scms\scms-backend
```

### 또는 매우 간단하게

```
SCMS 프로젝트 ERD 설계 시작!
경로: C:\Users\USER\Documents\choongang\Project\scms\scms-backend
```

---

## 📂 주요 파일 위치

### 문서 파일
```
docs/
├── 00-SESSION_RESUME.md           # 이 문서
└── 01-progress/
    └── 01-phase1-foundation.md    # Phase 1 진행사항

MSA_ARCHITECTURE_GUIDE.md          # MSA 가이드
PROJECT_SETUP_GUIDE.md             # 초기 설정 가이드
README.md                          # 프로젝트 소개
```

### 주요 소스 파일
```
src/main/java/com/university/scms/
├── ScmsApplication.java
├── config/
│   ├── JpaConfig.java
│   └── SecurityConfig.java
├── controller/
│   └── HealthCheckController.java
└── exception/
    └── GlobalExceptionHandler.java
```

### 설정 파일
```
src/main/resources/
└── application.yml                # 애플리케이션 설정
```

---

## 🔍 현재 프로젝트 확인 방법

### IntelliJ에서 프로젝트 열기
1. IntelliJ IDEA 실행
2. File → Open
3. `C:\Users\USER\Documents\choongang\Project\scms\scms-backend` 선택

### 애플리케이션 실행
1. `ScmsApplication.java` 파일 열기
2. 초록색 ▶️ 버튼 클릭
3. 브라우저에서 `http://localhost:8080/api/health` 접속하여 확인

### 문서 확인
```
docs/01-progress/01-phase1-foundation.md  # 상세한 진행사항
```

---

## 📊 Phase 1 체크리스트

### ✅ 완료
- [x] 프로젝트 초기 설정
- [x] 데이터베이스 설정
- [x] Spring Security 기본 설정
- [x] 헬스체크 API
- [x] MSA 가이드 문서

### 🔄 진행 중
- [ ] **ERD 설계** ← 현재 단계
- [ ] Entity 클래스 작성
- [ ] Repository 작성

### ⏳ 예정
- [ ] JWT 인증 시스템 구현
- [ ] 로그인/회원가입 API
- [ ] Phase 2 진행

---

## 💡 중요 설계 원칙 (재확인)

### MSA 전환을 위한 원칙
1. **도메인별 경계 명확히**
2. **Entity 간 직접 참조 금지** (ID만 참조)
3. **각 도메인이 자신의 데이터 소유**
4. **API 기반 통신 준비**

### 개발 시 체크
- [ ] 외래키(FK) 사용 지양
- [ ] DTO 사용 (Entity 노출 금지)
- [ ] 도메인별 API 경로: `/api/{domain}/**`
- [ ] 비즈니스 로직은 Service 계층에

---

## 🎓 참고할 문서

### 프로젝트 내부 문서
1. **MSA_ARCHITECTURE_GUIDE.md** - MSA 전환 전략
2. **docs/01-progress/01-phase1-foundation.md** - 상세 진행사항
3. **PROJECT_SETUP_GUIDE.md** - IntelliJ 설정

### Google Drive 문서
- 원본 기획 문서: `1LPxYcGUIk_J7sn4BlCQeZrpfCZGavj8dZMRhIfEAAh4`

---

## 🚀 ERD 설계 진행 순서

### 1단계: 도메인별 테이블 정의
- 각 도메인의 핵심 테이블 나열
- 테이블별 주요 컬럼 정의

### 2단계: 관계 정의
- 1:N, N:M 관계 파악
- 외래키 대신 ID 참조 방식 결정

### 3단계: ERD 다이어그램 작성
- Mermaid 또는 PlantUML 형식
- 시각화 문서 생성

### 4단계: Entity 클래스 생성
- JPA Entity 클래스 작성
- Repository 인터페이스 생성

---

## ⚙️ 환경 정보

### 개발 환경
- **IDE**: IntelliJ IDEA
- **Java**: 17
- **Gradle**: 8.5
- **Spring Boot**: 3.5.7

### 데이터베이스
- **DBMS**: MySQL 8.0
- **Database**: scms_db
- **Port**: 3306
- **Username**: root

### 서버
- **Port**: 8080
- **Base URL**: http://localhost:8080

---

## 📞 문제 해결

### 애플리케이션이 실행 안 될 때
1. MySQL 서버 실행 확인
2. application.yml의 DB 정보 확인
3. Gradle 동기화: `./gradlew clean build`

### 파일을 못 찾을 때
```
docs/01-progress/01-phase1-foundation.md  # 진행사항
docs/00-SESSION_RESUME.md                # 이 문서
MSA_ARCHITECTURE_GUIDE.md                # MSA 가이드
```

---

## ✨ 다음 세션 준비사항

### Claude에게 알려줄 정보
1. 프로젝트 경로
2. 현재 작업 단계 (ERD 설계)
3. 이전 세션 완료 사항

### 필요한 도구
- IntelliJ IDEA 실행
- MySQL 서버 실행
- 브라우저 (테스트용)

---

**작성일**: 2025-10-31  
**다음 작업**: ERD 설계  
**예상 소요시간**: 40-50분

---

## 🎯 목표

**Phase 1 완료까지 남은 작업:**
1. ERD 설계 (다음 단계)
2. Entity 클래스 작성
3. JWT 인증 시스템 구현

**Phase 1 완료 후:**
- Phase 2: 핵심 기능 개발 (비교과 프로그램, 마일리지 등)

---

**세션을 재개할 준비가 되었습니다! 🚀**
