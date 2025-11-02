# Repository Layer 구현 - Phase 1

## 📋 작업 개요

**작업 기간**: 2025-11-02  
**브랜치**: `feature/repository-layer`  
**목표**: Auth 및 Common 도메인의 Repository 계층 구현 및 테스트

---

## 🎯 Phase 1 완료 현황

### 구현 완료 (2/29)

| 도메인 | Repository | 테스트 | 상태 |
|--------|-----------|--------|------|
| **Auth** | UserRepository | UserRepositoryTest | ✅ 완료 |
| **Common** | CommonCodeRepository | CommonCodeRepositoryTest | ✅ 완료 |

---

## 📁 생성된 파일 목록

### 1. Repository 파일 (2개)

```
src/main/java/com/university/scms/domain/
├── auth/
│   └── repository/
│       └── UserRepository.java          # Auth 도메인 Repository
└── common/
    └── repository/
        └── CommonCodeRepository.java     # Common 도메인 Repository
```

### 2. 테스트 파일 (3개)

```
src/test/
├── java/com/university/scms/domain/
│   ├── auth/repository/
│   │   └── UserRepositoryTest.java      # User Repository 테스트 (22개 테스트)
│   └── common/repository/
│       └── CommonCodeRepositoryTest.java # CommonCode Repository 테스트 (27개 테스트)
└── resources/
    └── application-test.yml              # 테스트 환경 설정 (MySQL)
```

---

## 🔍 구현 상세

### 1. UserRepository

**파일 위치**: `src/main/java/com/university/scms/domain/auth/repository/UserRepository.java`

#### 주요 기능

**기본 조회**
- `findByUsername(String username)` - 사용자명으로 조회
- `findByEmail(String email)` - 이메일로 조회
- `existsByUsername(String username)` - 사용자명 존재 여부
- `existsByEmail(String email)` - 이메일 존재 여부

**역할별 조회**
- `findByRole(UserRole role)` - 역할별 사용자 목록
- `findByRoleAndEnabled(UserRole role, Boolean enabled)` - 활성화된 특정 역할 사용자

**학생 관련 조회**
- `findByStudentId(String studentId)` - 학번으로 조회
- `findActiveStudentsByDepartment(String department)` - 학과별 활성 학생
- `findActiveStudentsByGrade(Integer grade)` - 학년별 활성 학생
- `findActiveStudentsByDepartmentAndGrade(String, Integer)` - 학과+학년별 학생

**교직원 관련 조회**
- `findByEmployeeId(String employeeId)` - 직원번호로 조회
- `findActiveStaffByPosition(String position)` - 직위별 활성 교직원

**계정 상태 관련**
- `findByEnabled(Boolean enabled)` - 활성화 여부로 조회
- `countDisabledUsers()` - 비활성 사용자 수

**검색**
- `findByNameContaining(String name)` - 이름 검색
- `findByRoleAndNameContaining(UserRole role, String name)` - 역할+이름 검색

#### 특징
- Spring Data JPA 메서드 네이밍 규칙 활용
- 복잡한 쿼리는 `@Query` 어노테이션 사용
- MSA 전환 대비: Auth Domain의 핵심 리포지토리

---

### 2. CommonCodeRepository

**파일 위치**: `src/main/java/com/university/scms/domain/common/repository/CommonCodeRepository.java`

#### 주요 기능

**기본 조회**
- `findByCodeGroupAndCodeValue(String, String)` - 코드 그룹+값으로 조회
- `existsByCodeGroupAndCodeValue(String, String)` - 존재 여부 확인

**코드 그룹별 조회**
- `findByCodeGroup(String codeGroup)` - 코드 그룹의 모든 코드
- `findActiveCodesByGroup(String codeGroup)` - 활성화된 코드만 (정렬 포함)
- `countActiveCodesByGroup(String codeGroup)` - 활성 코드 개수

**코드 값별 조회**
- `findByCodeValue(String codeValue)` - 코드 값으로 조회

**활성화 상태별 조회**
- `findByIsActive(Boolean isActive)` - 활성화 여부로 조회
- `findAllNotDeleted()` - 삭제되지 않은 코드
- `findAllUsable()` - 사용 가능한 코드 (활성+미삭제)

**검색**
- `findByCodeNameContaining(String codeName)` - 코드명 검색
- `searchByGroupAndName(String, String)` - 그룹+코드명 검색

**통계**
- `findAllCodeGroups()` - 전체 코드 그룹 목록
- `countInactiveCodes()` - 비활성 코드 개수
- `countDeletedCodes()` - 삭제된 코드 개수
- `findMaxDisplayOrderByGroup(String)` - 그룹 내 최대 정렬 순서

