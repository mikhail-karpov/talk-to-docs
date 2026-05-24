package com.mikhailkarpov.docs.documents;

import com.mikhailkarpov.docs.documents.web.DocumentStatus;
import java.time.Instant;

public class DocumentMetadata {

  private final String id;
  private final String userId;
  private final String name;
  private final long sizeBytes;
  private DocumentStatus status;
  private Instant updatedAt;

  public DocumentMetadata(String id, String userId, String name, long sizeBytes, DocumentStatus status) {
    this.id = id;
    this.userId = userId;
    this.name = name;
    this.sizeBytes = sizeBytes;
    setStatus(status);
  }

  public void setStatus(DocumentStatus status) {
    this.status = status;
    this.updatedAt = Instant.now();
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public long getSizeBytes() {
    return sizeBytes;
  }

  public DocumentStatus getStatus() {
    return status;
  }

  public String getUserId() {
    return userId;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public final boolean equals(Object o) {
    if (!(o instanceof DocumentMetadata document))
      return false;

    return id.equals(document.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public String toString() {
    return "DocumentMetadata{" +
        "id='" + id + '\'' +
        ", userId='" + userId + '\'' +
        ", name='" + name + '\'' +
        ", sizeBytes=" + sizeBytes +
        ", status=" + status +
        ", updatedAt=" + updatedAt +
        '}';
  }
}
