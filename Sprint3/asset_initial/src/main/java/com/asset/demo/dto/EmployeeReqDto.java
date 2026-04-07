package com.asset.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmployeeReqDto(

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "Gender is required")
        String gender,

        @NotBlank(message = "Contact number is required")
        String contactNumber,

        @NotBlank(message = "Designation is required")
        String designation,

        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password

) {}