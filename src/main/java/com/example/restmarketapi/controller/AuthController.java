package com.example.restmarketapi.controller;

import com.example.restmarketapi.dto.LoginResponseDto;
import com.example.restmarketapi.dto.UserLoginDto;
import com.example.restmarketapi.dto.UserRegisterDto;
import com.example.restmarketapi.entity.User;
import com.example.restmarketapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User registration and login management")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register user", description = "Creates a new user account with a unique email.")
    public void register(@Valid @RequestBody UserRegisterDto userRegisterDto) {
        userService.register(userRegisterDto);
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates user and saves user ID to the session.")
    public LoginResponseDto login(@Valid @RequestBody UserLoginDto userLoginDto, HttpSession session) {
        User user = userService.login(userLoginDto);
        session.setAttribute("userId", user.getId());
        return new LoginResponseDto(session.getId());
    }
}