package com.analyzer.seo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SeoSemAnalyzerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeoSemAnalyzerApplication.class, args);
    }
}
