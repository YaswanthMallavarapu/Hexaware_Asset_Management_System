package com.asset.demo.controller;

import com.asset.demo.dto.FilterResDto;
import com.asset.demo.dto.UserDto;
import com.asset.demo.enums.AccountStatus;
import com.asset.demo.enums.UserStatus;
import com.asset.demo.exceptions.UserNotActivatedException;
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

import javax.security.auth.login.AccountLockedException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.util.StringUtils.capitalize;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class AuthController {

    private final JwtUtility jwtUtility;
    private final UserService userService;

    @GetMapping("/login")
    public ResponseEntity<Map<String, String>> login(Principal principal) throws UserNotActivatedException {
        String loggedInUser = principal.getName();
        User user = (User) userService.loadUserByUsername(loggedInUser);

        if (user.getAccountStatus() != AccountStatus.APPROVED) {
            throw new UserNotActivatedException("Account not approved by admin");
        }
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

    @GetMapping("/account-status")
    public ResponseEntity<List<FilterResDto>> getAccountStatus(){
        List<FilterResDto> list=new ArrayList<>();
        list.add(new FilterResDto("ALL","ALL"));
        for (AccountStatus value : AccountStatus.values()) {
            list.add(new FilterResDto(value.toString(),value.toString()));
        }
        return  ResponseEntity
                .ok()
                .body(list);

    }
    @GetMapping("/user-status")
    public ResponseEntity<List<FilterResDto>> getUserStatus(){
        List<FilterResDto> list=new ArrayList<>();
        list.add(new FilterResDto("ALL","ALL"));
        for (UserStatus value : UserStatus.values()) {
            list.add(new FilterResDto(value.toString(),value.toString()));
        }
        return  ResponseEntity
                .ok()
                .body(list);

    }

    @GetMapping("/user-status/v2")
    public ResponseEntity<List<FilterResDto>> getUserStatusV2(){
        List<FilterResDto> list=new ArrayList<>();
        list.add(new FilterResDto("ALL","All"));
        for (UserStatus value : UserStatus.values()) {
            list.add(new FilterResDto(capitalize(value.toString().toLowerCase()),value.toString()));
        }
        return  ResponseEntity
                .ok()
                .body(list);

    }
}
