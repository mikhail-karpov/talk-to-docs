package com.mikhailkarpov.docs.ai;

import com.mikhailkarpov.docs.chat.AuthorType;
import com.mikhailkarpov.docs.chat.ChatMessage;
import com.mikhailkarpov.docs.chat.ChatService;
import com.mikhailkarpov.docs.chat.event.MessageCreatedEvent;
import com.mikhailkarpov.docs.chat.command.SendMessageCommand;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class AiMessageListener {

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
      reply(message);
    }
  }

  private void reply(ChatMessage message) {
    aiAssistant.reply(message.getConversationId(), message.getUserId(), message.getContent())
        .thenApply(reply -> new SendMessageCommand(
            message.getConversationId(),
            message.getUserId(),
            reply,
            AuthorType.AI
        ))
        .thenAccept(chatService::sendMessage);
  }
}
