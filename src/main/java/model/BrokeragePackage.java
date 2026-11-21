package model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public class BrokeragePackage implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private UUID tutorId;
    private String name;
    private String description;
    private long price;
    private boolean active;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private User tutor;
    private List<Booking> bookings;

    public BrokeragePackage() {
        this.active = true;
    }

    public BrokeragePackage(UUID tutorId, String name, String description, long price, boolean active) {
        this.id = UUID.randomUUID();
        this.tutorId = tutorId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.active = active;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public BrokeragePackage(UUID id, UUID tutorId, String name, String description,
                            long price, boolean active, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.tutorId = tutorId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTutorId() {
        return tutorId;
    }

    public void setTutorId(UUID tutorId) {
        this.tutorId = tutorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public User getTutor() {
        return tutor;
    }

    public void setTutor(User tutor) {
        this.tutor = tutor;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    @Override
    public String toString() {
        return name + " (" + String.format("%,d", price) + " VNĐ)";
    }
}
