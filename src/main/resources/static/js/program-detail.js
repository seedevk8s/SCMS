/* ==================== */
/* Program Detail Page JavaScript */
/* ==================== */

// Mock Data - program-list.js와 일치
const ProgramDetailData = {
    1: {
        id: 1,
        title: "2025-2학기 토익경시대회",
        subtitle: "[A] 2025-2학기 토익경시대회 [약학대학, 한의과대학, 간호대학]",
        center: "학습역량개발센터",
        applicationPeriod: "2025.09.15 ~ 2025.11.03",
        eventDate: "2025.11.16(화) 17:30 ~ 19:30",
        location: "온라인",
        currentParticipants: 10,
        maxParticipants: 25,
        badge: "입박",
        badgeColor: "#e74c3c",
        manager: {
            center: "학습역량개발센터",
            name: "홍길동",
            phone: "02-1234-5678",
            email: "toeic@example.com"
        }
    },
    2: {
        id: 2,
        title: "AI 역량 강화 워크샵",
        subtitle: "인공지능 기초부터 응용까지 배우는 워크샵",
        center: "진로개발센터",
        applicationPeriod: "2025.10.01 ~ 2025.10.31",
        eventDate: "2025.11.05(수) 14:00 ~ 17:00",
        location: "공학관 302호",
        currentParticipants: 25,
        maxParticipants: 25,
        badge: "마감",
        badgeColor: "#95a5a6",
        manager: {
            center: "진로개발센터",
            name: "김철수",
            phone: "02-1234-5679",
            email: "ai.workshop@example.com"
        }
    },
    3: {
        id: 3,
        title: "명칭스피치&이미지메이킹",
        subtitle: "효과적인 소통과 이미지 관리 방법을 배웁니다",
        center: "학생지원센터",
        applicationPeriod: "2025.09.20 ~ 2025.10.25",
        eventDate: "2025.10.28(월) 15:00 ~ 18:00",
        location: "학생회관 대강당",
        currentParticipants: 18,
        maxParticipants: 30,
        badge: "모집중",
        badgeColor: "#27ae60",
        manager: {
            center: "학생지원센터",
            name: "이영희",
            phone: "02-1234-5680",
            email: "speech@example.com"
        }
    },
    4: {
        id: 4,
        title: "2학기 계슈탑트 자기이해 성장캠프",
        subtitle: "자기 이해와 성장을 위한 캠프 프로그램",
        center: "상담센터",
        applicationPeriod: "2025.10.10 ~ 2025.11.15",
        eventDate: "2025.11.20(목) 10:00 ~ 16:00",
        location: "수련관 세미나실",
        currentParticipants: 12,
        maxParticipants: 20,
        badge: "입박",
        badgeColor: "#e74c3c",
        manager: {
            center: "상담센터",
            name: "박지성",
            phone: "02-1234-5681",
            email: "counseling@example.com"
        }
    },
    5: {
        id: 5,
        title: "창업 아이디어 경진대회",
        subtitle: "혁신적인 창업 아이디어를 발굴하는 대회",
        center: "창업지원센터",
        applicationPeriod: "2025.09.01 ~ 2025.11.30",
        eventDate: "2025.12.05(금) 13:00 ~ 18:00",
        location: "창업보육센터 컨퍼런스홀",
        currentParticipants: 8,
        maxParticipants: 15,
        badge: "모집중",
        badgeColor: "#27ae60",
        manager: {
            center: "창업지원센터",
            name: "최민수",
            phone: "02-1234-5682",
            email: "startup@example.com"
        }
    },
    6: {
        id: 6,
        title: "글로벌 리더십 프로그램",
        subtitle: "국제적 감각과 리더십을 키우는 프로그램",
        center: "국제교류센터",
        applicationPeriod: "2025.08.15 ~ 2025.09.30",
        eventDate: "2025.10.10(목) 09:00 ~ 18:00",
        location: "국제회의실",
        currentParticipants: 20,
        maxParticipants: 20,
        badge: "완료",
        badgeColor: "#3498db",
        manager: {
            center: "국제교류센터",
            name: "정수진",
            phone: "02-1234-5683",
            email: "global@example.com"
        }
    }
};

