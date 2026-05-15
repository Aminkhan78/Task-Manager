package com.teamtaskmanager.backend.repository;

import com.teamtaskmanager.backend.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProjectRepository extends MongoRepository<Project, String> {
    List<Project> findByOwnerIdOrMemberIdsContaining(String ownerId, String memberId);
}
