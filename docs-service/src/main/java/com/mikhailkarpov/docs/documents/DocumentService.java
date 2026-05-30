package com.mikhailkarpov.docs.documents;

import com.mikhailkarpov.docs.documents.web.DocumentStatus;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DocumentService {

  private static final Logger log = LoggerFactory.getLogger(DocumentService.class);

  private final DocumentRepository documentRepository;
  private final ApplicationEventPublisher eventPublisher;

  public DocumentService(
      DocumentRepository documentRepository, ApplicationEventPublisher eventPublisher) {

    this.documentRepository = documentRepository;
    this.eventPublisher = eventPublisher;
  }

  public DocumentMetadata uploadDocument(String userId, Resource resource, String contentType) {

    try (var inputStream = resource.getInputStream()) {
      var filename = resource.getFilename();
      var bytes = inputStream.readAllBytes();
      var stableResource = new ByteArrayResource(bytes) {
        @Override
        public String getFilename() {
          return filename;
        }
      };
      var uploadedDocument = DocumentMetadata.builder()
          .userId(userId)
          .name(filename)
          .contentType(contentType)
          .sizeBytes(bytes.length)
          .status(DocumentStatus.UPLOADED)
          .build();

      documentRepository.addDocument(uploadedDocument);
      eventPublisher.publishEvent(new DocumentCreatedEvent(uploadedDocument, stableResource));
      log.info("Uploaded document: {}", uploadedDocument);
      return uploadedDocument;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public DocumentMetadata findDocument(String userId, String documentId) {
    return this.getDocumentOrThrow(documentId, userId);
  }

  public List<DocumentMetadata> findDocuments(String userId) {
    return documentRepository.findDocuments(userId);
  }

  public void deleteDocument(String userId, String documentId) {
    var document = documentRepository.deleteDocument(userId, documentId)
        .orElseThrow(() -> DocumentNotFoundException.of(documentId));
    eventPublisher.publishEvent(new DocumentDeletedEvent(document));
    log.info("Deleted document: {}", document);
  }

  @Transactional
  public void markProcessed(String userId, String documentId) {
    var document = this.getDocumentOrThrow(documentId, userId);
    document.setStatus(DocumentStatus.PROCESSED);
    documentRepository.updateDocument(document);
    log.info("Updated document: {}", document);
  }

  @Transactional
  public void markError(String userId, String documentId) {
    var document = this.getDocumentOrThrow(documentId, userId);
    document.setStatus(DocumentStatus.ERROR);
    documentRepository.updateDocument(document);
    log.info("Updated document: {}", document);
  }

  private DocumentMetadata getDocumentOrThrow(String documentId, String userId) {
    return documentRepository.findDocument(userId, documentId)
        .orElseThrow(() -> DocumentNotFoundException.of(documentId));
  }
}
