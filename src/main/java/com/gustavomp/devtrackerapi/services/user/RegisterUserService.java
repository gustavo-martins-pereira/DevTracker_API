package com.gustavomp.devtrackerapi.services.user;

import com.gustavomp.devtrackerapi.dtos.requests.user.RegisterRequestDto;
import com.gustavomp.devtrackerapi.exceptions.EntityAlreadyExistsException;
import com.gustavomp.devtrackerapi.mappers.UserMapper;
import com.gustavomp.devtrackerapi.models.User;
import com.gustavomp.devtrackerapi.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegisterUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public void execute(RegisterRequestDto registerRequestDto) {
        Optional<User> existingUser = userRepository.findByUsername(registerRequestDto.username());

        if (existingUser.isPresent()) throw new EntityAlreadyExistsException("Username already taken");

        User user = userMapper.toEntity(registerRequestDto);
        userRepository.save(user);
    }

}
