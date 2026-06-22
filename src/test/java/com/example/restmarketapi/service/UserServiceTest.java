package com.example.restmarketapi.service;

import com.example.restmarketapi.dto.UserLoginDto;
import com.example.restmarketapi.dto.UserRegisterDto;
import com.example.restmarketapi.dto.UserResponseDto;
import com.example.restmarketapi.entity.User;
import com.example.restmarketapi.exception.UserAlreadyExistsException;
import com.example.restmarketapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        UserRegisterDto registerDto = new UserRegisterDto("test@example.com", "password123");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistsException.class, () -> userService.register(registerDto));
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verifyNoInteractions(modelMapper, passwordEncoder);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_ShouldSaveUser_WhenEmailIsUnique() {
        UserRegisterDto registerDto = new UserRegisterDto("test@example.com", "password123");
        User unmappedUser = new User(null, "test@example.com", "password123");
        User savedUser = new User(1L, "test@example.com", "encodedPassword");
        UserResponseDto responseDto = new UserResponseDto(1L, "test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(modelMapper.map(registerDto, User.class)).thenReturn(unmappedUser);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(unmappedUser)).thenReturn(savedUser);
        when(modelMapper.map(savedUser, UserResponseDto.class)).thenReturn(responseDto);

        UserResponseDto result = userService.register(registerDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getEmail());

        verify(userRepository, times(1)).save(unmappedUser);
    }

    @Test
    void login_ShouldThrowException_WhenUserNotFound() {
        UserLoginDto loginDto = new UserLoginDto("test@example.com", "password123");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.login(loginDto));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void login_ShouldThrowException_WhenPasswordDoesNotMatch() {
        UserLoginDto loginDto = new UserLoginDto("test@example.com", "wrongPassword");
        User currentUser = new User(1L, "test@example.com", "encodedPassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(currentUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> userService.login(loginDto));
    }

    @Test
    void login_ShouldReturnUser_WhenCredentialsAreValid() {
        UserLoginDto loginDto = new UserLoginDto("test@example.com", "password123");
        User currentUser = new User(1L, "test@example.com", "encodedPassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(currentUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

        User result = userService.login(loginDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getEmail());
    }
}