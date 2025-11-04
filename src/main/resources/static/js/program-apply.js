/**
 * Program Apply Page JavaScript
 * Mock Data Implementation
 */

// Mock 프로그램 데이터 가져오기
function getProgramData() {
    const urlParams = new URLSearchParams(window.location.search);
    const programId = urlParams.get('id') || '1';
    
    // main.js의 PROGRAMS 데이터 사용
    const program = window.PROGRAMS?.find(p => p.id === parseInt(programId));
    
    if (!program) {
        alert('프로그램 정보를 찾을 수 없습니다.');
        window.location.href = '/program/list';
        return null;
    }
    
    return program;
}

// 프로그램 정보 렌더링
function renderProgramInfo(program) {
    // 프로그램 기본 정보
    document.getElementById('programBadge').textContent = program.badge;
    document.getElementById('programTitle').textContent = program.title;
    document.getElementById('programCenter').textContent = program.center;
    document.getElementById('eventDate').textContent = program.date;
    document.getElementById('location').textContent = program.location;
    
    // 참가 현황
    const participantText = `<strong class="current">${program.current}</strong> / ${program.capacity}명`;
    document.getElementById('participants').innerHTML = participantText;
}

// 글자 수 카운터
function setupCharCounter() {
    const motivationTextarea = document.getElementById('motivation');
    const charCountSpan = document.getElementById('charCount');
    
    const expectationsTextarea = document.getElementById('expectations');
    const expectCharCountSpan = document.getElementById('expectCharCount');
    
    // 신청 동기 카운터
    motivationTextarea.addEventListener('input', function() {
        const count = this.value.length;
        charCountSpan.textContent = count;
        
        // 글자 수에 따른 스타일 변경
        if (count < 50) {
            charCountSpan.style.color = '#f44336';
        } else {
            charCountSpan.style.color = '#4caf50';
        }
    });
    
    // 기대 효과 카운터
    expectationsTextarea.addEventListener('input', function() {
        expectCharCountSpan.textContent = this.value.length;
    });
}

// 전체 동의 체크박스
function setupAgreeAll() {
    const agreeAllCheckbox = document.getElementById('agreeAll');
    const agreementCheckboxes = document.querySelectorAll('.agreement-checkbox:not(#agreeAll)');
    
    // 전체 동의 클릭
    agreeAllCheckbox.addEventListener('change', function() {
        agreementCheckboxes.forEach(checkbox => {
            checkbox.checked = this.checked;
        });
    });
    
    // 개별 체크박스 상태 변경 시
    agreementCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            const allChecked = Array.from(agreementCheckboxes).every(cb => cb.checked);
            agreeAllCheckbox.checked = allChecked;
        });
    });
}

// 모달 관련 기능
function setupModals() {
    // 모달 열기
    const detailButtons = document.querySelectorAll('.btn-detail');
    detailButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            const modalId = this.getAttribute('data-modal') + 'Modal';
            const modal = document.getElementById(modalId);
            if (modal) {
                modal.style.display = 'flex';
            }
        });
    });
    
    // 모달 닫기
    const closeButtons = document.querySelectorAll('.modal-close, [data-close]');
    closeButtons.forEach(button => {
        button.addEventListener('click', function() {
            const modalId = this.getAttribute('data-close');
            const modal = modalId ? document.getElementById(modalId) : this.closest('.modal');
            if (modal) {
                modal.style.display = 'none';
            }
        });
    });
    
    // 모달 배경 클릭 시 닫기
    const modals = document.querySelectorAll('.modal');
    modals.forEach(modal => {
        modal.addEventListener('click', function(e) {
            if (e.target === this) {
                this.style.display = 'none';
            }
        });
    });
}

// 폼 유효성 검사
function validateForm(formData) {
    const errors = [];
    
    // 연락처 검사
    const phonePattern = /^010-\d{4}-\d{4}$/;
    if (!phonePattern.test(formData.phone)) {
        errors.push('연락처는 010-1234-5678 형식으로 입력해주세요.');
    }
    
    // 이메일 검사
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailPattern.test(formData.email)) {
        errors.push('올바른 이메일 주소를 입력해주세요.');
    }
    
    // 신청 동기 길이 검사
    if (formData.motivation.length < 50) {
        errors.push('신청 동기는 최소 50자 이상 입력해주세요.');
    }
    
    // 필수 동의 항목 검사
    const requiredAgreements = document.querySelectorAll('.required-agreement');
    const allAgreed = Array.from(requiredAgreements).every(checkbox => checkbox.checked);
    if (!allAgreed) {
        errors.push('필수 동의 항목을 모두 체크해주세요.');
    }
    
    // 토익 점수 검사 (입력한 경우)
    if (formData.toeicScore && (formData.toeicScore < 0 || formData.toeicScore > 990)) {
        errors.push('토익 점수는 0~990 사이의 값을 입력해주세요.');
    }
    
    return errors;
}

