/*
 * Copyright (c) 2011, Bernd Haug <haug@berndhaug.net>.
 */

package net.mgorski.quicktag.api;

/**
 * Represents build information contributed by a version control system.
 *
 * @author bhaug
 * @since 2.0.0
 */
public class VcsBuildInfo {
  private final String workingCopyInformation;
  private final Boolean workingCopyDirty;
  private final String version;
  private final String branch;

  /**
   * All of the following information may be unavailable from any given VCS. Where information is unavailable, set the
   * parameter to <code>null</code>.
   *
   * @param workingCopyInformation An identification of the source code working copy that we are building from, e.g. a
   *                               full file system path.
   * @param workingCopyDirty       Whether the working copy is in a state that can be exactly checked out from a source
   *                               code repository.
   * @param version                The version of the source code that we are building, according to the VCS.
   * @param branch                 The VCS branch that we are on (optional)
   */
  public VcsBuildInfo(String workingCopyInformation, Boolean workingCopyDirty, String version, String branch) {
    this.workingCopyInformation = workingCopyInformation;
    this.workingCopyDirty = workingCopyDirty;
    this.version = version;
    this.branch = branch;
  }

  public String getWorkingCopyInformation() {
    return workingCopyInformation;
  }

  public Boolean getWorkingCopyDirty() {
    return workingCopyDirty;
  }

  public String getVersion() {
    return version;
  }

  public String getBranch() {
    return branch;
  }
}
