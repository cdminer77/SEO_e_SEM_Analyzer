package com.analyzer.seo.model;

public class AuditIssue {
    public enum Severity {
        CRITICAL, WARNING, GOOD
    }

    private String category; // SEO, SEM, PERFORMANCE, TECH
    private Severity severity;
    private String title;
    private String details;

    public AuditIssue() {}

    public AuditIssue(String category, Severity severity, String title, String details) {
        this.category = category;
        this.severity = severity;
        this.title = title;
        this.details = details;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
