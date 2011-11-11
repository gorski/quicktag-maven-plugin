/*
 * Copyright (c) 2011, Bernd Haug <haug@berndhaug.net>.
 */

package net.mgorski.quicktag.api;

import org.apache.maven.plugin.logging.Log;

/**
 * API to generate build server information for the build info generator.
 *
 * @author bhaug
 * @since 2.0.0
 */
public interface BuildServerBuildInformationGatherer {

  /**
   * @return Information gathered from the build server that we run in.
   * @param log The maven log of the plugin's mojo.
   */
  BuildServerBuildInfo gatherBuildServerInfo(Log log);
}
