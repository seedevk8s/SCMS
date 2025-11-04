/**
 * Program History Page
 * 프로그램 신청 이력 관리
 */

// Mock Data
const mockApplications = [
    {
        id: 1,
        programId: 1,
        programTitle: '2025-2학기 토익경시대회',
        programType: 'academic',
        center: '외국어교육지원센터',
        eventDate: '2025.11.16(화) 17:30 ~ 19:30',
        location: '중앙관 501호',
        mileage: 50,
        applicationDate: '2025.10.20 14:30',
        status: 'APPROVED',
        rejectionReason: null
    },
    {
        id: 2,
        programId: 2,
        programTitle: '2025 진로탐색 워크샵',
        programType: 'career',
        center: '학생상담센터',
        eventDate: '2025.11.20(수) ~ 2025.11.22(금)',
        location: '대강당',
        mileage: 100,
        applicationDate: '2025.10.25 09:15',
        status: 'PENDING',
        rejectionReason: null
    },
    {
        id: 3,
        programId: 3,
        programTitle: '스트레스 관리 프로그램',
        programType: 'counseling',
        center: '학생상담센터',
        eventDate: '2025.11.10(월) 14:00 ~ 16:00',
        location: '상담센터 2층',
        mileage: 30,
        applicationDate: '2025.10.15 11:20',
        status: 'REJECTED',
        rejectionReason: '정원 초과로 인한 불가피한 선발 탈락입니다. 차기 프로그램을 이용해주시기 바랍니다.'
    },
    {
        id: 4,
        programId: 4,
        programTitle: '창업 아이디어 경진대회',
        programType: 'career',
        center: '창업지원센터',
        eventDate: '2025.12.01(일) 13:00 ~ 18:00',
        location: '창업관',
        mileage: 150,
        applicationDate: '2025.10.28 16:45',
        status: 'CANCELLED',
        rejectionReason: null
    },
    {
        id: 5,
        programId: 5,
        programTitle: 'Python 기초 프로그래밍',
        programType: 'academic',
        center: 'SW교육지원센터',
        eventDate: '2025.11.25(화) ~ 2025.12.20(금) 매주 화,목',
        location: '전산실습실',
        mileage: 80,
        applicationDate: '2025.10.30 10:00',
        status: 'APPROVED',
        rejectionReason: null
    }
];

// Global State
let currentApplications = [...mockApplications];
let currentPage = 1;
const itemsPerPage = 5;
let selectedApplicationId = null;

// Status Configuration
const statusConfig = {
    PENDING: { label: '대기중', icon: 'fa-clock' },
    APPROVED: { label: '승인됨', icon: 'fa-check-circle' },
    REJECTED: { label: '반려됨', icon: 'fa-times-circle' },
    CANCELLED: { label: '취소됨', icon: 'fa-ban' }
};

const typeConfig = {
    academic: { label: '학습역량', class: 'academic' },
    career: { label: '진로지도', class: 'career' },
    counseling: { label: '심리상담', class: 'counseling' }
};

// Initialize
document.addEventListener('DOMContentLoaded', function() {
    initializePage();
    setupEventListeners();
});

function initializePage() {
    updateSummary();
    renderApplications();
    updatePagination();
}

function setupEventListeners() {
    // Filter listeners
    document.getElementById('statusFilter').addEventListener('change', applyFilters);
    document.getElementById('periodFilter').addEventListener('change', applyFilters);
    
    // Search listener
    document.getElementById('searchBtn').addEventListener('click', performSearch);
    document.getElementById('searchInput').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            performSearch();
        }
    });
    
    // Modal listeners
    const cancelModal = document.getElementById('cancelModal');
    if (cancelModal) {
        cancelModal.addEventListener('hidden.bs.modal', function() {
            selectedApplicationId = null;
        });
    }
    
    document.getElementById('confirmCancelBtn')?.addEventListener('click', confirmCancel);
}

// Update Summary Cards
function updateSummary() {
    const pending = mockApplications.filter(app => app.status === 'PENDING').length;
    const approved = mockApplications.filter(app => app.status === 'APPROVED').length;
    const rejected = mockApplications.filter(app => app.status === 'REJECTED').length;
    const total = mockApplications.length;
    
    document.getElementById('pendingCount').textContent = pending;
    document.getElementById('approvedCount').textContent = approved;
    document.getElementById('rejectedCount').textContent = rejected;
    document.getElementById('totalCount').textContent = total;
}

