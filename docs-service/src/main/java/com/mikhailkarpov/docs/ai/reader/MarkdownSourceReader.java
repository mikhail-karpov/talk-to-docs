package com.mikhailkarpov.docs.ai.reader;

import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class MarkdownSourceReader implements DocumentSourceReader {

  @Override
  public String supportedContentType() {
    return "text/markdown";
  }

  @Override
  public List<Document> read(Resource resource) {
    var config = MarkdownDocumentReaderConfig.builder().build();
    return new MarkdownDocumentReader(resource, config).get();
  }
}
