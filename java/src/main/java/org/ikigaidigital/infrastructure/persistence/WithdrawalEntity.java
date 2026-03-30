package org.ikigaidigital.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "withdrawals")
public class WithdrawalEntity {
    @Id
    private int id;

    @Column(name = "time_deposit_id", nullable = false)
    private int timeDepositId;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_deposit_id", insertable = false, updatable = false)
    private TimeDepositEntity timeDeposit;

    public WithdrawalEntity() {
    }

    public WithdrawalEntity(int id, int timeDepositId, Double amount, LocalDate date) {
        this.id = id;
        this.timeDepositId = timeDepositId;
        this.amount = amount;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTimeDepositId() {
        return timeDepositId;
    }

    public void setTimeDepositId(int timeDepositId) {
        this.timeDepositId = timeDepositId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public TimeDepositEntity getTimeDeposit() {
        return timeDeposit;
    }

    public void setTimeDeposit(TimeDepositEntity timeDeposit) {
        this.timeDeposit = timeDeposit;
    }
}
