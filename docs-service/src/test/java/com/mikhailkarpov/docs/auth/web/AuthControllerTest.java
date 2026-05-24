package com.mikhailkarpov.docs.auth.web;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mikhailkarpov.docs.config.RestControllerTest;
import com.mikhailkarpov.docs.auth.User;
import com.mikhailkarpov.docs.auth.UserService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@RestControllerTest(AuthController.class)
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserService userService;

  private final User testUser = new User("test-id", "test@example.com", "{noop}password123", "John", "Doe");


  @Nested
  class LoginTest {

    @Test
    void login_withValidCredentials_returns200() throws Exception {
      when(userService.loadUserByUsername("test@example.com")).thenReturn(testUser);

      mockMvc.perform(post("/api/v1/auth/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                {"email": "test@example.com", "password": "password123"}
                """))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value("test-id"))
          .andExpect(jsonPath("$.email").value("test@example.com"))
          .andExpect(jsonPath("$.firstName").value("John"))
          .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void login_withInvalidEmailFormat_returns400() throws Exception {
      mockMvc.perform(post("/api/v1/auth/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                {"email": "not-an-email", "password": "password123"}
                """))
          .andExpect(status().isBadRequest());
    }

    @Test
    void login_withNullEmail_returns400() throws Exception {
      mockMvc.perform(post("/api/v1/auth/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                {"password": "password123"}
                """))
          .andExpect(status().isBadRequest());
    }

    @Test
    void login_withShortPassword_returns400() throws Exception {
      mockMvc.perform(post("/api/v1/auth/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                {"email": "test@example.com", "password": "abc"}
                """))
          .andExpect(status().isBadRequest());
    }

    @Test
    void login_withNullPassword_returns400() throws Exception {
      mockMvc.perform(post("/api/v1/auth/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                {"email": "test@example.com"}
                """))
          .andExpect(status().isBadRequest());
    }

    @Test
    void login_withUnknownUser_returns401() throws Exception {
      when(userService.loadUserByUsername("unknown@example.com"))
          .thenThrow(new UsernameNotFoundException("User not found"));

      mockMvc.perform(post("/api/v1/auth/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                {"email": "unknown@example.com", "password": "password123"}
                """))
          .andExpect(status().isUnauthorized());
    }
  }


  @Nested
  class CurrentUserTest {

    @Test
    void me_whenAuthenticated_returns200() throws Exception {
      var auth = UsernamePasswordAuthenticationToken.authenticated(
          testUser, null, testUser.getAuthorities());

      mockMvc.perform(get("/api/v1/auth/me").with(authentication(auth)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value("test-id"))
          .andExpect(jsonPath("$.email").value("test@example.com"))
          .andExpect(jsonPath("$.firstName").value("John"))
          .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void me_whenUnauthenticated_returns401() throws Exception {
      mockMvc.perform(get("/api/v1/auth/me"))
          .andExpect(status().isUnauthorized());
    }
  }
}
