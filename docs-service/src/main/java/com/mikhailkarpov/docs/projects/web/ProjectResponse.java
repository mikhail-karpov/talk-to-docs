package com.mikhailkarpov.docs.projects.web;

import com.mikhailkarpov.docs.projects.Project;
import java.time.Instant;

public record ProjectResponse(String id, String title, String description, Instant updatedAt) {

  public static ProjectResponse from(Project project) {
    return new ProjectResponse(
        project.id(),
        project.title(),
        project.description(),
        project.updatedAt()
    );
  }
}
