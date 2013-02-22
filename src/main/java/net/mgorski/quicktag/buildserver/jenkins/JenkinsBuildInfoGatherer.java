package net.mgorski.quicktag.buildserver.jenkins;

import net.mgorski.quicktag.api.BuildServerBuildInfo;
import net.mgorski.quicktag.api.BuildServerBuildInformationGatherer;
import org.apache.maven.plugin.logging.Log;

import java.util.Properties;

/**
 * Gathers build information from Jenkins
 *
 * @author malachid
 * @since 2.1.3
 */
public class JenkinsBuildInfoGatherer implements BuildServerBuildInformationGatherer
{
    private static final String DEFAULT = "N/A";
    private String buildId, buildPlan, buildTime;

    public JenkinsBuildInfoGatherer(Properties environment)
    {
        buildId = environment.getProperty("BUILD_NUMBER", DEFAULT);
        buildPlan = environment.getProperty("JOB_NAME", DEFAULT);
        buildTime = environment.getProperty("BUILD_ID", DEFAULT);
    }

    public boolean isJenkinsInUse()
    {
        if(DEFAULT.equals(buildId)) return false;
        if(DEFAULT.equals(buildPlan)) return false;
        if(DEFAULT.equals(buildTime)) return false;
        return true;
    }

    /**
     * @param log The maven log of the plugin's mojo.
     *
     * @return Information gathered from Jenkins.
     */
    @Override
    public BuildServerBuildInfo gatherBuildServerInfo(Log log) {
        return new BuildServerBuildInfo(buildPlan, buildId, buildTime);
    }
}
