package com.mikhailkarpov.docs.documents.jdbc;

import com.mikhailkarpov.docs.documents.DocumentMetadata;
import com.mikhailkarpov.docs.documents.DocumentRepository;
import com.mikhailkarpov.docs.documents.web.DocumentStatus;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class DocumentJdbcRepository implements DocumentRepository {

  private static final String INSERT_DOCUMENT = """
    INSERT INTO documents
        (id, user_id, name, content_type, size_bytes, status, updated_at)
    VALUES
        (:id, :userId, :name, :contentType, :sizeBytes, :status, :updatedAt);
    """;

  private static final String SELECT_DOCUMENT_BY_ID = """
    SELECT
        id, user_id, name, content_type, size_bytes, status, updated_at
    FROM documents
    WHERE
        id = :id AND user_id = :userId;
    """;

  private static final String SELECT_DOCUMENTS_BY_USER = """
    SELECT
        id, user_id, name, content_type, size_bytes, status, updated_at
    FROM documents
    WHERE
        user_id = :userId
    ORDER BY updated_at DESC;
    """;

  private static final String UPDATE_DOCUMENT = """
    UPDATE documents
    SET
        status = :status, updated_at = :updatedAt
    WHERE
        id = :id AND user_id = :userId;
    """;

  private static final String DELETE_DOCUMENT = """
    DELETE FROM documents
    WHERE
        id = :id AND user_id = :userId
    RETURNING
        id, user_id, name, content_type, size_bytes, status, updated_at;
    """;

  private final JdbcClient jdbcClient;
  private final RowMapper<DocumentMetadata> rowMapper = new DocumentRowMapper();

  public DocumentJdbcRepository(JdbcClient jdbcClient) {
    this.jdbcClient = jdbcClient;
  }

  @Override
  public void addDocument(DocumentMetadata document) {
    jdbcClient.sql(INSERT_DOCUMENT)
        .param("id", UUID.fromString(document.getId()))
        .param("userId", UUID.fromString(document.getUserId()))
        .param("name", document.getName())
        .param("contentType", document.getContentType())
        .param("sizeBytes", document.getSizeBytes())
        .param("status", document.getStatus().name())
        .param("updatedAt", Timestamp.from(document.getUpdatedAt()))
        .update();
  }

  @Override
  public Optional<DocumentMetadata> deleteDocument(String userId, String documentId) {
    return jdbcClient.sql(DELETE_DOCUMENT)
        .param("id", UUID.fromString(documentId))
        .param("userId", UUID.fromString(userId))
        .query(rowMapper)
        .optional();
  }

  @Override
  public Optional<DocumentMetadata> findDocument(String userId, String documentId) {
    return jdbcClient.sql(SELECT_DOCUMENT_BY_ID)
        .param("id", UUID.fromString(documentId))
        .param("userId", UUID.fromString(userId))
        .query(rowMapper)
        .optional();
  }

  @Override
  public List<DocumentMetadata> findDocuments(String userId) {
    return jdbcClient.sql(SELECT_DOCUMENTS_BY_USER)
        .param("userId", UUID.fromString(userId))
        .query(rowMapper)
        .list();
  }

  @Override
  public void updateDocument(DocumentMetadata document) {
    jdbcClient
        .sql(UPDATE_DOCUMENT)
        .param("id", UUID.fromString(document.getId()))
        .param("userId", UUID.fromString(document.getUserId()))
        .param("status", document.getStatus().name())
        .param("updatedAt", Timestamp.from(document.getUpdatedAt()))
        .update();
  }

  private static class DocumentRowMapper implements RowMapper<DocumentMetadata> {

    @Override
    public DocumentMetadata mapRow(ResultSet rs, int rowNum) throws SQLException {
      return DocumentMetadata.builder()
          .id(rs.getObject("id", UUID.class).toString())
          .userId(rs.getObject("user_id", UUID.class).toString())
          .name(rs.getString("name"))
          .contentType(rs.getString("content_type"))
          .sizeBytes(rs.getLong("size_bytes"))
          .status(DocumentStatus.valueOf(rs.getString("status")))
          .build();
    }
  }
}
