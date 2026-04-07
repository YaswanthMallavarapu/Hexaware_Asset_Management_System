package com.asset.demo.model;

import com.asset.demo.enums.RequestStatus;
import com.asset.demo.enums.RequestType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "asset_request")
public class AssetRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull(message = "Employee is required")
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;


    @ManyToOne
    @JoinColumn(name = "asset_id")
    private Asset asset;


    // ---------------- REQUEST DATE ----------------
    @CreationTimestamp
    @Column(name = "request_date", updatable = false)
    private Instant requestDate;

    // ---------------- STATUS ----------------
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private RequestStatus status = RequestStatus.PENDING;

    // ---------------- REMARKS ----------------
    @Column(columnDefinition = "TEXT")
    private String remarks;


}