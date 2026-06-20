package com.mikhailkarpov.docs.auth;

import java.util.Optional;

public interface UserRepository {

  void add(User user);

  boolean existsByEmail(String email);

  Optional<User> findByEmail(String email);
}
