package com.analyzer.seo.model;

import java.util.ArrayList;
import java.util.List;

public class SemMetrics {
    private int score; // 0 - 100

    // Tracking & Pixels
    private boolean googleTagManagerFound;
    private boolean googleAnalytics4Found;
    private boolean metaPixelFound;
    private boolean googleAdsConversionFound;
    private boolean tikTokPixelFound;
    private boolean hotjarOrClarityFound;
    private boolean linkedinInsightFound;
    private List<String> detectedPixels = new ArrayList<>();

    // Conversion & CRO
    private int formCount;
    private int phoneLinksCount;
    private int emailLinksCount;
    private int whatsAppLinksCount;
    private int ctaButtonsCount;

    // Google Ads SERP Preview simulator
    private String adHeadline1;
    private String adHeadline2;
    private String adDescription;
    private String displayUrl;

    public SemMetrics() {}

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public boolean isGoogleTagManagerFound() { return googleTagManagerFound; }
    public void setGoogleTagManagerFound(boolean googleTagManagerFound) { this.googleTagManagerFound = googleTagManagerFound; }

    public boolean isGoogleAnalytics4Found() { return googleAnalytics4Found; }
    public void setGoogleAnalytics4Found(boolean googleAnalytics4Found) { this.googleAnalytics4Found = googleAnalytics4Found; }

    public boolean isMetaPixelFound() { return metaPixelFound; }
    public void setMetaPixelFound(boolean metaPixelFound) { this.metaPixelFound = metaPixelFound; }

    public boolean isGoogleAdsConversionFound() { return googleAdsConversionFound; }
    public void setGoogleAdsConversionFound(boolean googleAdsConversionFound) { this.googleAdsConversionFound = googleAdsConversionFound; }

    public boolean isTikTokPixelFound() { return tikTokPixelFound; }
    public void setTikTokPixelFound(boolean tikTokPixelFound) { this.tikTokPixelFound = tikTokPixelFound; }

    public boolean isHotjarOrClarityFound() { return hotjarOrClarityFound; }
    public void setHotjarOrClarityFound(boolean hotjarOrClarityFound) { this.hotjarOrClarityFound = hotjarOrClarityFound; }

    public boolean isLinkedinInsightFound() { return linkedinInsightFound; }
    public void setLinkedinInsightFound(boolean linkedinInsightFound) { this.linkedinInsightFound = linkedinInsightFound; }

    public List<String> getDetectedPixels() { return detectedPixels; }
    public void setDetectedPixels(List<String> detectedPixels) { this.detectedPixels = detectedPixels; }

    public int getFormCount() { return formCount; }
    public void setFormCount(int formCount) { this.formCount = formCount; }

    public int getPhoneLinksCount() { return phoneLinksCount; }
    public void setPhoneLinksCount(int phoneLinksCount) { this.phoneLinksCount = phoneLinksCount; }

    public int getEmailLinksCount() { return emailLinksCount; }
    public void setEmailLinksCount(int emailLinksCount) { this.emailLinksCount = emailLinksCount; }

    public int getWhatsAppLinksCount() { return whatsAppLinksCount; }
    public void setWhatsAppLinksCount(int whatsAppLinksCount) { this.whatsAppLinksCount = whatsAppLinksCount; }

    public int getCtaButtonsCount() { return ctaButtonsCount; }
    public void setCtaButtonsCount(int ctaButtonsCount) { this.ctaButtonsCount = ctaButtonsCount; }

    public String getAdHeadline1() { return adHeadline1; }
    public void setAdHeadline1(String adHeadline1) { this.adHeadline1 = adHeadline1; }

    public String getAdHeadline2() { return adHeadline2; }
    public void setAdHeadline2(String adHeadline2) { this.adHeadline2 = adHeadline2; }

    public String getAdDescription() { return adDescription; }
    public void setAdDescription(String adDescription) { this.adDescription = adDescription; }

    public String getDisplayUrl() { return displayUrl; }
    public void setDisplayUrl(String displayUrl) { this.displayUrl = displayUrl; }
}
