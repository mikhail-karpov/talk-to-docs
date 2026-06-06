package com.mikhailkarpov.docs.documents.event;

import com.mikhailkarpov.docs.documents.DocumentMetadata;

public record DocumentDeletedEvent(DocumentMetadata document) {

}
