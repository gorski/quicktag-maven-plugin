package net.mgorski.quicktag;


import net.mgorski.quicktag.api.BuildInfoEmitter;
import net.mgorski.quicktag.api.BuildServerBuildInfo;
import net.mgorski.quicktag.api.BuildServerBuildInformationGatherer;
import net.mgorski.quicktag.api.MavenBuildInfo;
import net.mgorski.quicktag.api.MavenBuildInformationGatherer;
import net.mgorski.quicktag.api.SelfGeneratedBuildInfo;
import net.mgorski.quicktag.api.SelfGeneratedBuildInfoGatherer;
import net.mgorski.quicktag.api.VcsBuildInfo;
import net.mgorski.quicktag.api.VcsBuildInformationGatherer;
import net.mgorski.quicktag.buildserver.atlassianbamboo.AtlassianBambooBuildInfoGatherer;
import net.mgorski.quicktag.chaining.ChainingBuildInfoEmitter;
import net.mgorski.quicktag.chaining.ChainingBuildServerGatherer;
import net.mgorski.quicktag.chaining.ChainingVcsGatherer;
import net.mgorski.quicktag.emission.VersionClassBasedBuildInfoEmitter;
import net.mgorski.quicktag.utils.Utils;
import net.mgorski.quicktag.vcs.git.Git17VcsInfoGatherer;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

/**
 * Quicktag Maven Plugin. This plugin provides robust way build info of your git or svn repository inside application.
 * <p/>
 * See projects description at <a href="mgorki.net/projects/quicktag">project's page</a>
 *
 * @author Marcin Gorski (mgorski.net)
 * @author bhaug
 * @goal quicktag
 * @phase generate-sources
 */
