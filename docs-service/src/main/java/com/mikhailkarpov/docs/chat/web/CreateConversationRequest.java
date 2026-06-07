package com.mikhailkarpov.docs.chat.web;

import com.mikhailkarpov.docs.chat.command.CreateConversationCommand;
import com.mikhailkarpov.docs.projects.ProjectId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateConversationRequest(
    @NotBlank
    String projectId,

    @Size(max = 64)
    String title,

    @NotNull
    @Size(min = 2, max = 256)
    String content) {

  public CreateConversationCommand toCommand(String userId) {
    return new CreateConversationCommand(new ProjectId(projectId, userId), title, content);
  }
}
