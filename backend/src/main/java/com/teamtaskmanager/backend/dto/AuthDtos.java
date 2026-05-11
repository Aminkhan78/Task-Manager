package com.teamtaskmanager.backend.dto;

import com.teamtaskmanager.backend.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class AuthDtos {
    @Data
    public static class SignupRequest {
        @NotBlank
        private String fullName;
        @Email
        @NotBlank
        private String email;
        @NotBlank
        private String password;
        private Role role = Role.MEMBER;
    }

    @Data
    public static class LoginRequest {
        @Email
        @NotBlank
        private String email;
        @NotBlank
        private String password;
    }

    @Data
    public static class AuthResponse {
        private final String token;
        private final Long userId;
        private final String fullName;
        private final String email;
        private final Role role;
    }
}
