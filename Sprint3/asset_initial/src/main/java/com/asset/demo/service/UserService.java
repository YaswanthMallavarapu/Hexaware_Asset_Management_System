package com.asset.demo.service;

import com.asset.demo.dto.UserReqDto;
import com.asset.demo.dto.UserResDto;
import com.asset.demo.enums.UserStatus;
import com.asset.demo.exceptions.ResourceNotFoundException;
import com.asset.demo.mapper.UserMapper;
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
    private final PasswordEncoder passwordEncoder;
    public void addUser(@Valid UserReqDto userReqDto) {
        //map userReqDto to user

        User user= UserMapper.mapToEntity(userReqDto);
        user.setPassword(passwordEncoder.encode(userReqDto.password()));
        user.setStatus(UserStatus.ACTIVE);
        //save entity
        userRepository.save(user);
    }

    public List<UserResDto> getAllUsers(int page, int size) {
        //create pagination
        Pageable pageable=PageRequest.of(page,size);
        //get users form DB
        Page<User> list=userRepository.findAll(pageable);
        return list.toList()
                .stream()
                .map(UserMapper::mapToDto)
                .toList();
    }

    public UserResDto getUserById(long id) {
        User user=userRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("user with given id not found"));
        return UserMapper.mapToDto(user);
    }

    public User getUserByGivenId(long employeeId) {
        return userRepository.findById(employeeId)
                .orElseThrow(()->new ResourceNotFoundException("User not found with id."));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.getUserByUsername(username);
    }
}
