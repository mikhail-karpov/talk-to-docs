package com.mikhailkarpov.docs.projects.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mikhailkarpov.docs.auth.UserService;
import com.mikhailkarpov.docs.config.RestControllerTest;
import com.mikhailkarpov.docs.config.WithMockAuthenticatedUser;
import com.mikhailkarpov.docs.projects.Project;
import com.mikhailkarpov.docs.projects.ProjectService;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@RestControllerTest(ProjectController.class)
class ProjectControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private ProjectService projectService;

  private static final String USER_ID = WithMockAuthenticatedUser.TEST_USER_ID;

  private static final Project TEST_PROJECT =
      new Project("proj-1", USER_ID, "My Project", "A description", Instant.parse("2025-01-01T00:00:00Z"));


  @Nested
  class GetProjects {

    @Test
    @WithMockAuthenticatedUser
    void returnsListOfProjects() throws Exception {
      when(projectService.listProjects(USER_ID)).thenReturn(List.of(TEST_PROJECT));

      mockMvc.perform(get("/api/v1/projects"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].id").value("proj-1"))
          .andExpect(jsonPath("$[0].title").value("My Project"))
          .andExpect(jsonPath("$[0].description").value("A description"));
    }

    @Test
    void unauthenticated_returns401() throws Exception {
      mockMvc.perform(get("/api/v1/projects"))
          .andExpect(status().isUnauthorized());
    }
  }


  @Nested
  class CreateProject {

    @Test
    @WithMockAuthenticatedUser
    void withValidBody_returnsProjectResponse() throws Exception {
      when(projectService.createProject(any())).thenReturn(TEST_PROJECT);

      mockMvc.perform(post("/api/v1/projects")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {"title": "My Project", "description": "A description"}
                  """))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value("proj-1"))
          .andExpect(jsonPath("$.title").value("My Project"))
          .andExpect(jsonPath("$.description").value("A description"));
    }

    @Test
    @WithMockAuthenticatedUser
    void withBlankTitle_returns400() throws Exception {
      mockMvc.perform(post("/api/v1/projects")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {"title": "   "}
                  """))
          .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockAuthenticatedUser
    void withTitleTooLong_returns400() throws Exception {
      mockMvc.perform(post("/api/v1/projects")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {"title": "%s"}
                  """.formatted("x".repeat(65))))
          .andExpect(status().isBadRequest());
    }

    @Test
    void unauthenticated_returns401() throws Exception {
      mockMvc.perform(post("/api/v1/projects")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {"title": "My Project"}
                  """))
          .andExpect(status().isUnauthorized());
    }
  }


  @Nested
  class GetProject {

    @Test
    @WithMockAuthenticatedUser
    void whenFound_returnsProjectResponse() throws Exception {
      when(projectService.getProject(any())).thenReturn(TEST_PROJECT);

      mockMvc.perform(get("/api/v1/projects/{projectId}", "proj-1"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value("proj-1"))
          .andExpect(jsonPath("$.title").value("My Project"));
    }

    @Test
    void unauthenticated_returns401() throws Exception {
      mockMvc.perform(get("/api/v1/projects/{projectId}", "proj-1"))
          .andExpect(status().isUnauthorized());
    }
  }


  @Nested
  class EditProject {

    @Test
    @WithMockAuthenticatedUser
    void withValidBody_returnsUpdatedProject() throws Exception {
      var updated = new Project("proj-1", USER_ID, "Updated Title", "Updated Desc",
          Instant.parse("2025-06-01T00:00:00Z"));
      when(projectService.editProject(any())).thenReturn(updated);

      mockMvc.perform(put("/api/v1/projects/{projectId}", "proj-1")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {"title": "Updated Title", "description": "Updated Desc"}
                  """))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value("proj-1"))
          .andExpect(jsonPath("$.title").value("Updated Title"))
          .andExpect(jsonPath("$.description").value("Updated Desc"));
    }

    @Test
    @WithMockAuthenticatedUser
    void withTitleTooLong_returns400() throws Exception {
      mockMvc.perform(put("/api/v1/projects/{projectId}", "proj-1")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {"title": "%s"}
                  """.formatted("x".repeat(65))))
          .andExpect(status().isBadRequest());
    }

    @Test
    void unauthenticated_returns401() throws Exception {
      mockMvc.perform(put("/api/v1/projects/{projectId}", "proj-1")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {"title": "New Title"}
                  """))
          .andExpect(status().isUnauthorized());
    }
  }


  @Nested
  class DeleteProject {

    @Test
    @WithMockAuthenticatedUser
    void returnsNoContent() throws Exception {
      mockMvc.perform(delete("/api/v1/projects/{projectId}", "proj-1"))
          .andExpect(status().isNoContent());
    }

    @Test
    void unauthenticated_returns401() throws Exception {
      mockMvc.perform(delete("/api/v1/projects/{projectId}", "proj-1"))
          .andExpect(status().isUnauthorized());
    }
  }
}
