/*
 * Copyright (c) 2011, Bernd Haug <haug@berndhaug.net>.
 */

package net.mgorski.quicktag.vcs.git;

import net.mgorski.quicktag.api.VcsBuildInfo;
import net.mgorski.quicktag.api.VcsInfoGatherer;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static net.mgorski.quicktag.utils.Utils.buildPlatformCommandLine;
import static net.mgorski.quicktag.utils.Utils.execute;

/**
 * Implements the VcsInfoGatherer.
 *
 * @author bhaug
 * @since 2.0.0
 */
public class Git17VcsInfoGatherer implements VcsInfoGatherer {

  private String gitBinary;
  private String gitRepositoryPath;

  public Git17VcsInfoGatherer() {
    gitBinary = "git";
    gitRepositoryPath = ".git";
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
    describeArgs.addAll(Arrays.asList("--git-dir=" + gitRepositoryPath, "describe", "--always", "--dirty"));

    String gitDescribeOutput;
    try {
      gitDescribeOutput = execute(describeArgs);
    } catch (IOException e) {
      return null;
    }

    File workingCopy = new File(gitRepositoryPath + File.separator + "..");
    String wdPath = null;
    try {
      wdPath = workingCopy.getCanonicalPath();
      // for Windows systems - escape backslashes
      wdPath = wdPath.replace("\\","/");
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
    showBranchArgs.addAll(Arrays.asList("--git-dir=" + gitRepositoryPath, "symbolic-ref", "HEAD"));
    try {
      branchName = execute(showBranchArgs);
      branchName = branchName.split("/")[2];
    } catch (IOException e){
      // intentionally left blank. if anything goes wrong, we just don't have this, bfd.  
    } catch (RuntimeException e) {
      // intentionally left blank. if anything goes wrong, we just don't have this, bfd.
    }

    return new VcsBuildInfo(wdPath, dirty, gitDescribeOutput, branchName);
  }


  @Override
  public void setVcsBinaryPath(String vcsBinary) {
    this.gitBinary = vcsBinary;
  }

  @Override
  public void setVcsPath(String vcsPath) {
    this.gitRepositoryPath = vcsPath;
  }
}
