/*
 * Copyright (c) 2011, Bernd Haug <haug@berndhaug.net>.
 */

package net.mgorski.quicktag.api;

import org.apache.maven.plugin.logging.Log;

/**
 * API that writes some
 *
 * @author bhaug
 * @since 2.0.0
 */
public interface BuildInfoEmitter {

  /**
   * @param log             The maven log of the plugin's mojo.
   * @param buildServerInfo Information read from an adapter that reads information provided by a build server.
   *                        <code>null</code> if unavailable.
   * @param vcsInfo         Information from an adapter that reads information from a VCS, if in use. <code>null</code>
   *                        if unavailable.
   * @param mavenInfo       Information received from the maven invocation as part of which the module runs.
   * @param quicktagInfo    Some information that quicktag creates by itself.
   */
  void writeBuildInformation(Log log, BuildServerBuildInfo buildServerInfo, VcsBuildInfo vcsInfo,
                             MavenBuildInfo mavenInfo, SelfGeneratedBuildInfo quicktagInfo);
}
