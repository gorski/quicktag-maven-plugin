# quicktag-maven-plugin
Plugin allows you easily embed status of your repository into the project while building with Maven. In the process-resources phase plugin will simply generate Java class with static String fields that describe build time, version control state (and build server info, if present). This will enable you to easily distinguish which version of the project is currently deployed.


![alt tag](https://dl.dropboxusercontent.com/u/3118248/quicktag.png)

## Version
Current stable version: 2.1.5 (10.06.2014).

Currently supported VCS:
* GIT
* Mercurial
* SVN

Currently supported build servers:
* Bamboo
* Jenkins
* Teamcity

Commands called by the plugin: git describe for GIT, svn info for SVN and hg --id for Mercurial. Tested under Mac Os, Windows and Ubuntu/Linux.

## Authors
Marcin Górski mgorski.net
Bernd Haug berndhaug.net
Malachi de Ælfweald linkedin.com/in/malachid
If you have any ideas for improvements or you have noticed a bug feel free to contact us or create an issue!

## Requirements
Project uses Maven
Project is under SVN,GIT,Mercurial control

# Usage
Note: Refer to Configuration Wiki for more configuration options.

1) Add plugin inside plugins section (don't forget to change desired package). Plugin is available from maven central.
```
<plugin>
  <groupId>net.mgorski.quicktag</groupId>
  <artifactId>quicktag</artifactId>
  <version>2.1.4</version>
  <executions>
    <execution>
      <phase>generate-sources</phase>
      <goals>
        <goal>quicktag</goal>
      </goals>
    </execution>
  </executions>
  <configuration>
    <outputPackage>com.yourpackage</outputPackage>
  </configuration>
</plugin>
```
2) Build your project (plugin is called during generate-sources phase. Eventually you can execute it using mvn quicktag:quicktag.

In the generate-resources phase of the build plugin will generate java file called Version.java inside specified package. The contents of the file describes the build and version control status. Here is an example output:
```
package net.mgorski.output;

/**
 * Auto-generated file, contains project and version control state info.
 */
public final class Version {
  public static String PLUGIN_BUILD_TIME = "2013-02-25 23:20";

  public static String PROJECT_NAME = "My project";
  public static String PROJECT_GROUP = "com.foo.project";
  public static String PROJECT_ARTIFACT = "businessapp";
  public static String PROJECT_VERSION = "2.0.1";
  public static String SOURCE_VERSION = "b324bbe";
  public static String SOURCE_BRANCH = "master";
  public static Boolean SOURCE_DIRTY = true;
  public static String SOURCE_WORKING_COPY = "/Users/supercoder/workspace/businessapp";

  public static String SERVER_BUILD_PLAN = "PH Android SDK";
  public static String SERVER_BUILD_ID = "35";
  public static String SERVER_BUILD_TIME = "2014-02-44_11-25-53";

  public static String BANNER_FULL = "Build info: My project version  2.1.4 built on 2013-02-25 23:20 source version b324bbe, build id: N/A-N/A. (dirty-wd) ";
  public static String BANNER = "2.0.1-b324bbe** 2013-02-25 23:20 ";
}
```
4) Use generated file and embed it inside you app. For example you can add this to your JSP template:

```
<%= Version.BANNER %>
```
5) Start your app. Here is how it looks like in sample application:

## License
Quicktag-Maven-Plugin

https://code.google.com/p/quicktag-maven-plugin/
Open Source / Code license GNU GPL v3

2014.