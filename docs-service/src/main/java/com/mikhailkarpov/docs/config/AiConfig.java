package com.mikhailkarpov.docs.config;

import com.mikhailkarpov.docs.config.properties.TextSplitterProperties;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TextSplitterProperties.class)
public class AiConfig {

  @Bean
  ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
    return MessageWindowChatMemory.builder()
        .chatMemoryRepository(chatMemoryRepository)
        .maxMessages(10)
        .build();
  }

  @Bean
  TokenTextSplitter textSplitter(TextSplitterProperties props) {
    return TokenTextSplitter.builder()
        .withChunkSize(props.getChunkSize())
        .withMinChunkSizeChars(props.getMinChunkSizeChars())
        .withMinChunkLengthToEmbed(props.getMinChunkLengthToEmbed())
        .withMaxNumChunks(props.getMaxNumChunks())
        .withKeepSeparator(props.isKeepSeparator())
        .build();
  }
}
