# 🔄 SCMS 프로젝트 세션 재개

## 📊 **현재 진행 상황: Entity 구현 52% 완료 (12/23)**

### **✅ 완료된 작업 (12개 Entity + 6개 Enum)**

#### **Core Infrastructure:**
- BaseEntity (공통 감사 필드)

#### **Auth Domain (3개):**
- User, UserRole Enum
- Auth 서비스 Entity

#### **Program Domain (6개):**
- Program, ProgramApplication, ProgramParticipant
- ProgramStatus, ApplicationStatus, AttendanceStatus Enum

#### **Mileage Domain (4개):**
- MileageAccount, MileageTransaction
- TransactionType, VerificationStatus Enum

#### **Competency Domain (6개):** ⭐ **최근 완료**
- CompetencySurvey, SurveyQuestion, SurveyResponse, CompetencyResult
- TargetRole, QuestionType Enum
- CompetencyCertification (Mileage Domain과 연계)

---

## 🎯 **다음 작업 대상: Counseling Domain (3개 Entity)**

### **⏳ 남은 Entity (11개)**
- **Counseling Domain (3개):** CounselingReservation, CounselingSession, CounselorAvailability
- **Career Domain (3개):** CareerPlan, CareerGoal, CareerMilestone  
- **File Domain (1개):** FileMetadata
- **Notification Domain (2개):** Notification, NotificationTemplate
- **System Domain (2개):** SystemLog, AuditLog

---

## 💡 **적용 중인 핵심 원칙**
1. **Hybrid JPA 전략**
   - 동일 도메인 내 관계: JPA 매핑 사용 (OneToMany, ManyToOne 등)
   - 크로스 도메인 관계: ID만 참조 (Long userId, Long programId 등)

2. **NO_CONSTRAINT 전략**
   - 외래키 제약 없이 JPA 관계 유지
   - MSA 전환 준비를 위한 느슨한 결합

3. **BaseEntity 상속 원칙**
   - 생성일시/수정일시가 필요한 Entity만 상속
   - 불변 데이터(SurveyResponse 등)는 상속 제외

---

## 🚀 **준비 완료**

**다음 작업:** Counseling Domain Entity 구현 시작
- CounselingReservation
- CounselingSession  
- CounselorAvailability

**프로젝트 경로:** `C:\Users\USER\Documents\choongang\Project\scms\scms-backend`

