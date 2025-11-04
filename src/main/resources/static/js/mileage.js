/* ==================== */
/* Mileage Page JavaScript */
/* ==================== */

// Mock Data
const MileageData = {
    summary: {
        current: 1250,      // 현재 보유
        earned: 2500,       // 총 적립
        used: 1250,         // 총 사용
        expiring: 300       // 소멸 예정
    },
    monthlyTrend: [
        { month: '07월', earned: 400, used: 100 },
        { month: '08월', earned: 500, used: 200 },
        { month: '09월', earned: 600, used: 300 },
        { month: '10월', earned: 700, used: 400 },
        { month: '11월', earned: 300, used: 250 }
    ],
    transactions: [
        {
            id: 1,
            type: 'EARN',
            amount: 500,
            description: '2025-2학기 토익경시대회 참여',
            date: '2025.11.01',
            balance: 1250
        },
        {
            id: 2,
            type: 'USE',
            amount: -100,
            description: '프로그램 신청 수수료',
            date: '2025.10.28',
            balance: 750
        },
        {
            id: 3,
            type: 'EARN',
            amount: 300,
            description: 'AI 역량 강화 워크샵 수료',
            date: '2025.10.25',
            balance: 850
        },
        {
            id: 4,
            type: 'EARN',
            amount: 100,
            description: '진로상담 이용',
            date: '2025.10.20',
            balance: 550
        },
        {
            id: 5,
            type: 'USE',
            amount: -50,
            description: '학내 카페테리아 이용',
            date: '2025.10.15',
            balance: 450
        },
        {
            id: 6,
            type: 'EARN',
            amount: 200,
            description: '명칭스피치 프로그램 참여',
            date: '2025.10.10',
            balance: 500
        },
        {
            id: 7,
            type: 'EARN',
            amount: 150,
            description: '역량진단 설문 참여',
            date: '2025.10.05',
            balance: 300
        },
        {
            id: 8,
            type: 'USE',
            amount: -100,
            description: '도서관 연체료 납부',
            date: '2025.09.28',
            balance: 150
        },
        {
            id: 9,
            type: 'EARN',
            amount: 400,
            description: '창업 아이디어 경진대회 참여',
            date: '2025.09.20',
            balance: 250
        },
        {
            id: 10,
            type: 'EARN',
            amount: 250,
            description: '자기이해 성장캠프 수료',
            date: '2025.09.15',
            balance: -150
        },
        {
            id: 11,
            type: 'USE',
            amount: -80,
            description: '학생복지 시설 이용권',
            date: '2025.09.10',
            balance: -400
        },
        {
            id: 12,
            type: 'EARN',
            amount: 300,
            description: '글로벌 리더십 프로그램 수료',
            date: '2025.09.05',
            balance: -320
        },
        {
            id: 13,
            type: 'EARN',
            amount: 100,
            description: '봉사활동 참여 (2시간)',
            date: '2025.08.28',
            balance: -620
        },
        {
            id: 14,
            type: 'USE',
            amount: -150,
            description: '프로그램 교재 구입',
            date: '2025.08.20',
            balance: -720
        },
        {
            id: 15,
            type: 'EARN',
            amount: 200,
            description: '학습역량 향상 워크샵 참여',
            date: '2025.08.15',
            balance: -570
        },
        {
            id: 16,
            type: 'EARN',
            amount: 50,
            description: '설문조사 참여',
            date: '2025.08.10',
            balance: -770
        },
        {
            id: 17,
            type: 'USE',
            amount: -70,
            description: '학내 식당 이용',
            date: '2025.08.05',
            balance: -820
        },
        {
            id: 18,
            type: 'EARN',
            amount: 350,
            description: '여름방학 특별 프로그램 참여',
            date: '2025.07.28',
            balance: -890
        },
        {
            id: 19,
            type: 'EARN',
            amount: 100,
            description: '심리상담 이용',
            date: '2025.07.20',
            balance: -1240
        },
        {
            id: 20,
            type: 'EARN',
            amount: 150,
            description: '진로탐색 프로그램 참여',
            date: '2025.07.15',
            balance: -1340
        }
    ]
};

// Current filter state
let currentFilter = 'ALL';
let displayLimit = 10;

