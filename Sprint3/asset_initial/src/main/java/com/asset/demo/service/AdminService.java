package com.asset.demo.service;

import com.asset.demo.dto.AdminReqDto;
import com.asset.demo.enums.AccountStatus;
import com.asset.demo.enums.Role;
import com.asset.demo.mapper.AdminMapper;
import com.asset.demo.model.Admin;
import com.asset.demo.model.Manager;
import com.asset.demo.model.User;
import com.asset.demo.repository.AdminRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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


}
