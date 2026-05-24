package com.mikhailkarpov.docs.documents.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mikhailkarpov.docs.auth.UserService;
import com.mikhailkarpov.docs.config.RestControllerTest;
import com.mikhailkarpov.docs.config.WithMockAuthenticatedUser;
import com.mikhailkarpov.docs.documents.DocumentMetadata;
import com.mikhailkarpov.docs.documents.DocumentService;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@RestControllerTest(DocumentController.class)
class DocumentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private DocumentService documentService;

  private static final String USER_ID = "101";

  private static final DocumentMetadata TEST_DOCUMENT =
      new DocumentMetadata("doc-1", USER_ID, "test.md", 1024L, DocumentStatus.UPLOADED);


  @Nested
  class UploadDocumentTest {

    @Test
    @WithMockAuthenticatedUser
    void uploadDocument_withMarkdownFile_returnsDocumentResponse() throws Exception {
      when(documentService.uploadDocument(eq(USER_ID), any()))
          .thenReturn(TEST_DOCUMENT);

      var file = new MockMultipartFile(
          "document", "test.md", "text/markdown", "# Hello".getBytes());

      mockMvc.perform(multipart("/api/v1/documents").file(file))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value("doc-1"))
          .andExpect(jsonPath("$.name").value("test.md"))
          .andExpect(jsonPath("$.sizeBytes").value(1024))
          .andExpect(jsonPath("$.status").value("UPLOADED"));
    }

    @Test
    @WithMockAuthenticatedUser
    void uploadDocument_withInvalidContentType_returns400() throws Exception {
      var file = new MockMultipartFile(
          "document", "test.pdf", MediaType.APPLICATION_PDF_VALUE, "content".getBytes());

      mockMvc.perform(multipart("/api/v1/documents").file(file))
          .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockAuthenticatedUser
    void uploadDocument_withEmptyFile_returns400() throws Exception {
      var file = new MockMultipartFile(
          "document", "test.md", "text/markdown", new byte[0]);

      mockMvc.perform(multipart("/api/v1/documents").file(file))
          .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockAuthenticatedUser
    void uploadDocument_withoutFile_returns400() throws Exception {
      mockMvc.perform(multipart("/api/v1/documents"))
          .andExpect(status().isBadRequest());
    }

    @Test
    void uploadDocument_unauthenticated_returns401() throws Exception {
      mockMvc.perform(multipart("/api/v1/documents"))
          .andExpect(status().isUnauthorized());
    }
  }


  @Nested
  class GetDocumentsTest {

    @Test
    @WithMockAuthenticatedUser
    void getDocuments_returnsItemsWrapper() throws Exception {
      when(documentService.findDocuments(USER_ID))
          .thenReturn(List.of(TEST_DOCUMENT));

      mockMvc.perform(get("/api/v1/documents"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.items[0].id").value("doc-1"))
          .andExpect(jsonPath("$.items[0].name").value("test.md"))
          .andExpect(jsonPath("$.items[0].sizeBytes").value(1024))
          .andExpect(jsonPath("$.items[0].status").value("UPLOADED"));
    }

    @Test
    void getDocuments_unauthenticated_returns401() throws Exception {
      mockMvc.perform(get("/api/v1/documents"))
          .andExpect(status().isUnauthorized());
    }
  }


  @Nested
  class GetDocumentTest {

    @Test
    @WithMockAuthenticatedUser
    void getDocument_whenFound_returnsDocumentResponse() throws Exception {
      when(documentService.findDocument(USER_ID, "doc-1"))
          .thenReturn(TEST_DOCUMENT);

      mockMvc.perform(get("/api/v1/documents/{documentId}", "doc-1"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value("doc-1"))
          .andExpect(jsonPath("$.name").value("test.md"))
          .andExpect(jsonPath("$.sizeBytes").value(1024))
          .andExpect(jsonPath("$.status").value("UPLOADED"));
    }

    @Test
    void getDocument_unauthenticated_returns401() throws Exception {
      mockMvc.perform(get("/api/v1/documents/{documentId}", "doc-1"))
          .andExpect(status().isUnauthorized());
    }
  }


  @Nested
  class DeleteDocumentTest {

    @Test
    @WithMockAuthenticatedUser
    void deleteDocument_returnsNoContent() throws Exception {
      mockMvc.perform(delete("/api/v1/documents/{documentId}", "doc-1"))
          .andExpect(status().isNoContent());
    }

    @Test
    void deleteDocument_unauthenticated_returns401() throws Exception {
      mockMvc.perform(delete("/api/v1/documents/{documentId}", "doc-1"))
          .andExpect(status().isUnauthorized());
    }
  }
}
