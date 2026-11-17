package model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Entity: User (Người dùng)
 * Bảng tương ứng: tblUser
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String username;
    private String passwordHash;
    private String email;
    private String displayName;
    private String role;     // STUDENT, TUTOR, ADMIN
    private String status;   // ACTIVE, LOCKED
    private String phone;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // References
    private TutorProfile tutorProfile;
    private Wallet wallet;

    public User() {
    }

    public User(String username, String passwordHash, String displayName, String email, String role) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
        this.email = email;
        this.role = role;
        this.status = "ACTIVE";
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public User(UUID id, String username, String passwordHash, String email, String displayName, 
                String role, String status, String phone, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.displayName = displayName;
        this.role = role;
        this.status = status;
        this.phone = phone;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public boolean verifyPassword(String password) {
        if (this.passwordHash == null || password == null) return false;
        return this.passwordHash.equals(password);
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public TutorProfile getTutorProfile() {
        return tutorProfile;
    }

    public void setTutorProfile(TutorProfile tutorProfile) {
        this.tutorProfile = tutorProfile;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    @Override
    public String toString() {
        return displayName + " (" + username + " - " + role + ")";
    }
}
