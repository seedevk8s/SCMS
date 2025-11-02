package com.university.scms.domain.entity;

import com.university.scms.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mileage_accounts",
       indexes = {
           @Index(name = "idx_user", columnList = "user_id"),
           @Index(name = "idx_available_points", columnList = "available_points")
       })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MileageAccount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;  // Auth Domain 참조 (외래키 없음)

    @Column(name = "total_points", nullable = false)
    private Integer totalPoints = 0;  // 누적 마일리지

    @Column(name = "available_points", nullable = false)
    private Integer availablePoints = 0;  // 사용 가능 마일리지

    @Column(name = "used_points", nullable = false)
    private Integer usedPoints = 0;  // 사용한 마일리지

    // 도메인 내부: JPA 관계 (외래키 제약조건 제거)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MileageTransaction> transactions = new ArrayList<>();

    // === 생성 메서드 ===
    public static MileageAccount create(Long userId) {
        MileageAccount account = new MileageAccount();
        account.userId = userId;
        account.totalPoints = 0;
        account.availablePoints = 0;
        account.usedPoints = 0;
        return account;
    }

    // === 비즈니스 메서드 ===
    
    /**
     * 마일리지 적립
     */
    public void earn(int points) {
        if (points <= 0) {
            throw new IllegalArgumentException("적립 포인트는 0보다 커야 합니다.");
        }
        this.totalPoints += points;
        this.availablePoints += points;
    }

    /**
     * 마일리지 사용
     */
    public void use(int points) {
        if (points <= 0) {
            throw new IllegalArgumentException("사용 포인트는 0보다 커야 합니다.");
        }
        if (this.availablePoints < points) {
            throw new IllegalStateException("사용 가능한 마일리지가 부족합니다.");
        }
        this.availablePoints -= points;
        this.usedPoints += points;
    }

    /**
     * 마일리지 소멸
     */
    public void expire(int points) {
        if (points <= 0) {
            throw new IllegalArgumentException("소멸 포인트는 0보다 커야 합니다.");
        }
        if (this.availablePoints < points) {
            throw new IllegalStateException("소멸할 마일리지가 부족합니다.");
        }
        this.availablePoints -= points;
    }

    /**
     * 마일리지 조정 (관리자)
     */
    public void adjust(int points) {
        if (points > 0) {
            this.totalPoints += points;
            this.availablePoints += points;
        } else if (points < 0) {
            int absPoints = Math.abs(points);
            if (this.availablePoints < absPoints) {
                throw new IllegalStateException("차감할 마일리지가 부족합니다.");
            }
            this.totalPoints -= absPoints;
            this.availablePoints -= absPoints;
        }
    }

    /**
     * 거래 내역 추가
     */
    public void addTransaction(MileageTransaction transaction) {
        this.transactions.add(transaction);
    }
}
