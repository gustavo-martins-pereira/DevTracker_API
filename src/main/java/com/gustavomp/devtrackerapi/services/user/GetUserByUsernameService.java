package com.gustavomp.devtrackerapi.services.user;

import com.gustavomp.devtrackerapi.models.User;
import com.gustavomp.devtrackerapi.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetUserByUsernameService {

    private final UserRepository userRepository;

    public User execute(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);

        return optionalUser.orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

}
