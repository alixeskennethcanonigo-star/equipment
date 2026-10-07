package com.lab.system.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ActivityLog implements Serializable {
    private static final long serialVersionUID = 1L;

    private String logId;
    private String userId;
    private String action;
    private String timestamp;

    // Constructor generating automatic timestamp
    public ActivityLog(String logId, String userId, String action) {
        this.logId = logId;
        this.userId = userId;
        this.action = action;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // Constructor accepting an explicit timestamp (useful when loading historical logs)
    public ActivityLog(String logId, String userId, String action, String timestamp) {
        this.logId = logId;
        this.userId = userId;
        this.action = action;
        this.timestamp = timestamp;
    }

    // --- GETTERS (Required by Services and UI Controllers) ---
    public String getLogId() { return logId; }
    public String getUserId() { return userId; }
    public String getAction() { return action; }
    public String getTimestamp() { return timestamp; }

    // --- SETTERS ---
    public void setUserId(String userId) { this.userId = userId; }
    public void setAction(String action) { this.action = action; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return String.format("[%s] LogID: %-6s | User: %-8s | Event: %s", timestamp, logId, userId, action);
    }
}