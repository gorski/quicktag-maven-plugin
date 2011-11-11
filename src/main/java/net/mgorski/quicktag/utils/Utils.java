package net.mgorski.quicktag.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;


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
   * Builds a command line for the VCS's binary befitting the current OS.
   *
   * @param vcsBinaryName The name of the VCS binary to call.
   *
   * @return array of the arguments for git describe commands
   */
  public static List<String> buildPlatformCommandLine(String vcsBinaryName) {
    final String osName = osName();
    if (osName != null && osName.toLowerCase().contains("windows")) {
      return Arrays.asList("cmd", "/c", vcsBinaryName);
    }
    return Arrays.asList(vcsBinaryName);
  }

  /**
   * @return The system property <code>os.name</code> in lower case.
   */
  public static String osName() {
    return System.getProperty("os.name").toLowerCase();
  }

  /**
   * Executes a command and returns the [standard] output of the execution as a string.
   *
   * @param commandLine The command line to run.
   *
   * @return The command's [standard] output, as String.
   * @throws IOException If the process could not be executed or its output could not be read.
   */
  public static String execute(List<String> commandLine) throws IOException {
    Runtime runtime = Runtime.getRuntime();
    Process process;
    process = runtime.exec(commandLine.toArray(new String[commandLine.size()]));

    BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
    String line;

    StringBuilder output = new StringBuilder();

    while ((line = input.readLine()) != null) {
      output.append(line);
    }

    return output.toString();
  }
}
