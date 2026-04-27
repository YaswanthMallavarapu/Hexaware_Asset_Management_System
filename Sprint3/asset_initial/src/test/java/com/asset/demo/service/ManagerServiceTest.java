package com.asset.demo.service;

import com.asset.demo.dto.ManagerDto;
import com.asset.demo.dto.ManagerReqDto;
import com.asset.demo.enums.AccountStatus;
import com.asset.demo.enums.Role;
import com.asset.demo.exceptions.ResourceNotFoundException;
import com.asset.demo.model.Employee;
import com.asset.demo.model.Manager;
import com.asset.demo.model.User;
import com.asset.demo.repository.ManagerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ManagerServiceTest {

    @InjectMocks
    private ManagerService managerService;

    @Mock private ManagerRepository managerRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserService userService;
    @Mock private EmployeeService employeeService;

    private Manager createManager(AccountStatus status) {
        User user = new User();
        user.setId(1L);
        user.setUsername("mgr");
        user.setRole(Role.MANAGER);
        user.setAccountStatus(status);

        Manager m = new Manager();
        m.setId(1L);
        m.setFirstName("A");
        m.setLastName("B");
        m.setUser(user);
        return m;
    }

    private Employee createEmployee() {
        User u = new User();
        u.setId(2L);
        u.setAccountStatus(AccountStatus.PENDING);

        Employee e = new Employee();
        e.setId(10L);
        e.setUser(u);
        return e;
    }

    @Test
    void addManager_success() {
        ManagerReqDto dto = new ManagerReqDto("A","B","mgr","pass");

        when(passwordEncoder.encode("pass")).thenReturn("enc");

        User savedUser = new User();
        savedUser.setId(1L);

        when(userService.insertUser(any(User.class))).thenReturn(savedUser);

        managerService.addManager(dto);

        verify(managerRepository).save(any(Manager.class));
    }

    @Test
    void getAllManagers_success() {
        Manager m = createManager(AccountStatus.APPROVED);
        Page<Manager> page = new PageImpl<>(List.of(m));

        when(managerRepository.findAll(any(Pageable.class))).thenReturn(page);

        Assertions.assertEquals(1, managerService.getAllManagers(0,1).size());
    }

    @Test
    void getManagerById_success() {
        Manager m = createManager(AccountStatus.APPROVED);

        when(managerRepository.findById(1L)).thenReturn(Optional.of(m));

        Assertions.assertNotNull(managerService.getManagerById(1L));
    }

    @Test
    void getManagerById_notFound() {
        when(managerRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> managerService.getManagerById(1L));
    }

    @Test
    void approveEmployee_success() {
        Manager m = createManager(AccountStatus.APPROVED);
        Employee e = createEmployee();

        when(managerRepository.findByUsername("mgr")).thenReturn(m);
        when(employeeService.getEmployeeByGivenId(10L)).thenReturn(e);
        when(userService.insertUser(any(User.class))).thenAnswer(i -> i.getArgument(0));

        managerService.approveEmployee(10L,"mgr");

        Assertions.assertEquals(AccountStatus.APPROVED, e.getUser().getAccountStatus());
        verify(employeeService).updateEmployee(e);
    }

    @Test
    void approveEmployee_managerPending() {
        Manager m = createManager(AccountStatus.PENDING);

        when(managerRepository.findByUsername("mgr")).thenReturn(m);

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> managerService.approveEmployee(10L,"mgr"));
    }

    @Test
    void getManagerByGivenId_success() {
        Manager m = createManager(AccountStatus.APPROVED);

        when(managerRepository.findById(1L)).thenReturn(Optional.of(m));

        Assertions.assertEquals(m, managerService.getManagerByGivenId(1L));
    }

    @Test
    void getManagerByGivenId_notFound() {
        when(managerRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> managerService.getManagerByGivenId(1L));
    }

    @Test
    void updateManager_success() {
        Manager m = createManager(AccountStatus.APPROVED);

        managerService.updateManager(m);

        verify(managerRepository).save(m);
    }

    @Test
    void getManagerByUsername_success() {
        Manager m = createManager(AccountStatus.APPROVED);

        when(managerRepository.getManagerByUsername("mgr")).thenReturn(m);

        Assertions.assertEquals(m, managerService.getManagerByUsername("mgr"));
    }

    @Test
    void getCount_success() {
        when(managerRepository.count()).thenReturn(3L);

        Assertions.assertEquals(3L, managerService.getCount());
    }

    @Test
    void getAllManagersByStatus_success() {
        Manager m = createManager(AccountStatus.APPROVED);
        Page<Manager> page = new PageImpl<>(List.of(m));

        when(managerRepository.getAllByStatus(any(Pageable.class), eq(AccountStatus.APPROVED)))
                .thenReturn(page);

        Assertions.assertEquals(1,
                managerService.getAllManagersByStatus(0,1,"APPROVED").size());
    }

    @Test
    void getAllManagersByStatus_invalid() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> managerService.getAllManagersByStatus(0,1,"WRONG"));
    }

    @Test
    void getOne_success() {
        Manager m = createManager(AccountStatus.APPROVED);

        when(managerRepository.findByUsernameV2("mgr")).thenReturn(m);

        ManagerDto dto = managerService.getOne("mgr");

        Assertions.assertEquals("mgr", dto.username());
    }

    @Test
    void getOne_null() {
        when(managerRepository.findByUsernameV2("mgr")).thenReturn(null);

        Assertions.assertThrows(NullPointerException.class,
                () -> managerService.getOne("mgr"));
    }
}