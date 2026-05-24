package com.mikhailkarpov.docs.chat.web;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateConversationRequest(
    @Size(max = 64) String title,
    @NotNull @Size(min = 2, max = 256) String content) {}
