package com.analyzer.seo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String screenshotsPath = Paths.get("data", "screenshots").toAbsolutePath().toUri().toString();
        if (!screenshotsPath.endsWith("/")) {
            screenshotsPath += "/";
        }

        registry.addResourceHandler("/screenshots/**")
                .addResourceLocations(screenshotsPath);
    }
}
