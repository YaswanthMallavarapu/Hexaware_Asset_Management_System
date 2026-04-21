package com.asset.demo.repository;

import com.asset.demo.enums.AllocationStatus;
import com.asset.demo.model.AssetAllocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AssetAllocationRepository extends JpaRepository<AssetAllocation,Long> {

    @Query("""
         select aa from AssetAllocation aa
         where aa.employee.user.username=?1
""")
    Page<AssetAllocation> getByUsername(String name, Pageable pageable);

    @Query("""
      select aa from AssetAllocation aa
      where aa.status=AllocationStatus.ALLOCATED
""")
    List<AssetAllocation> getAllAllocatedAssets();

    @Query("""
    select aa from AssetAllocation aa
    where aa.status=?1
""")
    Page<AssetAllocation> getAllByStatus(AllocationStatus status1, Pageable pageable);

    @Query("""
    select count(*) from AssetAllocation aa
    where aa.employee.user.username=?1
""")
    long getCountByUser(String name);


    @Query("""
    select ar from AssetAllocation ar
    where ar.employee.user.username=?1
    and ar.status=?2

""")
    Page<AssetAllocation> findByUserStatus(String name, AllocationStatus allocationStatus, Pageable pageable);
}
