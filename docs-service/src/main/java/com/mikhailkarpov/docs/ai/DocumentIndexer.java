package com.mikhailkarpov.docs.ai;

import com.mikhailkarpov.docs.ai.reader.DocumentSourceReaderRegistry;
import com.mikhailkarpov.docs.documents.DocumentMetadata;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class DocumentIndexer {

  private final VectorStore vectorStore;
  private final DocumentSourceReaderRegistry readerRegistry;
  private final TextSplitter textSplitter;

  public DocumentIndexer(VectorStore vectorStore,
                         DocumentSourceReaderRegistry readerRegistry,
                         TextSplitter textSplitter) {
    this.vectorStore = vectorStore;
    this.readerRegistry = readerRegistry;
    this.textSplitter = textSplitter;
  }

  @Async("applicationTaskExecutor")
  CompletableFuture<Void> add(DocumentMetadata document, Resource resource) {
    return CompletableFuture.completedFuture(readDocuments(document, resource))
        .thenAccept(vectorStore::add);
  }

  @Async("applicationTaskExecutor")
  CompletableFuture<Void> delete(DocumentMetadata document) {
    vectorStore.delete("documentId == '" + document.getId() + "'");
    return CompletableFuture.completedFuture(null);
  }

  @Async("applicationTaskExecutor")
  CompletableFuture<Void> deleteByProjectId(String projectId) {
    vectorStore.delete("projectId == '" + projectId + "'");
    return CompletableFuture.completedFuture(null);
  }

  private List<Document> readDocuments(DocumentMetadata document, Resource resource) {
    var reader = readerRegistry.get(document.getContentType());
    var docs = reader.read(resource);
    docs.forEach(d -> {
      d.getMetadata().put("documentId", document.getId());
      d.getMetadata().put("projectId", document.getProjectId());
    });
    return textSplitter.apply(docs);
  }
}
