package com.example.restmarketapi.service;

import com.example.restmarketapi.dto.UserLoginDto;
import com.example.restmarketapi.dto.UserRegisterDto;
import com.example.restmarketapi.dto.UserResponseDto;
import com.example.restmarketapi.entity.User;
import com.example.restmarketapi.exception.UserAlreadyExistsException;
import com.example.restmarketapi.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDto register(UserRegisterDto userRegisterDto) {
        if (userRepository.findByEmail(userRegisterDto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("A user with this email already exists");
        }

        User newUser = modelMapper.map(userRegisterDto, User.class);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        User savedUser = userRepository.save(newUser);
        return modelMapper.map(savedUser, UserResponseDto.class);
    }

    public User login(UserLoginDto userLoginDto) {
        User currentUser = userRepository.findByEmail(userLoginDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(userLoginDto.getPassword(), currentUser.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return currentUser;
    }
}
