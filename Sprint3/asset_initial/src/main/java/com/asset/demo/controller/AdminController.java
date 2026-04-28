package com.asset.demo.controller;

import com.asset.demo.dto.AdminDto;
import com.asset.demo.dto.AdminReqDto;
import com.asset.demo.dto.AdminResDto;
import com.asset.demo.dto.AssetAllocationResDto;
import com.asset.demo.mapper.AdminDocumentResDto;
import com.asset.demo.service.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
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

    @PutMapping("/reject-manager/{managerId}")
    public ResponseEntity<Object> rejectManagerAccount(@PathVariable long managerId,
                                                        Principal principal){
        adminService.rejectManager(managerId,principal.getName());
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();

    }

    @GetMapping("/count")
    public ResponseEntity<Long> getAdminsCount(){
        long count=adminService.getCount();
        return ResponseEntity
                .ok()
                .body(count);
    }


    @GetMapping("/get-all")
    public ResponseEntity<List<AdminDto>> getAllAllocatedAsset(
            @RequestParam(value = "page",required = false,defaultValue = "0")int page,
            @RequestParam(value = "size",required = false,defaultValue = "5")int size,
            Principal principal
    ){

        List<AdminDto> list=adminService.getAll(principal.getName(),page,size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(list);

    }

    @GetMapping("/get-one")
    public ResponseEntity<AdminResDto> getOne(Principal principal){
        AdminResDto adminResDto=adminService.getOne(principal.getName());
        return ResponseEntity
                .ok()
                .body(adminResDto);
    }

    @PostMapping("/upload")
    public ResponseEntity<AdminDocumentResDto> uploadProfile(Principal principal,
                                                @RequestParam MultipartFile file) throws IOException {

        AdminDocumentResDto adminDocumentResDto =adminService.upload(principal.getName(),file);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adminDocumentResDto);
    }

    @GetMapping("/profile")
    public ResponseEntity<String> getAdminProfile(Principal principal){

        String url=adminService.getProfile(principal.getName());
        return ResponseEntity
                .ok()
                .body(url);

    }


}
