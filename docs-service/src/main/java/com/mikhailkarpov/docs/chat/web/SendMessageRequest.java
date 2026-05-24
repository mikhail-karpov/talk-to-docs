package com.mikhailkarpov.docs.chat.web;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SendMessageRequest(@NotNull @Size(min = 2, max = 256) String content) {

}
