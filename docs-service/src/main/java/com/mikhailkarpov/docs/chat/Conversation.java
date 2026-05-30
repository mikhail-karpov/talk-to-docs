package com.mikhailkarpov.docs.chat;

import java.time.Instant;

public record Conversation(String id, String userId, String title, Instant createdAt) {}
