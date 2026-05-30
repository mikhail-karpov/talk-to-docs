package com.mikhailkarpov.docs.chat.jdbc;

import com.mikhailkarpov.docs.chat.AuthorType;
import com.mikhailkarpov.docs.chat.ChatMessage;
import com.mikhailkarpov.docs.chat.ChatRepository;
import com.mikhailkarpov.docs.chat.Conversation;
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
public class ChatJdbcRepository implements ChatRepository {

  private static final String INSERT_CONVERSATION = """
    INSERT INTO conversations (id, user_id, title, created_at)
    VALUES (:id, :userId, :title, :createdAt);
    """;

  private static final String SELECT_CONVERSATIONS_BY_USER = """
    SELECT id, user_id, title, created_at
    FROM conversations
    WHERE user_id = :userId
    ORDER BY created_at DESC;
    """;

  private static final String SELECT_CONVERSATION_BY_ID = """
    SELECT id, user_id, title, created_at
    FROM conversations
    WHERE id = :id AND user_id = :userId;
    """;

  private static final String INSERT_MESSAGE = """
    INSERT INTO chat_messages
        (id, conversation_id, user_id, author_type, content, created_at)
    VALUES
        (:id, :conversationId, :userId, :authorType, :content, :createdAt);
    """;

  private static final String SELECT_MESSAGES_BY_CONVERSATION = """
    SELECT id, conversation_id, user_id, author_type, content, created_at
    FROM chat_messages
    WHERE conversation_id = :conversationId
    ORDER BY created_at;
    """;

  private final JdbcClient jdbcClient;
  private final RowMapper<Conversation> conversationMapper = new ConversationRowMapper();
  private final RowMapper<ChatMessage> messageMapper = new ChatMessageRowMapper();

  public ChatJdbcRepository(JdbcClient jdbcClient) {
    this.jdbcClient = jdbcClient;
  }

  @Override
  public void addConversation(Conversation conversation) {
    jdbcClient.sql(INSERT_CONVERSATION)
        .param("id", UUID.fromString(conversation.id()))
        .param("userId", UUID.fromString(conversation.userId()))
        .param("title", conversation.title())
        .param("createdAt", Timestamp.from(conversation.createdAt()))
        .update();
  }

  @Override
  public void addMessage(ChatMessage message) {
    jdbcClient.sql(INSERT_MESSAGE)
        .param("id", UUID.fromString(message.getId()))
        .param("conversationId", UUID.fromString(message.getConversationId()))
        .param("userId", UUID.fromString(message.getUserId()))
        .param("authorType", message.getAuthorType().name())
        .param("content", message.getContent())
        .param("createdAt", Timestamp.from(message.getCreatedAt()))
        .update();
  }

  @Override
  public List<Conversation> findConversations(String userId) {
    return jdbcClient.sql(SELECT_CONVERSATIONS_BY_USER)
        .param("userId", UUID.fromString(userId))
        .query(conversationMapper)
        .list();
  }

  @Override
  public Optional<Conversation> findConversation(String userId, String conversationId) {
    return jdbcClient.sql(SELECT_CONVERSATION_BY_ID)
        .param("id", UUID.fromString(conversationId))
        .param("userId", UUID.fromString(userId))
        .query(conversationMapper)
        .optional();
  }

  @Override
  public List<ChatMessage> findMessages(String conversationId) {
    return jdbcClient.sql(SELECT_MESSAGES_BY_CONVERSATION)
        .param("conversationId", UUID.fromString(conversationId))
        .query(messageMapper)
        .list();
  }

  private static class ConversationRowMapper implements RowMapper<Conversation> {

    @Override
    public Conversation mapRow(ResultSet rs, int rowNum) throws SQLException {
      return new Conversation(
          rs.getObject("id", UUID.class).toString(),
          rs.getObject("user_id", UUID.class).toString(),
          rs.getString("title"),
          rs.getTimestamp("created_at").toInstant());
    }
  }

  private static class ChatMessageRowMapper implements RowMapper<ChatMessage> {

    @Override
    public ChatMessage mapRow(ResultSet rs, int rowNum) throws SQLException {
      return new ChatMessage(
          rs.getObject("id", UUID.class).toString(),
          rs.getObject("conversation_id", UUID.class).toString(),
          rs.getObject("user_id", UUID.class).toString(),
          AuthorType.valueOf(rs.getString("author_type")),
          rs.getString("content"),
          rs.getTimestamp("created_at").toInstant());
    }
  }
}
