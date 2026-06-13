package com.mikhailkarpov.docs.ai;

import java.util.concurrent.CompletableFuture;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AiAssistant {

  private static final String SYSTEM_PROMPT = """
      You are a helpful assistant that answers questions strictly using the
      provided context from the user's documents. Use only the information in
      the context to answer. If the context does not contain the answer, reply
      that you don't have that information in the user's documents. Do not
      invent facts and do not rely on outside knowledge. Do not repeat yourself.
      """;

  private final ChatClient chatClient;

  public AiAssistant(ChatClient.Builder builder, ChatMemory chatMemory, VectorStore vectorStore) {

    var memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
    var ragAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
        .searchRequest(SearchRequest.builder().build())
        .build();
    var loggerAdvisor = new SimpleLoggerAdvisor();
    this.chatClient = builder
        .defaultSystem(SYSTEM_PROMPT)
        .defaultAdvisors(memoryAdvisor, ragAdvisor, loggerAdvisor)
        .build();
  }

  @Async("applicationTaskExecutor")
  CompletableFuture<String> reply(String conversationId, String projectId, String content) {
    return CompletableFuture.completedFuture(
        chatClient
            .prompt()
            .advisors(a -> a
                .param(ChatMemory.CONVERSATION_ID, conversationId)
                .param(QuestionAnswerAdvisor.FILTER_EXPRESSION, "projectId == '" + projectId + "'"))
            .user(content)
            .call()
            .content());
  }
}
