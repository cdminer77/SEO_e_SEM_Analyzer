package com.analyzer.seo.controller;

import com.analyzer.seo.entity.AuditReportEntity;
import com.analyzer.seo.model.AuditReport;
import com.analyzer.seo.service.AnalysisExecutionService;
import com.analyzer.seo.service.JobManagerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
public class WebController {

    private final JobManagerService jobManagerService;
    private final AnalysisExecutionService analysisExecutionService;

    public WebController(JobManagerService jobManagerService, AnalysisExecutionService analysisExecutionService) {
        this.jobManagerService = jobManagerService;
        this.analysisExecutionService = analysisExecutionService;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<AuditReportEntity> history = jobManagerService.getAllSavedReports();
        model.addAttribute("history", history);
        return "index";
    }

    @PostMapping("/analyze")
    public String startAnalysis(@RequestParam("url") String url) {
        String cleanUrl = url != null ? url.trim() : "";
        if (cleanUrl.isEmpty()) {
            return "redirect:/?error=empty_url";
        }
        String jobId = UUID.randomUUID().toString().substring(0, 8);
        jobManagerService.registerJob(jobId, cleanUrl);
        analysisExecutionService.executeAnalysis(jobId, cleanUrl);
        return "redirect:/progress/" + jobId;
    }

    @GetMapping("/progress/{jobId}")
    public String progress(@PathVariable("jobId") String jobId, Model model) {
        AuditReport report = jobManagerService.getReport(jobId);
        if (report == null) {
            return "redirect:/?error=not_found";
        }
        model.addAttribute("jobId", jobId);
        model.addAttribute("targetUrl", report.getTargetUrl());
        return "progress";
    }

    @GetMapping("/report/{jobId}")
    public String report(@PathVariable("jobId") String jobId, Model model) {
        AuditReport report = jobManagerService.getReport(jobId);
        if (report == null) {
            return "redirect:/?error=not_found";
        }
        model.addAttribute("report", report);
        model.addAttribute("seo", report.getSeoMetrics());
        model.addAttribute("sem", report.getSemMetrics());
        return "report";
    }

    @PostMapping("/report/delete/{jobId}")
    public String deleteReport(@PathVariable("jobId") String jobId) {
        jobManagerService.deleteReport(jobId);
        return "redirect:/?deleted=true";
    }
}
