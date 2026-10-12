package com.analyzer.seo.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogEvent {
    private String timestamp;
    private String phase;
    private String message;
    private String level; // INFO, SUCCESS, WARNING, ERROR
    private int progress; // 0 to 100

    public LogEvent() {}

    public LogEvent(String phase, String message, String level, int progress) {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        this.phase = phase;
        this.message = message;
        this.level = level;
        this.progress = progress;
    }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getPhase() { return phase; }
    public void setPhase(String phase) { this.phase = phase; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
}
