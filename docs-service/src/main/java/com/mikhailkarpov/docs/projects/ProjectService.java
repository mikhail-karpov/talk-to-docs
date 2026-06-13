package com.mikhailkarpov.docs.projects;

import com.mikhailkarpov.docs.projects.command.CreateProjectCommand;
import com.mikhailkarpov.docs.projects.command.EditProjectCommand;
import com.mikhailkarpov.docs.projects.event.ProjectCreatedEvent;
import com.mikhailkarpov.docs.projects.event.ProjectDeletedEvent;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {

  private final ProjectRepository projectRepository;
  private final ApplicationEventPublisher eventPublisher;

  public ProjectService(
      ProjectRepository projectRepository, ApplicationEventPublisher eventPublisher) {

    this.projectRepository = projectRepository;
    this.eventPublisher = eventPublisher;
  }

  public List<Project> listProjects(String userId) {
    return projectRepository.findProjects(userId);
  }

  @Transactional
  public Project createProject(CreateProjectCommand command) {
    var project = new Project(command.userId(), command.title(), command.description());
    projectRepository.addProject(project);
    eventPublisher.publishEvent(new ProjectCreatedEvent(project));
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

  @Transactional
  public void deleteProject(ProjectId projectId) {
    var project = projectRepository.deleteProject(projectId).orElse(null);
    if (project == null) {
      throw ProjectNotFoundException.of(projectId);
    }
    eventPublisher.publishEvent(new ProjectDeletedEvent(project));
  }
}
