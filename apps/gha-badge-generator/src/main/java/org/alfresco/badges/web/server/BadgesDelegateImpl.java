package org.alfresco.badges.web.server;

import java.net.URI;
import org.alfresco.badges.service.api.BadgeService;
import org.alfresco.badges.service.api.PullRequestService;
import org.alfresco.badges.web.server.api.BadgesApiDelegate;
import org.alfresco.badges.web.server.model.NamedColor;
import org.alfresco.badges.web.server.model.PrField;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class BadgesDelegateImpl implements BadgesApiDelegate {

  private final BadgeService badgeService;
  private final PullRequestService pullRequestService;

  public BadgesDelegateImpl(BadgeService badgeService, PullRequestService pullRequestService) {
    this.badgeService = badgeService;
    this.pullRequestService = pullRequestService;
  }

  @Override
  public Mono<ResponseEntity<Resource>> getBadge(String message, String label, NamedColor messageColor,
                                                 NamedColor labelColor, String logo, ServerWebExchange exchange) {
    return withErrorHandling(badgeService.getBadge(message, label, messageColor, labelColor, logo)
                                 .map(this::getResponse));
  }

  @Override
  public Mono<ResponseEntity<Resource>> getGitHubWorkflowBadge(String owner, String repository, String workflowId,
                                                               String branch, String label, ServerWebExchange exchange) {
    return withErrorHandling(badgeService.getWorkflowBadge(owner, repository, workflowId, branch, label)
                                 .map(this::getResponse));
  }

  @Override
  public Mono<ResponseEntity<Resource>> getPrStatus(String owner, String repository, String filter, String user, PrField field, ServerWebExchange exchange) {
    return withErrorHandling(pullRequestService.getPullRequestStatusBadge(owner, repository, filter, user, field)
                                 .map(this::getResponse));
  }

  @Override
  public Mono<ResponseEntity<Void>> getPrRedirectUrl(String owner, String repository, String filter, String user, ServerWebExchange exchange) {
    return withErrorHandling(pullRequestService.getPullRequestStatus(owner, repository, filter, user)
                                 .map(pr -> this.redirect(pr.getURL())));
  }

  private ResponseEntity<Void> redirect(String url) {
    return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
        .location(URI.create(url))
        .build();
  }

  private ResponseEntity<Resource> getResponse(String badge) {
    return ResponseEntity.ok()
        .cacheControl(CacheControl.noCache())
        .body(new ByteArrayResource(badge.getBytes()));
  }

  private static <T> Mono<ResponseEntity<T>> withErrorHandling(Mono<ResponseEntity<T>> mono) {
    return mono.onErrorResume(Exception.class, BadgesDelegateImpl::getErrorView);
  }

  private static Mono getErrorView(Exception ex) {
    ex.printStackTrace();
    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                         .contentType(MediaType.TEXT_PLAIN)
                         .body(new ByteArrayResource(ex.getMessage().getBytes())));
  }

}