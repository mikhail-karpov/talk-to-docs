package com.mikhailkarpov.docs.ai;

import com.mikhailkarpov.docs.projects.event.ProjectDeletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ProjectListener {

  private static final Logger log = LoggerFactory.getLogger(ProjectListener.class);
  private final DocumentIndexer documentIndexer;

  public ProjectListener(DocumentIndexer documentIndexer) {
    this.documentIndexer = documentIndexer;
  }

  @TransactionalEventListener(ProjectDeletedEvent.class)
  void onProjectDeleted(ProjectDeletedEvent event) {
    var projectId = event.project().id();
    documentIndexer.deleteByProjectId(projectId)
        .exceptionally(ex -> {
          log.error("Failed to delete documents for project {}", projectId, ex);
          return null;
        });
  }
}
