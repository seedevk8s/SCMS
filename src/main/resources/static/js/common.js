/* ==================== */
/* Common JavaScript */
/* ==================== */

// DOM Ready
document.addEventListener('DOMContentLoaded', function() {
    initSearch();
});

// Search Modal
function initSearch() {
    const searchBtn = document.getElementById('searchBtn');
    const searchModal = document.getElementById('searchModal');
    const closeSearchBtn = document.getElementById('closeSearchBtn');
    const searchInput = searchModal?.querySelector('.search-input');
    
    if (searchBtn && searchModal) {
        // Open search modal
        searchBtn.addEventListener('click', function() {
            searchModal.classList.add('active');
            searchInput?.focus();
        });
        
        // Close search modal
        closeSearchBtn?.addEventListener('click', function() {
            searchModal.classList.remove('active');
        });
        
        // Close on ESC key
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape' && searchModal.classList.contains('active')) {
                searchModal.classList.remove('active');
            }
        });
        
        // Close on backdrop click
        searchModal.addEventListener('click', function(e) {
            if (e.target === searchModal) {
                searchModal.classList.remove('active');
            }
        });
    }
}

// Utility Functions
const Utils = {
    // Format date
    formatDate(dateString) {
        const date = new Date(dateString);
        return date.toLocaleDateString('ko-KR', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit'
        });
    },
    
    // Format number with commas
    formatNumber(num) {
        return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    },
    
    // Calculate progress percentage
    calculateProgress(current, total) {
        if (total === 0) return 0;
        return Math.round((current / total) * 100);
    },
    
    // Truncate text
    truncateText(text, maxLength) {
        if (text.length <= maxLength) return text;
        return text.substring(0, maxLength) + '...';
    },
    
    // Show alert message
    showAlert(message, type = 'info') {
        alert(message); // Simple alert for now
        // TODO: Implement better notification system
    }
};

// Export for use in other scripts
window.Utils = Utils;
