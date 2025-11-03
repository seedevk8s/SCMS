/* ==================== */
/* Main Page JavaScript */
/* ==================== */

// Mock Data
const MockData = {
    programs1: [
        {
            id: 1,
            title: '2025-2학기 토익경시대회',
            description: '[A] 2025-2학기 토익경시대회 [약학대학, 한의과대학, 간호대학]',
            center: '외국어교육지원센터',
            category: '입박',
            startDate: '2025.09.15',
            endDate: '2025.11.03',
            eventDate: '2025.11.16(화) 17:30 ~ 19:30',
            currentParticipants: 10,
            maxParticipants: 25,
            hits: 151,
            badge: '입박',
            badgeColor: '#e74c3c'
        },
        {
            id: 2,
            title: '2025-2학기 토익경시대회',
            description: '[B] 2025-2학기 토익경시대회 [그 외 대학]',
            center: '외국어교육지원센터',
            category: '입박',
            startDate: '2025.09.15',
            endDate: '2025.11.03',
            eventDate: '2025.11.16(화) 17:30 ~ 19:30',
            currentParticipants: 5,
            maxParticipants: 25,
            hits: 151,
            badge: '입박',
            badgeColor: '#e74c3c'
        },
        {
            id: 3,
            title: '명칭스피치&이미지메이킹 1회차',
            description: '2025년 2학기 명칭스피치&이미지메이킹 1회차',
            center: '대학일자리본부',
            category: '입박',
            startDate: '2025.09.29',
            endDate: '2025.11.03',
            eventDate: '2025.11.05(수) 13:00 ~ 15:00',
            currentParticipants: 17,
            maxParticipants: 55,
            hits: 157,
            badge: '입박',
            badgeColor: '#e74c3c'
        },
        {
            id: 4,
            title: '2학기 계슈탑트 자기이해 성장캠프',
            description: '재슈캠프 자기이해 성장캠프',
            center: '학생상담센터',
            category: '입박',
            startDate: '2025.10.20',
            endDate: '2025.11.04',
            eventDate: '2025.11.19(수) 13:00 ~ 15:00',
            currentParticipants: 5,
            maxParticipants: 9,
            hits: 59,
            badge: '입박',
            badgeColor: '#3498db'
        }
    ]
};

// Render Program Card
function renderProgramCard(program) {
    const progress = calculateProgress(program.currentParticipants, program.maxParticipants);
    const progressText = program.maxParticipants > 0 
        ? `${program.currentParticipants}/${program.maxParticipants}명` 
        : '승인필요';
    
    return `
        <div class="program-card" onclick="location.href='/programs/${program.id}'">
            <div class="program-card-image" style="background: linear-gradient(135deg, ${program.badgeColor} 0%, ${program.badgeColor}dd 100%);">
                <i class="fas fa-graduation-cap"></i>
            </div>
            <div class="program-card-body">
                <div class="program-card-center">
                    <span class="badge" style="background: ${program.badgeColor};">${program.badge}</span>
                    <span style="font-size: 0.85rem; color: #7f8c8d;">
                        <i class="fas fa-user"></i> ${program.currentParticipants > 0 ? program.currentParticipants : 0}
                    </span>
                </div>
                <h3 class="program-card-title">${program.title}</h3>
                <p class="program-card-description">${program.description}</p>
                <div class="program-card-info">
                    <i class="far fa-calendar"></i> 신청: ${program.startDate} ~ ${program.endDate}
                </div>
                <div class="program-card-info">
                    <i class="far fa-clock"></i> 운영: ${program.eventDate}
                </div>
                <div class="program-card-progress">
                    <div class="progress-bar-wrapper">
                        <div class="progress-bar" style="width: ${progress}%; background: ${program.badgeColor};"></div>
                    </div>
                    <div class="progress-text">${progressText}</div>
                </div>
            </div>
            <div class="program-card-hits">${program.hits} HITS</div>
        </div>
    `;
}

// Calculate progress
function calculateProgress(current, total) {
    if (total === 0) return 0;
    return Math.round((current / total) * 100);
}

// Load Programs
function loadPrograms() {
    const grid1 = document.getElementById('programGrid1');
    if (grid1) {
        grid1.innerHTML = MockData.programs1.map(program => renderProgramCard(program)).join('');
    }
}

/* ==================== */
/* Hero Carousel Class */
/* ==================== */

class HeroCarousel {
    constructor() {
        this.slides = document.querySelectorAll('.hero-slide');
        this.prevBtn = document.getElementById('heroPrev');
        this.nextBtn = document.getElementById('heroNext');
        this.indicators = document.querySelectorAll('.indicator');
        this.currentSlide = 0;
        this.autoSlideInterval = null;
        this.autoSlideDelay = 5000; // 5초마다 자동 슬라이딩
        
        this.init();
    }
    
    init() {
        if (this.slides.length === 0) return;
        
        // 이벤트 리스너 등록
        if (this.prevBtn) {
            this.prevBtn.addEventListener('click', () => this.prevSlide());
        }
        
        if (this.nextBtn) {
            this.nextBtn.addEventListener('click', () => this.nextSlide());
        }
        
        this.indicators.forEach((indicator, index) => {
            indicator.addEventListener('click', () => this.goToSlide(index));
        });
        
        // 자동 슬라이딩 시작
        this.startAutoSlide();
        
        // 마우스 호버 시 자동 슬라이딩 정지/재개
        const heroSection = document.querySelector('.hero-section');
        if (heroSection) {
            heroSection.addEventListener('mouseenter', () => this.stopAutoSlide());
            heroSection.addEventListener('mouseleave', () => this.startAutoSlide());
        }
        
        console.log('✅ Hero Carousel initialized with 3 slides');
    }
    
    goToSlide(slideIndex) {
        // 모든 슬라이드와 인디케이터에서 active 제거
        this.slides.forEach(slide => slide.classList.remove('active'));
        this.indicators.forEach(indicator => indicator.classList.remove('active'));
        
        // 현재 슬라이드와 인디케이터에 active 추가
        this.currentSlide = slideIndex;
        this.slides[this.currentSlide].classList.add('active');
        this.indicators[this.currentSlide].classList.add('active');
        
        console.log(`📍 Slide changed to: ${slideIndex + 1}`);
    }
    
    nextSlide() {
        let nextIndex = this.currentSlide + 1;
        if (nextIndex >= this.slides.length) {
            nextIndex = 0; // 마지막 슬라이드에서 첫 슬라이드로
        }
        this.goToSlide(nextIndex);
    }
    
    prevSlide() {
        let prevIndex = this.currentSlide - 1;
        if (prevIndex < 0) {
            prevIndex = this.slides.length - 1; // 첫 슬라이드에서 마지막 슬라이드로
        }
        this.goToSlide(prevIndex);
    }
    
    startAutoSlide() {
        this.stopAutoSlide(); // 기존 인터벌 제거 (중복 방지)
        this.autoSlideInterval = setInterval(() => {
            this.nextSlide();
        }, this.autoSlideDelay);
        console.log('▶️ Auto-slide started (5s interval)');
    }
    
    stopAutoSlide() {
        if (this.autoSlideInterval) {
            clearInterval(this.autoSlideInterval);
            this.autoSlideInterval = null;
            console.log('⏸️ Auto-slide paused');
        }
    }
}

// Initialize on DOM Ready
document.addEventListener('DOMContentLoaded', function() {
    // Hero Carousel 초기화
    new HeroCarousel();
    
    // Programs 로드
    loadPrograms();
    
    console.log('🎨 Main page loaded successfully');
});
