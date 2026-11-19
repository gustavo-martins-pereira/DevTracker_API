package com.gustavomp.devtrackerapi.mappers.utils;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordHasher {

    private final PasswordEncoder passwordEncoder;

    @Named("hashPassword")
    public String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

}
