package com.asset.demo.model;

import com.asset.demo.enums.AccountStatus;
import com.asset.demo.enums.Role;
import com.asset.demo.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(name="employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;
    private String gender;

    private String contactNumber;
    private String designation;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Manager manager;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    @CreationTimestamp
    private Instant createdAt;
    @UpdateTimestamp
    private Instant updatedAt;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;



}
