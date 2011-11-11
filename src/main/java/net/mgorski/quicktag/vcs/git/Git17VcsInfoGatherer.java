/*
 * Copyright (c) 2011, Bernd Haug <haug@berndhaug.net>.
 */

package net.mgorski.quicktag.vcs.git;

import net.mgorski.quicktag.api.VcsBuildInfo;
import net.mgorski.quicktag.api.VcsBuildInformationGatherer;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static net.mgorski.quicktag.utils.Utils.buildPlatformCommandLine;
import static net.mgorski.quicktag.utils.Utils.execute;

/**
 * Implements the VcsBuildInformationGatherer.
 *
 * @author bhaug
 * @since 2.0.0
 */
public class Git17VcsInfoGatherer implements VcsBuildInformationGatherer {

  private final String gitBinary;
  private final String gitPath;

  public Git17VcsInfoGatherer(String gitBinary, String gitPath) {
    this.gitBinary = gitBinary;
    this.gitPath = gitPath;
  }

  /**
   * @param log The maven log of the plugin's mojo.
   *
   * @return Information that was collected from a version control system.
   */
  @Override
  public VcsBuildInfo gatherVcsBuildInfo(Log log) {
    List<String> osArgs = buildPlatformCommandLine(gitBinary);

    List<String> describeArgs = new LinkedList<String>(osArgs);
    describeArgs.addAll(Arrays.asList("--git-dir=" + gitPath, "describe", "--always", "--dirty"));

    String gitDescribeOutput;
    try {
      gitDescribeOutput = execute(describeArgs);
    } catch (IOException e) {
      return null;
    }

    File workingCopy = new File(gitPath + File.separator + "..");
    String wdPath = null;
    try {
      wdPath = workingCopy.getCanonicalPath();
    } catch (IOException e) {
      return null;
    }

    boolean dirty = false;
    if (gitDescribeOutput.endsWith("-dirty")) {
      dirty = true;
      gitDescribeOutput = gitDescribeOutput.replaceAll("-dirty$", "");
    }

    String branchName = null;
    List<String> showBranchArgs = new LinkedList<String>(osArgs);
    showBranchArgs.addAll(Arrays.asList("--git-dir=" + gitPath, "symbolic-ref", "HEAD"));
    try {
      branchName = execute(showBranchArgs);
      branchName = branchName.split("/")[2];
    } catch (IOException e) {
      // intentionally left blank. if anything goes wrong, we just don't have this, bfd.
    }

    return new VcsBuildInfo(wdPath, dirty, gitDescribeOutput, branchName);
  }
}
