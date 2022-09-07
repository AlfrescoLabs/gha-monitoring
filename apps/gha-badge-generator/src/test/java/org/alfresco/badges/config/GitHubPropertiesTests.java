package org.alfresco.badges.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class GitHubPropertiesTests {

  @Autowired
  private GitHubProperties gitHubProperties;

  @Test
  void propertiesShouldBeSet() {
    assertNotNull(gitHubProperties.getToken());
    assertNotNull(gitHubProperties.getCacheDuration());
  }

}
