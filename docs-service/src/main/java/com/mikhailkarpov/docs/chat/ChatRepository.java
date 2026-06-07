package com.mikhailkarpov.docs.chat;

import com.mikhailkarpov.docs.chat.command.ConversationQuery;
import java.util.List;
import java.util.Optional;

public interface ChatRepository {

  void addConversation(Conversation conversation);

  Optional<Conversation> updateTitle(String conversationId, String userId, String title);

  boolean deleteConversation(String conversationId, String userId);

  void addMessage(ChatMessage message);

  List<Conversation> findConversations(ConversationQuery query);

  Optional<Conversation> findConversation(String userId, String conversationId);

  List<ChatMessage> findMessages(String conversationId);
}
