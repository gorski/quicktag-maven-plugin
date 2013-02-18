package net.mgorski.quicktag.vcs.hg;

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
 * Implementation of the {@link net.mgorski.quicktag.api.VcsInfoGatherer} for Mercurial version control system.
 *
 * @author malachid
 * @since 2.1.3
 */
public class MercurialVcsInfoGatherer implements VcsInfoGatherer
{
    private String hgBinary;
    private String hgRepositoryPath;

    public MercurialVcsInfoGatherer() {
        hgBinary = "hg";
        /**
         * Note: hg will auto-append ".hg", so hgRepositoryPath has to be the working directory
         */
        hgRepositoryPath = ".";
    }

    /**
     * @param log The maven log of the plugin's mojo.
     *
     * @return Information that was collected from a version control system.
     */
    @Override
    public VcsBuildInfo gatherVcsBuildInfo(Log log) {
        List<String> osArgs = buildPlatformCommandLine(hgBinary);

        List<String> idArgs = new LinkedList<String>(osArgs);
        idArgs.addAll(Arrays.asList("--repository", hgRepositoryPath, "id", "--id", "--branch"));

        String hgIdOutput;
        try{
            hgIdOutput = execute(idArgs);
        } catch (IOException e) {
            System.err.format("Error talking to hg: %s\n", e.getMessage());
            return null;
        }
        System.out.format("hgIdOutput: %s\n", hgIdOutput);

        /**
         * Note: hg will auto-append ".hg", so hgRepositoryPath IS the working directory
         */
        File workingCopy = new File(hgRepositoryPath);
        String wdPath = null;
        try {
            wdPath = workingCopy.getCanonicalPath();
            // for Windows systems - escape backslashes
            wdPath = wdPath.replace("\\","/");
        } catch (IOException e) {
            return null;
        }

        String[] data = hgIdOutput.split("[\\s]+");
        String commit = data[0];
        boolean dirty = false;
        String branchName = data[1];

        if(commit.endsWith("+"))
        {
            dirty = true;
            commit = commit.substring(0, commit.length()-2);
        }

        return new VcsBuildInfo(wdPath, dirty, commit, branchName);
    }

    @Override
    public void setVcsBinaryPath(String vcsBinary) {
        this.hgBinary = vcsBinary;
    }

    @Override
    public void setVcsPath(String vcsPath) {
        this.hgRepositoryPath = vcsPath;
    }
}
