package com.teamtaskmanager.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;

public class ProjectDtos {
    @Data
    public static class CreateProjectRequest {
        @NotBlank
        private String name;
        private String description;
        private Set<String> memberIds;
    }

    @Data
    public static class ProjectResponse {
        private String id;
        private String name;
        private String description;
        private String ownerId;
        private String ownerName;
        private Set<MemberSummary> members;
    }

    @Data
    public static class MemberSummary {
        private String id;
        private String fullName;
        private String email;
    }
}
