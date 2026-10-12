package com.analyzer.seo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_reports")
public class AuditReportEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(nullable = false, length = 1024)
    private String targetUrl;

    @Column(nullable = false, length = 64)
    private String analyzedAt;

    @Column(nullable = false, length = 32)
    private String status;

    private int overallScore;
    private int seoScore;
    private int semScore;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String reportJson;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public AuditReportEntity() {
        this.createdAt = LocalDateTime.now();
    }

    public AuditReportEntity(String id, String targetUrl, String analyzedAt, String status, 
                             int overallScore, int seoScore, int semScore, String reportJson) {
        this.id = id;
        this.targetUrl = targetUrl;
        this.analyzedAt = analyzedAt;
        this.status = status;
        this.overallScore = overallScore;
        this.seoScore = seoScore;
        this.semScore = semScore;
        this.reportJson = reportJson;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
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

    public String getReportJson() { return reportJson; }
    public void setReportJson(String reportJson) { this.reportJson = reportJson; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
