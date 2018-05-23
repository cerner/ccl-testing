# Build Issues

Prompt Patterns
===
Be sure the [prompt patterns] are correct. The plugin uses them to decide when the back end system is ready to receive commands. 
If a pattern does not match the actual prompt, the plugin will either hang indefinitely or time out waiting for the prompt. In contrast, if a pattern ends up 
matching output that is not from the prompt, the plugin could send a command before the back end is ready to receive it. 

Passing the `-X` commmand argument to maven will cause the plugin to output to stdout
exactly what is being sent to the back end and what response is being expected. Search the output for `commandExpectationGroup:`. It will be followed by two lists. 
The first is a list of commands that will be sent to the back end. The second is a list of regular expressions that will be used to decide when the back end is ready for
the next command in the list. The plugin will wait till the output from the back end matches any one of the regular expressions. 
The work of sending the commands and waiting for the expected output is handled by expect4j which outputs `sending command` followed by the command enclosed in parentheses as 
each command is sent. That is followed by more expect4j output showing the receipt and processing of the back-end output. That part is rather chatty. The maven logging
configuration can be modified to suppress some of the expect4j output.

Compile Failures
===
Listing files for all compile operations are created in $cer_temp. The exact location will be shown in [cclLogfile][configuration] if it is configured. Look for `call compile`.

If an error occurs while trying to compile a test case into a test program, the listing file will be saved in $CCLUSERDIR with a .lis extension. 
The file is deleted if the compile is successful.

Parsing Errors
===
If an xml parsing error occurs, an exception will be prominently visible in the maven output. Unfortunately, the xml parser has no idea what it is parsing so the exception only 
indicates there was a parsing error. If the maven log level is a least Info (default), then the output will name each entity just before it is parsed.

Test Failures
===
Try [`-DdeprecatedFlag=W`][configuration]. CCL's messaging for deprecated constructs does not always explain the problem is deprecation. This should only be done temporarily as it
prevents the identificaiton of undeclared variables. Here are some known examples
- %CCL-E-414 and %CCL-E-415: Non-boolean predicate could return truncation in where clause requiring ccl893 or higher.
  - most likely MAXREC or ASSIGN was used in a where clause without adding '> 0' or '= #'. 
- %CCL-E-393: updt column missing from update command.
  - When updating a record, the updt_dt_tm column must be be updated.

[prompt patterns]:../README.md#file-landing-locations
[configuration]:CONFIGURATIONOPTIONS.md#cclLogfile