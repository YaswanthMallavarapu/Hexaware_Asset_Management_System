package com.asset.demo.service;

import com.asset.demo.dto.EmployeeDto;
import com.asset.demo.dto.EmployeeFilterDto;
import com.asset.demo.dto.EmployeeReqDto;
import com.asset.demo.dto.EmployeeResDto;
import com.asset.demo.enums.AccountStatus;
import com.asset.demo.enums.Role;
import com.asset.demo.enums.UserStatus;
import com.asset.demo.exceptions.ResourceNotFoundException;
import com.asset.demo.mapper.EmployeeMapper;
import com.asset.demo.model.Employee;
import com.asset.demo.model.User;
import com.asset.demo.repository.EmployeeRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public void addEmployee(EmployeeReqDto employeeReqDto) {
        Employee employee= EmployeeMapper.mapToEntity(employeeReqDto);
        User user=new User();
        user.setRole(Role.EMPLOYEE);
        user.setUsername(employeeReqDto.username());
        user.setPassword(passwordEncoder.encode(employeeReqDto.password()));
        user.setAccountStatus(AccountStatus.PENDING);
        user=userService.insertUser(user);
        employee.setUser(user);
        employee.setStatus(UserStatus.ACTIVE);
        employeeRepository.save(employee);
    }

    public List<EmployeeResDto> getAllEmployees(int page,int size) {
        Pageable pageable= PageRequest.of(page,size);
        Page<Employee> pageEmployee=employeeRepository.findAll(pageable);
        return pageEmployee
                .toList()
                .stream()
                .map(EmployeeMapper::mapToDto)
                .toList();
    }

    public EmployeeResDto getEmployeeById(long employeeId) {
        Employee employee=employeeRepository.findById(employeeId)
                .orElseThrow(()->new ResourceNotFoundException("Invalid Employee Id."));
        return EmployeeMapper.mapToDto(employee);
    }

    public List<EmployeeResDto> filterEmployees(EmployeeFilterDto employeeFilterDto, int page, int size) {
        AccountStatus accountStatus=employeeFilterDto.userStatus()==null || employeeFilterDto.userStatus().isBlank()?null:AccountStatus.valueOf(employeeFilterDto.userStatus());
        UserStatus userStatus =employeeFilterDto.employeeStatus()==null || employeeFilterDto.employeeStatus().isBlank()?null:UserStatus.valueOf(employeeFilterDto.employeeStatus());
        if(accountStatus==null && userStatus==null)
            return List.of();
        Pageable pageable=PageRequest.of(page,size);
        Page<Employee>list=employeeRepository.getEmployeeByStatus(accountStatus,userStatus,pageable);
        return list
                .toList()
                .stream()
                .map(EmployeeMapper::mapToDto)
                .toList();

    }

    public List<EmployeeResDto> getAllPendingEmployees(int page, int size) {
        Pageable pageable= PageRequest.of(page,size);
        Page<Employee> pageEmployee=employeeRepository.getAllPendingEmployees(pageable);
        return pageEmployee
                .toList()
                .stream()
                .map(EmployeeMapper::mapToDto)
                .toList();
    }

    public Employee getEmployeeByGivenId(long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(()->new ResourceNotFoundException("Invalid Employee Id."));
    }

    public void updateEmployee(Employee employee) {
        employeeRepository.save(employee);
    }

    public Employee getEmployeeByUsername(String username) {
        return employeeRepository.getEmployeeByUsername(username);
    }

    public long getCount() {
        return employeeRepository.count();
    }

    public EmployeeDto getOne(String name) {
        Employee employee=employeeRepository.findByUsername(name);
        return EmployeeMapper.mapToDtoV2(employee);
    }
}
