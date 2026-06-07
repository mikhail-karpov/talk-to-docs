package com.mikhailkarpov.docs.chat;

import com.mikhailkarpov.docs.projects.ProjectId;
import java.time.Instant;

public record Conversation(String id, ProjectId projectId, String title, Instant createdAt) {

  public String userId() {
    return projectId.userId();
  }
}
