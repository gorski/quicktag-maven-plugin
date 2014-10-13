package net.mgorski.quicktag.buildserver.teamcity;

import net.mgorski.quicktag.api.BuildServerBuildInfo;
import net.mgorski.quicktag.api.BuildServerBuildInformationGatherer;
import org.apache.maven.plugin.logging.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Gathers build information from TeamCity
 *
 * @author malachid
 * @since 2.1.4
 */
public class TeamCityBuildInfoGatherer  implements BuildServerBuildInformationGatherer
{
    private static final String DEFAULT = "N/A";
    private static final String FMT_DATE = "yyyy-MM-DD_hh-mm-ss";
    private String buildId, buildPlan, buildTime;

    public TeamCityBuildInfoGatherer(Properties environment)
    {
        buildId = environment.getProperty("BUILD_NUMBER", DEFAULT);
        buildPlan = environment.getProperty("TEAMCITY_BUILDCONF_NAME", DEFAULT);
        SimpleDateFormat sdf = new SimpleDateFormat(FMT_DATE);
        buildTime = sdf.format(new Date());
    }

    public boolean isTeamCityInUse()
    {
        if(DEFAULT.equals(buildId)) return false;
        if(DEFAULT.equals(buildPlan)) return false;
        if(DEFAULT.equals(buildTime)) return false;
        return true;
    }

    /**
     * @param log The maven log of the plugin's mojo.
     *
     * @return Information gathered from TeamCity.
     */
    @Override
    public BuildServerBuildInfo gatherBuildServerInfo(Log log) {
        return new BuildServerBuildInfo(buildPlan, buildId, buildTime);
    }

}
