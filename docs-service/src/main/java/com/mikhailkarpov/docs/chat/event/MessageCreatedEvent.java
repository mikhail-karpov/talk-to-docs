package com.mikhailkarpov.docs.chat.event;

import com.mikhailkarpov.docs.chat.ChatMessage;
import com.mikhailkarpov.docs.chat.Conversation;

public record MessageCreatedEvent(Conversation conversation, ChatMessage message) {}
