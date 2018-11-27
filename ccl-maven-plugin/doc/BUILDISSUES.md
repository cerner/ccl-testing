# Build Issues

Timout Issues
===
expectj4 send status TIMEOUT (-2)

Be sure the [prompt patterns] are correct. The plugin uses them to decide when the back end system is ready to receive commands. 
If a pattern does not match the actual prompt, the plugin will either hang indefinitely or time out waiting for the prompt. In contrast, if a pattern ends up 
matching output that is not from the prompt, the plugin could send a command before the back end is ready to receive it. 

Passing the `-X` command argument to maven will cause the plugin to output to stdout
exactly what is being sent to the back end and what response is being expected. Search the output for `commandExpectationGroup:` which will be followed by two lists,
the commands sent to the back end and the regular expressions used to decide when the back end is ready for
another command. The plugin waits for the output from the back end to match one of the regular expressions.

The sending of commands and waiting for expected output is handled by [expect4j] which ouputs `sending command` followed by the command enclosed in parentheses as 
each command is sent. That is followed by more [expect4j] output showing the receipt and processing of the back-end output. The logging for this is rather chatty. The maven logging
configuration can be modified to suppress some of the [expect4j] output.

If the prompt patterns are correct, then the backend device and/or the connection to it are too slow to beat the [expectation timeout][expectation timeout]. Try setting a larger value.

Compile Failures
===
Listing files for all compile operations are created in $cer_temp. The exact location will be shown in [cclLogfile][configuration] if it is configured. Look for `call compile`.

If an error occurs while trying to compile a test case into a test program, the listing file will be saved in $CCLUSERDIR with a .lis extension. 
The file is deleted if the compile is successful.

Parsing Errors
===
If an xml parsing error occurs, an exception will be prominently visible in the maven output. Unfortunately, the xml parser has no idea what it is parsing so the exception only 
indicates there was a parsing error. If the maven log level is at least Info (default), then the output will name each entity just before it is parsed.

Test Failures
===
Try [`-DdeprecatedFlag=W`][configuration]. CCL's messaging for deprecated constructs does not always explain the problem is deprecation. This should only be done temporarily as it
prevents the identification of undeclared variables. Here are some known examples
- %CCL-E-414 and %CCL-E-415: Non-boolean predicate could return truncation in where clause requiring ccl893 or higher.
  - most likely MAXREC or ASSIGN was used in a where clause without adding '> 0' or '= #'. 
- %CCL-E-393: updt column missing from update command.
  - When updating a record, the updt_dt_tm column must be be updated.

[prompt patterns]:./CONFIGURATIONOPTIONS.md#os-prompt-pattern
[expectation timeout]:./CONFIGURATIONOPTIONS.md#expectation-timeout
[configuration]:CONFIGURATIONOPTIONS.md#ccl-log-file
[expect4j]:https://github.com/cverges/expect4j