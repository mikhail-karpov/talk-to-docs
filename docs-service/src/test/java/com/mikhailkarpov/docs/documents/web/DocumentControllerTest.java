package com.mikhailkarpov.docs.documents.web;

import static org.mockito.ArgumentMatchers.any;
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
import com.mikhailkarpov.docs.documents.command.DocumentQuery;
import com.mikhailkarpov.docs.documents.command.UploadDocumentCommand;
import com.mikhailkarpov.docs.projects.ProjectId;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
  private static final String PROJECT_ID = "proj-1";

  private static final DocumentMetadata TEST_DOCUMENT =
      DocumentMetadata.builder().id("doc-1").userId(USER_ID).projectId(PROJECT_ID).name("test.md")
          .contentType("text/markdown").sizeBytes(1024L).status(DocumentStatus.UPLOADED)
          .build();


  @Nested
  class UploadDocumentTest {

    @ParameterizedTest
    @MethodSource("uploadDocument_ok")
    @WithMockAuthenticatedUser
    void uploadDocument_ok(MockMultipartFile file, UploadDocumentCommand expectedCommand) throws Exception {

      when(documentService.uploadDocument(expectedCommand))
          .thenReturn(TEST_DOCUMENT);

      mockMvc.perform(multipart("/api/v1/documents").file(file).param("projectId", PROJECT_ID))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value("doc-1"))
          .andExpect(jsonPath("$.projectId").value(PROJECT_ID))
          .andExpect(jsonPath("$.name").value("test.md"))
          .andExpect(jsonPath("$.sizeBytes").value(1024))
          .andExpect(jsonPath("$.status").value("UPLOADED"));
    }

    static Stream<Arguments> uploadDocument_ok() {

      var projectId = new ProjectId(PROJECT_ID, USER_ID);
      return Stream.of(
          new MockMultipartFile("document", "test.md", "text/markdown", "# Hello".getBytes()),
          new MockMultipartFile("document", "test.txt", "text/plain", "hello".getBytes()),
          new MockMultipartFile("document", "test.pdf", "application/pdf", "content".getBytes())
      ).map(file -> {
        var command = new UploadDocumentCommand(projectId, file.getResource(), file.getContentType());
        return Arguments.of(file, command);
      });
    }

    @Test
    @WithMockAuthenticatedUser
    void uploadDocument_withInvalidContentType_returns400() throws Exception {
      var file = new MockMultipartFile(
          "document", "test.png", MediaType.IMAGE_PNG_VALUE, "content".getBytes());

      mockMvc.perform(multipart("/api/v1/documents").file(file).param("projectId", PROJECT_ID))
          .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockAuthenticatedUser
    void uploadDocument_withEmptyFile_returns400() throws Exception {
      var file = new MockMultipartFile(
          "document", "test.md", "text/markdown", new byte[0]);

      mockMvc.perform(multipart("/api/v1/documents").file(file).param("projectId", PROJECT_ID))
          .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockAuthenticatedUser
    void uploadDocument_withoutFile_returns400() throws Exception {
      mockMvc.perform(multipart("/api/v1/documents").param("projectId", PROJECT_ID))
          .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockAuthenticatedUser
    void uploadDocument_withoutProjectId_returns400() throws Exception {
      var file = new MockMultipartFile(
          "document", "test.md", "text/markdown", "# Hello".getBytes());

      mockMvc.perform(multipart("/api/v1/documents").file(file))
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
      when(documentService.findDocuments(any(DocumentQuery.class)))
          .thenReturn(List.of(TEST_DOCUMENT));

      mockMvc.perform(get("/api/v1/documents"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.items[0].id").value("doc-1"))
          .andExpect(jsonPath("$.items[0].projectId").value(PROJECT_ID))
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
