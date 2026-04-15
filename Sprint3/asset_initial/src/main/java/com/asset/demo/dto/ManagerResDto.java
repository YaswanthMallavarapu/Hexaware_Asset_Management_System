package com.asset.demo.dto;

import com.asset.demo.enums.AccountStatus;

public record ManagerResDto(
        long id,
        String fullName,
        long userId,
        AccountStatus accountStatus

) {
}
