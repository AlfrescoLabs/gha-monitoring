package org.alfresco.badges.service.impl;

import io.github.dsibilio.badgemaker.core.BadgeFormatBuilder;
import io.github.dsibilio.badgemaker.core.BadgeMaker;
import io.github.dsibilio.badgemaker.model.BadgeFormat;
import org.alfresco.badges.models.WorkflowRun;
import org.alfresco.badges.models.WorkflowRuns;
import org.alfresco.badges.service.api.BadgeService;
import org.alfresco.badges.web.client.api.GitHubWebClient;
import org.alfresco.badges.web.server.model.NamedColor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class BadgeServiceImpl implements BadgeService {

  private static final String GITHUB_LOGO = "data:image/svg+xml;base64,PHN2ZyBmaWxsPSIjNEU5QkNEIiByb2xlPSJpbWciIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgdmlld0JveD0iMCAwIDI0IDI0Ij48dGl0bGU+R2l0SHViIGljb248L3RpdGxlPjxwYXRoIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgZmlsbD0iIzk1OURBNSIgZD0iTTExIDNjLTMuODY4IDAtNyAzLjEzMi03IDdhNi45OTYgNi45OTYgMCAwIDAgNC43ODYgNi42NDFjLjM1LjA2Mi40ODItLjE0OC40ODItLjMzMiAwLS4xNjYtLjAxLS43MTgtLjAxLTEuMzA0LTEuNzU4LjMyNC0yLjIxMy0uNDI5LTIuMzUzLS44MjItLjA3OS0uMjAyLS40Mi0uODIzLS43MTctLjk5LS4yNDUtLjEzLS41OTUtLjQ1NC0uMDEtLjQ2My41NTItLjAwOS45NDYuNTA4IDEuMDc3LjcxOC42MyAxLjA1OCAxLjYzNi43NiAyLjAzOS41NzcuMDYxLS40NTUuMjQ1LS43NjEuNDQ2LS45MzYtMS41NTctLjE3NS0zLjE4NS0uNzc5LTMuMTg1LTMuNDU2IDAtLjc2Mi4yNzEtMS4zOTIuNzE4LTEuODgyLS4wNy0uMTc1LS4zMTUtLjg5Mi4wNy0xLjg1NSAwIDAgLjU4Ni0uMTgzIDEuOTI1LjcxOGE2LjUgNi41IDAgMCAxIDEuNzUtLjIzNiA2LjUgNi41IDAgMCAxIDEuNzUuMjM2YzEuMzM4LS45MSAxLjkyNS0uNzE4IDEuOTI1LS43MTguMzg1Ljk2My4xNCAxLjY4LjA3IDEuODU1LjQ0Ni40OS43MTcgMS4xMTIuNzE3IDEuODgyIDAgMi42ODYtMS42MzYgMy4yOC0zLjE5NCAzLjQ1Ni4yNTQuMjE5LjQ3My42MzkuNDczIDEuMjk1IDAgLjkzNi0uMDA5IDEuNjg5LS4wMDkgMS45MjUgMCAuMTg0LjEzMS40MDIuNDgxLjMzMkE3LjAxMSA3LjAxMSAwIDAgMCAxOCAxMGMwLTMuODY3LTMuMTMzLTctNy03eiIvPjwvc3ZnPg==";
  private final GitHubWebClient gitHubWebClient;

  public BadgeServiceImpl(GitHubWebClient gitHubWebClient) {
    this.gitHubWebClient = gitHubWebClient;
  }

  @Override
  public Mono<String> getBadge(String message, String label, NamedColor messageColor, NamedColor labelColor,
                               String logo) {
    BadgeFormat badgeFormat = new BadgeFormatBuilder(message).withLabel(label)
        .withLabelColor(toNamedColor(labelColor, io.github.dsibilio.badgemaker.model.NamedColor.GREY))
        .withMessageColor(toNamedColor(messageColor, io.github.dsibilio.badgemaker.model.NamedColor.BRIGHTGREEN))
        .withLogo(logo).build();

    return Mono.fromCallable(() -> BadgeMaker.makeBadge(badgeFormat));
  }

  @Override
  public Mono<String> getWorkflowBadge(String owner, String repository, String workflowId, String branch) {
    return gitHubWebClient.getWorkflowRuns(owner, repository, workflowId, branch)
        .map(BadgeServiceImpl::buildWorkflowBadge);
  }

  private static String buildWorkflowBadge(WorkflowRuns workflowRuns) {
    String label = "Unknown";
    String message = "unknown";
    String status = "unknown";
    WorkflowRun lastWorkflowRun = workflowRuns.getWorkflow_runs()
        .stream()
        .reduce((a, b) -> a.getRun_started_at().compareTo(b.getRun_started_at()) > 0 ? a : b)
        .orElse(null);

    if (lastWorkflowRun != null) {
      label = lastWorkflowRun.getName();
      status = lastWorkflowRun.getStatus();
      message = lastWorkflowRun.getStatus();
    }

    BadgeFormat badgeFormat = new BadgeFormatBuilder(message)
        .withLabel(label)
        .withLabelColor(io.github.dsibilio.badgemaker.model.NamedColor.GREY)
        .withMessageColor(getBadgeColor(status))
        .withLogo(GITHUB_LOGO)
        .build();

    return BadgeMaker.makeBadge(badgeFormat);
  }

  private static io.github.dsibilio.badgemaker.model.NamedColor toNamedColor(NamedColor color,
                                                                             io.github.dsibilio.badgemaker.model.NamedColor defaultColor) {
    return color != null ? io.github.dsibilio.badgemaker.model.NamedColor.valueOf(color.toString()) : defaultColor;
  }

  private static io.github.dsibilio.badgemaker.model.NamedColor getBadgeColor(String status) {
    return switch (status) {
      case "completed", "success" -> io.github.dsibilio.badgemaker.model.NamedColor.BRIGHTGREEN;
      case "failure", "action_required", "timed_out" -> io.github.dsibilio.badgemaker.model.NamedColor.RED;
      case "in_progress", "queued", "requested", "waiting" -> io.github.dsibilio.badgemaker.model.NamedColor.YELLOW;
      case "cancelled", "skipped", "stale", "neutral" -> io.github.dsibilio.badgemaker.model.NamedColor.LIGHTGREY;
      default -> io.github.dsibilio.badgemaker.model.NamedColor.ORANGE;
    };
  }

}