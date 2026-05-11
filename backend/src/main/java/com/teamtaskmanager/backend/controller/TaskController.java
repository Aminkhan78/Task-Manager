package com.teamtaskmanager.backend.controller;

import com.teamtaskmanager.backend.dto.TaskDtos;
import com.teamtaskmanager.backend.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public TaskDtos.TaskResponse create(@Valid @RequestBody TaskDtos.CreateTaskRequest request) {
        return taskService.create(request);
    }

    @GetMapping("/project/{projectId}")
    public List<TaskDtos.TaskResponse> byProject(@PathVariable Long projectId) {
        return taskService.listByProject(projectId);
    }

    @PatchMapping("/{taskId}/status")
    public TaskDtos.TaskResponse updateStatus(@PathVariable Long taskId,
                                              @Valid @RequestBody TaskDtos.UpdateTaskStatusRequest request) {
        return taskService.updateStatus(taskId, request);
    }

    @GetMapping("/dashboard")
    public TaskDtos.DashboardResponse dashboard() {
        return taskService.dashboard();
    }
}
