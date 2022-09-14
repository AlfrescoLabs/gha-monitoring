package org.alfresco.badges.web.client.api;

import java.net.URI;
import org.alfresco.badges.models.IssueSearchResultItem;
import org.alfresco.badges.models.PullRequest;
import org.alfresco.badges.models.WorkflowRuns;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GitHubWebClient {
  Mono<WorkflowRuns> getWorkflowRuns(String owner, String repository, String workflowId, String branch);

  Mono<WorkflowRuns> getWorkflowRunsForARepository(String owner, String repository, String branch);

  Flux<IssueSearchResultItem> getIssuePullRequests(String owner, String repository, String user, String filter, String state);
  
  Flux<PullRequest> getPullRequest(URI id);
}
