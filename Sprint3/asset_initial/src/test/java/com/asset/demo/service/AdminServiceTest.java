package com.asset.demo.service;

import com.asset.demo.dto.AdminReqDto;
import com.asset.demo.dto.AdminResDto;
import com.asset.demo.enums.AccountStatus;
import com.asset.demo.enums.Role;
import com.asset.demo.exceptions.InvalidFileFormatException;
import com.asset.demo.model.Admin;
import com.asset.demo.model.AdminDocument;
import com.asset.demo.model.Manager;
import com.asset.demo.model.User;
import com.asset.demo.repository.AdminDocumentRepository;
import com.asset.demo.repository.AdminRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private AdminDocumentRepository adminDocumentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserService userService;

    @Mock
    private ManagerService managerService;

    @Test
    public void addAdminTest() {

        AdminReqDto dto = new AdminReqDto("John", "Doe", "admin123", "password");

        User user = new User();
        user.setId(1L);
        user.setUsername("admin123");
        user.setRole(Role.ADMIN);
        user.setAccountStatus(AccountStatus.APPROVED);

        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userService.insertUser(any(User.class))).thenReturn(user);

        adminService.addAdmin(dto);

        verify(adminRepository, times(1)).save(any(Admin.class));
    }

    @Test
    public void approveManagerTest() {

        User managerUser = new User();
        managerUser.setId(2L);
        managerUser.setUsername("manager123");
        managerUser.setRole(Role.MANAGER);
        managerUser.setAccountStatus(AccountStatus.PENDING);

        Manager manager = new Manager();
        manager.setId(1L);
        manager.setUser(managerUser);

        User adminUser = new User();
        adminUser.setId(3L);
        adminUser.setUsername("admin123");
        adminUser.setRole(Role.ADMIN);
        adminUser.setAccountStatus(AccountStatus.APPROVED);

        Admin admin = new Admin();
        admin.setId(10L);
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setUser(adminUser);

        when(managerService.getManagerByGivenId(1L)).thenReturn(manager);
        when(adminRepository.getAdminByUsername("admin123")).thenReturn(admin);
        when(userService.insertUser(any(User.class))).thenReturn(managerUser);

        adminService.approveManager(1L, "admin123");

        verify(managerService, times(1)).updateManager(manager);
    }

    @Test
    public void getCountTest() {

        when(adminRepository.count()).thenReturn(5L);

        long result = adminService.getCount();

        Assertions.assertEquals(5L, result);
    }

    @Test
    public void getAllTest() {

        User user1 = new User();
        user1.setUsername("user1");
        user1.setRole(Role.ADMIN);

        Admin admin1 = new Admin();
        admin1.setId(1L);
        admin1.setFirstName("Alice");
        admin1.setLastName("Smith");
        admin1.setUser(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setRole(Role.ADMIN);

        Admin admin2 = new Admin();
        admin2.setId(2L);
        admin2.setFirstName("Bob");
        admin2.setLastName("Brown");
        admin2.setUser(user2);

        Page<Admin> page = new PageImpl<>(List.of(admin1, admin2));

        when(adminRepository.findAll(any(PageRequest.class))).thenReturn(page);

        var result = adminService.getAll("requester", 0, 5);

        Assertions.assertEquals(2, result.size());
    }

    @Test
    public void getOneTest() {

        User user = new User();
        user.setUsername("admin123");

        Admin admin = new Admin();
        admin.setId(10L);
        admin.setFirstName("Jane");
        admin.setLastName("Doe");
        admin.setUser(user);

        when(adminRepository.findByUsername("admin123")).thenReturn(admin);

        AdminResDto result = adminService.getOne("admin123");

        Assertions.assertEquals("admin123", result.username());
    }

    @Test
    public void uploadValidFileTest() throws IOException {

        User user = new User();
        user.setUsername("admin123");
        user.setRole(Role.ADMIN);

        Admin admin = new Admin();
        admin.setId(10L);
        admin.setFirstName("Jane");
        admin.setLastName("Doe");
        admin.setUser(user);

        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("photo.png");
        when(file.getBytes()).thenReturn("data".getBytes());

        when(adminRepository.getAdminByUsername("admin123")).thenReturn(admin);

        AdminDocument doc = new AdminDocument();
        doc.setAdmin(admin);
        doc.setProfileImage("photo.png");

        when(adminDocumentRepository.save(any(AdminDocument.class))).thenReturn(doc);

        var result = adminService.upload("admin123", file);

        Assertions.assertNotNull(result);
    }

    @Test
    public void uploadInvalidFileTest() {

        User user = new User();
        user.setUsername("admin123");

        Admin admin = new Admin();
        admin.setUser(user);

        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("file.pdf");

        when(adminRepository.getAdminByUsername("admin123")).thenReturn(admin);

        Assertions.assertThrows(
                InvalidFileFormatException.class,
                () -> adminService.upload("admin123", file)
        );
    }

    @Test
    public void getProfileTest() {

        when(adminRepository.getProfileUrl("admin123")).thenReturn("photo.png");

        String result = adminService.getProfile("admin123");

        Assertions.assertEquals("photo.png", result);
    }

    @Test
    public void getProfileNullTest() {

        when(adminRepository.getProfileUrl("admin123")).thenReturn(null);

        String result = adminService.getProfile("admin123");

        Assertions.assertNull(result);
    }
}