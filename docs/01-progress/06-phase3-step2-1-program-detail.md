# Phase 3 - Step 2-1: 프로그램 상세 페이지 구현

**작업 일시**: 2025-11-03  
**브랜치**: `feature/program-detail-page`  
**진행률**: 진행 중

---

## 🎯 목표

프로그램 상세 페이지 구현 (레퍼런스: champ.woosuk.ac.kr)

---

## 📋 작업 체크리스트

- [x] 브랜치 생성
- [ ] HTML 파일 생성 (`program/detail.html`)
- [ ] CSS 파일 생성 (`program.css`)
- [ ] JavaScript 파일 생성 (`program-detail.js`)
- [ ] Controller 라우트 추가
- [ ] Mock 데이터 작성
- [ ] 테스트 실행
- [ ] 컨펌 및 커밋

---

## 🎨 UI 구조 (레퍼런스 기반)

### 1. 프로그램 헤더
- 프로그램 이미지/배너
- D-Day 배지 (D-1, D-2, 입박 등)
- 프로그램 제목
- 담당 센터/기관명
- 즐겨찾기 버튼

### 2. 신청 정보 박스
- **신청 기간**: 2025.09.29(일) ~ 2025.11.03(일)
- **운영 일시**: 2025.11.05(수) 13:00 ~ 15:00
- **장소**: 온라인 / 오프라인
- **모집 인원**: 18/55명
- **참가 진행률 바**: 32.7%
- **신청 버튼**: 큰 버튼 (모집중/마감 상태 표시)

### 3. 탭 메뉴
- 프로그램 소개
- 신청 현황
- 공지사항

### 4. 프로그램 상세 설명
- 프로그램 소개 내용
- 참여 대상
- 프로그램 일정
- 준비물
- 유의사항

### 5. 담당자 정보
- 담당 센터
- 담당자 이름
- 연락처
- 이메일

### 6. 하단 버튼
- 목록으로
- 신청하기

---

## 📊 Mock 데이터 구조

```javascript
const ProgramDetail = {
    id: 1,
    title: "2025-2학기 토익경시대회",
    description: "[A] 2025-2학기 토익경시대회 [약학대학, 한의과대학, 간호대학]",
    center: "외국어교육지원센터",
    category: "학습역량",
    
    // 일정 정보
    applicationStartDate: "2025.09.15",
    applicationEndDate: "2025.11.03",
    eventDate: "2025.11.16(화) 17:30 ~ 19:30",
    
    // 장소
    location: "온라인",
    locationType: "ONLINE", // ONLINE, OFFLINE, HYBRID
    
    // 모집 정보
    currentParticipants: 10,
    maxParticipants: 25,
    recruitmentStatus: "RECRUITING", // RECRUITING, CLOSED, IN_PROGRESS, COMPLETED
    
    // 배지
    badge: "입박",
    badgeColor: "#e74c3c",
    dDay: -1, // D-1
    
    // 조회수
    hits: 151,
    
    // 상세 내용
    content: `
        <h3>프로그램 소개</h3>
        <p>토익 점수 향상을 위한 경시대회입니다...</p>
        
        <h3>참여 대상</h3>
        <p>약학대학, 한의과대학, 간호대학 재학생</p>
        
        <h3>일정</h3>
        <ul>
            <li>신청 기간: 2025.09.15 ~ 2025.11.03</li>
            <li>대회 일시: 2025.11.16(화) 17:30 ~ 19:30</li>
        </ul>
    `,
    
    // 담당자
    manager: {
        center: "외국어교육지원센터",
        name: "홍길동",
        phone: "02-1234-5678",
        email: "contact@example.com"
    },
    
    // 첨부파일
    attachments: [
        {
            name: "토익경시대회_안내문.pdf",
            size: "1.2MB",
            url: "/files/toeic_guide.pdf"
        }
    ]
};
```

---

## 🎨 CSS 클래스 네이밍

```css
/* 프로그램 상세 페이지 */
.program-detail-page { }
.program-detail-header { }
.program-detail-banner { }
.program-detail-badge { }
.program-detail-title { }
.program-detail-info-box { }
.program-detail-tabs { }
.program-detail-content { }
.program-detail-manager { }
.program-detail-actions { }

/* 신청 정보 */
.application-info { }
.application-period { }
.application-status { }
.recruitment-progress { }

/* 버튼 */
.btn-apply { }
.btn-favorite { }
.btn-back-to-list { }
```

---

## 📂 파일 구조

```
src/main/resources/
├── templates/
│   └── program/
│       ├── list.html (✅ 완료)
│       └── detail.html (🔄 작업중)
└── static/
    ├── css/
    │   └── program.css (🔄 작업중)
    └── js/
        └── program-detail.js (🔄 작업중)
```

---

## 🔧 Controller 라우트

```java
/**
 * 프로그램 상세 페이지
 */
@GetMapping("/programs/{id}")
public String programDetail(@PathVariable Long id, Model model) {
    model.addAttribute("programId", id);
    return "program/detail";
}
```

---

## ✅ 테스트 URL

- 프로그램 목록: `http://localhost:8080/programs`
- 프로그램 상세: `http://localhost:8080/programs/1`

---

## 📝 다음 작업

1. HTML 파일 생성
2. CSS 스타일 작성
3. JavaScript Mock 데이터
4. Controller 수정
5. 테스트

---

**작성자**: Hojin  
**시작 시간**: 2025-11-03 18:30  
**예상 소요 시간**: 1시간
