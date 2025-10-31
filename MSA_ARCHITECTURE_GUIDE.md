# SCMS MSA 아키텍처 가이드

## 🎯 MSA 전환을 위한 현재 구조 설계 원칙

이 프로젝트는 **모노리스(Monolith)로 시작**하지만, **MSA로 전환 가능**하도록 설계됩니다.

---

## 📐 도메인 주도 설계 (DDD) 기반 패키지 구조

### 현재 구조
```
com.university.scms/
├── config/              # 공통 설정
├── controller/          # API 엔드포인트
├── domain/
│   ├── entity/         # 엔티티
│   └── repository/     # 리포지토리
├── service/            # 비즈니스 로직
├── dto/                # 데이터 전송 객체
├── security/           # 보안
├── exception/          # 예외 처리
└── util/               # 유틸리티
```

### MSA 전환 시 분리 가능한 도메인

#### 🎯 1. Auth Service (인증 서비스)
```
포트: 8081
책임: 사용자 인증, JWT 발급, 회원 관리
엔드포인트: /api/auth/**
```

#### 📚 2. Program Service (비교과 프로그램 서비스)
```
포트: 8082
책임: 프로그램 관리, 신청, 참여 확인
엔드포인트: /api/programs/**
```

#### 💰 3. Mileage Service (마일리지 서비스)
```
포트: 8083
책임: 마일리지 적립, 조회, 역량 인증
엔드포인트: /api/mileage/**
```

#### 📊 4. Competency Service (역량 진단 서비스)
```
포트: 8084
책임: 설문 관리, 역량 평가, 결과 분석
엔드포인트: /api/competency/**
```

#### 💬 5. Counseling Service (상담 서비스)
```
포트: 8085
책임: 상담 예약, 승인, 이력 관리
엔드포인트: /api/counseling/**
```

#### 👤 6. User Service (사용자 정보 서비스)
```
포트: 8086
책임: 사용자 프로필, 마이페이지
엔드포인트: /api/users/**
```

---

## 🔑 MSA 전환을 위한 설계 원칙

### 1. 느슨한 결합 (Loose Coupling)

#### ✅ DO: 인터페이스 기반 통신
```java
// 좋은 예: Service 인터페이스 정의
public interface ProgramService {
    ProgramDto getProgram(Long id);
    List<ProgramDto> getPrograms();
}

// 구현체
@Service
public class ProgramServiceImpl implements ProgramService {
    // ...
}
```

#### ❌ DON'T: 직접 Entity 반환
```java
// 나쁜 예: Entity를 직접 반환
public Program getProgram(Long id) {
    return programRepository.findById(id);
}
```

---

### 2. 도메인별 독립적인 데이터 관리

#### ✅ DO: 각 도메인이 자신의 데이터 소유
```java
// Program Service
@Entity
public class Program {
    private Long id;
    private String title;
    private Long organizerId;  // User ID만 참조 (외래키 X)
}

// User Service
@Entity
public class User {
    private Long id;
    private String username;
}
```

#### ❌ DON'T: 도메인 간 직접 참조
```java
// 나쁜 예: 다른 도메인 Entity 직접 참조
@Entity
public class Program {
    @ManyToOne
    private User organizer;  // ❌ 강한 결합!
}
```

---

### 3. API 기반 통신

#### ✅ DO: REST API 또는 이벤트로 통신
```java
// 프로그램 참여 시 마일리지 적립
@Service
public class ProgramApplicationService {
    
    @Autowired
    private RestTemplate restTemplate;  // 또는 WebClient
    
    public void confirmParticipation(Long userId, Long programId) {
        // 1. 참여 확인 처리
        // ...
        
        // 2. 마일리지 서비스 호출 (REST API)
        String mileageServiceUrl = "http://localhost:8083/api/mileage";
        MileageRequest request = new MileageRequest(userId, points);
        restTemplate.postForObject(mileageServiceUrl, request, MileageResponse.class);
    }
}
```

---

### 4. 공통 모듈 최소화

