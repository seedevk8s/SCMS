/* ==================== */
/* Dropdown Menu Fix - Inline in Header */
/* ==================== */

document.addEventListener('DOMContentLoaded', function() {
    // Inject critical dropdown CSS
    const style = document.createElement('style');
    style.textContent = `
        .dropdown-menu {
            position: absolute !important;
            top: 100% !important;
            left: 50% !important;
            transform: translateX(-50%) !important;
            background: white !important;
            border-radius: 8px !important;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15) !important;
            padding: 0.5rem 0 !important;
            margin-top: 1rem !important;
            min-width: 220px !important;
            opacity: 0 !important;
            visibility: hidden !important;
            transition: all 0.3s ease !important;
            z-index: 9999 !important;
            list-style: none !important;
            display: block !important;
        }
        .has-dropdown.active .dropdown-menu {
            opacity: 1 !important;
            visibility: visible !important;
            margin-top: 0.5rem !important;
        }
        .dropdown-item {
            display: flex !important;
            align-items: center !important;
            gap: 0.75rem !important;
            padding: 0.75rem 1.5rem !important;
            color: #2c3e50 !important;
            font-size: 0.95rem !important;
            transition: all 0.2s !important;
            white-space: nowrap !important;
            text-decoration: none !important;
        }
        .dropdown-item:hover {
            background: #ecf0f1 !important;
            color: #3498db !important;
        }
    `;
    document.head.appendChild(style);
    
    initSearch();
    initDropdown();
});

// Search Modal
function initSearch() {
    const searchBtn = document.getElementById('searchBtn');
    const searchModal = document.getElementById('searchModal');
    const closeSearchBtn = document.getElementById('closeSearchBtn');
    const searchInput = searchModal?.querySelector('.search-input');
    
    if (searchBtn && searchModal) {
        searchBtn.addEventListener('click', function() {
            searchModal.classList.add('active');
            searchInput?.focus();
        });
        
        closeSearchBtn?.addEventListener('click', function() {
            searchModal.classList.remove('active');
        });
        
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape' && searchModal.classList.contains('active')) {
                searchModal.classList.remove('active');
            }
        });
        
        searchModal.addEventListener('click', function(e) {
            if (e.target === searchModal) {
                searchModal.classList.remove('active');
            }
        });
    }
}

// Dropdown Menu
function initDropdown() {
    const dropdownItems = document.querySelectorAll('.has-dropdown');
    
    dropdownItems.forEach(item => {
        const toggle = item.querySelector('.dropdown-toggle');
        
        if (toggle) {
            toggle.addEventListener('click', function(e) {
                e.preventDefault();
                e.stopPropagation();
                
                dropdownItems.forEach(other => {
                    if (other !== item) {
                        other.classList.remove('active');
                    }
                });
                
                item.classList.toggle('active');
            });
        }
    });
    
    // 드롭다운 메뉴 아이템 클릭 시 페이지 이동 허용
    document.querySelectorAll('.dropdown-item').forEach(link => {
        link.addEventListener('click', function(e) {
            e.stopPropagation(); // 드롭다운 닫히는 것만 방지
            // 링크 기본 동작은 허용 (페이지 이동)
        });
    });
    
    document.addEventListener('click', function(e) {
        if (!e.target.closest('.has-dropdown')) {
            dropdownItems.forEach(item => {
                item.classList.remove('active');
            });
        }
    });
    
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            dropdownItems.forEach(item => {
                item.classList.remove('active');
            });
        }
    });
}

// Utility Functions
const Utils = {
    formatDate(dateString) {
        const date = new Date(dateString);
        return date.toLocaleDateString('ko-KR', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit'
        });
    },
    
    formatNumber(num) {
        return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    },
    
    calculateProgress(current, total) {
        if (total === 0) return 0;
        return Math.round((current / total) * 100);
    },
    
    truncateText(text, maxLength) {
        if (text.length <= maxLength) return text;
        return text.substring(0, maxLength) + '...';
    },
    
    showAlert(message, type = 'info') {
        alert(message);
    }
};

window.Utils = Utils;
