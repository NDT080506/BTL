package model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public class Wallet implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private UUID userId;
    private long availableBalance;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private User user;
    private List<Withdrawal> withdrawals;
    private List<PaymentTransaction> paymentTransactions;

    public Wallet() {
        this.availableBalance = 0;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public Wallet(UUID userId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.availableBalance = 0;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public Wallet(UUID userId, long availableBalance) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.availableBalance = availableBalance;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public Wallet(UUID id, UUID userId, long availableBalance, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.userId = userId;
        this.availableBalance = availableBalance;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void credit(long amount) {
        if (amount > 0) {
            this.availableBalance += amount;
            this.updatedAt = new Timestamp(System.currentTimeMillis());
        }
    }

    public boolean debit(long amount) {
        if (amount > 0 && this.availableBalance >= amount) {
            this.availableBalance -= amount;
            this.updatedAt = new Timestamp(System.currentTimeMillis());
            return true;
        }
        return false;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public long getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(long availableBalance) {
        this.availableBalance = availableBalance;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Withdrawal> getWithdrawals() {
        return withdrawals;
    }

    public void setWithdrawals(List<Withdrawal> withdrawals) {
        this.withdrawals = withdrawals;
    }

    public List<PaymentTransaction> getPaymentTransactions() {
        return paymentTransactions;
    }

    public void setPaymentTransactions(List<PaymentTransaction> paymentTransactions) {
        this.paymentTransactions = paymentTransactions;
    }

    @Override
    public String toString() {
        return "Số dư: " + String.format("%,d", availableBalance) + " VNĐ";
    }
}
