package net.mgorski.quicktag;


import net.mgorski.quicktag.api.*;
import net.mgorski.quicktag.buildserver.atlassianbamboo.AtlassianBambooBuildInfoGatherer;
import net.mgorski.quicktag.chaining.ChainingBuildInfoEmitter;
import net.mgorski.quicktag.chaining.ChainingBuildServerGatherer;
import net.mgorski.quicktag.emission.VersionClassBasedBuildInfoEmitter;
import net.mgorski.quicktag.utils.Utils;
import net.mgorski.quicktag.vcs.Vcs;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

/**
 * Quicktag Maven Plugin. This plugin provides robust way build info of your git/svn/hg repository inside application.
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

    Vcs activeVcs = Vcs.fromString(this.vcs);
    if (activeVcs == null){
      getLog().error("VCS has not been set. Please set <vcs> parameter to the correct VCS.");
      getLog().error("Currently supported values: SVN, GIT, HG");
      return;
    } else {
      getLog().info(String.format("Maven Quicktag Plugin running with %s version control system.", activeVcs));
    }
    VcsInfoGatherer vcsInfoGatherer = activeVcs.getVcsInfoGatherer();

    if (vcsBinaryPath!= null ) {
      System.out.println("---"+vcsBinaryPath.getClass());
      getLog().debug(String.format("Using %s for vcs binary", vcsBinaryPath));
      vcsInfoGatherer.setVcsBinaryPath(vcsBinaryPath);
    }
    if (vcsRepositoryPath != null){
      getLog().debug(String.format("Using %s for VCS repository path", vcsRepositoryPath));
      vcsInfoGatherer.setVcsPath(vcsRepositoryPath);
    }
    getLog().info("Gathering information from the version control system: "+ vcs);
    VcsBuildInfo vcsInfo = vcsInfoGatherer.gatherVcsBuildInfo(getLog());

    BuildServerBuildInformationGatherer buildServerInfoGatherer = null;
    if (bambooBuildKey != null && bambooBuildNumber != null && bambooBuildTimeStamp != null){
       buildServerInfoGatherer = new ChainingBuildServerGatherer(new
        AtlassianBambooBuildInfoGatherer(bambooBuildKey, bambooBuildNumber, bambooBuildTimeStamp));
    } else {
      getLog().info("Not using build server (Bamboo) configuration.");
    }

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

    BuildServerBuildInfo buildServerInfo = null;
    if (buildServerInfoGatherer  != null){
      getLog().info("Gathering information from build server...");
       buildServerInfo= buildServerInfoGatherer.gatherBuildServerInfo(getLog());
    }

    getLog().info("Now writing build information...");
    emitter.writeBuildInformation(getLog(), buildServerInfo, vcsInfo, mavenInfo, quicktagInfo);

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
   * in the build plan's configuration: <code>-Dbamboo.buildKey=${bamboo.buildKey}</code>
   *
   * @parameter expression="${bamboo.buildKey}" default=null
   */
  private String bambooBuildKey;

  /**
   * The Atlassian Bamboo build number. This does not get set automatically; rather, you have to put this in your maven
   * goal in the build plan's configuration: <code>-Dbamboo.buildNumber=${bamboo.buildNumber}</code>
   *
   * @parameter expression="${bamboo.buildNumber}" default=null
   */
  private String bambooBuildNumber;

  /**
   * The Atlassian Bamboo build timestamp. This does not get set automatically; rather, you have to put this in your
   * maven goal in the build plan's configuration: <code>-Dbamboo.buildTimeStamp=${bamboo.buildTimeStamp}</code>
   *
   * @parameter expression="${bamboo.buildTimeStamp}" default=null
   */
  private String bambooBuildTimeStamp;

  // ----------------------------------------------------------------------------------------------------------------
  // ----------------------------------------------------------------------------------------------------------------
  // used by the vcs info gatherer

  /**
   * Path to the binary of the active VCS. <code>null</code> if left default.
   *
   * @parameter expression="${vcs.path}" default-value=""
   */
  @SuppressWarnings({"UnusedDeclaration"})
  private String vcsRepositoryPath;

  /**
   * Path to the binary of the active VCS. <code>null</code> if left default.
   *
   * @parameter expression="${vcs.binary}" default-value=""
   */
  @SuppressWarnings({"UnusedDeclaration"})
  private String vcsBinaryPath;

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


  /**
   * Active VCS (default: git).
   * Specifies implicitly which VCS to use.
   * <p>
   * Available choices: <code>git</code>, <code>svn</code>, <code>hg</code>
   *
   * @parameter expression="${vcs}" default-value="git" 
   */
  private String vcs;

  // ----------------------------------------------------------------------------------------------------------------
  // ----------------------------------------------------------------------------------------------------------------
  // used by the quicktag info gatherer

  /**
   * Build time - set once and the same for the whole plugin run.
   */
  private String buildTime;
}
