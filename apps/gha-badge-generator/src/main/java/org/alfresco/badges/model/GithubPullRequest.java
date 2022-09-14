package org.alfresco.badges.model;

public class GithubPullRequest {

  private String title;
  private String description;
  private String url;
  private PullRequestStatus pullRequestStatus;
  private Boolean merged;

  public GithubPullRequest(String title, String description, String url, PullRequestStatus pullRequestStatus) {
    this.title = title;
    this.description = description;
    this.url = url;
    this.pullRequestStatus = pullRequestStatus;
  }

  public GithubPullRequest() {

  }

  public static final GithubPullRequest emptyRequest(){
    return new GithubPullRequest("Undefined", "Undefined", "https://www.github.com", PullRequestStatus.UNKNOWN);
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String name) {
    this.title = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public PullRequestStatus getPullRequestStatus() {
    return pullRequestStatus;
  }

  public void setPullRequestStatus(PullRequestStatus pullRequestStatus) {
    this.pullRequestStatus = pullRequestStatus;
  }

  public String getURL() {
    return url;
  }

  public void setURL(String url) {
    this.url = url;
  }

  public Boolean getMerged() {
    return merged;
  }

  public void setMerged(Boolean merged) {
    this.merged = merged;
  }
}
