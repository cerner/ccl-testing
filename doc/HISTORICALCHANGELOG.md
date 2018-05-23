# ccl-testing Historical Change Log

## 2011-11-14

### Additions
* Remove support for skipping compilation in TestMojo
* Allow for the enabling and disabling of "set modify predeclare" in the CCL Testing Framework.
* Enhance test name specification to specify the test subroutine to be executed.
* Refactor j4ccl to use JAAS objects. 
* Allow for a StructureBuilder to be constructed from an existing Structure.
* Remove deprecated "setCclSessionOutputStream" method from CclExecutor
* Add ability to compile in debug mode.
* Remove custom JETM code

### Corrections
* make the mojo parameter expressions follow the ccl-* pattern. 
* Refactor CclResourceUploaderImpl to not require Subject until upload() is called

## 2011-08-10
### Additions
* Add Ability To Specify RBO or CBO Optimization. 
* Ability to run a single unit test from maven.
* Enforce Maven 2 usage.

### Corrections
* Don't initialize record structure members to default values.

## 2011-07-11
### Additions
* Support script parameters in j4ccl.
* j4ccl-ssh should use cclseclogin2 to validate user credentials
* Report possible cause of failure to find a logical.
* Document when parameters were added to goals.
* Deprecate support for skipping compilation in TestMojo

### Corrections
* j4ccl-ssh uses platform encoding when reading files
* The type OutputFormat is not accessible due to restriction on required library...
* ValidateMojo does not honor skipProcessing parameter
* Modify maven-ccl-plugin integration tests to write build logs to /target instead of a temporary file.
* maven-ccl-plugin uses incorrect character encoding.
* maven-ccl-plugin attempts to compile include files that are not standalone, syntactically-whole source files.

## 2011-03-09
### Additions
* Hows about requiring an explicit version of the CCLUnit Frwk in the pom.
* maven-ccl-plugin needs integration tests
* Modify maven-ccl-plugin to use <server /> tags to provide username and password. 
* Modify maven-ccl-plugin to report what XML files it parses.
* maven-ccl-plugin to report a more meaningful error when the CCLUT framework replys with a 'F' failure status

### Corrections
* mvn-ccl-plugin not honoring skipCompile when setting cclut framework request.
* Incorrect documentation of enabling file log output
* Resolve issues in FindBugs report for maven-ccl-plugin

## 2011-03-04
### Additions
* Refactor Java projects to deploy using automated tools. 
* Enhance j4ccl-ssh to use "env" to dump domain logicals.
* Separate integration tests into /src/it.
* Deploy site JAR alongside primary JAR.
* Add Java and Maven version enforcements to CCL Testing projects.
* Enable full debug output for maven-ccl-plugin.
* Add snapshot repo deployment information to CCLTEST projects.

### Corrections
* j4ccl-ssh puts compilation listings in ccluserdir.
* j4ccl's RecordSetter breaks on large values.

## 2010-10-21
### Additions
* Refactor j4ccl-ssh to minimize the usage of factories. 
* Retrofit projects to use the maven-changes-plugin to track changes. 

### Corrections
* Compile Timeout Flag does not work. 
* Invalid credentials don't fail to log in on surround.
* Fail to construct Configuration if domain login credentials are invalid.
* CCL Maven Commands Hanging in Linux.

## 2010-08-26
### Corrections
* Problems generating the Test Results XML file. 
* Remove warnings about JETM and SLF4J from plugin execution.


## 2010-08-17
### Additions
* Improve the logging for test execution.

### Corrections
* Performance issue with Maven CCL Plugin.
* mvn compile hangs.


## 2010-07-16
### Additions
* Ability to move resource files like XSLT and XML to cer_install.


## 2010-06-04
### Additions
* Display CCL logging when executing Test mojo
* Add option skip compilation of CCL scripts.

### Corrections
* com.cerner.ftp.exception.TransferException when running tests from maven-ccl-plugin with invalid credentials.
* Test mojo needs to reference new "namespaced" record structure names.
* Upgrade j4ccl-ssh dependency in maven-ccl-plugin so it will stop logging in CCLUSERDIR and stop crashing on invalid credentials.

## 2010-05-20
### Additions
* maven-ccl-plugin needs JUnit tests for its Mojo classes

### Corrections
* CCL Programs with uppercase .PRG are not included for compilation.
* maven-ccl-plugin does not include *.sub files.
* Use `cclut` "namespace" to avoid identifier conflicts between the testing framework and the testing target.
* The structure of a DynamicRecordList cannot be viewed until an element is added.
* j4ccl still logging some files to CCLUSERDIR


## 2010-04-30
* Initial release