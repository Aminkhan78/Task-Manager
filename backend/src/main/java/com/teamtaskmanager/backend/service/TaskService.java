package com.teamtaskmanager.backend.service;

import com.teamtaskmanager.backend.dto.TaskDtos;
import com.teamtaskmanager.backend.exception.ResourceNotFoundException;
import com.teamtaskmanager.backend.model.Project;
import com.teamtaskmanager.backend.model.Role;
import com.teamtaskmanager.backend.model.Task;
import com.teamtaskmanager.backend.model.User;
import com.teamtaskmanager.backend.repository.TaskRepository;
import com.teamtaskmanager.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectService projectService;
    private final UserRepository userRepository;
    private final UserContextService userContextService;

    public TaskDtos.TaskResponse create(TaskDtos.CreateTaskRequest request) {
        Project project = projectService.getProjectById(request.getProjectId());
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setProjectId(project.getId());
        task.setDueDate(request.getDueDate());
        task.setStatus(request.getStatus());

        if (request.getAssigneeId() != null && !request.getAssigneeId().isBlank()) {
            userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
            task.setAssigneeId(request.getAssigneeId());
        }
        return toResponse(taskRepository.save(task));
    }

    public List<TaskDtos.TaskResponse> listByProject(String projectId) {
        return taskRepository.findByProjectId(projectId).stream().map(this::toResponse).toList();
    }

    public TaskDtos.TaskResponse updateStatus(String taskId, TaskDtos.UpdateTaskStatusRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        task.setStatus(request.getStatus());
        return toResponse(taskRepository.save(task));
    }

    public TaskDtos.DashboardResponse dashboard() {
        User current = userContextService.getCurrentUser();
        List<Task> tasks = current.getRole() == Role.ADMIN
                ? taskRepository.findAll()
                : taskRepository.findByAssigneeId(current.getId());

        TaskDtos.DashboardResponse response = new TaskDtos.DashboardResponse();
        response.setTotalTasks(tasks.size());
        response.setTodoTasks(tasks.stream().filter(t -> t.getStatus().name().equals("TODO")).count());
        response.setInProgressTasks(tasks.stream().filter(t -> t.getStatus().name().equals("IN_PROGRESS")).count());
        response.setDoneTasks(tasks.stream().filter(t -> t.getStatus().name().equals("DONE")).count());
        response.setOverdueTasks(tasks.stream()
                .filter(t -> t.getDueDate() != null && t.getDueDate().isBefore(LocalDate.now()) && !t.getStatus().name().equals("DONE"))
                .count());
        return response;
    }

    private TaskDtos.TaskResponse toResponse(Task task) {
        Project project = projectService.getProjectById(task.getProjectId());
        TaskDtos.TaskResponse response = new TaskDtos.TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setDueDate(task.getDueDate());
        response.setProjectId(task.getProjectId());
        response.setProjectName(project.getName());
        if (task.getAssigneeId() != null) {
            userRepository.findById(task.getAssigneeId()).ifPresent(assignee -> {
                response.setAssigneeId(assignee.getId());
                response.setAssigneeName(assignee.getFullName());
            });
        }
        return response;
    }
}
