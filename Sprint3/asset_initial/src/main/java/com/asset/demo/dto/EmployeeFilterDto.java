package com.asset.demo.dto;

import com.asset.demo.enums.UserStatus;

public record EmployeeFilterDto(
        String userStatus,
        String employeeStatus

) {
}
