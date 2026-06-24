package com.example.restmarketapi.repository;

import com.example.restmarketapi.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    private static final Long USER_ID = 101L;
    private static final String EXISTING_EMAIL = "alex.jones@gmail.com";
    private static final String UNKNOWN_EMAIL = "unknown@gmail.com";
    private static final int EXPECTED_INVOCATIONS = 1;

    @Mock
    private UserRepository userRepository;

    @Test
    void findByEmail_ShouldReturnUser_WhenUserExists() {
        User mockUser = new User();
        mockUser.setId(USER_ID);
        mockUser.setEmail(EXISTING_EMAIL);

        when(userRepository.findByEmail(EXISTING_EMAIL)).thenReturn(Optional.of(mockUser));

        Optional<User> result = userRepository.findByEmail(EXISTING_EMAIL);

        assertTrue(result.isPresent());
        assertEquals(EXISTING_EMAIL, result.get().getEmail());
        assertEquals(USER_ID, result.get().getId());
        verify(userRepository, times(EXPECTED_INVOCATIONS)).findByEmail(EXISTING_EMAIL);
    }

    @Test
    void findByEmail_ShouldReturnEmptyOptional_WhenUserDoesNotExist() {
        when(userRepository.findByEmail(UNKNOWN_EMAIL)).thenReturn(Optional.empty());

        Optional<User> result = userRepository.findByEmail(UNKNOWN_EMAIL);

        assertFalse(result.isPresent());
        verify(userRepository, times(EXPECTED_INVOCATIONS)).findByEmail(UNKNOWN_EMAIL);
    }
}