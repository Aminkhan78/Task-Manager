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
        private Set<Long> memberIds;
    }

    @Data
    public static class ProjectResponse {
        private Long id;
        private String name;
        private String description;
        private Long ownerId;
        private String ownerName;
        private Set<MemberSummary> members;
    }

    @Data
    public static class MemberSummary {
        private Long id;
        private String fullName;
        private String email;
    }
}