// Apply Filters
function applyFilters() {
    const statusFilter = document.getElementById('statusFilter').value;
    const periodFilter = document.getElementById('periodFilter').value;
    
    let filtered = [...mockApplications];
    
    // Status filter
    if (statusFilter) {
        filtered = filtered.filter(app => app.status === statusFilter);
    }
    
    // Period filter
    if (periodFilter) {
        const now = new Date();
        const filterDate = new Date();
        
        switch(periodFilter) {
            case '1month':
                filterDate.setMonth(now.getMonth() - 1);
                break;
            case '3months':
                filterDate.setMonth(now.getMonth() - 3);
                break;
            case '6months':
                filterDate.setMonth(now.getMonth() - 6);
                break;
            case '1year':
                filterDate.setFullYear(now.getFullYear() - 1);
                break;
        }
        
        filtered = filtered.filter(app => {
            const appDate = new Date(app.applicationDate);
            return appDate >= filterDate;
        });
    }
    
    currentApplications = filtered;
    currentPage = 1;
    renderApplications();
    updatePagination();
}

// Perform Search
function performSearch() {
    const searchText = document.getElementById('searchInput').value.toLowerCase().trim();
    
    if (!searchText) {
        currentApplications = [...mockApplications];
    } else {
        currentApplications = mockApplications.filter(app => 
            app.programTitle.toLowerCase().includes(searchText) ||
            app.center.toLowerCase().includes(searchText)
        );
    }
    
    currentPage = 1;
    renderApplications();
    updatePagination();
}

// Render Applications
function renderApplications() {
    const grid = document.getElementById('applicationsGrid');
    const noDataMessage = document.getElementById('noDataMessage');
    
    if (currentApplications.length === 0) {
        grid.style.display = 'none';
        noDataMessage.style.display = 'block';
        return;
    }
    
    grid.style.display = 'flex';
    noDataMessage.style.display = 'none';
    
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const pageApplications = currentApplications.slice(startIndex, endIndex);
    
    grid.innerHTML = pageApplications.map(app => createApplicationCard(app)).join('');
    
    // Attach event listeners
    attachCardEventListeners();
}

// Create Application Card
function createApplicationCard(app) {
    const status = statusConfig[app.status];
    const type = typeConfig[app.programType];
    
    const rejectionReasonHTML = app.status === 'REJECTED' && app.rejectionReason ? `
        <div class="rejection-reason">
            <div class="reason-label">
                <i class="fas fa-exclamation-circle"></i> 반려 사유
            </div>
            <div class="reason-text">${app.rejectionReason}</div>
        </div>
    ` : '';
    
    const actionsHTML = app.status === 'PENDING' ? `
        <button class="btn btn-outline-danger btn-sm cancel-btn" data-id="${app.id}">
            <i class="fas fa-times"></i> 신청 취소
        </button>
    ` : '';
    
    return `
        <div class="application-card status-${app.status.toLowerCase()}" data-id="${app.id}">
            <div class="application-header">
                <div class="application-title-area">
                    <span class="program-type-badge ${type.class}">${type.label}</span>
                    <h3 class="application-title">${app.programTitle}</h3>
                </div>
                <div class="application-status ${app.status.toLowerCase()}">
                    <i class="fas ${status.icon}"></i>
                    ${status.label}
                </div>
            </div>
            
            <div class="application-body">
                <div class="info-item">
                    <span class="info-label">운영 센터</span>
                    <span class="info-value">
                        <i class="fas fa-building"></i> ${app.center}
                    </span>
                </div>
                
                <div class="info-item">
                    <span class="info-label">운영 일시</span>
                    <span class="info-value">
                        <i class="fas fa-calendar-alt"></i> ${app.eventDate}
                    </span>
                </div>
                
                <div class="info-item">
                    <span class="info-label">장소</span>
                    <span class="info-value">
                        <i class="fas fa-map-marker-alt"></i> ${app.location}
                    </span>
                </div>
                
                <div class="info-item">
                    <span class="info-label">마일리지</span>
                    <span class="info-value mileage-value">
                        <i class="fas fa-star"></i> ${app.mileage}점
                    </span>
                </div>
            </div>
            
            ${rejectionReasonHTML}
            
            <div class="application-footer">
                <div class="application-date">
                    <i class="fas fa-clock"></i> 신청일: ${app.applicationDate}
                </div>
                <div class="application-actions">
                    <a href="/program/detail?id=${app.programId}" class="btn btn-outline-primary btn-sm">
                        <i class="fas fa-info-circle"></i> 프로그램 상세
                    </a>
                    ${actionsHTML}
                </div>
            </div>
        </div>
    `;
}

