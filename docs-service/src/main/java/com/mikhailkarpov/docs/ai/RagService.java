package com.mikhailkarpov.docs.ai;

import com.mikhailkarpov.docs.documents.DocumentMetadata;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class RagService {

  private final VectorStore vectorStore;

  public RagService(VectorStore vectorStore) {
    this.vectorStore = vectorStore;
  }

  @Async("applicationTaskExecutor")
  public CompletableFuture<Void> add(DocumentMetadata document, Resource resource) {
    return CompletableFuture.completedFuture(readDocuments(document, resource))
        .thenAccept(vectorStore::add);
  }

  @Async("applicationTaskExecutor")
  public CompletableFuture<Void> delete(DocumentMetadata document) {
    vectorStore.delete("documentId == '" + document.getId() + "'");
    return CompletableFuture.completedFuture(null);
  }

  private List<Document> readDocuments(DocumentMetadata document, Resource resource) {
    var config = MarkdownDocumentReaderConfig.builder()
        .withAdditionalMetadata("documentId", document.getId())
        .withAdditionalMetadata("userId", document.getUserId())
        .build();

    return TokenTextSplitter.builder().build()
        .apply(new MarkdownDocumentReader(resource, config).get());
  }
}
