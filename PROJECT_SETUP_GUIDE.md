# SCMS 프로젝트 구조 안내

## 📁 디렉토리 구조

```
scms-backend/
├── src/
│   ├── main/
│   │   ├── java/com/university/scms/
│   │   │   ├── ScmsApplication.java       # 메인 애플리케이션 클래스
│   │   │   ├── config/                    # 설정 클래스
│   │   │   │   └── JpaConfig.java         # JPA Auditing 설정
│   │   │   ├── controller/                # REST API 컨트롤러
│   │   │   │   └── HealthCheckController.java  # 헬스체크 API
│   │   │   ├── domain/
│   │   │   │   ├── entity/                # JPA 엔티티
│   │   │   │   │   └── User.java          # 사용자 엔티티
│   │   │   │   └── repository/            # JPA 리포지토리
│   │   │   │       └── UserRepository.java  # 사용자 리포지토리
│   │   │   ├── dto/                       # 데이터 전송 객체 (TODO)
│   │   │   ├── service/                   # 비즈니스 로직 (TODO)
│   │   │   ├── security/                  # JWT, Filter 등 보안 (TODO)
│   │   │   ├── util/                      # 유틸리티 클래스 (TODO)
│   │   │   └── exception/                 # 예외 처리
│   │   │       └── GlobalExceptionHandler.java
│   │   └── resources/
│   │       └── application.yml            # 애플리케이션 설정
│   └── test/                              # 테스트 코드 (TODO)
├── gradle/                                # Gradle Wrapper
├── build.gradle                           # Gradle 빌드 설정
├── settings.gradle                        # Gradle 프로젝트 설정
├── .gitignore                             # Git 제외 파일
└── README.md                              # 프로젝트 설명

```

## 🚀 IntelliJ IDEA에서 프로젝트 열기

### 방법 1: Open Project
1. IntelliJ IDEA 실행
2. **File > Open** 선택
3. `scms-backend` 폴더 선택
4. **Open as Project** 클릭

### 방법 2: Import Project
1. IntelliJ IDEA 실행
2. **File > New > Project from Existing Sources** 선택
3. `scms-backend` 폴더 선택
4. **Import project from external model > Gradle** 선택
5. **Finish** 클릭

## ⚙️ 초기 설정

### 1. JDK 설정 확인
- **File > Project Structure > Project**
- SDK를 Java 17로 설정
- Language Level을 17로 설정

### 2. Gradle 새로고침
- 우측 Gradle 탭 클릭
- 🔄 Reload All Gradle Projects 버튼 클릭
- 의존성 다운로드 대기

### 3. Lombok 플러그인 설치 (필수!)
- **File > Settings > Plugins**
- "Lombok" 검색 후 설치
- IntelliJ 재시작

### 4. Annotation Processing 활성화
- **File > Settings > Build, Execution, Deployment > Compiler > Annotation Processors**
- ✅ **Enable annotation processing** 체크

### 5. MySQL 설정
```sql
-- MySQL에 접속하여 실행
CREATE DATABASE scms_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 6. application.yml 수정
`src/main/resources/application.yml` 파일에서 DB 정보 수정:
```yaml
spring:
  datasource:
    username: your_mysql_username  # 본인의 MySQL 사용자명
    password: your_mysql_password  # 본인의 MySQL 비밀번호
```

## ▶️ 프로젝트 실행

### 방법 1: IntelliJ에서 직접 실행
1. `ScmsApplication.java` 파일 열기
2. `main` 메서드 옆의 ▶️ 버튼 클릭
3. **Run 'ScmsApplication'** 선택

### 방법 2: Gradle 사용
1. 터미널 열기 (Alt + F12)
2. 다음 명령어 실행:
```bash
./gradlew bootRun
```

### 실행 확인
브라우저에서 접속:
```
http://localhost:8080/api/health
```

응답:
```json
{
  "status": "UP",
  "timestamp": "2025-10-31T...",
  "message": "SCMS API is running"
}
```

## 📋 다음 단계

현재 프로젝트는 기본 구조만 갖춘 상태입니다.

### Phase 1: 기반 구축 (진행 예정)
- [ ] ERD 설계 및 전체 Entity 클래스 작성
- [ ] JWT 인증/인가 시스템 구현
  - JWT 토큰 생성/검증 유틸리티
  - Spring Security 설정
  - 로그인/회원가입 API
  - JWT 필터

### Phase 2: 핵심 기능 개발
- [ ] 사용자 관리 API
- [ ] 비교과 프로그램 API
- [ ] 마일리지 시스템 API

## 💡 개발 팁

### 1. Hot Reload 활성화
`build.gradle`에 이미 `spring-boot-devtools`가 포함되어 있습니다.
IntelliJ에서 자동 빌드 활성화:
- **File > Settings > Build, Execution, Deployment > Compiler**
- ✅ **Build project automatically** 체크

### 2. 로그 확인
실행 중 로그는 IntelliJ 하단의 **Run** 탭에서 확인 가능

### 3. 데이터베이스 확인
IntelliJ의 Database 툴을 사용하면 편리:
- **View > Tool Windows > Database**
- **+ > Data Source > MySQL** 선택

## ❓ 문제 해결

### Gradle 빌드 실패
```bash
# 터미널에서 실행
./gradlew clean build
```

### MySQL 연결 오류
- MySQL 서버가 실행 중인지 확인
- application.yml의 DB 정보 확인
- 방화벽 설정 확인

### Lombok이 동작하지 않음
- Lombok 플러그인 설치 확인
- Annotation Processing 활성화 확인
- IntelliJ 재시작

## 📞 추가 도움

프로젝트 개발 중 도움이 필요하면 언제든지 물어보세요! 🚀