#### 특징
- Soft Delete 지원 (deletedAt 필드 활용)
- 정렬 순서(displayOrder) 관리
- 모든 도메인에서 참조 가능한 독립적 리포지토리

---

## 🧪 테스트 구현

### 1. UserRepositoryTest

**파일 위치**: `src/test/java/com/university/scms/domain/auth/repository/UserRepositoryTest.java`

#### 테스트 구성 (총 22개)

**기본 CRUD (4개)**
- `testCreateUser()` - 사용자 생성
- `testFindUser()` - 사용자 조회
- `testUpdateUser()` - 사용자 수정
- `testDeleteUser()` - 사용자 삭제

**커스텀 쿼리 (4개)**
- `testFindByUsername()` - Username 조회
- `testFindByEmail()` - Email 조회
- `testExistsByUsername()` - Username 존재 확인
- `testExistsByEmail()` - Email 존재 확인

**역할별 조회 (2개)**
- `testFindByRole()` - 역할별 조회
- `testFindByRoleAndEnabled()` - 활성화된 역할별 조회

**학생 관련 조회 (4개)**
- `testFindByStudentId()` - 학번 조회
- `testFindActiveStudentsByDepartment()` - 학과별 학생
- `testFindActiveStudentsByGrade()` - 학년별 학생
- `testFindActiveStudentsByDepartmentAndGrade()` - 학과+학년별 학생

**교직원 관련 조회 (2개)**
- `testFindByEmployeeId()` - 직원번호 조회
- `testFindActiveStaffByPosition()` - 직위별 교직원

**계정 상태 관련 (2개)**
- `testFindByEnabled()` - 활성화 여부 조회
- `testCountDisabledUsers()` - 비활성 사용자 수

**검색 (2개)**
- `testFindByNameContaining()` - 이름 검색
- `testFindByRoleAndNameContaining()` - 역할+이름 검색

**비즈니스 메서드 (2개)**
- `testUserRoleCheckMethods()` - 역할 확인 메서드
- `testEnableDisableMethods()` - 활성화/비활성화 메서드

---

### 2. CommonCodeRepositoryTest

**파일 위치**: `src/test/java/com/university/scms/domain/common/repository/CommonCodeRepositoryTest.java`

#### 테스트 구성 (총 27개)

**기본 CRUD (4개)**
- `testCreateCommonCode()` - 공통 코드 생성
- `testFindCommonCode()` - 공통 코드 조회
- `testUpdateCommonCode()` - 공통 코드 수정
- `testDeleteCommonCode()` - 공통 코드 삭제

**커스텀 쿼리 (2개)**
- `testFindByCodeGroupAndCodeValue()` - 그룹+값 조회
- `testExistsByCodeGroupAndCodeValue()` - 존재 여부 확인

**코드 그룹별 조회 (3개)**
- `testFindByCodeGroup()` - 그룹별 전체 조회
- `testFindActiveCodesByGroup()` - 활성 코드 조회
- `testCountActiveCodesByGroup()` - 활성 코드 개수

**코드 값별 조회 (1개)**
- `testFindByCodeValue()` - 코드 값 조회

**활성화 상태별 조회 (3개)**
- `testFindByIsActive()` - 활성화 여부 조회
- `testFindAllNotDeleted()` - 미삭제 코드 조회
- `testFindAllUsable()` - 사용 가능 코드 조회

**검색 (2개)**
- `testFindByCodeNameContaining()` - 코드명 검색
- `testSearchByGroupAndName()` - 그룹+코드명 검색

**통계 (4개)**
- `testFindAllCodeGroups()` - 코드 그룹 목록
- `testCountInactiveCodes()` - 비활성 코드 개수
- `testCountDeletedCodes()` - 삭제 코드 개수
- `testFindMaxDisplayOrderByGroup()` - 최대 정렬 순서
- `testFindMaxDisplayOrderByGroupWithNoData()` - 데이터 없을 때

**비즈니스 메서드 (7개)**
- `testActivateMethod()` - 활성화 메서드
- `testDeactivateMethod()` - 비활성화 메서드
- `testSoftDeleteMethod()` - 소프트 삭제 메서드
- `testIsUsableMethod()` - 사용 가능 여부 확인
- `testBelongsToGroupMethod()` - 그룹 소속 확인
- `testHasValueMethod()` - 값 일치 확인
- `testGetFullCodeMethod()` - 전체 키 생성

---

## ⚙️ 테스트 환경 설정

### application-test.yml

**파일 위치**: `src/test/resources/application-test.yml`

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
    show-sql: true
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

