package com.mikhailkarpov.docs.chat;

import com.mikhailkarpov.docs.chat.command.CreateConversationCommand;
import com.mikhailkarpov.docs.chat.command.SendMessageCommand;
import com.mikhailkarpov.docs.chat.event.MessageCreatedEvent;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

  private final Map<String, List<Conversation>> conversations = new ConcurrentHashMap<>();
  private final Map<String, List<ChatMessage>> messages = new ConcurrentHashMap<>();
  private final ApplicationEventPublisher eventPublisher;

  public ChatService(ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  public Conversation createConversation(CreateConversationCommand command) {

    var conversation = new Conversation(
        UUID.randomUUID().toString(),
        command.userId(),
        command.title(),
        Instant.now());

    conversations.computeIfAbsent(command.userId(), _ -> new ArrayList<>()).add(conversation);

    var message = new ChatMessage(UUID.randomUUID().toString(),
        conversation.id(),
        command.userId(),
        AuthorType.USER,
        command.content(),
        Instant.now());
    messages.computeIfAbsent(conversation.id(), _ -> new ArrayList<>()).add(message);

    eventPublisher.publishEvent(new MessageCreatedEvent(message));
    return conversation;
  }

  public List<Conversation> getConversations(String userId) {
    return conversations.getOrDefault(userId, List.of());
  }

  public List<ChatMessage> getMessages(String conversationId, String userId) {
    if (!isConversationExists(conversationId, userId)) {
      throw ConversationNotFound.of(conversationId);
    }
    return messages.getOrDefault(conversationId, List.of());
  }

  public void sendMessage(SendMessageCommand command) {
    if (!isConversationExists(command.conversationId(), command.userId())) {
      throw ConversationNotFound.of(command.conversationId());
    }

    var message = new ChatMessage(
        UUID.randomUUID().toString(),
        command.conversationId(),
        command.userId(),
        command.authorType(),
        command.content(),
        Instant.now());
    messages.get(command.conversationId()).add(message);
    eventPublisher.publishEvent(new MessageCreatedEvent(message));
  }

  private boolean isConversationExists(String conversationId, String userId) {
    var conversations = this.conversations.get(userId);
    if (conversations == null) {
      return false;
    }
    return conversations.stream().anyMatch(c -> c.id().equals(conversationId));
  }
}
