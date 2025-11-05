/**
 * Program Completion Page Script
 * 프로그램 수료 이력 페이지 JavaScript
 */

// Mock Data
const mockCompletions = [
    {
        id: 1,
        programId: 201,
        programTitle: '리더십 개발 프로그램',
        category: 'LEADERSHIP',
        programStartDate: '2025-08-01',
        programEndDate: '2025-08-30',
        completionDate: '2025-08-30',
        hours: 30,
        mileage: 100,
        attendance: 95,
        participation: 90,
        score: 92,
        comment: '적극적인 참여와 우수한 리더십을 보여주었습니다.'
    },
    {
        id: 2,
        programId: 202,
        programTitle: '진로 설계 워크샵',
        category: 'CAREER',
        programStartDate: '2025-07-10',
        programEndDate: '2025-07-25',
        completionDate: '2025-07-25',
        hours: 20,
        mileage: 80,
        attendance: 100,
        participation: 95,
        score: 95,
        comment: '진로 목표 설정과 실행 계획이 탁월했습니다.'
    },
    {
        id: 3,
        programId: 203,
        programTitle: '글로벌 문화 체험',
        category: 'GLOBAL',
        programStartDate: '2025-06-01',
        programEndDate: '2025-06-20',
        completionDate: '2025-06-20',
        hours: 40,
        mileage: 120,
        attendance: 90,
        participation: 88,
        score: 89,
        comment: '다문화 이해와 글로벌 역량이 향상되었습니다.'
    },
    {
        id: 4,
        programId: 204,
        programTitle: '지역사회 봉사활동',
        category: 'VOLUNTEER',
        programStartDate: '2025-05-01',
        programEndDate: '2025-05-15',
        completionDate: '2025-05-15',
        hours: 25,
        mileage: 90,
        attendance: 100,
        participation: 100,
        score: 98,
        comment: '헌신적인 봉사 활동으로 지역사회에 기여했습니다.'
    },
    {
        id: 5,
        programId: 205,
        programTitle: '창의적 사고 훈련',
        category: 'CREATIVE',
        programStartDate: '2025-04-10',
        programEndDate: '2025-04-30',
        completionDate: '2025-04-30',
        hours: 35,
        mileage: 110,
        attendance: 93,
        participation: 92,
        score: 91,
        comment: '창의적 문제 해결 능력이 뛰어났습니다.'
    },
    {
        id: 6,
        programId: 206,
        programTitle: '팀워크 향상 프로그램',
        category: 'LEADERSHIP',
        programStartDate: '2025-03-01',
        programEndDate: '2025-03-20',
        completionDate: '2025-03-20',
        hours: 28,
        mileage: 95,
        attendance: 96,
        participation: 94,
        score: 93,
        comment: '팀 협업에서 뛰어난 조율 능력을 보였습니다.'
    }
];

// 카테고리 이름 매핑
const categoryNames = {
    'LEADERSHIP': '리더십',
    'CAREER': '진로',
    'GLOBAL': '글로벌',
    'VOLUNTEER': '봉사',
    'CREATIVE': '창의'
};

// 전역 변수
let currentCompletions = [];
let filteredCompletions = [];
let currentPage = 1;
const itemsPerPage = 9;
let selectedCompletionForCertificate = null;

// 초기화
document.addEventListener('DOMContentLoaded', function() {
    loadCompletions();
    initializeEventListeners();
    updateAchievementStats();
    updateCategoryStats();
});

// 수료 데이터 로드
function loadCompletions() {
    // LocalStorage에서 데이터 로드
    const storedData = localStorage.getItem('programCompletions');
    
    if (storedData) {
        currentCompletions = JSON.parse(storedData);
    } else {
        currentCompletions = [...mockCompletions];
        localStorage.setItem('programCompletions', JSON.stringify(currentCompletions));
    }
    
    filteredCompletions = [...currentCompletions];
    renderCompletions();
}

