package com.gustavomp.devtrackerapi.dtos.requests.client;

import jakarta.validation.constraints.*;

public record UpdateClientByIdRequestDto(
        @Size(min = 3, message = "The 'name' must be greater than 2 characters")
        String name,

        @Email(message = "Invalid email format")
        String email,

        @Pattern(
                regexp = "[0-9]{8}",
                message = "Invalid phone number format (expected: XXXXXXXX)"
        )
        String phone
) {}
