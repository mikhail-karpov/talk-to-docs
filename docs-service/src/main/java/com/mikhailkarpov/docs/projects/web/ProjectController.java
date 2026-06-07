package com.mikhailkarpov.docs.projects.web;

import com.mikhailkarpov.docs.auth.User;
import com.mikhailkarpov.docs.projects.ProjectId;
import com.mikhailkarpov.docs.projects.ProjectService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

  private final ProjectService projectService;

  public ProjectController(ProjectService projectService) {
    this.projectService = projectService;
  }


  @GetMapping
  public List<ProjectResponse> getProjects(@AuthenticationPrincipal User user) {
    return projectService.listProjects(user.getId())
        .stream()
        .map(ProjectResponse::from)
        .toList();
  }

  @PostMapping
  public ProjectResponse createProject(
      @AuthenticationPrincipal User user, @Valid @RequestBody CreateProjectRequest request) {

    var command = request.toCommand(user.getId());
    var project = projectService.createProject(command);
    return ProjectResponse.from(project);
  }

  @GetMapping("/{projectId}")
  public ProjectResponse getProject(
      @PathVariable String projectId, @AuthenticationPrincipal User user) {

    var project = projectService.getProject(new ProjectId(projectId, user.getId()));
    return ProjectResponse.from(project);
  }

  @PutMapping("/{projectId}")
  public ProjectResponse editProject(
      @PathVariable String projectId,
      @AuthenticationPrincipal User user,
      @Valid @RequestBody EditProjectRequest request) {

    var command = request.toCommand(new ProjectId(projectId, user.getId()));
    var project = projectService.editProject(command);
    return ProjectResponse.from(project);
  }

  @DeleteMapping("/{projectId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteProject(
      @PathVariable String projectId, @AuthenticationPrincipal User user) {

    projectService.deleteProject(new ProjectId(projectId, user.getId()));
  }
}
