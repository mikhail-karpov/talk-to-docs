package com.mikhailkarpov.docs.documents;

import com.mikhailkarpov.docs.documents.web.DocumentStatus;
import java.time.Instant;
import java.util.UUID;

public class DocumentMetadata {

  private final String id;
  private final String userId;
  private final String projectId;
  private final String name;
  private final String contentType;
  private final long sizeBytes;
  private DocumentStatus status;
  private Instant updatedAt;

  private DocumentMetadata(String id, String userId, String projectId, String name, String contentType, long sizeBytes, DocumentStatus status) {
    this.id = id;
    this.userId = userId;
    this.projectId = projectId;
    this.name = name;
    this.contentType = contentType;
    this.sizeBytes = sizeBytes;
    setStatus(status);
  }

  public static DocumentMetadataBuilder builder() {
    return new DocumentMetadataBuilder();
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

  public String getContentType() {
    return contentType;
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

  public String getProjectId() {
    return projectId;
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
        ", projectId='" + projectId + '\'' +
        ", name='" + name + '\'' +
        ", contentType='" + contentType + '\'' +
        ", sizeBytes=" + sizeBytes +
        ", status=" + status +
        ", updatedAt=" + updatedAt +
        '}';
  }

  public static class DocumentMetadataBuilder {

    private String id = UUID.randomUUID().toString();
    private String userId;
    private String projectId;
    private String name;
    private String contentType;
    private long sizeBytes;
    private DocumentStatus status = DocumentStatus.PENDING;

    private DocumentMetadataBuilder() {}

    public DocumentMetadataBuilder id(String id) {
      this.id = id;
      return this;
    }

    public DocumentMetadataBuilder userId(String userId) {
      this.userId = userId;
      return this;
    }

    public DocumentMetadataBuilder projectId(String projectId) {
      this.projectId = projectId;
      return this;
    }

    public DocumentMetadataBuilder name(String name) {
      this.name = name;
      return this;
    }

    public DocumentMetadataBuilder contentType(String contentType) {
      this.contentType = contentType;
      return this;
    }

    public DocumentMetadataBuilder sizeBytes(long sizeBytes) {
      this.sizeBytes = sizeBytes;
      return this;
    }

    public DocumentMetadataBuilder status(DocumentStatus status) {
      this.status = status;
      return this;
    }

    public DocumentMetadata build() {
      return new DocumentMetadata(id, userId, projectId, name, contentType, sizeBytes, status);
    }
  }
}
