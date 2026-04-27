package com.asset.demo.service;

import com.asset.demo.dto.*;
import com.asset.demo.enums.AccountStatus;
import com.asset.demo.enums.Role;
import com.asset.demo.enums.UserStatus;
import com.asset.demo.exceptions.InvalidFileFormatException;
import com.asset.demo.exceptions.ResourceNotFoundException;
import com.asset.demo.mapper.EmployeeDocumentMapper;
import com.asset.demo.mapper.EmployeeMapper;
import com.asset.demo.model.Employee;
import com.asset.demo.model.EmployeeDocument;
import com.asset.demo.model.User;
import com.asset.demo.repository.EmployeeDocumentRepository;
import com.asset.demo.repository.EmployeeRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    private final static String UPLOAD_PATH =
            "C:/Users/harip/Desktop/training/Hexaware_Asset_Management_System/Sprint5/UI/public/uploads/";

    private final EmployeeDocumentRepository employeeDocumentRepository;

    public void addEmployee(EmployeeReqDto employeeReqDto) {
        log.atInfo().log("Adding new employee username={}", employeeReqDto.username());

        Employee employee = EmployeeMapper.mapToEntity(employeeReqDto);

        User user = new User();
        user.setRole(Role.EMPLOYEE);
        user.setUsername(employeeReqDto.username());
        user.setPassword(passwordEncoder.encode(employeeReqDto.password()));
        user.setAccountStatus(AccountStatus.PENDING);

        user = userService.insertUser(user);

        employee.setUser(user);
        employee.setStatus(UserStatus.ACTIVE);

        employeeRepository.save(employee);

        log.atInfo().log("Employee created successfully username={}", employeeReqDto.username());
    }

    public List<EmployeeResDto> getAllEmployees(int page, int size) {
        log.atInfo().log("Fetching employees page={} size={}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> pageEmployee = employeeRepository.findAll(pageable);

        return pageEmployee
                .toList()
                .stream()
                .map(EmployeeMapper::mapToDto)
                .toList();
    }

    public EmployeeResDto getEmployeeById(long employeeId) {
        log.atInfo().log("Fetching employee by id={}", employeeId);

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> {
                    log.atWarn().log("Employee not found id={}", employeeId);
                    return new ResourceNotFoundException("Invalid Employee Id.");
                });

        return EmployeeMapper.mapToDto(employee);
    }

    public List<EmployeeResDto> filterEmployees(EmployeeFilterDto employeeFilterDto, int page, int size) {
        log.atInfo().log("Filtering employees userStatus={} employeeStatus={}",
                employeeFilterDto.userStatus(),
                employeeFilterDto.employeeStatus());

        AccountStatus accountStatus =
                employeeFilterDto.userStatus() == null || employeeFilterDto.userStatus().isBlank()
                        ? null
                        : AccountStatus.valueOf(employeeFilterDto.userStatus());

        UserStatus userStatus =
                employeeFilterDto.employeeStatus() == null || employeeFilterDto.employeeStatus().isBlank()
                        ? null
                        : UserStatus.valueOf(employeeFilterDto.employeeStatus());

        if (accountStatus == null && userStatus == null) {
            log.atWarn().log("No filter provided for employee search");
            return List.of();
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> list = employeeRepository.getEmployeeByStatus(accountStatus, userStatus, pageable);

        return list
                .toList()
                .stream()
                .map(EmployeeMapper::mapToDto)
                .toList();
    }

    public List<EmployeeResDto> getAllPendingEmployees(int page, int size) {
        log.atInfo().log("Fetching pending employees page={} size={}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> pageEmployee = employeeRepository.getAllPendingEmployees(pageable);

        return pageEmployee
                .toList()
                .stream()
                .map(EmployeeMapper::mapToDto)
                .toList();
    }

    public Employee getEmployeeByGivenId(long employeeId) {
        log.atInfo().log("Fetching employee entity id={}", employeeId);

        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> {
                    log.atWarn().log("Employee entity not found id={}", employeeId);
                    return new ResourceNotFoundException("Invalid Employee Id.");
                });
    }

    public void updateEmployee(Employee employee) {
        log.atInfo().log("Updating employee id={}", employee.getId());
        employeeRepository.save(employee);
    }

    public Employee getEmployeeByUsername(String username) {
        log.atInfo().log("Fetching employee by username={}", username);
        return employeeRepository.getEmployeeByUsername(username);
    }

    public long getCount() {
        log.atInfo().log("Fetching employee count");
        return employeeRepository.count();
    }

    public EmployeeDto getOne(String name) {
        log.atInfo().log("Fetching employee profile name={}", name);

        Employee employee = employeeRepository.findByUsername(name);
        return EmployeeMapper.mapToDtoV2(employee);
    }

    public EmployeeDocumentResDto upload(String username, MultipartFile file) throws IOException {
        log.atInfo().log("Uploading document for employee username={}", username);

        Employee employee = employeeRepository.getEmployeeByUsername(username);

        String fileName = file.getOriginalFilename();
        String extension = fileName.split("\\.")[1];

        if (!extension.equalsIgnoreCase("PNG") &&
                !extension.equalsIgnoreCase("JPG") &&
                !extension.equalsIgnoreCase("JPEG")) {

            log.atError().log("Invalid file format uploaded by username={}", username);
            throw new InvalidFileFormatException("File Extension not supported.");
        }

        Path path = Paths.get(UPLOAD_PATH + fileName);
        Files.write(path, file.getBytes());

        EmployeeDocument doc = new EmployeeDocument();
        doc.setEmployee(employee);
        doc.setProfileImage(fileName);

        doc = employeeDocumentRepository.save(doc);

        log.atInfo().log("Document uploaded successfully username={}", username);

        return EmployeeDocumentMapper.mapToDto(doc);
    }

    public String getProfile(String username) {
        log.atInfo().log("Fetching profile image for username={}", username);
        return employeeRepository.getProfileUrl(username);
    }
}