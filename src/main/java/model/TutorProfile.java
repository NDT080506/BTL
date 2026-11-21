package model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

public class TutorProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID userId;
    private String bio;
    private String subjectsCanTeach;
    private double rating;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private User user;

    public TutorProfile() {
        this.rating = 5.0;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public TutorProfile(UUID userId, String bio, String subjectsCanTeach, double rating) {
        this.userId = userId;
        this.bio = bio;
        this.subjectsCanTeach = subjectsCanTeach;
        this.rating = rating;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public TutorProfile(UUID userId, String bio, String subjectsCanTeach, double rating,
                        Timestamp createdAt, Timestamp updatedAt) {
        this.userId = userId;
        this.bio = bio;
        this.subjectsCanTeach = subjectsCanTeach;
        this.rating = rating;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
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
        return "TutorProfile{" +
                "userId=" + userId +
                ", subjectsCanTeach='" + subjectsCanTeach + '\'' +
                ", rating=" + rating +
                '}';
    }
}
