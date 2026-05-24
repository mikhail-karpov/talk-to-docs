package com.mikhailkarpov.docs.documents.web;

import com.mikhailkarpov.docs.documents.DocumentMetadata;
import java.time.Instant;

public record DocumentResponse(
    String id,
    String name,
    long sizeBytes,
    DocumentStatus status,
    Instant updatedAt) {

  public static DocumentResponse from(DocumentMetadata document) {
    return new DocumentResponse(
        document.getId(),
        document.getName(),
        document.getSizeBytes(),
        document.getStatus(),
        document.getUpdatedAt()
    );
  }
}