**특징**:
- 실제 MySQL 데이터베이스 사용
- SQL 로그 상세 출력
- 테스트별 데이터 격리 (`@AfterEach`에서 `deleteAll()`)

---

## 🔧 문제 해결 과정

### 1. Bean 중복 정의 에러

**문제**: 
```
The bean 'userRepository' has already been defined
```

**원인**: 
- 옛날 구조의 `domain/repository/UserRepository.java` 파일이 남아있음
- 새로운 `auth/repository/UserRepository.java`와 충돌

**해결**:
```bash
# 옛날 파일 삭제
Remove-Item "C:\Users\USER\Documents\choongang\Project\scms\scms-backend\src\main\java\com\university\scms\domain\repository\UserRepository.java"

# 빈 폴더 삭제
Remove-Item "C:\Users\USER\Documents\choongang\Project\scms\scms-backend\src\main\java\com\university\scms\domain\repository" -Recurse
```

---

### 2. JPA Auditing 미작동 에러

**문제**: 
```
Column 'created_at' cannot be null
```

**원인**: 
- `@DataJpaTest`는 JPA 관련 빈만 로드
- `@Configuration` 클래스인 `JpaConfig`가 스캔되지 않음
- JPA Auditing이 비활성화되어 `@CreatedDate`, `@LastModifiedDate` 미작동

**해결**:
두 테스트 클래스에 `@Import(JpaConfig.class)` 추가

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(JpaConfig.class)  // ✅ JPA Auditing 활성화
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserRepositoryTest {
    // ...
}
```

---

## 📊 테스트 결과

### 실행 결과

```
✅ UserRepositoryTest: 22/22 통과
✅ CommonCodeRepositoryTest: 27/27 통과
```

**총 49개 테스트 모두 성공** 🎉

---

## 🎓 학습 포인트

### 1. Spring Data JPA Repository 패턴

- **메서드 네이밍 규칙**: `findBy`, `existsBy`, `countBy` 등
- **@Query 어노테이션**: 복잡한 쿼리는 JPQL/SQL로 직접 작성
- **파라미터 바인딩**: `@Param` 어노테이션 사용

### 2. 테스트 전략

- **@DataJpaTest**: JPA 컴포넌트만 로드하는 슬라이스 테스트
- **@AutoConfigureTestDatabase**: 실제 DB 사용 설정
- **@TestMethodOrder**: 테스트 순서 지정
- **@BeforeEach/@AfterEach**: 테스트 전/후 처리

### 3. JPA Auditing

- **@EnableJpaAuditing**: JPA Auditing 활성화
- **@CreatedDate/@LastModifiedDate**: 자동 시간 관리
- **@EntityListeners(AuditingEntityListener.class)**: 엔티티에 적용

### 4. 도메인 주도 설계 (DDD)

- **도메인별 패키지 구조**: `auth/repository`, `common/repository`
- **MSA 전환 대비**: 도메인 간 느슨한 결합
- **Repository 네이밍**: `{Entity}Repository` 규칙

---

## 📈 다음 단계 (Phase 2)

### 구현 예정 (8개 Repository)

**Program 도메인 (6개)**
1. ProgramRepository
2. ProgramCompetencyRepository
3. ProgramApplicationRepository
4. ProgramParticipantRepository
5. ProgramSatisfactionRepository
6. ProgramCategoryRepository

**Mileage 도메인 (2개)**
7. MileageAccountRepository
8. MileageTransactionRepository

---

## 🔗 관련 문서

- [ERD 설계](../02-design/01-erd-design.md)
- [Entity 구현 가이드](../02-design/02-entity-implementation-guide.md)
- [MSA 아키텍처 가이드](../../MSA_ARCHITECTURE_GUIDE.md)

---

## 📝 변경 이력

| 날짜 | 작업 내용 | 작성자 |
|------|----------|--------|
| 2025-11-02 | Phase 1 Repository 구현 완료 | Hojin |

---

## 💡 참고사항

1. **테스트 실행 방법**
   ```bash
   # 전체 테스트
   ./gradlew test
   
   # 특정 테스트만
   ./gradlew test --tests "UserRepositoryTest"
   ```

2. **JPA Auditing 필수**
   - 모든 테스트 클래스에 `@Import(JpaConfig.class)` 추가 필요

3. **실제 DB 사용**
   - 테스트용 데이터는 `@AfterEach`에서 반드시 정리
   - MySQL 서버 실행 상태 확인 필요

4. **Git 워크플로우**
   - 브랜치: `feature/repository-layer`
   - Phase 완료 시마다 커밋
   - 전체 완료 후 `main`에 머지