// 이벤트 리스너 초기화
function initializeEventListeners() {
    // 필터 이벤트
    document.getElementById('categoryFilter').addEventListener('change', applyFilters);
    document.getElementById('yearFilter').addEventListener('change', applyFilters);
    document.getElementById('sortFilter').addEventListener('change', applyFilters);
    document.getElementById('searchBtn').addEventListener('click', applyFilters);
    document.getElementById('searchInput').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            applyFilters();
        }
    });
    
    // 카테고리 카드 클릭
    document.querySelectorAll('.category-card').forEach(card => {
        card.addEventListener('click', function() {
            const category = this.dataset.category;
            
            // 모든 카드에서 active 제거
            document.querySelectorAll('.category-card').forEach(c => c.classList.remove('active'));
            
            // 현재 카드 active 추가 또는 제거
            if (document.getElementById('categoryFilter').value === category) {
                document.getElementById('categoryFilter').value = '';
            } else {
                this.classList.add('active');
                document.getElementById('categoryFilter').value = category;
            }
            
            applyFilters();
        });
    });
    
    // 내보내기 버튼
    document.getElementById('exportBtn').addEventListener('click', exportCompletions);
    
    // 수료증 다운로드 버튼
    document.getElementById('downloadCertificateBtn').addEventListener('click', downloadCertificate);
    document.getElementById('printCertificateBtn').addEventListener('click', printCertificate);
    
    // 상세 모달의 수료증 보기 버튼
    document.getElementById('viewCertificateBtn').addEventListener('click', function() {
        // 상세 모달 닫기
        const detailModal = bootstrap.Modal.getInstance(document.getElementById('detailModal'));
        detailModal.hide();
        
        // 수료증 모달 열기
        setTimeout(() => {
            showCertificateModal(selectedCompletionForCertificate);
        }, 300);
    });
}

// 필터 적용
function applyFilters() {
    const categoryFilter = document.getElementById('categoryFilter').value;
    const yearFilter = document.getElementById('yearFilter').value;
    const sortFilter = document.getElementById('sortFilter').value;
    const searchKeyword = document.getElementById('searchInput').value.toLowerCase();
    
    filteredCompletions = currentCompletions.filter(completion => {
        const matchesCategory = !categoryFilter || completion.category === categoryFilter;
        const completionYear = new Date(completion.completionDate).getFullYear().toString();
        const matchesYear = !yearFilter || completionYear === yearFilter;
        const matchesSearch = !searchKeyword || completion.programTitle.toLowerCase().includes(searchKeyword);
        
        return matchesCategory && matchesYear && matchesSearch;
    });
    
    // 정렬 적용
    switch (sortFilter) {
        case 'recent':
            filteredCompletions.sort((a, b) => new Date(b.completionDate) - new Date(a.completionDate));
            break;
        case 'oldest':
            filteredCompletions.sort((a, b) => new Date(a.completionDate) - new Date(b.completionDate));
            break;
        case 'hours':
            filteredCompletions.sort((a, b) => b.hours - a.hours);
            break;
        case 'mileage':
            filteredCompletions.sort((a, b) => b.mileage - a.mileage);
            break;
    }
    
    currentPage = 1;
    renderCompletions();
}

