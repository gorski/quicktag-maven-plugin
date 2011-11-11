/*
 * Copyright (c) 2011, Bernd Haug <haug@berndhaug.net>.
 */

package net.mgorski.quicktag.api;

import org.apache.maven.plugin.logging.Log;

/**
 * API to gather maven information for the build info generator.
 *
 * @author bhaug
 * @since 2.0.0
 */
public interface MavenBuildInformationGatherer {

  /**
   * @return Information gathered from the build server that we run in.
   * @param log The maven log of the plugin's mojo.
   */
  MavenBuildInfo gatherMavenBuildInfo(Log log);
}
