package com.teamtaskmanager.backend.service;

import com.teamtaskmanager.backend.dto.AuthDtos;
import com.teamtaskmanager.backend.exception.BadRequestException;
import com.teamtaskmanager.backend.model.User;
import com.teamtaskmanager.backend.repository.UserRepository;
import com.teamtaskmanager.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthDtos.AuthResponse signup(AuthDtos.SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        User saved = userRepository.save(user);
        String token = jwtService.generateToken(
                saved.getEmail(),
                Map.of("role", saved.getRole().name(), "name", saved.getFullName())
        );
        return new AuthDtos.AuthResponse(token, saved.getId(), saved.getFullName(), saved.getEmail(), saved.getRole());
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));
        String token = jwtService.generateToken(
                user.getEmail(),
                Map.of("role", user.getRole().name(), "name", user.getFullName())
        );
        return new AuthDtos.AuthResponse(token, user.getId(), user.getFullName(), user.getEmail(), user.getRole());
    }
}