// 수료 목록 렌더링
function renderCompletions() {
    const grid = document.getElementById('completionsGrid');
    const noDataMessage = document.getElementById('noDataMessage');
    const displayCount = document.getElementById('displayCount');
    
    if (filteredCompletions.length === 0) {
        noDataMessage.style.display = 'block';
        grid.style.display = 'none';
        displayCount.textContent = '0';
        return;
    }
    
    noDataMessage.style.display = 'none';
    grid.style.display = 'grid';
    displayCount.textContent = filteredCompletions.length;
    
    // 페이지네이션 적용
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const pageCompletions = filteredCompletions.slice(startIndex, endIndex);
    
    grid.innerHTML = pageCompletions.map(completion => `
        <div class="completion-card">
            <div class="completion-card-header">
                <span class="completion-badge">
                    <i class="fas fa-check-circle"></i>
                    수료 완료
                </span>
            </div>
            <div class="completion-card-body">
                <h5 class="program-title">${completion.programTitle}</h5>
                <div class="program-meta">
                    <div class="meta-item">
                        <i class="fas fa-folder"></i>
                        <span>${categoryNames[completion.category]}</span>
                    </div>
                    <div class="meta-item">
                        <i class="fas fa-calendar"></i>
                        <span>${formatDate(completion.programStartDate)} ~ ${formatDate(completion.programEndDate)}</span>
                    </div>
                    <div class="meta-item">
                        <i class="fas fa-graduation-cap"></i>
                        <span>수료일: ${formatDate(completion.completionDate)}</span>
                    </div>
                </div>
                <div class="program-stats">
                    <div class="stat-box">
                        <div class="stat-box-label">이수 시간</div>
                        <div class="stat-box-value">${completion.hours}시간</div>
                    </div>
                    <div class="stat-box">
                        <div class="stat-box-label">획득 마일리지</div>
                        <div class="stat-box-value">${completion.mileage}점</div>
                    </div>
                </div>
            </div>
            <div class="completion-card-footer">
                <button class="btn btn-outline-primary btn-sm" onclick="showDetailModal(${completion.id})">
                    <i class="fas fa-info-circle"></i> 상세보기
                </button>
                <button class="btn btn-primary btn-sm" onclick="showCertificateModal(${completion.id})">
                    <i class="fas fa-certificate"></i> 수료증
                </button>
            </div>
        </div>
    `).join('');
    
    renderPagination();
}

// 페이지네이션 렌더링
function renderPagination() {
    const pagination = document.getElementById('pagination');
    const totalPages = Math.ceil(filteredCompletions.length / itemsPerPage);
    
    if (totalPages <= 1) {
        pagination.innerHTML = '';
        return;
    }
    
    let paginationHTML = `
        <li class="page-item ${currentPage === 1 ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="changePage(${currentPage - 1}); return false;">
                <i class="fas fa-chevron-left"></i>
            </a>
        </li>
    `;
    
    for (let i = 1; i <= totalPages; i++) {
        if (i === 1 || i === totalPages || (i >= currentPage - 1 && i <= currentPage + 1)) {
            paginationHTML += `
                <li class="page-item ${i === currentPage ? 'active' : ''}">
                    <a class="page-link" href="#" onclick="changePage(${i}); return false;">${i}</a>
                </li>
            `;
        } else if (i === currentPage - 2 || i === currentPage + 2) {
            paginationHTML += `<li class="page-item disabled"><span class="page-link">...</span></li>`;
        }
    }
    
    paginationHTML += `
        <li class="page-item ${currentPage === totalPages ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="changePage(${currentPage + 1}); return false;">
                <i class="fas fa-chevron-right"></i>
            </a>
        </li>
    `;
    
    pagination.innerHTML = paginationHTML;
}

