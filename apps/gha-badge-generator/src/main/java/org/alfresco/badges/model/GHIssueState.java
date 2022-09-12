package org.alfresco.badges.model;

public enum GHIssueState {
  OPEN("open"),
  CLOSED("closed"),
  ALL("all");

  private final String value;

  GHIssueState(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return this.value;
  }

  public String value() {
    return this.value;
  }
}
