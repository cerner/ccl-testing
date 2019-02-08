# whitenoise-maven-plugin

A maven reporting plugin for displaying the unit test and code coverage results obtained using [ccl-maven-plugin](../ccl-maven-plugin/README.md).

Usage
===
```xml
  <reporting>
    <plugins>
      <plugin>
        <groupId>com.cerner.ccl.whitenoise</groupId>
        <artifactId>whitenoise-maven-plugin</artifactId>
        <version>2.1</version>
        <extensions>true</extensions>
      </plugin>
    </plugins>
  </reporting>
```

Execute `mvn help:describe -DgroupId=com.cerner.ccl.whitenoise -DartifactId=whitenoise-maven-plugin -Ddetail=true` for a description of the available parameters.


Contributing
===
The integration tests for whitenoise-maven-plugin will not pass unless the domain's provide is the first profile specified in the maven command and it is activated
by a system property named `maven-profile` with value equal to the profile id.
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
