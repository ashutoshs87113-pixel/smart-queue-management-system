package com.smartqueue.service;

import com.smartqueue.dto.RegisterRequest;
import com.smartqueue.entity.Role;
import com.smartqueue.entity.User;
import com.smartqueue.exception.UserAlreadyExistsException;
import com.smartqueue.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private RegisterRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new RegisterRequest();
        validRequest.setFullName("Ashutosh Kumar");
        validRequest.setUsername("ashutosh123");
        validRequest.setEmail("ashutosh@gmail.com");
        validRequest.setPhone("9876543210");
        validRequest.setPassword("mypassword123");
        validRequest.setConfirmPassword("mypassword123");
    }

    @Test
    void registerUser_Success() {
        when(userRepository.existsByUsername("ashutosh123")).thenReturn(false);
        when(userRepository.existsByEmail("ashutosh@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("mypassword123")).thenReturn("hashedPassword123");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User registeredUser = userService.registerUser(validRequest);

        assertNotNull(registeredUser);
        assertEquals("Ashutosh Kumar", registeredUser.getFullName());
        assertEquals("ashutosh123", registeredUser.getUsername());
        assertEquals("ashutosh@gmail.com", registeredUser.getEmail());
        assertEquals("hashedPassword123", registeredUser.getPassword());
        assertEquals(Role.USER, registeredUser.getRole());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_PasswordMismatch_ThrowsException() {
        validRequest.setConfirmPassword("wrongPassword");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(validRequest));

        assertEquals("Passwords do not match", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_DuplicateUsername_ThrowsUserAlreadyExistsException() {
        when(userRepository.existsByUsername("ashutosh123")).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(validRequest));

        assertTrue(exception.getMessage().contains("Username already exists"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_DuplicateEmail_ThrowsUserAlreadyExistsException() {
        when(userRepository.existsByUsername("ashutosh123")).thenReturn(false);
        when(userRepository.existsByEmail("ashutosh@gmail.com")).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(validRequest));

        assertTrue(exception.getMessage().contains("Email is already registered"));
        verify(userRepository, never()).save(any());
    }
}
