package org.alfresco.badges.service.api;

import org.alfresco.badges.model.GithubPullRequest;
import org.alfresco.badges.web.server.model.PrField;
import reactor.core.publisher.Mono;

public interface PullRequestService {

  Mono<String> getPullRequestStatusBadge(String owner, String repository, String filter, String user, PrField field);

  Mono<GithubPullRequest> getPullRequestStatus(String owner, String repository, String filter, String user);

}