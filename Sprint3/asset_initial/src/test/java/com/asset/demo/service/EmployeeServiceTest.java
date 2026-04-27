package com.asset.demo.service;

import com.asset.demo.dto.*;
import com.asset.demo.enums.AccountStatus;
import com.asset.demo.enums.Role;
import com.asset.demo.enums.UserStatus;
import com.asset.demo.exceptions.InvalidFileFormatException;
import com.asset.demo.exceptions.ResourceNotFoundException;
import com.asset.demo.model.Employee;
import com.asset.demo.model.EmployeeDocument;
import com.asset.demo.model.User;
import com.asset.demo.repository.EmployeeDocumentRepository;
import com.asset.demo.repository.EmployeeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @InjectMocks
    private EmployeeService employeeService;

    @Mock private EmployeeRepository employeeRepository;
    @Mock private UserService userService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private EmployeeDocumentRepository employeeDocumentRepository;

    private Employee createEmployee() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setRole(Role.EMPLOYEE);
        user.setAccountStatus(AccountStatus.PENDING);

        Employee e = new Employee();
        e.setId(1L);
        e.setFirstName("John");
        e.setLastName("Doe");
        e.setStatus(UserStatus.ACTIVE);
        e.setUser(user);

        return e;
    }

    @Test
    void addEmployee_success() {
        EmployeeReqDto dto = new EmployeeReqDto(
                "John","Doe","Male","9999999999",
                "Dev","john","password"
        );

        when(passwordEncoder.encode("password")).thenReturn("encoded");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("john");
        savedUser.setRole(Role.EMPLOYEE);
        savedUser.setAccountStatus(AccountStatus.PENDING);

        when(userService.insertUser(any(User.class))).thenReturn(savedUser);

        employeeService.addEmployee(dto);

        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void getAllEmployees_success() {
        Employee e = createEmployee();
        Page<Employee> page = new PageImpl<>(List.of(e));

        when(employeeRepository.findAll(any(Pageable.class))).thenReturn(page);

        Assertions.assertEquals(1,
                employeeService.getAllEmployees(0,1).size());
    }

    @Test
    void getEmployeeById_success() {
        Employee e = createEmployee();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(e));

        Assertions.assertNotNull(employeeService.getEmployeeById(1L));
    }

    @Test
    void getEmployeeById_notFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> employeeService.getEmployeeById(1L));
    }

    @Test
    void filterEmployees_bothNull() {
        EmployeeFilterDto dto = new EmployeeFilterDto(null,null);

        Assertions.assertEquals(0,
                employeeService.filterEmployees(dto,0,5).size());
    }

    @Test
    void filterEmployees_accountOnly() {
        Employee e = createEmployee();
        Page<Employee> page = new PageImpl<>(List.of(e));

        when(employeeRepository.getEmployeeByStatus(
                eq(AccountStatus.PENDING), eq(null), any(Pageable.class)))
                .thenReturn(page);

        EmployeeFilterDto dto = new EmployeeFilterDto("PENDING",null);

        Assertions.assertEquals(1,
                employeeService.filterEmployees(dto,0,5).size());
    }

    @Test
    void filterEmployees_userOnly() {
        Employee e = createEmployee();
        Page<Employee> page = new PageImpl<>(List.of(e));

        when(employeeRepository.getEmployeeByStatus(
                eq(null), eq(UserStatus.ACTIVE), any(Pageable.class)))
                .thenReturn(page);

        EmployeeFilterDto dto = new EmployeeFilterDto(null,"ACTIVE");

        Assertions.assertEquals(1,
                employeeService.filterEmployees(dto,0,5).size());
    }

    @Test
    void filterEmployees_bothPresent() {
        Employee e = createEmployee();
        Page<Employee> page = new PageImpl<>(List.of(e));

        when(employeeRepository.getEmployeeByStatus(
                eq(AccountStatus.PENDING),
                eq(UserStatus.ACTIVE),
                any(Pageable.class)))
                .thenReturn(page);

        EmployeeFilterDto dto = new EmployeeFilterDto("PENDING","ACTIVE");

        Assertions.assertEquals(1,
                employeeService.filterEmployees(dto,0,5).size());
    }

    @Test
    void filterEmployees_invalidEnum() {
        EmployeeFilterDto dto = new EmployeeFilterDto("WRONG","ACTIVE");

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> employeeService.filterEmployees(dto,0,5));
    }

    @Test
    void getAllPendingEmployees_success() {
        Employee e = createEmployee();
        Page<Employee> page = new PageImpl<>(List.of(e));

        when(employeeRepository.getAllPendingEmployees(any(Pageable.class)))
                .thenReturn(page);

        Assertions.assertEquals(1,
                employeeService.getAllPendingEmployees(0,5).size());
    }

    @Test
    void getEmployeeByGivenId_success() {
        Employee e = createEmployee();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(e));

        Assertions.assertEquals(e,
                employeeService.getEmployeeByGivenId(1L));
    }

    @Test
    void getEmployeeByGivenId_notFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> employeeService.getEmployeeByGivenId(1L));
    }

    @Test
    void updateEmployee_success() {
        Employee e = createEmployee();

        employeeService.updateEmployee(e);

        verify(employeeRepository).save(e);
    }

    @Test
    void getEmployeeByUsername_success() {
        Employee e = createEmployee();

        when(employeeRepository.getEmployeeByUsername("john")).thenReturn(e);

        Assertions.assertEquals(e,
                employeeService.getEmployeeByUsername("john"));
    }

    @Test
    void getEmployeeByUsername_null() {
        when(employeeRepository.getEmployeeByUsername("john")).thenReturn(null);

        Assertions.assertNull(employeeService.getEmployeeByUsername("john"));
    }

    @Test
    void getCount_success() {
        when(employeeRepository.count()).thenReturn(5L);

        Assertions.assertEquals(5L, employeeService.getCount());
    }

    @Test
    void getOne_success() {
        Employee e = createEmployee();

        when(employeeRepository.findByUsername("john")).thenReturn(e);

        Assertions.assertNotNull(employeeService.getOne("john"));
    }

    @Test
    void getOne_null() {
        when(employeeRepository.findByUsername("john")).thenReturn(null);

        Assertions.assertThrows(NullPointerException.class,
                () -> employeeService.getOne("john"));
    }

    @Test
    void upload_success() throws IOException {
        Employee e = createEmployee();

        MultipartFile file = mock(MultipartFile.class);

        when(file.getOriginalFilename()).thenReturn("img.png");
        when(file.getBytes()).thenReturn(new byte[]{1});

        when(employeeRepository.getEmployeeByUsername("john")).thenReturn(e);
        when(employeeDocumentRepository.save(any(EmployeeDocument.class)))
                .thenAnswer(i -> i.getArgument(0));

        Assertions.assertNotNull(employeeService.upload("john", file));
    }

    @Test
    void upload_invalidExtension() {
        MultipartFile file = mock(MultipartFile.class);

        when(file.getOriginalFilename()).thenReturn("file.txt");

        Assertions.assertThrows(InvalidFileFormatException.class,
                () -> employeeService.upload("john", file));
    }

    @Test
    void upload_noExtension() {
        MultipartFile file = mock(MultipartFile.class);

        when(file.getOriginalFilename()).thenReturn("file");

        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class,
                () -> employeeService.upload("john", file));
    }

    @Test
    void upload_employeeNull() throws IOException {
        MultipartFile file = mock(MultipartFile.class);

        when(file.getOriginalFilename()).thenReturn("img.png");
        when(file.getBytes()).thenReturn(new byte[]{1});
        when(employeeRepository.getEmployeeByUsername("john")).thenReturn(null);

        Assertions.assertThrows(NullPointerException.class,
                () -> employeeService.upload("john", file));
    }

    @Test
    void getProfile_success() {
        when(employeeRepository.getProfileUrl("john")).thenReturn("url");

        Assertions.assertEquals("url",
                employeeService.getProfile("john"));
    }

    @Test
    void getProfile_null() {
        when(employeeRepository.getProfileUrl("john")).thenReturn(null);

        Assertions.assertNull(employeeService.getProfile("john"));
    }
}