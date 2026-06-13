package com.mikhailkarpov.docs.ai;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mikhailkarpov.docs.projects.Project;
import com.mikhailkarpov.docs.projects.event.ProjectDeletedEvent;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectListenerTest {

  @Mock
  private DocumentIndexer documentIndexer;

  @InjectMocks
  private ProjectListener listener;

  @Test
  void deletesAllDocumentsOfDeletedProject() {
    var project = new Project("user-1", "My project", "A description");
    when(documentIndexer.deleteByProjectId(project.id()))
        .thenReturn(CompletableFuture.completedFuture(null));

    listener.onProjectDeleted(new ProjectDeletedEvent(project));

    verify(documentIndexer).deleteByProjectId(project.id());
  }
}
