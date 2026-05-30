package com.mikhailkarpov.docs.documents;

import com.mikhailkarpov.docs.TestcontainersConfig;
import com.mikhailkarpov.docs.documents.jdbc.DocumentJdbcRepository;
import com.mikhailkarpov.docs.documents.web.DocumentStatus;
import java.io.IOException;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@DataJdbcTest
@Import(TestcontainersConfig.class)
@RecordApplicationEvents
class DocumentServiceTest {

  // Seeded by db/seed/V3__insert_test_users.sql; required by the documents.user_id FK.
  private static final String USER_ID = "2686f7a3-bd4a-4938-93a7-fe8e9360eb28";
  private static final String OTHER_USER_ID = UUID.randomUUID().toString();

  @Autowired
  private JdbcClient jdbcClient;

  @Autowired
  private ApplicationEventPublisher eventPublisher;

  @Autowired
  private ApplicationEvents events;

  private DocumentJdbcRepository documentRepository;
  private DocumentService documentService;

  @BeforeEach
  void setUp() {
    this.events.clear();
    this.documentRepository = new DocumentJdbcRepository(jdbcClient);
    this.documentService = new DocumentService(documentRepository, eventPublisher);
  }

  private DocumentMetadata persistDocument(String userId, String name, DocumentStatus status) {
    var document = DocumentMetadata.builder()
        .userId(userId)
        .name(name)
        .contentType("text/markdown")
        .sizeBytes(1024L)
        .status(status)
        .build();
    documentRepository.addDocument(document);
    return document;
  }

  @Nested
  class UploadDocument {

    @Test
    void persistsDocumentAndPublishesCreatedEvent() {
      var resource = new ByteArrayResource("# Hello".getBytes()) {
        @Override
        public String getFilename() {
          return "test.md";
        }
      };

      var uploaded = documentService.uploadDocument(USER_ID, resource, "text/markdown");

      Assertions.assertThat(uploaded)
          .returns(USER_ID, DocumentMetadata::getUserId)
          .returns("test.md", DocumentMetadata::getName)
          .returns("text/markdown", DocumentMetadata::getContentType)
          .returns((long) "# Hello".getBytes().length, DocumentMetadata::getSizeBytes)
          .returns(DocumentStatus.UPLOADED, DocumentMetadata::getStatus);

      Assertions.assertThat(documentRepository.findDocument(USER_ID, uploaded.getId()))
          .contains(uploaded);

      Assertions.assertThat(events.stream(DocumentCreatedEvent.class))
          .hasSize(1)
          .allMatch(e -> e.document().equals(uploaded))
          .allMatch(e -> e.resource().equals(resource));
    }

    @Test
    void wrapsIOExceptionInRuntimeException() throws IOException {
      var resource = Mockito.mock(Resource.class);
      Mockito.when(resource.getInputStream()).thenThrow(new IOException("boom"));

      Assertions.assertThatThrownBy(() -> documentService.uploadDocument(USER_ID, resource, "text/markdown"))
          .isInstanceOf(RuntimeException.class)
          .hasCauseInstanceOf(IOException.class);

      Assertions.assertThat(events.stream(DocumentDeletedEvent.class))
          .isEmpty();
    }
  }

  @Nested
  class FindDocument {

    @Test
    void returnsDocumentWhenFound() {
      var document = persistDocument(USER_ID, "found.md", DocumentStatus.UPLOADED);

      var found = documentService.findDocument(USER_ID, document.getId());

      Assertions.assertThat(found)
          .isEqualTo(document);
    }

    @Test
    void throwsWhenNotFound() {
      var missingId = UUID.randomUUID().toString();

      Assertions.assertThatThrownBy(() -> documentService.findDocument(USER_ID, missingId))
          .isInstanceOf(DocumentNotFoundException.class);
    }
  }

  @Nested
  class FindDocuments {

    @Test
    void returnsDocumentsForUser() {
      var first = persistDocument(USER_ID, "first.md", DocumentStatus.UPLOADED);
      var second = persistDocument(USER_ID, "second.md", DocumentStatus.PROCESSED);

      var documents = documentService.findDocuments(USER_ID);

      Assertions.assertThat(documents)
          .containsExactlyInAnyOrder(first, second);
    }

    @Test
    void returnsEmptyListWhenUserHasNoDocuments() {
      var documents = documentService.findDocuments(OTHER_USER_ID);

      Assertions.assertThat(documents)
          .isEmpty();
    }
  }

  @Nested
  class DeleteDocument {

    @Test
    void deletesDocumentAndPublishesDeletedEvent() {
      var document = persistDocument(USER_ID, "to-delete.md", DocumentStatus.UPLOADED);

      documentService.deleteDocument(USER_ID, document.getId());

      Assertions.assertThat(documentRepository.findDocument(USER_ID, document.getId()))
          .isEmpty();

      Assertions.assertThat(events.stream(DocumentDeletedEvent.class))
          .hasSize(1)
          .map(DocumentDeletedEvent::document)
          .containsExactly(document);
    }

    @Test
    void throwsAndPublishesNothingWhenNotFound() {
      var missingId = UUID.randomUUID().toString();

      Assertions.assertThatThrownBy(() -> documentService.deleteDocument(USER_ID, missingId))
          .isInstanceOf(DocumentNotFoundException.class);

      Assertions.assertThat(events.stream(DocumentDeletedEvent.class))
          .isEmpty();
    }
  }

  @Nested
  class MarkProcessed {

    @Test
    void updatesStatusToProcessed() {
      var document = persistDocument(USER_ID, "processing.md", DocumentStatus.UPLOADED);

      documentService.markProcessed(USER_ID, document.getId());

      Assertions.assertThat(documentRepository.findDocument(USER_ID, document.getId()))
          .get()
          .returns(DocumentStatus.PROCESSED, DocumentMetadata::getStatus);
    }

    @Test
    void throwsWhenNotFound() {
      var missingId = UUID.randomUUID().toString();

      Assertions.assertThatThrownBy(() -> documentService.markProcessed(USER_ID, missingId))
          .isInstanceOf(DocumentNotFoundException.class);
    }
  }

  @Nested
  class MarkError {

    @Test
    void updatesStatusToError() {
      var document = persistDocument(USER_ID, "failing.md", DocumentStatus.UPLOADED);

      documentService.markError(USER_ID, document.getId());

      Assertions.assertThat(documentRepository.findDocument(USER_ID, document.getId()))
          .get()
          .returns(DocumentStatus.ERROR, DocumentMetadata::getStatus);
    }

    @Test
    void throwsWhenNotFound() {
      var missingId = UUID.randomUUID().toString();

      Assertions.assertThatThrownBy(() -> documentService.markError(USER_ID, missingId))
          .isInstanceOf(DocumentNotFoundException.class);
    }
  }
}
