/**
 * Program Cancel Page Script
 * 프로그램 신청 취소 페이지 JavaScript
 */

// Mock Data
const mockApplications = [
    {
        id: 1,
        programId: 101,
        programTitle: '글로벌 리더십 워크샵',
        category: 'LEADERSHIP',
        status: 'PENDING',
        applicationDate: '2025-10-15',
        programStartDate: '2025-11-20',
        programEndDate: '2025-11-22',
        cancelable: true,
        daysUntilStart: 16
    },
    {
        id: 2,
        programId: 102,
        programTitle: '진로 탐색 멘토링',
        category: 'CAREER',
        status: 'APPROVED',
        applicationDate: '2025-10-10',
        programStartDate: '2025-11-10',
        programEndDate: '2025-12-10',
        cancelable: true,
        daysUntilStart: 6
    },
    {
        id: 3,
        programId: 103,
        programTitle: '해외 봉사활동 프로그램',
        category: 'VOLUNTEER',
        status: 'PENDING',
        applicationDate: '2025-10-20',
        programStartDate: '2025-12-01',
        programEndDate: '2025-12-15',
        cancelable: true,
        daysUntilStart: 27
    },
    {
        id: 4,
        programId: 104,
        programTitle: '창의적 문제해결 세미나',
        category: 'CREATIVE',
        status: 'APPROVED',
        applicationDate: '2025-10-05',
        programStartDate: '2025-11-05',
        programEndDate: '2025-11-06',
        cancelable: false,
        daysUntilStart: 1
    },
    {
        id: 5,
        programId: 105,
        programTitle: '국제 교류 프로그램',
        category: 'GLOBAL',
        status: 'APPROVED',
        applicationDate: '2025-09-30',
        programStartDate: '2025-11-08',
        programEndDate: '2025-11-15',
        cancelable: true,
        daysUntilStart: 4
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

// 상태 이름 매핑
const statusNames = {
    'PENDING': '승인대기',
    'APPROVED': '승인됨'
};

// 전역 변수
let currentApplications = [];
let filteredApplications = [];
let selectedApplicationIds = new Set();
let currentPage = 1;
const itemsPerPage = 10;

// 초기화
document.addEventListener('DOMContentLoaded', function() {
    loadApplications();
    initializeEventListeners();
    updateSummary();
});

// 신청 데이터 로드
function loadApplications() {
    // LocalStorage에서 데이터 로드
    const storedData = localStorage.getItem('programApplications');
    
    if (storedData) {
        currentApplications = JSON.parse(storedData);
    } else {
        currentApplications = [...mockApplications];
        localStorage.setItem('programApplications', JSON.stringify(currentApplications));
    }
    
    // 취소 가능 여부 재계산
    currentApplications = currentApplications
        .filter(app => app.status !== 'CANCELLED')
        .map(app => {
            const startDate = new Date(app.programStartDate);
            const today = new Date();
            const daysUntil = Math.ceil((startDate - today) / (1000 * 60 * 60 * 24));
            
            return {
                ...app,
                daysUntilStart: daysUntil,
                cancelable: app.status === 'PENDING' || (app.status === 'APPROVED' && daysUntil >= 3)
            };
        });
    
    filteredApplications = [...currentApplications];
    renderApplications();
}

// 이벤트 리스너 초기화
function initializeEventListeners() {
    // 필터 이벤트
    document.getElementById('categoryFilter').addEventListener('change', applyFilters);
    document.getElementById('statusFilter').addEventListener('change', applyFilters);
    document.getElementById('searchBtn').addEventListener('click', applyFilters);
    document.getElementById('searchInput').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            applyFilters();
        }
    });
    
    // 전체 선택 체크박스
    document.getElementById('selectAll').addEventListener('change', function(e) {
        const checkboxes = document.querySelectorAll('.application-checkbox');
        checkboxes.forEach(checkbox => {
            checkbox.checked = e.target.checked;
            const appId = parseInt(checkbox.dataset.appId);
            if (e.target.checked) {
                selectedApplicationIds.add(appId);
            } else {
                selectedApplicationIds.delete(appId);
            }
        });
        updateBulkActions();
    });
    
    // 일괄 취소 버튼
    document.getElementById('bulkCancelBtn').addEventListener('click', showBulkCancelModal);
    
    // 모달 확인 버튼
    document.getElementById('confirmCancelBtn').addEventListener('click', confirmCancel);
    document.getElementById('confirmBulkCancelBtn').addEventListener('click', confirmBulkCancel);
}

