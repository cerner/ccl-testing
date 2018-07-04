# Releasing ccl-testing

## Release Process

* Update the [changelog][changelog] with a description of all updates indicating the new version number for each released component.
* Update the `<modules>` tag of the reactor pom to only include components that need to be released.
    * Comment unneeded components.
* Make a copy of the repository to diff with in a later step and run 'mvn release:update-versions' on this copy.
* Remove `-SNAPSHOT` from all pom files to be released.
* Commit the changes to a new branch.
* Perform `mvn clean install site -P<profileId>` on the branch
    * Travis cannot execute the integration tests because they required an HNAM domain.
* Merge the branch to master and create a tag for the release.
* Perform `mvn clean install deploy` on the tag.
    * Use a jenkins job clearing com.cerner.ccl and com.cerner.ftp from the local maven repo.
* Perform `mvn site site:deploy` on the tag. 
    * Use a jenkins job.
* Diff to the copy made earlier and update all released poms to the next development version and merge to master.

       
* The maven deploy pushes the artifact to the [sonatype staging repo](https://oss.sonatype.org/#stagingpositories).
* After the artifacts are pushed to the Sonatype staging repo
    * Scroll down to the latest ccl-testing repo from the list. 
    * click on the release button to push the artifact to maven central.
    * **Note**: It takes roughly 2 hours for the artifacts to sync with the maven central.

## Release Prerequisites
* A valid Sonatype user account with write access to com.cerner is required to release this project. 
    * To create an account sign up with [Sonatype](https://issues.sonatype.org/secure/Signup!default.jspa).
* Deployed components must be signed. The verify phase of the the maven build will do this.
    * gpg must be installed and configured.  See [working with gpg][gpg help]. 
    * The gpg secrets and passwords must be encrypted and passed on to the jenkins box.
	```    
    $ export ENCRYPTION_PASSWORD=<password to encrypt>
    $ openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in ~/.gnupg/secring.gpg -out .ci/secring.gpg.enc
    $ openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in ~/.gnupg/pubring.gpg -out .ci/pubring.gpg.enc
	```

[changelog]:CHANGELOG.md
[gpg help]:https://central.sonatype.org/pages/working-with-pgp-signatures.html
[generating-ssh-keys]:https://help.github.com/articles/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent/#generating-a-new-ssh-key

