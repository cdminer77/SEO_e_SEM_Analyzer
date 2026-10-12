package com.analyzer.seo.service;

import com.analyzer.seo.model.AuditIssue;
import com.analyzer.seo.model.SemMetrics;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
public class SemAuditService {

    public record SemResult(SemMetrics metrics, List<AuditIssue> issues) {}

    public SemResult analyze(Document doc, String targetUrl) {
        SemMetrics metrics = new SemMetrics();
        List<AuditIssue> issues = new ArrayList<>();
        int score = 100;

        String html = doc.html();
        Elements scripts = doc.select("script");
        List<String> detectedPixels = new ArrayList<>();

        // 1. Google Tag Manager (GTM)
        boolean hasGtm = html.contains("googletagmanager.com/gtm.js") || html.contains("GTM-");
        metrics.setGoogleTagManagerFound(hasGtm);
        if (hasGtm) {
            detectedPixels.add("Google Tag Manager (GTM)");
            issues.add(new AuditIssue("SEM", AuditIssue.Severity.GOOD, "Google Tag Manager presente", "GTM facilita l'integrazione di tag di marketing senza toccare il codice sorgente."));
        } else {
            score -= 10;
            issues.add(new AuditIssue("SEM", AuditIssue.Severity.WARNING, "Google Tag Manager non rilevato", "Consigliato per gestire tracciamenti pubblicitari, eventi di conversione e pixel in modo centralizzato."));
        }

        // 2. Google Analytics 4 (GA4)
        boolean hasGa4 = html.contains("G-") || html.contains("gtag('config', 'G-") || html.contains("google-analytics.com/analytics.js");
        metrics.setGoogleAnalytics4Found(hasGa4);
        if (hasGa4) {
            detectedPixels.add("Google Analytics 4 (GA4)");
            issues.add(new AuditIssue("SEM", AuditIssue.Severity.GOOD, "Google Analytics 4 rilevato", "Tracciamento del traffico e comportamento utente attivo."));
        } else {
            score -= 15;
            issues.add(new AuditIssue("SEM", AuditIssue.Severity.CRITICAL, "Google Analytics assente", "Nessun codice GA4 identificato. Fondamentale per misurare ROI delle campagne e conversioni."));
        }

        // 3. Meta Pixel (Facebook Ads)
        boolean hasMeta = html.contains("connect.facebook.net") || html.contains("fbq('init'") || html.contains("facebook.com/tr");
        metrics.setMetaPixelFound(hasMeta);
        if (hasMeta) {
            detectedPixels.add("Meta Pixel (Facebook/Instagram Ads)");
            issues.add(new AuditIssue("SEM", AuditIssue.Severity.GOOD, "Meta Pixel rilevato", "Abilita retargeting e tracciamento conversioni per campagne su Facebook e Instagram."));
        } else {
            score -= 10;
            issues.add(new AuditIssue("SEM", AuditIssue.Severity.WARNING, "Meta Pixel non presente", "Se svolgi campagne social a pagamento su Meta, è fondamentale installare il pixel."));
        }

        // 4. Google Ads Conversion Tracking
        boolean hasGAds = html.contains("AW-") || html.contains("googleadservices.com") || html.contains("google_conversion");
        metrics.setGoogleAdsConversionFound(hasGAds);
        if (hasGAds) {
            detectedPixels.add("Google Ads Conversion Tracking");
            issues.add(new AuditIssue("SEM", AuditIssue.Severity.GOOD, "Google Ads Tag rilevato", "Monitoraggio del conversion rate e Smart Bidding abilitati per campagne Google Ads."));
        } else {
            score -= 5;
            issues.add(new AuditIssue("SEM", AuditIssue.Severity.WARNING, "Tag Conversioni Google Ads non trovato", "Consigliato se intendi avviare campagne Search o Performance Max."));
        }

        // 5. TikTok Pixel
        boolean hasTiktok = html.contains("analytics.tiktok.com") || html.contains("ttq.load");
        metrics.setTikTokPixelFound(hasTiktok);
        if (hasTiktok) {
            detectedPixels.add("TikTok Pixel");
        }

        // 6. Hotjar o Microsoft Clarity
        boolean hasHeatmap = html.contains("hotjar.com") || html.contains("clarity.ms") || html.contains("clarity.load");
        metrics.setHotjarOrClarityFound(hasHeatmap);
        if (hasHeatmap) {
            detectedPixels.add("Heatmap & Session Recording (Hotjar/Clarity)");
            issues.add(new AuditIssue("SEM", AuditIssue.Severity.GOOD, "Strumento Heatmap/UX attivo", "Consente di analizzare l'esperienza utente sulla landing page per ottimizzare il tasso di conversione."));
        }

        // 7. LinkedIn Insight Tag
        boolean hasLinkedin = html.contains("snap.licdn.com") || html.contains("_linkedin_partner_id");
        metrics.setLinkedinInsightFound(hasLinkedin);
        if (hasLinkedin) {
            detectedPixels.add("LinkedIn Insight Tag");
        }

        metrics.setDetectedPixels(detectedPixels);

        // 8. Punti di Conversione & Call to Action
        Elements forms = doc.select("form");
        Elements phoneLinks = doc.select("a[href^='tel:']");
        Elements mailLinks = doc.select("a[href^='mailto:']");
        Elements waLinks = doc.select("a[href*='wa.me'], a[href*='api.whatsapp.com'], a[href*='whatsapp.com']");
        Elements ctas = doc.select("button, .btn, .button, .cta, a.cta, a.button, input[type=submit]");

        metrics.setFormCount(forms.size());
        metrics.setPhoneLinksCount(phoneLinks.size());
        metrics.setEmailLinksCount(mailLinks.size());
        metrics.setWhatsAppLinksCount(waLinks.size());
        metrics.setCtaButtonsCount(ctas.size());

        int conversionChannels = 0;
        if (forms.size() > 0) conversionChannels++;
        if (phoneLinks.size() > 0) conversionChannels++;
        if (waLinks.size() > 0) conversionChannels++;
        if (mailLinks.size() > 0) conversionChannels++;

        if (conversionChannels >= 2) {
            issues.add(new AuditIssue("SEM", AuditIssue.Severity.GOOD, "Molteplici canali di conversione attivi", 
                    "Trovati " + conversionChannels + " canali di contatto (Form: " + forms.size() + 
                    ", Telefono: " + phoneLinks.size() + ", WhatsApp: " + waLinks.size() + ")."));
        } else if (conversionChannels == 1) {
            score -= 5;
            issues.add(new AuditIssue("SEM", AuditIssue.Severity.WARNING, "Canali di conversione limitati (1 solo canale)", 
                    "Diversificare i canali di contatto (es. aggiungere click-to-call o WhatsApp) aumenta il conversion rate fino al 30%."));
        } else {
            score -= 25;
            issues.add(new AuditIssue("SEM", AuditIssue.Severity.CRITICAL, "Nessun punto di conversione evidente", 
                    "Non sono stati individuati form di contatto, numeri telefonici click-to-call o link WhatsApp. Chi arriva da traffico a pagamento non ha un modo immediato per convertire!"));
        }

        // 9. Anteprima Simulatore Annuncio Google Ads
        String title = doc.title() != null ? doc.title().trim() : "Titolo della Landing Page";
        String metaDesc = "";
        Element metaDescElem = doc.selectFirst("meta[name=description i]");
        if (metaDescElem != null) {
            metaDesc = metaDescElem.attr("content").trim();
        }
        if (metaDesc.isEmpty()) {
            metaDesc = "Scopri i nostri servizi professionali. Visita il nostro sito per richiedere informazioni o un preventivo personalizzato.";
        }

        String domain = targetUrl;
        try {
            domain = URI.create(targetUrl).getHost();
        } catch (Exception ignored) {}

        metrics.setAdHeadline1(title.length() > 30 ? title.substring(0, 30) : title);
        metrics.setAdHeadline2("Richiedi Preventivo Gratuito");
        metrics.setAdDescription(metaDesc.length() > 90 ? metaDesc.substring(0, 87) + "..." : metaDesc);
        metrics.setDisplayUrl(domain);

        metrics.setScore(Math.max(0, Math.min(100, score)));
        return new SemResult(metrics, issues);
    }
}
