package net.mgorski.quicktag.utils;

import org.apache.maven.plugin.MojoExecutionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * This class encapsulates operating system. So far supported systems are: <ul> <li>Windows</li> <li>Linux (default:
 * bash)</li> <li>Mac OS</li> </ul>
 *
 * @author Marcin Gorski (mgorski.net)
 */
public class OsProxy {

  private final String os;
  private final String gitPath;

  public OsProxy(String gitPath) {
    os = System.getProperty("os.name").toLowerCase();
    this.gitPath = gitPath;
  }

  /**
   * Executes <code>git describe</code> and returns result as the string.
   *
   * @return
   *
   * @throws MojoExecutionException
   */
  public String executeGitDescribe() throws MojoExecutionException {
    List<String> osArgs = buildVcsCommandLine("git");
    List<String> fullArgs = new LinkedList<String>(osArgs);
    fullArgs.addAll(Arrays.asList("--git-dir=" + gitPath, "describe"));
    return execute(fullArgs);
  }

  /**
   * Executes <code>svn info</code> and returns result as the string.
   *
   * @return
   *
   * @throws MojoExecutionException
   */
  public String executeSvnInfo() throws MojoExecutionException {
    List<String> osArgs = buildVcsCommandLine("svn");
    List<String> fullArgs = new LinkedList<String>(osArgs);
    fullArgs.addAll(Arrays.asList("info", "--incremental", "--xml"));
    return execute(fullArgs);
  }

  /**
   * Builds a command line for the VCS's binary befitting the current OS.
   *
   * @param vcsBinaryName The name of the VCS binary to call.
   *
   * @return array of the arguments for git describe commands
   */
  private List<String> buildVcsCommandLine(String vcsBinaryName) {
    List<String> osArgs = Arrays.asList(vcsBinaryName);
    if (os != null) {
      if (os.toLowerCase().contains("windows")) {
        osArgs = Arrays.asList("cmd", "/c", vcsBinaryName);
      }
    }
    return osArgs;
  }

  /**
   * Executes a result and returns result of the execution in the string format.
   *
   * @param commandLine The command line to run.
   *
   * @return The command's output, as String.
   *
   * @throws org.apache.maven.plugin.MojoExecutionException
   *
   */
  private String execute(List<String> commandLine) throws MojoExecutionException {
    Runtime runtime = Runtime.getRuntime();
    Process process;
    try {
      process = runtime.exec(commandLine.toArray(new String[commandLine.size()]));
    } catch (IOException e) {
      throw new MojoExecutionException(String.format("Could not execute command line %s.", commandLine), e);
    }
    BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));

    String line;
    StringBuilder output = new StringBuilder();

    try {
      while ((line = input.readLine()) != null) {
        output.append(line);
      }
    } catch (IOException e) {
      throw new MojoExecutionException(String.format("Could not read output of command line %s.", commandLine), e);
    }
    return output.toString();
  }
}
