package com.mikhailkarpov.docs.auth;

import com.mikhailkarpov.docs.auth.jdbc.UserJdbcRepository;
import com.mikhailkarpov.docs.config.IntegrationTest;
import java.util.Objects;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@IntegrationTest
class UserServiceTest {

  @Autowired
  private JdbcClient jdbcClient;

  private final PasswordEncoder passwordEncoder =
      PasswordEncoderFactories.createDelegatingPasswordEncoder();

  private UserService userService;

  @BeforeEach
  void setUp() {
    var userRepository = new UserJdbcRepository(jdbcClient);
    this.userService = new UserService(userRepository, passwordEncoder);
  }


  @Nested
  class RegisterUserTest {

    @Test
    void registerUser_persistsUserWithEncodedPassword() {
      var command = new RegisterUserCommand("new@example.com", "password123", "Jane", "Roe");

      userService.registerUser(command);

      var user = userService.loadUserByUsername("new@example.com");

      Assertions.assertThat(user)
          .returns("new@example.com", User::getEmail)
          .returns("Jane", User::getFirstName)
          .returns("Roe", User::getLastName)
          .matches(u -> !Objects.equals(u.getPassword(), "password123"))
          .matches(u -> passwordEncoder.matches("password123", u.getPassword()));
    }

    @Test
    void registerUser_withExistingEmail_throwsUserRegisteredException() {
      var command = new RegisterUserCommand("test@example.com", "password123", "Test", "User");

      Assertions.assertThatThrownBy(() -> userService.registerUser(command))
          .isInstanceOf(UserRegisteredException.class);
    }
  }


  @Nested
  class LoadUserTest {

    @Test
    void loadTestUser() {
      var user = userService.loadUserByUsername("test@example.com");
      Assertions.assertThat(user)
          .isNotNull();
    }

    @Test
    void loadNonExistingUserThrows() {
      Assertions.assertThatThrownBy(() -> userService.loadUserByUsername("not-found@example.com"))
          .isInstanceOf(UsernameNotFoundException.class);
    }
  }
}
