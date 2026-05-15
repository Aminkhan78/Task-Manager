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
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
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
        project.setOwnerId(current.getId());

        Set<String> memberIds = new HashSet<>();
        memberIds.add(current.getId());
        if (request.getMemberIds() != null && !request.getMemberIds().isEmpty()) {
            memberIds.addAll(request.getMemberIds());
        }
        project.setMemberIds(memberIds);
        return toResponse(projectRepository.save(project));
    }

    public List<ProjectDtos.ProjectResponse> listProjects() {
        String currentUserId = userContextService.getCurrentUser().getId();
        return projectRepository.findByOwnerIdOrMemberIdsContaining(currentUserId, currentUserId).stream()
                .distinct()
                .map(this::toResponse)
                .toList();
    }

    public Project getProjectById(String id) {
        return projectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }

    public ProjectDtos.ProjectResponse toResponse(Project project) {
        Map<String, User> usersById = userRepository.findAllById(project.getMemberIds()).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        User owner = usersById.get(project.getOwnerId());
        if (owner == null) {
            owner = userRepository.findById(project.getOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project owner not found"));
        }

        ProjectDtos.ProjectResponse response = new ProjectDtos.ProjectResponse();
        response.setId(project.getId());
        response.setName(project.getName());
        response.setDescription(project.getDescription());
        response.setOwnerId(project.getOwnerId());
        response.setOwnerName(owner.getFullName());
        response.setMembers(project.getMemberIds().stream().map(memberId -> {
            User member = usersById.get(memberId);
            if (member == null) {
                return null;
            }
            ProjectDtos.MemberSummary summary = new ProjectDtos.MemberSummary();
            summary.setId(member.getId());
            summary.setFullName(member.getFullName());
            summary.setEmail(member.getEmail());
            return summary;
        }).filter(java.util.Objects::nonNull).collect(Collectors.toSet()));
        return response;
    }
}
