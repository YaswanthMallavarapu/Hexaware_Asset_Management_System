package com.asset.demo.model;


import com.asset.demo.enums.ServiceStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "service_request")
public class ServiceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ---------------- ASSET ----------------
    @NotNull(message = "Asset is required")
    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    // ---------------- EMPLOYEE ----------------
    @NotNull(message = "Employee is required")
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Manager manager;

    @OneToOne
    @JoinColumn(name = "asset_allocation_id")
    private AssetAllocation assetAllocation;

    // ---------------- DESCRIPTION ----------------
    @Size(max = 1000)
    @Column(columnDefinition = "TEXT")
    private String description;

    // ---------------- REQUEST DATE ----------------
    @CreationTimestamp
    @Column(name = "request_date", updatable = false)
    private Instant requestDate;

    // ---------------- STATUS ----------------
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private ServiceStatus status = ServiceStatus.OPEN;

    // ---------------- UPDATED AT ----------------
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}