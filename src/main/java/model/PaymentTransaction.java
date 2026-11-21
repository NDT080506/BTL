package model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

public class PaymentTransaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private UUID sourceWalletId;
    private UUID destinationWalletId;
    private UUID bookingId;
    private String type;
    private String status;
    private long amount;
    private String referenceCode;
    private Long balanceAfter;
    private Timestamp createdAt;
    private Booking booking;
    private Wallet sourceWallet;
    private Wallet destinationWallet;

    public PaymentTransaction() {
    }

    public PaymentTransaction(UUID sourceWalletId, UUID destinationWalletId, UUID bookingId,
                              String type, long amount, String referenceCode) {
        this.id = UUID.randomUUID();
        this.sourceWalletId = sourceWalletId;
        this.destinationWalletId = destinationWalletId;
        this.bookingId = bookingId;
        this.type = type;
        this.status = "PENDING";
        this.amount = amount;
        this.referenceCode = referenceCode;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public PaymentTransaction(UUID id, UUID sourceWalletId, UUID destinationWalletId, UUID bookingId,
                              String type, String status, long amount, String referenceCode,
                              Long balanceAfter, Timestamp createdAt) {
        this.id = id;
        this.sourceWalletId = sourceWalletId;
        this.destinationWalletId = destinationWalletId;
        this.bookingId = bookingId;
        this.type = type;
        this.status = status;
        this.amount = amount;
        this.referenceCode = referenceCode;
        this.balanceAfter = balanceAfter;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSourceWalletId() {
        return sourceWalletId;
    }

    public void setSourceWalletId(UUID sourceWalletId) {
        this.sourceWalletId = sourceWalletId;
    }

    public UUID getDestinationWalletId() {
        return destinationWalletId;
    }

    public void setDestinationWalletId(UUID destinationWalletId) {
        this.destinationWalletId = destinationWalletId;
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public void setBookingId(UUID bookingId) {
        this.bookingId = bookingId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public Long getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(Long balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Wallet getSourceWallet() {
        return sourceWallet;
    }

    public void setSourceWallet(Wallet sourceWallet) {
        this.sourceWallet = sourceWallet;
    }

    public Wallet getDestinationWallet() {
        return destinationWallet;
    }

    public void setDestinationWallet(Wallet destinationWallet) {
        this.destinationWallet = destinationWallet;
    }

    @Override
    public String toString() {
        return "Giao dịch " + referenceCode + " [" + type + " - " + String.format("%,d", amount) + " VNĐ]";
    }
}
