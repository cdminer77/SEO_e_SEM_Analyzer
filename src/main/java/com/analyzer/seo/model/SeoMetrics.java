package com.analyzer.seo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SeoMetrics {
    private int score; // 0 - 100
    private String title;
    private int titleLength;
    private String metaDescription;
    private int metaDescriptionLength;
    private String canonicalUrl;
    private String robotsMeta;
    private boolean isHttps;
    private int httpStatusCode;
    private long responseTimeMs;
    
    // Headings
    private List<String> h1Tags = new ArrayList<>();
    private List<String> h2Tags = new ArrayList<>();
    private int h1Count;
    private int h2Count;
    private int h3Count;

    // Immagini
    private int totalImages;
    private int imagesMissingAlt;
    private List<String> missingAltSources = new ArrayList<>();

    // Link
    private int totalInternalLinks;
    private int totalExternalLinks;
    private int nofollowLinks;

    // File e Social
    private boolean robotsTxtFound;
    private boolean sitemapFound;
    private boolean openGraphFound;
    private String ogTitle;
    private String ogImage;
    private boolean twitterCardFound;
    private boolean schemaOrgFound;
    private boolean viewportPresent;

    // Keyword Frequency (Top 10)
    private Map<String, Integer> topKeywords;

    public SeoMetrics() {}

    // Getters and Setters
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getTitleLength() { return titleLength; }
    public void setTitleLength(int titleLength) { this.titleLength = titleLength; }

    public String getMetaDescription() { return metaDescription; }
    public void setMetaDescription(String metaDescription) { this.metaDescription = metaDescription; }

    public int getMetaDescriptionLength() { return metaDescriptionLength; }
    public void setMetaDescriptionLength(int metaDescriptionLength) { this.metaDescriptionLength = metaDescriptionLength; }

    public String getCanonicalUrl() { return canonicalUrl; }
    public void setCanonicalUrl(String canonicalUrl) { this.canonicalUrl = canonicalUrl; }

    public String getRobotsMeta() { return robotsMeta; }
    public void setRobotsMeta(String robotsMeta) { this.robotsMeta = robotsMeta; }

    public boolean isHttps() { return isHttps; }
    public void setHttps(boolean https) { isHttps = https; }

    public int getHttpStatusCode() { return httpStatusCode; }
    public void setHttpStatusCode(int httpStatusCode) { this.httpStatusCode = httpStatusCode; }

    public long getResponseTimeMs() { return responseTimeMs; }
    public void setResponseTimeMs(long responseTimeMs) { this.responseTimeMs = responseTimeMs; }

    public List<String> getH1Tags() { return h1Tags; }
    public void setH1Tags(List<String> h1Tags) { this.h1Tags = h1Tags; }

    public List<String> getH2Tags() { return h2Tags; }
    public void setH2Tags(List<String> h2Tags) { this.h2Tags = h2Tags; }

    public int getH1Count() { return h1Count; }
    public void setH1Count(int h1Count) { this.h1Count = h1Count; }

    public int getH2Count() { return h2Count; }
    public void setH2Count(int h2Count) { this.h2Count = h2Count; }

    public int getH3Count() { return h3Count; }
    public void setH3Count(int h3Count) { this.h3Count = h3Count; }

    public int getTotalImages() { return totalImages; }
    public void setTotalImages(int totalImages) { this.totalImages = totalImages; }

    public int getImagesMissingAlt() { return imagesMissingAlt; }
    public void setImagesMissingAlt(int imagesMissingAlt) { this.imagesMissingAlt = imagesMissingAlt; }

    public List<String> getMissingAltSources() { return missingAltSources; }
    public void setMissingAltSources(List<String> missingAltSources) { this.missingAltSources = missingAltSources; }

    public int getTotalInternalLinks() { return totalInternalLinks; }
    public void setTotalInternalLinks(int totalInternalLinks) { this.totalInternalLinks = totalInternalLinks; }

    public int getTotalExternalLinks() { return totalExternalLinks; }
    public void setTotalExternalLinks(int totalExternalLinks) { this.totalExternalLinks = totalExternalLinks; }

    public int getNofollowLinks() { return nofollowLinks; }
    public void setNofollowLinks(int nofollowLinks) { this.nofollowLinks = nofollowLinks; }

    public boolean isRobotsTxtFound() { return robotsTxtFound; }
    public void setRobotsTxtFound(boolean robotsTxtFound) { this.robotsTxtFound = robotsTxtFound; }

    public boolean isSitemapFound() { return sitemapFound; }
    public void setSitemapFound(boolean sitemapFound) { this.sitemapFound = sitemapFound; }

    public boolean isOpenGraphFound() { return openGraphFound; }
    public void setOpenGraphFound(boolean openGraphFound) { this.openGraphFound = openGraphFound; }

    public String getOgTitle() { return ogTitle; }
    public void setOgTitle(String ogTitle) { this.ogTitle = ogTitle; }

    public String getOgImage() { return ogImage; }
    public void setOgImage(String ogImage) { this.ogImage = ogImage; }

    public boolean isTwitterCardFound() { return twitterCardFound; }
    public void setTwitterCardFound(boolean twitterCardFound) { this.twitterCardFound = twitterCardFound; }

    public boolean isSchemaOrgFound() { return schemaOrgFound; }
    public void setSchemaOrgFound(boolean schemaOrgFound) { this.schemaOrgFound = schemaOrgFound; }

    public boolean isViewportPresent() { return viewportPresent; }
    public void setViewportPresent(boolean viewportPresent) { this.viewportPresent = viewportPresent; }

    public Map<String, Integer> getTopKeywords() { return topKeywords; }
    public void setTopKeywords(Map<String, Integer> topKeywords) { this.topKeywords = topKeywords; }
}
