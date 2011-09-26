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

    int index = output.lastIndexOf("revision=");
    if (index > 0) {
      String sub = output.substring(index + 10);
      sub = sub.substring(0, sub.indexOf("\""));
      return sub;
    }
    return null;
  }
}
