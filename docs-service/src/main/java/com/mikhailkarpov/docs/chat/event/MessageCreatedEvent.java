package com.mikhailkarpov.docs.chat.event;

import com.mikhailkarpov.docs.chat.ChatMessage;

public record MessageCreatedEvent(ChatMessage message) {}
