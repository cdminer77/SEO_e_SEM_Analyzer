package com.analyzer.seo.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AuditReport {
    private String id;
    private String targetUrl;
    private String analyzedAt;
    private String status; // RUNNING, COMPLETED, FAILED
    private int overallScore; // 0 - 100
    private int seoScore; // 0 - 100
    private int semScore; // 0 - 100

    private SeoMetrics seoMetrics;
    private SemMetrics semMetrics;
    private List<AuditIssue> issues = new ArrayList<>();
    private List<RecommendationItem> recommendations = new ArrayList<>();
    private List<LogEvent> logs = new ArrayList<>();

    public AuditReport() {
        this.analyzedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        this.status = "PENDING";
    }

    public AuditReport(String id, String targetUrl) {
        this();
        this.id = id;
        this.targetUrl = targetUrl;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTargetUrl() { return targetUrl; }
    public void setTargetUrl(String targetUrl) { this.targetUrl = targetUrl; }

    public String getAnalyzedAt() { return analyzedAt; }
    public void setAnalyzedAt(String analyzedAt) { this.analyzedAt = analyzedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getOverallScore() { return overallScore; }
    public void setOverallScore(int overallScore) { this.overallScore = overallScore; }

    public int getSeoScore() { return seoScore; }
    public void setSeoScore(int seoScore) { this.seoScore = seoScore; }

    public int getSemScore() { return semScore; }
    public void setSemScore(int semScore) { this.semScore = semScore; }

    public SeoMetrics getSeoMetrics() { return seoMetrics; }
    public void setSeoMetrics(SeoMetrics seoMetrics) { this.seoMetrics = seoMetrics; }

    public SemMetrics getSemMetrics() { return semMetrics; }
    public void setSemMetrics(SemMetrics semMetrics) { this.semMetrics = semMetrics; }

    public List<AuditIssue> getIssues() { return issues; }
    public void setIssues(List<AuditIssue> issues) { this.issues = issues; }

    public List<RecommendationItem> getRecommendations() { return recommendations; }
    public void setRecommendations(List<RecommendationItem> recommendations) { this.recommendations = recommendations; }

    public List<LogEvent> getLogs() { return logs; }
    public void setLogs(List<LogEvent> logs) { this.logs = logs; }
}