// 필터 적용
function applyFilters() {
    const categoryFilter = document.getElementById('categoryFilter').value;
    const statusFilter = document.getElementById('statusFilter').value;
    const searchKeyword = document.getElementById('searchInput').value.toLowerCase();
    
    filteredApplications = currentApplications.filter(app => {
        const matchesCategory = !categoryFilter || app.category === categoryFilter;
        const matchesStatus = !statusFilter || app.status === statusFilter;
        const matchesSearch = !searchKeyword || app.programTitle.toLowerCase().includes(searchKeyword);
        
        return matchesCategory && matchesStatus && matchesSearch;
    });
    
    currentPage = 1;
    renderApplications();
}

// 신청 목록 렌더링
function renderApplications() {
    const tableBody = document.getElementById('applicationsTableBody');
    const noDataMessage = document.getElementById('noDataMessage');
    const applicationsTable = document.getElementById('applicationsTable');
    const displayCount = document.getElementById('displayCount');
    
    if (filteredApplications.length === 0) {
        noDataMessage.style.display = 'block';
        applicationsTable.style.display = 'none';
        displayCount.textContent = '0';
        return;
    }
    
    noDataMessage.style.display = 'none';
    applicationsTable.style.display = 'block';
    displayCount.textContent = filteredApplications.length;
    
    // 페이지네이션 적용
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const pageApplications = filteredApplications.slice(startIndex, endIndex);
    
    tableBody.innerHTML = pageApplications.map(app => `
        <tr>
            <td>
                <input type="checkbox" class="form-check-input application-checkbox" 
                       data-app-id="${app.id}" ${!app.cancelable ? 'disabled' : ''}>
            </td>
            <td>
                <span class="status-badge ${app.status.toLowerCase()}">${statusNames[app.status]}</span>
            </td>
            <td>
                <span class="program-title-cell" onclick="viewProgramDetail(${app.programId})">
                    ${app.programTitle}
                </span>
            </td>
            <td>
                <span class="category-badge ${app.category.toLowerCase()}">${categoryNames[app.category]}</span>
            </td>
            <td>${formatDate(app.applicationDate)}</td>
            <td>${formatDate(app.programStartDate)} ~ ${formatDate(app.programEndDate)}</td>
            <td>
                <span class="cancelable-badge ${app.cancelable ? 'yes' : 'no'}">
                    <i class="fas fa-${app.cancelable ? 'check' : 'times'}"></i>
                    ${app.cancelable ? `가능 (${app.daysUntilStart}일 남음)` : '불가'}
                </span>
            </td>
            <td>
                <div class="action-buttons">
                    ${app.cancelable ? `
                        <button class="btn btn-sm btn-danger" onclick="showCancelModal(${app.id})">
                            <i class="fas fa-ban"></i> 취소
                        </button>
                    ` : `
                        <button class="btn btn-sm btn-secondary" disabled>
                            <i class="fas fa-lock"></i> 취소불가
                        </button>
                    `}
                </div>
            </td>
        </tr>
    `).join('');
    
    // 체크박스 이벤트 리스너 추가
    document.querySelectorAll('.application-checkbox').forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            const appId = parseInt(this.dataset.appId);
            if (this.checked) {
                selectedApplicationIds.add(appId);
            } else {
                selectedApplicationIds.delete(appId);
            }
            updateBulkActions();
        });
    });
    
    renderPagination();
}

