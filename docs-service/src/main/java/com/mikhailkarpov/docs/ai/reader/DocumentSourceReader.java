package com.mikhailkarpov.docs.ai.reader;

import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;

public interface DocumentSourceReader {

  String supportedContentType();

  List<Document> read(Resource resource);
}
