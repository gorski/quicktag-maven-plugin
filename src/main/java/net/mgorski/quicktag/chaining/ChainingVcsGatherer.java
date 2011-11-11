/*
 * Copyright (c) 2011, Bernd Haug <haug@berndhaug.net>.
 */

package net.mgorski.quicktag.chaining;

import net.mgorski.quicktag.api.VcsBuildInfo;
import net.mgorski.quicktag.api.VcsBuildInformationGatherer;
import org.apache.maven.plugin.logging.Log;

import java.util.Arrays;
import java.util.List;

/**
 * VCS-based build information gatherer that can be constructed with different {@link net.mgorski.quicktag.api
 * .VcsBuildInformationGatherer} instances, which it uses to actually gather build information.
 *
 * @author bhaug
 * @since 2.0.0
 */
public class ChainingVcsGatherer implements VcsBuildInformationGatherer {

  private final List<VcsBuildInformationGatherer> gatherers;

  /**
   * @param gatherers The gatherers to call, in turn.
   */
  public ChainingVcsGatherer(VcsBuildInformationGatherer... gatherers) {
    this.gatherers = Arrays.asList(gatherers);
  }

  /**
   * @param gatherers The gatherers to call, in turn.
   */
  public ChainingVcsGatherer(List<VcsBuildInformationGatherer> gatherers) {
    this.gatherers = gatherers;
  }

  /**
   * @return The result of the first gatherer that is non-<code>null</code>.
   * @param log The maven log of the plugin's mojo.
   */
  @Override
  public VcsBuildInfo gatherVcsBuildInfo(Log log) {
    VcsBuildInfo result;
    for (VcsBuildInformationGatherer gatherer : gatherers) {
      result = gatherer.gatherVcsBuildInfo(log);
      if (result != null) {
        return result;
      }
    }
    return null;
  }
}
