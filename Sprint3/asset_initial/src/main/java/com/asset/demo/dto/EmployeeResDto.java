package com.asset.demo.dto;

import com.asset.demo.enums.AccountStatus;
import com.asset.demo.enums.UserStatus;

public record EmployeeResDto(
        long id,
        String fullName,
        String gender,
        String contactNumber,
        String designation,
        UserStatus status,
        long userId

) {
}
