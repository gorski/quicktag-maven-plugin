/*
 * Copyright (c) 2011, Bernd Haug <haug@berndhaug.net>.
 */

package net.mgorski.quicktag.api;

import org.apache.maven.plugin.logging.Log;

/**
 * Generates
 *
 * @author bhaug
 * @since 2.0.0
 */
public interface SelfGeneratedBuildInfoGatherer {

  /**
   * @return Build information generated by quicktag itself.
   * @param log The maven log of the plugin's mojo.
   */
  SelfGeneratedBuildInfo gatherQuicktagBuildInfo(Log log);
}