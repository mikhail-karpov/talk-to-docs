package com.mikhailkarpov.docs.notifications;

import com.mikhailkarpov.docs.documents.event.DocumentCreatedEvent;
import com.mikhailkarpov.docs.documents.DocumentMetadata;
import com.mikhailkarpov.docs.documents.event.DocumentUpdatedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class DocumentNotificationListener {

  private final NotificationPublisher<DocumentMetadata> publisher;

  public DocumentNotificationListener(NotificationPublisher<DocumentMetadata> publisher) {
    this.publisher = publisher;
  }

  @TransactionalEventListener(DocumentCreatedEvent.class)
  void onDocumentCreated(DocumentCreatedEvent event) {
    this.publish(event.document());
  }

  @TransactionalEventListener(DocumentUpdatedEvent.class)
  void onDocumentUpdated(DocumentUpdatedEvent event) {
    this.publish(event.document());
  }

  private void publish(DocumentMetadata document) {
    var notification = new Notification<>(document.getUserId(), document);
    publisher.publish(notification);
  }
}
