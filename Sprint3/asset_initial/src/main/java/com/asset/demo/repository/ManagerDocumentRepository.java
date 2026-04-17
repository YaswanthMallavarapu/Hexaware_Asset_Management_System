package com.asset.demo.repository;

import com.asset.demo.model.ManagerDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ManagerDocumentRepository extends JpaRepository<ManagerDocument,Long> {
    @Query("""
    select md.profileImage from ManagerDocument md
    where md.manager.id=?1
""")
    String getByManagerId(Long id);
}
