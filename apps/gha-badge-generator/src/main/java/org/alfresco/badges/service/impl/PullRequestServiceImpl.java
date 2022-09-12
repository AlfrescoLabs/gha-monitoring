package org.alfresco.badges.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.alfresco.badges.model.GithubPullRequest;
import org.alfresco.badges.web.client.api.GitHubWebClient;

public class PullRequestServiceImpl {

  private final GitHubWebClient gitHubWebClient;

  public PullRequestServiceImpl(GitHubWebClient gitHubWebClient) {
    this.gitHubWebClient = gitHubWebClient;
  }

  public Optional<GithubPullRequest> getPullRequestStatus(String repository, String filter) throws IOException {

    return getLastPullRequestToProcess(repository, filter)
        .stream()
        .map(processDataAndBuildResponse(repository))
        .collect(Collectors.toList())
        .stream()
        .findFirst();
  }

  private Function<GHPullRequest, GithubPullRequest> processDataAndBuildResponse(String repository) {
    return pr -> {
      GithubPullRequest githubPullRequest = new GithubPullRequest();
      githubPullRequest.setTitle(pr.getTitle());
      githubPullRequest.setDescription(pr.getBody());
      githubPullRequest.setURL(pr.getHtmlUrl().toString());
      githubPullRequest.setMerged(pr.getMergedAt() != null);
      String branch = pr.getHead().getRef();
      Optional<GHWorkflowRun> lastRunsFailedByPrNumber = githubClient.getLastRunsByPrBranch(repository, branch);
      lastRunsFailedByPrNumber.ifPresentOrElse(
          ghWorkflowRun -> computeStatus(githubPullRequest, ghWorkflowRun, pr),
          () -> githubPullRequest.setPullRequestStatus(PullRequestStatus.UNKNOWN));
      return githubPullRequest;
    };
  }

  private List<GHPullRequest> getLastPullRequestToProcess(String repository, String filter) throws IOException {
    List<GHPullRequest> result = gitHubWebClient.getPullRequest(repository, filter, GHIssueState.OPEN);
    if (result.isEmpty()) {
      result = githubClient.getPullRequest(repository, filter, GHIssueState.CLOSED);
    }
    return result;
  }

  private void computeStatus(GithubPullRequest githubPullRequest, GHWorkflowRun ghWorkflowRun, GHPullRequest pr) {
    if (isClosedWithoutMerging(pr)) {
      githubPullRequest.setPullRequestStatus(PullRequestStatus.MERGED);
    } else if (isMergedEvenIfItIsFailed(githubPullRequest, ghWorkflowRun)) {
      githubPullRequest.setPullRequestStatus(PullRequestStatus.MERGED);
    } else if (isFailedAndCompleted(ghWorkflowRun)) {
      githubPullRequest.setPullRequestStatus(PullRequestStatus.FAILED);
    } else if (ghWorkflowRun.getStatus().equals(Status.IN_PROGRESS)) {
      githubPullRequest.setPullRequestStatus(PullRequestStatus.RUNNING);
    } else if (ghWorkflowRun.getStatus().equals(Status.COMPLETED) && ghWorkflowRun.getConclusion().equals(Conclusion.CANCELLED)) {
      githubPullRequest.setPullRequestStatus(PullRequestStatus.CANCELLED);
    } else {
      githubPullRequest.setPullRequestStatus(PullRequestStatus.SUCCESS);
    }
  }

  private boolean isFailedAndCompleted(GHWorkflowRun ghWorkflowRun) {
    return ghWorkflowRun.getStatus().equals(Status.COMPLETED) && ghWorkflowRun.getConclusion().equals(Conclusion.FAILURE);
  }

  private boolean isMergedEvenIfItIsFailed(GithubPullRequest githubPullRequest, GHWorkflowRun ghWorkflowRun) {
    return ghWorkflowRun.getStatus().equals(Status.COMPLETED) && ghWorkflowRun.getConclusion().equals(Conclusion.FAILURE) && githubPullRequest.getMerged();
  }

  private boolean isClosedWithoutMerging(GHPullRequest pr) {
    try {
      return !pr.isMerged() && pr.getState().equals(GHIssueState.CLOSED);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
