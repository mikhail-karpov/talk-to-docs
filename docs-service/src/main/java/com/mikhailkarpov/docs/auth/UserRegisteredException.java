package com.mikhailkarpov.docs.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserRegisteredException extends RuntimeException {

  public UserRegisteredException() {
    super("User already registered");
  }

}
