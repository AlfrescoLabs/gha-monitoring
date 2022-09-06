package org.alfresco.badges.service.api;

import org.alfresco.badges.web.server.model.NamedColor;
import reactor.core.publisher.Mono;

public interface BadgeService {

  Mono<String> getBadge(String message, String label, NamedColor messageColor, NamedColor labelColor, String logo);

  Mono<String> getWorkflowBadge(String owner, String repository, String workflowId, String branch);
}
