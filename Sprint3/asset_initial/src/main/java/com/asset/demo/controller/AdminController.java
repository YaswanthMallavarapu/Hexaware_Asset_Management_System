package com.asset.demo.controller;

import com.asset.demo.dto.AdminReqDto;
import com.asset.demo.service.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
public class AdminController {

    private final AdminService adminService;
    @PostMapping("/add")
    public ResponseEntity<Object> addAdmin(@RequestBody AdminReqDto adminReqDto){
        adminService.addAdmin(adminReqDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();

    }

    @PutMapping("/approve-manager/{managerId}")
    public ResponseEntity<Object> approveManagerAccount(@PathVariable long managerId,
                                                         Principal principal){
        adminService.approveManager(managerId,principal.getName());
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();

    }
}
