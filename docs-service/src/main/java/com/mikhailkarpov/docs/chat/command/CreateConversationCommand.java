package com.mikhailkarpov.docs.chat.command;

import org.jspecify.annotations.Nullable;

public record CreateConversationCommand(String userId, @Nullable String title, String content) {

  public CreateConversationCommand(String userId, String title, String content) {
    this.userId = userId;
    this.title = title != null && !title.isBlank() ? title : "Untitled";
    this.content = content;
  }
}
