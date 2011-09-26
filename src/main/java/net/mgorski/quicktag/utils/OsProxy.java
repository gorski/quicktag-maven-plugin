package net.mgorski.quicktag.utils;

import org.apache.maven.plugin.MojoExecutionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * This class encapsulates operating system.
 * So far supported systems are:
 * <ul>
 * <li>Windows</li>
 * <li>Linux (default: bash)</li>
 * <li>Mac OS</li>
 * </ul>
 *
 * @author Marcin Gorski (mgorski.net)
 */
public class OsProxy {

  private final String os;
  private final String gitPath;
  private final String svnPath;

  public OsProxy(String gitPath, String svnPath) {
    os = System.getProperty("os.name").toLowerCase();
    this.gitPath = gitPath;
    this.svnPath = svnPath;
//    System.out.println("Operating system:" + os);
  }

  /**
   * Executes <code>git describe</code> and returns result as the string.
   * @return
   * @throws MojoExecutionException
   */
  public String executeGitDescribe() throws MojoExecutionException {
    String[] osArgs = getGitShellOs();
    int len = osArgs.length;
    String[] fullArgs = new String[len + 2];
    for (int i = 0; i < len; i++) {
      fullArgs[i] = osArgs[i];
    }
    fullArgs[len] = "--git-dir=" + gitPath;
    fullArgs[len + 1] = "describe";


    return execute(fullArgs);

  }

  /**
   * Executes <code>svn info</code> and returns result as the string.
   * @return
   * @throws MojoExecutionException
   */
  public String executeSvnInfo() throws MojoExecutionException {

    String[] osArgs = getSvnShellArguments();
    int len = osArgs.length;
    String[] fullArgs = new String[len + 4];
    for (int i = 0; i < len; i++) {
      fullArgs[i] = osArgs[i];
    }

    //svn info --incremental --xml workspace-java/itt
    fullArgs[len] = "info";
    fullArgs[len+1] = "--incremental";
    fullArgs[len+2] = "--xml";
    fullArgs[len+3] = svnPath;

    return execute(fullArgs);

  }

  private void printArgs(String[] args) {
    System.out.print("Execute{");
    for (String arg : args) {
      System.out.print(arg + " ");
    }
    System.out.print("}\n");

  }


  /**
   * Adopts GIT describe command to the underlying operating system.
   *
   * @return array of the arguments for git describe commands
   * @throws MojoExecutionException in case when operating system is not supported.
   */
  private String[] getGitShellOs() throws MojoExecutionException {

    String[] osArgs = null;
    if (os != null && os.toLowerCase().contains("windows")) {
      osArgs = new String[] { "cmd", "/c","git"};
    } else if (os != null && os.contains("mac")) {
      osArgs = new String[] {"git"};

    } else {
      osArgs = new String[] {"git"};
//      osArgs[0] = "/usr/bin/git";
    }
    return osArgs;

  }


  /**
   * Adopts GIT describe command to the underlying operating system.
   *
   * @return array of the arguments for git describe commands
   * @throws MojoExecutionException in case when operating system is not supported.
   */
  private String[] getSvnShellArguments() throws MojoExecutionException {
    String[] osArgs = null;
    if (os != null && os.toLowerCase().contains("windows")) {
      osArgs = new String[] {"cmd", "/c","svn"};
    } else if (os != null && os.contains("mac")) {
      osArgs = new String[]{"svn"};
    } else {
      osArgs = new String[] {"svn"};
//      osArgs[0] = "/usr/bin/svn";
    }
    return osArgs;
  }


  /**
   * Returns result of the execution in the string format.
   *
   * @return
   */
  private String execute(String[] arguments) {

//    printArgs(arguments);

    Runtime runtime = Runtime.getRuntime();
    Process process = null;
    try {
      process = runtime.exec(arguments);
    } catch (IOException e) {
      e.printStackTrace();
    }
    InputStream is = process.getInputStream();
    InputStreamReader isr = new InputStreamReader(is);
    BufferedReader br = new BufferedReader(isr);
    String line;
    StringBuffer output = new StringBuffer();


    try {
      while ((line = br.readLine()) != null) {
        output.append(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return output.toString();
  }


}
