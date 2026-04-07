package com.asset.demo.dto;

import com.asset.demo.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserReqDto(


        @NotBlank(message = "First name is required")
        @NotNull(message = "First name cannot be null")
        String firstName,

        @NotBlank(message = "Last name is required")
        @NotNull(message = "Last name cannot be null")
        String lastName,

        @NotBlank(message = "Gender is required")
        @NotNull(message = "Gender cannot be null")
        String gender,

        @NotBlank(message = "Contact number is required")
        @NotNull(message = "Contact number cannot be null")
        String contactNumber,

        @NotBlank(message = "designation number is required")
        @NotNull(message = "designation number cannot be null")
        String designation,

        @NotBlank(message = "Email is required")
        @NotNull(message = "Email cannot be null")
        String email,

        @NotBlank(message = "Password is required")
        @NotNull(message = "Password cannot be null")
        String password


) {
}