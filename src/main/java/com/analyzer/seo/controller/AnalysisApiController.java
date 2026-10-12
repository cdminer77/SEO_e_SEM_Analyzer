package com.analyzer.seo.controller;

import com.analyzer.seo.model.AuditReport;
import com.analyzer.seo.service.JobManagerService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api")
public class AnalysisApiController {

    private final JobManagerService jobManagerService;

    public AnalysisApiController(JobManagerService jobManagerService) {
        this.jobManagerService = jobManagerService;
    }

    @GetMapping(value = "/stream/{jobId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamJobEvents(@PathVariable("jobId") String jobId) {
        return jobManagerService.subscribe(jobId);
    }

    @GetMapping(value = "/report/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuditReport> getReportJson(@PathVariable("jobId") String jobId) {
        AuditReport report = jobManagerService.getReport(jobId);
        if (report == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(report);
    }
}
