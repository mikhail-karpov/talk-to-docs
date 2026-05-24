package com.mikhailkarpov.docs.chat;

import java.time.Instant;

public class ChatMessage {

  private final String id;
  private final String conversationId;
  private final String userId;
  private final AuthorType authorType;
  private final String content;
  private final Instant createdAt;

  public ChatMessage(
      String id,
      String conversationId,
      String userId,
      AuthorType authorType,
      String content,
      Instant createdAt) {

    this.id = id;
    this.conversationId = conversationId;
    this.userId = userId;
    this.authorType = authorType;
    this.content = content;
    this.createdAt = createdAt;
  }

  public String getId() {
    return id;
  }

  public String getConversationId() {
    return conversationId;
  }

  public String getUserId() {
    return userId;
  }

  public AuthorType getAuthorType() {
    return authorType;
  }

  public String getContent() {
    return content;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  @Override
  public final boolean equals(Object o) {
    if (!(o instanceof ChatMessage that))
      return false;

    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public String toString() {
    return "ChatMessage{" +
        "id='" + id + '\'' +
        ", conversationId='" + conversationId + '\'' +
        ", userId='" + userId + '\'' +
        ", authorType=" + authorType +
        ", content='" + content + '\'' +
        ", createdAt=" + createdAt +
        '}';
  }
}
