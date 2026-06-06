package com.mikhailkarpov.docs.documents.event;

import com.mikhailkarpov.docs.documents.DocumentMetadata;
import org.springframework.core.io.Resource;

public record DocumentCreatedEvent(DocumentMetadata document, Resource resource) {

}
