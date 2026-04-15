package com.asset.demo.controller;

import com.asset.demo.dto.EmployeeDto;
import com.asset.demo.dto.EmployeeFilterDto;
import com.asset.demo.dto.EmployeeReqDto;
import com.asset.demo.dto.EmployeeResDto;
import com.asset.demo.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/employee")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class EmployeeController {

    private EmployeeService employeeService;

    @PostMapping("/add")
    public ResponseEntity<Object> addEmployee(@Valid @RequestBody EmployeeReqDto employeeReqDto){

         employeeService.addEmployee(employeeReqDto);
         return ResponseEntity
                 .status(HttpStatus.CREATED)
                 .build();
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<EmployeeResDto>> getAllEmployees(@RequestParam(value = "page",required = false,defaultValue = "0") int page,
                                                       @RequestParam(value = "size",required = false,defaultValue = "5")int size){
        List<EmployeeResDto> list=employeeService.getAllEmployees(page,size);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(list);
    }

    @GetMapping("/get/{employeeId}")
    public ResponseEntity<EmployeeResDto> getEmployeeById(@PathVariable long employeeId){
        EmployeeResDto employeeResDto =employeeService.getEmployeeById(employeeId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(employeeResDto);
    }

    @PostMapping("/filter")
    public ResponseEntity<List<EmployeeResDto>> getEmployeeFilter(@RequestBody EmployeeFilterDto employeeFilterDto,
                                                                  @RequestParam(value = "page",required = false,defaultValue = "0") int page,
                                                                  @RequestParam(value = "size",required = false,defaultValue = "5")int size){
        List<EmployeeResDto>list=employeeService.filterEmployees(employeeFilterDto,page,size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(list);

    }

    @GetMapping("/pending")
    public ResponseEntity<List<EmployeeResDto>> getAllPendingAccounts(@RequestParam(value = "page",required = false,defaultValue = "0") int page,
                                                                      @RequestParam(value = "size",required = false,defaultValue = "5")int size){
        List<EmployeeResDto>list=employeeService.getAllPendingEmployees(page,size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(list);
    }


    @GetMapping("/count")
    public ResponseEntity<Long> getAdminsCount(){
        long count=employeeService.getCount();
        return ResponseEntity
                .ok()
                .body(count);
    }

    @GetMapping("/get-one")
    public ResponseEntity<EmployeeDto> getOne(Principal principal){
        EmployeeDto employeeDto=employeeService.getOne(principal.getName());
        return ResponseEntity
                .ok()
                .body(employeeDto);
    }
}
