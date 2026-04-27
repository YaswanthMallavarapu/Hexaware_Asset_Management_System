package com.asset.demo.repository;

import com.asset.demo.model.AdminDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminDocumentRepository extends JpaRepository<AdminDocument,Long> {
}
