package com.teamtaskmanager.backend.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "tasks")
@Getter
@Setter
public class Task {
    @Id
    private String id;

    private String title;

    private String description;

    private TaskStatus status = TaskStatus.TODO;

    private LocalDate dueDate;

    private String projectId;

    private String assigneeId;
}
