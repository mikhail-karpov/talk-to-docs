package com.mikhailkarpov.docs.ai.reader;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class SourceReaderTest {

  @Test
  void markdownReader_parsesContent() {
    var resource = new ClassPathResource("documents/test.md");
    var docs = new MarkdownSourceReader().read(resource);

    assertThat(docs).isNotEmpty();
    assertThat(docs).anyMatch(d -> d.getText() != null && d.getText().contains("Some body text"));
  }

  @Test
  void textReader_parsesContent() {
    var resource = new ClassPathResource("documents/test.txt");
    var docs = new TextSourceReader().read(resource);

    assertThat(docs).isNotEmpty();
    assertThat(docs.getFirst().getText()).contains("hello world");
  }

  @Test
  void pdfReader_parsesContent() {
    var resource = new ClassPathResource("documents/test.pdf");
    var docs = new PdfSourceReader().read(resource);

    assertThat(docs).isNotEmpty();
    assertThat(docs).anyMatch(d -> d.getText() != null && d.getText().contains("PDF"));
  }

}
