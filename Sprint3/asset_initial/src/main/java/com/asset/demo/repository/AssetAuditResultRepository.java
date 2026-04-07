package com.asset.demo.repository;

import com.asset.demo.model.AssetAuditResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AssetAuditResultRepository extends JpaRepository<AssetAuditResult,Long> {
    @Query("""
     select aar from AssetAuditResult aar
     where aar.audit.id=?1
""")
    Page<AssetAuditResult> getByAuditId(long auditId, Pageable pageable);
}
