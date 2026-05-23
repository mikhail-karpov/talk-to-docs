package com.mikhailkarpov.docs.auth.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AuthRequest(
    @NotNull
    @Email
    String email,

    @NotNull
    @Size(min = 6, max = 32)
    String password) {
}
