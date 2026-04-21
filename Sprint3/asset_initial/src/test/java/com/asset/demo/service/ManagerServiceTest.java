//package com.asset.demo.service;
//
//import com.asset.demo.dto.ManagerReqDto;
//import com.asset.demo.dto.ManagerResDto;
//import com.asset.demo.enums.AccountStatus;
//import com.asset.demo.enums.Role;
//import com.asset.demo.exceptions.ResourceNotFoundException;
//import com.asset.demo.model.Employee;
//import com.asset.demo.model.Manager;
//import com.asset.demo.model.User;
//import com.asset.demo.repository.ManagerRepository;
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
//public class ManagerServiceTest {
//
//    @InjectMocks
//    private ManagerService managerService;
//
//    @Mock
//    private ManagerRepository managerRepository;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @Mock
//    private UserService userService;
//
//    @Mock
//    private EmployeeService employeeService;
//
//    @Test
//    public void addManagerTest(){
//
//        ManagerReqDto dto = new ManagerReqDto(
//                "John",
//                "Doe",
//                "manager123",
//                "password123"
//        );
//
//        when(passwordEncoder.encode(anyString()))
//                .thenReturn("encodedPassword");
//
//        User savedUser = new User();
//        savedUser.setId(1L);
//        savedUser.setUsername("manager123");
//        savedUser.setRole(Role.MANAGER);
//        savedUser.setAccountStatus(AccountStatus.PENDING);
//
//        when(userService.insertUser(any(User.class)))
//                .thenReturn(savedUser);
//
//        managerService.addManager(dto);
//
//        ArgumentCaptor<Manager> captor = ArgumentCaptor.forClass(Manager.class);
//
//        verify(managerRepository).save(captor.capture());
//
//        Manager savedManager = captor.getValue();
//
//        Assertions.assertEquals(savedUser, savedManager.getUser());
//        Assertions.assertEquals("John", savedManager.getFirstName());
//        Assertions.assertEquals("Doe", savedManager.getLastName());
//    }
//
//    @Test
//    public void getAllManagersTest(){
//
//        User user = new User();
//        user.setId(1L);
//
//        Manager manager = new Manager();
//        manager.setId(1L);
//        manager.setFirstName("John");
//        manager.setLastName("Doe");
//        manager.setUser(user);
//
//        List<Manager> list = List.of(manager);
//
//        Page<Manager> pageManager = new PageImpl<>(list);
//
//        Pageable pageable = PageRequest.of(0,1);
//
//        when(managerRepository.findAll(pageable))
//                .thenReturn(pageManager);
//
//        List<ManagerResDto> result = managerService.getAllManagers(0,1);
//
//        Assertions.assertEquals(1,result.size());
//        Assertions.assertEquals("John Doe",result.get(0).fullName());
//        Assertions.assertEquals(1L,result.get(0).userId());
//    }
//
//    @Test
//    public void getManagerByIdTestWhenExist(){
//
//        User user = new User();
//        user.setId(2L);
//
//        Manager manager = new Manager();
//        manager.setId(1L);
//        manager.setFirstName("John");
//        manager.setLastName("Doe");
//        manager.setUser(user);
//
//        when(managerRepository.findById(1L))
//                .thenReturn(Optional.of(manager));
//
//        ManagerResDto result = managerService.getManagerById(1L);
//
//        Assertions.assertEquals("John Doe",result.fullName());
//        Assertions.assertEquals(2L,result.userId());
//    }
//
//    @Test
//    public void getManagerByIdTestWhenNotExist(){
//
//        when(managerRepository.findById(100L))
//                .thenReturn(Optional.empty());
//
//        Exception e = Assertions.assertThrows(ResourceNotFoundException.class,
//                ()->managerService.getManagerById(100L));
//
//        Assertions.assertEquals("Invalid Manager id.",e.getMessage());
//    }
//
//    @Test
//    public void approveEmployeeTest(){
//
//        User managerUser = new User();
//        managerUser.setAccountStatus(AccountStatus.APPROVED);
//
//        Manager manager = new Manager();
//        manager.setUser(managerUser);
//
//        User employeeUser = new User();
//        employeeUser.setAccountStatus(AccountStatus.PENDING);
//
//        Employee employee = new Employee();
//        employee.setUser(employeeUser);
//
//        when(managerRepository.findByUsername("manager123"))
//                .thenReturn(manager);
//
//        when(employeeService.getEmployeeByGivenId(1L))
//                .thenReturn(employee);
//
//        when(userService.insertUser(any(User.class)))
//                .thenReturn(employeeUser);
//
//        managerService.approveEmployee(1L,"manager123");
//
//        Assertions.assertEquals(AccountStatus.APPROVED,
//                employee.getUser().getAccountStatus());
//
//        verify(employeeService,times(1)).updateEmployee(employee);
//    }
//
//    @Test
//    public void approveEmployeeWhenManagerNotApproved(){
//
//        User managerUser = new User();
//        managerUser.setAccountStatus(AccountStatus.PENDING);
//
//        Manager manager = new Manager();
//        manager.setUser(managerUser);
//
//        when(managerRepository.findByUsername("manager123"))
//                .thenReturn(manager);
//
//        Exception e = Assertions.assertThrows(ResourceNotFoundException.class,
//                ()->managerService.approveEmployee(1L,"manager123"));
//
//                        Assertions.assertEquals(
//                                "You Cannot perform this action until your account get verified",
//                                e.getMessage()
//                        );
//    }
//
//    @Test
//    public void getManagerByGivenIdTest(){
//
//        Manager manager = new Manager();
//        manager.setId(1L);
//
//        when(managerRepository.findById(1L))
//                .thenReturn(Optional.of(manager));
//
//        Assertions.assertEquals(manager,
//                managerService.getManagerByGivenId(1L));
//    }
//
//    @Test
//    public void getManagerByGivenIdWhenNotExist(){
//
//        when(managerRepository.findById(50L))
//                .thenReturn(Optional.empty());
//
//        Exception e = Assertions.assertThrows(ResourceNotFoundException.class,
//                ()->managerService.getManagerByGivenId(50L));
//
//        Assertions.assertEquals("Invalid manager Id.",e.getMessage());
//    }
//
//    @Test
//    public void updateManagerTest(){
//
//        Manager manager = new Manager();
//        manager.setId(1L);
//
//        managerService.updateManager(manager);
//
//        verify(managerRepository,times(1)).save(manager);
//    }
//
//    @Test
//    public void getManagerByUsernameTest(){
//
//        Manager manager = new Manager();
//        manager.setId(1L);
//
//        when(managerRepository.getManagerByUsername("manager123"))
//                .thenReturn(manager);
//
//        Assertions.assertEquals(manager,
//                managerService.getManagerByUsername("manager123"));
//    }
//
//}