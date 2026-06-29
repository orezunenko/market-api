package com.example.restmarketapi.service;

import com.example.restmarketapi.dto.UserLoginDto;
import com.example.restmarketapi.dto.UserRegisterDto;
import com.example.restmarketapi.dto.UserResponseDto;
import com.example.restmarketapi.entity.User;
import com.example.restmarketapi.exception.UserAlreadyExistsException;
import com.example.restmarketapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final String USER_ALREADY_EXISTS_MSG = "A user with this email already exists";
    private static final String USER_NOT_FOUND_MSG = "User not found";
    private static final String INVALID_CREDENTIALS_MSG = "Invalid email or password";

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDto register(UserRegisterDto userRegisterDto) {
        if (userRepository.findByEmail(userRegisterDto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException(USER_ALREADY_EXISTS_MSG);
        }

        User newUser = modelMapper.map(userRegisterDto, User.class);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        User savedUser = userRepository.save(newUser);
        return modelMapper.map(savedUser, UserResponseDto.class);
    }

    public User login(UserLoginDto userLoginDto) {
        User currentUser = userRepository.findByEmail(userLoginDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND_MSG));

        if (!passwordEncoder.matches(userLoginDto.getPassword(), currentUser.getPassword())) {
            throw new IllegalArgumentException(INVALID_CREDENTIALS_MSG);
        }

        return currentUser;
    }
}
