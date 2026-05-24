package com.mikhailkarpov.docs.chat.web;

import com.mikhailkarpov.docs.chat.Conversation;
import java.time.Instant;

public record ConversationResponse(String id, String userId, String title, Instant createdAt) {

  public static ConversationResponse from(Conversation conversation) {
    return new ConversationResponse(
        conversation.id(),
        conversation.userId(),
        conversation.title(),
        conversation.createdAt()
    );
  }
}
