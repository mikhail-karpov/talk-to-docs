package com.mikhailkarpov.docs.chat.web;

import com.mikhailkarpov.docs.chat.AuthorType;
import com.mikhailkarpov.docs.chat.ChatMessage;
import java.time.Instant;

public record MessageResponse(
    String id,
    String conversationId,
    String userId,
    AuthorType authorType,
    String content,
    Instant createdAt) {

  public static MessageResponse from(ChatMessage message) {
    return new MessageResponse(
        message.getId(),
        message.getConversationId(),
        message.getUserId(),
        message.getAuthorType(),
        message.getContent(),
        message.getCreatedAt()
    );
  }
}
