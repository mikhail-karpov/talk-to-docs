package com.mikhailkarpov.docs.chat.command;

public record DeleteConversationCommand(String conversationId, String userId) {}
