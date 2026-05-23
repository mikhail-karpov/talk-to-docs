package com.mikhailkarpov.docs.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

  private final Map<String, User> users = new HashMap<>();

  private final PasswordEncoder passwordEncoder;

  public UserService(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  public User createUser(String email, String password, String firstName, String lastName) {
    if (users.containsKey(email)) {
      throw new IllegalArgumentException("User already exists");
    }
    var user = new User(
        UUID.randomUUID().toString(),
        email,
        passwordEncoder.encode(password),
        firstName,
        lastName
    );
    users.put(email, user);
    return user;
  }

  @Override
  @NonNull
  public User loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
    var user = users.get(username);
    if (user == null) {
      throw new UsernameNotFoundException("User not found");
    }
    return user;
  }
}