@SuppressWarnings({"UnusedDeclaration"})
public class QuickTagPlugin extends AbstractMojo
    implements SelfGeneratedBuildInfoGatherer, MavenBuildInformationGatherer {

  /**
   * Main method, called when plugin is being executed.
   *
   * @throws MojoExecutionException
   */
  public void execute() throws MojoExecutionException {
    BuildServerBuildInformationGatherer buildServerInfoGatherer = new ChainingBuildServerGatherer(new
        AtlassianBambooBuildInfoGatherer(bambooBuildPlanName, bambooBuildNumber, bambooBuildTimeStamp));
    VcsBuildInformationGatherer vcsInfoGatherer = new ChainingVcsGatherer(new Git17VcsInfoGatherer(gitBinary, gitPath));
    BuildInfoEmitter emitter =
        new ChainingBuildInfoEmitter(
            new VersionClassBasedBuildInfoEmitter(outputPackage, outputDirectory, versionClassName));

    getLog().info("QuickTag plugin is running.");

    buildTime = Utils.getTimestampString();
    getLog().info("Build timestamp: " + buildTime);

    getLog().info("Gathering information quicktag can create itself...");
    SelfGeneratedBuildInfo quicktagInfo = gatherQuicktagBuildInfo(getLog());
    getLog().info("Gathering information from maven...");
    MavenBuildInfo mavenInfo = gatherMavenBuildInfo(getLog());

    getLog().info("Gathering information from build server...");
    BuildServerBuildInfo buildServerInfo = buildServerInfoGatherer.gatherBuildServerInfo(getLog());
    getLog().info("Gathering information from version control system...");
    VcsBuildInfo vcsInfo = vcsInfoGatherer.gatherVcsBuildInfo(getLog());

    getLog().info("Now writing build information...");
    emitter.writeBuildInformation(getLog(), buildServerInfo, vcsInfo, mavenInfo, quicktagInfo);

    // try SVN
    getLog().debug("Quicktag plugin finished execution.");
  }

  @Override
  public MavenBuildInfo gatherMavenBuildInfo(Log log) {
    return new MavenBuildInfo(projectName, projectGroupId, projectArtifactId, projectVersion);
  }

  @Override
  public SelfGeneratedBuildInfo gatherQuicktagBuildInfo(Log log) {
    return new SelfGeneratedBuildInfo(buildTime);
  }

  // -------------------------------------------------------------------------------------------------------------------
  //   And here the interminable list of things that get imported from Maven, which does this as revoltingly verbosely
  //  and plain wrongly (comments that influence behaviour! WTF! annotations are not exactly new...) as everything else.
  // -------------------------------------------------------------------------------------------------------------------

  // ----------------------------------------------------------------------------------------------------------------
  // ----------------------------------------------------------------------------------------------------------------
  // used by the bamboo build server info gatherer

  /**
   * The Atlassian Bamboo project. This does not get set automatically; rather, you have to put this in your maven goal
   * in the build plan's configuration: <code>-Dbamboo.buildPlanName=${bamboo.buildPlanName}</code>
   *
   * @parameter expression="${bamboo.buildPlanName}"
   */
  private String bambooBuildPlanName;

  /**
   * The Atlassian Bamboo build number. This does not get set automatically; rather, you have to put this in your maven
   * goal in the build plan's configuration: <code>-Dbamboo.buildNumber=${bamboo.buildNumber}</code>
   *
   * @parameter expression="${bamboo.buildNumber}"
   */
  private String bambooBuildNumber;

  /**
   * The Atlassian Bamboo build timestamp. This does not get set automatically; rather, you have to put this in your
   * maven goal in the build plan's configuration: <code>-Dbamboo.buildTimeStamp=${bamboo.buildTimeStamp}</code>
   *
   * @parameter expression="${bamboo.buildTimeStamp}"
   */
  private String bambooBuildTimeStamp;

  // ----------------------------------------------------------------------------------------------------------------
  // ----------------------------------------------------------------------------------------------------------------
  // used by the git vcs info gatherer

  /**
   * Relative GIT path. Depending on the plugin location (module, submodule, etc.) it might be necessary to provide full
   * path to the repository. Default value: <code>.git</code>
   *
   * @parameter expression="${git.path}" default-value=".git"
   */
  @SuppressWarnings({"UnusedDeclaration"})
  private String gitPath;

  /**
   * Path to the git binary. Default value: <code>git</code>
   *
   * @parameter expression="${git.binary}" default-value="git"
   */
  @SuppressWarnings({"UnusedDeclaration"})
  private String gitBinary;

  // ----------------------------------------------------------------------------------------------------------------
  // ----------------------------------------------------------------------------------------------------------------
  // used by the (future) svn vcs info gatherer

  /**
   * Relative SVN path. Depending on the plugin location (module, submodule, etc.) it might be necessary to provide full
   * path to the repository. Default value: <code>.svn</code>
   *
   * @parameter expression="${svn.path}" default-value=".svn"
   */
  @SuppressWarnings({"UnusedDeclaration"})
  private String svnPath;

  // ----------------------------------------------------------------------------------------------------------------
  // ----------------------------------------------------------------------------------------------------------------
  // used by the info emitter

  /**
   * Output package. The package where the files will be created. For each supported version control system, a separate
   * Java class will be created in this package.
   *
   * @parameter expression="${output.package}"
   * @required
   */
  @SuppressWarnings({"UnusedDeclaration"})
  private String outputPackage;

  /**
   * Output directory. Base directory for the created files - by default it's <code>${project.basedir}/src/main/java/</code>
   * - generated files will be placed inside sources, so they can be used inside your IDE during development. Feel free
   * to write directly to the <code>target/</code> if you prefer to see the files only in the final package.
   *
   * @parameter expression="${project.basedir}/src/main/java/"
   */
  @SuppressWarnings({"UnusedDeclaration"})
  private String outputDirectory;

  /**
   * Base name of the class into which version information is written.
   *
   * @parameter default-value="Version"
   */
  @SuppressWarnings({"UnusedDeclaration"})
  private String versionClassName;

  // ----------------------------------------------------------------------------------------------------------------
  // ----------------------------------------------------------------------------------------------------------------
  // used by the maven info gatherer

  /**
   * Project name.
   *
   * @parameter expression="${project.name}"
   * @required
   */
  @SuppressWarnings({"UnusedDeclaration"})
  private String projectName;

  /**
   * Project version.
   *
   * @parameter expression="${project.version}"
   * @required
   */
  @SuppressWarnings({"UnusedDeclaration"})
  private String projectVersion;

  /**
   * Artifact ID.
   *
   * @parameter expression="${project.artifactId}"
   * @required
   */
  @SuppressWarnings({"UnusedDeclaration"})
  private String projectArtifactId;

  /**
   * Group ID.
   *
   * @parameter expression="${project.groupId}"
   * @required
   */
  @SuppressWarnings({"UnusedDeclaration"})
  private String projectGroupId;

  // ----------------------------------------------------------------------------------------------------------------
  // ----------------------------------------------------------------------------------------------------------------
  // used by the quicktag info gatherer

  /**
   * Build time - set once and the same for the whole plugin run.
   */
  private String buildTime;
}
