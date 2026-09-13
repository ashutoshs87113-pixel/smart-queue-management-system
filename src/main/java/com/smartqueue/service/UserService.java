package com.smartqueue.service;

import com.smartqueue.dto.RegisterRequest;
import com.smartqueue.entity.Role;
import com.smartqueue.entity.User;
import com.smartqueue.exception.UserAlreadyExistsException;
import com.smartqueue.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        if (userRepository.existsByUsername(request.getUsername().trim())) {
            throw new UserAlreadyExistsException("Username already exists. Please choose another username.");
        }

        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new UserAlreadyExistsException("Email is already registered. Please use another email.");
        }

        User user = new User(
                request.getFullName().trim(),
                request.getUsername().trim(),
                request.getEmail().trim().toLowerCase(),
                passwordEncoder.encode(request.getPassword()),
                request.getPhone().trim(),
                Role.USER
        );

        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByUsernameOrEmail(String identifier) {
        return userRepository.findByUsernameOrEmail(identifier, identifier);
    }

    public long getTotalUsers() {
        return userRepository.count();
    }
}
