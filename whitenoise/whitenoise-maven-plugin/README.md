# whitenoise-maven-plugin

A maven reporting plugin for performing a static analysis of CCL code and displaying the results. Normally it is used in conjunction with [ccl-maven-plugin],
but it can be used standalone. It has the same prerequisite [maven configuration][maven-configuration] and many of the same [options][ccl-maven-plugin-options] and
[troubleshooting steps][troubleshooting-steps].

![][sample-output-0]

Usage
===
```xml
  <reporting>
    <plugins>
      <plugin>
        <groupId>com.cerner.ccl.whitenoise</groupId>
        <artifactId>whitenoise-maven-plugin</artifactId>
        <version>2.6</version>
        <extensions>true</extensions>
      </plugin>
    </plugins>
  </reporting>
```

Configuration Options
===
Execute `mvn help:describe -DgroupId=com.cerner.ccl.whitenoise -DartifactId=whitenoise-maven-plugin -Ddetail=true` for a description of all available options.

Options of note include

**filterFile**
- (string) Location of the filter file, an xml file for excluding specific violations having the following format.
```xml
<?xml version="1.0" encoding="UTF-8"?>
<exclusions xmlns="urn:cerner:ccl:whitenoise-exclusions">
    <exclusion>
        <violationId>CORE.UNUSED_VARIABLE_DECLARATION</violationId>
        <scriptName>some_script_name</scriptName>
        <subroutineName>some_subroutine_name</subroutineName>
        <variableName>some_variable_name</variableName>
        <iterator>some_iterator</iterator>
        <lineNumber>321</lineNumber>
        <explanation>some explanation</explanation>
    </exclusion>
</exclusions>
```
All attributes are optional. The whitenoise report will exclude any violation matching the provided attributes for any exclusion. The explanation attribute is simply
storage for a comment to explain why the exclusion has been applied.


**doCompile**
- (true/false) Controls whether the source code is compiled with debug before attempting to analyze it. Defaults to false because the common use case is to run the 
whitenoise report in tandem with or shortly after running the unit tests using [ccl-maven-plugin][ccl-maven-plugin] which will already compile the code with debug.
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

**rulesToIgnore**
- (string) A comma delimited list of class names for rules to skip. Useful to temporarily avoid a defective rule causing errors till the rule can be fixed.
The class names are converted to regular expressions with leading wild cards. Collisions can be avoided with fullly qualified class names.
 - since 2.7


Developer Notes
===
**Architecture**  
The underlying design is to execute `translate the_script with xml` and apply [XPATH][x-path] queries to identify patterns that must or must not occur in the generated xml.

**Creating new Violations**
- Create a new java class implementing [com.cerner.ccl.analysis.data.Violation][violation-class] or one of its [subclasses][violation-subclasses].
- Create a new java class extending [com.cerner.ccl.analysis.core.rule.TimedDelegate][timed-delegate-class] or modify an existing one to check for the new violation. 
- If a brand new Rule is created, it must be added to the services resource file  
`src/main/resources/META-INF/services/com.cerner.ccl.analysis.jdom.JdomAnalysisRule$Delegate`

Use any [existing Rule][existing-rules] as an example.

**Custom Rules**  
- whitenoise supports "custom rules", a plugin-type capability for adding new rules without changing whitenoise itself.  
- custom rules provide an avenue to define rules not having broad use, such as enforcing team-specific standards.  
- custom rules are no different than standard rules. They just live outside of [whitenoise-core-rules][whitenoise-core-rules]
in a separate jar which closely resembles [whitenoise-core-rules][whitenoise-core-rules]. 
- A custom rules jar file must contain a services resource file listing the rules that are exported by the jar.
  - viz., `src/main/resources/META-INF/services/com.cerner.ccl.analysis.jdom.JdomAnalysisRule$Delegate`
- A custom rules jar file must be added to the whitenoise classpath to be recognized/applied.
     - ...as a dependency of a whitenoise plugin specification included in the &lt;build&gt; section of the project's pom file.  


**Integration Tests**  
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

[ccl-maven-plugin]: ../../ccl-maven-plugin/README.md
[ccl-maven-plugin-options]: ../../ccl-maven-plugin/doc/CONFIGURATIONOPTIONS.md
[maven-configuration]: ../../doc/CONFIGUREMAVEN.md
[x-path]: https://developer.mozilla.org/en-US/docs/Web/XPath
[violation-class]: ../whitenoise-data/src/main/java/com/cerner/ccl/analysis/data/Violation.java
[violation-subclasses]: ../whitenoise-data/src/main/java/com/cerner/ccl/analysis/data
[timed-delegate-class]: ../whitenoise-rules-core/src/main/java/com/cerner/ccl/analysis/core/rules/TimedDelegate.java
[existing-rules]: ../whitenoise-rules-core/src/main/java/com/cerner/ccl/analysis/core/rules
[whitenoise-core-rules]: ../whitenoise-rules-core
[troubleshooting-steps]: ../../ccl-maven-plugin/doc/BUILDISSUES.md
[sample-output-0]: ./doc/image/sample-output-0.png