// 폼 제출 처리
function handleFormSubmit(e) {
    e.preventDefault();
    
    const form = e.target;
    const formData = {
        programId: new URLSearchParams(window.location.search).get('id') || '1',
        studentName: document.getElementById('studentName').value,
        studentId: document.getElementById('studentId').value,
        department: document.getElementById('department').value,
        grade: document.getElementById('grade').value,
        email: document.getElementById('email').value,
        phone: document.getElementById('phone').value,
        motivation: document.getElementById('motivation').value,
        expectations: document.getElementById('expectations').value,
        toeicScore: document.getElementById('toeicScore').value,
        timestamp: new Date().toISOString()
    };
    
    // 유효성 검사
    const errors = validateForm(formData);
    
    if (errors.length > 0) {
        alert('입력 내용을 확인해주세요:\n\n' + errors.join('\n'));
        return;
    }
    
    // Mock 제출 처리
    console.log('신청 데이터:', formData);
    
    // 로컬 스토리지에 저장 (Mock)
    try {
        let applications = JSON.parse(localStorage.getItem('applications') || '[]');
        applications.push(formData);
        localStorage.setItem('applications', JSON.stringify(applications));
        
        // 성공 메시지
        showSuccessModal(formData);
    } catch (error) {
        console.error('신청 처리 중 오류:', error);
        alert('신청 처리 중 오류가 발생했습니다. 다시 시도해주세요.');
    }
}

// 성공 모달 표시
function showSuccessModal(formData) {
    const modal = document.createElement('div');
    modal.className = 'modal';
    modal.style.display = 'flex';
    modal.innerHTML = `
        <div class="modal-content">
            <div class="modal-header">
                <h3><i class="fas fa-check-circle" style="color: #4caf50;"></i> 신청 완료</h3>
            </div>
            <div class="modal-body">
                <div class="success-message">
                    <p class="success-title">프로그램 신청이 완료되었습니다!</p>
                    <div class="success-info">
                        <p><strong>신청자:</strong> ${formData.studentName} (${formData.studentId})</p>
                        <p><strong>연락처:</strong> ${formData.phone}</p>
                        <p><strong>이메일:</strong> ${formData.email}</p>
                        <p style="margin-top: 20px; color: #666; font-size: 14px;">
                            신청 내역은 마이페이지에서 확인하실 수 있습니다.<br>
                            승인 결과는 입력하신 이메일로 발송됩니다.
                        </p>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline" onclick="location.href='/program/list'">
                    목록으로
                </button>
                <button type="button" class="btn btn-primary" onclick="location.href='/mileage/dashboard'">
                    마이페이지
                </button>
            </div>
        </div>
    `;
    
    document.body.appendChild(modal);
    
    // 배경 클릭으로 닫기
    modal.addEventListener('click', function(e) {
        if (e.target === modal) {
            document.body.removeChild(modal);
            window.location.href = '/program/list';
        }
    });
}

// 페이지 초기화
document.addEventListener('DOMContentLoaded', function() {
    // 프로그램 데이터 로드
    const program = getProgramData();
    if (program) {
        renderProgramInfo(program);
    }
    
    // 기능 초기화
    setupCharCounter();
    setupAgreeAll();
    setupModals();
    
    // 폼 제출 이벤트
    const applyForm = document.getElementById('applyForm');
    applyForm.addEventListener('submit', handleFormSubmit);
    
    // 연락처 자동 포맷팅
    const phoneInput = document.getElementById('phone');
    phoneInput.addEventListener('input', function(e) {
        let value = e.target.value.replace(/\D/g, '');
        if (value.length > 3 && value.length <= 7) {
            value = value.slice(0, 3) + '-' + value.slice(3);
        } else if (value.length > 7) {
            value = value.slice(0, 3) + '-' + value.slice(3, 7) + '-' + value.slice(7, 11);
        }
        e.target.value = value;
    });
});
