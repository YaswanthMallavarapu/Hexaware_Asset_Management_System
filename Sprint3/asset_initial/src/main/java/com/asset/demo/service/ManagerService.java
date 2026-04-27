package com.asset.demo.service;

import com.asset.demo.dto.ManagerDto;
import com.asset.demo.dto.ManagerReqDto;
import com.asset.demo.dto.ManagerResDto;
import com.asset.demo.enums.AccountStatus;
import com.asset.demo.enums.Role;
import com.asset.demo.exceptions.ResourceNotFoundException;
import com.asset.demo.mapper.ManagerMapper;
import com.asset.demo.model.Employee;
import com.asset.demo.model.Manager;
import com.asset.demo.model.User;
import com.asset.demo.repository.ManagerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final EmployeeService employeeService;

    public void addManager(ManagerReqDto managerReqDto) {
        log.atInfo().log("Adding manager username={}", managerReqDto.username());

        Manager manager = ManagerMapper.mapToEntity(managerReqDto);

        User user = new User();
        user.setUsername(managerReqDto.username());
        user.setPassword(passwordEncoder.encode(managerReqDto.password()));
        user.setRole(Role.MANAGER);
        user.setAccountStatus(AccountStatus.PENDING);

        user = userService.insertUser(user);

        manager.setUser(user);
        managerRepository.save(manager);

        log.atInfo().log("Manager created successfully username={}", managerReqDto.username());
    }

    public List<ManagerResDto> getAllManagers(int page, int size) {
        log.atInfo().log("Fetching managers page={} size={}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Manager> pageManager = managerRepository.findAll(pageable);

        return pageManager
                .toList()
                .stream()
                .map(ManagerMapper::mapToDto)
                .toList();
    }

    public ManagerResDto getManagerById(long managerId) {
        log.atInfo().log("Fetching manager by id={}", managerId);

        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> {
                    log.atWarn().log("Manager not found id={}", managerId);
                    return new ResourceNotFoundException("Invalid Manager id.");
                });

        return ManagerMapper.mapToDto(manager);
    }

    public void approveEmployee(long employeeId, String username) {
        log.atInfo().log("Manager {} approving employee id={}", username, employeeId);

        Manager manager = managerRepository.findByUsername(username);

        if (manager.getUser().getAccountStatus() == AccountStatus.PENDING) {
            log.atWarn().log("Inactive manager {} tried to approve employee", username);
            throw new ResourceNotFoundException("You Cannot perform this action until your account get verified");
        }

        Employee employee = employeeService.getEmployeeByGivenId(employeeId);
        employee.setManager(manager);

        User user = employee.getUser();
        user.setAccountStatus(AccountStatus.APPROVED);
        user = userService.insertUser(user);

        employeeService.updateEmployee(employee);

        log.atInfo().log("Employee id={} approved by manager={}", employeeId, username);
    }

    public Manager getManagerByGivenId(long managerId) {
        log.atInfo().log("Fetching manager entity id={}", managerId);

        return managerRepository.findById(managerId)
                .orElseThrow(() -> {
                    log.atWarn().log("Manager entity not found id={}", managerId);
                    return new ResourceNotFoundException("Invalid manager Id.");
                });
    }

    public void updateManager(Manager manager) {
        log.atInfo().log("Updating manager id={}", manager.getId());
        managerRepository.save(manager);
    }

    public Manager getManagerByUsername(String username) {
        log.atInfo().log("Fetching manager by username={}", username);
        return managerRepository.getManagerByUsername(username);
    }

    public long getCount() {
        log.atInfo().log("Fetching manager count");
        return managerRepository.count();
    }

    public List<ManagerResDto> getAllManagersByStatus(int page, int size, String status) {
        log.atInfo().log("Fetching managers by status={} page={} size={}", status, page, size);

        Pageable pageable = PageRequest.of(page, size);
        AccountStatus managerStatus = AccountStatus.valueOf(status);

        Page<Manager> pageManager = managerRepository.getAllByStatus(pageable, managerStatus);

        return pageManager
                .toList()
                .stream()
                .map(ManagerMapper::mapToDto)
                .toList();
    }

    public ManagerDto getOne(String name) {
        log.atInfo().log("Fetching manager profile name={}", name);

        Manager manager = managerRepository.findByUsernameV2(name);
        return ManagerMapper.mapToDtoV2(manager);
    }
}