package com.gustavomp.devtrackerapi.dtos.requests;

import jakarta.validation.constraints.*;

public record CreateClientRequestDto(
        @NotBlank(message = "The 'name' couldn't be blank")
        @Size(min = 3, message = "The 'name' must be greater than 3 characters")
        String name,

        @NotBlank(message = "The 'email' couldn't be blank")
        @Email
        String email,

        @NotBlank(message = "The 'phone' couldn't be blank")
        @Pattern(
                regexp = "[0-9]{8}",
                message = "Invalid phone number format (expected: XXXXXXXX)"
        )
        String phone,

        @NotNull(message = "The 'active' is required")
        Boolean active
) {}
