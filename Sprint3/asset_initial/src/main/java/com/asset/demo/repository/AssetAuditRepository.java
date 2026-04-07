package com.asset.demo.repository;

import com.asset.demo.model.AssetAudit;
import com.asset.demo.model.AssetAuditResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface AssetAuditRepository extends JpaRepository<AssetAudit,Long> {

    @Query("""
    select a.auditDate from AssetAudit a
""")
    List<Instant> getAllAuditDates();


}
