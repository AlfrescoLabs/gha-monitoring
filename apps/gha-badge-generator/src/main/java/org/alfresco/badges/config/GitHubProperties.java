package org.alfresco.badges.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

import javax.validation.constraints.NotBlank;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ConfigurationProperties(prefix = "github")
public class GitHubProperties {

  @NotBlank
  private String token;
  @DurationUnit(ChronoUnit.SECONDS)
  private Duration cacheDuration = Duration.ofSeconds(60);

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public Duration getCacheDuration() {
    return cacheDuration;
  }

  public void setCacheDuration(Duration cacheDuration) {
    this.cacheDuration = cacheDuration;
  }
}
