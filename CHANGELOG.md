# ccl-testing Change Log

### Corrections
* Fix [#23](https://github.com/cerner/ccl-testing/issues/23) skip processing configurations not working.


## 2019-02-18
* whitenoise-maven-plugin **2.5**
* cclunit-archetype **1.5**

### Corrections
* Fix [#18](https://github.com/cerner/ccl-testing/issues/18) whitenoise flags setting a logical as using an undeclared variable.
* Improve the whitenoise unused variable check to handle things like ns::var = var.


## 2019-02-16
* ccl-maven-plugin **3.2**
* whitenoise-maven-plugin **2.4**
* cclunit-archetype **1.4**
* cclunit-maven-settings-check-archetype **1.2**

### Corrections
* Fix issue in regex used to decide if a .prg file name matches the created object name.

## 2019-02-14
* whitenoise-maven-plugin **2.3**
* cclunit-archetype **1.3**

### Corrections
* Fixed [#15](https://github.com/cerner/ccl-testing/issues/15) whitenoise fails to recognize parameters of subroutines declared in-line.


## 2019-02-11
* ccl-maven-plugin **3.1**
* cdoc-maven-plugin **1.2**
* whitenoise-maven-plugin **2.2**
* j4ccl **3.1**
* j4ccl-ssh **4.1**
* ccltesting-parent-pom **2.1**
* cclunit-archetype **1.2**
* cclunit-maven-settings-check-archetype **1.1**

### Corrections
* Fixed [#12](https://github.com/cerner/ccl-testing/issues/12) whitenoise fails to recognize `call some_sub(null)` in a report writer section as a call to some_sub.
* Fix whitnoise to recognize the previously unrecgonized declaration scopes privateprotect and persistscript. 
* Fixed the integration tests to honor the configured osPromptPattern.
* Fixed cdoc to not bomb on record structure definions which continue at the same level where an include file left off.

### Miscelaneous
* Fail a script file upload if the file name is not lower case or the created object name does not match the file name.
  - The whitenoise and cerreal reports are inaccurate in those situations.
* Changed the default osPromptPattern to `user:enviroment@host:[^\r\n]*(\r|\n)+#\s*`
  - This matches the conventions now being applied by CWx and IP Domains.
  - Previously, it was `user:enviroment@host:[^>]*>\s*`


## 2019-02-01
* cerreal-maven-plugin **2.1**
* whitenoise-maven-plugin **2.1**
* cclunit-archetype **1.1**

### Corrections
* Fixed [#8](https://github.com/cerner/ccl-testing/issues/8) cerreal 'without includes' report doesn't work correctly when there are consecutive include files.
* Fixed [#9](https://github.com/cerner/ccl-testing/issues/9) whitenoise fails to recognize `if(some_sub(null))` as a call to some_sub.
* Fixed [#10](https://github.com/cerner/ccl-testing/issues/10) whitenoise wrongly treats var = value in a report writer section as a usage of var.

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
