# cerreal-maven-plugin

A maven reporting plugin for displaying the unit test and code coverage results obtained using [ccl-maven-plugin](../ccl-maven-plugin/README.md).

Usage
===
```xml
  <reporting>
    <plugins>
      <plugin>
        <groupId>com.cerner.ccl.ccltesting</groupId>
        <artifactId>cerreal-maven-plugin</artifactId>
        <version>2.2</version>
        <extensions>true</extensions>
      </plugin>
    </plugins>
  </reporting>
```

Configuration Options
===
Execute `mvn help:describe -DgroupId=com.cerner.ccl.testing -DartifactId=cerreal-maven-plugin -Ddetail=true` for a description of all available parameters.