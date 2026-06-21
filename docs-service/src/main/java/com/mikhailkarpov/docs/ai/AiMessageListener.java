package com.mikhailkarpov.docs.ai;

import com.mikhailkarpov.docs.chat.AuthorType;
import com.mikhailkarpov.docs.chat.ChatMessage;
import com.mikhailkarpov.docs.chat.ChatService;
import com.mikhailkarpov.docs.chat.Conversation;
import com.mikhailkarpov.docs.chat.event.MessageCreatedEvent;
import com.mikhailkarpov.docs.chat.command.SendMessageCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class AiMessageListener {

  private static final Logger log = LoggerFactory.getLogger(AiMessageListener.class);

  private final AiAssistant aiAssistant;
  private final ChatService chatService;

  public AiMessageListener(AiAssistant aiAssistant, ChatService chatService) {
    this.aiAssistant = aiAssistant;
    this.chatService = chatService;
  }

  @TransactionalEventListener(value = MessageCreatedEvent.class)
  void process(MessageCreatedEvent event) {

    var message = event.message();
    if (message.getAuthorType() == AuthorType.USER) {
      reply(event.conversation(), message);
    }
  }

  private void reply(Conversation conversation, ChatMessage message) {
    var projectId = conversation.projectId().id();
    aiAssistant.reply(message.getConversationId(), projectId, message.getContent())
        .thenApply(reply -> new SendMessageCommand(
            message.getConversationId(),
            message.getUserId(),
            reply,
            AuthorType.AI
        ))
        .thenAccept(chatService::sendMessage)
        .exceptionally(ex -> {
          log.error("Failed to generate AI reply for conversation {}",
              message.getConversationId(), ex);
          return null;
        });
  }
}
