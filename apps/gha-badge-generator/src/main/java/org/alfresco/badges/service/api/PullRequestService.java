package org.alfresco.badges.service.api;

import java.io.IOException;
import java.util.Optional;
import org.alfresco.badges.model.GithubPullRequest;
import reactor.core.publisher.Mono;
import org.alfresco.badges.web.server.model.PrField;

public interface PullRequestService {

  Mono<String> getPullRequestStatus(String owner, String repository, String pattern, String user, PrField field);
}
