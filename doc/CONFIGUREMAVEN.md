# Configuring Maven for CCL Unit

## Contents
[Maven Settings](#maven-settings)  
[Password Security](#password-security)  

## Maven Settings

Apache Maven is a software build system. Refer to the [Apache Maven][apache-maven] website to learn more about it and specifically
the [downloading maven] instructions if you do not already have it installed.

[apache-maven]:https://maven.apache.org/
[downloading maven]:https://maven.apache.org/download.html

The [ccl-maven-plugin] must be provided credentials so it can transfer files to and from the back-end node and access CCL 
in the target environment.  

The [ccl-maven-plugin] must also be provided a [reqular expression][regular-expression] describing the input prompt for the back-end 
operating system if the default value does not work (`username:environment@host:[^\r\n]*(\r|\n)+#\s*` 
since ccl-maven-plugin version 3.1 and whitenoise-maven-plugin version 2.2, `username:environment@host:[^>]*>\s*`  before that). 

Credential information can be provided in several ways, but we recommend using maven <b>profiles</b> and <b>servers</b> to specify it. 
Moreover, we recommed specifying these items in a <b>settings.xml</b> file (opposed to putting them in the pom file of every single project). 
A copy of settings.xml resides in the conf folder of your maven installation, however, it is better to put the information in an abbreviated copy of 
settings.xml in your <b>${user.home}/.m2</b> folder. That way the information will persist when maven is upgraded without having to copy it 
forward to the new maven installation. Maven will coalesce settings provided via command parameters, the projects pom, your user-level settings.xml and the global
settings.xml. If you do not already have a user-level settings.xml file, there is sample content below to use. 
Otherwise just merge the new values into your existing file.

<b>SSH keys</b> can be used to access a backend host which supports it (since ccl-maven-plugin version 3.3 and whitenoise-maven-plugin version 2.6).
The ccl-keyFile property should only be supplied if using SSH key authentication. In that case, the value should be the full path to the private key
file using `/` as the path separator and the public key file must reside in the same directory 
and have the same name as the private key file but with a .pub extension. The private key can be passphrase protected. 
The configured password for the backend host (via hostCredentialsId or hostPassword) will be used for the passphrase. 
See [linode.com][linode] or [ssh.com] for an explanation of SSH public/private keys and setting them up.  
Only **PEM** format is supported by the version of JCraft currently used. Try regenerating the key using `-m PEM` if  
you see the error `com.jcraft.jsch.JSchException: invalid privatekey`.


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
        <id>the-profile-id</id>
        <properties>
            <ccl-host>node-name</ccl-host>
            <ccl-environment>environment-name</ccl-environment>
            <ccl-domain>domain-name</ccl-domain>
            <ccl-hostCredentialsId>the-backend-server-id</ccl-hostCredentialsId>
            <ccl-frontendCredentialsId>the-frontend-server-id</ccl-frontendCredentialsId>
            <ccl-keyFile>//full/path/to/an/RSA/private/key/file---omit-if-not-using-ssh-private-keys<ccl-keyFile>
            <ccl-osPromptPattern>whatever-works---omit-if-the-default-value-works</ccl-osPromptPattern>
        </properties>
    </profile>  
  </profiles>
</settings>  
```

The ids can be anything, but it is convenient to use the HNAM domain name or the HNAM domain together with the backend node name for the profile id, 
the HNAM domain name for the front-end-server-id and the back-end node name for the back-end-server-id. 
A single settings.xml can house the configuration for multiple nodes and domains. Providing the command argument `-P<profile-id>` dictates which profile gets used.

Specifically, `mvn <maven command> -Psome-profile-id` will perform `<maven command>` using the values defined for the profile with the id `some-profile-id`.

Note that the specified node user must have SSH access to the specified node, write access in CCLUSERDIR, and access to perform an envset to the specified ccl-environment.
If any program is specified to be a `:dba` program, the host user must have DBA access in CCL. Otherwise the programs will be compiled at the host user's CCL access level.

## Password Security
Don't want your passwords saved in clear text? Follow the [Maven Password Encryption Instructions](http://maven.apache.org/guides/mini/guide-encryption.html) to
encrypt the password value for the server tags. Note, however, that this only provides protection from an accidental disclosure when opening the settings.xml file. 
Some maven commands can display the decrypted values if the settings.xml and settings-security.xml files are both accessible. The only sure means of protection is to make 
settings-security.xml inaccessible to others.

In addition, the expect4j component logs everythig that gets sent to the backend including passwords. [Look here][password-logging-prevention] to see how to prevent that.

[ccl-maven-plugin]:../ccl-maven-plugin/README.md
[password-logging-prevention]:../ccl-maven-plugin/doc/PASSWORDLOGGING.md
[regular-expression]: https://en.wikipedia.org/wiki/Regular_expression
[ssh.com]: https://www.ssh.com/ssh/key/#sec-How-to-configure-key-based-authentication
[linode]: https://www.linode.com/docs/security/authentication/use-public-key-authentication-with-ssh
