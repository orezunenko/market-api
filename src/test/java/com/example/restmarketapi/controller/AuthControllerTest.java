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
import org.springframework.mock.web.MockHttpSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private static final Long USER_ID = 1L;
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final String SESSION_USER_ID_KEY = "userId";
    private static final int EXPECTED_INVOCATIONS = 1;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @Test
    void register_ShouldReturnOk_WhenDtoIsValid() {
        UserRegisterDto dto = new UserRegisterDto(TEST_EMAIL, TEST_PASSWORD);

        authController.register(dto);
        verify(userService, times(EXPECTED_INVOCATIONS)).register(dto);
    }

    @Test
    void login_ShouldReturnOkAndSetSession_WhenCredentialsAreValid() {
        UserLoginDto dto = new UserLoginDto(TEST_EMAIL, TEST_PASSWORD);
        User user = new User(USER_ID, TEST_EMAIL, ENCODED_PASSWORD);
        MockHttpSession session = new MockHttpSession();

        when(userService.login(dto)).thenReturn(user);

        LoginResponseDto responseDto = authController.login(dto, session);

        assertNotNull(responseDto);
        assertEquals(session.getId(), responseDto.getSessionId());
        assertEquals(USER_ID, session.getAttribute(SESSION_USER_ID_KEY));
        verify(userService, times(EXPECTED_INVOCATIONS)).login(dto);
    }
}