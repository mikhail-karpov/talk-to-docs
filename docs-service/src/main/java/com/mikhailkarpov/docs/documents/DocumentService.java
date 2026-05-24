package com.mikhailkarpov.docs.documents;

import com.mikhailkarpov.docs.documents.web.DocumentStatus;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {

  private static final Logger log = LoggerFactory.getLogger(DocumentService.class);

  private final Map<String, List<DocumentMetadata>> documents = new ConcurrentHashMap<>();
  private final ApplicationEventPublisher eventPublisher;

  public DocumentService(ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  public DocumentMetadata uploadDocument(String userId, Resource resource) {
    try {
      var filename = resource.getFilename();
      var bytes = resource.getInputStream().readAllBytes();
      var stableResource = new ByteArrayResource(bytes) {
        @Override
        public String getFilename() {
          return filename;
        }
      };
      var uploadedDocument = new DocumentMetadata(
          UUID.randomUUID().toString(),
          userId,
          filename,
          bytes.length,
          DocumentStatus.UPLOADED
      );
      documents.computeIfAbsent(userId, _ -> new ArrayList<>()).add(uploadedDocument);
      eventPublisher.publishEvent(new DocumentCreatedEvent(uploadedDocument, stableResource));
      log.info("Uploaded document: {}", uploadedDocument);
      return uploadedDocument;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public DocumentMetadata findDocument(String userId, String documentId) {
    var documents = this.documents.get(userId);
    if (documents == null) {
      throw DocumentNotFoundException.of(documentId);
    }
    return documents.stream()
        .filter(d -> d.getId().equals(documentId))
        .findFirst()
        .orElseThrow(() -> DocumentNotFoundException.of(documentId));
  }

  public List<DocumentMetadata> findDocuments(String userId) {
    var documents = this.documents.get(userId);
    return documents != null ? new ArrayList<>(documents) : List.of();
  }

  public void deleteDocument(String userId, String documentId) {
    var documents = this.documents.get(userId);
    if (documents == null) {
      throw DocumentNotFoundException.of(documentId);
    }
    var document = documents.stream()
        .filter(d -> d.getId().equals(documentId))
        .findFirst()
        .orElseThrow(() -> DocumentNotFoundException.of(documentId));

    documents.remove(document);
    eventPublisher.publishEvent(new DocumentDeletedEvent(document));
  }

  public void markProcessed(String documentId, String userId) {
    var documents = this.documents.get(userId);
    if (documents == null) {
      throw DocumentNotFoundException.of(documentId);
    }

    var document = documents.stream()
        .filter(d -> d.getId().equals(documentId))
        .findFirst()
        .orElse(null);
    if (document == null) {
      throw DocumentNotFoundException.of(documentId);
    }
    document.setStatus(DocumentStatus.PROCESSED);
  }

  public void markError(String documentId, String userId) {
    var documents = this.documents.get(userId);
    if (documents == null) {
      throw DocumentNotFoundException.of(documentId);
    }

    var document = documents.stream()
        .filter(d -> d.getId().equals(documentId))
        .findFirst()
        .orElse(null);
    if (document == null) {
      throw DocumentNotFoundException.of(documentId);
    }
    document.setStatus(DocumentStatus.ERROR);
  }
}
