package model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

public class ActionLog implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private UUID userId;
    private String action;
    private String targetType;
    private UUID targetId;
    private String ipAddress;
    private String userAgent;
    private Timestamp createdAt;
    private User user;

    public ActionLog() {
    }

    public ActionLog(UUID userId, String action, String targetType, UUID targetId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.action = action;
        this.targetType = targetType;
        this.targetId = targetId;
        this.ipAddress = "127.0.0.1";
        this.userAgent = "TutorTech Swing Client";
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public ActionLog(UUID id, UUID userId, String action, String targetType, UUID targetId, 
                     String ipAddress, String userAgent, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.action = action;
        this.targetType = targetType;
        this.targetId = targetId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.createdAt = createdAt;
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public UUID getTargetId() {
        return targetId;
    }

    public void setTargetId(UUID targetId) {
        this.targetId = targetId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "[" + createdAt + "] " + action + " on " + targetType + " (" + targetId + ")";
    }
}
