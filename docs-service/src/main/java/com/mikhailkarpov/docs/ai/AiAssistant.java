package com.mikhailkarpov.docs.ai;

import java.util.concurrent.CompletableFuture;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AiAssistant {

  private final ChatClient chatClient;

  public AiAssistant(ChatClient.Builder builder, ChatMemory chatMemory) {

    var memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
    this.chatClient = builder.defaultAdvisors(memoryAdvisor, new SimpleLoggerAdvisor()).build();
  }

  @Async("applicationTaskExecutor")
  CompletableFuture<String> reply(String conversationId, String content) {
    return CompletableFuture.completedFuture(
        chatClient
            .prompt()
            .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
            .user(content)
            .call()
            .content());
  }
}
