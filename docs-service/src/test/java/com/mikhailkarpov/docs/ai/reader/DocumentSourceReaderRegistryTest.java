package com.mikhailkarpov.docs.ai.reader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;

class DocumentSourceReaderRegistryTest {

  private final DocumentSourceReaderRegistry registry = new DocumentSourceReaderRegistry(List.of(
      new MarkdownSourceReader(),
      new TextSourceReader(),
      new PdfSourceReader()));

  @Test
  void get_returnsReader_forMarkdown() {
    assertThat(registry.get("text/markdown")).isInstanceOf(MarkdownSourceReader.class);
  }

  @Test
  void get_returnsReader_forPlainText() {
    assertThat(registry.get("text/plain")).isInstanceOf(TextSourceReader.class);
  }

  @Test
  void get_returnsReader_forPdf() {
    assertThat(registry.get("application/pdf")).isInstanceOf(PdfSourceReader.class);
  }

  @Test
  void get_throws_forUnknownContentType() {
    assertThatThrownBy(() -> registry.get("image/png"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("image/png");
  }
}
