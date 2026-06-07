package com.mikhailkarpov.docs.projects;

import java.time.Instant;
import java.util.UUID;
import org.jspecify.annotations.Nullable;

public record Project(
    String id,
    String userId,
    String title,
    @Nullable
    String description,
    Instant updatedAt) {

  public Project(String userId, String title, String description) {
    this(UUID.randomUUID().toString(), userId, title, description, Instant.now());
  }

  public Project edit(String title, String description) {
    return rename(title).updateDescription(description);
  }

  private Project rename(String title) {
    if (title != null && !title.equals(this.title)) {
      return new Project(id, userId, title, description, Instant.now());
    }
    return this;
  }

  private Project updateDescription(String description) {
    if (description != null && !description.equals(this.description)) {
      return new Project(id, userId, title, description, Instant.now());
    }
    return this;
  }

  public ProjectId projectId() {
    return new ProjectId(id, userId);
  }
}
