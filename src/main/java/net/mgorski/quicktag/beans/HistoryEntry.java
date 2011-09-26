package net.mgorski.quicktag.beans;

/**
 * Represents one history entry (build time + description).
 * @author Marcin Gorski (mgorski.net)
 */
public class HistoryEntry {

  private String buildTime;
  private String describeString;

  public String getBuildTime() {
    return buildTime;
  }

  public void setBuildTime(String buildTime) {
    this.buildTime = buildTime;
  }

  public String getDescribeString() {
    return describeString;
  }

  public void setDescribeString(String describeString) {
    this.describeString = describeString;
  }

  @Override
  public String toString() {
    return "HistoryEntry{" +
      "buildTime='" + buildTime + '\'' +
      ", describeString='" + describeString + '\'' +
      '}';
  }
}
        