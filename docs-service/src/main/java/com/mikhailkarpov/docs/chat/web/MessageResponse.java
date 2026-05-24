package com.mikhailkarpov.docs.chat.web;

import com.mikhailkarpov.docs.chat.AuthorType;
import com.mikhailkarpov.docs.chat.ChatMessage;
import java.time.Instant;

public record MessageResponse(
    String id,
    String userId,
    AuthorType authorType,
    String content,
    Instant createdAt) {

  public static MessageResponse from(ChatMessage message) {
    return new MessageResponse(
        message.getId(),
        message.getUserId(),
        message.getAuthorType(),
        message.getContent(),
        message.getCreatedAt()
    );
  }
}
