  # Build Issues

## Contents
[Timeout Issues](#timeout-issues)  
[Prerequisite Violations](#prerequisite-violations)  
[Compile Failures](#compile-failures)  
[Parsing Errors](#parsing-errors)  
[Test Failures](#test-failures)  
[Code Coverage Problems](#code-coverage-problems)  
[Legacy Upgrades](#legacy-upgrades)  

Timeout Issues
===
expectj4 send status TIMEOUT (-2)

Be sure the [prompt patterns] are correct. The plugin uses them to decide when the back end system is ready to receive commands. 
If a pattern does not match the actual prompt, the plugin will either hang indefinitely or time out waiting for the prompt. On the other hand, 
if a prompt pattern ends up matching output that is not actually the OS prompt, the plugin could send a command before the back end is ready to receive it. 

Passing the `-X` command argument to maven will cause the plugin to output to stdout
exactly what is being sent to the back end and what response is being expected. Search the output for `commandExpectationGroup:` which will be followed by two lists,
the commands sent to the back end and the regular expressions used to decide when the back end is ready for
another command. The plugin waits for the output from the back end to match one of the regular expressions.

The sending of commands and waiting for expected output is handled by [expect4j] which ouputs `sending command` followed by the command enclosed in parentheses as 
each command is sent. That is followed by more [expect4j] output showing the receipt and processing of the back-end output. The logging for this is rather chatty. The maven logging
configuration can be modified to suppress some of the [expect4j] output.

If the prompt patterns are correct, then the backend device and/or the connection to it are too slow to beat the [expectation timeout][expectation timeout]. Try setting a larger value.

Prerequisite Violations
===
To prevent a successful build with incorrect and confusing results, builds are failed if these prerequisites are not satisfied.  
- program source file names must be all lower case.
- program source files must have a .prg extension
- program source file names must match the name of the generated object
    - case-insensitive - characters inside the file can be any case

Compile Failures
===
If a compile failure occurs, inspect the last displayed listing file in `$cer_temp` to see the error in context.  
**validating compile &lt;listing file name&gt;** is displayed as each file is compiled.  


Parsing Errors
===
If an xml parsing error occurs, an exception will display prominently in the maven output. Unfortunately, the xml parser has no idea what it is parsing so the exception only 
indicates there was a parsing error. The output will name each entity just before it is parsed if the maven log level is at least Info (the default value).

Test Failures
===
Try [`-DdeprecatedFlag=W`](CONFIGURATIONOPTIONS.md#deprecatedFlag). CCL's messaging for deprecated constructs does not always explain the problem is deprecation. This should only be done temporarily as it
prevents the identification of undeclared variables. Here are some known examples
- %CCL-E-414 and %CCL-E-415: Non-boolean predicate could return truncation in where clause requiring ccl893 or higher.
  - most likely MAXREC or ASSIGN was used in a where clause without adding '> 0' or '= #'. 
- %CCL-E-393: updt column missing from update command.
  - When updating a record, the updt_dt_tm column must be be updated.

Code Coverage Problems
===
To see code coverage, [specifyDebugCcl](ccl-maven-plugin/doc/CONFIGURATIONOPTIONS.md#specifyDebugCcl) must be false for CCL versions prior to 8.13.0.

Legacy Upgrades
===
There are some significant differences from legacy versions to note:
* The artifactId for plugins is now `X-maven-plugin` rather than `maven-X-plugin` 
    * This is to satisfy [maven3 restrictions on plugin naming][plugin-naming].
* Several groupId values were changed. 
    * All components now live below `com.cerner.ccl` or `com.cerner.ftp`. 
    * In particular use `com.cerner.ccl.testing` not `com.cerner.ccltesting`.


[prompt patterns]:./CONFIGURATIONOPTIONS.md#osPromptPattern
[expectation timeout]:./CONFIGURATIONOPTIONS.md#expectationTimeout
[expect4j]:https://github.com/cverges/expect4j
[plugin-naming]:https://maven.apache.org/guides/introduction/introduction-to-plugin-prefix-mapping.html
