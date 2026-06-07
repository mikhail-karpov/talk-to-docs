package com.mikhailkarpov.docs.projects;

import com.mikhailkarpov.docs.TestcontainersConfig;
import com.mikhailkarpov.docs.projects.command.CreateProjectCommand;
import com.mikhailkarpov.docs.projects.command.EditProjectCommand;
import com.mikhailkarpov.docs.projects.jdbc.JdbcProjectRepository;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;

@DataJdbcTest
@Import(TestcontainersConfig.class)
class ProjectServiceTest {

  // Seeded by db/seed/V3__insert_test_users.sql; required by the project.user_id FK.
  private static final String USER_ID = "2686f7a3-bd4a-4938-93a7-fe8e9360eb28";
  private static final String OTHER_USER_ID = UUID.randomUUID().toString();

  @Autowired
  private JdbcClient jdbcClient;

  private JdbcProjectRepository projectRepository;
  private ProjectService projectService;

  @BeforeEach
  void setUp() {
    projectRepository = new JdbcProjectRepository(jdbcClient);
    projectService = new ProjectService(projectRepository);
  }

  @Nested
  class CreateProject {

    @Test
    void persistsProjectAndReturnsIt() {
      var command = new CreateProjectCommand(USER_ID, "My Project", "A description");

      var project = projectService.createProject(command);

      Assertions.assertThat(project)
          .returns(USER_ID, Project::userId)
          .returns("My Project", Project::title)
          .returns("A description", Project::description);

      Assertions.assertThat(projectRepository.findProject(project.projectId()))
          .isPresent().get()
          .returns("My Project", Project::title);
    }

    @Test
    void persistsProjectWithNullDescription() {
      var command = new CreateProjectCommand(USER_ID, "No Desc", null);

      var project = projectService.createProject(command);

      Assertions.assertThat(projectRepository.findProject(project.projectId()))
          .isPresent().get()
          .returns(null, Project::description);
    }
  }

  @Nested
  class GetProject {

    @Test
    void returnsFoundProject() {
      var created = projectService.createProject(
          new CreateProjectCommand(USER_ID, "Found", null));

      var found = projectService.getProject(created.projectId());

      Assertions.assertThat(found)
          .returns(created.id(), Project::id)
          .returns(USER_ID, Project::userId)
          .returns("Found", Project::title);
    }

    @Test
    void throwsWhenProjectMissing() {
      var missingId = new ProjectId(UUID.randomUUID().toString(), USER_ID);

      Assertions.assertThatThrownBy(() -> projectService.getProject(missingId))
          .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    void throwsWhenProjectBelongsToAnotherUser() {
      var created = projectService.createProject(
          new CreateProjectCommand(USER_ID, "Mine", null));
      var wrongId = new ProjectId(created.id(), OTHER_USER_ID);

      Assertions.assertThatThrownBy(() -> projectService.getProject(wrongId))
          .isInstanceOf(ProjectNotFoundException.class);
    }
  }

  @Nested
  class ListProjects {

    @Test
    void returnsProjectsForUser() {
      projectService.createProject(new CreateProjectCommand(USER_ID, "Alpha", null));
      projectService.createProject(new CreateProjectCommand(USER_ID, "Beta", null));

      Assertions.assertThat(projectService.listProjects(USER_ID))
          .hasSizeGreaterThanOrEqualTo(2)
          .allMatch(p -> p.userId().equals(USER_ID));
    }

    @Test
    void returnsEmptyForUnknownUser() {
      Assertions.assertThat(projectService.listProjects(OTHER_USER_ID))
          .isEmpty();
    }
  }

  @Nested
  class EditProject {

    @Test
    void persistsNewTitleAndDescription() {
      var created = projectService.createProject(
          new CreateProjectCommand(USER_ID, "Old Title", "Old Desc"));
      var command = new EditProjectCommand(created.projectId(), "New Title", "New Desc");

      var edited = projectService.editProject(command);

      Assertions.assertThat(edited)
          .returns(created.id(), Project::id)
          .returns("New Title", Project::title)
          .returns("New Desc", Project::description);

      Assertions.assertThat(projectRepository.findProject(created.projectId()))
          .isPresent().get()
          .returns("New Title", Project::title)
          .returns("New Desc", Project::description);
    }

    @Test
    void throwsWhenProjectMissing() {
      var missingId = new ProjectId(UUID.randomUUID().toString(), USER_ID);
      var command = new EditProjectCommand(missingId, "Title", null);

      Assertions.assertThatThrownBy(() -> projectService.editProject(command))
          .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    void throwsWhenProjectBelongsToAnotherUser() {
      var created = projectService.createProject(
          new CreateProjectCommand(USER_ID, "Mine", null));
      var wrongId = new ProjectId(created.id(), OTHER_USER_ID);
      var command = new EditProjectCommand(wrongId, "Hacked", null);

      Assertions.assertThatThrownBy(() -> projectService.editProject(command))
          .isInstanceOf(ProjectNotFoundException.class);

      Assertions.assertThat(projectRepository.findProject(created.projectId()))
          .isPresent().get()
          .returns("Mine", Project::title);
    }
  }

  @Nested
  class DeleteProject {

    @Test
    void removesProjectFromDatabase() {
      var created = projectService.createProject(
          new CreateProjectCommand(USER_ID, "To Delete", null));

      projectService.deleteProject(created.projectId());

      Assertions.assertThat(projectRepository.findProject(created.projectId()))
          .isEmpty();
    }

    @Test
    void throwsWhenProjectMissing() {
      var missingId = new ProjectId(UUID.randomUUID().toString(), USER_ID);

      Assertions.assertThatThrownBy(() -> projectService.deleteProject(missingId))
          .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    void throwsWhenProjectBelongsToAnotherUser() {
      var created = projectService.createProject(
          new CreateProjectCommand(USER_ID, "Mine", null));
      var wrongId = new ProjectId(created.id(), OTHER_USER_ID);

      Assertions.assertThatThrownBy(() -> projectService.deleteProject(wrongId))
          .isInstanceOf(ProjectNotFoundException.class);

      Assertions.assertThat(projectRepository.findProject(created.projectId()))
          .isPresent();
    }
  }
}
