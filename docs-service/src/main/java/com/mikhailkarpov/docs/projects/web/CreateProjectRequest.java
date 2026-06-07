package com.mikhailkarpov.docs.projects.web;

import com.mikhailkarpov.docs.projects.command.CreateProjectCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

public record CreateProjectRequest(
    @NotBlank
    @Size(max = 64)
    String title,

    @Nullable
    @Size(max = 256)
    String description) {

  public CreateProjectCommand toCommand(String userId) {
    return new CreateProjectCommand(userId, title, description);
  }
}