// Load Summary Statistics
function loadSummary() {
    const { current, earned, used, expiring } = MileageData.summary;
    
    document.getElementById('currentBalance').textContent = `${current.toLocaleString()} P`;
    document.getElementById('totalEarned').textContent = `${earned.toLocaleString()} P`;
    document.getElementById('totalUsed').textContent = `${used.toLocaleString()} P`;
    document.getElementById('expiringSoon').textContent = `${expiring.toLocaleString()} P`;
    
    console.log('✅ Summary loaded:', MileageData.summary);
}

// Load Monthly Trend Chart
function loadMonthlyChart() {
    const chartBars = document.getElementById('chartBars');
    
    if (!chartBars) return;
    
    // Find max value for scaling
    const maxValue = Math.max(
        ...MileageData.monthlyTrend.map(m => Math.max(m.earned, m.used))
    );
    
    const html = MileageData.monthlyTrend.map(month => {
        const earnedHeight = (month.earned / maxValue) * 100;
        const usedHeight = (month.used / maxValue) * 100;
        
        return `
            <div class="chart-bar-group">
                <div class="chart-bars-pair">
                    <div class="chart-bar earned" 
                         style="height: ${earnedHeight}%"
                         title="${month.month} 적립: ${month.earned}P">
                    </div>
                    <div class="chart-bar used" 
                         style="height: ${usedHeight}%"
                         title="${month.month} 사용: ${month.used}P">
                    </div>
                </div>
                <div class="chart-bar-label">${month.month}</div>
            </div>
        `;
    }).join('');
    
    chartBars.innerHTML = html;
    
    console.log('✅ Chart loaded:', MileageData.monthlyTrend.length, 'months');
}

// Render Transaction Item
function renderTransaction(transaction) {
    const typeClass = transaction.type === 'EARN' ? 'earn' : 'use';
    const amountClass = transaction.amount > 0 ? 'earn' : 'use';
    const amountSign = transaction.amount > 0 ? '+' : '';
    
    return `
        <div class="transaction-item ${typeClass}">
            <div class="transaction-info">
                <div class="transaction-description">${transaction.description}</div>
                <div class="transaction-date">${transaction.date}</div>
            </div>
            <div class="transaction-amount">
                <div class="amount-value ${amountClass}">
                    ${amountSign}${Math.abs(transaction.amount).toLocaleString()} P
                </div>
                <div class="amount-balance">잔액: ${transaction.balance.toLocaleString()} P</div>
            </div>
        </div>
    `;
}

// Load Transactions
function loadTransactions() {
    const transactionList = document.getElementById('transactionList');
    
    if (!transactionList) return;
    
    // Filter transactions
    let filteredTransactions = MileageData.transactions;
    
    if (currentFilter !== 'ALL') {
        filteredTransactions = filteredTransactions.filter(t => t.type === currentFilter);
    }
    
    // Limit display
    const displayTransactions = filteredTransactions.slice(0, displayLimit);
    
    if (displayTransactions.length === 0) {
        transactionList.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-inbox"></i>
                <p>거래 내역이 없습니다.</p>
            </div>
        `;
        return;
    }
    
    const html = displayTransactions.map(t => renderTransaction(t)).join('');
    transactionList.innerHTML = html;
    
    // Show/hide load more button
    const loadMoreBtn = document.getElementById('loadMoreBtn');
    if (loadMoreBtn) {
        if (displayTransactions.length < filteredTransactions.length) {
            loadMoreBtn.style.display = 'inline-block';
        } else {
            loadMoreBtn.style.display = 'none';
        }
    }
    
    console.log(`✅ Transactions loaded: ${displayTransactions.length}/${filteredTransactions.length}`);
}

// Initialize Filter
function initFilter() {
    const filterSelect = document.getElementById('transactionFilter');
    
    if (filterSelect) {
        filterSelect.addEventListener('change', (e) => {
            currentFilter = e.target.value;
            displayLimit = 10; // Reset limit
            loadTransactions();
            console.log('🔍 Filter changed:', currentFilter);
        });
    }
}

// Initialize Load More Button
function initLoadMore() {
    const loadMoreBtn = document.getElementById('loadMoreBtn');
    
    if (loadMoreBtn) {
        loadMoreBtn.addEventListener('click', () => {
            displayLimit += 10;
            loadTransactions();
            console.log('📄 Load more:', displayLimit);
        });
    }
}

// Initialize on DOM Ready
document.addEventListener('DOMContentLoaded', function() {
    console.log('🎨 Mileage Page loaded');
    
    loadSummary();
    loadMonthlyChart();
    loadTransactions();
    initFilter();
    initLoadMore();
    
    console.log('✅ Mileage Page initialized successfully');
});
