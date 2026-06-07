package com.mikhailkarpov.docs.projects.command;

import org.jspecify.annotations.Nullable;

public record CreateProjectCommand(String userId, String title, @Nullable String description) {}
