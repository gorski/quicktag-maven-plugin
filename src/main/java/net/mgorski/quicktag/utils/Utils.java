package net.mgorski.quicktag.utils;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Util class.
 *
 * @author Marcin Gorski (mgorski.net)
 */
public class Utils {

  /**
   * Default format: yyyy-MM-dd HH:mm
   *
   * @return formatted date
   */
  public static String getTimestampString() {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    return format.format(new Date());
  }


  /**
   * Extracts version from the output of the SVN xml format.
   *
   * @param output output tof the svn version --xml
   * @return version number or null
   */
  public static String extractVersionFromSvnOutput(String output) {

    if (output == null || output.length() < 24) {
      return null;
    }

    int versionIndex = output.lastIndexOf("revision=") + 10;
    if (versionIndex > 10) {
      return output.substring(versionIndex, output.indexOf("\"", versionIndex));
    }
    return null;
  }
}
