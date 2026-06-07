package com.mikhailkarpov.docs.projects;

public class ProjectNotFoundException extends RuntimeException {

  public ProjectNotFoundException(String message) {
    super(message);
  }

  public static ProjectNotFoundException of(ProjectId projectId) {
    return new ProjectNotFoundException("Project " + projectId.id() + " not found");
  }
}
