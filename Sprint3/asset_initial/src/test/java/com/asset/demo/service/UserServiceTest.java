//package com.asset.demo.service;
//
//import com.asset.demo.dto.UserResDto;
//import com.asset.demo.enums.AccountStatus;
//import com.asset.demo.enums.Role;
//import com.asset.demo.enums.UserStatus;
//import com.asset.demo.exceptions.ResourceNotFoundException;
//import com.asset.demo.model.Asset;
//import com.asset.demo.model.User;
//import com.asset.demo.repository.UserRepository;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class UserServiceTest {
//    @InjectMocks
//    private UserService userService;
//    @Mock
//    private UserRepository userRepository;
//
//    @Test
//    public void loadUserByUsernameTest(){
//        User user=new User();
//        user.setId(1L);
//        user.setUsername("Yaswanth");
//        user.setPassword("^@)(@&^@&#GUYSUYWVVSTYFSJ");
//        user.setAccountStatus(AccountStatus.APPROVED);
//        user.setRole(Role.ADMIN);
//
//        when(userRepository.getUserByUsername("Yaswanth")).thenReturn(user);
//
//        Assertions.assertEquals(user,userService.loadUserByUsername("Yaswanth"));
//    }
//
//    @Test
//    public void insertUserTest(){
//        User user=new User();
//        user.setId(1L);
//        user.setUsername("Yaswanth");
//        user.setPassword("^@)(@&^@&#GUYSUYWVVSTYFSJ");
//        user.setAccountStatus(AccountStatus.APPROVED);
//        user.setRole(Role.ADMIN);
//
//        when(userRepository.save(user)).thenReturn(user);
//
//        Assertions.assertEquals(user,userService.insertUser(user));
//        verify(userRepository,times(1)).save(user);
//
//    }
//
//
//}
