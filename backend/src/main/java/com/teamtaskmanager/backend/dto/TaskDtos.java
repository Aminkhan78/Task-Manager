package com.teamtaskmanager.backend.dto;

import com.teamtaskmanager.backend.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

public class TaskDtos {
    @Data
    public static class CreateTaskRequest {
        @NotBlank
        private String title;
        private String description;
        @NotNull
        private Long projectId;
        private Long assigneeId;
        private LocalDate dueDate;
        private TaskStatus status = TaskStatus.TODO;
    }

    @Data
    public static class UpdateTaskStatusRequest {
        @NotNull
        private TaskStatus status;
    }

    @Data
    public static class TaskResponse {
        private Long id;
        private String title;
        private String description;
        private TaskStatus status;
        private LocalDate dueDate;
        private Long projectId;
        private String projectName;
        private Long assigneeId;
        private String assigneeName;
    }

    @Data
    public static class DashboardResponse {
        private long totalTasks;
        private long todoTasks;
        private long inProgressTasks;
        private long doneTasks;
        private long overdueTasks;
    }
}
