package org.alfresco.badges.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(GitHubProperties.class)
public class WebClientsConfig {

  private final GitHubProperties gitHubProperties;

  public WebClientsConfig(GitHubProperties gitHubProperties) {
    this.gitHubProperties = gitHubProperties;
  }

  @Bean
  public WebClient githubWebClient() {
    return WebClient.builder()
        .baseUrl("https://api.github.com/repos")
        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
        .defaultHeaders(headers -> headers.setBearerAuth(gitHubProperties.getToken()))
        .build();
  }

}
