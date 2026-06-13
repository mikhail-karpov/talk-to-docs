package com.mikhailkarpov.docs.documents.command;

import org.jspecify.annotations.Nullable;

public record DocumentQuery(String userId, @Nullable String projectId) {}
