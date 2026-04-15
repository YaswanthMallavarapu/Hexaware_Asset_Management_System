//package com.asset.demo.service;
//
//import com.asset.demo.dto.EmployeeFilterDto;
//import com.asset.demo.dto.EmployeeReqDto;
//import com.asset.demo.dto.EmployeeResDto;
//import com.asset.demo.enums.AccountStatus;
//import com.asset.demo.enums.Role;
//import com.asset.demo.enums.UserStatus;
//import com.asset.demo.exceptions.ResourceNotFoundException;
//import com.asset.demo.model.Employee;
//import com.asset.demo.model.User;
//import com.asset.demo.repository.EmployeeRepository;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class EmployeeServiceTest {
//
//    @InjectMocks
//    private EmployeeService employeeService;
//
//    @Mock
//    private EmployeeRepository employeeRepository;
//
//    @Mock
//    private UserService userService;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @Test
//    public void addEmployeeTest(){
//
//        EmployeeReqDto dto = new EmployeeReqDto(
//                "John",
//                "Doe",
//                "Male",
//                "9876543210",
//                "Software Engineer",
//                "john123",
//                "password123"
//        );
//
//        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
//
//        User savedUser = new User();
//        savedUser.setId(1L);
//        savedUser.setUsername("john123");
//        savedUser.setRole(Role.EMPLOYEE);
//        savedUser.setAccountStatus(AccountStatus.PENDING);
//
//        when(userService.insertUser(any(User.class))).thenReturn(savedUser);
//
//        employeeService.addEmployee(dto);
//
//        ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
//
//        verify(employeeRepository).save(captor.capture());
//
//        Employee savedEmployee = captor.getValue();
//
//        Assertions.assertEquals(savedUser, savedEmployee.getUser());
//        Assertions.assertEquals(UserStatus.ACTIVE, savedEmployee.getStatus());
//    }
//
//    @Test
//    public void getAllEmployeesTest(){
//
//        User user = new User();
//        user.setId(1L);
//        user.setUsername("john123");
//        user.setRole(Role.EMPLOYEE);
//        user.setAccountStatus(AccountStatus.PENDING);
//
//        Employee employee = new Employee();
//        employee.setId(1L);
//        employee.setStatus(UserStatus.ACTIVE);
//        employee.setUser(user);
//
//        User user1 = new User();
//        user1.setId(2L);
//        user1.setUsername("john124");
//        user1.setRole(Role.EMPLOYEE);
//        user1.setAccountStatus(AccountStatus.PENDING);
//
//        Employee employee1 = new Employee();
//        employee1.setId(2L);
//        employee1.setStatus(UserStatus.ACTIVE);
//        employee1.setUser(user1);
//
//        List<Employee> list = List.of(employee,employee1);
//
//        Page<Employee> pageEmployee = new PageImpl<>(list);
//
//        int page = 0;
//        int size = 2;
//
//        Pageable pageable = PageRequest.of(page,size);
//
//        when(employeeRepository.findAll(pageable)).thenReturn(pageEmployee);
//
//        Assertions.assertEquals(2,employeeService.getAllEmployees(0,2).size());
//    }
//
//    @Test
//    public void getEmployeeByIdTestWhenExist(){
//
//        User user = new User();
//        user.setId(1L);
//        user.setUsername("john123");
//        user.setRole(Role.EMPLOYEE);
//        user.setAccountStatus(AccountStatus.PENDING);
//
//        Employee employee = new Employee();
//        employee.setId(1L);
//        employee.setStatus(UserStatus.ACTIVE);
//        employee.setUser(user);
//
//        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
//
//        Assertions.assertNotNull(employeeService.getEmployeeById(1L));
//    }
//
//    @Test
//    public void getEmployeeByIdTestWhenNotExist(){
//
//        when(employeeRepository.findById(100L)).thenReturn(Optional.empty());
//
//        Exception e = Assertions.assertThrows(ResourceNotFoundException.class,
//                ()->employeeService.getEmployeeById(100L));
//
//        Assertions.assertEquals("Invalid Employee Id.",e.getMessage());
//    }
//
//    @Test
//    public void filterEmployeesTest(){
//
//        User user = new User();
//        user.setId(1L);
//        user.setUsername("john123");
//        user.setRole(Role.EMPLOYEE);
//        user.setAccountStatus(AccountStatus.PENDING);
//
//        Employee employee = new Employee();
//        employee.setId(1L);
//        employee.setStatus(UserStatus.ACTIVE);
//        employee.setUser(user);
//        List<Employee> list = List.of(employee);
//
//        Page<Employee> pageEmployee = new PageImpl<>(list);
//
//        int page = 0;
//        int size = 1;
//
//        Pageable pageable = PageRequest.of(page,size);
//
//        EmployeeFilterDto filterDto = new EmployeeFilterDto("ACTIVE");
//
//        when(employeeRepository.getEmployeeByStatus(UserStatus.ACTIVE,pageable))
//                .thenReturn(pageEmployee);
//
//        Assertions.assertEquals(1,
//                employeeService.filterEmployees(filterDto,0,1).size());
//    }
//
//    @Test
//    public void filterEmployeesWhenStatusNull(){
//
//        EmployeeFilterDto filterDto = new EmployeeFilterDto(null);
//
//        Assertions.assertEquals(0,
//                employeeService.filterEmployees(filterDto,0,1).size());
//    }
//
//    @Test
//    public void getAllPendingEmployeesTest(){
//
//        User user = new User();
//        user.setId(1L);
//        user.setUsername("john123");
//        user.setRole(Role.EMPLOYEE);
//        user.setAccountStatus(AccountStatus.PENDING);
//
//        Employee employee = new Employee();
//        employee.setId(1L);
//        employee.setStatus(UserStatus.ACTIVE);
//        employee.setUser(user);
//
//        List<Employee> list = List.of(employee);
//
//        Page<Employee> pageEmployee = new PageImpl<>(list);
//
//        int page = 0;
//        int size = 1;
//
//        Pageable pageable = PageRequest.of(page,size);
//
//        when(employeeRepository.getAllPendingEmployees(pageable))
//                .thenReturn(pageEmployee);
//
//        Assertions.assertEquals(1,
//                employeeService.getAllPendingEmployees(0,1).size());
//    }
//
//    @Test
//    public void getEmployeeByGivenIdTest(){
//
//        Employee employee = new Employee();
//        employee.setId(1L);
//
//        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
//
//        Assertions.assertEquals(employee,
//                employeeService.getEmployeeByGivenId(1L));
//    }
//
//    @Test
//    public void updateEmployeeTest(){
//
//        Employee employee = new Employee();
//        employee.setId(1L);
//
//        employeeService.updateEmployee(employee);
//
//        verify(employeeRepository,times(1)).save(employee);
//    }
//
//    @Test
//    public void getEmployeeByUsernameTest(){
//
//        Employee employee = new Employee();
//        employee.setId(1L);
//
//        when(employeeRepository.getEmployeeByUsername("john123"))
//                .thenReturn(employee);
//
//        Assertions.assertEquals(employee,
//                employeeService.getEmployeeByUsername("john123"));
//    }
//
//}