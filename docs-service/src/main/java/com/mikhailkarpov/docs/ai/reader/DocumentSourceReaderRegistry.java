package com.mikhailkarpov.docs.ai.reader;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class DocumentSourceReaderRegistry {

  private final Map<String, DocumentSourceReader> readersByContentType;

  public DocumentSourceReaderRegistry(List<DocumentSourceReader> readers) {
    this.readersByContentType = readers.stream()
        .collect(Collectors.toUnmodifiableMap(
            DocumentSourceReader::supportedContentType,
            r -> r));
  }

  public DocumentSourceReader get(String contentType) {
    var reader = readersByContentType.get(contentType);
    if (reader == null) {
      throw new IllegalArgumentException("No DocumentSourceReader for content type: " + contentType);
    }
    return reader;
  }
}
