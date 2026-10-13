package com.analyzer.seo.service;

import com.analyzer.seo.entity.AuditReportEntity;
import com.analyzer.seo.model.AuditReport;
import com.analyzer.seo.model.LogEvent;
import com.analyzer.seo.repository.AuditReportRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class JobManagerService {

    private final Map<String, AuditReport> reports = new ConcurrentHashMap<>();
    private final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();
    private final AuditReportRepository auditReportRepository;
    private final ObjectMapper objectMapper;

    public JobManagerService(AuditReportRepository auditReportRepository, ObjectMapper objectMapper) {
        this.auditReportRepository = auditReportRepository;
        this.objectMapper = objectMapper;
    }

    public void registerJob(String jobId, String url) {
        reports.put(jobId, new AuditReport(jobId, url));
        emitters.put(jobId, new CopyOnWriteArrayList<>());
    }

    public AuditReport getReport(String jobId) {
        AuditReport report = reports.get(jobId);
        if (report != null) {
            return report;
        }

        // Cerca nel database H2 persistente
        Optional<AuditReportEntity> entityOpt = auditReportRepository.findById(jobId);
        if (entityOpt.isPresent()) {
            try {
                AuditReport loaded = objectMapper.readValue(entityOpt.get().getReportJson(), AuditReport.class);
                reports.put(jobId, loaded);
                return loaded;
            } catch (Exception e) {
                // Se fallisce la deserializzazione, restituisci comunque le info minime
                AuditReport fallback = new AuditReport(entityOpt.get().getId(), entityOpt.get().getTargetUrl());
                fallback.setAnalyzedAt(entityOpt.get().getAnalyzedAt());
                fallback.setStatus(entityOpt.get().getStatus());
                fallback.setOverallScore(entityOpt.get().getOverallScore());
                fallback.setSeoScore(entityOpt.get().getSeoScore());
                fallback.setSemScore(entityOpt.get().getSemScore());
                fallback.setScreenshotUrl(entityOpt.get().getScreenshotUrl());
                return fallback;
            }
        }
        return null;
    }

    public void saveReport(AuditReport report) {
        reports.put(report.getId(), report);
        persistToDatabase(report);
    }

    public List<AuditReportEntity> getAllSavedReports() {
        return auditReportRepository.findAllByOrderByCreatedAtDesc();
    }

    public void deleteReport(String jobId) {
        reports.remove(jobId);
        emitters.remove(jobId);
        auditReportRepository.deleteById(jobId);
    }

    public SseEmitter subscribe(String jobId) {
        SseEmitter emitter = new SseEmitter(180_000L); // 3 minuti timeout
        List<SseEmitter> list = emitters.computeIfAbsent(jobId, k -> new CopyOnWriteArrayList<>());
        list.add(emitter);

        emitter.onCompletion(() -> list.remove(emitter));
        emitter.onTimeout(() -> list.remove(emitter));
        emitter.onError(e -> list.remove(emitter));

        // Invia log pregressi se presenti
        AuditReport report = getReport(jobId);
        if (report != null) {
            for (LogEvent prevLog : report.getLogs()) {
                try {
                    emitter.send(SseEmitter.event().name("log").data(prevLog));
                } catch (IOException e) {
                    list.remove(emitter);
                    break;
                }
            }
            if ("COMPLETED".equalsIgnoreCase(report.getStatus()) || "FAILED".equalsIgnoreCase(report.getStatus())) {
                try {
                    emitter.send(SseEmitter.event().name("complete").data(report));
                } catch (IOException ignored) {}
            }
        }

        return emitter;
    }

    public void emitLog(String jobId, String phase, String message, String level, int progress) {
        LogEvent event = new LogEvent(phase, message, level, progress);
        AuditReport report = reports.get(jobId);
        if (report != null) {
            report.getLogs().add(event);
        }

        List<SseEmitter> list = emitters.get(jobId);
        if (list != null) {
            for (SseEmitter emitter : list) {
                try {
                    emitter.send(SseEmitter.event().name("log").data(event));
                } catch (Exception e) {
                    list.remove(emitter);
                }
            }
        }
    }

    public void completeJob(String jobId, AuditReport finalReport) {
        reports.put(jobId, finalReport);
        persistToDatabase(finalReport);

        List<SseEmitter> list = emitters.get(jobId);
        if (list != null) {
            for (SseEmitter emitter : list) {
                try {
                    emitter.send(SseEmitter.event().name("complete").data(finalReport));
                    emitter.complete();
                } catch (Exception e) {
                    list.remove(emitter);
                }
            }
        }
    }

    public void failJob(String jobId, String errorMessage) {
        emitLog(jobId, "ERRORE", errorMessage, "ERROR", 100);
        AuditReport report = reports.get(jobId);
        if (report != null) {
            report.setStatus("FAILED");
            persistToDatabase(report);
        }
        List<SseEmitter> list = emitters.get(jobId);
        if (list != null) {
            for (SseEmitter emitter : list) {
                try {
                    emitter.send(SseEmitter.event().name("error").data(errorMessage));
                    emitter.complete();
                } catch (Exception ignored) {}
            }
        }
    }

    private void persistToDatabase(AuditReport report) {
        try {
            String json = objectMapper.writeValueAsString(report);
            AuditReportEntity entity = new AuditReportEntity(
                    report.getId(),
                    report.getTargetUrl(),
                    report.getAnalyzedAt(),
                    report.getStatus(),
                    report.getOverallScore(),
                    report.getSeoScore(),
                    report.getSemScore(),
                    report.getScreenshotUrl(),
                    json
            );
            auditReportRepository.save(entity);
        } catch (Exception e) {
            System.err.println("Errore durante il salvataggio persistente su DB: " + e.getMessage());
        }
    }
}
