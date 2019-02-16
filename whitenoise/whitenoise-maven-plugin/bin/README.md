# whitenoise-maven-plugin

A maven reporting plugin for displaying the unit test and code coverage results obtained using [ccl-maven-plugin](../../ccl-maven-plugin/README.md).

Usage
===
```xml
  <reporting>
    <plugins>
      <plugin>
        <groupId>com.cerner.ccl.whitenoise</groupId>
        <artifactId>whitenoise-maven-plugin</artifactId>
        <version>2.4</version>
        <extensions>true</extensions>
      </plugin>
    </plugins>
  </reporting>
```

Configuration Options
===
Execute `mvn help:describe -DgroupId=com.cerner.ccl.whitenoise -DartifactId=whitenoise-maven-plugin -Ddetail=true` for a description of all available parameters. 

Options of note include

**filterFile**
- (string) Location of the filter file, an xml file for excluding specific violations having the following format.
```xml
<?xml version="1.0" encoding="UTF-8"?>
<exclusions xmlns="urn:cerner:ccl:whitenoise-exclusions">
    <exclusion>
        <scriptName>some_script_name</scriptName>
        <subroutineName>some_subroutine_name</subroutineName>
        <variableName>some_variable_name</variableName>
        <lineNumber>321</lineNumber>
        <explanation>some explanation</explanation>
        <violationId>CORE.UNUSED_VARIABLE_DECLARATION</violationId>
    </exclusion>
</exclusions>
```
All attributes are optional. The whitenoise report will exclude any violation matching the provided attributes for any exclusion. The explanation attribute is simply
storage for a comment to explain why the exclusion has been applied.


**doCompile**
- (true/false) Controls whether the source code is compiled with debug before attempting to analyze it. Defaults to false because the common use case is to run the 
whitenoise report in tandem with or shortly after running the unit tests which will already compile the code with debug.
 - default:
    - `false`

**outputRawData**
- (true/false) Indicates whether to output the raw violation data.
 - since 2.2
 - default:
    - `false`

**whitenoiseDataDirectory**
- (string) The folder into which the raw violation data should be output.
 - since 2.2
 - default:
    - `${project.build.directory}/whitenoise/`


Developer Notes
===
The integration tests require the domain specific profile to be activated by a system property named `maven-profile` with value equal to the profile id.
For convenience, the profile should set that property.
```xml
        <profile>
            <id>p-id</id>
            <properties>
                <ccl-host>p-host</ccl-host>
                <ccl-environment>p-env</ccl-environment>
                <ccl-domain>p-domain</ccl-domain>
                <ccl-hostCredentialsId>p-host</ccl-hostCredentialsId>
                <ccl-frontendCredentialsId>p-domain</ccl-frontendCredentialsId>
                <maven-profile>p-id</maven-profile>
            </properties>
            <activation>
                <property>
                    <name>maven-profile</name>
                    <value>p-id</value>
                </property>
            </activation>
        </profile>
```
