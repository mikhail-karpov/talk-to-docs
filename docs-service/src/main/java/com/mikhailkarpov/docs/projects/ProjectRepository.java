package com.mikhailkarpov.docs.projects;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository {

  void addProject(Project project);

  Optional<Project> findProject(ProjectId projectId);

  List<Project> findProjects(String userId);

  void updateProject(Project project);

  boolean deleteProject(ProjectId projectId);
}
