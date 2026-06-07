package com.mikhailkarpov.docs.projects;

import com.mikhailkarpov.docs.projects.command.CreateProjectCommand;
import com.mikhailkarpov.docs.projects.command.EditProjectCommand;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {

  private final ProjectRepository projectRepository;

  public ProjectService(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  public List<Project> listProjects(String userId) {
    return projectRepository.findProjects(userId);
  }

  public Project createProject(CreateProjectCommand command) {
    var project = new Project(command.userId(), command.title(), command.description());
    projectRepository.addProject(project);
    return project;
  }

  public Project getProject(ProjectId projectId) {
    return projectRepository.findProject(projectId)
        .orElseThrow(() -> ProjectNotFoundException.of(projectId));
  }

  @Transactional
  public Project editProject(EditProjectCommand command) {
    var project = projectRepository.findProject(command.projectId())
        .orElseThrow(() -> ProjectNotFoundException.of(command.projectId()))
        .edit(command.title(), command.description());
    projectRepository.updateProject(project);
    return project;
  }

  public void deleteProject(ProjectId projectId) {
    boolean isDeleted = projectRepository.deleteProject(projectId);
    if (!isDeleted) {
      throw ProjectNotFoundException.of(projectId);
    }
  }
}
