package org.alfresco.badges.web.client.api;

import org.alfresco.badges.models.WorkflowRuns;
import reactor.core.publisher.Mono;

public interface GitHubWebClient {
  Mono<WorkflowRuns> getWorkflowRuns(String owner, String repository, String workflowId, String branch);

  Mono<WorkflowRuns> getPullRequests(String owner, String repository, String workflowId, String branch);
}
