package com.mikhailkarpov.docs.chat.command;

import com.mikhailkarpov.docs.chat.AuthorType;

public record SendMessageCommand(
    String conversationId, String userId, String content, AuthorType authorType) {}
