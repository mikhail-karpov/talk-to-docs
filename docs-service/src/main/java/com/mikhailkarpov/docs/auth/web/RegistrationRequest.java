package com.mikhailkarpov.docs.auth.web;

import com.mikhailkarpov.docs.auth.RegisterUserCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(

    @NotNull
    @Email
    String email,

    @NotNull
    @Size(min = 6, max = 32)
    String password,

    @NotNull
    @Size(min = 1, max = 32)
    String firstName,

    @NotNull
    @Size(min = 1, max = 32)
    String lastName) {

  public RegisterUserCommand toCommand() {
    return new RegisterUserCommand(email, password, firstName, lastName);
  }
}
