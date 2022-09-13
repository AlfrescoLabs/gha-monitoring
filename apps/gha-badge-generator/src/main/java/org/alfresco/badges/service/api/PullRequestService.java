package org.alfresco.badges.service.api;

import org.alfresco.badges.web.server.model.PrField;
import reactor.core.publisher.Mono;

public interface PullRequestService {

  Mono<String> getPullRequestStatus(String owner, String repository, String pattern, String user, PrField field);
}
