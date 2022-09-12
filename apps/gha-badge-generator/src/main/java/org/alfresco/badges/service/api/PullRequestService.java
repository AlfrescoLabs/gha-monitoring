package org.alfresco.badges.service.api;

import java.io.IOException;
import java.util.Optional;
import org.alfresco.badges.model.GithubPullRequest;

public interface PullRequestService {

  Optional<GithubPullRequest> getPullRequestStatus(String repository, String pattern) throws IOException;
}
