package com.mikhailkarpov.docs.chat;

import com.mikhailkarpov.docs.chat.command.ConversationQuery;
import com.mikhailkarpov.docs.chat.command.CreateConversationCommand;
import com.mikhailkarpov.docs.chat.command.DeleteConversationCommand;
import com.mikhailkarpov.docs.chat.command.RenameConversationCommand;
import com.mikhailkarpov.docs.chat.command.SendMessageCommand;
import com.mikhailkarpov.docs.chat.event.ConversationCreatedEvent;
import com.mikhailkarpov.docs.chat.event.MessageCreatedEvent;
import com.mikhailkarpov.docs.projects.ProjectNotFoundException;
import com.mikhailkarpov.docs.projects.ProjectRepository;
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
  private final ProjectRepository projectRepository;
  private final ApplicationEventPublisher eventPublisher;

  public ChatService(
      ChatRepository chatRepository,
      ProjectRepository projectRepository,
      ApplicationEventPublisher eventPublisher) {

    this.chatRepository = chatRepository;
    this.projectRepository = projectRepository;
    this.eventPublisher = eventPublisher;
  }

  @Transactional
  public ChatMessage createConversation(CreateConversationCommand command) {

    if (!projectRepository.exists(command.projectId())) {
      throw ProjectNotFoundException.of(command.projectId());
    }

    var conversation = new Conversation(
        UUID.randomUUID().toString(),
        command.projectId(),
        command.title(),
        Instant.now());

    chatRepository.addConversation(conversation);

    var message = new ChatMessage(UUID.randomUUID().toString(),
        conversation.id(),
        command.projectId().userId(),
        AuthorType.USER,
        command.content(),
        Instant.now());
    chatRepository.addMessage(message);

    eventPublisher.publishEvent(new MessageCreatedEvent(conversation, message));
    eventPublisher.publishEvent(new ConversationCreatedEvent(conversation, message));
    log.info("Created conversation: {}", conversation);
    return message;
  }

  @Transactional
  public Conversation renameConversation(RenameConversationCommand command) {
    var conversation = chatRepository.updateTitle(
            command.conversationId(), command.userId(), command.title())
        .orElseThrow(() -> ConversationNotFound.of(command.conversationId()));
    log.info("Renamed conversation {} to: {}", command.conversationId(), command.title());
    return conversation;
  }

  @Transactional
  public void deleteConversation(DeleteConversationCommand command) {
    if (!chatRepository.deleteConversation(command.conversationId(), command.userId())) {
      throw ConversationNotFound.of(command.conversationId());
    }
    log.info("Deleted conversation {}", command.conversationId());
  }

  public List<Conversation> getConversations(ConversationQuery query) {
    return chatRepository.findConversations(query);
  }

  public Conversation getConversation(String userId, String conversationId) {
    return chatRepository.findConversation(userId, conversationId)
        .orElseThrow(() -> ConversationNotFound.of(conversationId));
  }

  public List<ChatMessage> getMessages(String conversationId, String userId) {
    if (chatRepository.findConversation(userId, conversationId).isEmpty()) {
      throw ConversationNotFound.of(conversationId);
    }
    return chatRepository.findMessages(conversationId);
  }

  @Transactional
  public ChatMessage sendMessage(SendMessageCommand command) {
    var conversation = chatRepository.findConversation(command.userId(), command.conversationId());
    if (conversation.isEmpty()) {
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
    eventPublisher.publishEvent(new MessageCreatedEvent(conversation.get(), message));
    log.info("Created message: {}", message);
    return message;
  }
}
