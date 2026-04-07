package com.asset.demo.model;

import com.asset.demo.enums.AllocationStatus;
import com.asset.demo.enums.ReturnRequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "asset_allocation")
public class AssetAllocation {

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
    @JoinColumn(name = "manager_id", nullable = false)
    private Manager manager;
    // ---------------- ALLOCATION DATE ----------------
    @CreationTimestamp

    @Column(name = "allocation_date")
    private LocalDate allocationDate;

    // ---------------- RETURN DATE ----------------
    @Column(name = "return_date")
    private LocalDate returnDate;

    // ---------------- STATUS ----------------
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private AllocationStatus status = AllocationStatus.ALLOCATED;
}