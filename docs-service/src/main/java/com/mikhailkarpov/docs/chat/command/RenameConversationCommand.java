package com.mikhailkarpov.docs.chat.command;

public record RenameConversationCommand(String conversationId, String userId, String title) {}
