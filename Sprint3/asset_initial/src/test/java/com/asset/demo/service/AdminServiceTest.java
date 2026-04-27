package com.asset.demo.service;

import com.asset.demo.dto.AdminDto;
import com.asset.demo.dto.AdminReqDto;
import com.asset.demo.dto.AdminResDto;
import com.asset.demo.enums.AccountStatus;
import com.asset.demo.enums.Role;
import com.asset.demo.exceptions.InvalidFileFormatException;
import com.asset.demo.mapper.AdminDocumentResDto;
import com.asset.demo.model.Admin;
import com.asset.demo.model.AdminDocument;
import com.asset.demo.model.Manager;
import com.asset.demo.model.User;
import com.asset.demo.repository.AdminDocumentRepository;
import com.asset.demo.repository.AdminRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

    private User buildUser(long id, String username, Role role, AccountStatus status) {
        User u = new User();
        u.setId(id);
        u.setUsername(username);
        u.setRole(role);
        u.setAccountStatus(status);
        return u;
    }

    private Admin buildAdmin(long id, String first, String last, User user) {
        Admin a = new Admin();
        a.setId(id);
        a.setFirstName(first);
        a.setLastName(last);
        a.setUser(user);
        return a;
    }

    @Test
    public void addAdminTest() {

        AdminReqDto dto = new AdminReqDto(
                "John",
                "Doe",
                "admin123",
                "password123"
        );

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encodedPassword");

        User savedUser = buildUser(1L, "admin123", Role.ADMIN, AccountStatus.APPROVED);

        when(userService.insertUser(any(User.class)))
                .thenReturn(savedUser);

        adminService.addAdmin(dto);

        ArgumentCaptor<Admin> captor = ArgumentCaptor.forClass(Admin.class);
        verify(adminRepository).save(captor.capture());

        Admin savedAdmin = captor.getValue();

        Assertions.assertEquals(savedUser, savedAdmin.getUser());
        Assertions.assertEquals("John", savedAdmin.getFirstName());
        Assertions.assertEquals("Doe", savedAdmin.getLastName());
        Assertions.assertEquals(Role.ADMIN, savedAdmin.getUser().getRole());
        Assertions.assertEquals(AccountStatus.APPROVED, savedAdmin.getUser().getAccountStatus());
    }

    @Test
    public void approveManagerTest() {

        User managerUser = buildUser(2L, "manager123", Role.MANAGER, AccountStatus.PENDING);

        Manager manager = new Manager();
        manager.setId(1L);
        manager.setUser(managerUser);

        User adminUser = buildUser(3L, "admin123", Role.ADMIN, AccountStatus.APPROVED);
        Admin admin = buildAdmin(10L, "Admin", "User", adminUser);

        when(managerService.getManagerByGivenId(1L)).thenReturn(manager);
        when(adminRepository.getAdminByUsername("admin123")).thenReturn(admin);
        when(userService.insertUser(any(User.class))).thenReturn(managerUser);

        adminService.approveManager(1L, "admin123");

        Assertions.assertEquals(admin, manager.getAdmin());
        Assertions.assertEquals(AccountStatus.APPROVED, manager.getUser().getAccountStatus());

        verify(userService, times(1)).insertUser(managerUser);
        verify(managerService, times(1)).updateManager(manager);
    }

    @Test
    public void getCountTest() {

        when(adminRepository.count()).thenReturn(5L);

        long result = adminService.getCount();

        Assertions.assertEquals(5L, result);
        verify(adminRepository, times(1)).count();
    }

    @Test
    public void getAllTest() {

        User user1 = buildUser(1L, "user1", Role.ADMIN, AccountStatus.APPROVED);
        User user2 = buildUser(2L, "user2", Role.ADMIN, AccountStatus.APPROVED);

        Admin admin1 = buildAdmin(1L, "Alice", "Smith", user1);
        Admin admin2 = buildAdmin(2L, "Bob", "Brown", user2);

        Page<Admin> page = new PageImpl<>(List.of(admin1, admin2));

        when(adminRepository.findAll(any(PageRequest.class))).thenReturn(page);

        List<AdminDto> result = adminService.getAll("requester", 0, 5);

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Alice", result.get(0).firstName());
        Assertions.assertEquals("Smith", result.get(0).lastName());
        Assertions.assertEquals("user1", result.get(0).username());
        Assertions.assertEquals("Bob", result.get(1).firstName());
    }

    @Test
    public void getOneTest() {

        User user = buildUser(1L, "admin123", Role.ADMIN, AccountStatus.APPROVED);
        Admin admin = buildAdmin(10L, "Jane", "Doe", user);

        when(adminRepository.findByUsername("admin123")).thenReturn(admin);

        AdminResDto result = adminService.getOne("admin123");

        Assertions.assertEquals(10L, result.id());
        Assertions.assertEquals("Jane Doe", result.name());
        Assertions.assertEquals("admin123", result.username());
    }

    @Test
    public void uploadValidPngTest() throws IOException {

        User user = buildUser(1L, "admin123", Role.ADMIN, AccountStatus.APPROVED);
        Admin admin = buildAdmin(10L, "Jane", "Doe", user);

        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("photo.png");
        when(file.getBytes()).thenReturn("imageBytes".getBytes());

        when(adminRepository.getAdminByUsername("admin123")).thenReturn(admin);

        AdminDocument savedDoc = new AdminDocument();
        savedDoc.setAdmin(admin);
        savedDoc.setProfileImage("photo.png");

        when(adminDocumentRepository.save(any(AdminDocument.class))).thenReturn(savedDoc);

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.write(any(Path.class), any(byte[].class)))
                    .thenReturn(null);

            AdminDocumentResDto result = adminService.upload("admin123", file);

            ArgumentCaptor<AdminDocument> docCaptor = ArgumentCaptor.forClass(AdminDocument.class);
            verify(adminDocumentRepository).save(docCaptor.capture());

            AdminDocument captured = docCaptor.getValue();
            Assertions.assertEquals(admin, captured.getAdmin());
            Assertions.assertEquals("photo.png", captured.getProfileImage());

            Assertions.assertNotNull(result);
            Assertions.assertEquals("Jane Doe", result.fullName());
            Assertions.assertEquals("admin123", result.username());
            Assertions.assertEquals(Role.ADMIN, result.role());
            Assertions.assertEquals("photo.png", result.profile());
        }
    }

    @Test
    public void uploadValidJpgTest() throws IOException {

        User user = buildUser(1L, "admin123", Role.ADMIN, AccountStatus.APPROVED);
        Admin admin = buildAdmin(10L, "Jane", "Doe", user);

        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("photo.jpg");
        when(file.getBytes()).thenReturn("imageBytes".getBytes());

        when(adminRepository.getAdminByUsername("admin123")).thenReturn(admin);

        AdminDocument savedDoc = new AdminDocument();
        savedDoc.setAdmin(admin);
        savedDoc.setProfileImage("photo.jpg");

        when(adminDocumentRepository.save(any(AdminDocument.class))).thenReturn(savedDoc);

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.write(any(Path.class), any(byte[].class)))
                    .thenReturn(null);

            AdminDocumentResDto result = adminService.upload("admin123", file);

            Assertions.assertNotNull(result);
            Assertions.assertEquals("photo.jpg", result.profile());
        }
    }

    @Test
    public void uploadValidJpegTest() throws IOException {

        User user = buildUser(1L, "admin123", Role.ADMIN, AccountStatus.APPROVED);
        Admin admin = buildAdmin(10L, "Jane", "Doe", user);

        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("photo.jpeg");
        when(file.getBytes()).thenReturn("imageBytes".getBytes());

        when(adminRepository.getAdminByUsername("admin123")).thenReturn(admin);

        AdminDocument savedDoc = new AdminDocument();
        savedDoc.setAdmin(admin);
        savedDoc.setProfileImage("photo.jpeg");

        when(adminDocumentRepository.save(any(AdminDocument.class))).thenReturn(savedDoc);

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.write(any(Path.class), any(byte[].class)))
                    .thenReturn(null);

            AdminDocumentResDto result = adminService.upload("admin123", file);

            Assertions.assertNotNull(result);
            Assertions.assertEquals("photo.jpeg", result.profile());
        }
    }

    @Test
    public void uploadInvalidExtensionThrowsExceptionTest() {

        User user = buildUser(1L, "admin123", Role.ADMIN, AccountStatus.APPROVED);
        Admin admin = buildAdmin(10L, "Jane", "Doe", user);

        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("document.pdf");

        when(adminRepository.getAdminByUsername("admin123")).thenReturn(admin);

        Assertions.assertThrows(InvalidFileFormatException.class,
                () -> adminService.upload("admin123", file));

        verify(adminDocumentRepository, never()).save(any());
    }

    @Test
    public void getProfileTest() {

        when(adminRepository.getProfileUrl("admin123"))
                .thenReturn("photo.png");

        String result = adminService.getProfile("admin123");

        Assertions.assertEquals("photo.png", result);
        verify(adminRepository, times(1)).getProfileUrl("admin123");
    }

    @Test
    public void getProfileReturnsNullWhenNotFoundTest() {

        when(adminRepository.getProfileUrl("noone")).thenReturn(null);

        String result = adminService.getProfile("noone");

        Assertions.assertNull(result);
    }
}