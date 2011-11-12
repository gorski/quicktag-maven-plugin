/*
 * Copyright (c) 2011, Bernd Haug <haug@berndhaug.net>.
 */

package net.mgorski.quicktag.api;

import org.apache.maven.plugin.logging.Log;

/**
 * API to generate VCS information for the build info generator.
 *
 * @author bhaug
 * @author mgorski
 * @since 2.0.0
 */
public interface VcsInfoGatherer {

  /**
   * @return Information that was collected from a version control system.
   * @param log The maven log of the plugin's mojo.
   */
  VcsBuildInfo gatherVcsBuildInfo(Log log);

  /**
   * Allows VCS binary to be set
   * @param vcsBinary path to VCS binary
   */
  void setVcsBinaryPath(String vcsBinary);

  /**
   * Allows VCS path to be set.
   * @param vcsPath new value of the path
   */
  void setVcsPath(String vcsPath);
}
