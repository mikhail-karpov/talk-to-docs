package com.mikhailkarpov.docs.auth;

public record RegisterUserCommand(String email, String password, String firstName, String lastName) {}
