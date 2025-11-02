package com.university.scms.domain.mileage.entity;

import com.university.scms.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mileage_transactions",
       indexes = {
           @Index(name = "idx_account", columnList = "account_id"),
           @Index(name = "idx_user", columnList = "user_id"),
           @Index(name = "idx_type", columnList = "transaction_type"),
           @Index(name = "idx_source", columnList = "source_type, source_id")
       })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MileageTransaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 도메인 내부: JPA 관계 (외래키 제약조건 제거)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false,
                foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private MileageAccount account;

    @Column(name = "user_id", nullable = false)
    private Long userId;  // Auth Domain 참조 (조회 편의용)

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 20)
    private TransactionType transactionType;

    @Column(name = "points", nullable = false)
    private Integer points;  // 적립/사용 포인트 (양수: 증가, 음수: 감소)

    @Column(name = "source_type", length = 50)
    private String sourceType;  // PROGRAM, CERTIFICATION, ADJUSTMENT 등

    @Column(name = "source_id")
    private Long sourceId;  // 출처 엔티티의 ID

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "balance_after", nullable = false)
    private Integer balanceAfter;  // 거래 후 잔액

    // === 생성 메서드 ===
    
    public static MileageTransaction createEarn(
            MileageAccount account, 
            Long userId,
            int points, 
            String sourceType, 
            Long sourceId, 
            String description) {
        
        MileageTransaction transaction = new MileageTransaction();
        transaction.account = account;
        transaction.userId = userId;
        transaction.transactionType = TransactionType.EARN;
        transaction.points = points;
        transaction.sourceType = sourceType;
        transaction.sourceId = sourceId;
        transaction.description = description;
        transaction.balanceAfter = account.getAvailablePoints() + points;
        
        account.earn(points);
        account.addTransaction(transaction);
        
        return transaction;
    }

    public static MileageTransaction createUse(
            MileageAccount account,
            Long userId,
            int points,
            String sourceType,
            Long sourceId,
            String description) {
        
        MileageTransaction transaction = new MileageTransaction();
        transaction.account = account;
        transaction.userId = userId;
        transaction.transactionType = TransactionType.USE;
        transaction.points = -points;  // 음수로 저장
        transaction.sourceType = sourceType;
        transaction.sourceId = sourceId;
        transaction.description = description;
        transaction.balanceAfter = account.getAvailablePoints() - points;
        
        account.use(points);
        account.addTransaction(transaction);
        
        return transaction;
    }

    public static MileageTransaction createExpire(
            MileageAccount account,
            Long userId,
            int points,
            String description) {
        
        MileageTransaction transaction = new MileageTransaction();
        transaction.account = account;
        transaction.userId = userId;
        transaction.transactionType = TransactionType.EXPIRE;
        transaction.points = -points;  // 음수로 저장
        transaction.description = description;
        transaction.balanceAfter = account.getAvailablePoints() - points;
        
        account.expire(points);
        account.addTransaction(transaction);
        
        return transaction;
    }

    public static MileageTransaction createAdjust(
            MileageAccount account,
            Long userId,
            int points,
            String description) {
        
        MileageTransaction transaction = new MileageTransaction();
        transaction.account = account;
        transaction.userId = userId;
        transaction.transactionType = TransactionType.ADJUST;
        transaction.points = points;
        transaction.description = description;
        transaction.balanceAfter = account.getAvailablePoints() + points;
        
        account.adjust(points);
        account.addTransaction(transaction);
        
        return transaction;
    }

    // === 조회 메서드 ===
    
    public boolean isEarn() {
        return this.transactionType == TransactionType.EARN;
    }

    public boolean isUse() {
        return this.transactionType == TransactionType.USE;
    }

    public boolean isExpire() {
        return this.transactionType == TransactionType.EXPIRE;
    }

    public boolean isAdjust() {
        return this.transactionType == TransactionType.ADJUST;
    }
}
