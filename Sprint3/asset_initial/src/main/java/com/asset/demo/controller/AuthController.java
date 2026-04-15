package com.asset.demo.controller;

import com.asset.demo.dto.UserDto;
import com.asset.demo.model.User;
import com.asset.demo.service.UserService;
import com.asset.demo.util.JwtUtility;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class AuthController {

    private final JwtUtility jwtUtility;
    private final UserService userService;

    @GetMapping("/login")
    public ResponseEntity<?> login(Principal principal){
        String loggedInUser = principal.getName();
//        System.out.println(principal);
        Map<String,String> map = new HashMap<>();
        map.put("token", jwtUtility.generateToken(loggedInUser));
        return ResponseEntity.status(HttpStatus.OK)
                .body(map);
    }
    @GetMapping("/user-details")
    public ResponseEntity<UserDto> getUserDetails(Principal principal){

        String username=principal.getName();
        User user=(User)userService.loadUserByUsername(username);
        return ResponseEntity.ok()
                .body(new UserDto(user.getUsername(),user.getRole()));
    }
}
