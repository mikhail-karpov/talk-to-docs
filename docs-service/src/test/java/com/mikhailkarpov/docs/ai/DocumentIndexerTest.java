package com.mikhailkarpov.docs.ai;

import static org.assertj.core.api.Assertions.assertThat;

import com.mikhailkarpov.docs.ai.reader.DocumentSourceReaderRegistry;
import com.mikhailkarpov.docs.ai.reader.MarkdownSourceReader;
import com.mikhailkarpov.docs.ai.reader.PdfSourceReader;
import com.mikhailkarpov.docs.ai.reader.TextSourceReader;
import com.mikhailkarpov.docs.documents.DocumentMetadata;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.core.io.ClassPathResource;

class DocumentIndexerTest {

  private SimpleVectorStore vectorStore;
  private DocumentIndexer documentIndexer;

  @BeforeEach
  void setUp() {
    var embeddingModel = new ConstantEmbeddingModel();
    this.vectorStore = SimpleVectorStore.builder(embeddingModel).build();

    var readerRegistry = new DocumentSourceReaderRegistry(List.of(
        new MarkdownSourceReader(),
        new TextSourceReader(),
        new PdfSourceReader()));
    var textSplitter = TokenTextSplitter.builder().build();
    this.documentIndexer = new DocumentIndexer(vectorStore, readerRegistry, textSplitter);
  }

  @Test
  void add_storesChunksEnrichedWithDocumentMetadata() {
    var document = DocumentMetadata.builder()
        .id("doc-1")
        .userId("user-1")
        .name("test.txt")
        .contentType("text/plain")
        .build();
    var resource = new ClassPathResource("documents/test.txt");

    documentIndexer.add(document, resource).join();

    var stored = search();
    assertThat(stored).isNotEmpty();
    assertThat(stored).allSatisfy(d -> assertThat(d.getMetadata())
        .containsEntry("documentId", "doc-1")
        .containsEntry("userId", "user-1"));
  }

  @Test
  void add_usesReaderForDocumentContentType() {
    var document = DocumentMetadata.builder()
        .id("doc-md")
        .userId("user-1")
        .name("test.md")
        .contentType("text/markdown")
        .build();
    var resource = new ClassPathResource("documents/test.md");

    documentIndexer.add(document, resource).join();

    assertThat(search())
        .anyMatch(d -> d.getText() != null && d.getText().contains("Some body text"));
  }

  @Test
  void delete_removesOnlyDocumentsOfGivenDocument() {
    var first = DocumentMetadata.builder()
        .id("doc-1").userId("user-1").name("test.txt").contentType("text/plain").build();
    var second = DocumentMetadata.builder()
        .id("doc-2").userId("user-1").name("test.md").contentType("text/markdown").build();

    documentIndexer.add(first, new ClassPathResource("documents/test.txt")).join();
    documentIndexer.add(second, new ClassPathResource("documents/test.md")).join();

    documentIndexer.delete(first).join();

    var remaining = search();
    assertThat(remaining).isNotEmpty();
    assertThat(remaining).allSatisfy(d ->
        assertThat(d.getMetadata()).containsEntry("documentId", "doc-2"));
  }

  private List<Document> search() {
    return vectorStore.similaritySearch(SearchRequest.builder()
        .query("anything")
        .topK(100)
        .similarityThreshold(0.0)
        .build());
  }

  private static class ConstantEmbeddingModel implements EmbeddingModel {

    private static final float[] VECTOR = {0.1f, 0.2f, 0.3f};

    @Override
    public float @NonNull [] embed(@NonNull Document document) {
      return VECTOR;
    }

    @Override
    public @NonNull EmbeddingResponse call(EmbeddingRequest request) {
      var embeddings = request.getInstructions().stream()
          .map(_ -> new Embedding(VECTOR, 0))
          .toList();
      return new EmbeddingResponse(embeddings);
    }
  }
}
