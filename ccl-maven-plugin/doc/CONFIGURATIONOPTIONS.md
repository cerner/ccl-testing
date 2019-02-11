Configuration Options
===
Cofiguration options can be specified in a configuration tag in the plugin's tag in the pom.xml file 
```xml
<configuration>
  <option-name-1>option-value-1</option-name-1>
  <option-name-2>option-value-2</option-name-2>
  ...
</configuration>
```
by setting system properties in a profile tag in the pom.xml file or a settings.xml file
```xml
    <profile>
      <id>profile-id</id>
      <properties>
        <property-name-1>property-value-1</property-name-1>
        <property-name-2>property-value-2</property-name-2>
        ...
      </properties>
    </profile>
```
by setting system properties at the command line  
 `-Dproperty-name-1=property-value-1 -Dproperty-name-2=property-value-2 ...`  
 
The property name is generally the same as the name of the configuration option with 'ccl-' prepended. Any excpetions will be noted. Both are case sensitive.

**[logFile](#ccl-log-file)**
- (file path) Specifies the log file location. 
 - since 1.0.1, default="", renamed with 3.0.

**enableFullDebug**
- (true/false) Causes the plugin to dump all SSH and CCL input/output to stdout and ${ccl-logFile}. 
 - since 1.1
 - default:
    - `false`

**skipEnvset**
- (true/false) Tells the plugin to not bother performing an envset after each SSH connection is established. 
This provides a minor performance improvement if the default environment for the host credentials is already the correct environment.
 - since 3.0
 - default:
    - `false`

**enforcePredeclare** 
- (true/false) Causes CCL to issue a message if any test executes code which accesses variables that have not been declared. The message level is decided by the 
deprecated level as described in the CCL documentation. The default deprecated level is E which will cause the build to fail.
 - since 3.0
 - default:
   - `true`
 - requires framework version 1.5 - has no impact with earlier versions.

**deprecatedFlag** 
- (E, W, L, I, D) Sets the corresponding deprecated trace when executing unit tests causing CCL to issue an Error, Warning, Log, Information or Debug message 
when a deprecated construct is encountered. The value E (default) will cause the build to fail if deprecated constructs are encountered. 
 - since 3.0
 - default:
   - `E` (error)
 - requires framework version 1.5 - has no impact with earlier versions.

**optimizerMode**
- (CBO/RBO/) Causes the specified optimizer mode to be set when executing unit tests. If not specified the environment's default is used.
 - since 1.3, default=""

**testCase**
- (test-case-name-pattern#test-name-pattern) Restricts the test goal to only executing unit tests from test cases matching the specified test case name pattern and 
if provided tests whose names match the test name pattern. Regular expression pattern matching is used in both cases. Caveat: test name matchihg is handled by the 
CCL Unit framework. CCL patstring matching is used prior to version 2.0. 
Do not include the .inc extension. Omit the hash mark and test-name-pattern to execute all unit tests from the indicated test cases.
 - since 1.3, default=""
 
**failOnTestFailures**
- (true/false) Causes the plugin to fail the maven build if any of the CCL unit tests fail. This was the original plugin behavior and is not very "surefire-like". 
If set to false, then the test results must be inspected manually either using the cerreal plugin or by checking the xml output to determine the outcome. 
CCL test cases do not fail fast, but if set to true, the maven build will fail fast if any test case fails.
 - since 3.0
 - default:
    - `true`

**specifyDebugCcl**
- (true/false) Causes the plugin to use the new CCL executable cclora_dbg rather than the the original CCL executable cclora which has been 
modified to no longer produce debug or code coverage data. If the target domain does not yet have the cclora_dbg executable, then the plugin will 
fail indicating there was an expect4j timeout for the command $cer_exe/cclora_dbg. In that case set specifyDebugCcl to false. It will be most convenient
to do this using the settings.xml file. 
 - since 3.0
 - default:
   - `true`

**[osPromptPattern](#os-prompt-pattern)**
- (regex for host prompt) The plugin uses this to determine when the host is ready to receive a command. 
 - since 3.0
 - default:
    - `username:environment@host:[^\r\n]*(\r|\n)+#\s*`
    - `username:\w*@host:[^\r\n]*(\r|\n)+#\s*` if environement not provided
    - changed from `username:environment@host:[^>]*>\s*` or `username:\w*@host:[^>]*>\s*` in version 3.1


**cclPromptPattern**
- (regex for CCL prompt) The plugin uses this to determine when CCL host is ready to receive a command. 
 - since 3.0
 - default:
   - `\n\s*\d+\)\s*$`


**cclLoginPromptPattern**
- (regex for CCL Login prompt) The plugin uses this to determine when CCL is ready to receive login credentials.

 - since 3.0
 - default:
   -  `\(Hit PF3 or RETURN to skip security login; this will disable Uar functions\)`


**cclLoginSuccessPromptPattern**
- (regex for CCL Login success prompt) The plugin uses this to determine when CCL authentication has completed.
 - since 3.0
 - default:
   - `Enter Y to continue`
 
**cclLoginFailurePromptPatterns**
- (regex for CCL Login failure prompt) The plugin uses this to determine when CCL authentication has completed. 
 - since 3.0
 - default:
   - `V500 SECURITY LOGIN FAILURE`, `V500 SECURITY LOGIN WARNING`, `Retry \(Y/N\), and `Repeat New Password:`


**[expectationTimeout](#expectation-timeout)**
(milliseconds) The maximum time the plugin will wait for the host system to to finish processing a command and indicate it is ready for the next command by displaying 
something that mathces the osPromptPattern or cclPromptPattern as appropriate 
(except an "exit" CCL command is permitted to take twice as long and CCL commands that end with " go" are permitted to take forever). 
Indicate -1 to have the plugin wait indefinitely for all commands. 
 - since 3.0
 - default:
    - `20000`


**cclSourceDirectory**
- (path specification) Overrides the default source directory ${basedir}/src/main/ccl
 - since 1.0-alpha-3, default=""

**resources** 
- (list of path specifications) Overrides the default resources directory ${basedir}/src/main/resources. 
See [maven resources][maven resources] for configuration details. There is no command line option.
 - since 1.0-alpha-3
 

**cclTestSourceDirectory**
- (path specification) Overrides the default test source directory ${basedir}/src/test/ccl
 - since 1.0-alpha-3, default=""

**testResources** 
- (list of path specifications) Overrides the default test resources directory ${basedir}/src/test/resources. 
See [maven resources][maven resources] for configuration details. There is no command line option.
 - since 1.0-alpha-3

**outputDirectory**
- (path specification) Directory where build output is saved. 
 - since 1.0-alpha-3, default=${project.build.directory}


**skipProcessing**
- (true/false) Causes the plugin to skip the processing for all goals prior to the test goal.
 - since 1.0-alpha-3
 - default:
   - `false`

**skipProcessResources**
- (true/false) Causes the plugin to skip the process-resources goal
 - since 1.0-alpha-3:
 - default
    - `false`

**skipCompile**
- (true/false) Causes the plugin to skip the compile goal
 - since 1.0-alpha-3
 - default:
   - `false`

**skipProcessTestResources**
- (true/false) Causes the plugin to skip the process-test-resources goal
 - since 1.0-alpha-3
 - default:
   - `false`

**skipTestCompile**
- (true/false) Causes the plugin to skip the test-compile goal
 - since 1.0-alpha-3
 - default:
   - `false`

**testFrameworkVersion**
- (version range) Causes the validate goal to check the installed version of the [CCL Unit framework][ccl-unit-framework-source] and fail the 
build if it is not in the specified range. Specify by adding a validationRule configuration to a validate goal for the plugin.
```xml
        <executions>
            <execution>
                <id>validate-framework-version</id>
                <goals>
                    <goal>validate</goal>
                </goals>
                <configuration>
                    <validationRule>
                        <testFrameworkVersion>[1.5]</testFrameworkVersion>
                    </validationRule>
                </configuration>
            </execution>
        </executions>
```
The specified value must be a version range such as [exact-version], [lower-bound-allowed, upper-bound-excluded), [lower-bound-allowed, upper-bound-included], etc. 
See [maven version range] for more details.

**host**
- (string) Specifies the remote host.
 - required.

**environment**
- (string) Specifies the remote environment.
 - required unless skipEnvset is not set to false and the default osPromptPattern is okay without it.

**domain**
- (string) Specifies the target HNAM domain.
 - optional (see [caveat]).
 
**hostCredentialsId**
- (string) Specifies a server id defining credentials for the remote host.
 - The preferred method for specifying host credentials
 - required unless both hostUsername and hostPassword are provided.
 
**frontendCredentialsId**
- (string) Specifies a maven server id defining credentials for the target HNAM domain. 
 - The preferred method for specifying domain credentials
 - optional (see [caveat]).

**hostUsername**
- (string) Alternate way to specify the remote host user. Ignored if hostCredentialsId is provided.

**hostPassword**
- (string) Alternate way to specify the remote host password. Does not support encryption. Ignored if hostCredentialsId is provided.

**domainUsername**
- (string) Alternate way to specify the HNAM domain user. Ignored if hostCredentialsId is provided.

**domainPassword**
- (string) Alternate way to specify the HNAM domain password. Does not support encryption. Ignored if hostCredentialsId is provided.

Domain Credentials Caveat
===
<a name="domain-credentials-caveat"></a>
Domain credentials are not required by the plugin, but they are necessary if any unit test executes CCL code that requires authentication, 
code that accesses uar functions or makes server calls, for example.
 
[maven resources]:https://maven.apache.org/pom.html#Resources
[ccl-unit-framework-source]:https://github.com/cerner/cclunit-framework/cclunit-framework-source/README.md
[maven version range]:http://maven.apache.org/enforcer/enforcer-rules/versionRanges.html
[caveat]:#domain-credentials-caveat