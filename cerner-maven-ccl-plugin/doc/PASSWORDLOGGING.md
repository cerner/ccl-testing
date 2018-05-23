# Password Logging Issues

Legacy versions of the CCL Unit plugins log passwords in log files. The latest verions do not, however, the expect4j plugin which is leveraged for back-end communications
logs every command that is sent to the back end including the password used to log into CCL if maven's debug log level 
is set using the `-X` command parameter. There is no way to prevent this programatically. The only option is to turn logging off 
for `expect4j.ConsumerImpl`. There are a number of options for doing this.

* ***Add the following plugin to the pom file***

```xml
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>properties-maven-plugin</artifactId>
        <version>1.0.0</version>
        <executions>
          <execution>
            <goals>
              <goal>set-system-properties</goal>
            </goals>
            <configuration>
              <properties>
                <property>
                    <name>org.slf4j.simpleLogger.log.expect4j.ConsumerImpl</name>
                    <value>off</value>
                </property>
              </properties>
            </configuration>
          </execution>
        </executions>
      </plugin>
```

This seems to be the most convenient option and the `cclunit-archetype` and `cclunit-maven-settings-check-archetype` archetypes do
this automatically.  

Note that running this will set the specified system property for the lifetime of the current command terminal session.


* ***Configure maven's logging options***

Add the following line to the conf/logging/simplelogger.properties file of your maven installation.
`org.slf4j.simpleLogger.log.expect4j.ConsumerImpl=off`

The drawback here is forgetting to repeat this exercise when maven is upgraded.

* ***Define it as a permanent system property.***

"it" being `org.slf4j.simpleLogger.log.expect4j.ConsumerImpl` with value `off`.


* ***Pass the following command parameter to maven***

`-Dorg.slf4j.simpleLogger.log.expect4j.ConsumerImpl=off`

yuk! 



