package com.mikhailkarpov.docs.ai;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.mikhailkarpov.docs.chat.AuthorType;
import com.mikhailkarpov.docs.chat.ChatMessage;
import com.mikhailkarpov.docs.chat.ChatService;
import com.mikhailkarpov.docs.chat.Conversation;
import com.mikhailkarpov.docs.chat.command.SendMessageCommand;
import com.mikhailkarpov.docs.chat.event.MessageCreatedEvent;
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
class AiMessageListenerTest {

  private final String conversationId = UUID.randomUUID().toString();
  private final String userId = UUID.randomUUID().toString();
  private final ProjectId projectId = new ProjectId(UUID.randomUUID().toString(), userId);
  private final String content = "How do I reset my router?";

  @Mock
  private AiAssistant aiAssistant;

  @Mock
  private ChatService chatService;

  @InjectMocks
  private AiMessageListener listener;

  private MessageCreatedEvent eventFrom(AuthorType authorType) {
    var conversation = new Conversation(conversationId, projectId, "Untitled", Instant.now());
    var message = new ChatMessage(
        UUID.randomUUID().toString(),
        conversationId,
        userId,
        authorType,
        content,
        Instant.now()
    );
    return new MessageCreatedEvent(conversation, message);
  }

  @Test
  void repliesToUserMessageAndPersistsAiResponse() {
    when(aiAssistant.reply(conversationId, projectId.id(), content))
        .thenReturn(CompletableFuture.completedFuture("Hold the reset button for 10 seconds."));

    listener.process(eventFrom(AuthorType.USER));

    verify(chatService).sendMessage(new SendMessageCommand(
        conversationId, userId, "Hold the reset button for 10 seconds.", AuthorType.AI));
  }

  @Test
  void ignoresNonUserMessage() {
    listener.process(eventFrom(AuthorType.AI));

    verifyNoInteractions(aiAssistant);
    verifyNoInteractions(chatService);
  }

  @Test
  void logsAndSwallowsErrorWhenReplyFails() {
    when(aiAssistant.reply(anyString(), anyString(), anyString()))
        .thenReturn(CompletableFuture.failedFuture(new RuntimeException("chat memory unavailable")));

    listener.process(eventFrom(AuthorType.USER));

    verify(chatService, never())
        .sendMessage(any());
  }
}
