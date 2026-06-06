package com.mikhailkarpov.docs.ai;

import com.mikhailkarpov.docs.chat.ChatService;
import com.mikhailkarpov.docs.chat.command.RenameConversationCommand;
import com.mikhailkarpov.docs.chat.event.ConversationCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ConversationListener {

  private static final Logger log = LoggerFactory.getLogger(ConversationListener.class);

  private final AiTitleGenerator titleGenerator;
  private final ChatService chatService;

  public ConversationListener(AiTitleGenerator titleGenerator, ChatService chatService) {
    this.titleGenerator = titleGenerator;
    this.chatService = chatService;
  }

  @TransactionalEventListener(ConversationCreatedEvent.class)
  void onConversationCreated(ConversationCreatedEvent event) {

    var conversation = event.conversation();
    if (!needsTitle(conversation.title())) {
      return;
    }

    titleGenerator.generate(event.firstMessage().getContent())
        .thenAccept(title -> {
          if (title != null && !title.isBlank()) {
            chatService.renameConversation(
                new RenameConversationCommand(conversation.id(), conversation.userId(), title));
          }
        })
        .exceptionally(ex -> {
          log.warn("Title generation failed for conversation {}, keeping 'Untitled'",
              conversation.id(), ex);
          return null;
        });
  }

  private boolean needsTitle(String title) {
    return title == null || title.isBlank() || title.equalsIgnoreCase("Untitled");
  }
}
