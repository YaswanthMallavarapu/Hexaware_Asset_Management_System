package com.asset.demo.repository;

import com.asset.demo.enums.AccountStatus;
import com.asset.demo.enums.UserStatus;
import com.asset.demo.model.Employee;
import com.asset.demo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {

    @Query("""
       select e from Employee e
       where (?1 is null or e.user.accountStatus=?1) and
       (?2 is null or e.status=?2)
""")
    Page<Employee> getEmployeeByStatus(AccountStatus accountStatus,UserStatus status, Pageable pageable);

    @Query("""
      select e from Employee e
      where e.user.accountStatus=PENDING and e.user.role=EMPLOYEE
""")
    Page<Employee> getAllPendingEmployees(Pageable pageable);



    @Query("""
     select e from Employee e
     where e.user.username=?1
""")
    Employee getEmployeeByUsername(String username);

    @Query("""
       select e from Employee e
       where e.user.username=?1
""")
    Employee findByUsername(String name);
}
