# Maven Archetype Usage
A maven archetype is used to create a new maven project having predefined folders and files.

## Contents
[Skeleton CCL Project](#skeleton-ccl-project)  
[Maven Verification Project](#maven-verification-project)  

## Skeleton CCL Project

**cclunit-archetype** generates a skeleton CCL project with the expected folder structure using the lastest maven plugins.  
Once the project is generated 
- Put CCL source files in src/main/ccl   
- Put CCL unit test cases in src/test/ccl   
- Launch a command prompt in the project folder.  
- Execute any maven command, for example  
    - `mvn clean compile -P<profile>` to compile the source  
    - `mvn clean compile test -P<profile>` to compile the source and execute the unit tests  
    - `mvn clean compile test site -P<profile>` to compile the source, execute the unit tests and generate reports  

While generating the project a series of prompts will be presented.  
- You can press `Enter` to select the default value for any prompt that presents a default value.
- Along the way you must enter a groupId, artifactId and vesion. These references contain guidelines for their selection.
    - [maven conventions], 
    - [maven coordinates], 
    - [choosing coordinates].
    
To generate the project 
- Launch a command prompt in an empty directory.  
- Enter `mvn archetype:generate -Dfilter=com.cerner.ccl.archetype:cclunit-archetype`.  
- Respond to the ensuing prompts.
    - Enter `1` to select the first/only item `cclunit-archetype` in the presented list of archetypes.
    - Press `Enter` to select the highest version in the list of available versions.
    - Enter a groupId, artifactId, version, and (opitonal) package when prompted.
    - Enter `Y` or `N`when prompted to confirm the entries or start over.

Maven will create a folder named `<artifactId>` and add the archetype contents into it.  
Note that the starting directory does not really have to be empyt, but it must not contain a pom file. 
 - If it already contains a folder named `<artifactId>`, the behavior is not guaranteed except that  
    - No existing files will be modified.  
    - Partial contents from the archetype could be added or the command could fail.  



## Maven Verification Project
**cclunit-maven-settings-check-archetype** generates a mavenized CCL project used to verify if maven is properly configured .  
- The generated project will contain a sample program with a sample unit test case.  
- The project will be configured with maven-ccl-plugin but no other plugins.  
- An older version of maven-ccl-plugin could be used. 


Once the project is generated 
- Launch a command prompt in the project folder 
- Execute the following maven command
    - `mvn clean compile test -P<profile>`

If the project builds without errors, maven is properly configured for mavenize CCL projects, and otherwise, it is not.


To generate the project 
- Execute the folowing command in an empty directory and 
    - `mvn archetype:generate -Dfilter=com.cerner.ccl.archetype:cclunit-maven-settings-check-archetype`   

Refer to [Skeleton CCL Project](#skeleton-ccl-project) for guidance in answering the prompts.


[maven conventions]:https://maven.apache.org/maven-conventions.html
[maven coordinates]:https://maven.apache.org/pom.html#Maven_Coordinates
[choosing coordinates]:http://central.sonatype.org/pages/choosing-your-coordinates.html
