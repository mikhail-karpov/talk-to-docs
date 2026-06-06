package com.mikhailkarpov.docs.chat;

import java.util.List;
import java.util.Optional;

public interface ChatRepository {

  void addConversation(Conversation conversation);

  void updateTitle(String conversationId, String userId, String title);

  void addMessage(ChatMessage message);

  List<Conversation> findConversations(String userId);

  Optional<Conversation> findConversation(String userId, String conversationId);

  List<ChatMessage> findMessages(String conversationId);
}
