package com.analyzer.seo.service;

import com.analyzer.seo.model.RecommendationItem;
import com.analyzer.seo.model.RecommendationItem.Priority;
import com.analyzer.seo.model.SeoMetrics;
import com.analyzer.seo.model.SemMetrics;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendationEngine {

    public List<RecommendationItem> generateRecommendations(SeoMetrics seo, SemMetrics sem, String url) {
        List<RecommendationItem> list = new ArrayList<>();

        // 1. Meta Title
        if (seo.getTitle() == null || seo.getTitle().isBlank()) {
            list.add(new RecommendationItem(
                    "REC-SEO-01", "SEO Tecnico", Priority.ALTA,
                    "Aggiungi un Tag <title> univoco ed efficace",
                    "Il titolo è il fattore on-page più rilevante per Google e definisce l'headline nei risultati di ricerca organici.",
                    "Inserisci il tag <title> nella sezione <head> della pagina con la parola chiave primaria a inizio testo, rimanendo tra i 45 e i 60 caratteri.",
                    "<head>\n    <title>Keyword Principale - Nome Brand | Servizio</title>\n</head>"
            ));
        } else if (seo.getTitleLength() < 30 || seo.getTitleLength() > 65) {
            list.add(new RecommendationItem(
                    "REC-SEO-02", "SEO On-Page", Priority.MEDIA,
                    "Ottimizza la lunghezza del Tag <title> (Attuale: " + seo.getTitleLength() + " car.)",
                    "Titoli troppo brevi perdono opportunità di ranking su query correlate; titoli oltre 65 caratteri vengono troncati nelle SERP con puntini di sospensione.",
                    "Riformula il titolo per avere una lunghezza ideale compresa tra 45 e 60 caratteri.",
                    "<!-- Esempio consigliato -->\n<title>Soluzioni Digitali e Software su Misura | Studio Web</title>"
            ));
        }

        // 2. Meta Description
        if (seo.getMetaDescription() == null || seo.getMetaDescription().isBlank()) {
            list.add(new RecommendationItem(
                    "REC-SEO-03", "SEO On-Page", Priority.ALTA,
                    "Inserisci la Meta Description con Call To Action",
                    "Senza una meta description compilata, Google genera automaticamente snippet spesso poco accattivanti, abbassando sensibilmente il Click-Through-Rate (CTR).",
                    "Aggiungi un riassunto persuasivo di circa 130-155 caratteri che inviti al clic.",
                    "<meta name=\"description\" content=\"Scopri come aumentare la visibilità del tuo sito con la nostra analisi professionale. Richiedi subito una consulenza gratuita!\" />"
            ));
        }

        // 3. Headings H1
        if (seo.getH1Count() == 0) {
            list.add(new RecommendationItem(
                    "REC-SEO-04", "SEO Semantico", Priority.ALTA,
                    "Inserisci il titolo principale <h1> nella pagina",
                    "L'assenza di un tag H1 impedisce ai bot di identificare a colpo d'occhio il topic cardine della pagina.",
                    "Aggiungi esattamente un tag <h1> nella parte superiore del contenuto principale.",
                    "<h1>Sviluppo Applicazioni Web e Strategie SEO Avanzate</h1>"
            ));
        } else if (seo.getH1Count() > 1) {
            list.add(new RecommendationItem(
                    "REC-SEO-05", "SEO Semantico", Priority.BASSA,
                    "Riduci i tag <h1> ad uno solo principale (Rilevati: " + seo.getH1Count() + ")",
                    "Avere più tag <h1> confonde la gerarchia semantica della pagina. È preferibile strutturare i sotto-paragrafi con <h2> e <h3>.",
                    "Mantieni l'H1 per il titolo dominante e trasforma gli altri H1 in <h2> o <h3>.",
                    "<h1>Titolo Principale della Pagina</h1>\n<h2>Primo Argomento di Approfondimento</h2>\n<h2>Secondo Argomento</h2>"
            ));
        }

        // 4. Immagini e ALT
        if (seo.getImagesMissingAlt() > 0) {
            list.add(new RecommendationItem(
                    "REC-SEO-06", "Accessibilità & Immagini", Priority.MEDIA,
                    "Compila il testo alternativo 'alt' per " + seo.getImagesMissingAlt() + " immagini",
                    "Gli attributi alt consentono l'indicizzazione su Google Immagini e rendono la pagina accessibile per screen reader e utenti con disabilità visiva.",
                    "Aggiungi un attributo alt descrittivo a ciascun tag <img>.",
                    "<img src=\"/images/dashboard-analytics.png\" alt=\"Schermata della dashboard di analisi SEO con grafici in tempo reale\" />"
            ));
        }

        // 5. Open Graph & Social Cards
        if (!seo.isOpenGraphFound()) {
            list.add(new RecommendationItem(
                    "REC-SEO-07", "Social SEO & Condivisione", Priority.BASSA,
                    "Configura i Meta Tag Open Graph (og:title, og:image)",
                    "Quando la pagina viene condivisa su WhatsApp, LinkedIn o Facebook, una card accattivante con immagine aumenta drasticamente i clic organici.",
                    "Inserisci i tag Open Graph all'interno dell'<head>.",
                    "<meta property=\"og:title\" content=\"Titolo di Impatto per i Social\" />\n<meta property=\"og:description\" content=\"Descrizione accattivante per le condivisioni social.\" />\n<meta property=\"og:image\" content=\"https://tuodominio.it/assets/anteprima-social.jpg\" />\n<meta property=\"og:type\" content=\"website\" />"
            ));
        }

        // 6. Schema.org / Dati Strutturati
        if (!seo.isSchemaOrgFound()) {
            list.add(new RecommendationItem(
                    "REC-SEO-08", "Rich Snippets & AI", Priority.MEDIA,
                    "Integra i Dati Strutturati Schema.org (JSON-LD)",
                    "I dati strutturati aiutano Google e gli assistenti AI a interpretare entità, prezzi, recensioni, breadcrumbs e FAQ, sbloccando i Rich Snippets grafici in SERP.",
                    "Aggiungi uno script JSON-LD di tipo Organization o WebSite prima della chiusura del tag </body> o dentro <head>.",
                    "<script type=\"application/ld+json\">\n{\n  \"@context\": \"https://schema.org\",\n  \"@type\": \"WebSite\",\n  \"name\": \"Nome Del Tuo Sito\",\n  \"url\": \"" + url + "\"\n}\n</script>"
            ));
        }

        // 7. SEM: Google Analytics & GTM
        if (!sem.isGoogleAnalytics4Found()) {
            list.add(new RecommendationItem(
                    "REC-SEM-01", "SEM & Tracking", Priority.ALTA,
                    "Installa Google Analytics 4 (GA4) per tracciare le conversioni",
                    "Senza analytics non è possibile calcolare il ROI delle campagne adv, il bounce rate o il percorso degli utenti paganti.",
                    "Crea una proprietà su Google Analytics 4 e incolla il tag Google gtag.js in testa a tutte le pagine, oppure usa Google Tag Manager.",
                    "<!-- Google tag (gtag.js) -->\n<script async src=\"https://www.googletagmanager.com/gtag/js?id=G-XXXXXXXXXX\"></script>\n<script>\n  window.dataLayer = window.dataLayer || [];\n  function gtag(){dataLayer.push(arguments);}\n  gtag('js', new Date());\n  gtag('config', 'G-XXXXXXXXXX');\n</script>"
            ));
        }

        // 8. SEM: Conversioni e Lead Gen
        if (sem.getPhoneLinksCount() == 0 && sem.getWhatsAppLinksCount() == 0 && sem.getFormCount() == 0) {
            list.add(new RecommendationItem(
                    "REC-SEM-02", "SEM & CRO (Tasso di Conversione)", Priority.ALTA,
                    "Aggiungi Call to Action dirette (Click-to-Call o WhatsApp)",
                    "Il traffico proveniente da campagne sponsorizzate (Google Ads, Meta) ha un costo per clic: se non trova un metodo di contatto immediato da smartphone, abbandona la pagina.",
                    "Aggiungi un pulsante di contatto mobile fluttuante o visibile 'above the fold'.",
                    "<a href=\"tel:+390123456789\" class=\"btn btn-primary\">Chiama Ora</a>\n<a href=\"https://wa.me/390123456789?text=Vorrei%20informazioni\" class=\"btn btn-success\">Scrivici su WhatsApp</a>"
            ));
        }

        // 9. SEM: Retargeting Meta Pixel
        if (!sem.isMetaPixelFound()) {
            list.add(new RecommendationItem(
                    "REC-SEM-03", "SEM & Remarketing", Priority.BASSA,
                    "Valuta l'installazione del Meta Pixel per campagne di Remarketing",
                    "Il Meta Pixel consente di ri-mostrare annunci su Instagram e Facebook a tutti gli utenti che hanno già visitato il sito ma non hanno completato una richiesta.",
                    "Installa il frammento di codice fornito da Meta Business Manager all'interno di <head>.",
                    "<!-- Meta Pixel Code -->\n<script>\n!function(f,b,e,v,n,t,s){if(f.fbq)return;n=f.fbq=function(){n.callMethod?\nn.callMethod.apply(n,arguments):n.queue.push(arguments)};\n/* Inizializzazione Pixel */\n}(window, document,'script','https://connect.facebook.net/en_US/fbevents.js');\nfbq('init', 'TUO_PIXEL_ID');\nfbq('track', 'PageView');\n</script>"
            ));
        }

        return list;
    }
}
