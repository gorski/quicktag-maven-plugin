/*
 * Copyright (c) 2011, Bernd Haug <haug@berndhaug.net>.
 */

package net.mgorski.quicktag.api;

/**
 * Represents build information received from maven.
 *
 * @author bhaug
 * @since 2.0.0
 */
public class MavenBuildInfo {
  private final String name;
  private final String groupId;
  private final String artifactId;
  private final String version;

  /**
   * @param name       The name of the project.
   * @param groupId    The maven group ID of the artifact building which we are running.
   * @param artifactId The maven artifact ID of the artifact building which we are running.
   * @param version    The maven version of the artifact building which we are running.
   */
  public MavenBuildInfo(String name, String groupId, String artifactId, String version) {
    this.name = name;
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version;
  }

  public String getName() {
    return name;
  }

  public String getGroupId() {
    return groupId;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public String getVersion() {
    return version;
  }
}
