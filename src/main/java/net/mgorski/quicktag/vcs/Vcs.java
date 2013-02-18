package net.mgorski.quicktag.vcs;

import net.mgorski.quicktag.api.VcsInfoGatherer;
import net.mgorski.quicktag.vcs.git.Git17VcsInfoGatherer;
import net.mgorski.quicktag.vcs.hg.MercurialVcsInfoGatherer;
import net.mgorski.quicktag.vcs.svn.SvnVcsInfoGatherer;

/**
 * Enum for all supported VCSes.
 * @author mgorski
 */
public enum Vcs {
    GIT(new Git17VcsInfoGatherer()),
    SVN(new SvnVcsInfoGatherer()),
    HG(new MercurialVcsInfoGatherer());

  /**
   * Parses string to enum.
   * @param vcsString string to parse
   * @return VCS
   */
  public static Vcs fromString(final String vcsString){
    return Vcs.valueOf(vcsString.toUpperCase());
  }

  private final VcsInfoGatherer vcsInfoGatherer;

  Vcs(VcsInfoGatherer vcsInfoGatherer) {
    this.vcsInfoGatherer = vcsInfoGatherer;
  }

  /**
   * Returns {@link net.mgorski.quicktag.api.VcsInfoGatherer} bounded with this version controll system.
   * @return
   */
  public VcsInfoGatherer getVcsInfoGatherer() {
    return vcsInfoGatherer;
  }
}


