/*
 * Copyright (c) 2011, Bernd Haug <haug@berndhaug.net>.
 */

package net.mgorski.quicktag.api;

/**
 * Represents build information created by this module itself.
 *
 * @author bhaug
 * @since 2.0.0
 */
public class SelfGeneratedBuildInfo {
  private final String date;

  /**
   * @param date The timestamp of the build according to the quicktag module,
   */
  public SelfGeneratedBuildInfo(String date) {
    this.date = date;
  }

  public String getDate() {
    return date;
  }
}
