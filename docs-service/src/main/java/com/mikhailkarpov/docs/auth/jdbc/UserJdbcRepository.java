package com.mikhailkarpov.docs.auth.jdbc;

import com.mikhailkarpov.docs.auth.User;
import com.mikhailkarpov.docs.auth.UserRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class UserJdbcRepository implements UserRepository {

  private static final String EXISTS_USER_BY_EMAIL = """
    SELECT EXISTS (
      SELECT 1
      FROM users
      WHERE email = :email
    )
    """;

  private static final String INSERT_USER = """
    INSERT INTO users (id, email, password, first_name, last_name, created_at)
    VALUES (:id, :email, :password, :first_name, :last_name, now());
    """;

  private static final String SELECT_USER_BY_EMAIL = """
    SELECT
        id, email, password, first_name, last_name
    FROM users
    WHERE
        email = :email;
    """;

  private final JdbcClient jdbcClient;
  private final RowMapper<User> rowMapper = new UserRowMapper();

  public UserJdbcRepository(JdbcClient jdbcClient) {
    this.jdbcClient = jdbcClient;
  }

  @Override
  public void add(User user) {
    jdbcClient.sql(INSERT_USER)
        .param("id", UUID.fromString(user.getId()))
        .param("email", user.getEmail())
        .param("password", user.getPassword())
        .param("first_name", user.getFirstName())
        .param("last_name", user.getLastName())
        .update();
  }

  @Override
  public boolean existsByEmail(String email) {
    return jdbcClient.sql(EXISTS_USER_BY_EMAIL)
        .param("email", email)
        .query(Boolean.class)
        .single();
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return jdbcClient.sql(SELECT_USER_BY_EMAIL)
        .param("email", email)
        .query(rowMapper)
        .optional();
  }

  private static class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
      return new User(
          rs.getObject("id", UUID.class).toString(),
          rs.getString("email"),
          rs.getString("password"),
          rs.getString("first_name"),
          rs.getString("last_name")
      );
    }
  }
}
