package com.analyzer.seo.service;

import com.analyzer.seo.model.AuditIssue;
import com.analyzer.seo.model.AuditReport;
import com.analyzer.seo.model.RecommendationItem;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnalysisExecutionService {

    private final CrawlerService crawlerService;
    private final SeoAuditService seoAuditService;
    private final SemAuditService semAuditService;
    private final RecommendationEngine recommendationEngine;
    private final JobManagerService jobManagerService;

    public AnalysisExecutionService(CrawlerService crawlerService,
                                    SeoAuditService seoAuditService,
                                    SemAuditService semAuditService,
                                    RecommendationEngine recommendationEngine,
                                    JobManagerService jobManagerService) {
        this.crawlerService = crawlerService;
        this.seoAuditService = seoAuditService;
        this.semAuditService = semAuditService;
        this.recommendationEngine = recommendationEngine;
        this.jobManagerService = jobManagerService;
    }

    @Async("analysisTaskExecutor")
    public void executeAnalysis(String jobId, String rawUrl) {
        try {
            AuditReport report = jobManagerService.getReport(jobId);
            if (report == null) {
                jobManagerService.registerJob(jobId, rawUrl);
                report = jobManagerService.getReport(jobId);
            }
            report.setStatus("RUNNING");

            // FASE 1: Connessione
            jobManagerService.emitLog(jobId, "CONNESSIONE", "Normalizzazione URL e handshake di rete...", "INFO", 10);
            Thread.sleep(300);

            // FASE 2: Crawling & Download DOM
            jobManagerService.emitLog(jobId, "CRAWLING", "Scaricamento pagina con User-Agent Googlebot...", "INFO", 25);
            CrawlerService.CrawlResult crawl = crawlerService.crawl(rawUrl);
            report.setTargetUrl(crawl.normalizedUrl());

            jobManagerService.emitLog(jobId, "NETWORK", 
                    "Risposta HTTP " + crawl.statusCode() + " ricevuta in " + crawl.responseTimeMs() + "ms. SSL: " + (crawl.isHttps() ? "OK" : "NO"),
                    crawl.statusCode() == 200 ? "SUCCESS" : "WARNING", 35);

            Thread.sleep(400);

            // FASE 3: Verifica File Tecnici
            jobManagerService.emitLog(jobId, "FILE TECNICI", "Verifica presenza robots.txt e sitemap.xml...", "INFO", 45);
            jobManagerService.emitLog(jobId, "FILE TECNICI", 
                    "robots.txt: " + (crawl.robotsTxtFound() ? "Trovato" : "Non trovato") + 
                    " | sitemap.xml: " + (crawl.sitemapFound() ? "Trovato" : "Non trovato"),
                    "INFO", 55);

            Thread.sleep(400);

            // FASE 4: Audit SEO On-Page
            jobManagerService.emitLog(jobId, "AUDIT SEO", "Scansione Meta Tag, gerarchia Heading (H1-H6), tag ALT e link...", "INFO", 65);
            SeoAuditService.SeoResult seoResult = seoAuditService.analyze(crawl.document(), crawl);
            report.setSeoMetrics(seoResult.metrics());
            report.setSeoScore(seoResult.metrics().getScore());

            jobManagerService.emitLog(jobId, "AUDIT SEO", 
                    "Audit SEO completato. Calcolato SEO Score: " + seoResult.metrics().getScore() + "/100", 
                    "SUCCESS", 75);

            Thread.sleep(400);

            // FASE 5: Audit SEM & Conversion Tracking
            jobManagerService.emitLog(jobId, "AUDIT SEM", "Rilevamento Pixel (GTM, GA4, Meta, Google Ads) e punti di conversione...", "INFO", 85);
            SemAuditService.SemResult semResult = semAuditService.analyze(crawl.document(), crawl.normalizedUrl());
            report.setSemMetrics(semResult.metrics());
            report.setSemScore(semResult.metrics().getScore());

            jobManagerService.emitLog(jobId, "AUDIT SEM", 
                    "Audit SEM completato. Pixel individuati: " + semResult.metrics().getDetectedPixels().size() + 
                    ". SEM Score: " + semResult.metrics().getScore() + "/100", 
                    "SUCCESS", 90);

            Thread.sleep(300);

            // FASE 6: Aggregazione Issue e Generazione Raccomandazioni
            jobManagerService.emitLog(jobId, "GENERAZIONE REPORT", "Elaborazione raccomandazioni operative e calcolo punteggio globale...", "INFO", 95);
            List<AuditIssue> allIssues = new ArrayList<>();
            allIssues.addAll(seoResult.issues());
            allIssues.addAll(semResult.issues());
            report.setIssues(allIssues);

            List<RecommendationItem> recommendations = recommendationEngine.generateRecommendations(
                    seoResult.metrics(), semResult.metrics(), crawl.normalizedUrl()
            );
            report.setRecommendations(recommendations);

            int overall = (int) Math.round((seoResult.metrics().getScore() * 0.55) + (semResult.metrics().getScore() * 0.45));
            report.setOverallScore(overall);
            report.setStatus("COMPLETED");

            jobManagerService.emitLog(jobId, "COMPLETATO", 
                    "Analisi conclusa con successo! Punteggio complessivo: " + overall + "/100", 
                    "SUCCESS", 100);

            // Finalizza
            jobManagerService.completeJob(jobId, report);

        } catch (Exception e) {
            jobManagerService.failJob(jobId, "Errore durante l'analisi del sito: " + e.getMessage());
        }
    }
}