// Attach Card Event Listeners
function attachCardEventListeners() {
    // Cancel button listeners
    document.querySelectorAll('.cancel-btn').forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            const appId = parseInt(this.getAttribute('data-id'));
            showCancelModal(appId);
        });
    });
}

// Show Cancel Modal
function showCancelModal(appId) {
    const application = mockApplications.find(app => app.id === appId);
    if (!application) return;
    
    selectedApplicationId = appId;
    
    document.getElementById('cancelProgramTitle').textContent = application.programTitle;
    document.getElementById('cancelApplicationDate').textContent = 
        `신청일: ${application.applicationDate}`;
    
    const modal = new bootstrap.Modal(document.getElementById('cancelModal'));
    modal.show();
}

// Confirm Cancel
function confirmCancel() {
    if (!selectedApplicationId) return;
    
    const index = mockApplications.findIndex(app => app.id === selectedApplicationId);
    if (index !== -1) {
        mockApplications[index].status = 'CANCELLED';
    }
    
    // Update current applications if filtered
    const currentIndex = currentApplications.findIndex(app => app.id === selectedApplicationId);
    if (currentIndex !== -1) {
        currentApplications[currentIndex].status = 'CANCELLED';
    }
    
    // Close modal
    const modal = bootstrap.Modal.getInstance(document.getElementById('cancelModal'));
    modal.hide();
    
    // Update UI
    updateSummary();
    renderApplications();
    
    // Show success message
    showSuccessMessage('신청이 취소되었습니다.');
}

// Update Pagination
function updatePagination() {
    const totalPages = Math.ceil(currentApplications.length / itemsPerPage);
    const pagination = document.getElementById('pagination');
    
    if (totalPages <= 1) {
        pagination.innerHTML = '';
        return;
    }
    
    let html = '';
    
    // Previous button
    html += `
        <li class="page-item ${currentPage === 1 ? 'disabled' : ''}">
            <a class="page-link" href="#" data-page="${currentPage - 1}">
                <i class="fas fa-chevron-left"></i>
            </a>
        </li>
    `;
    
    // Page numbers
    for (let i = 1; i <= totalPages; i++) {
        if (
            i === 1 || 
            i === totalPages || 
            (i >= currentPage - 1 && i <= currentPage + 1)
        ) {
            html += `
                <li class="page-item ${i === currentPage ? 'active' : ''}">
                    <a class="page-link" href="#" data-page="${i}">${i}</a>
                </li>
            `;
        } else if (i === currentPage - 2 || i === currentPage + 2) {
            html += `<li class="page-item disabled"><span class="page-link">...</span></li>`;
        }
    }
    
    // Next button
    html += `
        <li class="page-item ${currentPage === totalPages ? 'disabled' : ''}">
            <a class="page-link" href="#" data-page="${currentPage + 1}">
                <i class="fas fa-chevron-right"></i>
            </a>
        </li>
    `;
    
    pagination.innerHTML = html;
    
    // Attach pagination event listeners
    pagination.querySelectorAll('a.page-link').forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const page = parseInt(this.getAttribute('data-page'));
            if (page > 0 && page <= totalPages && page !== currentPage) {
                currentPage = page;
                renderApplications();
                updatePagination();
                window.scrollTo({ top: 0, behavior: 'smooth' });
            }
        });
    });
}

// Show Success Message
function showSuccessMessage(message) {
    // Create toast element
    const toast = document.createElement('div');
    toast.className = 'toast-message success';
    toast.innerHTML = `
        <i class="fas fa-check-circle"></i>
        <span>${message}</span>
    `;
    
    document.body.appendChild(toast);
    
    // Show toast
    setTimeout(() => toast.classList.add('show'), 100);
    
    // Hide and remove toast
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

// Toast styles (to be added to CSS if not exists)
const style = document.createElement('style');
style.textContent = `
.toast-message {
    position: fixed;
    top: 20px;
    right: 20px;
    background: white;
    padding: 1rem 1.5rem;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
    display: flex;
    align-items: center;
    gap: 0.75rem;
    opacity: 0;
    transform: translateX(100%);
    transition: all 0.3s ease;
    z-index: 9999;
}

.toast-message.show {
    opacity: 1;
    transform: translateX(0);
}

.toast-message.success {
    border-left: 4px solid #27ae60;
}

.toast-message i {
    font-size: 1.25rem;
    color: #27ae60;
}
`;
document.head.appendChild(style);
