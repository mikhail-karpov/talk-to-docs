package com.mikhailkarpov.docs.chat.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mikhailkarpov.docs.auth.UserService;
import com.mikhailkarpov.docs.chat.AuthorType;
import com.mikhailkarpov.docs.chat.command.CreateConversationCommand;
import com.mikhailkarpov.docs.chat.command.DeleteConversationCommand;
import com.mikhailkarpov.docs.chat.command.RenameConversationCommand;
import com.mikhailkarpov.docs.chat.command.SendMessageCommand;
import com.mikhailkarpov.docs.chat.ChatMessage;
import com.mikhailkarpov.docs.chat.ChatService;
import com.mikhailkarpov.docs.chat.Conversation;
import com.mikhailkarpov.docs.chat.ConversationNotFound;
import com.mikhailkarpov.docs.config.RestControllerTest;
import com.mikhailkarpov.docs.config.WithMockAuthenticatedUser;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@RestControllerTest(ChatController.class)
class ChatControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private ChatService chatService;

  private static final String USER_ID = "101";

  private static final ChatMessage MOCK_MESSAGE = new ChatMessage(
      "msg-id", "conv-id", USER_ID, AuthorType.USER, "hi", Instant.parse("2023-01-01T10:20:30Z"));

  private static final Conversation MOCK_CONVERSATION =
      new Conversation("conv-id", USER_ID, "Test Chat", Instant.parse("2023-01-01T10:20:30Z"));


  @Nested
  class GetConversationsTest {

    @Test
    @WithMockAuthenticatedUser
    void returnsConversations_whenAuthenticated() throws Exception {
      when(chatService.getConversations(USER_ID))
          .thenReturn(List.of(MOCK_CONVERSATION));

      mockMvc.perform(get("/api/v1/chat"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.items[0].id").value("conv-id"))
          .andExpect(jsonPath("$.items[0].createdAt").value("2023-01-01T10:20:30Z"));
    }

    @Test
    void returns401_whenUnauthenticated() throws Exception {
      mockMvc.perform(get("/api/v1/chat"))
          .andExpect(status().isUnauthorized());
    }
  }


  @Nested
  class CreateConversationTest {

    @Test
    @WithMockAuthenticatedUser
    void returnsConversationId_whenAuthenticated() throws Exception {
      when(chatService.createConversation(any(CreateConversationCommand.class)))
          .thenReturn(MOCK_MESSAGE);

      mockMvc.perform(post("/api/v1/chat")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {"content": "hello world"}
                  """))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value("msg-id"));
    }

    @Test
    @WithMockAuthenticatedUser
    void returns400_whenContentIsNull() throws Exception {
      mockMvc.perform(post("/api/v1/chat")
              .contentType(MediaType.APPLICATION_JSON)
              .content("{}"))
          .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockAuthenticatedUser
    void returns400_whenContentIsTooShort() throws Exception {
      mockMvc.perform(post("/api/v1/chat")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {"content": "a"}
                  """))
          .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockAuthenticatedUser
    void returns400_whenContentIsTooLong() throws Exception {
      mockMvc.perform(post("/api/v1/chat")
              .contentType(MediaType.APPLICATION_JSON)
              .content("{\"content\": \"" + "a".repeat(257) + "\"}"))
          .andExpect(status().isBadRequest());
    }

    @Test
    void returns401_whenUnauthenticated() throws Exception {
      mockMvc.perform(post("/api/v1/chat"))
          .andExpect(status().isUnauthorized());
    }
  }


  @Nested
  class RenameConversationTest {

    private static final RenameConversationCommand COMMAND =
        new RenameConversationCommand("conv-id", USER_ID, "New name");

    @Test
    @WithMockAuthenticatedUser
    void returns200WithUpdatedConversation_whenValid() throws Exception {
      when(chatService.renameConversation(COMMAND))
          .thenReturn(MOCK_CONVERSATION);

      mockMvc.perform(put("/api/v1/chat/conv-id")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {"title": "New name"}
                  """))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value("conv-id"));
    }

    @Test
    @WithMockAuthenticatedUser
    void returns400_whenTitleIsBlank() throws Exception {
      mockMvc.perform(put("/api/v1/chat/conv-id")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {"title": "   "}
                  """))
          .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockAuthenticatedUser
    void returns400_whenTitleIsTooLong() throws Exception {
      mockMvc.perform(put("/api/v1/chat/conv-id")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {"title": "%s"}
              """.formatted("a".repeat(65))))
          .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockAuthenticatedUser
    void returns404_whenConversationNotFound() throws Exception {
      when(chatService.renameConversation(COMMAND))
          .thenThrow(ConversationNotFound.of("conv-id"));

      mockMvc.perform(put("/api/v1/chat/conv-id")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {"title": "New name"}
                  """))
          .andExpect(status().isNotFound());
    }

    @Test
    void returns401_whenUnauthenticated() throws Exception {
      mockMvc.perform(put("/api/v1/chat/conv-id")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {"title": "New name"}
                  """))
          .andExpect(status().isUnauthorized());
    }
  }


  @Nested
  class DeleteConversationTest {

    private static final DeleteConversationCommand COMMAND =
        new DeleteConversationCommand("conv-id", USER_ID);

    @Test
    @WithMockAuthenticatedUser
    void returns204_whenDeleted() throws Exception {
      mockMvc.perform(delete("/api/v1/chat/conv-id"))
          .andExpect(status().isNoContent());
    }

    @Test
    @WithMockAuthenticatedUser
    void returns404_whenConversationNotFound() throws Exception {
      doThrow(ConversationNotFound.of("conv-id"))
          .when(chatService).deleteConversation(COMMAND);

      mockMvc.perform(delete("/api/v1/chat/conv-id"))
          .andExpect(status().isNotFound());
    }

    @Test
    void returns401_whenUnauthenticated() throws Exception {
      mockMvc.perform(delete("/api/v1/chat/conv-id"))
          .andExpect(status().isUnauthorized());
    }
  }


  @Nested
  class SendMessageTest {

    @Test
    @WithMockAuthenticatedUser
    void returns200_whenValidRequest() throws Exception {
      when(chatService.sendMessage(any(SendMessageCommand.class)))
          .thenReturn(MOCK_MESSAGE);

      mockMvc.perform(post("/api/v1/chat/conv-id/messages")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {"content": "hello world"}
                  """))
          .andExpect(status().isOk());
    }

    @Test
    @WithMockAuthenticatedUser
    void returns400_whenContentIsNull() throws Exception {
      mockMvc.perform(post("/api/v1/chat/conv-id/messages")
              .contentType(MediaType.APPLICATION_JSON)
              .content("{}"))
          .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockAuthenticatedUser
    void returns400_whenContentIsTooShort() throws Exception {
      mockMvc.perform(post("/api/v1/chat/conv-id/messages")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {"content": "a"}
                  """))
          .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockAuthenticatedUser
    void returns400_whenContentIsTooLong() throws Exception {
      mockMvc.perform(post("/api/v1/chat/conv-id/messages")
              .contentType(MediaType.APPLICATION_JSON)
              .content("{\"content\": \"" + "a".repeat(257) + "\"}"))
          .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockAuthenticatedUser
    void returns404_whenConversationNotFound() throws Exception {
      doThrow(ConversationNotFound.of("conv-id"))
          .when(chatService).sendMessage(any(SendMessageCommand.class));

      mockMvc.perform(post("/api/v1/chat/conv-id/messages")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {"content": "hello world"}
                  """))
          .andExpect(status().isNotFound());
    }

    @Test
    void returns401_whenUnauthenticated() throws Exception {
      mockMvc.perform(post("/api/v1/chat/conv-id/messages")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {"content": "hello world"}
                  """))
          .andExpect(status().isUnauthorized());
    }
  }


  @Nested
  class GetMessagesTest {

    @Test
    @WithMockAuthenticatedUser
    void returnsMessages_whenAuthenticated() throws Exception {
      when(chatService.getMessages("conv-id", USER_ID))
          .thenReturn(List.of(MOCK_MESSAGE));

      mockMvc.perform(get("/api/v1/chat/conv-id/messages"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.items[0].id").value("msg-id"))
          .andExpect(jsonPath("$.items[0].authorType").value("USER"))
          .andExpect(jsonPath("$.items[0].content").value("hi"))
          .andExpect(jsonPath("$.items[0].createdAt").value("2023-01-01T10:20:30Z"));
    }

    @Test
    @WithMockAuthenticatedUser
    void returns404_whenConversationNotFound() throws Exception {
      when(chatService.getMessages("conv-id", USER_ID))
          .thenThrow(ConversationNotFound.of("conv-id"));

      mockMvc.perform(get("/api/v1/chat/conv-id/messages"))
          .andExpect(status().isNotFound());
    }

    @Test
    void returns401_whenUnauthenticated() throws Exception {
      mockMvc.perform(get("/api/v1/chat/conv-id/messages"))
          .andExpect(status().isUnauthorized());
    }
  }
}
