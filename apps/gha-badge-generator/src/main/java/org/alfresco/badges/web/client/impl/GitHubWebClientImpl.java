package org.alfresco.badges.web.client.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.alfresco.badges.config.GitHubProperties;
import org.alfresco.badges.models.IssueSearchResultItem;
import org.alfresco.badges.models.Issues;
import org.alfresco.badges.models.PullRequest;
import org.alfresco.badges.models.WorkflowRuns;
import org.alfresco.badges.web.client.api.GitHubWebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class GitHubWebClientImpl implements GitHubWebClient {

  private static final Logger LOG = LoggerFactory.getLogger(GitHubWebClientImpl.class);
  private static final String WORKFLOW_URI = "/repos/{owner}/{repository}/actions/workflows/{workflowId}/runs";
  private static final String WORKFLOW_LIST_URI = "/repos/{owner}/{repository}/actions/runs";
  private static final String PULL_URI = "/search/issues";
  private final WebClient webClient;
  private final Cache<String, WorkflowRuns> cache;
  private final Cache<String, List<IssueSearchResultItem>> cachePrs;
  private final Cache<String, WorkflowRuns> cacheWfRuns;

  public GitHubWebClientImpl(WebClient webClient, GitHubProperties gitHubProperties) {
    this.webClient = webClient;
    this.cache = Caffeine.newBuilder().expireAfterWrite(gitHubProperties.getCacheDuration()).build();
    this.cachePrs = Caffeine.newBuilder().expireAfterWrite(gitHubProperties.getCacheDuration()).build();
    this.cacheWfRuns = Caffeine.newBuilder().expireAfterWrite(gitHubProperties.getCacheDuration()).build();
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
        .doOnNext(workflowRuns -> cache.put(workflowCompositeKey, workflowRuns));
  }

  @Override
  public Mono<WorkflowRuns> getWorkflowRunsForARepository(String owner, String repository,
                                                          String branch) {
    
    String wfRunsKey = String.join("+", owner, repository, branch);
    WorkflowRuns cachedValue = cacheWfRuns.getIfPresent(wfRunsKey);
    if (cachedValue != null) {
      return Mono.just(cachedValue);
    }

    LOG.info("Requesting list workflow runs for a repository for owner+repository+workflow={}", owner + "/" + repository);
    return webClient.get()
        .uri(uriBuilder -> uriBuilder.path(WORKFLOW_LIST_URI).queryParam("branch", branch).build(owner, repository))
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(WorkflowRuns.class)
        .doOnNext(wfr -> cacheWfRuns.put(wfRunsKey, wfr));
  }

  @Override
  public Flux<IssueSearchResultItem> getIssuePullRequests(String owner, String repository, String user,
                                                          String filter, String state) {
    String searchQuery = buildQuery(owner, repository, user, filter, state);
    
    List<IssueSearchResultItem> cachedValue = cachePrs.getIfPresent(searchQuery);
    if (cachedValue != null) {
      return Flux.fromIterable(cachedValue);
    }
    
    return webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(PULL_URI)
            .queryParam("per_page", 1)
            .queryParam("q", searchQuery)
            .build())
        .retrieve()
        .bodyToFlux(Issues.class)
        .doOnNext(prs -> cachePrs.put(searchQuery, prs.getItems()))
        .flatMap(i -> Flux.fromIterable(i.getItems()));
  }

  @Override
  public Flux<PullRequest> getPullRequest(URI uri) {
    return webClient.get()
        .uri(uri)
        .retrieve()
        .bodyToFlux(PullRequest.class);
  }

  private String buildQuery(String owner, String repository, String user, String filter, String state) {
    Map<String, String> params = Map
        .of("", filter,
            "is", "pr",
            "repo", owner + "/" + repository,
            "author", user,
            "state", state
           );
    return params
        .entrySet()
        .stream()
        .map(entry -> buildSearchField(entry))
        .collect(Collectors.joining("+"));
  }

  private String buildSearchField(Entry<String, String> entry) {
    String key = entry.getKey();
    String value = entry.getValue();
    return StringUtils.hasText(key) ? key + ":" + value : value;
  }

}
