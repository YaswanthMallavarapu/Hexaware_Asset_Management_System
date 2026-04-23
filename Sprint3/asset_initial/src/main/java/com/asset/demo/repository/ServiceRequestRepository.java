package com.asset.demo.repository;

import com.asset.demo.enums.ServiceStatus;
import com.asset.demo.model.ServiceRequest;
import org.springframework.data.domain.ManagedTypes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest,Long> {


    @Query("""
      select sr from ServiceRequest sr
      where sr.employee.user.username=?1
""")
    Page<ServiceRequest> getServiceRequestByUser(String name, Pageable pageable);



    @Query("""
    select sr from ServiceRequest sr
    where sr.status=?1
""")
    Page<ServiceRequest> getByStatus(ServiceStatus serviceStatus, Pageable pageable);

    @Query("""
    select count(sr) from ServiceRequest sr
    where sr.employee.user.username=?1
""")
    long getCountByUser(String name);

    @Query("""
    select sr from ServiceRequest sr
    where sr.employee.user.username=?2
    and sr.status=?1
""")
    Page<ServiceRequest> getAllByUserStatus(ServiceStatus serviceStatus, String name, Pageable pageable);
}
