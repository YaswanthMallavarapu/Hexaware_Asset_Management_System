package com.asset.demo.repository;

import com.asset.demo.enums.AssetStatus;
import com.asset.demo.model.Asset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AssetRepository extends JpaRepository<Asset,Long> {

    @Query("select a from Asset a where a.category.id=?1")
    List<Asset> getAssetByCategory(long categoryId);

    @Query("""
         select a from Asset a
                  where a.status=?1
         """)
    Page<Asset> getByStatus(AssetStatus assetStatus, Pageable pageable);
}
