# cdoc-maven-plugin

A maven reporting plugin for displaying the unit test and code coverage results obtained using [ccl-maven-plugin](../ccl-maven-plugin/README.md).

Usage
===
```xml
  <reporting>
    <plugins>
      <plugin>
        <groupId>com.cerner.ccl.cdoc</groupId>
        <artifactId>cdoc-maven-plugin</artifactId>
        <version>1.3</version>
      </plugin>
    </plugins>
  </reporting>
```

Configuration Options
===
Execute `mvn help:describe -DgroupId=com.cerner.ccl.cdoc -DartifactId=cdoc-maven-plugin -Ddetail=true` for a description of all available parameters.