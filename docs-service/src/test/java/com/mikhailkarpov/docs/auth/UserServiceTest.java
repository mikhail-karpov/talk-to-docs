package com.mikhailkarpov.docs.auth;

import com.mikhailkarpov.docs.TestcontainersConfig;
import com.mikhailkarpov.docs.auth.jdbc.UserJdbcRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@DataJdbcTest
@Import(TestcontainersConfig.class)
class UserServiceTest {

  @Autowired
  private JdbcClient jdbcClient;

  private UserService userService;

  @BeforeEach
  void setUp() {
    var userRepository = new UserJdbcRepository(jdbcClient);
    this.userService = new UserService(userRepository);
  }

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
