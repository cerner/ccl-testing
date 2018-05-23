# Configuring maven for CCL Unit

Apache Maven is a software build system. Refer to the [Apache Maven][apache-maven] website to learn more about it and specifically
the [downloading maven] instructions if you do not already have it installed.

[apache-maven]:https://maven.apache.org/
[downloading maven]:https://maven.apache.org/download.html

The [cerner-maven-ccl-plugin][cerner-maven-ccl-plugin] must be provided credentials so it can transfer files to and from the back-end node and access CCL 
in the target environment. This information can be provided in several ways, but we recommend using maven <b>profiles</b> and <b>servers</b> to specify it. 
Moreover, we recommed specifying these items in a <b>settings.xml</b> file (opposed to putting it in the pom file of every single project). 
A copy of settings.xml resides in the conf folder of your maven installation, however it is better to put the information in an abbreviated copy of 
settings.xml in your <b>${user.home}/.m2</b> folder. That way the information will persist when maven is upgraded without having to copy it 
forward to the new installation. Maven will coalesce settings provided via command parameters, the projects pom, your user-level settings.xml and the global
settings.xml. If you do not already have a user-level settings.xml file, here are specific contents to use. 
Otherwise just merge the new values into your existing file.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <servers>
    <server>
      <id>the-backend-server-id</id>
      <username>the-username</username>
      <password>the-encrypted-password</password>
    </server>
    <server>
      <id>the-frontend-server-id</id>
      <username>the-username</username>
      <password>the-encrypted-password</password>
    </server>
  </servers>
  <profiles>
    <profile>
        <id>domain-name-node-name</id>
        <properties>
            <ccl-host>node-name</ccl-host>
            <ccl-environment>environment-name</ccl-environment>
            <ccl-domain>domain-name</ccl-domain>
            <ccl-hostCredentialsId>the-backend-server-id</ccl-hostCredentialsId>
            <ccl-frontendCredentialsId>the-frontend-server-id</ccl-frontendCredentialsId>
        </properties>
    </profile>  
  </profiles>
</settings>  
```

The ids can be anything, but it is convenient to use the name of the HNAM domain together with the name of the back-end node for the profile id, 
the name of the HNAM domain for the front-end server id and the name of the back-end node for the back-end server id. 
A single settings.xml can house the configuration for multiple nodes and domains. A `-P<profile-id>` command argument will decide which get used.

Specifically, `mvn <maven command> -Psome-profile-id` will perform `<maven command>` using the values defined for the profile with the id `some-profile-id`.

Note that the specified node user must have SSH access to the specified node, write access in CCLUSERDIR, and access to perform an envset to the specified ccl-environment.
The specified ccl-domainUsername must have DBA access in CCL.

## Password Security
Don't want your passwords saved in clear text? Follow the [Maven Password Encryption Instructions](http://maven.apache.org/guides/mini/guide-encryption.html) to
encrypt the password value for the server tags. Note, however, that this only provides protection from an accidental disclosure when opening the settings.xml file. 
Some maven commands can display the decrypted values if the settings.xml and settings-security.xml files are both accessible. The only sure means of protection is to make 
settings-security.xml inaccessible to others.

In addition, the expect4j component logs everythig that gets sent to the backend including passwords. [Look here][password-logging-prevention] to see how to prevent that.

[cerner-maven-ccl-plugin]:../cerner-maven-ccl-plugin/README.md
[password-logging-prevention]:../cerner-maven-ccl-plugin/doc/PASSWORDLOGGING.md