package com.mikhailkarpov.docs.chat;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ConversationNotFound extends RuntimeException {

  private ConversationNotFound(String message) {
    super(message);
  }

  public static ConversationNotFound of(String id) {
    return new ConversationNotFound("Conversation with id " + id + " not found");
  }
}
