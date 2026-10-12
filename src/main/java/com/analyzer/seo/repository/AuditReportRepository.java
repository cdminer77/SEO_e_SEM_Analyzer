package com.analyzer.seo.repository;

import com.analyzer.seo.entity.AuditReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditReportRepository extends JpaRepository<AuditReportEntity, String> {
    List<AuditReportEntity> findAllByOrderByCreatedAtDesc();
}