#### 공통 라이브러리로 분리 가능한 것들:
- `common-dto`: 공통 DTO
- `common-security`: JWT 유틸리티
- `common-exception`: 공통 예외 클래스
- `common-util`: 공통 유틸리티

---

## 🚀 MSA 전환 로드맵

### Phase 1: 모노리스 개발 (현재)
```
✅ 단일 애플리케이션
✅ 하나의 데이터베이스
✅ 도메인별 패키지 분리
✅ 느슨한 결합 유지
```

### Phase 2: 모듈화
```
□ 각 도메인을 별도 Maven/Gradle 모듈로 분리
□ 공통 모듈 추출
□ 내부 API 인터페이스 정의
```

### Phase 3: 서비스 분리
```
□ 각 모듈을 독립 애플리케이션으로 전환
□ 독립적인 데이터베이스 분리
□ REST API 통신 구현
□ API Gateway 도입
```

### Phase 4: MSA 완성
```
□ Service Discovery (Eureka)
□ Config Server (중앙 설정 관리)
□ Circuit Breaker (Resilience4j)
□ 메시지 큐 (Kafka/RabbitMQ)
□ 분산 추적 (Zipkin/Jaeger)
```

---

## 📋 현재 개발 시 체크리스트

### ✅ 매 개발마다 확인할 사항

#### 1. Entity 설계
- [ ] 다른 도메인 Entity 직접 참조 안 함
- [ ] ID만으로 연관관계 표현
- [ ] 도메인별 경계 명확

#### 2. Service 설계
- [ ] 인터페이스 기반 설계
- [ ] DTO 사용 (Entity 노출 금지)
- [ ] 비즈니스 로직만 포함

#### 3. Controller 설계
- [ ] `/api/{domain}/**` 패턴 사용
- [ ] RESTful API 원칙 준수
- [ ] 도메인별 명확한 경로

#### 4. 의존성 관리
- [ ] 도메인 간 순환 참조 방지
- [ ] 공통 기능은 별도 패키지

---

## 🎯 현재 프로젝트 API 설계

### 인증 & 사용자
```
POST   /api/auth/login           # 로그인
POST   /api/auth/register        # 회원가입
GET    /api/users/me             # 내 정보
PUT    /api/users/me             # 정보 수정
```

### 비교과 프로그램
```
GET    /api/programs             # 프로그램 목록
POST   /api/programs             # 프로그램 생성 (교직원)
GET    /api/programs/{id}        # 프로그램 상세
POST   /api/programs/{id}/apply  # 프로그램 신청
DELETE /api/programs/{id}/apply  # 신청 취소
```

### 마일리지
```
GET    /api/mileage              # 내 마일리지 조회
GET    /api/mileage/history      # 마일리지 내역
POST   /api/mileage/certify      # 역량 인증
```

### 역량 진단
```
GET    /api/competency/surveys   # 설문 목록
POST   /api/competency/surveys/{id}/submit  # 설문 제출
GET    /api/competency/results   # 내 진단 결과
```

### 상담
```
GET    /api/counseling           # 상담 목록
POST   /api/counseling           # 상담 신청
PUT    /api/counseling/{id}      # 상담 승인/거절
```

---

## 💡 핵심 원칙 요약

1. **도메인별 명확한 경계**: 각 도메인이 자신의 데이터와 로직 소유
2. **느슨한 결합**: 인터페이스와 DTO 사용, 직접 참조 금지
3. **독립적 배포 가능**: 각 도메인이 독립 실행 가능하도록 설계
4. **API 우선**: REST API 기반 통신 구조
5. **공통 최소화**: 공통 모듈은 최소한으로 유지

---

## 📚 참고 자료

- Spring Cloud: https://spring.io/projects/spring-cloud
- MSA Pattern: https://microservices.io/patterns/
- Domain-Driven Design (DDD)
- API Gateway Pattern
- CQRS & Event Sourcing

---

**이 문서는 개발 진행에 따라 지속적으로 업데이트됩니다.**
