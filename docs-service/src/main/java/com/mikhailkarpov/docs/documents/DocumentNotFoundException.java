package com.mikhailkarpov.docs.documents;

public class DocumentNotFoundException extends RuntimeException {

  public DocumentNotFoundException(String message) {
    super(message);
  }

  public static DocumentNotFoundException of(String documentId) {
    return new DocumentNotFoundException("Document " + documentId + " not found");
  }
}
