package com.asset.demo.service;

import com.asset.demo.model.User;
import com.asset.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    public void loadUserByUsername_success() {

        User user = new User();
        user.setUsername("john");

        when(userRepository.getUserByUsername("john")).thenReturn(user);

        UserDetails result = userService.loadUserByUsername("john");

        assertNotNull(result);
        assertEquals("john", result.getUsername());

        verify(userRepository, times(1)).getUserByUsername("john");
    }

    @Test
    public void loadUserByUsername_userNotFound_returnsNull() {

        when(userRepository.getUserByUsername("unknown")).thenReturn(null);

        UserDetails result = userService.loadUserByUsername("unknown");

        assertNull(result);

        verify(userRepository).getUserByUsername("unknown");
    }

    @Test
    public void insertUser_success() {

        User user = new User();
        user.setUsername("john");

        when(userRepository.save(user)).thenReturn(user);

        User saved = userService.insertUser(user);

        assertNotNull(saved);
        assertEquals("john", saved.getUsername());

        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void insertUser_nullInput() {

        when(userRepository.save(null)).thenReturn(null);

        User saved = userService.insertUser(null);

        assertNull(saved);

        verify(userRepository).save(null);
    }

    @Test
    public void loadUserByUsername_repositoryThrowsException() {

        when(userRepository.getUserByUsername("error"))
                .thenThrow(new RuntimeException("DB Error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.loadUserByUsername("error"));

        assertEquals("DB Error", ex.getMessage());
    }
}