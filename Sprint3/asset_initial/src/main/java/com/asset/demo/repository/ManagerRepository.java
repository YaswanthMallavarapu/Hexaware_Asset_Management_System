package com.asset.demo.repository;

import com.asset.demo.model.Manager;
import com.asset.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ManagerRepository extends JpaRepository<Manager,Long> {

    @Query("""
     select m from Manager m
     where m.user.username=?1
""")
    Manager findByUsername(String username);

    @Query("""
    select m from Manager m
    where m.user.username=?1
""")
    Manager getManagerByUsername(String username);
}
