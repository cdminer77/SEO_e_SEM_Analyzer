package com.analyzer.seo.service;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class CrawlerService {

    private static final String DEFAULT_USER_AGENT = 
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36 SeoSemAnalyzerBot/1.0";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public record CrawlResult(
            String normalizedUrl,
            Document document,
            int statusCode,
            long responseTimeMs,
            boolean isHttps,
            boolean robotsTxtFound,
            boolean sitemapFound
    ) {}

    public String normalizeUrl(String inputUrl) {
        String url = inputUrl.trim();
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        return url;
    }

    public CrawlResult crawl(String targetUrl) throws Exception {
        String url = normalizeUrl(targetUrl);
        boolean isHttps = url.toLowerCase().startsWith("https://");

        long start = System.currentTimeMillis();
        Connection.Response response = Jsoup.connect(url)
                .userAgent(DEFAULT_USER_AGENT)
                .timeout(15000)
                .followRedirects(true)
                .ignoreHttpErrors(true)
                .execute();
        long responseTime = System.currentTimeMillis() - start;

        Document doc = response.parse();
        int statusCode = response.statusCode();

        // Verifica robots.txt e sitemap.xml
        URI uri = URI.create(url);
        String hostBase = uri.getScheme() + "://" + uri.getHost();
        if (uri.getPort() != -1 && uri.getPort() != 80 && uri.getPort() != 443) {
            hostBase += ":" + uri.getPort();
        }

        boolean robotsFound = checkUrlExists(hostBase + "/robots.txt");
        boolean sitemapFound = checkUrlExists(hostBase + "/sitemap.xml");

        return new CrawlResult(
                url,
                doc,
                statusCode,
                responseTime,
                isHttps,
                robotsFound,
                sitemapFound
        );
    }

    private boolean checkUrlExists(String urlString) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlString))
                    .timeout(Duration.ofSeconds(5))
                    .header("User-Agent", DEFAULT_USER_AGENT)
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<Void> resp = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            return resp.statusCode() >= 200 && resp.statusCode() < 400;
        } catch (Exception e) {
            return false;
        }
    }
}
