# Maven Archetype Usage

Enter either of the following commands in an empty directory.  
  `mvn archetype:generate -Dfilter=<artifactId>`  
  `mvn archetype:generate -Dfilter=<groupId>:<artifactId>` 
- The filter is optional (but not really)
 - if one is specified it must be complete
- The archetypes of interest for CCL Unit testing are
 - **com.cerner.ccl.archetype:cclunit-archetype**
   - generates a skeleton CCL project with the specified maven coordinates configured to use the latest versions of ccl-testing plugins available when the archetype was created.
 - **com.cerner.ccl.archetype:cclunit-maven-settings-check-archetype**
   - generates a mavenized CCL project with no additional plugins that builds without errors.
   - execute the command `mvn clean test -P<profile>` on the generated project to determine if maven, the profile and associated domain are set up correctly.

Here is what to expect when running the `achetype:generate` command.  
You can just press `Enter` to select the default 
for any prompt that provides one.

- After entering the generate command maven will present a numbered list of matching archetypes. 
 - or indicate no matches if there are none. 
- Enter the number from the list for the desired archetype.
- If multiple versions are available, there will be a prompt to select the version.
- Enter a groupId, artifactId, version, and (opitonal) package when prompted. 
- When prompted, enter Y to confirm the entries are correct or N to start again.
- Maven will create a folder with the same name as the artifactId and add the contents from the archetype into it. 
  - If a folder with that name already exists, the behavior is not guaranteed except that no existing files will be modified.  
   - Partial contents from the archetype could be added or the command could fail.

Here are a some references containing guidelines for selecting a groupId, artifactId and version for the project you are generating: 
 - [maven conventions], 
 - [maven coordinates], 
 - [choosing coordinates].

[maven conventions]:https://maven.apache.org/maven-conventions.html
[maven coordinates]:https://maven.apache.org/pom.html#Maven_Coordinates
[choosing coordinates]:http://central.sonatype.org/pages/choosing-your-coordinates.html
[cerreal]:../cerreal-maven-plugin/README.md
[whitenoise]:../whitnoise-maven-plugin/README.md
[cdoc]:../cdoc-maven-plugin/README.md