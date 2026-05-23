package com.mikhailkarpov.docs.auth.web;

import com.mikhailkarpov.docs.auth.User;

public record AuthResponse(String id, String email, String firstName, String lastName) {

  public static AuthResponse from(User user) {
    return new AuthResponse(
        user.getId(),
        user.getUsername(),
        user.getFirstName(),
        user.getLastName()
    );
  }
}
