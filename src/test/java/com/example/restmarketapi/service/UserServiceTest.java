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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final Long USER_ID = 1L;
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String WRONG_PASSWORD = "wrongPassword";
    private static final String ENCODED_PASSWORD = "encodedPassword";

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
        UserRegisterDto registerDto = new UserRegisterDto(TEST_EMAIL, TEST_PASSWORD);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistsException.class, () -> userService.register(registerDto));
        verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
        verifyNoInteractions(modelMapper, passwordEncoder);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_ShouldSaveUser_WhenEmailIsUnique() {
        UserRegisterDto registerDto = new UserRegisterDto(TEST_EMAIL, TEST_PASSWORD);
        User unmappedUser = new User(null, TEST_EMAIL, TEST_PASSWORD);
        User savedUser = new User(USER_ID, TEST_EMAIL, ENCODED_PASSWORD);
        UserResponseDto responseDto = new UserResponseDto(USER_ID, TEST_EMAIL);

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(modelMapper.map(registerDto, User.class)).thenReturn(unmappedUser);
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(unmappedUser)).thenReturn(savedUser);
        when(modelMapper.map(savedUser, UserResponseDto.class)).thenReturn(responseDto);

        UserResponseDto result = userService.register(registerDto);

        assertNotNull(result);
        assertEquals(USER_ID, result.getId());
        assertEquals(TEST_EMAIL, result.getEmail());

        verify(userRepository, times(1)).save(unmappedUser);
    }

    @Test
    void login_ShouldThrowException_WhenUserNotFound() {
        UserLoginDto loginDto = new UserLoginDto(TEST_EMAIL, TEST_PASSWORD);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.login(loginDto));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void login_ShouldThrowException_WhenPasswordDoesNotMatch() {
        UserLoginDto loginDto = new UserLoginDto(TEST_EMAIL, WRONG_PASSWORD);
        User currentUser = new User(USER_ID, TEST_EMAIL, ENCODED_PASSWORD);

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(currentUser));
        when(passwordEncoder.matches(WRONG_PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> userService.login(loginDto));
    }

    @Test
    void login_ShouldReturnUser_WhenCredentialsAreValid() {
        UserLoginDto loginDto = new UserLoginDto(TEST_EMAIL, TEST_PASSWORD);
        User currentUser = new User(USER_ID, TEST_EMAIL, ENCODED_PASSWORD);

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(currentUser));
        when(passwordEncoder.matches(TEST_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

        User result = userService.login(loginDto);

        assertNotNull(result);
        assertEquals(USER_ID, result.getId());
        assertEquals(TEST_EMAIL, result.getEmail());
    }
}