package com.asset.demo.controller;

import com.asset.demo.dto.EmployeeResDto;
import com.asset.demo.dto.ManagerReqDto;
import com.asset.demo.dto.ManagerResDto;
import com.asset.demo.model.Manager;
import com.asset.demo.service.ManagerService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/manager")
@AllArgsConstructor
public class ManagerController {

    private final ManagerService managerService;
    @PostMapping("/add")
    public ResponseEntity<Object> addManager(@RequestBody ManagerReqDto managerReqDto){
       managerService.addManager(managerReqDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<ManagerResDto>> getAllManagers(@RequestParam(value = "page",required = false,defaultValue = "0") int page,
                                                              @RequestParam(value = "size",required = false,defaultValue = "5")int size){
        List<ManagerResDto> list=managerService.getAllManagers(page,size);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(list);
    }

    @GetMapping("/get/{managerId}")
    public ResponseEntity<ManagerResDto> getManagerById(@PathVariable long managerId){
        ManagerResDto managerResDto =managerService.getManagerById(managerId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(managerResDto);
    }

    @PutMapping("/approve-employee/{employeeId}")
    public ResponseEntity<Object> approveEmployeeAccount(@PathVariable long employeeId,
                                                         Principal principal){
    managerService.approveEmployee(employeeId,principal.getName());
    return ResponseEntity
            .status(HttpStatus.ACCEPTED)
            .build();

    }
}
