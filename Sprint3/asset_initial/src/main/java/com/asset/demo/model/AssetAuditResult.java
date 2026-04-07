package com.asset.demo.model;

import com.asset.demo.enums.AuditStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "asset_audit_result")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AssetAuditResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @ManyToOne
    @JoinColumn(name = "audit_id")
    private AssetAudit audit;

    @ManyToOne
    @JoinColumn(name = "asset_allocation_id")
    private AssetAllocation assetAllocation;

    @CreationTimestamp
    private LocalDate auditDate;

    @Enumerated(EnumType.STRING)
    private AuditStatus status;

}