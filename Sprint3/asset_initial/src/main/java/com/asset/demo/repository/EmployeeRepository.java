package com.asset.demo.repository;

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
       where e.status=?1
""")
    Page<Employee> getEmployeeByStatus(UserStatus status,Pageable pageable);

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
}
