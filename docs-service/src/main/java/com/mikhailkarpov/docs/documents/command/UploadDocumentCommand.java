package com.mikhailkarpov.docs.documents.command;

import com.mikhailkarpov.docs.projects.ProjectId;
import org.springframework.core.io.Resource;

public record UploadDocumentCommand(ProjectId projectId, Resource resource, String contentType) {}
