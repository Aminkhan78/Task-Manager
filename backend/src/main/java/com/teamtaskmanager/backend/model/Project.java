package com.teamtaskmanager.backend.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "projects")
@Getter
@Setter
public class Project {
    @Id
    private String id;

    private String name;

    private String description;

    private String ownerId;

    private Set<String> memberIds = new HashSet<>();
}
