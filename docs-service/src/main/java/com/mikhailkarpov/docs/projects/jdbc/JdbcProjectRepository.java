package com.mikhailkarpov.docs.projects.jdbc;

import com.mikhailkarpov.docs.projects.Project;
import com.mikhailkarpov.docs.projects.ProjectId;
import com.mikhailkarpov.docs.projects.ProjectRepository;
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
public class JdbcProjectRepository implements ProjectRepository {

  private static final String INSERT_PROJECT = """
      INSERT INTO project (id, user_id, title, description, updated_at)
      VALUES (:id, :userId, :title, :description, :updatedAt);
      """;

  private static final String SELECT_PROJECT_BY_ID = """
      SELECT id, user_id, title, description, updated_at
      FROM project
      WHERE id = :id AND user_id = :userId;
      """;

  private static final String SELECT_PROJECTS_BY_USER = """
      SELECT id, user_id, title, description, updated_at
      FROM project
      WHERE user_id = :userId
      ORDER BY updated_at DESC;
      """;

  private static final String UPDATE_PROJECT = """
      UPDATE project
      SET title = :title, description = :description, updated_at = :updatedAt
      WHERE id = :id AND user_id = :userId;
      """;

  private static final String EXISTS_PROJECT = """
      SELECT EXISTS(
          SELECT 1 FROM project WHERE id = :id AND user_id = :userId
      );
      """;

  private static final String DELETE_PROJECT = """
      DELETE FROM project
      WHERE id = :id AND user_id = :userId;
      """;

  private final JdbcClient jdbcClient;
  private final RowMapper<Project> rowMapper = new ProjectRowMapper();

  public JdbcProjectRepository(JdbcClient jdbcClient) {
    this.jdbcClient = jdbcClient;
  }

  @Override
  public void addProject(Project project) {
    jdbcClient.sql(INSERT_PROJECT)
        .param("id", UUID.fromString(project.id()))
        .param("userId", UUID.fromString(project.userId()))
        .param("title", project.title())
        .param("description", project.description())
        .param("updatedAt", Timestamp.from(project.updatedAt()))
        .update();
  }

  @Override
  public boolean exists(ProjectId projectId) {
    return jdbcClient.sql(EXISTS_PROJECT)
          .param("id", UUID.fromString(projectId.id()))
          .param("userId", UUID.fromString(projectId.userId()))
          .query(Boolean.class)
          .single();
}

  @Override
  public Optional<Project> findProject(ProjectId projectId) {
    return jdbcClient.sql(SELECT_PROJECT_BY_ID)
        .param("id", UUID.fromString(projectId.id()))
        .param("userId", UUID.fromString(projectId.userId()))
        .query(rowMapper)
        .optional();
  }

  @Override
  public List<Project> findProjects(String userId) {
    return jdbcClient.sql(SELECT_PROJECTS_BY_USER)
        .param("userId", UUID.fromString(userId))
        .query(rowMapper)
        .list();
  }

  @Override
  public void updateProject(Project project) {
    jdbcClient.sql(UPDATE_PROJECT)
        .param("id", UUID.fromString(project.id()))
        .param("userId", UUID.fromString(project.userId()))
        .param("title", project.title())
        .param("description", project.description())
        .param("updatedAt", Timestamp.from(project.updatedAt()))
        .update();
  }

  @Override
  public boolean deleteProject(ProjectId projectId) {
    return jdbcClient.sql(DELETE_PROJECT)
        .param("id", UUID.fromString(projectId.id()))
        .param("userId", UUID.fromString(projectId.userId()))
        .update() > 0;
  }

  private static class ProjectRowMapper implements RowMapper<Project> {

    @Override
    public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
      return new Project(
          rs.getObject("id", UUID.class).toString(),
          rs.getObject("user_id", UUID.class).toString(),
          rs.getString("title"),
          rs.getString("description"),
          rs.getTimestamp("updated_at").toInstant());
    }
  }
}
