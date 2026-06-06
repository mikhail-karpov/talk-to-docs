package com.mikhailkarpov.docs.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;

class AiTitleGeneratorTest {

  private AiTitleGenerator titleGenerator;

  @BeforeEach
  void setUp() {
    var builder = mock(ChatClient.Builder.class, RETURNS_DEEP_STUBS);
    this.titleGenerator = new AiTitleGenerator(builder);
  }

  @Test
  void stripsSurroundingDoubleQuotes() {
    assertThat(titleGenerator.sanitize("\"Reset router\"")).isEqualTo("Reset router");
  }

  @Test
  void stripsSurroundingSingleQuotes() {
    assertThat(titleGenerator.sanitize("'Reset router'")).isEqualTo("Reset router");
  }

  @Test
  void stripsTitlePrefixCaseInsensitively() {
    assertThat(titleGenerator.sanitize("Title: Reset router")).isEqualTo("Reset router");
  }

  @Test
  void collapsesWhitespaceAndNewlines() {
    assertThat(titleGenerator.sanitize("  Reset\n  router  ")).isEqualTo("Reset router");
  }

  @Test
  void capsLengthAt64Characters() {
    assertThat(titleGenerator.sanitize("a".repeat(100))).hasSize(64);
  }

  @Test
  void returnsNullWhenInputNull() {
    assertThat(titleGenerator.sanitize(null)).isNull();
  }
}
