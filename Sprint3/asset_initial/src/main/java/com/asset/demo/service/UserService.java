package com.asset.demo.service;

import com.asset.demo.dto.UserReqDto;
import com.asset.demo.dto.UserResDto;
import com.asset.demo.enums.UserStatus;
import com.asset.demo.exceptions.ResourceNotFoundException;
import com.asset.demo.model.User;

import com.asset.demo.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.module.ResolutionException;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.getUserByUsername(username);
    }


    public User insertUser(User user) {
        return userRepository.save(user);
    }
}
