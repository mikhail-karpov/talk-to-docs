package com.mikhailkarpov.docs.chat.command;

import com.mikhailkarpov.docs.projects.ProjectId;
import org.jspecify.annotations.Nullable;

public record CreateConversationCommand(ProjectId projectId, @Nullable String title, String content) {

  public CreateConversationCommand(ProjectId projectId, String title, String content) {
    this.projectId = projectId;
    this.title = title != null && !title.isBlank() ? title : "Untitled";
    this.content = content;
  }
}
