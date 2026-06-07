package com.mikhailkarpov.docs.chat.command;

import org.jspecify.annotations.Nullable;

public record ConversationQuery(String userId, @Nullable String projectId) {}
