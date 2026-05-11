package com.teamtaskmanager.backend.service;

import com.teamtaskmanager.backend.dto.ProjectDtos;
import com.teamtaskmanager.backend.exception.ResourceNotFoundException;
import com.teamtaskmanager.backend.model.Project;
import com.teamtaskmanager.backend.model.User;
import com.teamtaskmanager.backend.repository.ProjectRepository;
import com.teamtaskmanager.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final UserContextService userContextService;

    public ProjectDtos.ProjectResponse createProject(ProjectDtos.CreateProjectRequest request) {
        User current = userContextService.getCurrentUser();
        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setOwner(current);

        Set<User> members = new HashSet<>();
        members.add(current);
        if (request.getMemberIds() != null && !request.getMemberIds().isEmpty()) {
            members.addAll(userRepository.findAllById(request.getMemberIds()));
        }
        project.setMembers(members);
        return toResponse(projectRepository.save(project));
    }

    public List<ProjectDtos.ProjectResponse> listProjects() {
        Long currentUserId = userContextService.getCurrentUser().getId();
        return projectRepository.findByOwnerIdOrMembersId(currentUserId, currentUserId).stream()
                .distinct()
                .map(this::toResponse)
                .toList();
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }

    public ProjectDtos.ProjectResponse toResponse(Project project) {
        ProjectDtos.ProjectResponse response = new ProjectDtos.ProjectResponse();
        response.setId(project.getId());
        response.setName(project.getName());
        response.setDescription(project.getDescription());
        response.setOwnerId(project.getOwner().getId());
        response.setOwnerName(project.getOwner().getFullName());
        response.setMembers(project.getMembers().stream().map(member -> {
            ProjectDtos.MemberSummary summary = new ProjectDtos.MemberSummary();
            summary.setId(member.getId());
            summary.setFullName(member.getFullName());
            summary.setEmail(member.getEmail());
            return summary;
        }).collect(Collectors.toSet()));
        return response;
    }
}
