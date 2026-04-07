package com.asset.demo.service;

import com.asset.demo.dto.AdminReqDto;
import com.asset.demo.enums.AccountStatus;
import com.asset.demo.enums.Role;
import com.asset.demo.model.Admin;
import com.asset.demo.model.Manager;
import com.asset.demo.model.User;
import com.asset.demo.repository.AdminRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserService userService;

    @Mock
    private ManagerService managerService;

    @Test
    public void addAdminTest(){

        AdminReqDto dto = new AdminReqDto(
                "John",
                "Doe",
                "admin123",
                "password123"
        );

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("admin123");
        savedUser.setAccountStatus(AccountStatus.APPROVED);
        savedUser.setRole(Role.ADMIN);

        when(userService.insertUser(any(User.class)))
                .thenReturn(savedUser);

        adminService.addAdmin(dto);

        ArgumentCaptor<Admin> captor = ArgumentCaptor.forClass(Admin.class);

        verify(adminRepository).save(captor.capture());

        Admin savedAdmin = captor.getValue();

        Assertions.assertEquals(savedUser, savedAdmin.getUser());
        Assertions.assertEquals("John", savedAdmin.getFirstName());
        Assertions.assertEquals("Doe", savedAdmin.getLastName());
        Assertions.assertEquals(Role.ADMIN,savedAdmin.getUser().getRole());
    }

    @Test
    public void approveManagerTest(){

        User managerUser = new User();
        managerUser.setId(2L);
        managerUser.setUsername("manager123");
        managerUser.setAccountStatus(AccountStatus.PENDING);

        Manager manager = new Manager();
        manager.setId(1L);
        manager.setUser(managerUser);

        Admin admin = new Admin();
        admin.setId(10L);
        admin.setFirstName("Admin");
        admin.setLastName("User");

        when(managerService.getManagerByGivenId(1L))
                .thenReturn(manager);

        when(adminRepository.getAdminByUsername("admin123"))
                .thenReturn(admin);

        when(userService.insertUser(any(User.class)))
                .thenReturn(managerUser);

        adminService.approveManager(1L,"admin123");

        Assertions.assertEquals(admin, manager.getAdmin());
        Assertions.assertEquals(AccountStatus.APPROVED,
                manager.getUser().getAccountStatus());

        verify(userService,times(1)).insertUser(managerUser);
        verify(managerService,times(1)).updateManager(manager);
    }

}