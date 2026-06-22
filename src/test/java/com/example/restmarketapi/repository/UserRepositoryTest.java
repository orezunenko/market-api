package com.example.restmarketapi.repository;

import com.example.restmarketapi.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    @Test
    void findByEmail_ShouldReturnUser_WhenUserExists() {
        String email = "alex.jones@gmail.com";
        User mockUser = new User();
        mockUser.setId(101L);
        mockUser.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        Optional<User> result = userRepository.findByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
        assertEquals(101L, result.get().getId());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void findByEmail_ShouldReturnEmptyOptional_WhenUserDoesNotExist() {
        String email = "unknown@gmail.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<User> result = userRepository.findByEmail(email);

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByEmail(email);
    }
}