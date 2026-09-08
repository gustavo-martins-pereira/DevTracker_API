package com.gustavomp.devtrackerapi.dtos.requests;

import jakarta.validation.constraints.*;

public record UpdateClientByIdRequestDto(
        @Size(min = 3, message = "The 'name' must be greater than 2 characters")
        String name,

        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "The 'phone' couldn't be blank")
        @Pattern(
                regexp = "[0-9]{8}",
                message = "Invalid phone number format (expected: XXXXXXXX)"
        )
        String phone
) {}
