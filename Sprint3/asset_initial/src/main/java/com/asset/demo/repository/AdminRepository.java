package com.asset.demo.repository;

import com.asset.demo.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AdminRepository extends JpaRepository<Admin,Long> {
    @Query("""
     select a from Admin a
     where a.user.username=?1
""")
    Admin getAdminByUsername(String username);
}
