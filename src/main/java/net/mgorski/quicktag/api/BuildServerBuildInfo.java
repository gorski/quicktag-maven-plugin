/*
 * Copyright (c) 2011, Bernd Haug <haug@berndhaug.net>.
 */

package net.mgorski.quicktag.api;

/**
 * Represents build information contributed by a build server.
 *
 * @author bhaug
 * @since 2.0.0
 */
public class BuildServerBuildInfo {
  private final String buildPlan;
  private final String buildId;
  private final String serverBuildTime;

  /**
   *
   * @param buildPlan The plan on which this build was generated.
   * @param buildId A unique identification of the present build as presented by the build server.
   * @param serverBuildTime The build time, as reported by the build server.
   */
  public BuildServerBuildInfo(String buildPlan, String buildId, String serverBuildTime) {
    this.buildPlan = buildPlan;
    this.buildId = buildId;
    this.serverBuildTime = serverBuildTime;
  }

  public String getBuildPlan() {
    return buildPlan;
  }

  public String getBuildId() {
    return buildId;
  }

  public String getServerBuildTime() {
    return serverBuildTime;
  }
}
