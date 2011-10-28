package net.mgorski.quicktag;


import net.mgorski.quicktag.beans.HistoryEntry;
import net.mgorski.quicktag.utils.OsProxy;
import net.mgorski.quicktag.utils.Utils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Quicktag Maven Plugin. This plugin provides robust way build info of your git or svn repository inside application.
 * <p/>
 * See projects description at <a href="mgorki.net/projects/quicktag">project's page</a>
 *
 * @author Marcin Gorski (mgorski.net)
 * @goal quicktag
 * @phase generate-sources
 */
public class QuickTagPlugin extends AbstractMojo {

  /**
   * Relative GIT path. Depending on the plugin location (module, submodule, etc.) it might be necessary to provide full
   * path to the repository. Default value: <code>.git</code>
   *
   * @parameter expression="${git.path}" default-value=".git"
   */
  private String gitPath = ".git";

  /**
   * Relative SVN path. Depending on the plugin location (module, submodule, etc.) it might be necessary to provide full
   * path to the repository. Default value: <code>.svn</code>
   *
   * @parameter expression="${svn.path}" default-value=".svn"
   */
  private String svnPath = ".svn";

  /**
   * Output package. The package where the files will be created. For each supported version control system, a separate
   * Java class will be created in this package.
   *
   * @parameter expression="${output.package}"
   * @required
   */
  private String outputPackage;


  /**
   * Output directory. Base directory for the created files - by default it's <code>${project.basedir}/src/main/java/</code>
   * - generated files will be placed inside sources, so they can be used inside your IDE during development. Feel free
   * to write directly to the <code>target/</code> if you prefer to see the files only in the final package.
   *
   * @parameter expression="${project.basedir}/src/main/java/"
   */
  private String outputDirectory;

  /**
   * Project name.
   *
   * @parameter expression="${project.name}"
   * @required
   */
  private String projectName;

  /**
   * Project version.
   *
   * @parameter expression="${project.version}"
   * @required
   */
  private String projectVersion;


  /**
   * Artifact ID.
   *
   * @parameter expression="${project.artifactId}"
   * @required
   */
  private String projectFinalName;


  /**
   * Build time - set once and the same for the whole plugin run.
   */
  private String buildTime;


  /**
   * Main method, called when plugin is being executed.
   *
   * @throws MojoExecutionException
   */
  public void execute() throws MojoExecutionException {
    getLog().info("QuickTag plugin is running.");

    buildTime = Utils.getTimestampString();
    getLog().info("Build timestamp : " + buildTime);

    OsProxy osBridge = new OsProxy(gitPath);
    // try GIT
    try {
      processGitRepository(osBridge);
    } catch (MojoExecutionException e) {
      getLog().error("Error: " + e.getMessage());
    }
    // try SVN
    try {
      processSvnRepository(osBridge);
    } catch (MojoExecutionException e) {
      getLog().error("Error: " + e.getMessage());
    }
    getLog().debug("Quicktag plugin finished execution.");
  }

  private void processGitRepository(OsProxy osBridge) throws MojoExecutionException {
    final String gitString = osBridge.executeGitDescribe();
    if (gitString != null && !gitString.isEmpty()) {
      HistoryEntry history = new HistoryEntry();
      history.setBuildTime(buildTime);
      history.setDescribeString(gitString);
      createOutputVersionFile(history, "GitVersion");
      getLog().info("GIT (" + gitPath + "): Success, version: " + history.getDescribeString());
    } else {
      getLog().info("GIT (" + gitPath + "): Does not exist or cannot be described.");
    }
  }

  private void processSvnRepository(OsProxy osBridge) throws MojoExecutionException {
    final String svnString = osBridge.executeSvnInfo();
    if (svnString != null && !svnString.isEmpty()) {
      String versionNo = Utils.extractVersionFromSvnOutput(svnString);
      if (versionNo != null) {
        HistoryEntry history = new HistoryEntry();
        history.setBuildTime(buildTime);
        history.setDescribeString(versionNo);
        createOutputVersionFile(history, "SvnVersion");
        getLog().info("SVN (" + svnPath + "): Success, version: " + history.getDescribeString());
        return;
      }
    }
    getLog().info("SVN (" + svnPath + "): Does not exist or cannot retrieve info.");
  }

  /**
   * Creates output file.
   *
   * @param history
   * @param className
   */
  private void createOutputVersionFile(HistoryEntry history, String className) {
    final String outputDir = outputDirectory.endsWith("/") ? outputDirectory : outputDirectory + File.separator;
    final String packageAsPathName = outputDir + outputPackage.replace(".", File.separator);
    final File packagePath = new File(packageAsPathName);
    packagePath.mkdirs();
    final String outputPath = packageAsPathName + File.separator + className + ".java";

    getLog().info("Creating file: " + outputPath);
    File outFile = new File(outputPath);
    if (!outFile.exists()) {
      try {
        outFile.createNewFile();
      } catch (IOException e) {
        getLog().error("Cannot create file: " + e.getMessage());
      }
    }

    if (outFile.exists() && outFile.canWrite()) {
      String output = createOutputClassContent(className, outputPackage, history);
      try {
        FileWriter fileWriter = new FileWriter(outFile);
        BufferedWriter out = new BufferedWriter(fileWriter);
        out.write(output);
        out.close();
      } catch (Exception e) {
        getLog().error("Error: " + e.getMessage());
      }
    } else {
      getLog().error("File or location not writable: " + outputPath);
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
    sb.append(projectName);
    sb.append("\";");

    sb.append("\n  public static String PROJECT_ARTIFACT = \"");
    sb.append(projectFinalName);
    sb.append("\";");

    sb.append("\n  public static String PROJECT_VERSION = \"");
    sb.append(projectVersion);
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
}
