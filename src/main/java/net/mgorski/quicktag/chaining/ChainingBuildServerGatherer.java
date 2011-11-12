/*
 * Copyright (c) 2011, Bernd Haug <haug@berndhaug.net>.
 */

package net.mgorski.quicktag.chaining;

import net.mgorski.quicktag.api.BuildServerBuildInfo;
import net.mgorski.quicktag.api.BuildServerBuildInformationGatherer;
import org.apache.maven.plugin.logging.Log;

import java.util.Arrays;
import java.util.List;

/**
 * VCS-based build information gatherer that can be constructed with different {@link net.mgorski.quicktag.api
 * .VcsInfoGatherer} instances, which it uses to actually gather build information.
 *
 * @author bhaug
 * @since 2.0.0
 */
public class ChainingBuildServerGatherer implements BuildServerBuildInformationGatherer {

  private final List<BuildServerBuildInformationGatherer> gatherers;

  /**
   * @param gatherers The gatherers to call, in turn.
   */
  public ChainingBuildServerGatherer(BuildServerBuildInformationGatherer... gatherers) {
    this.gatherers = Arrays.asList(gatherers);
  }

  /**
   * @param gatherers The gatherers to call, in turn.
   */
  public ChainingBuildServerGatherer(List<BuildServerBuildInformationGatherer> gatherers) {
    this.gatherers = gatherers;
  }

  /**
   * @return The result of the first gatherer that is non-<code>null</code>.
   * @param log
   */
  @Override
  public BuildServerBuildInfo gatherBuildServerInfo(Log log) {
    BuildServerBuildInfo result;
    for (BuildServerBuildInformationGatherer gatherer : gatherers) {
      result = gatherer.gatherBuildServerInfo(log);
      if (result != null) {
        return result;
      }
    }
    return null;
  }
}
