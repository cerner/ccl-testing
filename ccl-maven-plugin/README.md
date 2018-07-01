# ccl-maven-plugin

A maven build plugin for transferring resources, compiling CCL code and unit tests, executing the tests, and retrieving the test and code coverage results. 
Additional reporting plugins are used to add the results to the maven site.

Usage
===
Maven must be configured as described [here][configure-maven].

The expected project layout is shown below. This can be overridden with [configuration options][configuration options].
```text
project_directory
  pom.xml
  - src
      - main
          - ccl
          - resources
      - test      
          - ccl
          - resources
```
Here is the plugin specification.
```xml
  <plugin>
    <groupId>com.cerner.ccl.testing</groupId>
    <artifactId>ccl-maven-plugin</artifactId>
    <version>3.0</version>
    <extensions>true</extensions>
  </plugin>
```

A complete [sample pom][sample pom] is available. It includes various reporting plugins discussed elsewhere. If `mvn test -Pprofile` succeeds 
but `mvn test site -Pprofile` fails, some of the reporting plugins are at fault. Remove them from the build or visit their 
individual documentation for suggestions.

A [maven archetype][maven archetype] is available to automatically create an empty starter project with the proper folder layout and the sample pom referenced above. 
Execute the following command to get started [looking here][archetype usage] for guidance on populating the prompts.   
`mvn archetype:generate -Dfilter=com.cerner.ccl.archetype:cclunit-archetype`  


A number of configuration options are available. [Look here][configuration options] for details.

Don't want your password showing up in log files? [Look here](doc/PASSWORDLOGGING.md).

Resolving Build Issues
===
[Look here][build issues] for guidance.

Goals and their Functions
===
All goals require a `-Pprofile` argument to indicate the target host and domain and credentials for them. All file transfers are performed using sftp.

**validate**
- Verify the version of [CCL Unit framework][ccl-unit-framework] installed in the target environment meets the validationRule.testFrameworkVersion configured 
for ccl-maven-plugin. The plugin does not currently enforce or define a default testFrameworkVersion, 
but the value [can be configured][configuration options] in the pom file. 

**process-resources**
- Transfer all files from the resources directory to the target host. The [landing location][landing location] for each file depends on the file's extension. 

**compile** 
- Transfer all .prg, .inc and .sub files from the sources directory to $CCLSOURCE on the target host.
- For each .prg file:
  - Create an SSH connection to the target host.
  - envset to the specified environment.
  - Launch CCL.
  - Compile (%i) the .prg file generating an output file in $cer_temp.
  - Transfer the output file from the target host and inspect it to determine if the compile was successful. 

**process-test-resources**
- Transfer all files from the test resources directory to the target host. The [landing location][landing location] for each file depends on the file's extension.

**test-compile** 
- Transfer all .prg, .inc and .sub files from the test sources directory to $CCLSOURCE on the target host.
- For each .inc and .sub file:
  - dynamically create a prg file which includes the file.
- For each .prg file (including the dynamically created ones):
  - Create an SSH connection to the target host.
  - envset to the specified environment.
  - Launch CCL.
  - Compile (%i) the .prg file generating an output file in $cer_temp.
  - Transfer the output file from the target host and inspect it to determine if the compile was successful. 


**test** 
- For each test case (.inc file):
  - Create an SSH connection to the target host.
  - envset to the specified environment.
  - Launch CCL.
  - Authenticate. 
  - Run the CCL program cclut_execute_test_case to execute the all unit tests from the test case and create output files in $cer_temp for the 
  test results, code coverage and program listings.
  - Transfer the output files from the target host and save in the project build directory.
  - Analyze the output files to determine if there were any unit test failures.


File landing locations
===
 - CCL source files (.prg, .inc and .sub) 
are sent to $CCLSOURCE. 
 - Shell script files (.ksh and .com) are sent to $cer_proc. 
 - All other files are sent to $cer_install.




Developer Notes
===
If you are building the plugin from source please note the following points.

- The integration tests for the plugin require an active maven profile that provides all the information 
required to use the plugin including identification of a target node, environment and domain as well as credentials for accessing them.

- The integration tests will not compile properly with m2eclipse. To work around this, manually invoke the "test-compile" lifecycle (with an appropriate profile)
from a command prompt. The desired version of the plugin must reside in the local maven repo as that is what the integration tests will run against when executed within the IDE.

[configure-maven]:../doc/CONFIGUREMAVEN.md
[configuration options]:doc/CONFIGURATIONOPTIONS.md
[maven archetype]:https://maven.apache.org/guides/introduction/introduction-to-archetypes.html
[ccl-unit-framework]:https://github.com/cerner/cclunit-framework
[archetype usage]: ../archetype/ARCHETYPEUSAGE.md
[landing location]:#file-landing-locations
[build issues]:doc/BUILDISSUES.md
[sample pom]:doc/SAMPLEPOM.md