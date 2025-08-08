package com.UAPP.ProjectApplication.repository;

import com.UAPP.ProjectApplication.model.ProjectEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ProjectRepository extends MongoRepository<ProjectEntity, String> {
    List<ProjectEntity> findByOwnerUserId(String ownerUserId);
}