package org.alfresco.badges.web.client.api;

import java.util.List;
import org.alfresco.badges.models.WorkflowRuns;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.alfresco.badges.models.PullRequestSimple;

public interface GitHubWebClient {
  Mono<WorkflowRuns> getWorkflowRuns(String owner, String repository, String workflowId, String branch);

  Mono<WorkflowRuns> getWorkflowRunsForARepository(String owner, String repository, String branch);

  Flux<PullRequestSimple> getPullRequests(String owner, String repository, String user, String state);
}
