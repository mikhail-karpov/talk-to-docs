package com.mikhailkarpov.docs.ai;

import java.util.concurrent.CompletableFuture;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AiTitleGenerator {

  private static final String SYSTEM_PROMPT = """
      Generate a short, descriptive title (3-6 words, max 64 characters) for a
      conversation that begins with the user's message. Reply with ONLY the
      title text — no quotes, no trailing punctuation, no explanation.
      """;

  private static final int MAX_TITLE_LENGTH = 64;

  private final ChatClient chatClient;

  public AiTitleGenerator(ChatClient.Builder builder) {
    this.chatClient = builder.defaultSystem(SYSTEM_PROMPT).build();
  }

  @Async("applicationTaskExecutor")
  CompletableFuture<String> generate(String userMessage) {
    var raw = chatClient
        .prompt()
        .user(userMessage)
        .call()
        .content();
    return CompletableFuture.completedFuture(sanitize(raw));
  }

  String sanitize(String title) {
    if (title == null) {
      return null;
    }
    var cleaned = title.replaceAll("\\s+", " ").trim();
    if (cleaned.regionMatches(true, 0, "title:", 0, "title:".length())) {
      cleaned = cleaned.substring("title:".length()).trim();
    }
    if (cleaned.length() >= 2
        && (cleaned.charAt(0) == '"' || cleaned.charAt(0) == '\'')
        && cleaned.charAt(cleaned.length() - 1) == cleaned.charAt(0)) {
      cleaned = cleaned.substring(1, cleaned.length() - 1).trim();
    }
    if (cleaned.length() > MAX_TITLE_LENGTH) {
      cleaned = cleaned.substring(0, MAX_TITLE_LENGTH).trim();
    }
    return cleaned;
  }
}
