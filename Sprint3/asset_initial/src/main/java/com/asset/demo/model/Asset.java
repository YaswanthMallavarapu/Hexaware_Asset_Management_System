package com.asset.demo.model;

import com.asset.demo.enums.AssetStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "assets")
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // ---------------- ASSET NUMBER ----------------
    @NotBlank(message = "Asset number is required")
    @Size(max = 100)
    @Column(name = "asset_no", nullable = false, unique = true, length = 100)
    private String assetNo;

    // ---------------- ASSET NAME ----------------
    @NotBlank(message = "Asset name is required")
    @Size(max = 150)
    @Column(name = "asset_name", nullable = false, length = 150)
    private String assetName;

    // ---------------- MODEL ----------------
    @Size(max = 150)
    @Column(name = "asset_model", length = 150)
    private String assetModel;

    // ---------------- MANUFACTURING DATE ----------------
    @Column(name = "manufacturing_date")
    private LocalDate manufacturingDate;


    // ---------------- VALUE ----------------
    @DecimalMin(value = "0.0", inclusive = true, message = "Asset value must be positive")
    @Column(name = "asset_value", precision = 12, scale = 2)
    private BigDecimal assetValue;

    // ---------------- STATUS ----------------
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private AssetStatus status = AssetStatus.AVAILABLE;

    // ---------------- CATEGORY (Many Assets -> One Category) ----------------
    @ManyToOne
    @JoinColumn(name = "category_id")
    private AssetCategory category;

    // ---------------- CREATED AT ----------------
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}