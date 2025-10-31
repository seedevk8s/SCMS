# SCMS 문서 디렉토리

## 📁 문서 구조

```
docs/
├── README.md                          # 문서 디렉토리 안내 (이 파일)
├── 00-SESSION_RESUME.md               # 세션 재개 가이드 ⭐ 우선 읽기
└── 01-progress/
    └── 01-phase1-foundation.md        # Phase 1 진행사항
```

---

## 📖 문서 읽기 순서

### 1️⃣ 새로운 세션 시작 시
**`00-SESSION_RESUME.md`** 를 먼저 읽으세요!
- 현재 프로젝트 상태
- 완료된 작업
- 다음 작업 (ERD 설계)
- 세션 재개 시 사용할 멘트

### 2️⃣ 상세 진행사항 확인
**`01-progress/01-phase1-foundation.md`**
- Phase 1 완료 작업 상세
- 기술 스택
- 설계 원칙
- API 설계 가이드
- 다음 단계

---

## 🗂️ 문서 넘버링 규칙

### 디렉토리 넘버링
- `00-` : 최우선 문서 (세션 재개 가이드)
- `01-` : 진행사항 (Progress)
- `02-` : 설계 문서 (Design) - 추후 추가
- `03-` : API 문서 (API) - 추후 추가
- `04-` : 회의록 (Meeting) - 추후 추가

### 파일 넘버링
- `01-` : Phase 1
- `02-` : Phase 2
- `03-` : Phase 3
- `04-` : Phase 4

---

## 📋 향후 추가될 문서 (예정)

### 02-design/ (설계 문서)
```
02-design/
├── 01-erd-design.md               # ERD 설계
├── 02-api-specification.md        # API 명세
└── 03-security-design.md          # 보안 설계
```

### 03-api/ (API 문서)
```
03-api/
├── 01-auth-api.md                 # 인증 API
├── 02-program-api.md              # 비교과 프로그램 API
├── 03-mileage-api.md              # 마일리지 API
├── 04-competency-api.md           # 역량 진단 API
└── 05-counseling-api.md           # 상담 API
```

### 04-meeting/ (회의록)
```
04-meeting/
├── 01-kickoff-20251031.md         # 킥오프 회의
├── 02-sprint1-planning.md         # 스프린트 1 계획
└── ...
```

---

## 🔄 문서 업데이트 규칙

1. **Phase 완료 시**: `01-progress/` 에 새 문서 추가
2. **설계 완료 시**: `02-design/` 에 문서 추가
3. **API 개발 완료 시**: `03-api/` 에 문서 추가
4. **중요 회의 후**: `04-meeting/` 에 회의록 추가

---

## ⚠️ 정리 필요

### 삭제할 파일/폴더 (수동 삭제 필요)
```
❌ docs/SESSION_RESUME.md          # → 00-SESSION_RESUME.md 로 대체됨
❌ docs/progress/                   # → 01-progress/ 로 대체됨
   └── phase1-foundation.md        # → 01-phase1-foundation.md 로 대체됨
```

### IntelliJ에서 삭제하는 방법
1. 프로젝트 탐색기에서 파일/폴더 선택
2. Delete 키 또는 우클릭 → Delete
3. Safe delete 확인

---

## 📞 문서 관련 문의

문서 구조나 내용에 대한 질문이 있다면:
1. `00-SESSION_RESUME.md` 확인
2. `01-progress/01-phase1-foundation.md` 확인
3. 프로젝트 루트의 `MSA_ARCHITECTURE_GUIDE.md` 참고

---

**작성일**: 2025-10-31  
**최종 수정**: 2025-10-31
