package com.asset.demo.model;

import com.asset.demo.enums.AuditStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "asset_audit")
public class AssetAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // ---------------- AUDIT DATE ----------------
    @CreationTimestamp
    @Column(name = "audit_date", updatable = false)
    private Instant auditDate;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Manager manager;
}