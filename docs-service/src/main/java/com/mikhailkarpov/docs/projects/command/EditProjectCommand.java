package com.mikhailkarpov.docs.projects.command;

import com.mikhailkarpov.docs.projects.ProjectId;
import org.jspecify.annotations.Nullable;

public record EditProjectCommand(
    ProjectId projectId,
    @Nullable String title,
    @Nullable String description) {}
