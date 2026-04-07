package com.asset.demo.repository;

import com.asset.demo.enums.AssetStatus;
import com.asset.demo.enums.RequestStatus;
import com.asset.demo.model.Asset;
import com.asset.demo.model.AssetRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AssetRequestRepository extends JpaRepository<AssetRequest,Long> {

    @Query("""
         select a from AssetRequest a
                  where a.employee.user.username=?1
         """)
    Page<AssetRequest> getByUsername(String name, Pageable pageable);

    @Query("""
    select ar from AssetRequest ar
    where ar.status=?1
""")
    Page<AssetRequest> getByStatus(RequestStatus requestStatus, Pageable pageable);
}
