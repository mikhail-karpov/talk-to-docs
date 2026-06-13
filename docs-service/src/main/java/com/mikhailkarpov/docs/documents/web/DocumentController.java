package com.mikhailkarpov.docs.documents.web;

import com.mikhailkarpov.docs.auth.User;
import com.mikhailkarpov.docs.documents.DocumentService;
import com.mikhailkarpov.docs.documents.command.DocumentQuery;
import com.mikhailkarpov.docs.documents.command.UploadDocumentCommand;
import com.mikhailkarpov.docs.projects.ProjectId;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
      @RequestParam String projectId,
      @FileConstraint(allowedTypes = {"text/markdown", "text/plain", "application/pdf"})
      @RequestPart(required = false) MultipartFile document) {

    var command = new UploadDocumentCommand(
        new ProjectId(projectId, user.getId()), document.getResource(), document.getContentType());
    var doc = documentService.uploadDocument(command);
    return DocumentResponse.from(doc);
  }

  @GetMapping
  public Map<String, List<DocumentResponse>> getDocuments(
      @AuthenticationPrincipal User user, @RequestParam(required = false) String projectId) {

    var documents = documentService.findDocuments(new DocumentQuery(user.getId(), projectId))
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
