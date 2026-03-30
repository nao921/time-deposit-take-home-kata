package org.ikigaidigital.infrastructure.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "time_deposits")
public class TimeDepositEntity {
    @Id
    private int id;

    @Column(nullable = false)
    private String planType;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private int days;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "timeDeposit")
    private List<WithdrawalEntity> withdrawals = new ArrayList<>();

    public TimeDepositEntity() {
    }

    public TimeDepositEntity(int id, String planType, BigDecimal balance, int days) {
        this.id = id;
        this.planType = planType;
        this.balance = balance;
        this.days = days;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public List<WithdrawalEntity> getWithdrawals() {
        return withdrawals;
    }

    public void setWithdrawals(List<WithdrawalEntity> withdrawals) {
        this.withdrawals = withdrawals;
    }
}
