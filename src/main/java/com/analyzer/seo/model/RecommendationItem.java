package com.analyzer.seo.model;

public class RecommendationItem {
    public enum Priority {
        ALTA, MEDIA, BASSA
    }

    private String id;
    private String category; // SEO, SEM, UX
    private Priority priority;
    private String title;
    private String impactExplanation;
    private String suggestedFix;
    private String codeSnippet; // Opzionale: snippet HTML/JS/Robots

    public RecommendationItem() {}

    public RecommendationItem(String id, String category, Priority priority, String title, 
                              String impactExplanation, String suggestedFix, String codeSnippet) {
        this.id = id;
        this.category = category;
        this.priority = priority;
        this.title = title;
        this.impactExplanation = impactExplanation;
        this.suggestedFix = suggestedFix;
        this.codeSnippet = codeSnippet;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getImpactExplanation() { return impactExplanation; }
    public void setImpactExplanation(String impactExplanation) { this.impactExplanation = impactExplanation; }

    public String getSuggestedFix() { return suggestedFix; }
    public void setSuggestedFix(String suggestedFix) { this.suggestedFix = suggestedFix; }

    public String getCodeSnippet() { return codeSnippet; }
    public void setCodeSnippet(String codeSnippet) { this.codeSnippet = codeSnippet; }
}
