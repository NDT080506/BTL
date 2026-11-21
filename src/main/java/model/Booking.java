package model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public class Booking implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private UUID studentId;
    private UUID tutorId;
    private UUID packageId;
    private Timestamp scheduledAt;
    private int durationMinutes;
    private String note;
    private String status;
    private long totalPrice;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private User student;
    private User tutor;
    private BrokeragePackage brokeragePackage;
    private List<Dispute> disputes;
    private List<PaymentTransaction> paymentTransactions;

    public Booking() {
    }

    public Booking(UUID studentId, UUID tutorId, UUID packageId, Timestamp scheduledAt,
                   int durationMinutes, String note, long totalPrice) {
        this.id = UUID.randomUUID();
        this.studentId = studentId;
        this.tutorId = tutorId;
        this.packageId = packageId;
        this.scheduledAt = scheduledAt;
        this.durationMinutes = durationMinutes;
        this.note = note;
        this.totalPrice = totalPrice;
        this.status = "PENDING_TUTOR_CONFIRMATION";
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public Booking(UUID id, UUID studentId, UUID tutorId, UUID packageId, Timestamp scheduledAt,
                   int durationMinutes, String note, String status, long totalPrice,
                   Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.studentId = studentId;
        this.tutorId = tutorId;
        this.packageId = packageId;
        this.scheduledAt = scheduledAt;
        this.durationMinutes = durationMinutes;
        this.note = note;
        this.status = status;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void markPaid() {
        this.status = "PAID";
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getStudentId() {
        return studentId;
    }

    public void setStudentId(UUID studentId) {
        this.studentId = studentId;
    }

    public UUID getTutorId() {
        return tutorId;
    }

    public void setTutorId(UUID tutorId) {
        this.tutorId = tutorId;
    }

    public UUID getPackageId() {
        return packageId;
    }

    public void setPackageId(UUID packageId) {
        this.packageId = packageId;
    }

    public Timestamp getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(Timestamp scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
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

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public User getTutor() {
        return tutor;
    }

    public void setTutor(User tutor) {
        this.tutor = tutor;
    }

    public BrokeragePackage getBrokeragePackage() {
        return brokeragePackage;
    }

    public void setBrokeragePackage(BrokeragePackage brokeragePackage) {
        this.brokeragePackage = brokeragePackage;
    }

    public List<Dispute> getDisputes() {
        return disputes;
    }

    public void setDisputes(List<Dispute> disputes) {
        this.disputes = disputes;
    }

    public List<PaymentTransaction> getPaymentTransactions() {
        return paymentTransactions;
    }

    public void setPaymentTransactions(List<PaymentTransaction> paymentTransactions) {
        this.paymentTransactions = paymentTransactions;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", scheduledAt=" + scheduledAt +
                ", status='" + status + '\'' +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
