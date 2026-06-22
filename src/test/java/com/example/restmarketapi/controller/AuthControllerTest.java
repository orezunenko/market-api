package com.example.restmarketapi.controller;

import com.example.restmarketapi.dto.LoginResponseDto;
import com.example.restmarketapi.dto.UserLoginDto;
import com.example.restmarketapi.dto.UserRegisterDto;
import com.example.restmarketapi.entity.User;
import com.example.restmarketapi.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @Test
    void register_ShouldReturnOk_WhenDtoIsValid() {
        UserRegisterDto dto = new UserRegisterDto("test@example.com", "password123");

        ResponseEntity<String> response = authController.register(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Success", response.getBody());
        verify(userService, times(1)).register(dto);
    }

    @Test
    void login_ShouldReturnOkAndSetSession_WhenCredentialsAreValid() {
        UserLoginDto dto = new UserLoginDto("test@example.com", "password123");
        User user = new User(1L, "test@example.com", "encodedPassword");
        MockHttpSession session = new MockHttpSession();

        when(userService.login(dto)).thenReturn(user);

        ResponseEntity<LoginResponseDto> response = authController.login(dto, session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(session.getId(), response.getBody().getSessionId());
        assertEquals(1L, session.getAttribute("userId"));
        verify(userService, times(1)).login(dto);
    }
}