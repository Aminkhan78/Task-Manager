package com.teamtaskmanager.backend.controller;

import com.teamtaskmanager.backend.dto.ProjectDtos;
import com.teamtaskmanager.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ProjectDtos.MemberSummary> list() {
        return userRepository.findAll().stream().map(user -> {
            ProjectDtos.MemberSummary dto = new ProjectDtos.MemberSummary();
            dto.setId(user.getId());
            dto.setFullName(user.getFullName());
            dto.setEmail(user.getEmail());
            return dto;
        }).toList();
    }
}
