package com.asset.demo.mapper;

import com.asset.demo.dto.EmployeeReqDto;
import com.asset.demo.dto.EmployeeResDto;
import com.asset.demo.enums.AccountStatus;
import com.asset.demo.enums.Role;
import com.asset.demo.enums.UserStatus;
import com.asset.demo.model.Employee;
import com.asset.demo.model.User;

public class EmployeeMapper {
    public static Employee mapToEntity(EmployeeReqDto employeeReqDto){
        Employee employee=new Employee();
        employee.setFirstName(employeeReqDto.firstName());
        employee.setLastName(employeeReqDto.lastName());
        employee.setGender(employeeReqDto.gender());
        employee.setContactNumber(employeeReqDto.contactNumber());
        employee.setDesignation(employeeReqDto.designation());


        return employee;
    }

    public static EmployeeResDto mapToDto(Employee employee) {
        return new EmployeeResDto(
                employee.getId(),
                employee.getFirstName()+" "+employee.getLastName(),
                employee.getGender(),
                employee.getContactNumber(),
                employee.getDesignation(),
                employee.getStatus(),
                employee.getUser().getId()
        );
    }
}
