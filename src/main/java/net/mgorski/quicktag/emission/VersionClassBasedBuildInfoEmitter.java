/*
 * Copyright (c) 2011, Bernd Haug <haug@berndhaug.net>.
 */

package net.mgorski.quicktag.emission;

import net.mgorski.quicktag.api.BuildInfoEmitter;
import net.mgorski.quicktag.api.BuildServerBuildInfo;
import net.mgorski.quicktag.api.MavenBuildInfo;
import net.mgorski.quicktag.api.SelfGeneratedBuildInfo;
import net.mgorski.quicktag.api.VcsBuildInfo;
import org.apache.maven.plugin.logging.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Emits the gathered build information to a "Version" class.
 *
 * @author bhaug
 * @since 2.0.0
 */
public class VersionClassBasedBuildInfoEmitter implements BuildInfoEmitter {

  private final String outputPackage;
  private final String outputDirectory;
  private final String className;
  private static final String NA = "N/A";

  public VersionClassBasedBuildInfoEmitter(String outputPackage, String outputDirectory, String versionClassName) {
    this.outputPackage = outputPackage;
    this.outputDirectory = outputDirectory;
    className = versionClassName;
  }

  /**
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
    final String outputDir = outputDirectory.endsWith("/") ? outputDirectory : outputDirectory + File.separator;
    final String packageAsPathName = outputDir + outputPackage.replace(".", File.separator);
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
      Boolean sourceDirty = vcsInfo.getWorkingCopyDirty();
      String sourceDirtyString = "null";
      if (sourceDirty != null) {
        sourceDirtyString = sourceDirty ? "true" : "false";
      }

      String bannerDirtyString = "";
      if (Boolean.TRUE.equals(sourceDirty)) {
        bannerDirtyString = " (dirty-wd) ";
      }

      final String detailed_banner = String.format("Build info: %s version %s built on %s source version %s, build id: %s-%s.%s",
          mavenInfo.getName(), mavenInfo.getVersion(), quicktagInfo.getDate(), vcsInfo.getVersion(),
          buildServerInfo == null ? NA :buildServerInfo.getBuildPlan() ,
          buildServerInfo == null ? NA : buildServerInfo.getBuildId(),
          bannerDirtyString);

      final String banner = String.format("%s-%s %s ",
        mavenInfo.getVersion(),
        Boolean.TRUE.equals(sourceDirty) ? vcsInfo.getVersion() +"**" : vcsInfo.getVersion(),
        quicktagInfo.getDate()
      );
      
      String output = String.format(
          "package %s;\n" +
              "\n" +
              "/**\n" +
              " * Auto-generated file, contains project and version control state info.\n" +
              " */\n" +
              "public final class %s {\n" +
              "  public static String PLUGIN_BUILD_TIME = \"%s\";\n" +
              "\n" +
              "  public static String PROJECT_NAME = \"%s\";\n" +
              "  public static String PROJECT_GROUP = \"%s\";\n" +
              "  public static String PROJECT_ARTIFACT = \"%s\";\n" +
              "  public static String PROJECT_VERSION = \"%s\";" +
              "\n" +
              "  public static String SOURCE_VERSION = \"%s\";\n" +
              "  public static String SOURCE_BRANCH = \"%s\";\n" +
              "  public static Boolean SOURCE_DIRTY = %s;\n" +
              "  public static String SOURCE_WORKING_COPY = \"%s\";\n" +
              "\n" +
              "  public static String SERVER_BUILD_PLAN = \"%s\";\n" +
              "  public static String SERVER_BUILD_ID = \"%s\";\n" +
              "  public static String SERVER_BUILD_TIME = \"%s\";\n" +
              "\n" +
              "  public static String BANNER_FULL = \"%s\";\n" +
              "  public static String BANNER = \"%s\";\n" +
              "}\n",
          outputPackage, className,
          quicktagInfo.getDate(),
          mavenInfo.getName(), mavenInfo.getGroupId(), mavenInfo.getArtifactId(), mavenInfo.getVersion(),
          vcsInfo.getVersion(), vcsInfo.getBranch(), sourceDirtyString, vcsInfo.getWorkingCopyInformation(),
          buildServerInfo == null ? NA : buildServerInfo.getBuildPlan(),
          buildServerInfo == null ? NA : buildServerInfo.getBuildId(),
          buildServerInfo == null ? NA : buildServerInfo.getServerBuildTime(),
          detailed_banner,
          banner
      );
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
}
