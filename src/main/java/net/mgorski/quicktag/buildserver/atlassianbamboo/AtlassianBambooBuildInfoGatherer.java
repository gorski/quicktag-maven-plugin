/*
 * Copyright (c) 2011, Bernd Haug <haug@berndhaug.net>.
 */

package net.mgorski.quicktag.buildserver.atlassianbamboo;

import net.mgorski.quicktag.api.BuildServerBuildInfo;
import net.mgorski.quicktag.api.BuildServerBuildInformationGatherer;
import org.apache.maven.plugin.logging.Log;

/**
 * Gathers build information from Atlassian Bamboo, with some help from the user (i.e.,
 * setting the necessary variables in the Bamboo build's goals.
 *
 * @author bhaug
 * @since 2.0.0
 */
public class AtlassianBambooBuildInfoGatherer implements BuildServerBuildInformationGatherer {

  private final String bambooBuildPlanName;
  private final String bambooBuildNumber;
  private final String bambooBuildTimeStamp;

  public AtlassianBambooBuildInfoGatherer(String bambooBuildKey, String bambooBuildNumber,
                                          String bambooBuildTimeStamp) {
    this.bambooBuildPlanName = bambooBuildKey.replaceFirst("-\\d+$", "");
    this.bambooBuildNumber = bambooBuildNumber;
    this.bambooBuildTimeStamp = bambooBuildTimeStamp;
  }

  /**
   * @param log The maven log of the plugin's mojo.
   *
   * @return Information gathered from Bamboo.
   */
  @Override
  public BuildServerBuildInfo gatherBuildServerInfo(Log log) {
    return new BuildServerBuildInfo(bambooBuildPlanName, bambooBuildNumber, bambooBuildTimeStamp);
  }
}
