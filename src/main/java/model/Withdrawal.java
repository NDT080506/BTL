package model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

public class Withdrawal implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private UUID userId;
    private UUID walletId;
    private long amount;
    private String bankName;
    private String accountNumber;
    private String accountHolder;
    private String status;
    private Timestamp createdAt;
    private Timestamp processedAt;
    private User user;
    private Wallet wallet;

    public Withdrawal() {
    }

    public Withdrawal(UUID userId, UUID walletId, long amount, String bankName,
                      String accountNumber, String accountHolder) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.walletId = walletId;
        this.amount = amount;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.status = "PENDING";
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public Withdrawal(UUID id, UUID userId, UUID walletId, long amount, String bankName,
                      String accountNumber, String accountHolder, String status,
                      Timestamp createdAt, Timestamp processedAt) {
        this.id = id;
        this.userId = userId;
        this.walletId = walletId;
        this.amount = amount;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.status = status;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
    }

    public String getMaskedAccountNumber() {
        if (accountNumber == null || accountNumber.length() < 4) {
            return "****";
        }
        return "••••" + accountNumber.substring(accountNumber.length() - 4);
    }

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

    public UUID getWalletId() {
        return walletId;
    }

    public void setWalletId(UUID walletId) {
        this.walletId = walletId;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public void setAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Timestamp processedAt) {
        this.processedAt = processedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    @Override
    public String toString() {
        return bankName + " - " + getMaskedAccountNumber() + " (" + String.format("%,d", amount) + " VNĐ)";
    }
}
