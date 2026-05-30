package com.mikhailkarpov.docs.ai;

import com.mikhailkarpov.docs.documents.DocumentCreatedEvent;
import com.mikhailkarpov.docs.documents.DocumentDeletedEvent;
import com.mikhailkarpov.docs.documents.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DocumentListener {

  private static final Logger log = LoggerFactory.getLogger(DocumentListener.class);
  private final DocumentIndexer documentIndexer;
  private final DocumentService documentService;

  public DocumentListener(DocumentIndexer documentIndexer, DocumentService documentService) {
    this.documentIndexer = documentIndexer;
    this.documentService = documentService;
  }

  @EventListener(DocumentCreatedEvent.class)
  void process(DocumentCreatedEvent event) {

    var document = event.document();
    var resource = event.resource();
    documentIndexer.add(document, resource)
        .thenRun(() -> documentService.markProcessed(document.getId(), document.getUserId()))
        .exceptionally(ex -> {
          log.error("Failed to process document {}", document, ex);
          documentService.markError(document.getId(), document.getUserId());
          return null;
        });
  }

  @EventListener(DocumentDeletedEvent.class)
  void process(DocumentDeletedEvent event) {

    documentIndexer.delete(event.document())
        .exceptionally(ex -> {
          log.error("Failed to delete document {}", event, ex);
          return null;
        });
  }
}
