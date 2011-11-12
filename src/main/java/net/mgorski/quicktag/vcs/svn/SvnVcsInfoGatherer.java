/*
 * Copyright (c) 2011, Bernd Haug <haug@berndhaug.net>.
 */

package net.mgorski.quicktag.vcs.svn;

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
 * Implementation of the {@link net.mgorski.quicktag.api.VcsInfoGatherer} for SVN version control system.
 * <p/>
 * TODO(mgorski): SVN branch info
 *
 * @author bhaug
 * @autho mgorski
 * @since 2.1.0
 */
public class SvnVcsInfoGatherer implements VcsInfoGatherer {

  private String svnBinaryPath;
  private String svnPath;

  public SvnVcsInfoGatherer() {
    svnBinaryPath = "svn";
    svnPath = ".svn";
  }

  /**
   * @param log The maven log of the plugin's mojo.
   * @return Information that was collected from a version control system.
   */
  @Override
  public VcsBuildInfo gatherVcsBuildInfo(Log log) {
    List<String> osArgs = buildPlatformCommandLine(svnBinaryPath);

    List<String> infoArgs = new LinkedList<String>(osArgs);
    infoArgs.addAll(Arrays.asList("info", "--incremental", "--xml"));

    String infoOutput;
    try {
      infoOutput = extractVersionFromSvnXmlOutput(execute(infoArgs));
    } catch (IOException e) {
      return null;
    }

    File workingCopy = new File(svnPath + File.separator + "..");
    String wdPath = null;
    try {
      wdPath = workingCopy.getCanonicalPath();
      // for Windows systems - escape backslashes
      wdPath = wdPath.replace("\\", "/");
    } catch (IOException e) {
      return null;
    }
    return new VcsBuildInfo(wdPath, null, infoOutput, null);
  }


  /**
   * Extracts version from the output of the SVN xml format.
   * TODO(mgorski): refactor/simplify ...
   * @param output output of the svn vcs
   * @return version number or null
   */
  private String extractVersionFromSvnXmlOutput(String output) {
    if (output == null || output.length() < 24) {
      return null;
    }
    int versionIndex = output.lastIndexOf("revision=") + 10;
    if (versionIndex > 10) {
      return output.substring(versionIndex, output.indexOf("\"", versionIndex));
    }
    return null;
  }


  @Override
  public void setVcsBinaryPath(String vcsBinary) {
    this.svnBinaryPath = vcsBinary;
  }

  @Override
  public void setVcsPath(String vcsPath) {
    this.svnPath = vcsPath;
  }
}
