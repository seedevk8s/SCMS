# Git 워크플로우

## 🌳 브랜치 전략

### 메인 브랜치
- **main**: 프로덕션 준비 코드
  - 항상 배포 가능한 상태 유지
  - feature 브랜치 머지를 통해서만 업데이트

### Feature 브랜치
- **명명 규칙**: `feature/{작업-내용}`
- **생성 기준**: 도메인별 Entity 구현 단위
- **작업 완료 후**: main에 머지 후 삭제하지 않음 (이력 보존)

---

## 📝 커밋 메시지 규칙

### 포맷
```
<type>: <subject>

<body>

<footer>
```

### Type
- **feat**: 새로운 기능 추가
- **fix**: 버그 수정
- **docs**: 문서 변경
- **refactor**: 코드 리팩토링
- **test**: 테스트 추가/수정
- **chore**: 빌드 업무, 패키지 매니저 설정 등

### Subject
- 50자 이내
- 명령형으로 작성
- 마침표 없음

### Body (선택)
- 무엇을, 왜 변경했는지 설명
- 72자마다 줄바꿈

### Footer (선택)
- 이슈 번호 참조
- Breaking Changes 명시

---

## 🔄 실제 워크플로우

### 1. Phase 1: 초기 설정
```bash
# 브랜치 생성
git checkout -b feature/phase1-erd-design

# 작업 진행 (BaseEntity, User, Program Domain)
git add .
git commit -m "feat: ERD 설계 완료 및 기초 Entity 구현 (5/23)"

# main 머지
git checkout main
git merge feature/phase1-erd-design --no-ff -m "Merge feature/phase1-erd-design"
git push origin main
```

**완료 Entity**: BaseEntity, User, UserRole, Program, ProgramApplication, ProgramParticipant (5개)

---

### 2. Phase 2: Mileage Domain
```bash
# 브랜치 생성
git checkout -b feature/entity-mileage-domain

# 작업 진행 (Mileage Domain)
# - MileageAccount, MileageTransaction, CompetencyCertification
# - TransactionType, VerificationStatus
# 중간에 Competency Domain도 함께 작업

git add .
git commit -m "feat: Mileage & Competency Domain Entity 구현 완료

- Mileage Domain (3개 Entity)
  * MileageAccount: 마일리지 계좌 관리
  * MileageTransaction: 마일리지 거래 내역
  * CompetencyCertification: 역량 인증 (Mileage 연계)
  * Enum: TransactionType, VerificationStatus

- Competency Domain (4개 Entity)
  * CompetencySurvey: 역량 진단 설문
  * SurveyQuestion: 설문 문항
  * SurveyResponse: 설문 응답
  * CompetencyResult: 진단 결과
  * Enum: TargetRole, QuestionType

- Hybrid JPA 전략 적용
  * 동일 도메인 내: JPA 관계 매핑
  * 크로스 도메인: ID 참조
  * NO_CONSTRAINT로 느슨한 결합

- 진행률: 12/23 Entity 완료 (52%)
- 다음 작업: Counseling Domain

Docs: 세션 재개 문서 업데이트"

# main 머지
git checkout main
git merge feature/entity-mileage-domain --no-ff -m "Merge branch 'feature/entity-mileage-domain'"
git push origin main
```

**완료 Entity**: Mileage Domain 5개 + Competency Domain 6개 (총 11개, 누적 12개)

---

### 3. Phase 3: Counseling Domain (진행 중)
```bash
# 브랜치 생성
git checkout -b feature/entity-counseling-domain

# 작업 진행 예정
# - CounselingReservation
# - CounselingSession
# - CounselorAvailability
```

---

## 📊 브랜치 이력

| 브랜치명 | 작업 내용 | Entity 수 | 상태 |
|---------|-----------|-----------|------|
| feature/phase1-erd-design | 초기 설정 + Auth/Program Domain | 5개 | ✅ Merged |
| feature/entity-mileage-domain | Mileage + Competency Domain | 11개 | ✅ Merged |
| feature/entity-counseling-domain | Counseling Domain | 3개 예정 | 🔄 작업 중 |

---

## 🎯 머지 전략

### Fast-Forward 머지 방지
```bash
git merge feature/xxx --no-ff
```

**이유:**
- 명확한 기능 단위 구분
- 머지 히스토리 보존
- 되돌리기 쉬움

### 충돌 해결
1. 충돌 파일 확인
2. 수동으로 충돌 해결
3. 테스트 실행
4. 커밋 및 푸시

---

## 📋 체크리스트

### 커밋 전
- [ ] 빌드 성공 확인
- [ ] 코드 포맷팅 적용
- [ ] 불필요한 파일 제외 (.gitignore)
- [ ] 커밋 메시지 규칙 준수

### 머지 전
- [ ] 모든 변경사항 커밋
- [ ] main 최신 상태 확인
- [ ] 충돌 가능성 체크
- [ ] 테스트 통과 확인

### 푸시 전
- [ ] 로컬 빌드 성공
- [ ] 민감 정보 포함 여부 확인
- [ ] 원격 저장소 상태 확인

---

## 🔍 유용한 Git 명령어

### 브랜치 관리
```bash
# 모든 브랜치 보기
git branch -a

# 브랜치 생성 및 전환
git checkout -b feature/new-feature

# 브랜치 삭제
git branch -d feature/old-feature
```

### 커밋 관리
```bash
# 최근 커밋 메시지 수정
git commit --amend

# 커밋 로그 보기
git log --oneline -10

# 특정 파일 변경 이력
git log --follow -- path/to/file
```

### 원격 저장소
```bash
# 원격 저장소 확인
git remote -v

# 원격 변경사항 가져오기
git fetch origin

# 푸시
git push origin main
```

---

## 📌 주의사항

1. **main 브랜치에 직접 커밋 금지**
   - 항상 feature 브랜치에서 작업
   - 머지를 통해서만 main 업데이트

2. **작업 단위 명확히**
   - 도메인별로 브랜치 분리
   - 관련 없는 변경사항 혼재 금지

3. **커밋 메시지 상세히**
   - 무엇을 왜 변경했는지 명확히
   - 향후 유지보수를 위한 기록

4. **정기적인 푸시**
   - 작업 내용 백업
   - 팀원과 진행 상황 공유

