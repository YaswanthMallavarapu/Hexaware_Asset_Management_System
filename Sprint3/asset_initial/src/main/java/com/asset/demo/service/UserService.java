package com.asset.demo.service;

import com.asset.demo.model.User;
import com.asset.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.atInfo().log("Loading user by username: {}", username);

        UserDetails user = userRepository.getUserByUsername(username);

        log.atInfo().log("User loaded: {}", username);
        return user;
    }

    public User insertUser(User user) {
        log.atInfo().log("Inserting user: {}", user.getUsername());

        User saved = userRepository.save(user);

        log.atInfo().log("User inserted successfully: {}", saved.getUsername());
        return saved;
    }
}