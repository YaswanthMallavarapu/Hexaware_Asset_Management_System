package com.asset.demo.service;

import com.asset.demo.dto.UserResDto;
import com.asset.demo.enums.Role;
import com.asset.demo.enums.UserStatus;
import com.asset.demo.exceptions.ResourceNotFoundException;
import com.asset.demo.model.Asset;
import com.asset.demo.model.User;
import com.asset.demo.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @Test
    public void getUserByIdTestWhenExist(){
        User user=new User();
        user.setId(1L);
        user.setFirstName("yaswanth");
        user.setLastName("mallavarapu");
        user.setContactNumber("6305820373");
        user.setGender("MALE");
        user.setDesignation("Junior Dev");
        user.setRole(Role.EMPLOYEE);
        user.setEmail("yash@gmail.com");
        user.setPassword("abc@123");
        user.setStatus(UserStatus.ACTIVE);

        UserResDto dto=new UserResDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getDesignation(),
                user.getStatus()
        );

       UserResDto dto1= new UserResDto(
                1L,
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getDesignation(),
                UserStatus.ON_LEAVE
        );
       when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Assertions.assertEquals(dto,userService.getUserById(1L));
        Assertions.assertNotEquals(dto1,userService.getUserById(1L));
    }

    @Test
    public void getUserByIdTestWhenNotExist(){

        //mocking when id not found
        Mockito.when(userRepository.findById(11L)).thenReturn(Optional.empty());


        //catching exception when id not found
        Exception e=Assertions.assertThrows(ResourceNotFoundException.class,()->userService.getUserById(11L));

        //check for error message
        Assertions.assertEquals("user with given id not found",e.getMessage());


    }

    @Test
    public void getUserByGivenIdTestWhenExist(){
        User user=new User();
        user.setId(1L);
        user.setFirstName("yaswanth");
        user.setLastName("mallavarapu");
        user.setContactNumber("6305820373");
        user.setGender("MALE");
        user.setDesignation("Junior Dev");
        user.setRole(Role.EMPLOYEE);
        user.setEmail("yash@gmail.com");
        user.setPassword("abc@123");
        user.setStatus(UserStatus.ACTIVE);

        User user1=new User();
        user.setId(1L);
        user.setFirstName("yaswan");
        user.setLastName("mallavarapu");
        user.setContactNumber("6305820373");
        user.setGender("MALE");
        user.setDesignation("Junior Dev");
        user.setRole(Role.EMPLOYEE);
        user.setEmail("yash@gmail.com");
        user.setPassword("abc@123");
        user.setStatus(UserStatus.ACTIVE);


        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Assertions.assertEquals(user,userService.getUserByGivenId(1L));
        Assertions.assertNotEquals(user1,userService.getUserByGivenId(1L));
    }


    @Test
    public void getUserByGivenIdTestWhenNotExist(){

        //mocking when id not found
        Mockito.when(userRepository.findById(11L)).thenReturn(Optional.empty());


        //catching exception when id not found
        Exception e=Assertions.assertThrows(ResourceNotFoundException.class,()->userService.getUserByGivenId(11L));

        //check for error message
        Assertions.assertEquals("User not found with id.",e.getMessage());


    }


    @Test
    public void getAll(){
        User user=new User();
        user.setId(1L);
        user.setFirstName("yaswanth");
        user.setLastName("mallavarapu");
        user.setContactNumber("6305820373");
        user.setGender("MALE");
        user.setDesignation("Junior Dev");
        user.setRole(Role.EMPLOYEE);
        user.setEmail("yash@gmail.com");
        user.setPassword("abc@123");
        user.setStatus(UserStatus.ACTIVE);

        User user1=new User();
        user1.setId(1L);
        user1.setFirstName("lokesh");
        user1.setLastName("mekala");
        user1.setContactNumber("63058XXXXX");
        user1.setGender("MALE");
        user1.setDesignation("Senior Dev");
        user1.setRole(Role.ADMIN);
        user1.setEmail("lokesh@gmail.com");
        user1.setPassword("admin@123");
        user1.setStatus(UserStatus.ACTIVE);

        List<User>list=List.of(user,user1);


        //testing for page=0 and size=2
        Page<User> pageUser=new PageImpl<>(list);
        int page=0;
        int size=2;
        Pageable pageable= PageRequest.of(page,size);
        Mockito.when(userRepository.findAll(pageable)).thenReturn(pageUser);
        Assertions.assertEquals(2,userService.getAllUsers(0,2).size());

        //testing for page=0 and size=1
        pageUser=new PageImpl<>(list.subList(0,1));
        page=0;
        size=1;
        pageable= PageRequest.of(page,size);
        Mockito.when(userRepository.findAll(pageable)).thenReturn(pageUser);
        Assertions.assertEquals(1,userService.getAllUsers(0,1).size());

        //testing for page=0 and size=3
        pageUser=new PageImpl<>(list.subList(0,2));
        page=0;
        size=3;
        pageable= PageRequest.of(page,size);
        Mockito.when(userRepository.findAll(pageable)).thenReturn(pageUser);
        Assertions.assertEquals(2,userService.getAllUsers(0,3).size());
    }
}