// Get Program ID from URL
function getProgramId() {
    const path = window.location.pathname;
    const match = path.match(/\/programs\/(\d+)/);
    return match ? parseInt(match[1]) : 1;
}

// Load Program Detail
function loadProgramDetail() {
    const programId = getProgramId();
    const program = ProgramDetailData[programId] || ProgramDetailData[1];
    
    // Update Page Content
    document.getElementById('programTitle').textContent = program.title;
    document.getElementById('programSubtitle').textContent = program.subtitle;
    document.getElementById('programCenter').textContent = program.center;
    document.getElementById('applicationPeriod').textContent = program.applicationPeriod;
    document.getElementById('eventDate').textContent = program.eventDate;
    document.getElementById('location').textContent = program.location;
    
    // Update Participants
    const currentElem = document.querySelector('#participants .current');
    if (currentElem) {
        currentElem.textContent = program.currentParticipants;
    }
    document.getElementById('participants').innerHTML = 
        `<strong class="current">${program.currentParticipants}</strong> / ${program.maxParticipants}명`;
    
    // Update Progress Bar
    const progress = Math.round((program.currentParticipants / program.maxParticipants) * 100);
    document.getElementById('progressPercentage').textContent = `${progress}%`;
    document.getElementById('progressBar').style.width = `${progress}%`;
    
    // Update Badge
    const badgeElem = document.querySelector('#programBadge .badge-lg');
    if (badgeElem) {
        badgeElem.textContent = program.badge;
        badgeElem.style.backgroundColor = program.badgeColor;
    }
    
    // Update Manager Info
    document.getElementById('managerCenter').textContent = program.manager.center;
    document.getElementById('managerName').textContent = program.manager.name;
    document.getElementById('managerPhone').textContent = program.manager.phone;
    document.getElementById('managerEmail').textContent = program.manager.email;
    
    console.log(`✅ Program Detail loaded: ${program.title}`);
}

// Tab Navigation
function initTabs() {
    const tabItems = document.querySelectorAll('.tab-item');
    const tabContents = document.querySelectorAll('.tab-content');
    
    tabItems.forEach((item, index) => {
        item.addEventListener('click', (e) => {
            e.preventDefault();
            
            // Remove active class from all tabs
            tabItems.forEach(tab => tab.classList.remove('active'));
            tabContents.forEach(content => content.classList.remove('active'));
            
            // Add active class to clicked tab
            item.classList.add('active');
            tabContents[index].classList.add('active');
        });
    });
}

// Apply Button Handler
function initApplyButtons() {
    const applyButtons = document.querySelectorAll('#applyButton, #applyButtonBottom');
    const programId = getProgramId();
    
    applyButtons.forEach(button => {
        button.addEventListener('click', () => {
            // 신청 페이지로 이동 (programId를 쿼리 파라미터로 전달)
            window.location.href = `/program/apply?id=${programId}`;
        });
    });
}

// Favorite Button Handler
function initFavoriteButton() {
    const favoriteBtn = document.querySelector('.btn-favorite');
    
    if (favoriteBtn) {
        favoriteBtn.addEventListener('click', () => {
            const icon = favoriteBtn.querySelector('i');
            if (icon.classList.contains('far')) {
                icon.classList.remove('far');
                icon.classList.add('fas');
                alert('즐겨찾기에 추가되었습니다.');
            } else {
                icon.classList.remove('fas');
                icon.classList.add('far');
                alert('즐겨찾기에서 제거되었습니다.');
            }
        });
    }
}

// Initialize on DOM Ready
document.addEventListener('DOMContentLoaded', function() {
    loadProgramDetail();
    initTabs();
    initApplyButtons();
    initFavoriteButton();
    
    console.log('🎨 Program Detail Page loaded successfully');
});
