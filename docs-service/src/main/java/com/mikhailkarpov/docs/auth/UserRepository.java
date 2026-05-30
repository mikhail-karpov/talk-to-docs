package com.mikhailkarpov.docs.auth;

import java.util.Optional;

public interface UserRepository {

  Optional<User> findByEmail(String email);
}
