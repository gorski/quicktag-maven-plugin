/*
 * Copyright (c) 2011, Bernd Haug <haug@berndhaug.net>.
 */

package net.mgorski.quicktag.vcs.svn;

import net.mgorski.quicktag.api.VcsBuildInfo;
import net.mgorski.quicktag.api.VcsBuildInformationGatherer;
import net.mgorski.quicktag.beans.HistoryEntry;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static net.mgorski.quicktag.utils.Utils.buildPlatformCommandLine;
import static net.mgorski.quicktag.utils.Utils.execute;

/**
 * This should become a vcs build information gatherer working with subversion as its back-end. Right now this is where
 * bhaug put all the crap he needed to get out of his face to continue reworking quicktag. Bernd Haug would like to
 * apologize abjectly for your inconvenience.
 * <p/>
 * TODO(bhaug):  Make whole SvnBuildInfoGatherer
 *
 * @author bhaug
 * @since 2.0.0
 */
public class SvnBuildInfoGatherer implements VcsBuildInformationGatherer {
  /**
   * @param log The maven log of the plugin's mojo.
   *
   * @return Information that was collected from a version control system.
   */
  @Override
  public VcsBuildInfo gatherVcsBuildInfo(Log log) {
    // OsProxy osBridge = new OsProxy(""); - osproxy being a class being initialized with the path to vcs
    try {
      processSvnRepository(log); // second arg used to be an osproxy
    } catch (MojoExecutionException e) {
      log.error("Error: " + e.getMessage());
    }
    return null;
  }

  private void processSvnRepository(Log log) throws MojoExecutionException {
    final String svnString = executeSvnInfo();
    if (svnString != null && !svnString.isEmpty()) {
      String versionNo = extractVersionFromSvnOutput(svnString);
      if (versionNo != null) {
        HistoryEntry history = new HistoryEntry();
        history.setBuildTime("buildTime");
        history.setDescribeString(versionNo);
        createOutputVersionFile(log, history, "SvnVersion");
        log.info("SVN (" + "svnPath" + "): Success, version: " + history.getDescribeString());
        return;
      }
    }
    log.info("SVN (" + "gitPath" + "): Does not exist or cannot retrieve info.");
  }

  /**
   * Creates output file.
   *
   * @param history
   * @param className
   */
  private void createOutputVersionFile(Log log, HistoryEntry history, String className) {
    final String outputDir = "outputDirectory".endsWith("/") ? "outputDirectory" : "outputDirectory" + File.separator;
    final String packageAsPathName = outputDir + "outputPackage".replace(".", File.separator);
    final File packagePath = new File(packageAsPathName);
    packagePath.mkdirs();
    final String outputPath = packageAsPathName + File.separator + className + ".java";

    log.info("Creating file: " + outputPath);
    File outFile = new File(outputPath);
    if (!outFile.exists()) {
      try {
        outFile.createNewFile();
      } catch (IOException e) {
        log.error("Cannot create file: " + e.getMessage());
      }
    }

    if (outFile.exists() && outFile.canWrite()) {
      String output = createOutputClassContent(className, "outputPackage", history);
      try {
        FileWriter fileWriter = new FileWriter(outFile);
        BufferedWriter out = new BufferedWriter(fileWriter);
        out.write(output);
        out.close();
      } catch (Exception e) {
        log.error("Error: " + e.getMessage());
      }
    } else {
      log.error("File or location not writable: " + outputPath);
    }
  }

  /**
   * Creates output file content.
   *
   * @param className     name of the class (and file)
   * @param outputPackage output package
   * @param historyEntry  bean
   *
   * @return content of the file (unicode string)
   */
  private String createOutputClassContent(String className, String outputPackage, HistoryEntry historyEntry) {

    StringBuffer sb = new StringBuffer();
    sb.append("package ");
    sb.append(outputPackage);
    sb.append(";\n/**");
    sb.append("\n * Auto-generated file, contains project and version control state info.");
    sb.append("\n */");
    sb.append("\npublic class ");
    sb.append(className);
    sb.append(" {");

    sb.append("\n  public static String PROJECT_NAME = \"");
    sb.append("projectName");
    sb.append("\";");

    sb.append("\n  public static String PROJECT_ARTIFACT = \"");
    sb.append("projectArtifactId");
    sb.append("\";");

    sb.append("\n  public static String PROJECT_VERSION = \"");
    sb.append("projectVersion");
    sb.append("\";");

    sb.append("\n  public static String DESCRIBE = \"");
    sb.append(historyEntry.getDescribeString());
    sb.append("\";");

    sb.append("\n  public static String BUILD_TIME = \"");
    sb.append(historyEntry.getBuildTime());
    sb.append("\";");

    sb.append("\n  public static String BANNER = \"");
    sb.append(historyEntry.getDescribeString());
    sb.append(" ");
    sb.append(historyEntry.getBuildTime());
    sb.append("\";");

    sb.append("\n}");
    return sb.toString();
  }

  /**
   * Executes <code>svn info</code> and returns result as the string.
   *
   * @return
   *
   * @throws MojoExecutionException
   */
  public String executeSvnInfo() throws MojoExecutionException {
    List<String> osArgs = buildPlatformCommandLine("svn");
    List<String> fullArgs = new LinkedList<String>(osArgs);
    fullArgs.addAll(Arrays.asList("info", "--incremental", "--xml"));
    try {
      return execute(fullArgs);
    } catch (IOException e) {
      throw new MojoExecutionException("Failed to run svn info and read its output.", e);
    }
  }

  /**
   * Extracts version from the output of the SVN xml format.
   *
   * @param output output tof the svn version --xml
   *
   * @return version number or null
   */
  private String extractVersionFromSvnOutput(String output) {

    if (output == null || output.length() < 24) {
      return null;
    }

    int versionIndex = output.lastIndexOf("revision=") + 10;
    if (versionIndex > 10) {
      return output.substring(versionIndex, output.indexOf("\"", versionIndex));
    }
    return null;
  }
}
