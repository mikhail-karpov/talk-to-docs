package com.mikhailkarpov.docs.auth;

import java.util.Collection;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class User implements UserDetails {

  private final String id;
  private final String email;
  private final String password;
  private final String firstName;
  private final String lastName;

  public User(String id, String email, String password, String firstName, String lastName) {
    if (id == null || id.isBlank()) {
      throw new IllegalArgumentException("id cannot be null");
    }
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("email cannot be null");
    }
    if (password == null || password.isBlank()) {
      throw new IllegalArgumentException("password cannot be null");
    }
    if (firstName == null || firstName.isBlank()) {
      throw new IllegalArgumentException("First name cannot be null");
    }
    if (lastName == null || lastName.isBlank()) {
      throw new IllegalArgumentException("Last name cannot be null");
    }
    this.id = id;
    this.email = email;
    this.password = password;
    this.firstName = firstName;
    this.lastName = lastName;
  }

  @Override
  @NonNull
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_USER"));
  }

  public String getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  @NonNull
  public String getUsername() {
    return getEmail();
  }

  @Override
  public final boolean equals(Object o) {
    if (!(o instanceof User user))
      return false;

    return id.equals(user.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public String toString() {
    return "User{" +
        "id='" + id + '\'' +
        '}';
  }
}
