package com.mikhailkarpov.docs;

import com.mikhailkarpov.docs.config.TestcontainersConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("openai")
@SpringBootTest
@Import(TestcontainersConfig.class)
class DocsServiceApplicationTest {

  @Autowired
  private ApplicationContext applicationContext;

  @Test
  void contextLoads() {
    Assertions.assertThat(applicationContext).isNotNull();
  }
}
