package com.teamtaskmanager.backend.controller;

import com.teamtaskmanager.backend.dto.ProjectDtos;
import com.teamtaskmanager.backend.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ProjectDtos.ProjectResponse create(@Valid @RequestBody ProjectDtos.CreateProjectRequest request) {
        return projectService.createProject(request);
    }

    @GetMapping
    public List<ProjectDtos.ProjectResponse> list() {
        return projectService.listProjects();
    }
}
