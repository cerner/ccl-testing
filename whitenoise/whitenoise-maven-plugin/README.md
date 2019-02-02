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