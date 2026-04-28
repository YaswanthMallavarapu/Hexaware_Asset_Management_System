package com.asset.demo.service;

import com.asset.demo.dto.AdminDto;
import com.asset.demo.dto.AdminReqDto;
import com.asset.demo.dto.AdminResDto;
import com.asset.demo.enums.AccountStatus;
import com.asset.demo.enums.Role;
import com.asset.demo.exceptions.InvalidFileFormatException;
import com.asset.demo.mapper.AdminDocumentMapper;
import com.asset.demo.mapper.AdminDocumentResDto;
import com.asset.demo.mapper.AdminMapper;
import com.asset.demo.model.Admin;
import com.asset.demo.model.AdminDocument;
import com.asset.demo.model.Manager;
import com.asset.demo.model.User;
import com.asset.demo.repository.AdminDocumentRepository;
import com.asset.demo.repository.AdminRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@Slf4j
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final ManagerService managerService;

    private final static String UPLOAD_PATH = "C:/Users/harip/Desktop/training/Hexaware_Asset_Management_System/Sprint5/UI/public/uploads/";
    private final AdminDocumentRepository adminDocumentRepository;

    public void addAdmin(AdminReqDto adminReqDto) {

        log.atInfo().log("Adding admin: {}", adminReqDto.username());

        Admin admin = AdminMapper.mapToEntity(adminReqDto);

        User user = new User();
        user.setUsername(adminReqDto.username());
        user.setPassword(passwordEncoder.encode(adminReqDto.password()));
        user.setAccountStatus(AccountStatus.APPROVED);
        user.setRole(Role.ADMIN);

        user = userService.insertUser(user);
        admin.setUser(user);

        adminRepository.save(admin);

        log.atInfo().log("Admin created successfully: {}", adminReqDto.username());
    }

    public void approveManager(long managerId, String username) {

        log.atInfo().log("Admin {} approving manager {}", username, managerId);

        Manager manager = managerService.getManagerByGivenId(managerId);
        Admin admin = adminRepository.getAdminByUsername(username);

        manager.setAdmin(admin);

        User user = manager.getUser();
        user.setAccountStatus(AccountStatus.APPROVED);

        userService.insertUser(user);
        managerService.updateManager(manager);

        log.atInfo().log("Manager {} approved by {}", managerId, username);
    }

    public long getCount() {
        long count = adminRepository.count();
        log.atInfo().log("Admin count: {}", count);
        return count;
    }

    public List<AdminDto> getAll(String name, int page, int size) {

        log.atInfo().log("Fetching admins page={} size={}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Admin> pageAdmin = adminRepository.findAll(pageable);

        return pageAdmin
                .toList()
                .stream()
                .map(AdminMapper::mapToDto)
                .toList();
    }

    public AdminResDto getOne(String name) {

        log.atInfo().log("Fetching admin by username: {}", name);

        Admin admin = adminRepository.findByUsername(name);
        return AdminMapper.mapToDtoV2(admin);
    }

    public AdminDocumentResDto upload(String name, MultipartFile file) throws IOException {

        log.atInfo().log("Uploading profile image for admin: {}", name);

        Admin admin = adminRepository.getAdminByUsername(name);

        String fileName = file.getOriginalFilename();
        String extension = Objects.requireNonNull(fileName).split("\\.")[1];

        if (!extension.equalsIgnoreCase("PNG") &&
                !extension.equalsIgnoreCase("JPG") &&
                !extension.equalsIgnoreCase("JPEG")) {

            log.atInfo().log("Invalid file format attempted: {}", extension);
            throw new InvalidFileFormatException("File Extension not supported.");
        }

        Path path = Paths.get(UPLOAD_PATH + fileName);
        Files.write(path, file.getBytes());

        AdminDocument adminDocument = new AdminDocument();
        adminDocument.setAdmin(admin);
        adminDocument.setProfileImage(fileName);

        adminDocument = adminDocumentRepository.save(adminDocument);

        log.atInfo().log("File uploaded successfully: {}", fileName);

        return AdminDocumentMapper.mapToDto(adminDocument);
    }

    public String getProfile(String name) {

        log.atInfo().log("Fetching profile for admin: {}", name);

        return adminRepository.getProfileUrl(name);
    }

    public void rejectManager(long managerId, String username) {
        log.atInfo().log("Admin {} rejecting manager {}", username, managerId);
        Manager manager = managerService.getManagerByGivenId(managerId);
        Admin admin = adminRepository.getAdminByUsername(username);

        manager.setAdmin(admin);

        User user = manager.getUser();
        user.setAccountStatus(AccountStatus.REJECTED);

        userService.insertUser(user);
        managerService.updateManager(manager);

        log.atInfo().log("Manager {} rejected by {}", managerId, username);
    }
}