// 페이지 변경
function changePage(page) {
    const totalPages = Math.ceil(filteredCompletions.length / itemsPerPage);
    if (page < 1 || page > totalPages) return;
    
    currentPage = page;
    renderCompletions();
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

// 성과 통계 업데이트
function updateAchievementStats() {
    const totalCompletions = currentCompletions.length;
    const totalHours = currentCompletions.reduce((sum, c) => sum + c.hours, 0);
    const totalMileage = currentCompletions.reduce((sum, c) => sum + c.mileage, 0);
    const certificatesCount = currentCompletions.length;
    
    document.getElementById('totalCompletions').textContent = totalCompletions;
    document.getElementById('totalHours').textContent = totalHours;
    document.getElementById('totalMileage').textContent = totalMileage;
    document.getElementById('certificatesCount').textContent = certificatesCount;
}

// 분야별 통계 업데이트
function updateCategoryStats() {
    const categoryCounts = {
        'LEADERSHIP': 0,
        'CAREER': 0,
        'GLOBAL': 0,
        'VOLUNTEER': 0,
        'CREATIVE': 0
    };
    
    currentCompletions.forEach(completion => {
        categoryCounts[completion.category]++;
    });
    
    document.getElementById('leadershipCount').textContent = categoryCounts.LEADERSHIP;
    document.getElementById('careerCount').textContent = categoryCounts.CAREER;
    document.getElementById('globalCount').textContent = categoryCounts.GLOBAL;
    document.getElementById('volunteerCount').textContent = categoryCounts.VOLUNTEER;
    document.getElementById('creativeCount').textContent = categoryCounts.CREATIVE;
}

// 상세 모달 표시
function showDetailModal(completionId) {
    const completion = currentCompletions.find(c => c.id === completionId);
    if (!completion) return;
    
    selectedCompletionForCertificate = completionId;
    
    document.getElementById('detailProgramTitle').textContent = completion.programTitle;
    document.getElementById('detailCategory').textContent = categoryNames[completion.category];
    document.getElementById('detailProgramPeriod').textContent = 
        `${formatDate(completion.programStartDate)} ~ ${formatDate(completion.programEndDate)}`;
    document.getElementById('detailCompletionDate').textContent = formatDate(completion.completionDate);
    document.getElementById('detailHours').textContent = `${completion.hours}시간`;
    document.getElementById('detailMileage').textContent = `${completion.mileage}점`;
    document.getElementById('detailAttendance').textContent = `${completion.attendance}%`;
    document.getElementById('detailParticipation').textContent = `${completion.participation}%`;
    document.getElementById('detailScore').textContent = `${completion.score}점`;
    document.getElementById('detailComment').textContent = completion.comment;
    
    const modal = new bootstrap.Modal(document.getElementById('detailModal'));
    modal.show();
}

// 수료증 모달 표시
function showCertificateModal(completionId) {
    const completion = currentCompletions.find(c => c.id === completionId);
    if (!completion) return;
    
    selectedCompletionForCertificate = completionId;
    
    document.getElementById('certificateProgramTitle').textContent = completion.programTitle;
    document.getElementById('certificateProgramPeriod').textContent = 
        `${formatDate(completion.programStartDate)} ~ ${formatDate(completion.programEndDate)}`;
    document.getElementById('certificateHours').textContent = `${completion.hours}시간`;
    document.getElementById('certificateCompletionDate').textContent = formatDate(completion.completionDate);
    document.getElementById('certificateMileage').textContent = `${completion.mileage}점`;
    
    const today = new Date();
    const issueDateStr = `${today.getFullYear()}년 ${today.getMonth() + 1}월 ${today.getDate()}일`;
    document.getElementById('certificateIssueDate').textContent = issueDateStr;
    
    const modal = new bootstrap.Modal(document.getElementById('certificateModal'));
    modal.show();
}

// 수료증 다운로드
function downloadCertificate() {
    showSuccessToast('수료증 다운로드 기능은 추후 구현 예정입니다.');
}

// 수료증 인쇄
function printCertificate() {
    window.print();
}

// 이력 내보내기
function exportCompletions() {
    const csvContent = generateCSV();
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    
    link.setAttribute('href', url);
    link.setAttribute('download', `수료이력_${new Date().toISOString().split('T')[0]}.csv`);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    
    showSuccessToast('수료 이력이 내보내기 되었습니다.');
}

// CSV 생성
function generateCSV() {
    const headers = ['프로그램명', '분야', '시작일', '종료일', '수료일', '이수시간', '마일리지', '출석률', '참여도', '평가점수'];
    const rows = currentCompletions.map(c => [
        c.programTitle,
        categoryNames[c.category],
        c.programStartDate,
        c.programEndDate,
        c.completionDate,
        c.hours,
        c.mileage,
        c.attendance,
        c.participation,
        c.score
    ]);
    
    const csvRows = [headers, ...rows];
    return csvRows.map(row => row.join(',')).join('\n');
}

// 날짜 포맷팅
function formatDate(dateString) {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

// 성공 토스트 표시
function showSuccessToast(message) {
    document.getElementById('successToastMessage').textContent = message;
    const toast = new bootstrap.Toast(document.getElementById('successToast'));
    toast.show();
}
