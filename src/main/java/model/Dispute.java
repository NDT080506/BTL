package model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Entity: Dispute (Khiếu nại và hoàn tiền)
 * Bảng tương ứng: tblDispute
 */
public class Dispute implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private UUID bookingId;
    private UUID createdById;
    private String reason;
    private String evidenceUrl;
    private String status; // PENDING, APPROVED, REJECTED
    private long refundAmount;
    private String adminNote;
    private Timestamp createdAt;
    private Timestamp resolvedAt;

    // References
    private Booking booking;
    private User createdBy;

    public Dispute() {
    }

    public Dispute(UUID bookingId, UUID createdById, String reason, String evidenceUrl, long refundAmount) {
        this.id = UUID.randomUUID();
        this.bookingId = bookingId;
        this.createdById = createdById;
        this.reason = reason;
        this.evidenceUrl = evidenceUrl;
        this.refundAmount = refundAmount;
        this.status = "PENDING";
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public Dispute(UUID id, UUID bookingId, UUID createdById, String reason, String evidenceUrl, 
                   String status, long refundAmount, String adminNote, 
                   Timestamp createdAt, Timestamp resolvedAt) {
        this.id = id;
        this.bookingId = bookingId;
        this.createdById = createdById;
        this.reason = reason;
        this.evidenceUrl = evidenceUrl;
        this.status = status;
        this.refundAmount = refundAmount;
        this.adminNote = adminNote;
        this.createdAt = createdAt;
        this.resolvedAt = resolvedAt;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public void setBookingId(UUID bookingId) {
        this.bookingId = bookingId;
    }

    public UUID getCreatedById() {
        return createdById;
    }

    public void setCreatedById(UUID createdById) {
        this.createdById = createdById;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getEvidenceUrl() {
        return evidenceUrl;
    }

    public void setEvidenceUrl(String evidenceUrl) {
        this.evidenceUrl = evidenceUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(long refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getAdminNote() {
        return adminNote;
    }

    public void setAdminNote(String adminNote) {
        this.adminNote = adminNote;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(Timestamp resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "Dispute{" +
                "id=" + id +
                ", bookingId=" + bookingId +
                ", status='" + status + '\'' +
                ", refundAmount=" + refundAmount +
                '}';
    }
}
