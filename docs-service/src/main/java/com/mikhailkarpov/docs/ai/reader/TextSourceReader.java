package com.mikhailkarpov.docs.ai.reader;

import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class TextSourceReader implements DocumentSourceReader {

  @Override
  public String supportedContentType() {
    return "text/plain";
  }

  @Override
  public List<Document> read(Resource resource) {
    return new TikaDocumentReader(resource).get();
  }
}
