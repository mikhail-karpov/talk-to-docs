package com.mikhailkarpov.docs.documents.event;

import com.mikhailkarpov.docs.documents.DocumentMetadata;

public record DocumentUpdatedEvent(DocumentMetadata document) {
}
