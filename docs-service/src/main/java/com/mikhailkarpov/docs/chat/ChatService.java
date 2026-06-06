package com.mikhailkarpov.docs.chat;

import com.mikhailkarpov.docs.chat.command.CreateConversationCommand;
import com.mikhailkarpov.docs.chat.command.RenameConversationCommand;
import com.mikhailkarpov.docs.chat.command.SendMessageCommand;
import com.mikhailkarpov.docs.chat.event.ConversationCreatedEvent;
import com.mikhailkarpov.docs.chat.event.MessageCreatedEvent;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatService {

  private static final Logger log = LoggerFactory.getLogger(ChatService.class);

  private final ChatRepository chatRepository;
  private final ApplicationEventPublisher eventPublisher;

  public ChatService(ChatRepository chatRepository, ApplicationEventPublisher eventPublisher) {
    this.chatRepository = chatRepository;
    this.eventPublisher = eventPublisher;
  }

  @Transactional
  public ChatMessage createConversation(CreateConversationCommand command) {

    var conversation = new Conversation(
        UUID.randomUUID().toString(),
        command.userId(),
        command.title(),
        Instant.now());

    chatRepository.addConversation(conversation);

    var message = new ChatMessage(UUID.randomUUID().toString(),
        conversation.id(),
        command.userId(),
        AuthorType.USER,
        command.content(),
        Instant.now());
    chatRepository.addMessage(message);

    eventPublisher.publishEvent(new MessageCreatedEvent(message));
    eventPublisher.publishEvent(new ConversationCreatedEvent(conversation, message));
    log.info("Created conversation: {}", conversation);
    return message;
  }

  @Transactional
  public void renameConversation(RenameConversationCommand command) {
    chatRepository.updateTitle(command.conversationId(), command.userId(), command.title());
    log.info("Renamed conversation {} to: {}", command.conversationId(), command.title());
  }

  public List<Conversation> getConversations(String userId) {
    return chatRepository.findConversations(userId);
  }

  public List<ChatMessage> getMessages(String conversationId, String userId) {
    if (chatRepository.findConversation(userId, conversationId).isEmpty()) {
      throw ConversationNotFound.of(conversationId);
    }
    return chatRepository.findMessages(conversationId);
  }

  @Transactional
  public ChatMessage sendMessage(SendMessageCommand command) {
    if (chatRepository.findConversation(command.userId(), command.conversationId()).isEmpty()) {
      throw ConversationNotFound.of(command.conversationId());
    }

    var message = new ChatMessage(
        UUID.randomUUID().toString(),
        command.conversationId(),
        command.userId(),
        command.authorType(),
        command.content(),
        Instant.now());
    chatRepository.addMessage(message);
    eventPublisher.publishEvent(new MessageCreatedEvent(message));
    log.info("Created message: {}", message);
    return message;
  }
}
