package model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

public class TutorApplication implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private UUID userId;
    private String bio;
    private String subjectsCanTeach;
    private String cvUrl;
    private String testSubmission;
    private String status;
    private String adminNote;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private User user;

    public TutorApplication() {
    }

    public TutorApplication(UUID userId, String bio, String subjectsCanTeach, String cvUrl, String testSubmission) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.bio = bio;
        this.subjectsCanTeach = subjectsCanTeach;
        this.cvUrl = cvUrl;
        this.testSubmission = testSubmission;
        this.status = "PENDING";
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public TutorApplication(UUID id, UUID userId, String bio, String subjectsCanTeach, String cvUrl,
                            String testSubmission, String status, String adminNote,
                            Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.userId = userId;
        this.bio = bio;
        this.subjectsCanTeach = subjectsCanTeach;
        this.cvUrl = cvUrl;
        this.testSubmission = testSubmission;
        this.status = status;
        this.adminNote = adminNote;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getSubjectsCanTeach() {
        return subjectsCanTeach;
    }

    public void setSubjectsCanTeach(String subjectsCanTeach) {
        this.subjectsCanTeach = subjectsCanTeach;
    }

    public String getCvUrl() {
        return cvUrl;
    }

    public void setCvUrl(String cvUrl) {
        this.cvUrl = cvUrl;
    }

    public String getTestSubmission() {
        return testSubmission;
    }

    public void setTestSubmission(String testSubmission) {
        this.testSubmission = testSubmission;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    @Override
    public String toString() {
        return "TutorApplication{" +
                "id=" + id +
                ", userId=" + userId +
                ", status='" + status + '\'' +
                ", subjectsCanTeach='" + subjectsCanTeach + '\'' +
                '}';
    }
}
