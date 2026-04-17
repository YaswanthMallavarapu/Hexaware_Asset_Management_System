package com.asset.demo.repository;

import com.asset.demo.model.AssetAudit;
import com.asset.demo.model.AssetAuditResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import com.asset.demo.dto.AuditDateDto;
public interface AssetAuditRepository extends JpaRepository<AssetAudit,Long> {

    @Query("""
    select new com.asset.demo.dto.AuditDateDto(a.id,a.manager.id,a.auditDate) from AssetAudit a
""")
    List<AuditDateDto> getAllAuditDates();


}
