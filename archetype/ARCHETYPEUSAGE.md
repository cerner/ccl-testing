# Maven Archetype Usage

Enter either of the following commands  
  `mvn archetype:generate -Dfilter=<artifactId>`  
  `mvn archetype:generate -Dfilter=<groupId>:<artifactId>` 
- The filter is optional (but not really)
 - if one is specified it must be complete
- The archetypes of interest for CCL Unit testing are
 - **com.cerner.ccl.archetype:cclunit-archetype**
   - generates a project with the specified maven coordinates configured to use cerreal, whitenoise and cdoc.
 - **com.cerner.ccl.archetype:cclunit-maven-settings-check-archetype**
   - generates a mavenized CCL project with no additional plugins that builds without errors.
    - execute the command `mvn clean test -P<profile>` to determine if maven, the profile and associated domain are set up correctly.

Here is what to expect when running the `achetype:generate` command.
- After entering the command maven will present a list of matching archetypes or indicate there are no matches. 
- Enter the number from the list for the desired archetype
- Enter a groupId, artifactId, version, and (opitonal) package when prompted. 
 - You can just press `Enter` to select the default value whenever one is indicated.
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
[cerreal]:../cerner-maven-cerreal-plugin/README.md
[whitenoise]:../cerner-maven-whitnoise-plugin/README.md
[cdoc]:../cerner-maven-cdoc-plugin/README.md