// 페이지네이션 렌더링
function renderPagination() {
    const pagination = document.getElementById('pagination');
    const totalPages = Math.ceil(filteredApplications.length / itemsPerPage);
    
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
    const totalPages = Math.ceil(filteredApplications.length / itemsPerPage);
    if (page < 1 || page > totalPages) return;
    
    currentPage = page;
    renderApplications();
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

// 요약 정보 업데이트
function updateSummary() {
    const cancelableCount = currentApplications.filter(app => app.cancelable).length;
    const pendingCount = currentApplications.filter(app => app.status === 'PENDING').length;
    const approvedCount = currentApplications.filter(app => app.status === 'APPROVED').length;
    const nonCancelableCount = currentApplications.filter(app => !app.cancelable).length;
    
    document.getElementById('cancelableCount').textContent = cancelableCount;
    document.getElementById('pendingCount').textContent = pendingCount;
    document.getElementById('approvedCount').textContent = approvedCount;
    document.getElementById('nonCancelableCount').textContent = nonCancelableCount;
}

// 일괄 작업 버튼 업데이트
function updateBulkActions() {
    const bulkActions = document.getElementById('bulkActions');
    const selectAllCheckbox = document.getElementById('selectAll');
    
    if (selectedApplicationIds.size > 0) {
        bulkActions.style.display = 'flex';
        document.getElementById('bulkCancelBtn').innerHTML = `
            <i class="fas fa-ban"></i> 선택 항목 취소 (${selectedApplicationIds.size})
        `;
    } else {
        bulkActions.style.display = 'none';
        selectAllCheckbox.checked = false;
    }
}

// 취소 모달 표시
function showCancelModal(applicationId) {
    const application = currentApplications.find(app => app.id === applicationId);
    if (!application) return;
    
    document.getElementById('cancelProgramTitle').textContent = application.programTitle;
    document.getElementById('cancelApplicationDate').textContent = formatDate(application.applicationDate);
    document.getElementById('cancelProgramPeriod').textContent = 
        `${formatDate(application.programStartDate)} ~ ${formatDate(application.programEndDate)}`;
    document.getElementById('cancelCurrentStatus').innerHTML = 
        `<span class="status-badge ${application.status.toLowerCase()}">${statusNames[application.status]}</span>`;
    
    const modal = new bootstrap.Modal(document.getElementById('cancelModal'));
    modal.show();
    
    // 확인 버튼에 applicationId 저장
    document.getElementById('confirmCancelBtn').dataset.applicationId = applicationId;
}

// 일괄 취소 모달 표시
function showBulkCancelModal() {
    const selectedApps = currentApplications.filter(app => selectedApplicationIds.has(app.id));
    
    document.getElementById('bulkCancelCount').textContent = selectedApps.length;
    
    const listHTML = selectedApps.map(app => `
        <div class="selected-program-item">
            <i class="fas fa-file-alt"></i>
            <div>
                <strong>${app.programTitle}</strong>
                <br>
                <small class="text-muted">${formatDate(app.applicationDate)} 신청</small>
            </div>
        </div>
    `).join('');
    
    document.getElementById('selectedProgramsList').innerHTML = listHTML;
    
    const modal = new bootstrap.Modal(document.getElementById('bulkCancelModal'));
    modal.show();
}

// 취소 확인
function confirmCancel() {
    const applicationId = parseInt(document.getElementById('confirmCancelBtn').dataset.applicationId);
    const reason = document.getElementById('cancelReason').value;
    
    // 취소 처리
    const applicationIndex = currentApplications.findIndex(app => app.id === applicationId);
    if (applicationIndex !== -1) {
        currentApplications[applicationIndex].status = 'CANCELLED';
        currentApplications[applicationIndex].cancelDate = new Date().toISOString().split('T')[0];
        currentApplications[applicationIndex].cancelReason = reason;
        
        // LocalStorage 업데이트
        localStorage.setItem('programApplications', JSON.stringify(currentApplications));
        
        // 모달 닫기
        const modal = bootstrap.Modal.getInstance(document.getElementById('cancelModal'));
        modal.hide();
        
        // 성공 메시지
        showSuccessToast('신청이 취소되었습니다.');
        
        // 페이지 새로고침
        setTimeout(() => {
            loadApplications();
            updateSummary();
        }, 500);
    }
}

// 일괄 취소 확인
function confirmBulkCancel() {
    const cancelDate = new Date().toISOString().split('T')[0];
    
    selectedApplicationIds.forEach(appId => {
        const applicationIndex = currentApplications.findIndex(app => app.id === appId);
        if (applicationIndex !== -1) {
            currentApplications[applicationIndex].status = 'CANCELLED';
            currentApplications[applicationIndex].cancelDate = cancelDate;
        }
    });
    
    // LocalStorage 업데이트
    localStorage.setItem('programApplications', JSON.stringify(currentApplications));
    
    // 모달 닫기
    const modal = bootstrap.Modal.getInstance(document.getElementById('bulkCancelModal'));
    modal.hide();
    
    // 선택 초기화
    selectedApplicationIds.clear();
    
    // 성공 메시지
    showSuccessToast(`${selectedApplicationIds.size}개의 신청이 취소되었습니다.`);
    
    // 페이지 새로고침
    setTimeout(() => {
        loadApplications();
        updateSummary();
        updateBulkActions();
    }, 500);
}

// 프로그램 상세 보기
function viewProgramDetail(programId) {
    window.location.href = `/programs/${programId}`;
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
