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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.alfresco.badges.models.PullRequestSimple;

@Component
public class GitHubWebClientImpl implements GitHubWebClient {

  private static final Logger LOG = LoggerFactory.getLogger(GitHubWebClientImpl.class);
  private static final String WORKFLOW_URI = "/{owner}/{repository}/actions/workflows/{workflowId}/runs";
  private static final String WORKFLOW_LIST_URI = "/{owner}/{repository}/actions/runs";
  private static final String PULL_URI = "/{owner}/{repo}/pulls";
  private final WebClient webClient;
  private final Cache<String, WorkflowRuns> cache;

  public GitHubWebClientImpl(WebClient webClient, GitHubProperties gitHubProperties) {
    this.webClient = webClient;
    this.cache = Caffeine.newBuilder().expireAfterWrite(gitHubProperties.getCacheDuration()).build();
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
        .uri(uriBuilder -> uriBuilder.path(WORKFLOW_URI).queryParam("branch", branch).build(owner, repository, workflowId))
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(WorkflowRuns.class)
        .doOnNext(workflowRuns -> {
          cache.put(workflowCompositeKey, workflowRuns);
        });
  }

  @Override
  public Mono<WorkflowRuns> getWorkflowRunsForARepository(String owner, String repository,
      String branch) {
    //TODO add cache

    LOG.info("Requesting list workflow runs for a repository for owner+repository+workflow={}", owner+"/"+repository);
    return webClient.get()
        .uri(uriBuilder -> uriBuilder.path(WORKFLOW_LIST_URI).queryParam("branch", branch).build(owner, repository))
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(WorkflowRuns.class);
  }

  @Override
  public Flux<PullRequestSimple> getPullRequests(String owner, String repository, String user,
      String state) {
    //TODO add cache
    LOG.info("Requesting pull request for {}", owner+"/"+repository);
    return webClient.get()
        .uri(uriBuilder -> uriBuilder.path(PULL_URI).queryParam("head", "user:"+user).build(owner, repository))
        .retrieve()
        .bodyToFlux(PullRequestSimple.class);
  }

}
