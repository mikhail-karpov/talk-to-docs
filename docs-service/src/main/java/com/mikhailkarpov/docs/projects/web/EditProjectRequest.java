package com.mikhailkarpov.docs.projects.web;

import com.mikhailkarpov.docs.projects.ProjectId;
import com.mikhailkarpov.docs.projects.command.EditProjectCommand;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

public record EditProjectRequest(
    @Nullable
    @Size(max = 64)
    String title,

    @Nullable
    @Size(max = 256)
    String description) {

    public EditProjectCommand toCommand(ProjectId projectId) {
        return new EditProjectCommand(projectId, title, description);
    }
}
