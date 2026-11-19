package com.gustavomp.devtrackerapi.mappers;

import com.gustavomp.devtrackerapi.dtos.requests.user.RegisterUserRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.user.LoginResponseDto;
import com.gustavomp.devtrackerapi.mappers.utils.PasswordHasher;
import com.gustavomp.devtrackerapi.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = PasswordHasher.class
)
public interface UserMapper {

    // Create User
    @Mapping(target = "password", source = "password", qualifiedByName = "hashPassword")
    User toEntity(RegisterUserRequestDto dto);

    // Login User
    LoginResponseDto toLoginResponseDto(String token);

}
