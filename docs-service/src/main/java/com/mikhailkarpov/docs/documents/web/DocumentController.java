package com.mikhailkarpov.docs.documents.web;

import com.mikhailkarpov.docs.auth.User;
import com.mikhailkarpov.docs.documents.DocumentService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

  private final DocumentService documentService;

  public DocumentController(DocumentService documentService) {
    this.documentService = documentService;
  }


  @PostMapping
  public DocumentResponse uploadDocument(
      @AuthenticationPrincipal User user,
      @FileConstraint(allowedTypes = {"text/markdown", "text/plain", "application/pdf"})
      @RequestPart(required = false) MultipartFile document) {

    var doc = documentService.uploadDocument(user.getId(), document.getResource(), document.getContentType());
    return DocumentResponse.from(doc);
  }

  @GetMapping
  public Map<String, List<DocumentResponse>> getDocuments(
      @AuthenticationPrincipal User user) {

    var documents = documentService.findDocuments(user.getId())
        .stream()
        .map(DocumentResponse::from)
        .toList();

    return Map.of("items", documents);
  }

  @GetMapping("/{documentId}")
  public DocumentResponse getDocument(
      @PathVariable String documentId,
      @AuthenticationPrincipal User user) {

    var doc = documentService.findDocument(user.getId(), documentId);
    return DocumentResponse.from(doc);
  }

  @DeleteMapping("/{documentId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteDocument(
      @PathVariable String documentId,
      @AuthenticationPrincipal User user) {

    documentService.deleteDocument(user.getId(), documentId);
  }
}
