package com.mikhailkarpov.docs.documents;

import org.springframework.core.io.Resource;

public record DocumentCreatedEvent(DocumentMetadata document, Resource resource) {

}
