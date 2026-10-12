package com.analyzer.seo.service;

import com.analyzer.seo.model.AuditIssue;
import com.analyzer.seo.model.SeoMetrics;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SeoAuditService {

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "il", "lo", "la", "i", "gli", "le", "un", "uno", "una", "di", "a", "da", "in", "con", "su",
            "per", "tra", "fra", "e", "ed", "o", "ma", "se", "che", "del", "della", "dello", "dei", "degli",
            "delle", "al", "allo", "alla", "ai", "agli", "alle", "dal", "dalla", "nel", "nella", "sul", "sulla",
            "non", "piu", "più", "anche", "come", "ci", "vi", "si", "mi", "ti", "ne", "the", "and", "or",
            "on", "at", "to", "for", "with", "an", "is", "are", "of", "by", "this", "that", "from"
    ));

    public record SeoResult(SeoMetrics metrics, List<AuditIssue> issues) {}

    public SeoResult analyze(Document doc, CrawlerService.CrawlResult crawl) {
        SeoMetrics metrics = new SeoMetrics();
        List<AuditIssue> issues = new ArrayList<>();
        int score = 100;

        // 1. Status Code & HTTPS
        metrics.setHttpStatusCode(crawl.statusCode());
        metrics.setResponseTimeMs(crawl.responseTimeMs());
        metrics.setHttps(crawl.isHttps());

        if (crawl.statusCode() == 200) {
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.GOOD, "Status Code HTTP 200 OK", "La pagina risponde con stato corretto senza errori 4xx o 5xx."));
        } else {
            score -= 25;
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.CRITICAL, "Stato HTTP Anomalo: " + crawl.statusCode(), "La pagina non restituisce uno status code 200 OK."));
        }

        if (crawl.isHttps()) {
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.GOOD, "Protocollo Sicuro HTTPS Attivo", "Il sito fa uso di cifratura SSL/TLS per proteggere gli utenti."));
        } else {
            score -= 20;
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.CRITICAL, "Protocollo HTTP non sicuro", "Il sito non usa HTTPS. I motori di ricerca penalizzano i siti non cifrati."));
        }

        // Tempo di risposta
        if (crawl.responseTimeMs() < 800) {
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.GOOD, "Tempo di risposta ottimo (" + crawl.responseTimeMs() + "ms)", "La prima risposta del server (TTFB) è molto rapida."));
        } else if (crawl.responseTimeMs() < 2000) {
            score -= 5;
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.WARNING, "Tempo di risposta medio (" + crawl.responseTimeMs() + "ms)", "Il caricamento della pagina potrebbe essere velocizzato ottimizzando cache o server."));
        } else {
            score -= 15;
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.CRITICAL, "Tempo di risposta lento (" + crawl.responseTimeMs() + "ms)", "Il server impiega più di 2 secondi per rispondere. Rischio di abbandono e penalizzazione SEO."));
        }

        // 2. Title Tag
        String title = doc.title() != null ? doc.title().trim() : "";
        metrics.setTitle(title);
        metrics.setTitleLength(title.length());

        if (title.isEmpty()) {
            score -= 20;
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.CRITICAL, "Tag Title mancante", "La pagina non ha un elemento <title>. È uno dei fattori di ranking on-page più critici."));
        } else if (title.length() < 30) {
            score -= 5;
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.WARNING, "Tag Title troppo corto (" + title.length() + " caratteri)", "La lunghezza ideale consigliata per Google è tra 30 e 60 caratteri."));
        } else if (title.length() > 65) {
            score -= 5;
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.WARNING, "Tag Title troppo lungo (" + title.length() + " caratteri)", "Il titolo supera i 60-65 caratteri e rischia di essere troncato nelle SERP."));
        } else {
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.GOOD, "Tag Title ottimale (" + title.length() + " caratteri)", "La lunghezza del titolo rispetta le best practice dei motori di ricerca."));
        }

        // 3. Meta Description
        Element metaDescElem = doc.selectFirst("meta[name=description i]");
        String metaDesc = metaDescElem != null ? metaDescElem.attr("content").trim() : "";
        metrics.setMetaDescription(metaDesc);
        metrics.setMetaDescriptionLength(metaDesc.length());

        if (metaDesc.isEmpty()) {
            score -= 15;
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.CRITICAL, "Meta Description mancante", "Manca la meta description. Google potrebbe generare uno snippet casuale nei risultati di ricerca."));
        } else if (metaDesc.length() < 70) {
            score -= 5;
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.WARNING, "Meta Description troppo corta (" + metaDesc.length() + " caratteri)", "Si consiglia una lunghezza compresa tra 120 e 160 caratteri per massimizzare il CTR."));
        } else if (metaDesc.length() > 165) {
            score -= 5;
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.WARNING, "Meta Description troppo lunga (" + metaDesc.length() + " caratteri)", "La descrizione supera i 160 caratteri e potrebbe apparire tagliata su Google."));
        } else {
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.GOOD, "Meta Description ottimale (" + metaDesc.length() + " caratteri)", "La lunghezza della descrizione è ideale per il CTR nelle SERP."));
        }

        // 4. Canonical URL
        Element canonicalElem = doc.selectFirst("link[rel=canonical]");
        String canonical = canonicalElem != null ? canonicalElem.attr("href").trim() : "";
        metrics.setCanonicalUrl(canonical);
        if (canonical.isEmpty()) {
            score -= 5;
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.WARNING, "Tag Canonical mancante", "Senza tag canonical, c'è rischio di contenuti duplicati per parametri URL o varianti HTTP/HTTPS."));
        } else {
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.GOOD, "Tag Canonical configurato", "Canonical presente: " + canonical));
        }

        // 5. Meta Robots
        Element robotsElem = doc.selectFirst("meta[name=robots i]");
        String robotsContent = robotsElem != null ? robotsElem.attr("content").trim() : "";
        metrics.setRobotsMeta(robotsContent);
        if (robotsContent.toLowerCase().contains("noindex")) {
            score -= 25;
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.CRITICAL, "Meta Robots contiene 'noindex'", "Attenzione! Il tag meta robots blocca l'indicizzazione nei motori di ricerca."));
        }

        // 6. Headings (H1, H2, H3)
        Elements h1s = doc.select("h1");
        Elements h2s = doc.select("h2");
        Elements h3s = doc.select("h3");
        metrics.setH1Count(h1s.size());
        metrics.setH2Count(h2s.size());
        metrics.setH3Count(h3s.size());
        metrics.setH1Tags(h1s.stream().map(Element::text).filter(s -> !s.isBlank()).collect(Collectors.toList()));
        metrics.setH2Tags(h2s.stream().map(Element::text).filter(s -> !s.isBlank()).limit(10).collect(Collectors.toList()));

        if (h1s.isEmpty()) {
            score -= 15;
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.CRITICAL, "Tag H1 mancante", "Nessun titolo principale <h1> individuato nella pagina."));
        } else if (h1s.size() > 1) {
            score -= 5;
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.WARNING, "Molteplici tag H1 (" + h1s.size() + ")", "È buona norma avere un solo <h1> principale che definisce l'argomento cardine della pagina."));
        } else {
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.GOOD, "Tag H1 presente e univoco", "H1 trovato: \"" + h1s.first().text() + "\""));
        }

        if (h2s.isEmpty()) {
            score -= 5;
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.WARNING, "Nessun tag H2 rilevato", "I sottotitoli <h2> aiutano la scansione del contenuto e migliorano il posizionamento semantico."));
        } else {
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.GOOD, "Gerarchia Headings H2 presente (" + h2s.size() + ")", "La pagina ha una buona suddivisione dei paragrafi."));
        }

        // 7. Immagini e Alt Tag
        Elements imgs = doc.select("img");
        metrics.setTotalImages(imgs.size());
        List<String> missingAlts = new ArrayList<>();
        for (Element img : imgs) {
            String alt = img.attr("alt");
            if (alt == null || alt.trim().isEmpty()) {
                missingAlts.add(img.attr("src"));
            }
        }
        metrics.setImagesMissingAlt(missingAlts.size());
        metrics.setMissingAltSources(missingAlts.stream().limit(5).collect(Collectors.toList()));

        if (missingAlts.isEmpty()) {
            if (imgs.size() > 0) {
                issues.add(new AuditIssue("SEO", AuditIssue.Severity.GOOD, "Tutte le immagini (" + imgs.size() + ") hanno l'attributo alt", "Ottima accessibilità e SEO per Google Immagini."));
            }
        } else {
            score -= Math.min(15, missingAlts.size() * 3);
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.WARNING, missingAlts.size() + " immagini senza testo alternativo (alt)", "Gli attributi alt sono essenziali per l'accessibilità e per il posizionamento su Google Immagini."));
        }

        // 8. Link interni ed esterni
        Elements links = doc.select("a[href]");
        int internalLinks = 0;
        int externalLinks = 0;
        int nofollowLinks = 0;
        String currentHost = "";
        try {
            currentHost = URI.create(crawl.normalizedUrl()).getHost();
        } catch (Exception ignored) {}

        for (Element link : links) {
            String href = link.attr("href").trim();
            String rel = link.attr("rel");
            if (rel.toLowerCase().contains("nofollow")) {
                nofollowLinks++;
            }
            if (href.startsWith("/") || href.startsWith("#") || (!currentHost.isEmpty() && href.contains(currentHost))) {
                internalLinks++;
            } else if (href.startsWith("http://") || href.startsWith("https://")) {
                externalLinks++;
            }
        }
        metrics.setTotalInternalLinks(internalLinks);
        metrics.setTotalExternalLinks(externalLinks);
        metrics.setNofollowLinks(nofollowLinks);

        if (internalLinks == 0) {
            score -= 5;
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.WARNING, "Nessun link interno trovato", "I link interni distribuiscono l'autorità SEO (PageRank) tra le pagine del sito."));
        } else {
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.GOOD, "Link interni individuati (" + internalLinks + ")", "Buona architettura di navigazione interna."));
        }

        // 9. Viewport (Mobile Friendly)
        Element viewport = doc.selectFirst("meta[name=viewport i]");
        boolean hasViewport = viewport != null && !viewport.attr("content").isBlank();
        metrics.setViewportPresent(hasViewport);
        if (!hasViewport) {
            score -= 15;
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.CRITICAL, "Meta tag Viewport mancante", "Senza meta viewport la pagina non è ottimizzata per dispositivi mobili (Mobile-First Indexing)."));
        } else {
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.GOOD, "Meta tag Viewport presente", "La pagina supporta il rendering responsive per smartphone e tablet."));
        }

        // 10. Open Graph & Twitter & Schema.org
        Element ogTitleElem = doc.selectFirst("meta[property=og:title i]");
        Element ogImgElem = doc.selectFirst("meta[property=og:image i]");
        metrics.setOpenGraphFound(ogTitleElem != null || ogImgElem != null);
        metrics.setOgTitle(ogTitleElem != null ? ogTitleElem.attr("content") : "");
        metrics.setOgImage(ogImgElem != null ? ogImgElem.attr("content") : "");

        Element twitterCard = doc.selectFirst("meta[name=twitter:card i]");
        metrics.setTwitterCardFound(twitterCard != null);

        boolean hasSchema = !doc.select("script[type=application/ld+json]").isEmpty() || !doc.select("[itemscope]").isEmpty();
        metrics.setSchemaOrgFound(hasSchema);

        if (metrics.isOpenGraphFound()) {
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.GOOD, "Tag Open Graph configurati", "I social network mostreranno anteprime ricche con titolo e immagine quando la pagina viene condivisa."));
        } else {
            score -= 5;
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.WARNING, "Tag Open Graph mancanti", "Consigliato aggiungere og:title e og:image per ottimizzare le condivisioni social."));
        }

        if (metrics.isSchemaOrgFound()) {
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.GOOD, "Dati strutturati Schema.org / JSON-LD trovati", "I motori di ricerca possono comprendere entità e generare Rich Snippets nelle SERP."));
        } else {
            score -= 5;
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.WARNING, "Dati strutturati JSON-LD assenti", "Aggiungere dati strutturati (Organization, WebSite o Article) aumenta la visibilità grafica nei risultati di ricerca."));
        }

        // 11. Robots.txt e Sitemap.xml
        metrics.setRobotsTxtFound(crawl.robotsTxtFound());
        metrics.setSitemapFound(crawl.sitemapFound());

        if (crawl.robotsTxtFound()) {
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.GOOD, "File robots.txt individuato", "Il crawler di Google può leggere le direttive di scansione del sito."));
        } else {
            score -= 5;
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.WARNING, "File robots.txt non trovato", "Consigliato avere un file robots.txt nella radice del dominio per guidare i motori di ricerca."));
        }

        if (crawl.sitemapFound()) {
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.GOOD, "File sitemap.xml individuato", "La mappa del sito consente a Google di scoprire tutte le URL in modo esaustivo."));
        } else {
            score -= 5;
            issues.add(new AuditIssue("SEO", AuditIssue.Severity.WARNING, "File sitemap.xml non rilevato all'indirizzo standard", "Assicurarsi di avere una sitemap XML e di inviarla a Google Search Console."));
        }

        // 12. Keyword density estrazione
        String bodyText = doc.body() != null ? doc.body().text().toLowerCase() : "";
        Map<String, Integer> freqMap = extractKeywordFrequency(bodyText);
        metrics.setTopKeywords(freqMap);

        metrics.setScore(Math.max(0, Math.min(100, score)));
        return new SeoResult(metrics, issues);
    }

    private Map<String, Integer> extractKeywordFrequency(String text) {
        String[] words = text.replaceAll("[^a-zA-Z0-9àèéìòùáéíóú]", " ").split("\\s+");
        Map<String, Integer> counts = new HashMap<>();
        for (String w : words) {
            if (w.length() >= 4 && !STOP_WORDS.contains(w)) {
                counts.put(w, counts.getOrDefault(w, 0) + 1);
            }
        }
        return counts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(8)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
}
