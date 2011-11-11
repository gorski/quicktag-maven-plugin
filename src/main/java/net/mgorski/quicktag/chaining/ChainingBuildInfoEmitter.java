/*
 * Copyright (c) 2011, Bernd Haug <haug@berndhaug.net>.
 */

package net.mgorski.quicktag.chaining;

import net.mgorski.quicktag.api.BuildInfoEmitter;
import net.mgorski.quicktag.api.BuildServerBuildInfo;
import net.mgorski.quicktag.api.MavenBuildInfo;
import net.mgorski.quicktag.api.SelfGeneratedBuildInfo;
import net.mgorski.quicktag.api.VcsBuildInfo;
import org.apache.maven.plugin.logging.Log;

import java.util.Arrays;
import java.util.List;

/**
 * Build info emitter that allows calling other emitters to generate different expressions of available build info.
 *
 * @author bhaug
 * @since 2.0.0
 */
public class ChainingBuildInfoEmitter implements BuildInfoEmitter {

  private final List<BuildInfoEmitter> emitters;

  /**
   * @param emitters The emitters to call, in turn.
   */
  public ChainingBuildInfoEmitter(BuildInfoEmitter... emitters) {
    this(Arrays.asList(emitters));
  }

  /**
   * @param emitters The emitters to call, in turn.
   */
  public ChainingBuildInfoEmitter(List<BuildInfoEmitter> emitters) {
    this.emitters = emitters;
  }

  /**
   * Calls all the emitters, in turn, that were passed at construction time. Each is passed the same parameters that
   * this method received.
   *
   * @param log             The maven log of the plugin's mojo.
   * @param buildServerInfo Information read from an adapter that reads information provided by a build server.
   *                        <code>null</code> if unavailable.
   * @param vcsInfo         Information from an adapter that reads information from a VCS, if in use. <code>null</code>
   *                        if unavailable.
   * @param mavenInfo       Information received from the maven invocation as part of which the module runs.
   * @param quicktagInfo    Some information that quicktag creates by itself.
   */
  @Override
  public void writeBuildInformation(Log log, BuildServerBuildInfo buildServerInfo, VcsBuildInfo vcsInfo,
                                    MavenBuildInfo mavenInfo, SelfGeneratedBuildInfo quicktagInfo) {
    for (BuildInfoEmitter emitter : emitters) {
      emitter.writeBuildInformation(log, buildServerInfo, vcsInfo, mavenInfo, quicktagInfo);
    }
  }
}
