package com.filecraft.service;

import com.filecraft.dto.AuthResponse;
import com.filecraft.dto.RegisterRequest;
import com.filecraft.entity.User;
import com.filecraft.repository.UserRepository;
import com.filecraft.dto.LoginRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "Email already exists"
            );
        }

        User user = new User();

        user.setEmail(request.getEmail().toLowerCase());
        user.setDisplayName(request.getDisplayName());

        String hashedPassword = passwordEncoder.encode(
                request.getPassword()
        );

        user.setPasswordHash(hashedPassword);

        User savedUser = userRepository.save(user);

        AuthResponse response = new AuthResponse();

        response.setUserId(savedUser.getId());
        response.setEmail(savedUser.getEmail());
        response.setDisplayName(savedUser.getDisplayName());
        response.setToken(null);
        response.setToken(jwtService.generateToken(savedUser));

        return response;
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        AuthResponse response = new AuthResponse();
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setDisplayName(user.getDisplayName());
        response.setToken(null);
        response.setToken(jwtService.generateToken(user));

        return response;
    }
}