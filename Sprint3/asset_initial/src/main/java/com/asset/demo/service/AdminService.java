package com.asset.demo.service;

import com.asset.demo.dto.AdminDto;
import com.asset.demo.dto.AdminReqDto;
import com.asset.demo.dto.AdminResDto;
import com.asset.demo.dto.AssetAllocationResDto;
import com.asset.demo.enums.AccountStatus;
import com.asset.demo.enums.Role;
import com.asset.demo.mapper.AdminMapper;
import com.asset.demo.model.Admin;
import com.asset.demo.model.Manager;
import com.asset.demo.model.User;
import com.asset.demo.repository.AdminRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final ManagerService managerService;

    public void addAdmin(AdminReqDto adminReqDto) {
        Admin admin= AdminMapper.mapToEntity(adminReqDto);
        User user=new User();
        user.setUsername(adminReqDto.username());
        user.setPassword(passwordEncoder.encode(adminReqDto.password()));
        user.setAccountStatus(AccountStatus.APPROVED);
        user.setRole(Role.ADMIN);
        user=userService.insertUser(user);
        admin.setUser(user);
        adminRepository.save(admin);

    }

    public void approveManager(long managerId, String username) {
        Manager manager=managerService.getManagerByGivenId(managerId);
        Admin admin=adminRepository.getAdminByUsername(username);
        manager.setAdmin(admin);
        User user=manager.getUser();
        user.setAccountStatus(AccountStatus.APPROVED);
        userService.insertUser(user);
        managerService.updateManager(manager);


    }


    public long getCount() {
        return adminRepository.count();
    }

    public List<AdminDto> getAll(String name, int page, int size) {
        Pageable pageable= PageRequest.of(page,size);
        Page<Admin>pageAdmin=adminRepository.findAll(pageable);
        return pageAdmin
                .toList()
                .stream()
                .map(AdminMapper::mapToDto)
                .toList();
    }

    public AdminResDto getOne(String name) {
        Admin admin=adminRepository.findByUsername(name);
        return AdminMapper.mapToDtoV2(admin);
    }
}
