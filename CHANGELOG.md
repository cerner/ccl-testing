# ccl-testing Change Log

## 2018-07-04

* ccl-maven-plugin **3.0**
* cerreal-maven-plugin **2.0.0**
* whitenoise-maven-plugin **2.0**
* cdoc-maven-plugin **1.1**
* ftp-util **2.0**
* jsch-util **2.0.0**
* j4ccl **3.0**
* j4ccl-ssh **4.0**
* ccltesting-parent-pom **2.0.0**
* cclunit-framework-schema-xml **2.0**
* cclunit-archetype **1.0**
* cclunit-maven-settings-check-archetype **1.0**


### Enhancements
* Updated j4ccl-ssh (hence ccl-maven-plugin) to work correctly with a linux VM as the remote host. This was accomplished by using expect4j to determine that 
a command has completed before transmitting the next command.
* Replaced the ccl-maven-plugin setting `ccl-compileTimeout` with `expectationTimeout`. Rather than capping the total runtime of each SSH connection, 
ccl-maven-plugin now limits the allowed response time for each line of instructions transmitted to the remote host.
* Improved the `testCase` option to apply true regular expression matching and made matching available to both the test case name and the test name.
* Added `enablePredeclare` setting to ccl-maven-plugin allowing one to build a project that has undelcared variables if necessary.
* Added `deprecatedFlag` setting to ccl-maven-plugin allowing one to build a project that uses deprecated constructs if necessary.
* Added `failOnTestFailures` setting to ccl-maven-plugin allowing one to prevent the build from failing when unit tests fail and echo failed test suites to stdout.
* Created an archetype to generate a mavenized CCL project for testing the maven configuration.
* Created an archetype to generate an empty mavenized CCL project configured with the CCL reporting plugins and expected project layout.
* Stopped dynamically compiling the .inc and .sub files in the source directory and removed the compileExcludes setting from ccl-maven-plugin.
* Added help Mojo to the plugins and added Mojo help to the maven site.
* Improved the ccl-maven-plugin to make the infomation about the execution environment available for reporting.
* Updated the cerreal report to display information about the execution environment when available.

### Corrections
* Fixed an issue constructing output files on the remote host when the user name ends in "$" leading to a file name containing "$_" 
which then got expanded to the last argument of the last command.
* Fixed cerreal code coverage report to show 0% and red rather tha NaN and green when there is no coverage. 
* Fixed cerreal report details to show the entire assert statement for assert statements split over multiple lines.
* Fixed the whitenoise report to recognize namespaces.
* Fixed cdoc to not force stringent formatting rules on the declaration of record structures.
* Fixed javadoc issues causing java 8 builds to fail. 
* Addressed all findbugs issues and compiler warnings.
* Fixed an issue where a record structure date field having a zero value would cause an exception to be thrown.

### Miscelaneous
* Changed `maven-X-plugin` to `X-maven-plugin` for each maven plugin to satisfy [maven3 restrictions on plugin naming][plugin-naming].
* Added additional ccl-maven-plugin output to help identify the source of the problem when it has build failures.
* Changed the names of some ccl-maven-plugin settings and adopted a uniform naming convention for the associated property.
* Simplified the name of the property associated to several ccl-maven-plugin settings to make it easier to enter them at the command line.
* Upgraded the plugins and dependencies of all projects and started leveraging pitest to grade the unit testing quality.
* Started leveraging parent poms to reduce duplications accross pom files.

-------
Earlier versions are not available via Maven Central. Changelog entries are provided [here][historical-changelog] for historical purposes.

[plugin-naming]:https://maven.apache.org/guides/introduction/introduction-to-plugin-prefix-mapping.html
[historical-changelog]:doc/HISTORICALCHANGELOG.md
