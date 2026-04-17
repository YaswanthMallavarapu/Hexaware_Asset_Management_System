package com.asset.demo.controller;


import com.asset.demo.dto.ManagerDocumentResDto;
import com.asset.demo.service.ManagerDocumentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping ("/api/manager-document")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class ManagerDocumentController {
    private final ManagerDocumentService managerDocumentService;

    @PostMapping("/upload")
    public ResponseEntity<ManagerDocumentResDto> upload(Principal principal,
                                                        @RequestParam("file")MultipartFile file) throws IOException {

        ManagerDocumentResDto managerDocumentResDto=managerDocumentService.upload(principal.getName(),file);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(managerDocumentResDto);

    }
    @GetMapping("/profile")
    public ResponseEntity<String> getManagerProfile(Principal principal){

        String url=managerDocumentService.getProfile(principal.getName());
        return ResponseEntity
                .ok()
                .body(url);

    }

}
