/**
 * Program List Page JavaScript
 * Fragment 적용 테스트용
 */

// Mock 데이터
const MockProgramData = {
    programs: [
        {
            id: 1,
            title: '2025-2학기 토익경시대회',
            description: '토익 점수 향상을 위한 경시대회입니다.',
            center: '학습역량개발센터',
            category: '학습역량',
            startDate: '2025.09.15',
            endDate: '2025.11.03',
            eventDate: '2025.11.16(화) 17:30 ~ 19:30',
            currentParticipants: 10,
            maxParticipants: 25,
            hits: 151,
            badge: '입박',
            badgeColor: '#e74c3c',
            status: 'RECRUITING'
        },
        {
            id: 2,
            title: 'AI 역량 강화 워크샵',
            description: '인공지능 기초부터 응용까지 배우는 워크샵',
            center: '진로개발센터',
            category: '진로지도',
            startDate: '2025.10.01',
            endDate: '2025.10.31',
            eventDate: '2025.11.05(수) 14:00 ~ 17:00',
            currentParticipants: 25,
            maxParticipants: 25,
            hits: 203,
            badge: '마감',
            badgeColor: '#95a5a6',
            status: 'IN_PROGRESS'
        },
        {
            id: 3,
            title: '명칭스피치&이미지메이킹',
            description: '효과적인 소통과 이미지 관리 방법을 배웁니다.',
            center: '학생지원센터',
            category: '공감소통역량',
            startDate: '2025.09.20',
            endDate: '2025.10.25',
            eventDate: '2025.10.28(월) 15:00 ~ 18:00',
            currentParticipants: 18,
            maxParticipants: 30,
            hits: 176,
            badge: '모집중',
            badgeColor: '#27ae60',
            status: 'RECRUITING'
        },
        {
            id: 4,
            title: '2학기 계슈탑트 자기이해 성장캠프',
            description: '자기 이해와 성장을 위한 캠프 프로그램',
            center: '상담센터',
            category: '자기관리역량',
            startDate: '2025.10.10',
            endDate: '2025.11.15',
            eventDate: '2025.11.20(목) 10:00 ~ 16:00',
            currentParticipants: 12,
            maxParticipants: 20,
            hits: 134,
            badge: '입박',
            badgeColor: '#e74c3c',
            status: 'RECRUITING'
        },
        {
            id: 5,
            title: '창업 아이디어 경진대회',
            description: '혁신적인 창업 아이디어를 발굴하는 대회',
            center: '창업지원센터',
            category: '문제해결역량',
            startDate: '2025.09.01',
            endDate: '2025.11.30',
            eventDate: '2025.12.05(금) 13:00 ~ 18:00',
            currentParticipants: 8,
            maxParticipants: 15,
            hits: 289,
            badge: '모집중',
            badgeColor: '#27ae60',
            status: 'RECRUITING'
        },
        {
            id: 6,
            title: '글로벌 리더십 프로그램',
            description: '국제적 감각과 리더십을 키우는 프로그램',
            center: '국제교류센터',
            category: '공감소통역량',
            startDate: '2025.08.15',
            endDate: '2025.09.30',
            eventDate: '2025.10.10(목) 09:00 ~ 18:00',
            currentParticipants: 20,
            maxParticipants: 20,
            hits: 412,
            badge: '완료',
            badgeColor: '#3498db',
            status: 'COMPLETED'
        }
    ]
};

// 진행률 계산
function calculateProgress(current, max) {
    if (max === 0) return 0;
    return Math.round((current / max) * 100);
}

// 상태 텍스트 반환
function getStatusText(status) {
    const statusMap = {
        'RECRUITING': '모집중',
        'IN_PROGRESS': '진행중',
        'COMPLETED': '완료',
        'CANCELLED': '취소됨'
    };
    return statusMap[status] || status;
}

// 프로그램 카드 렌더링
function renderProgramCard(program) {
    const progress = calculateProgress(program.currentParticipants, program.maxParticipants);
    
    return `
        <div class="program-card">
            <div class="program-image">
                <div class="program-placeholder">
                    <i class="fas fa-graduation-cap fa-3x"></i>
                </div>
            </div>
            <div class="program-content">
                <div class="program-header">
                    <span class="program-badge" style="background-color: ${program.badgeColor}">
                        ${program.badge}
                    </span>
                    <span class="program-participants">
                        <i class="fas fa-users"></i> ${program.currentParticipants}/${program.maxParticipants}
                    </span>
                </div>
                <h3 class="program-title">${program.title}</h3>
                <p class="program-description">${program.description}</p>
                <div class="program-info">
                    <div class="info-item">
                        <i class="fas fa-calendar"></i>
                        <span>신청: ${program.startDate} ~ ${program.endDate}</span>
                    </div>
                    <div class="info-item">
                        <i class="fas fa-clock"></i>
                        <span>운영: ${program.eventDate}</span>
                    </div>
                </div>
                <div class="program-progress">
                    <div class="progress-bar">
                        <div class="progress-fill" style="width: ${progress}%"></div>
                    </div>
                    <span class="progress-text">${progress}%</span>
                </div>
                <div class="program-footer">
                    <span class="program-hits">
                        <i class="fas fa-eye"></i> HITS ${program.hits}
                    </span>
                    <a href="/programs/${program.id}" class="btn btn-primary btn-sm">
                        상세보기 <i class="fas fa-arrow-right"></i>
                    </a>
                </div>
            </div>
        </div>
    `;
}

// 프로그램 목록 로드
function loadPrograms() {
    const programGrid = document.getElementById('programGrid');
    
    if (!programGrid) {
        console.error('programGrid element not found');
        return;
    }
    
    // Mock 데이터로 렌더링
    const html = MockProgramData.programs.map(program => 
        renderProgramCard(program)
    ).join('');
    
    programGrid.innerHTML = html;
    
    console.log('✅ Programs loaded:', MockProgramData.programs.length);
}

// 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', function() {
    console.log('🎨 Program List Page - Fragment 적용 테스트');
    loadPrograms();
    
    // 검색 버튼 이벤트
    const searchBtn = document.getElementById('searchBtn');
    if (searchBtn) {
        searchBtn.addEventListener('click', function() {
            alert('검색 기능은 Phase 5에서 구현됩니다.');
        });
    }
});
