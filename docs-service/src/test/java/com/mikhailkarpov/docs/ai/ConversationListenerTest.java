package com.mikhailkarpov.docs.ai;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.mikhailkarpov.docs.chat.AuthorType;
import com.mikhailkarpov.docs.chat.ChatMessage;
import com.mikhailkarpov.docs.chat.ChatService;
import com.mikhailkarpov.docs.chat.Conversation;
import com.mikhailkarpov.docs.chat.command.RenameConversationCommand;
import com.mikhailkarpov.docs.chat.event.ConversationCreatedEvent;
import com.mikhailkarpov.docs.projects.ProjectId;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConversationListenerTest {

  private final String conversationId = UUID.randomUUID().toString();
  private final String userId = UUID.randomUUID().toString();
  private final ProjectId projectId = new ProjectId(UUID.randomUUID().toString(), userId);
  private final RenameConversationCommand COMMAND =
      new RenameConversationCommand(conversationId, userId, "Reset router");

  @Mock
  private AiTitleGenerator titleGenerator;

  @Mock
  private ChatService chatService;

  @InjectMocks
  private ConversationListener listener;

  private ConversationCreatedEvent eventWithTitle(String title) {
    var conversation = new Conversation(conversationId, projectId, title, Instant.now());
    var message = new ChatMessage(
        UUID.randomUUID().toString(),
        conversationId,
        userId,
        AuthorType.USER,
        "How do I reset my router?",
        Instant.now());
    return new ConversationCreatedEvent(conversation, message);
  }

  @Test
  void generatesFromFirstMessageAndUpdatesTitleWhenUntitled() {
    when(titleGenerator.generate(anyString()))
        .thenReturn(CompletableFuture.completedFuture("Reset router"));

    listener.onConversationCreated(eventWithTitle("Untitled"));

    verify(titleGenerator)
        .generate("How do I reset my router?");

    verify(chatService)
        .renameConversation(COMMAND);
  }

  @Test
  void generatesWhenTitleBlank() {
    when(titleGenerator.generate(anyString()))
        .thenReturn(CompletableFuture.completedFuture("Reset router"));

    listener.onConversationCreated(eventWithTitle("   "));

    verify(chatService)
        .renameConversation(COMMAND);
  }

  @Test
  void skipsGenerationWhenTitleAlreadyProvided() {
    listener.onConversationCreated(eventWithTitle("My custom title"));

    verify(titleGenerator, never())
        .generate(anyString());

    verifyNoInteractions(chatService);
  }

  @Test
  void doesNotUpdateWhenGeneratedTitleIsBlank() {
    when(titleGenerator.generate(anyString()))
        .thenReturn(CompletableFuture.completedFuture("   "));

    listener.onConversationCreated(eventWithTitle("Untitled"));

    verifyNoInteractions(chatService);
  }

  @Test
  void doesNotUpdateAndSwallowsErrorWhenGenerationFails() {
    when(titleGenerator.generate(anyString()))
        .thenReturn(CompletableFuture.failedFuture(new RuntimeException("ollama down")));

    listener.onConversationCreated(eventWithTitle("Untitled"));

    verifyNoInteractions(chatService);
  }
}
