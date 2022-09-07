package org.alfresco.badges.web.client.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.alfresco.badges.config.GitHubProperties;
import org.alfresco.badges.models.WorkflowRuns;
import org.alfresco.badges.web.client.api.GitHubWebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class GitHubWebClientImpl implements GitHubWebClient {

  private static final Logger LOG = LoggerFactory.getLogger(GitHubWebClientImpl.class);
  private static final String URI = "/{owner}/{repository}/actions/workflows/{workflowId}/runs";
  private final WebClient webClient;
  private final Cache<String, WorkflowRuns> cache;

  public GitHubWebClientImpl(WebClient webClient, GitHubProperties gitHubProperties) {
    this.webClient = webClient;
    cache = Caffeine.newBuilder().expireAfterWrite(gitHubProperties.getCacheDuration()).build();
  }

  @Override
  public Mono<WorkflowRuns> getWorkflowRuns(String owner, String repository, String workflowId, String branch) {
    final String workflowCompositeKey = String.join("+", owner, repository, workflowId, branch);
    WorkflowRuns cachedValue = cache.getIfPresent(workflowCompositeKey);
    if (cachedValue != null) {
      return Mono.just(cachedValue);
    }

    LOG.info("Requesting workflow runs for owner+repository+workflow={}", workflowCompositeKey);
    return webClient.get()
        .uri(uriBuilder -> uriBuilder.path(URI).queryParam("branch", branch).build(owner, repository, workflowId))
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(WorkflowRuns.class)
        .doOnNext(workflowRuns -> {
          cache.put(workflowCompositeKey, workflowRuns);
        });
  }

}
