package com.mikhailkarpov.docs.documents;

import com.mikhailkarpov.docs.documents.command.DocumentQuery;
import java.util.List;
import java.util.Optional;

public interface DocumentRepository {

  void addDocument(DocumentMetadata document);

  Optional<DocumentMetadata> deleteDocument(String userId, String documentId);

  Optional<DocumentMetadata> findDocument(String userId, String documentId);

  List<DocumentMetadata> findDocuments(DocumentQuery query);

  void updateDocument(DocumentMetadata document);

}
