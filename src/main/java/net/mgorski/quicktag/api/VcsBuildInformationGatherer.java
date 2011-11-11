/*
 * Copyright (c) 2011, Bernd Haug <haug@berndhaug.net>.
 */

package net.mgorski.quicktag.api;

import org.apache.maven.plugin.logging.Log;

/**
 * API to generate VCS information for the build info generator.
 *
 * @author bhaug
 * @since 2.0.0
 */
public interface VcsBuildInformationGatherer {

  /**
   * @return Information that was collected from a version control system.
   * @param log The maven log of the plugin's mojo.
   */
  VcsBuildInfo gatherVcsBuildInfo(Log log);
}
