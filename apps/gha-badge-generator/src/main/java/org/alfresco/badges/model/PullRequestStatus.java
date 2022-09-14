package org.alfresco.badges.model;

import io.github.dsibilio.badgemaker.model.NamedColor;

public enum PullRequestStatus {

  CANCELLED(NamedColor.LIGHTGREY, "Cancelled"),
  UNKNOWN(NamedColor.GREY, "Unknown"),
  FAILED(NamedColor.RED, "Failed"),
  RUNNING(NamedColor.ORANGE, "Running"),
  SUCCESS(NamedColor.BRIGHTGREEN, "Success"),
  MERGED(NamedColor.YELLOW, "Merged with error");

  private final NamedColor color;
  private final String label;

  PullRequestStatus(NamedColor color, String label) {
    this.color = color;
    this.label = label;
  }

  public NamedColor getColor() {
    return color;
  }

  public String getLabel() {
    return label;
  }
  
}
